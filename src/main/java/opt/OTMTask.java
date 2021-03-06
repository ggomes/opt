package opt;

import api.OTMdev;
import javafx.application.Platform;
import javafx.concurrent.Task;
import opt.data.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OTMTask  extends Task {

	private AppMainController mainController;
	private FreewayScenario fwyscenario;
	private OTMdev otmdev;

	private int simsteps;
	private float outdt;
	private int step_per_progbar_update;

	public OTMTask(AppMainController mainController, FreewayScenario fwyscenario,float outdt, int progbar_steps) throws Exception {

		this.mainController = mainController;
		this.fwyscenario = fwyscenario;
		this.outdt = outdt;

		assert(outdt==300f);

		// bind the progress bar and make it visible
		if(mainController!=null)
			mainController.bindProgressBar(progressProperty());

		try {

			// add ghost pieces
			fwyscenario.add_ghost_pieces();

			final float nominal_duration = fwyscenario.get_sim_duration();
			final float nominal_simdt = fwyscenario.get_sim_dt_sec();

			if(nominal_simdt<1f)
				throw new Exception("out dt too small.");
			if(nominal_simdt>300f)
				throw new Exception("out dt too large.");

			List<Float> factors_300 = List.of(1f, 2f, 3f, 4f, 5f, 6f, 9f, 10f, 12f, 15f, 20f, 25f, 30f, 50f, 60f, 75f, 100f, 150f, 300f);

			float simdt = nominal_simdt;
			if(!factors_300.contains(simdt))
				simdt = factors_300.stream().filter(x->x>nominal_simdt).findFirst().get();

			fwyscenario.set_sim_dt_sec(simdt);
			this.simsteps = (int) Math.ceil(nominal_duration/simdt);

			this.step_per_progbar_update = Math.max( simsteps / progbar_steps , 1);

			// check outdt is multiple of simdt
			if(outdt%simdt > 0.01)
				throw new Exception("Reporting time step should be a multiple of simulation time step.");

			jaxb.Scenario jscenario = fwyscenario.get_scenario().to_jaxb();
			api.OTM otm = new api.OTM();
			otm.load_from_jaxb(jscenario,false);
			this.otmdev = new OTMdev(otm);
		} catch (Exception e) {
			e.printStackTrace();
			fwyscenario.remove_ghost_pieces();
		}
	}

	@Override
	protected Object call()  {
		this.run_simulation();
		return null;
	}

	@Override
	protected void done() {
		super.done();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// unbind progress bar and make it invisible.
				if (mainController!=null) {
					mainController.unbindProgressBar();
					mainController.completeSimulation();
				}
			}
		});
	}

	public SimDataScenario run_simulation(){

		SimDataScenario simdata;

		try {

			float sim_dt = fwyscenario.get_sim_dt_sec();

			Set<Long> linkids = fwyscenario.get_links().stream()
					.filter(link->link.get_type()!= AbstractLink.Type.ghost)
					.map(x->x.id).collect(Collectors.toSet());
			for(Long commid : otmdev.scenario.commodities.keySet()){
				otmdev.otm.output().request_cell_flw(commid,linkids,sim_dt);
				otmdev.otm.output().request_cell_veh(commid,linkids,sim_dt);
			}

			otmdev.otm.initialize(fwyscenario.get_start_time());

			int steps_taken = 0;
			while(steps_taken<simsteps){

				if (isCancelled())
					break;

				// advance otm, get back information
				otmdev.otm.advance(sim_dt);
				steps_taken += 1;

				// progress bar
				if(mainController!=null && steps_taken%step_per_progbar_update==0){
					final int ii = steps_taken;
					Platform.runLater(new Runnable() {
						@Override public void run() {
							updateProgress(ii, simsteps);
						}
					});
				}
			}

		} catch (Exception e) {
			failed();
		} finally {
			simdata = new SimDataScenario(fwyscenario,otmdev,outdt);
			fwyscenario.remove_ghost_pieces();
			if(mainController!=null)
				mainController.attachSimDataToScenario(simdata);
		}

		return simdata;
	}

}

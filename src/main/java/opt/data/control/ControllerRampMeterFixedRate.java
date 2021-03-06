package opt.data.control;

import jaxb.Parameter;
import opt.data.FreewayScenario;

import java.util.Collection;

public class ControllerRampMeterFixedRate extends AbstractControllerRampMeter {

    protected float rate_vphpl;

    ////////////////////////////////
    // construction
    ////////////////////////////////

    public ControllerRampMeterFixedRate(FreewayScenario scn, Long id,float dt, boolean has_queue_control, float min_rate_vphpl, float max_rate_vphpl,float rate_vphpl) throws Exception {
        super(id!=null ? id : scn.new_controller_id(),
                dt,
                control.AbstractController.Algorithm.fixed_rate,
                has_queue_control,
                min_rate_vphpl,
                max_rate_vphpl);

        this.rate_vphpl = rate_vphpl;
    }

    public float get_rate_vphpl(){
        return rate_vphpl;
    }

    public void set_rate_vphpl(float new_rate){
        this.rate_vphpl = new_rate;
    }

    @Override
    public Collection<Parameter> jaxb_parameters() {

        Collection<Parameter> j = super.jaxb_parameters();

        // write rate
        jaxb.Parameter p = new jaxb.Parameter();
        p.setName("rate_vphpl");
        p.setValue(String.format("%.0f",rate_vphpl));
        j.add(p);

        return j;
    }

}

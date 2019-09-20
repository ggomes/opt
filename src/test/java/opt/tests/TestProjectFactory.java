package opt.tests;

import error.OTMException;
import opt.data.*;
import org.junit.Test;
import xml.JaxbLoader;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestProjectFactory extends AbstractTest {

    @Test
    public void test_load_scenario_from_file(){
        try {
            String scn_file = get_test_fullpath("two_fwy_hov.xml");
            assertNotNull(JaxbLoader.load_scenario(scn_file,true));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_load_project_from_file(){
        String project_file_name = get_test_fullpath(project_name);
        boolean validate = true;
        try {
            Project project = ProjectFactory.load_project(project_file_name,validate);
            assertNotNull(project);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_create_empty_project(){
        ParametersFreeway params = new ParametersFreeway(100f,200f,300f,100f,200f,300f,100f,200f,300f);
        Project project = ProjectFactory.create_empty_project(
                "Unnamed scenario",
                "Unnamed segment",
                params);
        assertNotNull(project);
        FreewayScenario scenario = project.get_scenario_with_name("Unnamed scenario");
        assertNotNull(scenario);
        Segment segment = scenario.get_segment_by_name("Unnamed segment");
        assertNotNull(segment);
    }

    @Test
    public void test_save_project_to_file() {
        try {
            TestData X = new TestData();
            ProjectFactory.save_project(X.project,get_test_fullpath("project_saved.opt"));
            Project project_saved = ProjectFactory.load_project(get_test_fullpath("project_saved.opt"),false);
            assertTrue(X.project.equals(project_saved));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}

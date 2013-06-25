package fursten.simulator.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.persistent.DAOManager;
import fursten.util.persistent.DAOTestHelper;

public class TestSampleCommad {

	//private final DAOTestHelper helper = DAOManager.getTestHelper();
	
	@Before
	public void setUp() {
	    //helper.setUp();
	    TestStartup.init();
	}
	
	@After
	public void tearDown() {
	    //helper.tearDown();
	    TestShutDown.destroy();
	}
    
    @Test
    public void testCommand() throws Exception {
    	
    }
}

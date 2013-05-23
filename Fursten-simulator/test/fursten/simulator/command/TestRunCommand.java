package fursten.simulator.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.persistent.DAOManager;
import fursten.util.persistent.DAOTestHelper;

public class TestRunCommand {
	
	private final DAOTestHelper helper = DAOManager.getTestHelper();
	
	@Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
    
    @Test
    public void testCommand() throws Exception {
    	
    }
}

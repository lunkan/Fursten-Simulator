package fursten.simulator.node;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.persistent.DAOManager;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.TestResourceUtil;
import fursten.util.persistent.DAOTestHelper;

public class TestNodeStabilityCalculator {

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
    public void testStabilityCalculator() throws Exception {
    	
    	
    	HashMap<String, Resource> staticResources = TestResourceUtil.generateResources(0, null, "static", 2, 1, true, 0, 0);
    	
    }
}

package fursten.simulator.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestCaseHelper;
import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.persistent.DAOManager;

public class TestResourceDependencyManager {

	private static final Logger logger = Logger.getLogger(TestResourceDependencyManager.class.getName());
	
	private HashMap<String, Resource> staticSamples;
	private HashMap<String, Resource> dynamicSamples;
	
	@Before
    public void setUp() {
        TestStartup.init();
        staticSamples = TestCaseHelper.loadResources("junit/testcase/resource/static-resources.xml");
        dynamicSamples = TestCaseHelper.loadResources("junit/testcase/resource/dynamic-resources.xml");
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }
    
    @Test
	public void testGetDependents() {
		
    	//Test dependent self
    	Set<Integer> dependentKeys = ResourceDependencyManager.getDependents(dynamicSamples.get("dynamic_11").getKey());
    	assertEquals(1, dependentKeys.size());
    	assertTrue(dependentKeys.containsAll(Arrays.asList(new Integer[]{ 805306368 })));
    	
    	//Test dependent branch
    	dependentKeys = ResourceDependencyManager.getDependents(staticSamples.get("static_12").getKey());
    	assertEquals(3, dependentKeys.size());
    	assertTrue(dependentKeys.containsAll(Arrays.asList(new Integer[]{ 536870912, 805306368, 671088640 })));
    	
    	//Test dependent root
    	dependentKeys = ResourceDependencyManager.getDependents(staticSamples.get("static_1").getKey());
    	assertEquals(1, dependentKeys.size());
    	assertTrue(dependentKeys.containsAll(Arrays.asList(new Integer[]{ 536870912 })));
	}
}


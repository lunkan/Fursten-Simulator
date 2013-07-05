package fursten.simulator.persistent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestCaseHelper;
import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.resource.Resource;

import junit.framework.TestCase;

public class TestResourceManager extends TestCase {

	private static final Logger logger = Logger.getLogger(TestResourceManager.class.getName());
	
	private HashMap<String, Resource> resourceSamples;
	private ResourceManager RM;
			
    @Before
    public void setUp() {
    	TestStartup.init();
        RM = DAOManager.get().getResourceManager();
        resourceSamples = TestCaseHelper.loadResources("junit/testcase/resource/simple-resources.xml");
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }
    
    @Test
    public void testInsert() {
    	
    	//Put single
    	RM.put(resourceSamples.get("resourceA"));
    	
    	//Put multiple 
    	RM.putAll(Arrays.asList(new Resource[]{ resourceSamples.get("resourceB"), resourceSamples.get("resourceC"), resourceSamples.get("resourceD") }));
    	
    	//Overwrite resourceB
    	Resource newResource = new Resource();
    	newResource.setKey(2);
    	newResource.setName("newResourceB");
    	RM.put(newResource);
    	
    	assertTrue(RM.get(1).equals(resourceSamples.get("resourceA")));
    	assertFalse(RM.get(2).equals(resourceSamples.get("resourceB")));
    	assertTrue(RM.get(2).equals(newResource));
    	assertTrue(RM.get(3).equals(resourceSamples.get("resourceC")));
    	assertTrue(RM.get(4).equals(resourceSamples.get("resourceD")));
    }
    
    @Test
    public void testRemove() {
    	
    	//Init
    	RM.putAll(new ArrayList<Resource>(resourceSamples.values()));
    	
    	//Delete single
    	RM.remove(1);
    	
    	//Delete multiple
    	RM.removeAll(new HashSet<Integer>(Arrays.asList(new Integer[]{ 3, 4 })));
    	
    	assertEquals(RM.get(1), null);
    	assertTrue(RM.get(2).equals(resourceSamples.get("resourceB")));
    	assertEquals(RM.get(3), null);
    	assertEquals(RM.get(4), null);
    }
    
    @Test
    public void testGetKeys() {
    	
    	//Init
    	RM.putAll(new ArrayList<Resource>(resourceSamples.values()));
    	
    	Set<Integer> resourceKeys = RM.getKeys();
    	assertTrue(resourceKeys.containsAll(Arrays.asList(new Integer[]{ 1, 2, 3, 4 })));
    }
}

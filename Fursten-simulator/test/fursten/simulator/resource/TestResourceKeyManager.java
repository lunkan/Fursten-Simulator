package fursten.simulator.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestCaseHelper;
import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.ResourceManager;

public class TestResourceKeyManager {
	
	private static final Logger logger = Logger.getLogger(TestResourceKeyManager.class.getName());
	
	private HashMap<String, Resource> resourceSamples;
	
	@Before
    public void setUp() {
        TestStartup.init();
        resourceSamples = TestCaseHelper.loadResources("junit/testcase/resource/static-resources.xml");
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }
    
    @Test
    public void testContainsKey() {
    	assertTrue(ResourceKeyManager.containsKey(-1073741824));
    	assertFalse(ResourceKeyManager.containsKey(1234));
    }
    
    @Test
    public void testGetChildren() {
    
    	Set<Integer> children = ResourceKeyManager.getChildren(-1073741824);
    	assertEquals(2, children.size());
    	assertTrue(children.containsAll(Arrays.asList(new Integer[]{ -536870912, -805306368 })));
		
    	children = ResourceKeyManager.getChildren(1073741824);
    	assertEquals(6, children.size());
    	assertTrue(children.containsAll(Arrays.asList(new Integer[]{ 1610612736, 1879048192, 1744830464, 1342177280, 1476395008, 1409286144 })));
    }
    
    @Test
    public void testGetParents() {
    	
    	Set<Integer> parents = ResourceKeyManager.getParents(1610612736);
    	assertEquals(1, parents.size());
    	assertTrue(parents.containsAll(Arrays.asList(new Integer[]{ 1073741824 })));
		
    	parents = ResourceKeyManager.getParents(-1476395008);
    	assertEquals(2, parents.size());
    	assertTrue(parents.containsAll(Arrays.asList(new Integer[]{ -1610612736, -2147483648 })));
    	
    }
    
    @Test
    public void testGetNext() {
    	
    	assertEquals(536870912, ResourceKeyManager.getNext());
    	assertEquals(1207959552, ResourceKeyManager.getNext(1073741824));
    }
    
    @Test
    public void testIsDescendant() {
    	
    	assertTrue(ResourceKeyManager.isDescendant(1409286144, 1073741824));
    	assertFalse(ResourceKeyManager.isDescendant(1409286144, -1476395008));
    }
}

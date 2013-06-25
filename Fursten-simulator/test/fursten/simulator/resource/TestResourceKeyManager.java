package fursten.simulator.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.math.BigInteger;
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
import fursten.simulator.persistent.ResourceManager;

public class TestResourceKeyManager {
	
	private static final Logger logger = Logger.getLogger(TestResourceKeyManager.class.getName());
	
	private HashMap<String, Resource> resourceSamples;
	private ResourceManager RM;
	
	@Before
    public void setUp() {
        TestStartup.init();
        RM = DAOManager.get().getResourceManager();
        resourceSamples = TestCaseHelper.load("junit/testcase/resource/static-resources.xml");
        
        /*BigInteger bigIntA = BigInteger.valueOf(0);
        BigInteger bigIntB = BigInteger.valueOf(0);
        
        bigIntA = bigIntA.setBit(31);
        bigIntB = bigIntB.setBit(30);
        
        BigInteger bigIntAA = bigIntA.setBit(30);
        BigInteger bigIntAB = bigIntA.setBit(29);
        
        BigInteger bigIntBA = bigIntB.setBit(29);
        BigInteger bigIntBB = bigIntB.setBit(28);
        
        System.out.println("bigIntA " + bigIntA.intValue());
        System.out.println("bigIntB " + bigIntB.intValue());
        
        System.out.println("bigIntAA " + bigIntAA.intValue());
        System.out.println("bigIntAB " + bigIntAB.intValue());
        
        System.out.println("bigIntBA " + bigIntBA.intValue());
        System.out.println("bigIntBB " + bigIntBB.intValue());*/
        
        
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }
    
    @Test
    public void testContainsKey(int key) {
    	
    	//ResourceKeyManager.
    }
    
    /*@Test
    public void testGetChildren(int key) {
    	
    }
    
    @Test
    public void testGetParents(int key) {
    	
    }
    
    @Test
    public void testGetNext(int key) {
    	
    }
    
    @Test
    public void testIsRelatives(int resourceKeyA, int resourceKeyB) {
    	
    }
    
    @Test
    public void testIsDescendant(int descendantKey, int parentKey) {
    	
    }
	
    @Test
    public void testClear(int descendantKey, int parentKey) {
    	
    }*/
    
    
    
    @Test
    public void testResourceKeyManager() throws Exception {
    	
    	/*
    	 * Create two root resources, where root1 have two descendants
    	 */
    	int rootKey1 = ResourceKeyManager.getNext();
    	int childKey1 = ResourceKeyManager.getNext(rootKey1);
    	int siblingKey1 = ResourceKeyManager.getNext(rootKey1);
    	int rootKey2 = ResourceKeyManager.getNext();
    	int childKey2 = ResourceKeyManager.getNext(childKey1);
    	
    	//Key validation - isDescendant | isRelatives
    	assertTrue(ResourceKeyManager.isDescendant(childKey2, rootKey1));
    	assertFalse(ResourceKeyManager.isDescendant(rootKey1, childKey2));
    	assertFalse(ResourceKeyManager.isDescendant(childKey1, rootKey2));
    	assertTrue(ResourceKeyManager.isRelatives(childKey2, childKey1));
    	
    	//Test Children
    	Set<Integer> childKeys = ResourceKeyManager.getChildren(rootKey1);
    	assertEquals(childKeys.size(), 3);
    	assertTrue(childKeys.contains(childKey1));
    	assertTrue(childKeys.contains(childKey2));
    	assertTrue(childKeys.contains(siblingKey1));
    	
    	childKeys = ResourceKeyManager.getChildren(rootKey2);
    	assertEquals(childKeys.size(), 0);
    	
    	//Test Parents
    	Set<Integer> parentKeys = ResourceKeyManager.getParents(childKey2);
    	assertEquals(parentKeys.size(), 2);
    	assertTrue(parentKeys.contains(childKey1));
    	assertTrue(parentKeys.contains(rootKey1));
    	
    	parentKeys = ResourceKeyManager.getParents(rootKey2);
    	assertEquals(parentKeys.size(), 0);
	}
}

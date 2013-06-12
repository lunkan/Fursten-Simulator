package fursten.simulator.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.persistent.DAOManager;
import fursten.util.persistent.DAOTestHelper;

public class TestResourceKeyManager {

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

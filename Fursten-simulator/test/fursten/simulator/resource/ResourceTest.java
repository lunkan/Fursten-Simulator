package fursten.simulator.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource.Weight;
import fursten.simulator.resource.Resource.WeightGroup;
import fursten.util.persistent.DAOTestHelper;

public class ResourceTest {

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
    
    @Test
    public void testResourceDependencyManager() throws Exception {
    	
    	int rootKey1 = ResourceKeyManager.getNext();
    	int rootKey2 = ResourceKeyManager.getNext();
    	int childKey1 = ResourceKeyManager.getNext(rootKey1);
    	int childKey2 = ResourceKeyManager.getNext(childKey1);
    	
    	List<Resource> resources = new ArrayList<Resource>();
    	
    	//root res 1
    	Resource rootRes1 = new Resource();
    	rootRes1.setKey(rootKey1);
    	resources.add(rootRes1);
    	
    	//root res 2
    	Resource rootRes2 = new Resource();
    	rootRes2.setKey(rootKey2);
    	resources.add(rootRes2);
    	
	    	//weight of res 2
	    	ArrayList<Weight> weights = new ArrayList<Resource.Weight>();
	    	Weight weight = new Weight();
	    	weight.setResource(childKey1);
	    	weight.setValue(1);
	    	weights.add(weight);
	    	
	    	ArrayList<WeightGroup> weightGroups = new ArrayList<WeightGroup>();
	    	WeightGroup weightGroup = new WeightGroup();
	    	weightGroup.setWeights(weights);
	    	weightGroups.add(weightGroup);
	    	
	    	rootRes2.setWeightGroups(weightGroups);
	    	resources.add(rootRes2);
    	
    	//child res 1
    	Resource childRes1 = new Resource();
    	childRes1.setKey(childKey1);
    	resources.add(childRes1);
    	
    	//child res 2
    	Resource childRes2 = new Resource();
    	childRes2.setKey(childKey2);
    	
    	//Add resources to DB
    	DAOFactory.get().getResourceManager().insert(resources);
		
    	//Check that Root 2 is dependent of childKey1 + childKey2 and self
    	Set<Integer> dependentResources = ResourceDependencyManager.getDependents(childKey1);
    	assertEquals(dependentResources.size(), 1);
    	assertTrue(dependentResources.contains(rootKey2));
    	
    	dependentResources = ResourceDependencyManager.getDependents(childKey2);
    	assertEquals(dependentResources.size(), 1);
    	assertTrue(dependentResources.contains(rootKey2));
    	
    	dependentResources = ResourceDependencyManager.getDependents(rootKey1);
    	assertEquals(dependentResources.size(), 0);
    	
    	//Remember that self is only active if resource have weights - thats why size = 1
    	dependentResources = ResourceDependencyManager.getDependents(rootKey2);
    	assertEquals(dependentResources.size(), 1);
    	assertTrue(dependentResources.contains(rootKey2));
    }
}

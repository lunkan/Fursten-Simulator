package fursten.simulator.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource.Weight;
import fursten.simulator.resource.Resource.WeightGroup;
import fursten.util.persistent.DAOTestHelper;

public class TestResourceDependencyManager {

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


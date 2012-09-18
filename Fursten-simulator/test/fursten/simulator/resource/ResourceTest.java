package fursten.simulator.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.DAOTestHelper;

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
    
	private Set<Integer> generateResourceSampleKeys(int depth) {
    	
    	ResourceKeyManager RKM = new ResourceKeyManager(new HashSet<Integer>());
    	HashSet<Integer> sampleKeys = new HashSet<Integer>();
    	
    	for(int i = 0; i < depth; i++) {
    		
    		HashSet<Integer> newKeys = new HashSet<Integer>();
    		for(Integer sampleKey : sampleKeys) {
    			newKeys.add(RKM.getNext(sampleKey));
    		}
    		
    		newKeys.add(RKM.getNext(0));
    		sampleKeys.addAll(newKeys);
    	}
    	
    	return sampleKeys;
    }
    
    /*   @Test
    public void testResourcesKeyManagerParents() throws Exception
	{
    	System.out.println("");
    	System.out.println("*** Execute testResourcesKeyManagerParents begin ***");
    	
    	Random rand = new Random();
    	int DEPTH = 4;
    	
    	Set<Integer> sampleKeys = generateResourceSampleKeys(DEPTH);
    	ResourceKeyManager RKM = new ResourceKeyManager(sampleKeys);
    	
    	int parentIntIndex = rand.nextInt(sampleKeys.size());
    	int sampleKey = sampleKeys.
    			//get(parentIntIndex);
    	System.out.println("Sample key:" + sampleKey + " " + toBitString(sampleKey));
    	
    	List<Integer> parentKeys = RKM.getParents(sampleKey);
    	
    	//Create validation list
    	ArrayList<Integer> testParentKeys = new ArrayList<Integer>();
    	for(Integer testKey : sampleKeys) {
    		
    		int shift = Integer.numberOfTrailingZeros(Integer.lowestOneBit(testKey));
    		if(((testKey >> shift) ^ (sampleKey >> shift)) == 0 && testKey != sampleKey) {
    			testParentKeys.add(testKey);
			}
    	}
    	
    	//Validate
    	assertEquals(testParentKeys.size(), parentKeys.size());
    	for(Integer parentKey : parentKeys) {
    		System.out.println("Parent key:" + parentKey + " " + toBitString(parentKey));
    		assertEquals(true, testParentKeys.remove(parentKey));
    	}
    	assertEquals(testParentKeys.size(), 0);
    	
    	System.out.println("*** Execute testResourcesKeyManagerParents end ***");
    	System.out.println("");
	}

    @Test
    public void testResourcesKeyManagerChildren() throws SimulatorException
	{
    	System.out.println("");
    	System.out.println("*** Execute testResourcesKeyManagerChildren begin ***");
    	
    	Random rand = new Random();
    	int DEPTH = 4;
    	
    	List<Integer> sampleKeys = generateResourceSampleKeys(DEPTH);
    	ResourceKeyManager RKM = new ResourceKeyManager(sampleKeys);
    	
    	int childIntIndex = rand.nextInt(sampleKeys.size());
    	int sampleKey = sampleKeys.get(childIntIndex);
    	System.out.println("Sample key:" + sampleKey + " " + toBitString(sampleKey));
    	
    	List<Integer> childKeys = RKM.getChildren(sampleKey);
    	
    	//Create validation list
    	ArrayList<Integer> testChildKeys = new ArrayList<Integer>();
    	int shift = Integer.numberOfTrailingZeros(Integer.lowestOneBit(sampleKey));
		
    	for(Integer testKey : sampleKeys) {
    		
    		if(((testKey >> shift) ^ (sampleKey >> shift)) == 0 && testKey != sampleKey) {
    			testChildKeys.add(testKey);
			}
    	}
    	
    	//Validate
    	assertEquals(testChildKeys.size(), childKeys.size());
    	for(Integer childKey : childKeys) {
    		System.out.println("Child key:" + childKey + " " + toBitString(childKey));
    		assertEquals(true, testChildKeys.remove(childKey));
    	}
    	assertEquals(testChildKeys.size(), 0);
    	
    	System.out.println("*** Execute testResourcesKeyManagerChildren end ***");
    	System.out.println("");
	}*/
}

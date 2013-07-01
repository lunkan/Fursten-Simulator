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

public class TestResourceDependencyManager {

	private static final Logger logger = Logger.getLogger(TestResourceDependencyManager.class.getName());
	
	private HashMap<String, Resource> staticSamples;
	private HashMap<String, Resource> dynamicSamples;
	
	@Before
    public void setUp() {
        TestStartup.init();
        staticSamples = TestCaseHelper.load("junit/testcase/resource/static-resources.xml");
        dynamicSamples = TestCaseHelper.load("junit/testcase/resource/dynamic-resources.xml");
        
        /*BigInteger bigIntC = BigInteger.valueOf(0);
        
        bigIntC = bigIntC.setBit(29);
        	BigInteger bigIntCA = bigIntC.setBit(28);
        		BigInteger bigIntCAA = bigIntCA.setBit(27);
        		BigInteger bigIntCAB = bigIntCA.setBit(26);
        	BigInteger bigIntCB = bigIntC.setBit(27);
        		BigInteger bigIntCBA = bigIntCB.setBit(26);
        		BigInteger bigIntCBB = bigIntCB.setBit(25);
        		
        System.out.println("bigIntC " + bigIntC.intValue());
        System.out.println("bigIntCA " + bigIntCA.intValue());
        System.out.println("bigIntCAA " + bigIntCAA.intValue());
        System.out.println("bigIntCAB " + bigIntCAB.intValue());
        System.out.println("bigIntCB " + bigIntCB.intValue());
        System.out.println("bigIntCBA " + bigIntCBA.intValue());
        System.out.println("bigIntCBB " + bigIntCBB.intValue());*/
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


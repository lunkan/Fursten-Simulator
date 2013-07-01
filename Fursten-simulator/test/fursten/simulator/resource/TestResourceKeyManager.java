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
        resourceSamples = TestCaseHelper.load("junit/testcase/resource/static-resources.xml");
        
        /*BigInteger bigIntA = BigInteger.valueOf(0);
        BigInteger bigIntB = BigInteger.valueOf(0);
        
        bigIntA = bigIntA.setBit(31);
        	BigInteger bigIntAA = bigIntA.setBit(30);
        		BigInteger bigIntAAA = bigIntAA.setBit(29);
        		BigInteger bigIntAAB = bigIntAA.setBit(28);
        	BigInteger bigIntAB = bigIntA.setBit(29);
        		BigInteger bigIntABA = bigIntAB.setBit(28);
        		BigInteger bigIntABB = bigIntAB.setBit(27);
        
        bigIntB = bigIntB.setBit(30);
        	BigInteger bigIntBA = bigIntB.setBit(29);
        		BigInteger bigIntBAA = bigIntBA.setBit(28);
        		BigInteger bigIntBAB = bigIntBA.setBit(27);
        	BigInteger bigIntBB = bigIntB.setBit(28);
        		BigInteger bigIntBBA = bigIntBB.setBit(27);
        		BigInteger bigIntBBB = bigIntBB.setBit(26);
        		
        System.out.println("bigIntA " + bigIntA.intValue());
        System.out.println("bigIntAA " + bigIntAA.intValue());
        System.out.println("bigIntAAA " + bigIntAAA.intValue());
        System.out.println("bigIntAAB " + bigIntAAB.intValue());
        System.out.println("bigIntAB " + bigIntAB.intValue());
        System.out.println("bigIntABA " + bigIntABA.intValue());
        System.out.println("bigIntABB " + bigIntABB.intValue());
        
        System.out.println("bigIntB " + bigIntB.intValue());
        System.out.println("bigIntBA " + bigIntBA.intValue());
        System.out.println("bigIntBAA " + bigIntBAA.intValue());
        System.out.println("bigIntBAB " + bigIntBAB.intValue());
        System.out.println("bigIntBB " + bigIntBB.intValue());
        System.out.println("bigIntBBA " + bigIntBBA.intValue());
        System.out.println("bigIntBBB " + bigIntBBB.intValue());
        
        BigInteger bigIntC = BigInteger.valueOf(0);
        bigIntC  = bigIntC.setBit(29);
        
        BigInteger bigIntBBC = bigIntB.setBit(27);
        System.out.println("bigIntC " + bigIntC.intValue() + " # " + "bigIntBBC " + bigIntBBC.intValue());*/
        
        
        /**/
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

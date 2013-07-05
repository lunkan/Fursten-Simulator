package fursten.simulator.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestCaseHelper;
import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.resource.Resource.Offspring;

public class TestResourceWrapper {

	private static final Logger logger = Logger.getLogger(TestResourceWrapper.class.getName());
	
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
	public void testGetUpdateintervall() {
    	//Update-precision = 4
    	assertEquals(5, ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_1")).getUpdateintervall());
	}
    
    @Test
    public void testGetMortality() {
    	//Update-precision = 4
    	assertEquals(0.004990009995001f, ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_1")).getMortality(), 0.0001f);
	}
	
    @Test
	public void testGetOffsprings() {
    	
    	List<Offspring> offsprings = ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_1")).getOffsprings();
    	for(Offspring offspring : offsprings) {
    		
    		if(offspring.getResource() == 536870912) {
		    	assertEquals(0, offspring.getCost(), 0);
		    	assertFalse(offspring.getIsLinked());
		    	assertEquals(1f, offspring.getMultiplier(), 0.0001f);
		    	assertEquals(0.2262190625f, offspring.getRatio(), 0.0001f);
    		}
    		else if(offspring.getResource() == 805306368 || offspring.getResource() == 671088640) {
    			assertEquals(0.0490099501f, offspring.getRatio(), 0.0001f);
    		}
    		else {
    			throw new Error("Non expected resource key!");
    		}
    	}
	}
	
    @Test
	public void testGetWeightMap() {
    	
    	List<HashMap<Integer, Float>> weightMap = ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_11")).getWeightMap();
    	assertEquals(2, weightMap.size());
    	
    	for(HashMap<Integer, Float> weightGroup : weightMap) {
    		for(Integer resourceKey : weightGroup.keySet()) {
    			
    			if(resourceKey == -1073741824 || resourceKey == 1879048192 || resourceKey == 1744830464)
    				assertEquals(1f, weightGroup.get(resourceKey), 0.0001f);
    			else if(resourceKey == -1610612736)
    				assertEquals(-1f, weightGroup.get(resourceKey), 0.0001f);
    			else
    				throw new Error("Non expected resource key!");
    		}
    	}
	}

    @Test
	public void testGetWeight() {
    	
    	Float weightValue = ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_12")).getWeight(1, 1744830464);
    	assertEquals(-0.5f, weightValue, 0.0001f);
	}
    
    @Test
	public void testGetDependencies() {
    	
    	assertTrue(ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_11")).getDependencies(1).containsAll(Arrays.asList(new Integer[]{ 1879048192, 1744830464 })));
	}
	
    @Test
	public void testNumGroups() {
    	assertEquals(2, ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_12")).numGroups());
	}
	
    @Test
	public void testIsStatic() {
    	assertTrue(ResourceWrapper.getWrapper(staticSamples.get("static_12")).isStatic());
    	assertFalse(ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_12")).isStatic());
    	assertFalse(ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_1")).isStatic());
	}
	
    @Test
	public void testIsDependent() {
    	assertTrue(ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_11")).isDependent());
    	assertFalse(ResourceWrapper.getWrapper(staticSamples.get("static_11")).isDependent());
	}
	
    @Test
	public void testHasLinks() {
    	assertFalse(ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_1")).hasLinks());
	}
	
    @Test
	public void testIsValid() {
	}

}

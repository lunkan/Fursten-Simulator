package fursten.simulator.node;

//import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.Resource.WeightGroup;
import fursten.simulator.resource.ResourceWrapper;
import fursten.util.persistent.DAOTestHelper;

public class TestNodeStabilityCalculator {

	//private final DAOTestHelper helper = DAOManager.getTestHelper();
	
    @Before
    public void setUp() {
        //helper.setUp();
        TestStartup.init();
    }

    @After
    public void tearDown() {
        //helper.tearDown();
        TestShutDown.destroy();
    }

    @Test
    public void testSimpelStability() throws Exception {
    	
    	/* SETUP
    	 * one layer of static root resources and one layer of static child resources.
    	 * Static resources is ordered in a 2x2 cross with 250 distance to origo
    	 * A single node dependent on static root nodes located at origo and another at [250,250] 
    	 
    	String prefix = "sp";
    	int numResources = 1;
    	float mortality = 0.0f;
    	float threshold = 0.0f;
    	float weightValue = -2.5f;
    	HashMap<String, Resource> resources = TestResourceHelper.setupDepthDependentScenario(prefix, numResources, mortality, threshold, weightValue);
    	
    	float nodeValue = 1.5f;
    	TestNodeHelper.setupArray(resources.get("sp_static_1_1"), new int[][]{{-250,0},{0,250},{250,0},{-250,0}}, nodeValue);
    	TestNodeHelper.setupArray(resources.get("sp_dependent_1_1"), new int[][]{{0,0},{250,250}}, nodeValue);
    	
    	/* TEST 
    	float expected = (float)(4*weightValue*nodeValue*(1 - 250/NodeStabilityCalculator.NODE_RADIUS) - nodeValue*(nodeValue + Math.sqrt(250*250 + 250*250)));
    	float stability = NodeStabilityCalculator.getInstance().calculateStability(0, 0, ResourceWrapper.getWrapper(resources.get("sp_dependent_1_1")));
    	Assert.assertEquals(expected, stability, 0.000001f);*/
    }
    
    @Test
    public void testInheritanceStability() throws Exception {
    	
    	/* SETUP
    	 * one layer of static root resources and one layer of static child resources (depth 3).
    	 * Static resources is ordered in a 2x2 cross centered at origo with 500 spacing in between
    	 
    	String prefix = "ip";
    	int numResources = 3;
    	float mortality = 0.0f;
    	float threshold = 0.0f;
    	float weightValue = 1.5f;
    	HashMap<String, Resource> resources = TestResourceHelper.setupDepthDependentScenario(prefix, numResources, mortality, threshold, weightValue);
    	
    	float nodeValue = 2.2f;
    	TestNodeHelper.setupArray(resources.get("ip_static_1_1"), new int[][]{{-500,0},{0,500},{500,0},{-500,0}}, nodeValue);
    	TestNodeHelper.setupArray(resources.get("ip_static_3_1"), new int[][]{{-500,0},{0,500},{500,0},{-500,0}}, nodeValue);*/
    	
    	/* TEST
    	float expected = (float)(8*weightValue*nodeValue*(1 - 500/NodeStabilityCalculator.NODE_RADIUS));
    	float stability = NodeStabilityCalculator.getInstance().calculateStability(0, 0, ResourceWrapper.getWrapper(resources.get("ip_dependent_3_1")));
    	Assert.assertEquals(expected, stability, 0.000001f); */
    }
    
    @Test
    public void testGroupStability() throws Exception {
    	
    	/* SETUP
    	 * three layers of static root resources.
    	 * Static resources is ordered in a 2x2 cross centered at origo with 100 spacing in between
    	 * static resources 1 & 2 is grouped to one dependency group resource 3 to a second group
    	 
    	String prefix = "gp";
    	int numResources = 3;
    	float mortality = 0.0f;
    	float threshold = 0.0f;
    	float weightValue = 1.3f;
    	HashMap<String, Resource> resources = TestResourceHelper.setupGroupDependentScenario(prefix, numResources, mortality, threshold, weightValue);
    	
    	float nodeValue = 2.2f;
    	TestNodeHelper.setupArray(resources.get("gp_static_1_1"), new int[][]{{-100,0},{0,100},{100,0},{-100,0}}, nodeValue);
    	TestNodeHelper.setupArray(resources.get("gp_static_1_2"), new int[][]{{-100,0},{0,100},{100,0},{-100,0}}, nodeValue);
    	TestNodeHelper.setupArray(resources.get("gp_static_1_3"), new int[][]{{-100,0},{0,100},{100,0},{-100,0}}, nodeValue);
    	
    	/* TEST 
    	float expected = (float)(4*weightValue*nodeValue*(1 - 100/NodeStabilityCalculator.NODE_RADIUS));
    	float stability = NodeStabilityCalculator.getInstance().calculateStability(0, 0, ResourceWrapper.getWrapper(resources.get("gp_dependent_1_3")));
    	Assert.assertEquals(expected, stability, 0.000001f);*/
    }
}

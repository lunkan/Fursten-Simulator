package fursten.simulator.node;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestCaseHelper;
import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceWrapper;

public class TestNodeStabilityCalculator {
	
	//private static final Logger logger = Logger.getLogger(TestNodeStabilityCalculator.class.getName());
	
	private HashMap<String, Resource> staticSamples;
	private HashMap<String, Resource> dynamicSamples;
	private HashMap<String, Node> nodeStaticSamples;
	private HashMap<String, Node> nodeDynamicSamples;
	
	@Before
    public void setUp() {
        TestStartup.init();
        staticSamples = TestCaseHelper.loadResources("junit/testcase/resource/static-resources.xml");
        dynamicSamples = TestCaseHelper.loadResources("junit/testcase/resource/dynamic-resources.xml");
        nodeStaticSamples = TestCaseHelper.loadNodes("junit/testcase/node/static-nodes.xml");
        nodeDynamicSamples = TestCaseHelper.loadNodes("junit/testcase/node/dynamic-nodes.xml");
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }

    @Test
    public void testCalculateStability() throws Exception {
    	
    	//Use a sigmoid function to get better distribution for nodes of same type
    	float sigX= 5.0f + ((float)(0)/(float)NodeStabilityCalculator.NODE_RADIUS)*-10.0f;
    	float sigmoidImpact = 1.0f / (1.0f + (float)Math.exp(-sigX));
    			
    	/*
    	 * Test root node [0:0]
    	 */
    	float expectedStability = -sigmoidImpact;//negative from self
    	expectedStability += 2.0f*2.0f;//depth 0
    	expectedStability += Math.max(0, 1 - (Math.sqrt(Math.pow(500, 2) + Math.pow(500, 2)) / NodeStabilityCalculator.NODE_RADIUS)) * 4;//depth 1
    	expectedStability += Math.max(0, 1 - (Math.sqrt(Math.pow(650, 2) + Math.pow(650, 2)) / NodeStabilityCalculator.NODE_RADIUS)) * (0.5f * 8);//depth 2
    	
    	float stability = NodeStabilityCalculator.getInstance().calculateStability(0, 0, ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_1").getKey()));
    	assertEquals(expectedStability, stability, 0.0001f);
    	
    	/*
    	 * Test child node [250:250]
    	 */
    	expectedStability = -sigmoidImpact;//-1;//negative from self
    	expectedStability += Math.max(0, 1 - (Math.sqrt(Math.pow(900, 2) + Math.pow(400, 2)) / NodeStabilityCalculator.NODE_RADIUS)) * 0.5f;//static_11 [-650:650]
    	expectedStability += Math.max(0, 1 - (Math.sqrt(Math.pow(900, 2) + Math.pow(400, 2)) / NodeStabilityCalculator.NODE_RADIUS)) * 0.5f;//static_12 [-650:650]
    	
    	stability = NodeStabilityCalculator.getInstance().calculateStability(250, 250, ResourceWrapper.getWrapper(dynamicSamples.get("dynamic_11").getKey()));
    	assertEquals(expectedStability, stability, 0.0001f);
    }
}

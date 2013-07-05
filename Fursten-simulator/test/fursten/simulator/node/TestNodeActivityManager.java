package fursten.simulator.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestCaseHelper;
import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.resource.Resource;

public class TestNodeActivityManager {

	//private static final Logger logger = Logger.getLogger(TestNodeActivityManager.class.getName());
	
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
        NodeActivityManager.clean();
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }
    
    @Test
    public void testGetInvalidResources() {
    	
    	/*
    	 * invalidate root weight
    	 */
    	NodeActivityManager.invalidate(
    			Arrays.asList(new Node[]{
    					nodeStaticSamples.get("static_1[0:0]"),
    					nodeStaticSamples.get("static_2[0:0]")
    			})
    	);
        
    	Set<Integer> resourceKeys = NodeActivityManager.getInvalidResources();
    	assertEquals(1, resourceKeys.size());
    	assertTrue(
    			resourceKeys.containsAll(Arrays.asList(new Integer[]{
    					536870912
    			}))
    	);
    	
    	/*
    	 * invalidate self
    	 */
    	NodeActivityManager.invalidate(
    			Arrays.asList(new Node[]{
    					nodeDynamicSamples.get("dynamic_1[0:0]"),
    					nodeDynamicSamples.get("dynamic_11[250:250]")
    			})
    	);
    	
    	resourceKeys = NodeActivityManager.getInvalidResources();
    	assertEquals(2, resourceKeys.size());
    	assertTrue(
    			resourceKeys.containsAll(Arrays.asList(new Integer[]{
    					536870912, 805306368
    			}))
    	);
    	
    	/*
    	 * invalidate child weight
    	 */
    	NodeActivityManager.invalidate(
    			Arrays.asList(new Node[]{
    					nodeStaticSamples.get("static_212[-650:650]")
    			})
    	);
    	
    	resourceKeys = NodeActivityManager.getInvalidResources();
    	assertEquals(3, resourceKeys.size());
    	assertTrue(
    			resourceKeys.containsAll(Arrays.asList(new Integer[]{
    					805306368, 671088640, 536870912
    			}))
    	);
    }
    
    @Test
    public void testGetInvalidRectByResourceKey() {
    	
    	NodeActivityManager.invalidate(
    			Arrays.asList(new Node[]{
    					nodeStaticSamples.get("static_211[-650:650]"),
    					nodeStaticSamples.get("static_12[-500:-500]"),
    					nodeStaticSamples.get("static_21[-500:500]")
    			})
    	);
    
    	List<Rectangle> invalidRects = NodeActivityManager.getInvalidRectByResourceKey(536870912);
    	assertEquals(2, invalidRects.size());
    	assertTrue(
    			invalidRects.containsAll(Arrays.asList(new Rectangle[]{
    					new Rectangle(-1024, 0, 1024, 1024),
    					new Rectangle(-1024, -1024, 1024, 1024)
    			}))
    	);
    }
    
    @Test
    public void testNodeActivityManagerInvalidateAll() {

    	NodeActivityManager.invalidateAll();
        Set<Integer> resourceKeys = NodeActivityManager.getInvalidResources();
        assertEquals(17, resourceKeys.size());
        
        NodeActivityManager.clean();
        resourceKeys = NodeActivityManager.getInvalidResources();
        assertEquals(0, resourceKeys.size());
    }
}

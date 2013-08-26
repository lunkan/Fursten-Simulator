package fursten.simulator.command;

import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestCaseHelper;
import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.joint.Joint;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeActivityManager;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;

import junit.framework.TestCase;

public class TestNodeTransactionCommand extends TestCase {
	
	private HashMap<String, Resource> staticSamples;
	private HashMap<String, Resource> dynamicSamples;
	private HashMap<String, Resource> linkedSamples;
	
	private HashMap<String, Node> nodeStaticSamples;
	private HashMap<String, Node> nodeDynamicSamples;
	private HashMap<String, Node> nodeLinkedSamples;
	
	private HashMap<String, Joint> jointsSamples;
	
	private NodeManager NM;
	
	@Before
    public void setUp() {
		
    	TestStartup.init();
    	staticSamples = TestCaseHelper.loadResources("junit/testcase/resource/static-resources.xml");
        dynamicSamples = TestCaseHelper.loadResources("junit/testcase/resource/dynamic-resources.xml");
        linkedSamples = TestCaseHelper.loadResources("junit/testcase/resource/joint-resources.xml");
        
        nodeStaticSamples = TestCaseHelper.loadNodes("junit/testcase/node/static-nodes.xml");
        nodeDynamicSamples = TestCaseHelper.loadNodes("junit/testcase/node/dynamic-nodes.xml");
        nodeLinkedSamples = TestCaseHelper.loadNodes("junit/testcase/node/joint-nodes.xml");
        
        jointsSamples = TestCaseHelper.loadJoints("junit/testcase/joint/joints.xml");
        
        NM = DAOFactory.get().getNodeManager();
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }
    
    @Test
	public void testSimpelTransaction() throws Exception {
		
    	System.out.println(NodeActivityManager.getInvalidResources().size());
    	
    	HashMap<String, Node> substractMap = new HashMap<String, Node>();
    	substractMap.put("static_11[500:500]", nodeStaticSamples.get("static_11[500:500]"));
    	substractMap.put("dynamic_1[0:0]", nodeDynamicSamples.get("dynamic_1[0:0]"));
    	substractMap.put("dynamic_12[-250:-250]", new Node(dynamicSamples.get("dynamic_12").getKey(), -250, -250, 0.25f));
    	
    	HashMap<String, Node> addMap = new HashMap<String, Node>();
    	addMap.put("static_11[501:501]", new Node(staticSamples.get("static_11").getKey(), 501, 501, 1));
    	addMap.put("dynamic_12[-250:-250]", new Node(dynamicSamples.get("dynamic_12").getKey(), -250, -250, 0.5f));
    	
    	//Execute command
    	try {
			new NodeTransactionCommand(
				new ArrayList<Node>(substractMap.values()),
				new ArrayList<Node>(addMap.values())
			).execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("testSimpelTransaction could not be executed!");
		}
		
    	//Validate values
    	List<Node> nodes = NM.get(new Rectangle(-5000, -5000, 10000, 10000));
    	
    	assertEquals(-1, nodes.indexOf(substractMap.get("static_11[500:500]")));
    	assertEquals(-1, nodes.indexOf(substractMap.get("dynamic_1[0:0]")));
    	
    	assertEquals(1.0f, nodes.get(nodes.indexOf(addMap.get("static_11[501:501]"))).getV());
    	assertEquals(1.25f, nodes.get(nodes.indexOf(addMap.get("dynamic_12[-250:-250]"))).getV());
    	
    	//Validate invalid resources
    	Set<Integer> invalidResources = NodeActivityManager.getInvalidResources();
    	assertEquals(5, invalidResources.size());
    	assertTrue(
    		invalidResources.containsAll(Arrays.asList(new Integer[]{
    			536870912, 805306368, 671088640, 268435456, 402653184
    		}))
    	);
    	
    	//Validate invalid regions
    	List<Rectangle> invalidRegions = NodeActivityManager.getInvalidRectByResourceKey(linkedSamples.get("linked_11").getKey());
    	assertEquals(1, invalidRegions.size());
    	assertTrue(invalidRegions.contains(new Rectangle(0, 0, 1024, 1024)));
	}
    
    @Test
	public void testLinkedTransaction() throws Exception {
		
    	HashMap<String, Node> substractMap = new HashMap<String, Node>();
    	substractMap.put("substract[0:0]", new Node(nodeLinkedSamples.get("linked_1[0:0]").getR(), 0, 0, 2.0f));
    	substractMap.put("substract[500:-500]", new Node(nodeLinkedSamples.get("linked_1[500:-500]").getR(), 500, -500, 5.0f));
    	substractMap.put("substract[-500:500]", new Node(nodeLinkedSamples.get("linked_1[-500:500]").getR(), -500, 500, 5.0f));
    	
    	//Execute command
    	try {
			new NodeTransactionCommand(
				new ArrayList<Node>(substractMap.values())
			).execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("testLinkedTransaction could not be executed!");
		}
		
    	//Validate values
    	List<Node> nodes = NM.get(new Rectangle(-5000, -5000, 10000, 10000));
    	
    	assertEquals(8.0f, nodes.get(nodes.indexOf(nodeLinkedSamples.get("linked_1[0:0]"))).getV());
    	assertEquals(4.0f, nodes.get(nodes.indexOf(nodeLinkedSamples.get("linked_11[250:250]"))).getV());
    	assertEquals(4.0f, nodes.get(nodes.indexOf(nodeLinkedSamples.get("linked_11[250:500]"))).getV());
    	assertEquals(4.0f, nodes.get(nodes.indexOf(nodeLinkedSamples.get("linked_11[500:250]"))).getV());
    	assertEquals(4.0f, nodes.get(nodes.indexOf(nodeLinkedSamples.get("linked_12[-250:-250]"))).getV());
    	assertEquals(4.0f, nodes.get(nodes.indexOf(nodeLinkedSamples.get("linked_12[-250:-500]"))).getV());
    	assertEquals(4.0f, nodes.get(nodes.indexOf(nodeLinkedSamples.get("linked_12[-500:-250]"))).getV());
    	
    	assertEquals(5.0f, nodes.get(nodes.indexOf(nodeLinkedSamples.get("linked_1[500:-500]"))).getV());
    	assertEquals(5.0f, nodes.get(nodes.indexOf(nodeLinkedSamples.get("linked_1[-500:500]"))).getV());
    	assertEquals(-1, nodes.indexOf(nodeLinkedSamples.get("linked_11[0:0]")));
		assertEquals(-1, nodes.indexOf(nodeLinkedSamples.get("linked_11[500:0]")));
	}
}

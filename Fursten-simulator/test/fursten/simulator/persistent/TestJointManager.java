package fursten.simulator.persistent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.joint.Joint;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeKeyGenerator;
import junit.framework.TestCase;

public class TestJointManager  extends TestCase {
	
	private HashMap<String, Node> nodeSamples;
	private HashMap<String, Joint> jointsSamples;
	private JointManager LM;
	
    @Before
    public void setUp() {
    	TestStartup.init();
        LM = DAOManager.get().getLinkManager();
        
        nodeSamples = new HashMap<String, Node>();
        nodeSamples.put("node_1", new Node(1, 0, 0, 1));
        nodeSamples.put("node_11", new Node(1, 100, 100, 1));
        nodeSamples.put("node_111", new Node(1, 200, 200, 1));
        nodeSamples.put("node_12", new Node(2, -100, -100, 1));
        nodeSamples.put("node_2", new Node(3, -1000, -1000, 1));
        
        jointsSamples = new HashMap<String, Joint>();
        
        jointsSamples.put("link_1",
        		new Joint(nodeSamples.get("node_1").getNodePoint())
        			.addLink(nodeSamples.get("node_11").getNodePoint(), 1f)
        			.addLink(nodeSamples.get("node_12").getNodePoint(), 0.5f)
        		);
        jointsSamples.put("link_11",
        		new Joint(nodeSamples.get("node_11").getNodePoint())
        			.addLink(nodeSamples.get("node_111").getNodePoint(), 0.5f)
        		);
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }
    
    @Test
	public void testAddremove() {
		
		//Add
		LM.putAll(new ArrayList<Joint>(jointsSamples.values()));
		
		List<Joint> removedJoints = LM.remove(
			jointsSamples.get("link_11").getNodePoint()
		);
		
		//Remove
		assertEquals(1, removedJoints.size());
		assertTrue(removedJoints.containsAll(
			Arrays.asList(
				new Joint[]{
					jointsSamples.get("link_11")
				}
			)
		));
		
		//Validate size
		Joint joint1 = LM.get(jointsSamples.get("link_1").getNodePoint());
		Joint joint11 = LM.get(jointsSamples.get("link_11").getNodePoint());
		assertEquals(jointsSamples.get("link_1"), joint1);
		assertEquals(null, joint11);
	}
}

package fursten.simulator.persistent;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.node.Node;
import fursten.simulator.persistent.mysql.DAOFactory;

import junit.framework.TestCase;

public class TestNodeManager extends TestCase {

	private static final Logger logger = Logger.getLogger(TestNodeManager.class.getName());
	
	private Rectangle worldRect;
	private HashMap<String, Node> nodeSamples;
	private NodeManager NM;
			
    @Before
    public void setUp() {
    	
        TestStartup.init();
        
        worldRect = new Rectangle(-(Integer.MAX_VALUE/2), -(Integer.MAX_VALUE/2), Integer.MAX_VALUE, Integer.MAX_VALUE);
        NM = DAOManager.get().getNodeManager();
        
        nodeSamples = new HashMap<String, Node>();
        nodeSamples.put("nodeA", new Node(1, 0, 0, 1));
        nodeSamples.put("nodeB", new Node(1, 0, 10, 1));
        nodeSamples.put("nodeC", new Node(2, -10, 0, 1));
        nodeSamples.put("nodeD", new Node(3, 0, -10, 1));
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }
    
    @Test
    public void testInsert() {
    	logger.log(Level.INFO, "Test testInsert");
    	
    	NM.addAll(new ArrayList<Node>(nodeSamples.values()));
    	List<Node> nodes = NM.get(worldRect);
    	assertTrue(nodes.containsAll(nodeSamples.values()));
    	assertEquals(nodes.size(), nodeSamples.size());
    }
    
    @Test
    public void testGet() {
    	logger.log(Level.INFO, "Test testGet");
    	
    	NM.addAll(new ArrayList<Node>(nodeSamples.values()));
    	
    	//Get by resource type
    	List<Node> nodesByResourceType = NM.get(worldRect, 1);
    	assertTrue(nodesByResourceType.containsAll(Arrays.asList(new Node[]{ nodeSamples.get("nodeA"), nodeSamples.get("nodeB") })));
    	assertEquals(nodesByResourceType.size(), 2);
    	
    	//Get by rectangle
    	List<Node> nodesByRect = NM.get(new Rectangle(-10, -10, 11, 11));
    	assertTrue(nodesByRect.containsAll(Arrays.asList(new Node[]{ nodeSamples.get("nodeA"), nodeSamples.get("nodeC"), nodeSamples.get("nodeD") })));
    	assertEquals(nodesByRect.size(), 3);
    }
    
    @Test
    public void testSubstract() {
    	logger.log(Level.INFO, "Test testSubstract");
    	
    	NM.addAll(new ArrayList<Node>(nodeSamples.values()));
    	NM.substractAll(Arrays.asList(new Node[]{ nodeSamples.get("nodeA"), new Node(2, -10, 0, 0.3f) }));
    	
    	List<Node> nodesListD = NM.get(worldRect);
    	assertTrue(nodesListD.containsAll(Arrays.asList(new Node[]{new Node(2, -10, 0, 0.7f), nodeSamples.get("nodeB"), nodeSamples.get("nodeD") })));
    	assertEquals(nodesListD.size(), 3);
    }
    
    @Test
    public void testContains() {
    	logger.log(Level.INFO, "Test testContains");
    	
    	NM.addAll(new ArrayList<Node>(nodeSamples.values()));
    	
    	assertEquals(NM.contains(nodeSamples.get("nodeB")), true);
    	assertEquals(NM.contains(new Node(3, 0, 0, 1)), false);
    	assertEquals(NM.contains(new Node(5, -10, 0, 1)), false);
    	assertEquals(NM.contains(new Node(1, 0, 10, 0.6f)), true);
    	
    	assertEquals(NM.containsAll(new ArrayList<Node>(nodeSamples.values())), true);
    }
}


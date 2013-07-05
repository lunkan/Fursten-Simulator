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
import fursten.simulator.link.Link;
import fursten.simulator.node.Node;
import junit.framework.TestCase;

public class TestLinkManager  extends TestCase {
	
	private HashMap<String, Node> nodeSamples;
	private HashMap<String, Link> linkSamples;
	private LinkManager LM;
	
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
        
        linkSamples = new HashMap<String, Link>();
        linkSamples.put("link_1-11", new Link(nodeSamples.get("node_1"), nodeSamples.get("node_11"), 1f));
        linkSamples.put("link_1-12", new Link(nodeSamples.get("node_1"), nodeSamples.get("node_12"), 0.5f));
        linkSamples.put("link_11-111", new Link(nodeSamples.get("node_11"), nodeSamples.get("node_111"), 0.5f));
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }
    
    @Test
	public void testAddremove() {
		
		//Add
		int numAdded = LM.addAll(new ArrayList<Link>(linkSamples.values()));
		assertEquals(3, numAdded);
		
		List<Link> removedLinks = LM.remove(
			linkSamples.get("link_1-12"),
			linkSamples.get("link_1-11"),
			new Link(nodeSamples.get("node_1"), nodeSamples.get("node_2"), 0.5f)
		);
		
		//Remove
		assertEquals(2, removedLinks.size());
		assertTrue(removedLinks.containsAll(
			Arrays.asList(
				new Link[]{
					linkSamples.get("link_1-12"),
					linkSamples.get("link_1-11")
				}
			)
		));
		
		//Validate size
		List<Link> links = LM.get(nodeSamples.get("node_1"), nodeSamples.get("node_11"));
		assertEquals(1, links.size());//node_1 links are all removed
		assertTrue(links.contains(linkSamples.get("link_11-111")));
	}
}

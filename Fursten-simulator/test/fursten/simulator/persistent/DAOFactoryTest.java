package fursten.simulator.persistent;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.world.World;
import fursten.simulator.node.Node;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceWrapper;
import fursten.util.persistent.DAOTestHelper;

public class DAOFactoryTest extends TestCase {

	private final DAOTestHelper helper = DAOManager.getTestHelper();
	
    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
    
    @Test
    public void testSessionManager() {
    	
    	int NUM_ITERATIONS = 3;
    	String TEST_NAME = "test name";
    	long totTime = 0;
    	
    	System.out.println("");
    	System.out.println("*** Execute testSessionManager begin ***");
    	
    	ArrayList<String> exeLog = new ArrayList<String>();
    	for(int i = 0; i < NUM_ITERATIONS; i++) {
    		
    		WorldManager SM = DAOManager.get().getWorldManager();
    		
    		// Perform test and measure time
    		long startTime = System.currentTimeMillis();
    		
    		World session = SM.getActive();
    		assertEquals(session.getName(), "Untitled");
    		assertEquals(session.getTick(), 0);
    		long initTime = System.currentTimeMillis() - startTime;
    		
    		session.setName(TEST_NAME);
    		session.setTick(i);
    		SM.setActive(session);
    		long insertTime = System.currentTimeMillis() - initTime;
    		
    		World fetchedSession = SM.getActive();
    		assertEquals(fetchedSession.getName(), TEST_NAME);
    		assertEquals(fetchedSession.getTick(), i);
    		long readTime = System.currentTimeMillis() - insertTime;
    		
    		SM.clear();
    		long clearTime = System.currentTimeMillis() - readTime;
    		long exeTime = System.currentTimeMillis() - startTime;
    		
    		World clearedSession = SM.getActive();
    		assertEquals(clearedSession.getName(), "Untitled");
    		assertEquals(clearedSession.getTick(), 0);
    		
    		//Log result
    		exeLog.add("Init " + initTime + "ms " +
    				"Write " + insertTime + "ms " +
    				"Read " + readTime + "ms " +
    				"Clear " + clearTime + "ms " + 
    				"Tot exe time =  " + exeTime + "ms"
    			);
    		
    		//clear session
    		SM.clear();
    	}
    	
    	System.out.println("");
    	System.out.println("--- RESULT ---");
    	System.out.println("Avg exe time: " + Math.round(totTime/NUM_ITERATIONS) + "ms");
    	
    	System.out.println("");
    	System.out.println("--- Iterations ---");
    	for(String logMsg : exeLog) {
    		System.out.println(logMsg);
    	}
    	
    	System.out.println("");
    	System.out.println("*** Execute testSessionManager end ***");
    	System.out.println("");
    	
	}
    
   //@Test
    public void testResourceManager()
	{
    	int NUM_ITERATIONS = 10;
    	int NUM_RESOURCES = 10;
    	long totTime = 0;
    	
    	System.out.println("");
    	System.out.println("*** Execute testResourceManager begin ***");
    	
    	ArrayList<String> exeLog = new ArrayList<String>();
    	ResourceManager RM = DAOFactory.get().getResourceManager();
    	
    	List<Integer> resourceKeys = getRandomResources(NUM_RESOURCES);
    	HashMap<Integer, Resource> resources = new HashMap<Integer, Resource>();
    	/*for(Integer resourceKey : resourceKeys) {
    		Resource resource = new ResourceWrapper(resourceKey).getResource();
    		resources.put(resourceKey, resource);
    	}*/
    	    	
    	for(int i = 0; i < NUM_ITERATIONS; i++) {
    		
    		//clear recourses
    		RM.delete(RM.getKeys());
    		
    		//get resources to delete
    		Set<Integer> deleteResources = getResourceSample(i, resourceKeys);
    		Set<Integer> sampleResourceKeys = getResourceSample(NUM_RESOURCES, resourceKeys);
    		
    		//Calculate reference values
    		HashMap<Integer, Resource> refResources = new HashMap<Integer, Resource>();
    	    for(Integer key: resources.keySet()) {
    	    	refResources.put(key, resources.get(key));           
    	    }
    	    
    		//Delete resources
    		for(Integer deletedResource : deleteResources) {
    			refResources.remove(deletedResource);
    		}
    		
    		//Perform test and measure time
    		long startTime = System.currentTimeMillis();
    		
    		RM.insert(new ArrayList<Resource>(resources.values()));
    		long insertTime = System.currentTimeMillis() - startTime;
    			
    		RM.delete(deleteResources);
    		long deleteTime = System.currentTimeMillis() - (startTime + insertTime);
    		
    		List<Resource> fetchedResources = RM.get(sampleResourceKeys);
    		long fetchTime = System.currentTimeMillis() - (startTime + insertTime + deleteTime);
    		
    		long exeTime = System.currentTimeMillis() - startTime;
    		totTime += exeTime;
    		
    		//Validate equality
    		assertEquals(refResources.size(), fetchedResources.size());
    		for(Resource resource : fetchedResources) {
    			Resource deletedRefResource = refResources.remove(resource.getKey());
    			assertTrue(!(deletedRefResource == null));
    		}
    		
    		assertEquals(0, refResources.size());
    		
    		//Log result
    		exeLog.add("Insert " + resources.size() + ":" + insertTime + "ms " +
    				"Delete " + deleteResources.size() + ":" + deleteTime + "ms " +
    				"Fetch " + fetchedResources.size() + ":" + fetchTime + "ms " + 
    				"Tot exe time =  " + exeTime + "ms"
    			);
    		
    	}
    	
    	System.out.println("");
    	System.out.println("--- RESULT ---");
    	System.out.println("Num iterations: " + NUM_ITERATIONS);
    	System.out.println("Num resources: " + NUM_RESOURCES);
    	System.out.println("Avg exe time: " + Math.round(totTime/NUM_ITERATIONS) + "ms");
    	
    	System.out.println("");
    	System.out.println("--- Iterations ---");
    	for(String logMsg : exeLog) {
    		System.out.println(logMsg);
    	}
    	
    	System.out.println("");
    	System.out.println("*** Execute testResourceManager end ***");
    	System.out.println("");
    	
	}
    
    @Test
    public void testNodeManager()
	{
    	int NUM_ITERATIONS = 10;
    	int NUM_NODES = 100;
    	int NUM_RESOURCES = 10;
    	long totTime = 0;
    	
    	System.out.println("");
    	System.out.println("*** Execute testNodeManager begin ***");
    	
    	Random rand = new Random();
    	ArrayList<String> exeLog = new ArrayList<String>();
    	
    	NodeManager NM = DAOFactory.get().getNodeManager();
    	List<Integer> resourceKeys = getRandomResources(10);
    	Rectangle rect = new Rectangle(-(Integer.MAX_VALUE/2), -(Integer.MAX_VALUE/2), Integer.MAX_VALUE, Integer.MAX_VALUE);
    	
    	for(int i = 0; i < NUM_ITERATIONS; i++) {
    	
    		//clear node-tree
    		for(Integer resourceKey : resourceKeys) {
    			NM.deleteByResourceKey(resourceKey);
    		}
    		
    		List<Node> nodes = getRandomNodes(NUM_NODES, resourceKeys, rect);
    		List<Node> deletedNodes = getNodeSample((rand.nextInt(NUM_NODES)), nodes);
    		Set<Integer> resources = getResourceSample(i, resourceKeys);
    		
    		Rectangle sampleRect = new Rectangle(
    				-rand.nextInt(Integer.MAX_VALUE/2),
    				-rand.nextInt(Integer.MAX_VALUE/2),
    				rand.nextInt(Integer.MAX_VALUE),
    				rand.nextInt(Integer.MAX_VALUE)
    			);
    		
    		// Calculate reference values
    		List<Node> refNodes = new ArrayList<Node>(nodes);
    		
    		//Delete nodes
    		for(Node deletedNode : deletedNodes) {
    			refNodes.remove(deletedNode);
    		}
    		
    		//Count nodes within bounding box
    		List<Node> refNodeList = new ArrayList<Node>();
    		for(Node refNode : refNodes) {
    			if(resources.contains(refNode.getR())) {
    				
    				if(sampleRect.contains(refNode.getX(), refNode.getY()) && resources.contains(refNode.getR()))
    					refNodeList.add(refNode);
    			}
    		}
    		
    		//Perform test and measure time
    		long startTime = System.currentTimeMillis();
    		
    		NM.insert(nodes);
    		long insertTime = System.currentTimeMillis() - startTime;
    			
    		NM.delete(deletedNodes);
    		long deleteTime = System.currentTimeMillis() - (startTime + insertTime);
    		
    		List<Node> fetchedNodes = NM.get(sampleRect, resources);
    		long fetchTime = System.currentTimeMillis() - (startTime + insertTime + deleteTime);
    		
    		long exeTime = System.currentTimeMillis() - startTime;
    		totTime += exeTime;
    		
    		//Validate equality
    		assertEquals(refNodeList.size(), fetchedNodes.size());
    		for(Node node : fetchedNodes) {
    			assertTrue(refNodeList.remove(node));
    		}
    		
    		assertEquals(0, refNodeList.size());
    		
    		//Log result
    		exeLog.add("Insert " + nodes.size() + ":" + insertTime + "ms " +
    				"Delete " + deletedNodes.size() + ":" + deleteTime + "ms " +
    				"Fetch " + fetchedNodes.size() + ":" + fetchTime + "ms " + 
    				"Tot exe time =  " + exeTime + "ms"
    			);
    	}
    	
    	System.out.println("");
    	System.out.println("--- RESULT ---");
    	System.out.println("Num iterations: " + NUM_ITERATIONS);
    	System.out.println("Num nodes: " + NUM_NODES);
    	System.out.println("Num resources: " + NUM_RESOURCES);
    	System.out.println("Avg exe time: " + Math.round(totTime/NUM_ITERATIONS) + "ms");
    	
    	System.out.println("");
    	System.out.println("--- Iterations ---");
    	for(String logMsg : exeLog) {
    		System.out.println(logMsg);
    	}
    	
    	System.out.println("");
    	System.out.println("*** Execute testNodeManager end ***");
    	System.out.println("");
	}

    private List<Integer> getRandomResources(int num) {
    	
    	Random rand = new Random();
    	ArrayList<Integer> resources = new ArrayList<Integer>();
    	
    	while(resources.size() < num) {
    		
    		Integer resourceKey = rand.nextInt();
    		if(!resources.contains(resourceKey))
    			resources.add(resourceKey);
    	}
    	
    	return resources;
    }
    
    private Set<Integer> getResourceSample(int num, List<Integer> resources) {
		
    	Random rand = new Random();
    	ArrayList<Integer> clonedResources = new ArrayList<Integer>(resources);
    	
    	if(clonedResources.size() <= num)
    		return new HashSet<Integer>(clonedResources);
    	
    	while(clonedResources.size() > num) {
    		int index = rand.nextInt(clonedResources.size());
    		clonedResources.remove(index);
    	}
    	
    	return new HashSet<Integer>(clonedResources);//sampleResources;
    }
    
    private List<Node> getRandomNodes(int num, List<Integer> resourceKeys, Rectangle rect) {
    	
    	Random rand = new Random();
    	ArrayList<Node> nodes = new ArrayList<Node>();
    		
    	for(int i = 0; i < num; i++) {
    		
    		int resourceIndex = rand.nextInt(resourceKeys.size());
    		Integer resourceKey = resourceKeys.get(resourceIndex);
    		
    		int nx =  rand.nextInt((int)rect.getWidth()) + (int)rect.getX();
    		int ny = rand.nextInt((int)rect.getHeight()) + (int)rect.getY();
    		
    		Node node = new Node(resourceKey, nx, ny, 1.0f);
    		nodes.add(node);
    	}
    	
    	return nodes;
    }
    
    private List<Node> getNodeSample(int num, List<Node> nodes) {
    	
    	Random rand = new Random();
    	List<Node> clonedNodes = new ArrayList<Node>(nodes);
    	
    	if(nodes.size() <= num)
    		return clonedNodes;
    	
    	List<Node> sampleNodes = new ArrayList<Node>();
    	while(sampleNodes.size() < num) {
    		
    		int index = rand.nextInt(clonedNodes.size());
    		Node sampleNode = clonedNodes.remove(index);
    		sampleNodes.add(sampleNode);
    	}
    	
    	return sampleNodes;
    }
}

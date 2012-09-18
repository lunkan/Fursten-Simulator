package fursten.simultor.persistent;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.PersistentTestHelper;
import fursten.simulator.persistent.SessionManager;
import fursten.simulator.session.Session;

public class DAOFactoryTest extends TestCase {

	private final PersistentTestHelper helper = DAOManager.getTestHelper();
	
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
    		
    		SessionManager SM = DAOManager.get().getSessionManager();
    		
    		// Perform test and measure time
    		long startTime = System.currentTimeMillis();
    		
    		Session session = SM.getActive();
    		assertEquals(session.getName(), Session.DEFAULT_NAME);
    		assertEquals(session.getTick(), 0);
    		long initTime = System.currentTimeMillis() - startTime;
    		
    		session.setName(TEST_NAME);
    		session.setTick(i);
    		SM.setActive(session);
    		long insertTime = System.currentTimeMillis() - initTime;
    		
    		Session fetchedSession = SM.getActive();
    		assertEquals(fetchedSession.getName(), TEST_NAME);
    		assertEquals(fetchedSession.getTick(), i);
    		long readTime = System.currentTimeMillis() - insertTime;
    		
    		SM.clear();
    		long clearTime = System.currentTimeMillis() - readTime;
    		long exeTime = System.currentTimeMillis() - startTime;
    		
    		Session clearedSession = SM.getActive();
    		assertEquals(clearedSession.getName(), Session.DEFAULT_NAME);
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
    
    /*@Test
    public void testNodeManager() throws SimulatorException
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
    	BoundingBox bounds = new BoundingBox(Long.valueOf(0), Long.valueOf(0), Long.valueOf(0), Long.valueOf(Integer.MAX_VALUE), Long.valueOf(Integer.MAX_VALUE), 0);
    	
    	for(int i = 0; i < NUM_ITERATIONS; i++) {
    	
    		//clear node-tree
    		for(Integer resourceKey : resourceKeys) {
    			NM.deleteByResourceKey(resourceKey);
    		}
    		
    		List<Node> nodes = getRandomNodes(NUM_NODES, resourceKeys, bounds);
    		List<Node> deletedNodes = getNodeSample(NUM_RESOURCES, nodes);
    		List<Integer> resources = getResourceSample(i, resourceKeys);
    		
    		//Sample bounds
    		int w = rand.nextInt(bounds.xmax - bounds.xmin); 
    		int h = rand.nextInt(bounds.ymax - bounds.ymin);
    		int x = rand.nextInt((bounds.xmax - bounds.xmin) - w);
    		int y = rand.nextInt((bounds.ymax - bounds.ymin) - h);
    		BoundingBox sampleBounds = new BoundingBox(Long.valueOf(x), Long.valueOf(y), Long.valueOf(0), Long.valueOf(w), Long.valueOf(h), Long.valueOf(0));
    		
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
    				
    				if(sampleBounds.xmin <= refNode.getX() &&
    						sampleBounds.xmax >= refNode.getX() &&
    						sampleBounds.ymin <= refNode.getY() &&
    						sampleBounds.ymax >= refNode.getY()) {
    					
    					refNodeList.add(refNode);
    				}
    			}
    		}
    		
    		//Perform test and measure time
    		long startTime = System.currentTimeMillis();
    		
    		NM.insert(nodes);
    		long insertTime = System.currentTimeMillis() - startTime;
    			
    		NM.delete(deletedNodes);
    		long deleteTime = System.currentTimeMillis() - (startTime + insertTime);
    		
    		List<Node> fetchedNodes = NM.get(sampleBounds, resources);
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
	}*/
    
    /*@SuppressWarnings("unchecked")
	@Test
    public void testResourceManager() throws SimulatorException
	{
    	int NUM_ITERATIONS = 10;
    	int NUM_RESOURCES = 10;
    	long totTime = 0;
    	
    	System.out.println("");
    	System.out.println("*** Execute testResourceManager begin ***");
    	
    	Random rand = new Random();ArrayList<String> exeLog = new ArrayList<String>();
    	ResourceManager RM = DAOFactory.get().getResourceManager();
    	
    	List<Integer> resourceKeys = getRandomResources(NUM_RESOURCES);
    	HashMap<Integer, Resource> resources = new HashMap<Integer, Resource>();
    	for(Integer resourceKey : resourceKeys) {
    		Resource resource = new Resource(resourceKey);
    		resources.put(resourceKey, resource);
    	}
    	
    	for(int i = 0; i < NUM_ITERATIONS; i++) {
    		
    		//clear recources
    		RM.delete(RM.getKeys());
    		
    		//get resources to delete
    		List<Integer> deleteResources = getResourceSample(i, resourceKeys);
    		List<Integer> sampleResourceKeys = getResourceSample(NUM_RESOURCES, resourceKeys);
    		
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
    		
    		RM.insert(new ArrayList(resources.values()));
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
    	
	}*/
    
 /*private List<Integer> getRandomResources(int num) {
    	
    	Random rand = new Random();
    	ArrayList<Integer> resources = new ArrayList<Integer>();
    	
    	while(resources.size() < num) {
    		
    		Integer resourceKey = rand.nextInt();
    		
    		if(!resources.contains(resourceKey))
    			resources.add(resourceKey);
    	}
    	
    	return resources;
    }*/
    
    /*private List<Integer> getResourceSample(int num, List<Integer> resources) {
    	
    	Random rand = new Random();
    	List<Integer> clonedResources = new ArrayList<Integer>(resources);
    	
    	if(clonedResources.size() <= num)
    		return clonedResources;
    	
    	//List<Integer> sampleResources = new ArrayList<Integer>();
    	while(clonedResources.size() > num) {
    		
    		int index = rand.nextInt(clonedResources.size());
    		//Integer sampleResource = 
    		clonedResources.remove(index);
    		//sampleResources.add(sampleResource);
    	}
    	
    	return clonedResources;//sampleResources;
    }*/
    
    /*private List<Node> getRandomNodes(int num, List<Integer> resourceKeys, BoundingBox bounds) {
    	
    	Random rand = new Random();
    	ArrayList<Node> nodes = new ArrayList<Node>();
    	int w = bounds.xmax - bounds.xmin;
    	int h = bounds.ymax - bounds.ymin;
    		
    	for(int i = 0; i < num; i++) {
    		
    		int resourceIndex = rand.nextInt(resourceKeys.size());
    		Integer resourceKey = resourceKeys.get(resourceIndex);
    		
    		int nx =  rand.nextInt(w) + bounds.xmin;
    		int ny = rand.nextInt(h) + bounds.ymin;
    		
    		Node node = new Node(resourceKey, nx, ny);
    		nodes.add(node);
    	}
    	
    	return nodes;
    }*/
    
    /*private List<Node> getNodeSample(int num, List<Node> nodes) {
    	
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
    }*/
}

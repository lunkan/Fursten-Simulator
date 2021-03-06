package fursten.simulator;

import static org.junit.Assert.assertEquals;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.command.NodeGetCommand;
import fursten.simulator.command.NodeTransactionCommand;
import fursten.simulator.command.ResourceGetCommand;
import fursten.simulator.command.ResourceEditCommand;
import fursten.simulator.command.InitializeCommand;
import fursten.simulator.command.RunCommand;
import fursten.simulator.world.World;
import fursten.simulator.node.Node;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceSelection;
import fursten.simulator.resource.ResourceWrapper;
import fursten.util.persistent.DAOTestHelper;

public class SimulatorCommandTest {

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
    public void testInitializeCommand() throws Exception {
    
    	ArrayList<String> exeLog = new ArrayList<String>();
    	
    	int NUM_ITERATIONS = 1;
    	String TEST_NAME = "test name";
    	
    	Random rand = new Random();
    	
    	System.out.println("");
    	System.out.println("*** Execute testSessionManager begin ***");
    	
    	for(int i=0; i < NUM_ITERATIONS; i++) {
    		
    		Rectangle rect = new Rectangle(
    				-rand.nextInt(Integer.MAX_VALUE/2),
    				-rand.nextInt(Integer.MAX_VALUE/2),
    				rand.nextInt(Integer.MAX_VALUE),
    				rand.nextInt(Integer.MAX_VALUE)
    			);
    		
    		ResourceManager RM = DAOManager.get().getResourceManager();
    		NodeManager NM = DAOManager.get().getNodeManager();
    		WorldManager SM = DAOManager.get().getWorldManager();
    		
    		Resource resourceData = new Resource();
    		resourceData.setKey(1);
    		RM.put(resourceData);
    		
    		ResourceWrapper resource = ResourceWrapper.getWrapper(resourceData.getKey());
    		Node node = new Node(1);
    		
    		NM.addAll(new ArrayList<Node>(Arrays.asList(node)));
    		
    		World world = new World()
    			.setName(TEST_NAME)
    			.setWidth(Integer.MAX_VALUE)
    			.setHeight(Integer.MAX_VALUE);
    		
    		//Perform test and measure time
    		long startTime = System.currentTimeMillis();
    		
    		new InitializeCommand(world).execute();
    		
    		//Log result
    		exeLog.add("Initialize Tot exe time =  " + (System.currentTimeMillis() - startTime) + "ms");
    		
    		World activeWorld = SM.get();
    		assertEquals(world.getRect(), activeWorld.getRect());
    		assertEquals(world.getName(), activeWorld.getName());
    		
    		assertEquals(null, RM.get(1));
    	}
    	
    	
    	System.out.println("");
    	System.out.println("--- RESULT ---");
    	
    	System.out.println("");
    	System.out.println("--- Iterations ---");
    	for(String logMsg : exeLog) {
    		System.out.println(logMsg);
    	}
    	
    	System.out.println("");
    	System.out.println("*** Execute testSessionManager end ***");
    	System.out.println("");
    }
    
    /*@SuppressWarnings("unchecked")
	@Test*/
    public void testResourceCommands() throws Exception {
    	
    	Random rand = new Random();
    	
    	int NUM_RESOURCES = 3;
    	int NUM_OFFSPRINGS = 2;
    	int NUM_WEIGHT_GROUPS = 2;
    	int NUM_WEIGHTS = 2;
    	
    	System.out.println("");
    	System.out.println("*** Execute testResourceCommands begin ***");
    	
    	ResourceManager RM = DAOManager.get().getResourceManager();
    	
    	//Create test resources
    	ArrayList<Resource> resources = new ArrayList<Resource>();
    	for(int r = 0; r < NUM_RESOURCES; r++) {
    		
    		/*ResourceWrapper resource = new ResourceWrapper(RKM.getNext(0));
    		resource.getResource().setName("Resource@" + r);
        	resource.getResource().setThreshold(rand.nextFloat());*/
    		
        	//Redo
    		/*for(int o = 0; o < NUM_OFFSPRINGS; o++) {
    			resource.putOffspring(rand.nextInt(), rand.nextFloat());
        	}
    		
    		for(int g = 0; g < NUM_WEIGHT_GROUPS; g++) {
        		for(int w = 0; w < NUM_WEIGHTS; w++) {
        			resource.putWeight(NUM_WEIGHT_GROUPS - (g + 1), rand.nextInt(), rand.nextFloat());
        		}
        	}*/
    		
    		//resources.add(resource.getResource());
    	}
    	
    	//Updatera resources
    	int keyToDelete = resources.get(rand.nextInt(resources.size())).getKey();
    	new ResourceEditCommand(null, resources).execute();
    	new ResourceEditCommand(new HashSet<Integer>(Arrays.asList(keyToDelete))).execute();
    	
    	//Get resources
    	ResourceSelection selection = new ResourceSelection(RM.getKeys());
    	List<Resource> retrivedResources = (List<Resource>)new ResourceGetCommand(selection).execute();
    	
    	//Validate
    	for(Resource retrivedResourceObj : retrivedResources) {
    		
    		ResourceWrapper retrivedResource = ResourceWrapper.getWrapper(retrivedResourceObj.getKey());
    		
    		//Get reference
    		ResourceWrapper refResource = null;
    		for(Resource refRes : resources) {
    			if(refRes.getKey() == retrivedResource.getKey()) {
    				refResource = ResourceWrapper.getWrapper(refRes.getKey());
    				break;
    			}
    		}
    		
    		assertEquals(refResource.getName(), retrivedResource.getName());
    		assertEquals(refResource.getThreshold(), retrivedResource.getThreshold(), 0.1);
    		
    		/*for(Integer offspringKey : retrivedResource.getOffspringMap().keySet())
    			assertEquals(refResource.getOffspringMap().get(offspringKey), retrivedResource.getOffspringMap().get(offspringKey), 0.1);
    		*/
    		for(int g = 0; g < retrivedResource.numGroups() ; g++) {
    			for(Integer weightKey : retrivedResource.getDependencies(g)) {
    				assertEquals(refResource.getWeight(g, weightKey), retrivedResource.getWeight(g, weightKey), 0.1);
    			}
    		}
    	}
    	
    	System.out.println("*** Execute testResourceCommands end ***");
    	System.out.println("");
    }
    
    @Test
    public void testNodeCommands() {
    	
    }
    
    /*@SuppressWarnings("unchecked")
	@Test*/
    public void testRunCommand() throws Exception
	{
    	
    	int WORLD_X = -50000;
    	int WORLD_Y = -50000;
    	int WORLD_W = 100000;
    	int WORLD_H = 100000;
    	
    	System.out.println("");
    	System.out.println("*** Execute testRunCommand begin ***");
    	
    	Rectangle rect = new Rectangle(WORLD_X, WORLD_Y, WORLD_W, WORLD_H);
    	Random rand = new Random();
    	
    	//Generate Session
    	World session = new World()
    		.setName("Testing run command")
    		.setWidth(WORLD_W)
    		.setHeight(WORLD_H);
    	
		session.setName("Testing run command");
		new InitializeCommand(session).execute();
		
    	//Generate Resources
    	int grassKey = ResourceKeyManager.getNext();
    	int animalKey = ResourceKeyManager.getNext();
    	int sheepKey = ResourceKeyManager.getNext(animalKey);
    	int wolfKey = ResourceKeyManager.getNext(animalKey);
    	
    	/*ResourceWrapper grassResource = new ResourceWrapper(grassKey);
    	ResourceWrapper sheepResource = new ResourceWrapper(sheepKey);
    	ResourceWrapper wolfResource = new ResourceWrapper(wolfKey);*/
    	
    	//Redo
    	/*sheepResource.putWeight(0, grassKey, 2);
    	sheepResource.putWeight(0, wolfKey, -0.25f);
    	sheepResource.putOffspring(sheepKey, 0.8f);
    	sheepResource.putOffspring(wolfKey, 0.2f);
    	
    	wolfResource.putWeight(0, sheepKey, 0.25f);
    	wolfResource.putOffspring(wolfKey, 1);*/
    	
    	//ArrayList<Resource> resources = new ArrayList<Resource>(Arrays.asList(sheepResource.getResource(), wolfResource.getResource(), grassResource.getResource()));
    	//new ResourceEditCommand(null, resources).execute();
    	
    	//Generate nodes
    	ArrayList<Node> nodes = new ArrayList<Node>();
    	for(int i=0; i < 50; i++) {
    		
    		//Grass
    		for(int g=0; g < 4; g++) {
    			Node node = new Node(grassKey);
    			node.setX(WORLD_X + rand.nextInt(WORLD_W));
    			node.setY(WORLD_Y + rand.nextInt(WORLD_H));
    			node.setV(1.0f);
    			nodes.add(node);
    		}
    		
    		//Sheep
    		for(int s=0; s < 2; s++) {
    			Node node = new Node(sheepKey);
    			node.setX(WORLD_X + rand.nextInt(WORLD_W));
    			node.setY(WORLD_Y + rand.nextInt(WORLD_H));
    			node.setV(1.0f);
    			nodes.add(node);
    		}
    	}
    	
    	new NodeTransactionCommand(null, nodes).execute();
		
		//Perform test and measure time
		long startTime = System.currentTimeMillis();
		
		for(int i=0; i < 100; i++)
    		new RunCommand(rect).execute();
    	
		long totTime = System.currentTimeMillis() - startTime;
		
    	//Expected span of num Nodes - if not within span there is probably a bug in the calculations
    	List<Node> grassNodes = (List<Node>)new NodeGetCommand(rect, grassKey).execute();
    	List<Node> sheepNodes = (List<Node>)new NodeGetCommand(rect, sheepKey).execute();
    	List<Node> wolfNodes = (List<Node>)new NodeGetCommand(rect, wolfKey).execute();
    	
    	assertEquals(200, grassNodes.size());
    	assertEquals(100, sheepNodes.size(), 80);
    	assertEquals(100, wolfNodes.size(), 80);
    	
    	System.out.println("Tot exe time =  " + totTime + "ms");
    	System.out.println("*** Execute testRunCommand end ***");
    	System.out.println("");
	}
}

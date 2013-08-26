package fursten.simulator.command;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.world.World;
import fursten.simulator.joint.Joint;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodePoint;
import fursten.simulator.node.NodeStabilityCalculator;
import fursten.simulator.persistent.JointManager;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.Resource.Offspring;
import fursten.simulator.resource.ResourceWrapper;

public class RunCommand implements SimulatorCommand {
	
	protected static final Logger logger = Logger.getLogger(RunCommand.class.getName());
	public static final String NAME = "Run";
	
	private Random rand;
	private Rectangle rect;
	private NodeStabilityCalculator nodeMath;
	private ResourceManager RM;
	private World world;
	
	public RunCommand(Rectangle rect){
		this.rect = rect;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {

		long timeStampStart = System.currentTimeMillis();
		rand = new Random();
		
		WorldManager SM = DAOFactory.get().getWorldManager();
		world = SM.get();
		int tick = world.getTick() + 1;
		world.setTick(tick);
		SM.set(world);
		
		NodeManager NM = DAOFactory.get().getNodeManager();
		RM = DAOFactory.get().getResourceManager();
		Set<Integer> resourceKeys = RM.getKeys();
		nodeMath = NodeStabilityCalculator.getInstance();
		
		JointManager LM = DAOFactory.get().getLinkManager();
		
		List<Node> removedNodes = new ArrayList<Node>();
		List<Node> addedNodes = new ArrayList<Node>();
		List<Joint> addedJoints = new ArrayList<Joint>();
		
		for(Integer resourceKey : resourceKeys) {
			
			ResourceWrapper resource = ResourceWrapper.getWrapper(resourceKey);
			
			
			//Check if it's the resource time to get updated
			//0 = static -> never updated
			if(resource.getUpdateintervall() != 0) {
				
				if(tick % resource.getUpdateintervall() == resource.getUpdateintervall()-1) {
				
					for(Node node : NM.get(rect, resourceKey)) {
						
						//Validate mortality - node values are reduced by 1 for mortality
						Float randVal = rand.nextFloat();
						if(resource.getMortality() > randVal){
							float reduceVal = Math.min(node.getV(), 1.0f); 
							Node substractNode = node.clone();
							substractNode.setV(reduceVal);
							removedNodes.add(node);
						}
						
						//Even if node is dead it has a chance to breed
						// - or interval calculations will be inaccurate
						//--if(resource.isBreedable()) {
						for(Offspring offspring : resource.getOffsprings()) {
							
							randVal = rand.nextFloat();
							
							if(offspring.getRatio() > randVal) {
								
								//No spores lower than 1 - to infinity protection
								float value = offspring.getMultiplier() * node.getV();
								
								if(value < 1)
									continue;
								
								//Leave no parent with value less than 1
								float cost = node.getV() * offspring.getCost();
								if(node.getV() - cost < 1)
									continue;
								
								Node spore = runSpore(node.getX(), node.getY(), value, offspring.getResource());
								
								if(spore != null) {
									addedNodes.add(spore);
									
									//Add a link if of link type
									if(offspring.getIsLinked()) {
										
										NodePoint nodePoint = node.getNodePoint();
										Joint joint = LM.get(nodePoint);
										if(joint == null) {
											joint = new Joint(nodePoint);
										}
										
										joint.addLink(spore.getNodePoint(), offspring.getMultiplier());
										addedJoints.add(joint);
									}
										
									//Reduce cost from parent
									if(cost > 0) {
										Node reducedNode = node.clone();
										reducedNode.setV(cost);
										removedNodes.add(reducedNode);
									}
								}
							}
						}
					}
				}
			}
		}
		
		new NodeTransactionCommand(removedNodes, addedNodes).execute();
		
		logger.log(Level.INFO, "Run: Deleted " + removedNodes.size() + " & Added " + addedNodes.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}

	private Node runSpore(int x, int y, float v, int r) throws Exception {

		Resource resource = RM.get(r);
		ResourceWrapper resourceWrapper = ResourceWrapper.getWrapper(resource.getKey());
		
		Node spore = new Node(r);
		spore.setV(v);
		
		int numIterations = 1;
		double speed = resourceWrapper.getResource().getSpeed() * (NodeStabilityCalculator.NODE_RADIUS/(1 + numIterations));
		
		//Test initial direction
		double direction = (Math.PI*2) * rand.nextDouble();
		int sporeX = x + (int)(Math.sin(direction) * speed);
		int sporeY = y + (int)(Math.cos(direction) * speed);
		float stability = nodeMath.calculateStability(sporeX, sporeY, resourceWrapper);
		spore.setX(sporeX);
		spore.setY(sporeY);
		
		for(int i = 0; i < numIterations; i++) {
			
			direction = (direction + Math.PI*-0.5 + Math.PI*rand.nextDouble()) % Math.PI*2;
			sporeX += (int)(Math.sin(direction) * (speed / Math.max(1, 2+stability)));
			sporeY += (int)(Math.cos(direction) * (speed / Math.max(1, 2+stability)));
			float nextStability = nodeMath.calculateStability(sporeX, sporeY, resourceWrapper);
			
			if(nextStability > stability) {
				spore.setX(sporeX);
				spore.setY(sporeY);
				stability = nextStability;
			}
		}
		
		//float stability = 0;
		
		//Best of 3 - Simpel test
		/*for(int i = 0; i < 3; i++) {
			int seedX = rand.nextInt(NodeStabilityCalculator.NODE_RADIUS*8)-(NodeStabilityCalculator.NODE_RADIUS*4);
			int seedY = rand.nextInt(NodeStabilityCalculator.NODE_RADIUS*8)-(NodeStabilityCalculator.NODE_RADIUS*4);
			float nextStability = nodeMath.calculateStability(seedX, seedY, resourceWrapper);
			
			if(nextStability > stability) {
				spore.setX(x + seedX);
				spore.setY(y + seedY);
				stability = nextStability;
			}
		}*/
		
		//Check that no node is out of world bounds
		if(!world.getRect().contains(spore.getX(), spore.getY()))
			return null;
		
		float threshold = spore.getV() * resourceWrapper.getThreshold();
		if(stability - threshold > 0)
    		return spore;
    	else
    		return null;
	}
}

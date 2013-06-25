package fursten.simulator.command;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.world.World;
import fursten.simulator.link.Link;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeActivityManager;
import fursten.simulator.node.NodeStabilityCalculator;
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
		
		List<Node> removedNodes = new ArrayList<Node>();
		List<Node> addedNodes = new ArrayList<Node>();
		List<Link> addedLinks = new ArrayList<Link>();
		
		for(Integer resourceKey : resourceKeys) {
			
			ResourceWrapper resource = ResourceWrapper.getWrapper(RM.get(resourceKey));
			
			//Check if it's the resource time to get updated
			//0 = static -> never updated
			if(resource.getUpdateintervall() != 0) {
				
				if(tick % resource.getUpdateintervall() == resource.getUpdateintervall()-1) {
				
					for(Node node : NM.get(rect, resourceKey)) {
						
						Float randVal = rand.nextFloat();
						if(resource.getMortality() > randVal){
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
										Link newLink = new Link();
										newLink.setChildNode(spore);
										newLink.setParentNode(node);
										addedLinks.add(newLink);
									}
										
									//Reduce cost from parent
									if(cost > 0) {
										Node reducedNode = node.clone();
										reducedNode.setV(cost);
										removedNodes.add(reducedNode);
									}
									
									//System.out.println("#New " + spore + " " + cost + " - " + value + " # " + node.getV());
									
								}
							}
						}
					}
				}
			}
		}
		
		/*if(removedNodes.size() > 0) {
			NodeActivityManager.invalidate(removedNodes);
			NM.delete(removedNodes);
		}
		
		if(addedNodes.size() > 0) {
			NodeActivityManager.invalidate(addedNodes);
			NM.insert(addedNodes);
		}*/
		
		new NodeTransactionCommand(removedNodes, addedNodes);
		
		logger.log(Level.INFO, "Run: Deleted " + removedNodes.size() + " & Added " + addedNodes.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}

	private Node runSpore(int x, int y, float v, int r) throws Exception {

		Resource resource = RM.get(r);
		ResourceWrapper resourceWrapper = ResourceWrapper.getWrapper(resource);
		
		Node spore = new Node(r);
		spore.setV(v);
		float stability = 0;
		
		//Best of 3
		for(int i = 0; i < 3; i++) {
			int seedX = rand.nextInt(NodeStabilityCalculator.NODE_RADIUS*8)-(NodeStabilityCalculator.NODE_RADIUS*4);
			int seedY = rand.nextInt(NodeStabilityCalculator.NODE_RADIUS*8)-(NodeStabilityCalculator.NODE_RADIUS*4);
			float nextStability = nodeMath.calculateStability(seedX, seedY, resourceWrapper);
			
			if(nextStability > stability) {
				spore.setX(x + seedX);
				spore.setY(y + seedY);
				stability = nextStability;
			}
		}
		
		//Check that no node is out of world bounds
		if(!world.getRect().contains(spore.getX(), spore.getY()))
			return null;
		
		//float stability = nodeMath.calculateStability(spore.getX(), spore.getY(), resourceWrapper);
		//stability *= resourceWrapper.getThreshold();//nodeMath.normalizeStability(stability, resourceWrapper.getThreshold());
		
		//if(stability > rand.nextFloat())
		float threshold = spore.getV() * resourceWrapper.getThreshold();
		if(stability - threshold > 0)
    		return spore;
    	else
    		return null;
	}
}

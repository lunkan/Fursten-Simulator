package fursten.simulator.command;

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
		world = SM.getActive();
		int tick = world.getTick() + 1;
		world.setTick(tick);
		SM.setActive(world);
		
		NodeManager NM = DAOFactory.get().getNodeManager();
		RM = DAOFactory.get().getResourceManager();
		Set<Integer> resourceKeys = RM.getKeys();
		nodeMath = NodeStabilityCalculator.getInstance();
		
		List<Node> removedNodes = new ArrayList<Node>();
		List<Node> addedNodes = new ArrayList<Node>();
		
		for(Integer resourceKey : resourceKeys) {
			
			ResourceWrapper resource = ResourceWrapper.getWrapper(RM.get(resourceKey));
			
			//Check if it's the resource time to get updated
			if(resource.getUpdateintervall() != 0 && tick % resource.getUpdateintervall() == resource.getUpdateintervall()-1) {
				
				for(Node node : NM.get(rect, resourceKey)) {
					
					Float randVal = rand.nextFloat();
					float mortality = resource.getMortality();
					float adjMortality = resource.adjustByInterval(mortality);
					
					if(adjMortality > randVal){
						removedNodes.add(node);
					}
					
					//Even if node is dead it has a chance to breed
					// - or interval calculations will be inaccurate
					if(resource.isBreedable()) {
						
						for(Offspring offspring : resource.getOffsprings()) {
							
							randVal = rand.nextFloat();
							float breedRatio = offspring.getRatio();
							float adjBreedRato = resource.adjustByInterval(breedRatio);
							
							if(adjBreedRato > randVal) {
								
								Node spore = runSpore(node.getX(), node.getY(), offspring.getResource());
								if(spore != null)
									addedNodes.add(spore);
							}
						}
			    	}
				}
			}
		}
		
		if(removedNodes.size() > 0) {
			NodeActivityManager.invalidate(removedNodes);
			NM.delete(removedNodes);
		}
		
		if(addedNodes.size() > 0) {
			NodeActivityManager.invalidate(addedNodes);
			NM.insert(addedNodes);
		}
		
		logger.log(Level.INFO, "Run: Deleted " + removedNodes.size() + " & Added " + addedNodes.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}

	private Node runSpore(int x, int y, int r) throws Exception {

		Resource resource = RM.get(r);
		ResourceWrapper resourceWrapper = ResourceWrapper.getWrapper(resource);
		
		int seedX = rand.nextInt(NodeStabilityCalculator.NODE_RADIUS*8)-(NodeStabilityCalculator.NODE_RADIUS*4);
		int seedY = rand.nextInt(NodeStabilityCalculator.NODE_RADIUS*8)-(NodeStabilityCalculator.NODE_RADIUS*4);
		
		Node spore = new Node(r);
		spore.setX(x + seedX);
		spore.setY(y + seedY);
		
		//Check that no node is out of world bounds
		if(!world.getRect().contains(spore.getX(), spore.getY()))
			return null;
		
		float stability = nodeMath.calculateStability(spore.getX(), spore.getY(), resourceWrapper, false);
		//stability *= resourceWrapper.getThreshold();//nodeMath.normalizeStability(stability, resourceWrapper.getThreshold());
		
		//if(stability > rand.nextFloat())
		if((stability * resourceWrapper.getThreshold()) > 1)
    		return spore;
    	else
    		return null;
	}
}

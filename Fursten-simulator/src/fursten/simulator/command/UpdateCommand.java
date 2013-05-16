package fursten.simulator.command;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.node.Node;
import fursten.simulator.node.NodeActivityManager;
import fursten.simulator.node.NodeStabilityCalculator;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.ResourceDependencyManager;
import fursten.simulator.resource.ResourceWrapper;
import fursten.simulator.world.World;

public class UpdateCommand implements SimulatorCommand {
	
	protected static final Logger logger = Logger.getLogger(RunCommand.class.getName());
	public static final String NAME = "Update";
	
	//private Rectangle rect;
	private NodeStabilityCalculator nodeMath;
	private ResourceManager RM;
	
	public UpdateCommand(Rectangle rect){
		//this.rect = rect;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {

		long timeStampStart = System.currentTimeMillis();
		int numCalNode = 0;
		int numRect = 0;
		
		WorldManager SM = DAOFactory.get().getWorldManager();
		World world = SM.getActive();
		
		NodeManager NM = DAOFactory.get().getNodeManager();
		RM = DAOFactory.get().getResourceManager();
		nodeMath = NodeStabilityCalculator.getInstance();
		
		List<Node> removedNodes = new ArrayList<Node>();
		Rectangle updateRect = new Rectangle();
		
		//System.out.println("*");
		
		//Loop all resources that have been updated
		Set<Integer> invalidResources = NodeActivityManager.getInvalidResources(world.getTick());
		for(Integer invalidResource : invalidResources) {
			
			ResourceWrapper resource = ResourceWrapper.getWrapper(RM.get(invalidResource));
			if(resource.isDependent()){
				
				//Find and loop all regions where an update has occurred
				for(Rectangle rect : NodeActivityManager.getInvalidRectByResourceKey(invalidResource, world.getTick())) {
				
					numRect++;
					
					//Adjust rect to cover possible node-radius-impact
					updateRect.x = rect.x - NodeStabilityCalculator.NODE_RADIUS;
					updateRect.y = rect.y - NodeStabilityCalculator.NODE_RADIUS;
					updateRect.width = rect.width + (NodeStabilityCalculator.NODE_RADIUS * 2);
					updateRect.height = rect.height + (NodeStabilityCalculator.NODE_RADIUS * 2);
					
					//Fetch all nodes in the region to be revalidated
					for(Node node : NM.get(updateRect, invalidResource)) {
						numCalNode++;
						float stability = nodeMath.calculateStability(node.getX(), node.getY(), resource);
						if(stability < 0)  {
							removedNodes.add(node);
				    	}
					}
				}
			}
		}
		
		NodeActivityManager.clear();
		
		if(removedNodes.size() > 0) {
			NM.delete(removedNodes);
			NodeActivityManager.invalidate(removedNodes);
		}
		
		logger.log(Level.INFO, "Update: NumCal " + numCalNode + " Rects " + numRect + " Deleted " + removedNodes.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}
}
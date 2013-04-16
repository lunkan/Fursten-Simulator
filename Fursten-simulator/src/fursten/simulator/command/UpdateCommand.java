package fursten.simulator.command;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.node.Node;
import fursten.simulator.node.NodeStabilityCalculator;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.ResourceWrapper;

public class UpdateCommand implements SimulatorCommand {
	
	protected static final Logger logger = Logger.getLogger(RunCommand.class.getName());
	public static final String NAME = "Update";
	
	private Rectangle rect;
	private NodeStabilityCalculator nodeMath;
	private ResourceManager RM;
	
	public UpdateCommand(Rectangle rect){
		this.rect = rect;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {

		long timeStampStart = System.currentTimeMillis();
		
		NodeManager NM = DAOFactory.get().getNodeManager();
		RM = DAOFactory.get().getResourceManager();
		Set<Integer> resourceKeys = RM.getKeys();
		nodeMath = NodeStabilityCalculator.getInstance();
		
		List<Node> removedNodes = new ArrayList<Node>();
		
		for(Integer resourceKey : resourceKeys) {
			
			ResourceWrapper resource = ResourceWrapper.getWrapper(RM.get(resourceKey));
			
			if(!resource.isStatic() && !resource.getIsLocked()) {
				
				for(Node node : NM.get(rect, resourceKey)) {
					
					float stability = nodeMath.calculateStability(node.getX(), node.getY(), resource, false);
					if(stability < 1.0f)  {
						removedNodes.add(node);
			    	}
				}
			}
		}
		
		NM.delete(removedNodes);
		
		logger.log(Level.INFO, "Update: Deleted " + removedNodes.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}
}

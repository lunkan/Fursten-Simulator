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
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceDependencyManager;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceWrapper;

public class ResourceEditCommand implements SimulatorCommand {

	protected static final Logger logger = Logger.getLogger(ResourceEditCommand.class.getName());
	public static final String NAME = "DeleteResources";
	
	private Set<Integer> deleteResources;
	private List<Resource> insertResources;

	public ResourceEditCommand(Set<Integer> deleteResources) {
		this.deleteResources = deleteResources;
	}
	
	public ResourceEditCommand(Set<Integer> deleteResources, List<Resource> insertResources) {
		this.deleteResources = deleteResources;
		this.insertResources = insertResources;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		long timeStampStart = System.currentTimeMillis();
		int numDeleted = 0;
		int numInserted = 0;
		
		WorldManager WM = DAOFactory.get().getWorldManager();
		NodeManager NM = DAOFactory.get().getNodeManager();
		ResourceManager RM = DAOFactory.get().getResourceManager();
		//Set<Integer> validResourceKeys = RM.getKeys();
		
		//Refresh resource dependent handlers
		NodeStabilityCalculator.clean();
		
		if(deleteResources != null) {
			
			//Delete all nodes in of the resource type
			Rectangle worldBounds = WM.get().getRect();
			List<Node> deletedNodes = NM.get(worldBounds, deleteResources);
			new NodeTransactionCommand(deletedNodes).execute();
			
			//Delete all resources
			numDeleted = RM.removeAll(deleteResources);
			if(numDeleted != deleteResources.size())
				throw new Exception("Database may be corrupt! Resources could not be deleted.");
		}
		
		if(insertResources != null) {
			
			//Validate resources
			//ResourceWrapper resourceWrapper = new ResourceWrapper();
			for(Resource resource : insertResources) {
				
				if(!ResourceWrapper.getWrapper(resource.getKey()).isValid()) {
					logger.log(Level.WARNING, "Resources is invalid: " + resource);
					throw new Exception("Resources is invalid.");
				}
			}
			
			numInserted = RM.putAll(insertResources);
			if(numInserted != insertResources.size())
				throw new Exception("Database may be corrupt! Resources could not be inserted.");
		}
		
		//Important! Clear resource manager cache
		if(numDeleted != 0 || numInserted != 0) {
			//Lazy clean - clean all
			new CleanCommand().execute();
		}
			
		
		logger.log(Level.INFO, "Deleted " + numDeleted + " resources, Inserted " + numInserted + " resources. Time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}
}

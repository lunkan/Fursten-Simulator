package fursten.simulator.command;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.node.NodeStabilityCalculator;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;

public class ResourceUpdateCommand implements SimulatorCommand {

	protected static final Logger logger = Logger.getLogger(ResourceUpdateCommand.class.getName());
	public static final String NAME = "DeleteResources";
	
	private Set<Integer> deleteResources;
	private List<Resource> insertResources;

	public ResourceUpdateCommand(Set<Integer> deleteResources) {
		this.deleteResources = deleteResources;
	}
	
	public ResourceUpdateCommand(Set<Integer> deleteResources, List<Resource> insertResources) {
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
		
		NodeManager NM = DAOFactory.get().getNodeManager();
		ResourceManager RM = DAOFactory.get().getResourceManager();
		Set<Integer> validResourceKeys = RM.getKeys();
		
		//Refresh resource dependent handlers
		NodeStabilityCalculator.clean();
		
		if(deleteResources != null) {
			
			//Delete all nodes in of the resource type
			for(Integer resourceKey : validResourceKeys)
				NM.deleteByResourceKey(resourceKey);
			
			//Delete all resources
			numDeleted = RM.delete(deleteResources);
			if(numDeleted != deleteResources.size())
				throw new Exception("Database may be corrupt! Resources could not be deleted.");
		}
		
		if(insertResources != null) {
			
			numInserted = RM.insert(insertResources);
			if(numInserted != insertResources.size())
				throw new Exception("Database may be corrupt! Resources could not be inserted.");
		}
			
		logger.log(Level.INFO, "Deleted " + numDeleted + " resources, Inserted " + numInserted + " resources. Time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}
}

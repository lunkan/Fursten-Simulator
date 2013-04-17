package fursten.simulator.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceSelection;
import fursten.simulator.resource.ResourceWrapper;

public class ResourceGetCommand implements SimulatorCommand {

	protected static final Logger logger = Logger.getLogger(ResourceGetCommand.class.getName());
	public static final String NAME = "GetResources";
	private ResourceSelection selection;
	
	public ResourceGetCommand(ResourceSelection selection) {
		this.selection = selection;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		long timeStampStart = System.currentTimeMillis();
		
		ResourceManager RM = DAOFactory.get().getResourceManager();
		List<Resource> resources;
		
		switch(selection.getMethod()) {
		
			case ALL :
				resources = RM.get(RM.getKeys());
				break;
				
			case MATCH :
				resources = RM.get(selection.getResourceKeys());
				break;
				
			case CHILDREN :
				
				HashSet<Integer> childKeys = new HashSet<Integer>();
				for(Integer selectedKey : selection.getResourceKeys()) {
					
					for(Integer childKey : ResourceKeyManager.getChildren(selectedKey)) {
						
						if(!childKeys.contains(childKey)) {
							childKeys.add(childKey);
						}
					}
				}
				
				resources = RM.get(childKeys);
				break;
				
			case PARENTS :
				
				HashSet<Integer> parentKeys = new HashSet<Integer>();
				for(Integer selectedKey : selection.getResourceKeys()) {
					
					for(Integer parentKey : ResourceKeyManager.getParents(selectedKey)) {
						
						if(!parentKeys.contains(parentKey)) {
							parentKeys.add(parentKey);
						}
					}
				}
				
				resources = RM.get(parentKeys);
				break;
				
			case NEXT :
				
				HashSet<Integer> nextKeys = new HashSet<Integer>();
				for(Integer selectedKey : selection.getResourceKeys()) {
					
					Integer nextKey = ResourceKeyManager.getNext(selectedKey);
					if(!nextKeys.contains(nextKey)) {
						nextKeys.add(nextKey);
					}
				}
				
				resources = new ArrayList<Resource>();
				for(Integer nextKey : nextKeys) {
					Resource newResource = new Resource();
					newResource.setKey(nextKey);
					resources.add(newResource);
				}
				
				break;
				
			default :
				throw new Exception("No fetch method is provided for resource get");
		}
		
		logger.log(Level.INFO, "Retrived " + resources.size() + " resources by method " + selection.getMethod() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return resources;
	}
}

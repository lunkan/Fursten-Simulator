package fursten.simulator.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;

public class ResourceDependencyManager {
	
	private List<Resource> resources;
	private HashMap<Integer, Set<Integer>> dependencyMap;
	private static ResourceDependencyManager instance; 
	
	private ResourceDependencyManager() {
		
		ResourceManager RM = DAOFactory.get().getResourceManager();
		resources = RM.get(RM.getKeys());
		dependencyMap = new HashMap<Integer, Set<Integer>>();
	}
	
	private static ResourceDependencyManager getInstance() {
		
		if(instance == null)
			instance = new ResourceDependencyManager();
			
		return instance;
    }
	
	public static Set<Integer> getDependents(int resourceKey) {
		return getInstance()._getDependents(resourceKey);
	}
	
	public Set<Integer> _getDependents(int resourceKey) {
		
		//Check and return if cached
		if(dependencyMap.containsKey(resourceKey))
			return dependencyMap.get(resourceKey);
		
		//Loop all resources - if resource has a dependency to the resource add it to the cacheMap
		Set<Integer> dependentResources = new HashSet<Integer>();
		for(Resource resource : resources) {
			
			ResourceWrapper wrapper;
				
			//Loop and add dependencies - also descendants are dependent
			wrapper = ResourceWrapper.getWrapper(resource);
			for(Integer dependency : wrapper.getDependencies()){
				
				if(ResourceKeyManager.isDescendant(resourceKey, dependency)) {
					dependentResources.add(resource.getKey());
					break;
				}
			}
		}
		
		dependencyMap.put(resourceKey, dependentResources);
		return dependentResources;
	}
	
	public static void clear() {
		getInstance()._clear();
	}
	
	public void _clear() {
		ResourceManager RM = DAOFactory.get().getResourceManager();
		resources = RM.get(RM.getKeys());
		dependencyMap.clear();
	}
}

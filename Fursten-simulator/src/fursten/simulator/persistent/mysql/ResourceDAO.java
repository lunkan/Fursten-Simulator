package fursten.simulator.persistent.mysql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.resource.Resource;

public class ResourceDAO implements ResourceManager {

	private static final Logger logger = Logger.getLogger(ResourceDAO.class.getName());
	private static final String RESOURCE_TREE_KEY = "recourceTree";
	private static HashMap<Integer, Resource> cachedResourcesMap = null;
	
	private static ResourceDAO instance = new ResourceDAO();
	
	private ResourceDAO() {
		
		//if(cachedResourcesMap == null) {
			cachedResourcesMap = new HashMap<Integer, Resource>();
		//}
			System.out.println("AppEngineResourceDAO init");
	}
	
	public static ResourceDAO getInstance() {
        return instance;
    }
	
	public void clearCache() {
		cachedResourcesMap = new HashMap<Integer, Resource>();
	}
	
	public int insert(Resource recource) {
		
		ArrayList<Resource> recources = new ArrayList<Resource>();
		recources.add(recource);
		return insert(recources);
	}
	
	public boolean delete(int key) {
		ArrayList<Integer> recources = new ArrayList<Integer>();
		recources.add(key);
		return delete(recources);
	}
	
	public Resource get(int key) {
		
		if(cachedResourcesMap != null) {
			if(cachedResourcesMap.containsKey(key)) {
				return cachedResourcesMap.get(key);
			}
		}
			
		ArrayList<Integer> recources = new ArrayList<Integer>();
		recources.add(key);
		List<Resource> result = get(recources);
		
		if(result.size() > 0)
			return result.get(0);
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public int insert(List<Resource> recources)  {
		
		/*PersistentWrapper wrapper;
		HashMap<Integer, Resource> resourceMap;
		
		int retries = 3;
		while (true) {
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Transaction txn = pm.currentTransaction();
			
			try {
				
				txn.begin();
				
				try {
					wrapper = pm.getObjectById(PersistentWrapper.class, RESOURCE_TREE_KEY);
					wrapper = pm.detachCopy(wrapper);
					resourceMap = (HashMap<Integer, Resource>)BinaryTranslator.binaryToObject(wrapper.byteData.getBytes());
				}
				catch (javax.jdo.JDOObjectNotFoundException notFound) {
					wrapper = new PersistentWrapper();
					wrapper.key = KeyFactory.createKey(PersistentWrapper.class.getSimpleName(), RESOURCE_TREE_KEY);
					resourceMap = new HashMap<Integer, Resource>();
				}
				catch(Exception e) {
					logger.log(Level.SEVERE, "Could not read Object", e);
					return -1;
				}
				
				//Insert resources
				for(Resource insertResource : recources) {
					resourceMap.put(insertResource.getKey(), insertResource);
				}
				
				//Cache map (last version!)
				cachedResourcesMap = resourceMap;
				
				//Save tree
				wrapper.byteData = new Blob(BinaryTranslator.objectToBinary(resourceMap));
				pm.makePersistent(wrapper);
				txn.commit();
				
			    break;
			}
			catch(Exception e) {
				if (retries >= 3) {
					logger.log(Level.SEVERE, "Retry no: " + retries + " #" + e.toString());
					return -1;
				}
			}
			finally {
			    if (txn.isActive()) {
			        txn.rollback();
			    }
			    
			    pm.close();
			}
			
			retries++;
		}
		
		return recources.size();*/
		
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public boolean delete(List<Integer> keys) {
		
		/*PersistentWrapper wrapper;
		HashMap<Integer, Resource> resourceMap;
		
		int retries = 3;
		while (true) {
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Transaction txn = pm.currentTransaction();
			
			try {
				
				txn.begin();
				
				wrapper = pm.getObjectById(PersistentWrapper.class, RESOURCE_TREE_KEY);
				wrapper = pm.detachCopy(wrapper);
				resourceMap = (HashMap<Integer, Resource>)BinaryTranslator.binaryToObject(wrapper.byteData.getBytes());
				
				//Delete recources
				for(Integer resourceKey : keys) {
					resourceMap.remove(resourceKey);
				}
				
				//Cache map (last version!)
				cachedResourcesMap = resourceMap;
				
				wrapper.byteData = new Blob(BinaryTranslator.objectToBinary(resourceMap));
				pm.makePersistent(wrapper);
			    txn.commit();
			    
			    break;
			}
			catch (javax.jdo.JDOObjectNotFoundException notFound) {
				//no recources to delete
				break;
			}
			catch(Exception e) {
				if (retries >= 3) {
					logger.log(Level.SEVERE, e.toString());
					return false;
				}
			}
			finally {
			    if (txn.isActive()) {
			        txn.rollback();
			    }
			    
			    pm.close();
			}
			
			retries++;
		}*/
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<Resource> get(List<Integer> keys) {
		
		/*PersistentWrapper wrapper;
		HashMap<Integer, Resource> resourceMap;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		ArrayList<Resource> result = new ArrayList<Resource>();
		
		if(cachedResourcesMap.size() == 0) {
			
			try {
				wrapper = pm.getObjectById(PersistentWrapper.class, RESOURCE_TREE_KEY);
				resourceMap = (HashMap<Integer, Resource>)BinaryTranslator.binaryToObject(wrapper.byteData.getBytes());
				
				//Cache map (last version!)
				cachedResourcesMap = resourceMap;
				
				//Add nodes
				for(Integer resourceKey : keys) {
					if(resourceMap.containsKey(resourceKey)) {
						result.add(resourceMap.get(resourceKey));
					}
				}
			}
			catch (javax.jdo.JDOObjectNotFoundException notFound) {
				//No resources
			}
			catch(Exception e) {
				logger.log(Level.SEVERE, "Could not read Object", e);
				return null;
			}
			finally {
				pm.close();
			}
		}
		
		else {
			//Add nodes
			for(Integer resourceKey : keys) {
				if(cachedResourcesMap.containsKey(resourceKey)) {
					result.add(cachedResourcesMap.get(resourceKey));
				}
			}
		}
		
		return result;*/
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getKeys() {
		
		/*if(cachedResourcesMap.size() != 0) {
			return Collections.list(Collections.enumeration(cachedResourcesMap.keySet()));
		}
		else {
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			
			try {
				PersistentWrapper wrapper = pm.getObjectById(PersistentWrapper.class, RESOURCE_TREE_KEY);
				HashMap<Integer, Resource> resourcesMap = (HashMap<Integer, Resource>)BinaryTranslator.binaryToObject(wrapper.byteData.getBytes());
				
				cachedResourcesMap = resourcesMap;
				return Collections.list(Collections.enumeration(resourcesMap.keySet()));
			}
			catch (javax.jdo.JDOObjectNotFoundException notFound) {
				return new ArrayList<Integer>();
			}
			catch(Exception e) {
				logger.log(Level.SEVERE, "Could not read Object", e);
				return null;
			}
			finally {
				pm.close();
			}
		}*/
		
		return null;
	}
}

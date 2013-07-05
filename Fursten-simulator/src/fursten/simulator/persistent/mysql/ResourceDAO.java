package fursten.simulator.persistent.mysql;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialBlob;

import fursten.simulator.persistent.AutoSaveManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.Persistable;
import fursten.simulator.resource.Resource;
import fursten.util.BinaryTranslator;

public class ResourceDAO implements ResourceManager, Persistable {

	private static final Logger logger = Logger.getLogger(ResourceDAO.class.getName());
	private static final int RESOURCE_TREE_ID = 1;
	private static HashMap<Integer, Resource> resourcesMap;
	
	private static boolean changed;
	
	private static ResourceDAO instance = null;
	
	private ResourceDAO() {
		resourcesMap = new HashMap<Integer, Resource>();
	}
	
	public static ResourceDAO getInstance() {
		
		if(instance == null)
			instance = new ResourceDAO();
		
        return instance;
    }
	
	public synchronized int put(Resource... resources) {
		return putAll(Arrays.asList(resources));
	}
	
	public synchronized int putAll(List<Resource> resources)  {
		
		for(Resource insertResource : resources)
			resourcesMap.put(insertResource.getKey(), insertResource);
		
		changed = true;
		return resources.size();
	}
	
	public synchronized int remove(int... keys) {
		HashSet<Integer> recources = new HashSet<Integer>();
		for(int key : keys)
			recources.add(key);
		
		return removeAll(recources);
	}
	
	public synchronized int removeAll(Set<Integer> keys) {
			
		int deleteCount = 0;
		for(Integer resourceKey : keys) {
			if(resourcesMap.remove(resourceKey) != null) {
				deleteCount++;
			}
		}
		
		if(deleteCount > 0)
			changed = true;
		
		return deleteCount;
	}
	
	public synchronized boolean clear() {
		resourcesMap.clear();
		changed = true;
		return true;
	}
	
	public synchronized boolean reset() {
		instance = null;
		changed = true;
		return false;
	}
	
	public Resource get(int key) {
		
		if(resourcesMap.containsKey(key))
			return resourcesMap.get(key);
			
		return null;
	}
	
	public List<Resource> get(Set<Integer> keys) {
		
		ArrayList<Resource> result = new ArrayList<Resource>();
		for(Integer resourceKey : keys) {
			if(resourcesMap.containsKey(resourceKey)) {
				result.add(resourcesMap.get(resourceKey));
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Integer> getKeys() {
		
		if(resourcesMap == null) {
				
			Connection con = DAOFactory.getConnection();
			PreparedStatement statement = null;
			
			try {
				statement = con.prepareStatement("select resource_object from resources where id = ?");
				statement.setInt(1, RESOURCE_TREE_ID);
				statement.setMaxRows(1);
				ResultSet resultSet = statement.executeQuery();
				
				if(resultSet.first()) {
					Blob resourcesBin = resultSet.getBlob("resource_object");
					resourcesMap = (HashMap<Integer, Resource>)BinaryTranslator.binaryToObject(resourcesBin.getBinaryStream());
				}
				else {
					return new HashSet<Integer>();
				}
				
				statement.close();
			}
			catch(Exception e) {
				logger.log(Level.SEVERE, "Retry no: " + e.toString());
				return new HashSet<Integer>();
			}
			finally {
				DAOFactory.freeConnection(con);
			}
		}
		
		return new HashSet<Integer>(resourcesMap.keySet());
	}

	@Override
	public boolean hasChanged() {
		return changed;
	}

	@Override
	public synchronized void clean() {

		if(changed)
			pushPersistent();
		
		pullPersistent();
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized void pullPersistent() {
		
		Connection con = DAOFactory.getConnection();
			
		try {
			PreparedStatement statement = con.prepareStatement("select resource_object from resources where id = ?");
			statement.setInt(1, RESOURCE_TREE_ID);
			statement.setMaxRows(1);
			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.first()) {
				Blob resourcesBin = resultSet.getBlob("resource_object");
				resourcesMap = (HashMap<Integer, Resource>)BinaryTranslator.binaryToObject(resourcesBin.getBinaryStream());
				logger.log(Level.INFO, "Pulled " + resourcesMap.size() + " resources from database.");
			}
			else {
				logger.log(Level.INFO, "Pulled resources from database but no resources was found.");
			}
			
			changed = false;
			statement.close();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Retry no: " + e.toString());
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}

	@Override
	public synchronized void pushPersistent() {
		
		if(!changed)
			return;
		
		Connection con = DAOFactory.getConnection();
		
		try {
			
			PreparedStatement statement = con.prepareStatement("select resource_object from resources where id = ?");
			statement.setInt(1, RESOURCE_TREE_ID);
			statement.setMaxRows(1);
			ResultSet resultSet = statement.executeQuery();
			
			boolean isNew = true;
			if(resultSet.first())
				isNew = false;
			
			statement.close();
			
			Blob resourcesBin;
			resourcesBin = new SerialBlob(BinaryTranslator.objectToBinary((Serializable)resourcesMap.clone()));
			
			if(isNew) {
				statement = con.prepareStatement("insert into resources(id, resource_object) values (?, ?)");
				statement.setInt(1, RESOURCE_TREE_ID);
				statement.setBlob(2, resourcesBin);
				statement.executeUpdate();
				statement.close();
			}
			else {
				statement = con.prepareStatement("update resources set resource_object = ? where id = ?");
				statement.setBlob(1, resourcesBin);
				statement.setInt(2, RESOURCE_TREE_ID);
				statement.executeUpdate();
				statement.close();
			}
			
			changed = false;
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Retry no: " + e.toString());
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}
}

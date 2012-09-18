package fursten.simulator.persistent.mysql;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialBlob;

import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.resource.Resource;
import fursten.simulator.session.Session;
import fursten.utils.BinaryTranslator;

public class ResourceDAO implements ResourceManager {

	private static final Logger logger = Logger.getLogger(ResourceDAO.class.getName());
	private static final int RESOURCE_TREE_ID = 1;
	private static HashMap<Integer, Resource> cachedResourcesMap = null;
	
	private static ResourceDAO instance = new ResourceDAO();
	
	private ResourceDAO() {
		//...
	}
	
	public static ResourceDAO getInstance() {
        return instance;
    }
	
	public void clearCache() {
		cachedResourcesMap = null;
	}
	
	public int insert(Resource recource) {
		
		ArrayList<Resource> recources = new ArrayList<Resource>();
		recources.add(recource);
		return insert(recources);
	}
	
	public int delete(int key) {
		HashSet<Integer> recources = new HashSet<Integer>();
		recources.add(key);
		return delete(recources);
	}
	
	public Resource get(int key) {
		
		if(cachedResourcesMap != null) {
			if(cachedResourcesMap.containsKey(key)) {
				return cachedResourcesMap.get(key);
			}
		}
			
		HashSet<Integer> recources = new HashSet<Integer>();
		recources.add(key);
		List<Resource> result = get(recources);
		
		if(result.size() > 0)
			return result.get(0);
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public int insert(List<Resource> recources)  {
			
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			statement = con.prepareStatement("select resource_object from resources where id = ?");
			statement.setInt(1, RESOURCE_TREE_ID);
			statement.setMaxRows(1);
			ResultSet resultSet = statement.executeQuery();
			boolean isNew = true;
			
			HashMap<Integer, Resource> resourceMap;
			Blob resourcesBin;
			
			if(resultSet.first()) {
				isNew = false;
				resourcesBin = resultSet.getBlob("resource_object");
				resourceMap = (HashMap<Integer, Resource>)BinaryTranslator.binaryToObject(resourcesBin.getBinaryStream());
			}
			else {
				resourceMap = new HashMap<Integer, Resource>();
			}
			
			statement.close();
			
			//Insert resources
			for(Resource insertResource : recources) {
				resourceMap.put(insertResource.getKey(), insertResource);
			}
			
			resourcesBin = new SerialBlob(BinaryTranslator.objectToBinary(resourceMap));
			
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
			
			cachedResourcesMap = resourceMap;
		    return recources.size();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Retry no: " + e.toString());
			return 0;
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}
	
	@SuppressWarnings("unchecked")
	public int delete(Set<Integer> keys) {
			
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			statement = con.prepareStatement("select resource_object from resources where id = ?");
			statement.setInt(1, RESOURCE_TREE_ID);
			statement.setMaxRows(1);
			ResultSet resultSet = statement.executeQuery();
			
			if(!resultSet.first())
				return 0;
			
			Blob resourcesBin = resultSet.getBlob("resource_object");
			HashMap<Integer, Resource> resourceMap = (HashMap<Integer, Resource>)BinaryTranslator.binaryToObject(resourcesBin.getBinaryStream());
			statement.close();
			
			for(Integer resourceKey : keys)
				resourceMap.remove(resourceKey);
			
			resourcesBin = new SerialBlob(BinaryTranslator.objectToBinary(resourceMap));
			statement = con.prepareStatement("update resources set resource_object = ? where id = ?");
			statement.setBlob(1, resourcesBin);
			statement.setInt(2, RESOURCE_TREE_ID);
			statement.executeUpdate();
			statement.close();
			
			cachedResourcesMap = resourceMap;
		    return keys.size();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Retry no: " + e.toString());
			return 0;
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}
	
	public boolean deleteAll() {
		
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
			
		try {
			statement = con.prepareStatement("truncate resources");
			statement.executeUpdate();
			statement.close();
			clearCache();
			return true;
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, e.toString());
			return false;
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Resource> get(Set<Integer> keys) {
		
		ArrayList<Resource> result = new ArrayList<Resource>();
		
		if(cachedResourcesMap == null) {
				
			Connection con = DAOFactory.getConnection();
			PreparedStatement statement = null;
			
			try {
				statement = con.prepareStatement("select resource_object from resources where id = ?");
				statement.setInt(1, RESOURCE_TREE_ID);
				statement.setMaxRows(1);
				ResultSet resultSet = statement.executeQuery();
				
				if(resultSet.first()) {
					Blob resourcesBin = resultSet.getBlob("resource_object");
					cachedResourcesMap = (HashMap<Integer, Resource>)BinaryTranslator.binaryToObject(resourcesBin.getBinaryStream());
				}
				else {
					return result;
				}
				
				statement.close();
			}
			catch(Exception e) {
				logger.log(Level.SEVERE, "Retry no: " + e.toString());
				return result;
			}
			finally {
				DAOFactory.freeConnection(con);
			}
		}
		
		//Add nodes
		for(Integer resourceKey : keys) {
			if(cachedResourcesMap.containsKey(resourceKey)) {
				result.add(cachedResourcesMap.get(resourceKey));
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Integer> getKeys() {
		
		if(cachedResourcesMap == null) {
				
			Connection con = DAOFactory.getConnection();
			PreparedStatement statement = null;
			
			try {
				statement = con.prepareStatement("select resource_object from resources where id = ?");
				statement.setInt(1, RESOURCE_TREE_ID);
				statement.setMaxRows(1);
				ResultSet resultSet = statement.executeQuery();
				
				if(resultSet.first()) {
					Blob resourcesBin = resultSet.getBlob("resource_object");
					cachedResourcesMap = (HashMap<Integer, Resource>)BinaryTranslator.binaryToObject(resourcesBin.getBinaryStream());
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
		
		return cachedResourcesMap.keySet();
	}
}

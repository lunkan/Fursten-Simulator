package fursten.simulator.persistent.mysql;

import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.sql.rowset.serial.SerialBlob;

import fursten.simulator.node.Node;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.resource.Resource;
import fursten.utils.BinaryTranslator;

class NodeDAO implements NodeManager {

	private static final Logger logger = Logger.getLogger(NodeDAO.class.getName());
	private static HashMap<Integer, NodeTree> cachedNodeTreeMap = null;
	private static NodeDAO instance = new NodeDAO();
	
	private NodeDAO() {
		cachedNodeTreeMap = new HashMap<Integer, NodeTree>();
	}
	
	public static NodeDAO getInstance() {
        return instance;
    }
	
	public void clearCacheByResourceKey(int resourceKey) {
		cachedNodeTreeMap.remove(resourceKey);
	}
	
	public void clearCache() {
		cachedNodeTreeMap = new HashMap<Integer, NodeTree>();
	}
	
	public int deleteByResourceKey(int resourceKey) {
			
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			statement = con.prepareStatement("delete from nodes where resource_key = ?");
			statement.setInt(1, resourceKey);
			int deleteCount = statement.executeUpdate();
			statement.close();
			
			if(deleteCount > 0) {
				cachedNodeTreeMap.remove(resourceKey);
				return 1;
			}
			else {
				return 0;
			}
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Retry no: " + e.toString());
			return 0;
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}
	
	public int insert(List<Node> nodes) {
		
		HashMap<Integer, List<Node>> groupedNodeMap = groupByResource(nodes);
		Iterator<Integer> it = groupedNodeMap.keySet().iterator();
		
		while(it.hasNext()) {
		
			Integer resourceKey = it.next();
			List<Node> groupedNodes = groupedNodeMap.get(resourceKey);
				
			Connection con = DAOFactory.getConnection();
			PreparedStatement statement = null;
			
			try {
				statement = con.prepareStatement("select node_tree from nodes where resource_key = ?");
				statement.setInt(1, resourceKey);
				statement.setMaxRows(1);
				ResultSet resultSet = statement.executeQuery();
				boolean isNew = true;
				
				NodeTree nodeTree;
				Blob nodesBin;
				
				if(resultSet.first()) {
					isNew = false;
					nodesBin = resultSet.getBlob("node_tree");
					nodeTree = (NodeTree)BinaryTranslator.binaryToObject(nodesBin.getBinaryStream());
				}
				else {
					nodeTree = new NodeTree(31);
				}
				
				statement.close();
				
				//Insert nodes
				for(Node groupedNode : groupedNodes)
					nodeTree.insert(groupedNode);
				
				nodesBin = new SerialBlob(BinaryTranslator.objectToBinary(nodeTree));
				
				if(isNew) {
					statement = con.prepareStatement("insert into nodes(resource_key, node_tree) values (?, ?)");
					statement.setInt(1, resourceKey);
					statement.setBlob(2, nodesBin);
					statement.executeUpdate();
					statement.close();
				}
				else {
					statement = con.prepareStatement("update nodes set node_tree = ? where resource_key = ?");
					statement.setBlob(1, nodesBin);
					statement.setInt(2, resourceKey);
					statement.executeUpdate();
					statement.close();
				}
				
				cachedNodeTreeMap.put(resourceKey, nodeTree);
			}
			catch(Exception e) {
				logger.log(Level.SEVERE, "Retry no: " + e.toString());
				return -1;
			}
			finally {
				DAOFactory.freeConnection(con);
			}
		}
		
		return nodes.size();
	}
	
	public int delete(List<Node> nodes) {
		
		HashMap<Integer, List<Node>> groupedNodeMap = groupByResource(nodes);
		Iterator<Integer> it = groupedNodeMap.keySet().iterator();
		while(it.hasNext()) {
		
			Integer resourceKey = it.next();
			List<Node> groupedNodes = groupedNodeMap.get(resourceKey);
				
			Connection con = DAOFactory.getConnection();
			PreparedStatement statement = null;
			
			try {
				statement = con.prepareStatement("select node_tree from nodes where resource_key = ?");
				statement.setInt(1, resourceKey);
				statement.setMaxRows(1);
				ResultSet resultSet = statement.executeQuery();
				
				if(!resultSet.first()) {
					statement.close();
				}
				else {
					Blob nodesBin = resultSet.getBlob("node_tree");
					NodeTree nodeTree = (NodeTree)BinaryTranslator.binaryToObject(nodesBin.getBinaryStream());
					statement.close();
					
					//Remove nodes
					for(Node groupedNode : groupedNodes)
						nodeTree.delete(groupedNode);
					
					nodesBin = new SerialBlob(BinaryTranslator.objectToBinary(nodeTree));
					statement = con.prepareStatement("update nodes set node_tree = ? where resource_key = ?");
					statement.setBlob(1, nodesBin);
					statement.setInt(2, resourceKey);
					statement.executeUpdate();
					statement.close();
					
					cachedNodeTreeMap.put(resourceKey, nodeTree);
				}
			}
			catch(Exception e) {
				logger.log(Level.SEVERE, "Retry no: " + e.toString());
				return 0;
			}
			finally {
				DAOFactory.freeConnection(con);
			}
		}
		
		return nodes.size();
	}
	
	public boolean deleteAll() {
		
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			statement = con.prepareStatement("truncate nodes");
			statement.executeUpdate();
			statement.close();
			
			clearCache();
			return true;
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Retry no: " + e.toString());
			return false;
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}
	
	public List<Node> get(Rectangle bounds) {
		
		HashSet<Integer> resourceKeys = new HashSet<Integer>();
		
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			statement = con.prepareStatement("select resource_key from nodes");
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
	            int resourceKey = resultSet.getInt("resource_key");
	            resourceKeys.add(resourceKey);
	        }
			
			statement.close();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, e.toString());
			return null;
		}
		finally {
			DAOFactory.freeConnection(con);
		}
		
		return get(bounds, resourceKeys);
	}

	public List<Node> get(Rectangle bounds, Integer resource) {
		
		HashSet<Integer> resourceKeys = new HashSet<Integer>();
		resourceKeys.add(resource);
		return get(bounds, resourceKeys);
		
	}
	
	public List<Node> get(Rectangle bounds, Set<Integer> resources) {
		
		Connection con = null; 
		ArrayList<Node> result = new ArrayList<Node>();
		
		for(Integer resourceKey : resources) {
			
			if(cachedNodeTreeMap.containsKey(resourceKey)) {
				List<Node> nodes = cachedNodeTreeMap.get(resourceKey).get(bounds);
				result.addAll(nodes);
			}
			else {
				
				if(con == null)
					con = DAOFactory.getConnection();
					
				try {
					PreparedStatement statement = con.prepareStatement("select node_tree from nodes where resource_key = ?");
					statement.setInt(1, resourceKey);
					statement.setMaxRows(1);
					ResultSet resultSet = statement.executeQuery();
					
					if(resultSet.first()) {
						Blob nodeBin = resultSet.getBlob("node_tree");
						NodeTree nodeTree = (NodeTree)BinaryTranslator.binaryToObject(nodeBin.getBinaryStream());
						resultSet.close();
						
						//Cache tree (last version!)
						cachedNodeTreeMap.put(resourceKey, nodeTree);
					
						//Add nodes
						List<Node> nodes = nodeTree.get(bounds);
						result.addAll(nodes);
					}
					else {
						resultSet.close();
					}
				}
				catch(Exception e) {
					logger.log(Level.SEVERE, "Could not read Object", e);
					return null;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Group nodes into resource classes - Convenient for insert.
	 * @param nodes
	 * @return
	 */
	private HashMap<Integer, List<Node>> groupByResource(List<Node> nodes) {
		
		HashMap<Integer, List<Node>> groupMap = new HashMap<Integer, List<Node>>();
		for(Node node : nodes) {
			
			if(!groupMap.containsKey(node.getR())) {
				groupMap.put(node.getR(), new ArrayList<Node>());
			}
			
			groupMap.get(node.getR()).add(node);
		}
		
		return groupMap;
	}
}
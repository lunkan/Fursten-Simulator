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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.sql.rowset.serial.SerialBlob;

import fursten.simulator.node.Node;
import fursten.simulator.nodetree.QuadTree;
import fursten.simulator.persistent.NodeManager;
import fursten.utils.BinaryTranslator;

class NodeDAO implements NodeManager {

	private static final Logger logger = Logger.getLogger(NodeDAO.class.getName());
	private static HashMap<Integer, QuadTree> cachedNodeTreeMap = null;
	
	private static NodeDAO instance = new NodeDAO();
	
	private NodeDAO() {
		cachedNodeTreeMap = new HashMap<Integer, QuadTree>();
	}
	
	public static NodeDAO getInstance() {
        return instance;
    }
	
	public void clearCacheByResourceKey(int resourceKey) {
		cachedNodeTreeMap.remove(resourceKey);
	}
	
	public void clearCache() {
		cachedNodeTreeMap = new HashMap<Integer, QuadTree>();
	}
	
	public boolean deleteByResourceKey(int resourceKey) {
		
		Connection con = DAOFactory.getConnection();
				
		int retries = 3;
		while (true) {
			
			try {
				PreparedStatement statement = con.prepareStatement("delete from nodes where resource_key = ?");
				statement.setInt(1, resourceKey);
				statement.executeQuery();
				clearCacheByResourceKey(resourceKey);
				return true;
			}
			catch(Exception e) {
				if (retries >= 3) {
					logger.log(Level.SEVERE, "Retry no: " + retries + " #" + e.toString());
					return false;
				}
			}
			finally {
				//TODO:Rollback?
			}
			
			retries++;
		}
	}
	
	public int insert(List<Node> nodes) {
		
		Connection con = DAOFactory.getConnection();
		HashMap<Integer, List<Node>> groupedNodeMap = groupByResource(nodes);
		
		Iterator<Integer> it = groupedNodeMap.keySet().iterator();
		while(it.hasNext()) {
		
			Integer resourceKey = it.next();
			List<Node> groupedNodes = groupedNodeMap.get(resourceKey);
			
			int retries = 3;
			while (true) {
				
				try {
					PreparedStatement statement = con.prepareStatement("select node_tree from nodes where resource_key = ?");
					statement.setInt(1, resourceKey);
					statement.setMaxRows(1);
					ResultSet result = statement.executeQuery();
					QuadTree nodeTree;
					boolean isNew = true;
					
					if(result.first()) {
						Blob nodeBin = result.getBlob("node_tree");
						nodeTree = (QuadTree)BinaryTranslator.binaryToObject(nodeBin.getBinaryStream());
						isNew = false;
					}
					else {
						nodeTree = new QuadTree(31);
					}
					
					//Insert nodes
					for(Node insertNode : groupedNodes) {
						nodeTree.insert(insertNode);
					}
						
					//Cache tree (last version!)
					cachedNodeTreeMap.put(resourceKey, nodeTree);
					
					//Nodes to bin
					Blob nodeBin = new SerialBlob(BinaryTranslator.objectToBinary(nodeTree));
					
					if(isNew) {
						statement = con.prepareStatement("insert into nodes(resource_key, node_tree) values (?, ?)");
						statement.setInt(1, resourceKey);
						statement.setBlob(2, nodeBin);
						statement.executeQuery();
					}
					else {
						statement = con.prepareStatement("update nodes set node_tree = ? where resource_key = ?");
						statement.setBlob(1, nodeBin);
						statement.setInt(2, resourceKey);
						statement.executeQuery();
					}
					
				    return nodes.size();
				}
				catch(Exception e) {
					if (retries >= 3) {
						logger.log(Level.SEVERE, "Retry no: " + retries + " #" + e.toString());
						return -1;
					}
				}
				finally {
					//TODO:Rollback?
				}
				
				retries++;
			}
		}
		
		return 0;
	}
	
	public boolean delete(List<Node> nodes) {
		
		Connection con = DAOFactory.getConnection();
		HashMap<Integer, List<Node>> groupedNodeMap = groupByResource(nodes);
		
		Iterator<Integer> it = groupedNodeMap.keySet().iterator();
		while(it.hasNext()) {
			
			Integer resourceKey = it.next();
			List<Node> groupedNodes = groupedNodeMap.get(resourceKey);
			
			int retries = 3;
			while (true) {
				
				try {
					
					PreparedStatement statement = con.prepareStatement("select node_tree from nodes where resource_key = ?");
					statement.setInt(1, resourceKey);
					statement.setMaxRows(1);
					ResultSet result = statement.executeQuery();
					QuadTree nodeTree;
					
					if(result.first()) {
						Blob nodeBin = result.getBlob("node_tree");
						nodeTree = (QuadTree)BinaryTranslator.binaryToObject(nodeBin.getBinaryStream());
					
						//Delete nodes
						for(Node deletedNode : groupedNodes) {
							nodeTree.delete(deletedNode);
						}
					
						//Cache tree (last version!)
						cachedNodeTreeMap.put(resourceKey, nodeTree);
					
						//Nodes to bin
						nodeBin = new SerialBlob(BinaryTranslator.objectToBinary(nodeTree));
						statement = con.prepareStatement("update nodes set node_tree = ? where resource_key = ?");
						statement.setBlob(1, nodeBin);
						statement.setInt(2, resourceKey);
						statement.executeQuery();
					}
				}
				catch(Exception e) {
					if (retries >= 3) {
						logger.log(Level.SEVERE, e.toString());
						return false;
					}
				}
				finally {
					//TODO:Rollback?
				}
				
				retries++;
			}
		
		}
		
		return true;
	}
	
	public List<Node> get(Rectangle bounds, Integer resource) {
		
		ArrayList<Integer> resourceKeys = new ArrayList<Integer>();
		resourceKeys.add(resource);
		return get(bounds, resourceKeys);
		
	}
	
	public List<Node> get(Rectangle bounds, List<Integer> resources) {
		
		Connection con = null; 
		ArrayList<Node> result = new ArrayList<Node>();
		
		for(Integer resourceKey : resources) {
			
			if(!cachedNodeTreeMap.containsKey(resourceKey)) {
				
				if(con == null)
					con = DAOFactory.getConnection();
					
				try {
					PreparedStatement statement = con.prepareStatement("select node_tree from nodes where resource_key = ?");
					statement.setInt(1, resourceKey);
					statement.setMaxRows(1);
					ResultSet resultSet = statement.executeQuery();
					QuadTree nodeTree;
					
					if(resultSet.first()) {
						Blob nodeBin = resultSet.getBlob("node_tree");
						nodeTree = (QuadTree)BinaryTranslator.binaryToObject(nodeBin.getBinaryStream());
					
						//Cache tree (last version!)
						cachedNodeTreeMap.put(resourceKey, nodeTree);
					
						//Add nodes
						List<Node> nodes = nodeTree.get(bounds);
						result.addAll(nodes);
					}
				}
				catch(Exception e) {
					logger.log(Level.SEVERE, "Could not read Object", e);
					return null;
				}
			}
			else {
				List<Node> nodes = cachedNodeTreeMap.get(resourceKey).get(bounds);
				result.addAll(nodes);
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
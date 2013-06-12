package fursten.simulator.persistent.mysql;

import java.awt.Rectangle;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialBlob;

import fursten.simulator.node.Node;
import fursten.simulator.persistent.NodeManager;
import fursten.util.BinaryTranslator;

class NodeDAO implements NodeManager, Synchronisable {

	private static final Logger logger = Logger.getLogger(NodeDAO.class.getName());
	private static HashMap<Integer, NodeTree> cachedNodeTreeMap = null;
	private static Set<Integer> changedNodeTrees = null;
	private static NodeDAO instance = null;
	private static Thread persistantSynchroniser = null;
	private static long lastUpdate = 0;
	
	private NodeDAO() {
		cachedNodeTreeMap = new HashMap<Integer, NodeTree>();
		changedNodeTrees = new HashSet<Integer>();
		
		pullPersistent();
		persistantSynchroniser = new Thread(new PersistantSynchroniser(this));//new Thread(new PersistantSynchroniser(this));
		persistantSynchroniser.start();
	}
	
	public static NodeDAO getInstance() {
		
		if(instance == null)
			instance = new NodeDAO();
		
        return instance;
    }
	
	public boolean hasChanged() {
		return (changedNodeTrees.size() > 0);
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public synchronized void clean() {
		
		persistantSynchroniser.interrupt();
		if(changedNodeTrees.size() > 0)
			pushPersistent();
		
		pullPersistent();
		persistantSynchroniser = new Thread(new PersistantSynchroniser(this));
		persistantSynchroniser.start();
	}
	
	public void close() {
		
		persistantSynchroniser.interrupt();
		if(changedNodeTrees.size() > 0)
			pushPersistent();
	}
	
	/*public void clearCacheByResourceKey(int resourceKey) {
		cachedNodeTreeMap.remove(resourceKey);
	}
	
	public void clearCache() {
		cachedNodeTreeMap = new HashMap<Integer, NodeTree>();
	}*/
	
	/*public synchronized int deleteByResourceKey(int resourceKey) {
		lastUpdate = System.currentTimeMillis();
		cachedNodeTreeMap.remove(resourceKey);
		changedNodeTrees.add(resourceKey);
		return 1;
	}*/
	
	/*public int deleteByResourceKey(int resourceKey) {
			
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			statement = con.prepareStatement("delete from nodes where resource_key = ?");
			statement.setInt(1, resourceKey);
			int deleteCount = statement.executeUpdate();
			statement.close();
			
			if(deleteCount > 0) {
				cachedNodeTreeMap.remove(resourceKey);
				updatedNodeTrees.remove(resourceKey);
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
	}*/
	
	public synchronized int insert(List<Node> nodes) {
		
		lastUpdate = System.currentTimeMillis();
		int resourceKey = 0;
		NodeTree nodeTree = null;
		
		Collections.sort(nodes, new NodeSort());
		for(Node node : nodes) {
			
			if(node.getR() != resourceKey) {
				resourceKey = node.getR();
				nodeTree = cachedNodeTreeMap.get(resourceKey);
				if(nodeTree == null) {
					nodeTree = new NodeTree(31);
					cachedNodeTreeMap.put(resourceKey, nodeTree);
				}
				
				changedNodeTrees.add(resourceKey);
			}
			
			cachedNodeTreeMap.get(resourceKey).insert(node);
		}
		
		return nodes.size();
	}
		
	/*
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
	}*/
	
	public synchronized List<Node> substract(List<Node> nodes) {
	
		lastUpdate = System.currentTimeMillis();
		int resourceKey = 0;
		NodeTree nodeTree = null;
		List<Node> deletedNodes = new ArrayList<Node>();
		
		Collections.sort(nodes, new NodeSort());
		for(Node node : nodes) {
			
			if(node.getR() != resourceKey) {
				nodeTree = cachedNodeTreeMap.get(node.getR());
				if(nodeTree == null) 
					continue;
				
				resourceKey = node.getR();
				changedNodeTrees.add(resourceKey);
			}
			
			if(cachedNodeTreeMap.get(resourceKey).substract(node))
				deletedNodes.add(node);
		}
		
		return deletedNodes;
	}
	
	/*public synchronized int delete(List<Node> nodes) {
		
		lastUpdate = System.currentTimeMillis();
		int resourceKey = 0;
		NodeTree nodeTree = null;
		
		Collections.sort(nodes, new NodeSort());
		for(Node node : nodes) {
			
			if(node.getR() != resourceKey) {
				nodeTree = cachedNodeTreeMap.get(node.getR());
				if(nodeTree == null) 
					continue;
				
				resourceKey = node.getR();
				changedNodeTrees.add(resourceKey);
			}
			
			cachedNodeTreeMap.get(resourceKey).substract(node);
		}
		
		return nodes.size();
	}*/
	
	/*public int delete(List<Node> nodes) {
		
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
	}*/
	
	public synchronized boolean deleteAll() {
		lastUpdate = System.currentTimeMillis();
		changedNodeTrees.addAll(cachedNodeTreeMap.keySet());
		cachedNodeTreeMap.clear();
		clean();
		return true;
	}
	
	/*public boolean deleteAll() {
		
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
	}*/
	
	public List<Node> get(Rectangle bounds) {
		return get(bounds, cachedNodeTreeMap.keySet());
	}
	
	/*public List<Node> get(Rectangle bounds) {
		
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
	}*/

	public List<Node> get(Rectangle bounds, Integer resource) {
		HashSet<Integer> resourceKeys = new HashSet<Integer>();
		resourceKeys.add(resource);
		return get(bounds, resourceKeys);
	}
	
	public List<Node> get(Rectangle bounds, Set<Integer> resources) {
		
		ArrayList<Node> result = new ArrayList<Node>();
		for(Integer resourceKey : resources) {
			
			if(cachedNodeTreeMap.containsKey(resourceKey)) {
				List<Node> nodes = cachedNodeTreeMap.get(resourceKey).get(bounds);
				result.addAll(nodes);
			}
		}
		
		return result;
	}
	
	/*public List<Node> get(Rectangle bounds, Set<Integer> resources) {
		
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
				finally {
					DAOFactory.freeConnection(con);
				}
			}
		}
		
		return result;
	}*/
	
	
	public boolean containsAll(List<Node> nodes) {
		
		for(Node node : nodes) {
			if(!contains(node))
				return false;
		}
			
		return true;
	}
	
	public boolean contains(Node node) {
		
		List<Node> nodes = get(new Rectangle(node.getX(), node.getY(), 1, 1), node.getR());
		if(nodes.size() != 0)
			return true;
			
		return false;
	}
	
	
	/**
	 * Group nodes into resource classes - Convenient for insert.
	 * @param nodes
	 * @return
	 */
	/*private HashMap<Integer, List<Node>> groupByResource(List<Node> nodes) {
		
		HashMap<Integer, List<Node>> groupMap = new HashMap<Integer, List<Node>>();
		for(Node node : nodes) {
			
			if(!groupMap.containsKey(node.getR())) {
				groupMap.put(node.getR(), new ArrayList<Node>());
			}
			
			groupMap.get(node.getR()).add(node);
		}
		
		return groupMap;
	}*/
	
	public synchronized void pullPersistent() {
		
		lastUpdate = System.currentTimeMillis();
		Connection con = DAOFactory.getConnection();
		
		try {
			
			cachedNodeTreeMap.clear();
			changedNodeTrees.clear();
			
			PreparedStatement statement = con.prepareStatement("select * from nodes");
			ResultSet resultSet = statement.executeQuery();
			
			while(resultSet.next()) {
				int resourceKey =  resultSet.getInt("resource_key");
				Blob nodeBin = resultSet.getBlob("node_tree");
				NodeTree nodeTree = (NodeTree)BinaryTranslator.binaryToObject(nodeBin.getBinaryStream());
				cachedNodeTreeMap.put(resourceKey, nodeTree);
			}
			
			resultSet.close();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Could not syncLocal", e);
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}
	
	public synchronized void pushPersistent() {
		
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			
			Iterator<Integer> it = changedNodeTrees.iterator();
			while(it.hasNext()) {
			
				Integer resourceKey = it.next();
				NodeTree updatedTree = cachedNodeTreeMap.get(resourceKey);
				
				//Delete if tree is removed or new/update
				if(updatedTree == null) {
					statement = con.prepareStatement("delete from nodes where resource_key = ?");
					statement.setInt(1, resourceKey);
					statement.executeUpdate();
					statement.close();
				}
				else {
					
					Blob nodesBin = new SerialBlob(BinaryTranslator.objectToBinary(updatedTree));
					
					statement = con.prepareStatement("select node_tree from nodes where resource_key = ?");
					statement.setInt(1, resourceKey);
					statement.setMaxRows(1);
					ResultSet resultSet = statement.executeQuery();
					
					//create new or update
					if(resultSet.first()) {
						statement = con.prepareStatement("update nodes set node_tree = ? where resource_key = ?");
						statement.setBlob(1, nodesBin);
						statement.setInt(2, resourceKey);
						statement.executeUpdate();
						statement.close();
					}
					else {
						statement = con.prepareStatement("insert into nodes(resource_key, node_tree) values (?, ?)");
						statement.setInt(1, resourceKey);
						statement.setBlob(2, nodesBin);
						statement.executeUpdate();
						statement.close();
					}
				}
				
				//remove changed flag
				it.remove();
				
			}
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Retry no: " + e.toString());
		}
		finally {
			DAOFactory.freeConnection(con);
			logger.log(Level.INFO, "pushed nodes to server");
		}
	}
	
	private static class NodeSort implements Comparator<Node>{
		 
	    public int compare(Node o1, Node o2) {
	        return (o1.getR() > o2.getR() ? -1 : (o1.getR() == o2.getR() ? 0 : 1));
	    }
	}
	
	/*class NodeMemeCache {
		
		private HashMap<Integer, NodeTree> cachedNodeTreeMap = null;
		private Set<Integer> changedNodeTrees = null;
		
		public NodeMemeCache() {
			cachedNodeTreeMap = new HashMap<Integer, NodeTree>();
			changedNodeTrees = new HashSet<Integer>();
		}
		
		public void addNode() {
			
		}
		
		public void deleteNode() {
			
		}
		
	}*/
	
	/*private static class PersistantSynchroniser implements Runnable {
		
		private NodeDAO parent;
		
		public PersistantSynchroniser(NodeDAO parent) {
			this.parent = parent;
		}
		
		public void run() {
			
			try {
				
				while(!Thread.interrupted()) {
					
					//Check every 20 seconds for changed nodeTRees to push to database
					Thread.sleep(20000);
					if(parent.hasChanged()) {
						if(parent.getLastUpdate() < System.currentTimeMillis()) {
							parent.pushPersistent();
						}
					}
				}
	        }
			catch (InterruptedException e) {
	            //("I wasn't done!");
	        }
		}
	}*/
}
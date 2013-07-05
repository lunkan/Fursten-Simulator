package fursten.simulator.persistent.mysql;

import java.awt.Rectangle;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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
import fursten.simulator.persistent.AutoSaveManager;
import fursten.simulator.persistent.Persistable;
import fursten.util.BinaryTranslator;

class NodeDAO implements NodeManager {

	private static final Logger logger = Logger.getLogger(NodeDAO.class.getName());
	private static HashMap<Integer, NodeTree> cachedNodeTreeMap = null;
	private static Set<Integer> changedNodeTrees = null;
	private static boolean changed;
	
	private static NodeDAO instance = null;
	
	private NodeDAO() {
		cachedNodeTreeMap = new HashMap<Integer, NodeTree>();
		changedNodeTrees = new HashSet<Integer>();
	}
	
	public static NodeDAO getInstance() {
		
		if(instance == null)
			instance = new NodeDAO();
		
        return instance;
    }
	
	public boolean hasChanged() {
		return changed;//(changedNodeTrees.size() > 0);
	}
	
	public synchronized void clean() {
		
		if(hasChanged())
			pushPersistent();
		
		pullPersistent();
	}
	
	public synchronized boolean reset() {
		instance = null;
		changed = true;
		return true;
	}
	
	public synchronized int add(Node... nodes) {
		return addAll(Arrays.asList(nodes));
	}
	
	public synchronized int addAll(List<Node> nodes) {
		
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
				
				changed = true;
				changedNodeTrees.add(resourceKey);
			}
			
			cachedNodeTreeMap.get(resourceKey).insert(node);
		}
		
		return nodes.size();
	}
	
	public synchronized List<Node> substract(Node... nodes) {
		return substractAll(Arrays.asList(nodes));
	}
	
	public synchronized List<Node> substractAll(List<Node> nodes) {
	
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
				changed = true;
				changedNodeTrees.add(resourceKey);
			}
			
			if(cachedNodeTreeMap.get(resourceKey).substract(node))
				deletedNodes.add(node);
		}
		
		return deletedNodes;
	}
	
	public synchronized boolean clear() {
		changedNodeTrees.addAll(cachedNodeTreeMap.keySet());
		cachedNodeTreeMap.clear();
		changed = true;
		return true;
	}
	
	public List<Node> get(Rectangle bounds) {
		return get(bounds, cachedNodeTreeMap.keySet());
	}

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
	
	public synchronized void pullPersistent() {
		
		Connection con = DAOFactory.getConnection();
		
		try {
			
			cachedNodeTreeMap.clear();
			changedNodeTrees.clear();
			changed = false;
			
			PreparedStatement statement = con.prepareStatement("select * from nodes");
			ResultSet resultSet = statement.executeQuery();
			
			int pullTreeCount = 0;
			while(resultSet.next()) {
				int resourceKey =  resultSet.getInt("resource_key");
				Blob nodeBin = resultSet.getBlob("node_tree");
				NodeTree nodeTree = (NodeTree)BinaryTranslator.binaryToObject(nodeBin.getBinaryStream());
				cachedNodeTreeMap.put(resourceKey, nodeTree);
				pullTreeCount ++;
			}
			
			resultSet.close();
			logger.log(Level.INFO, "Pulled " + pullTreeCount + " node trees from database.");
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Could not syncLocal", e);
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}
	
	public synchronized void pushPersistent() {
		
		//changedNodeTrees.size() == 0)
		if(!changed)
			return;
		
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
			
			changed = false;
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
}
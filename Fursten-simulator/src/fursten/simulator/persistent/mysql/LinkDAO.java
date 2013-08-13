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

import fursten.simulator.link.Link;
import fursten.simulator.node.Node;
import fursten.simulator.node.Nodes;
import fursten.simulator.persistent.LinkManager;
import fursten.simulator.persistent.AutoSaveManager;
import fursten.simulator.persistent.Persistable;
import fursten.util.BinaryTranslator;

public class LinkDAO implements LinkManager {

	private static final Logger logger = Logger.getLogger(LinkDAO.class.getName());
	private static LinkDAO instance = null;
	
	private HashMap<Node, List<Link>> linkMap = null;
	private boolean changed;
	
	private LinkDAO() {
		linkMap = new HashMap<Node, List<Link>>();
	}
	 
	public static LinkDAO getInstance() {
		
		if(instance == null)
			instance = new LinkDAO();
		
        return instance;
    }
	
	public boolean hasChanged() {
		return changed;
	}
	
	public synchronized void clean() {
		
		if(hasChanged())
			pushPersistent();
		
		pullPersistent();
	}

	public synchronized boolean clear() {
		linkMap.clear();
		changed = true;
		return true;
	}
	
	public synchronized boolean reset() {
		changed = true;
		instance = null;
		return true;
	}
	
	public synchronized int add(Link... links) {
		return addAll(Arrays.asList(links));
	}
	
	public synchronized int addAll(List<Link> links) {
		
		for(Link link : links) {
			
			Node parentNode = link.getParentNode();
			if(!linkMap.containsKey(parentNode))
				linkMap.put(parentNode, new ArrayList<Link>());
			
			linkMap.get(parentNode).add(link);
		}
		
		changed = true;
		return links.size();
	}

	public synchronized List<Link> remove(Link... links) {
		return removeAll(Arrays.asList(links));
	}
	
	public synchronized List<Link> removeAll(List<Link> links) {
		
		ArrayList<Link> removedLinks = new ArrayList<Link>();
		
		Iterator<Link> it = links.iterator();
		while(it.hasNext()) {
			
			Link removeLink = it.next();
			
			List<Link> sourceLinks = linkMap.get(removeLink.getParentNode());
			if(sourceLinks != null) {
				
				for(Link sourceLink : sourceLinks) {
					
					if(sourceLink.equals(removeLink)) {
						removedLinks.add(removeLink);
						linkMap.get(removeLink.getParentNode()).remove(removeLink);
						
						if(linkMap.get(removeLink.getParentNode()).size() == 0)
							linkMap.remove(removeLink.getParentNode());
						
						break;
					}
				}
			}	
		}
		
		changed = true;
		return removedLinks;
	}

	public List<Link> get(Node... nodes) {
		return getAll(Arrays.asList(nodes));
	}
	
	public List<Link> getAll(List<Node> nodes) {
		List<Link> links = new ArrayList<Link>();
		for(Node node : nodes) {
			List<Link> nodeLink = linkMap.get(node);
			if(nodeLink != null)
				links.addAll(linkMap.get(node));
		}
		
		return links;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void pullPersistent() {
		
		Connection con = DAOFactory.getConnection();
		
		try {
			
			linkMap.clear();
			
			PreparedStatement statement = con.prepareStatement("select * from links");
			ResultSet resultSet = statement.executeQuery();
			
			while(resultSet.next()) {
				String hashString = resultSet.getString("node_hash");
				Node node = Nodes.toNode(hashString);
				Blob linksBin = resultSet.getBlob("links");
				List<Link> links = (List<Link>)BinaryTranslator.binaryToObject(linksBin.getBinaryStream());
				linkMap.put(node, links);
			}
			
			resultSet.close();
			changed = false;
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
			
			//Lazy Delete all
			//ToDo:Make more efficient
			statement = con.prepareStatement("delete from links");
			statement.executeUpdate();
			statement.close();
			
			Iterator<Node> it = linkMap.keySet().iterator();
			while(it.hasNext()) {
			
				Node node = it.next();
				String nodeHash = Nodes.toHashString(node);
				List<Link> links = linkMap.get(node);
				
				//Delete if tree is removed or new/update
				/*if(links == null) {
					statement = con.prepareStatement("delete from links where node_hash = ?");
					statement.setString(1, nodeHash);
					statement.executeUpdate();
					statement.close();
				}
				else {*/
					
					Blob linksBin = new SerialBlob(BinaryTranslator.objectToBinary((Serializable)links));
					
					/*statement = con.prepareStatement("select links from links where node_hash = ?");
					statement.setString(1, nodeHash);
					statement.setMaxRows(1);
					ResultSet resultSet = statement.executeQuery();
					
					//create new or update
					if(resultSet.first()) {
						statement = con.prepareStatement("update links set links = ? where node_hash = ?");
						statement.setBlob(1, linksBin);
						statement.setString(2, nodeHash);
						statement.executeUpdate();
						statement.close();
					}
					else {*/
						statement = con.prepareStatement("insert into links(node_hash, links) values (?, ?)");
						statement.setString(1, nodeHash);
						statement.setBlob(2, linksBin);
						statement.executeUpdate();
						statement.close();
					//}
				//}
			}
			
			changed = false;
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Retry no: " + e.toString());
		}
		finally {
			DAOFactory.freeConnection(con);
			logger.log(Level.INFO, "pushed links to server");
		}
	}
}

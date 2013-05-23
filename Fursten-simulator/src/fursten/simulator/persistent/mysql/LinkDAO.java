package fursten.simulator.persistent.mysql;

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

import javax.sql.rowset.serial.SerialBlob;

import fursten.simulator.link.Link;
import fursten.simulator.node.Node;
import fursten.simulator.node.Nodes;
import fursten.simulator.persistent.LinkManager;
import fursten.util.BinaryTranslator;

public class LinkDAO implements LinkManager, Synchronisable {

	private static final Logger logger = Logger.getLogger(LinkDAO.class.getName());
	private static LinkDAO instance = null;
	
	private static HashMap<Node, List<Link>> cachedLinkMap = null;
	private static Set<Node> changedLinks = null;
	
	private static Thread persistantSynchroniser = null;
	private static long lastUpdate = 0;
	
	private LinkDAO() {
		cachedLinkMap = new HashMap<Node, List<Link>>();
		changedLinks = new HashSet<Node>();
		
		pullPersistent();
		persistantSynchroniser = new Thread(new PersistantSynchroniser(this));//new Thread(new PersistantSynchroniser(this));
		persistantSynchroniser.start();
	}
	 
	public static LinkDAO getInstance() {
		
		if(instance == null)
			instance = new LinkDAO();
		
        return instance;
    }
	
	public boolean hasChanged() {
		return (changedLinks.size() > 0);
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public synchronized void clean() {
		
		persistantSynchroniser.interrupt();
		if(cachedLinkMap.size() > 0)
			pushPersistent();
		
		pullPersistent();
		persistantSynchroniser.start();
	}
	
	public void close() {
		
		persistantSynchroniser.interrupt();
		if(cachedLinkMap.size() > 0)
			pushPersistent();
	}

	@Override
	public int insert(List<Link> links) {
		
		lastUpdate = System.currentTimeMillis();
		
		for(Link link : links) {
			
			Node parentNode = link.getParentNode();
			if(!cachedLinkMap.containsKey(parentNode))
				cachedLinkMap.put(parentNode, new ArrayList<Link>());
			
			cachedLinkMap.get(parentNode).add(link);
			changedLinks.add(parentNode);
		}
		
		return links.size();
	}

	@Override
	public int delete(List<Link> links) {
		
		lastUpdate = System.currentTimeMillis();
		
		for(Link link : links) {
			List<Link> sourceLinks = cachedLinkMap.get(link.getParentNode());
			if(sourceLinks.remove(link))
				changedLinks.add(link.getParentNode());
		}
		
		return 0;
	}

	@Override
	public boolean deleteAll() {
		lastUpdate = System.currentTimeMillis();
		changedLinks.addAll(cachedLinkMap.keySet());
		cachedLinkMap.clear();
		clean();
		return true;
	}

	@Override
	public List<Link> getByNode(Node node) {
		return cachedLinkMap.get(node);
	}

	@Override
	public List<Link> getAllByNode(Node node) {
		
		List<Link> links = new ArrayList<Link>();
		List<Link> parentLinks = cachedLinkMap.get(node);
		if(parentLinks != null) {
			
			for(Link link : parentLinks)
				links.addAll(getAllByNode(link.getParentNode()));
		}
		
		return links;
	}

	@Override
	public List<Link> get(List<Node> nodes) {
		
		List<Link> links = new ArrayList<Link>();
		for(Node node : nodes) {
			links.addAll(cachedLinkMap.get(node));
		}
		
		return links;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void pullPersistent() {
		
		lastUpdate = System.currentTimeMillis();
		Connection con = DAOFactory.getConnection();
		
		try {
			
			cachedLinkMap.clear();
			changedLinks.clear();
			
			PreparedStatement statement = con.prepareStatement("select * from links");
			ResultSet resultSet = statement.executeQuery();
			
			while(resultSet.next()) {
				String hashString = resultSet.getString("node_hash");
				Node node = Nodes.toNode(hashString);
				Blob linksBin = resultSet.getBlob("links");
				List<Link> links = (List<Link>)BinaryTranslator.binaryToObject(linksBin.getBinaryStream());
				cachedLinkMap.put(node, links);
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
			
			Iterator<Node> it = changedLinks.iterator();
			while(it.hasNext()) {
			
				Node node = it.next();
				String nodeHash = Nodes.toHashString(node);
				List<Link> links = cachedLinkMap.get(node);
				
				//Delete if tree is removed or new/update
				if(links == null) {
					statement = con.prepareStatement("delete from links where node_hash = ?");
					statement.setString(1, nodeHash);
					statement.executeUpdate();
					statement.close();
				}
				else {
					
					Blob linksBin = new SerialBlob(BinaryTranslator.objectToBinary((Serializable)links));
					
					statement = con.prepareStatement("select links from links where node_hash = ?");
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
					else {
						statement = con.prepareStatement("insert into links(node_hash, links) values (?, ?)");
						statement.setString(1, nodeHash);
						statement.setBlob(2, linksBin);
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
}

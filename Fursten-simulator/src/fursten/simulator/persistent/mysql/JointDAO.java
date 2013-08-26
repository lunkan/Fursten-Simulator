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

import fursten.simulator.joint.Joint;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodePoint;
import fursten.simulator.node.Nodes;
import fursten.simulator.persistent.JointManager;
import fursten.simulator.persistent.AutoSaveManager;
import fursten.simulator.persistent.Persistable;
import fursten.simulator.resource.Resource;
import fursten.util.BinaryTranslator;

public class JointDAO implements JointManager {

	private static final Logger logger = Logger.getLogger(JointDAO.class.getName());
	private static final String LINKS_ID = "1";
	private static JointDAO instance = null;
	
	private HashMap<NodePoint, Joint> jointMap = null;
	private boolean changed;
	
	private JointDAO() {
		jointMap = new HashMap<NodePoint, Joint>();
	}
	 
	public static JointDAO getInstance() {
		
		if(instance == null)
			instance = new JointDAO();
		
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
		jointMap.clear();
		changed = true;
		return true;
	}
	
	public synchronized void put(Joint... joints) {
		putAll(Arrays.asList(joints));
	}
	
	public synchronized void putAll(List<Joint> joints) {
		
		for(Joint joint : joints)
			jointMap.put(joint.getNodePoint(), joint);
				
		changed = true;
	}
	
	public synchronized List<Joint> remove(NodePoint... nodePoints) {
		return removeAll(Arrays.asList(nodePoints));
	}
	
	public synchronized List<Joint> removeAll(List<NodePoint> nodePoints) {
		
		ArrayList<Joint> removedJoints = new ArrayList<Joint>();
		for(NodePoint nodePoint : nodePoints)
			removedJoints.add(jointMap.remove(nodePoint));
		
		return removedJoints;
	}
	
	public Joint get(NodePoint nodePoint) {
		return jointMap.get(nodePoint);
	}
	
	public List<Joint> getAll(List<NodePoint> nodePoints) {
		
		ArrayList<Joint> joints = new ArrayList<Joint>();
		for(NodePoint nodePoint : nodePoints)
			joints.add(jointMap.get(nodePoint));
		
		return joints;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public synchronized void pullPersistent() {
		
		Connection con = DAOFactory.getConnection();
			
		try {
			PreparedStatement statement = con.prepareStatement("select links from links where node_hash = ?");
			statement.setString(1, LINKS_ID);
			statement.setMaxRows(1);
			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.first()) {
				Blob resourcesBin = resultSet.getBlob("links");
				jointMap = (HashMap<NodePoint, Joint>)BinaryTranslator.binaryToObject(resourcesBin.getBinaryStream());
				logger.log(Level.INFO, "Pulled " + jointMap.size() + " joints from database.");
			}
			else {
				logger.log(Level.INFO, "Pulled joints from database but no joints was found.");
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
		
		Connection con = DAOFactory.getConnection();
		
		try {
			
			PreparedStatement statement = con.prepareStatement("select links from links where node_hash = ?");
			statement.setString(1, LINKS_ID);
			statement.setMaxRows(1);
			ResultSet resultSet = statement.executeQuery();
			
			boolean isNew = true;
			if(resultSet.first())
				isNew = false;
			
			statement.close();
			
			Blob jointsBin;
			jointsBin = new SerialBlob(BinaryTranslator.objectToBinary((Serializable)jointMap.clone()));
			
			if(isNew) {
				statement = con.prepareStatement("insert into links(node_hash, links) values (?, ?)");
				statement.setString(1, LINKS_ID);
				statement.setBlob(2, jointsBin);
				statement.executeUpdate();
				statement.close();
			}
			else {
				statement = con.prepareStatement("update links set links = ? where node_hash = ?");
				statement.setBlob(1, jointsBin);
				statement.setString(2, LINKS_ID);
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

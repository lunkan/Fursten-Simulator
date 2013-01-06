package fursten.simulator.persistent.mysql;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialBlob;

import fursten.simulator.instance.Instance;
import fursten.simulator.node.Node;
import fursten.simulator.persistent.SessionManager;
import fursten.util.BinaryTranslator;

class SessionDAO implements SessionManager {

	private static final Logger logger = Logger.getLogger(SessionDAO.class.getName());
	private static final int CURRENT_SESSION_ID = 1;
	private static Instance cachedSession;
	
	private static SessionDAO instance = new SessionDAO();
	
	private SessionDAO() {
		
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			statement = con.prepareStatement("select session_object from sessions where id = ?");
			statement.setInt(1, CURRENT_SESSION_ID);
			statement.setMaxRows(1);
			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.first()) {
				Blob sessionBin = resultSet.getBlob("session_object");
				cachedSession = (Instance)BinaryTranslator.binaryToObject(sessionBin.getBinaryStream());
			}
			else {
				Instance session = new Instance();
				setActive(session);
			}
			
			statement.close();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Could not read Object", e);
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}
	
	public static SessionDAO getInstance() {
        return instance;
    }
	
	@Override
	public boolean clear() {
		
		Instance session = new Instance();
		setActive(session);
		return false;
	}

	@Override
	public Instance getActive() {
		
		if(cachedSession != null) {
			return cachedSession;
		}
		else {
			logger.log(Level.SEVERE, "Session is null but null is an invalid value");
			return null;
		}
	}

	@Override
	public int setActive(Instance session) {
		
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			statement = con.prepareStatement("select session_object from sessions where id = ?");
			statement.setInt(1, CURRENT_SESSION_ID);
			statement.setMaxRows(1);
			ResultSet result = statement.executeQuery();
			boolean isNew = true;
			
			if(result.first())
				isNew = false;
				
			cachedSession = session;
			Blob sessionBin = new SerialBlob(BinaryTranslator.objectToBinary(session));
			statement.close();
			
			if(isNew) {
				statement = con.prepareStatement("insert into sessions(id, session_object) values (?, ?)");
				statement.setInt(1, CURRENT_SESSION_ID);
				statement.setBlob(2, sessionBin);
				statement.executeUpdate();
				statement.close();
			}
			else {
				statement = con.prepareStatement("update sessions set session_object = ? where id = ?");
				statement.setBlob(1, sessionBin);
				statement.setInt(2, CURRENT_SESSION_ID);
				statement.executeUpdate();
				statement.close();
			}
			
		    return 1;
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Retry no: " + e.toString());
			return 0;
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}
	
	@Override
	public List<Instance> getHistory() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean deleteAll() {
		
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			statement = con.prepareStatement("truncate sessions");
			statement.executeUpdate();
			statement.close();
			clear();
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
}

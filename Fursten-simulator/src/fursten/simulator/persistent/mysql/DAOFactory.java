package fursten.simulator.persistent.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.Settings;
import fursten.simulator.Settings.DatabaseSettings;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.LinkManager;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.WorldManager;
import fursten.util.persistent.ConnectionPool;

public class DAOFactory extends DAOManager {

	private static final Logger logger = Logger.getLogger(DAOFactory.class.getName());
	private static ConnectionPool connectionPool;
	
	private static void createConnection() {
		
		DatabaseSettings dbSettings = Settings.getInstance().getDatabaseSettings();
		
		try {
			connectionPool = new ConnectionPool(dbSettings.getDriver(), dbSettings.getUrl(), dbSettings.getUser(), dbSettings.getPassword(), 2, 10, false);
		}
		catch (SQLException e) {
			logger.log(Level.SEVERE, "Could not create connection from pool " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		
		if(connectionPool == null)
			createConnection();
		
		try {
			return connectionPool.getConnection();
		}
		catch (SQLException e) {
			logger.log(Level.SEVERE, "Could not create connection from pool " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Could not create connection from pool " + e.getMessage());
			e.printStackTrace();
			return null;
		}
  	}
		
	public static void freeConnection(Connection connection) {
		
		if(connectionPool != null)
			connectionPool.free(connection);
	}
	
	public NodeManager getNodeManager() {
		return NodeDAO.getInstance();
	}
	
	public ResourceManager getResourceManager() {
		return ResourceDAO.getInstance();
	}
	
	public WorldManager getWorldManager() {
		return WorldDAO.getInstance();
	}
	
	public LinkManager getLinkManager() {
		return LinkDAO.getInstance();
	}
}

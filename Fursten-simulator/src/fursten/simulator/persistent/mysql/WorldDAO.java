package fursten.simulator.persistent.mysql;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialBlob;

import fursten.simulator.world.World;
import fursten.simulator.persistent.WorldManager;
import fursten.util.BinaryTranslator;

class WorldDAO implements WorldManager {

	private static final Logger logger = Logger.getLogger(WorldDAO.class.getName());
	private static final int CURRENT_SESSION_ID = 1;
	private static World world;
	
	private static WorldDAO worldDAO = null;// = new WorldDAO();
	
	private WorldDAO() {
		
		/*
		
		
		Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		
		try {
			statement = con.prepareStatement("select session_object from sessions where id = ?");
			statement.setInt(1, CURRENT_SESSION_ID);
			statement.setMaxRows(1);
			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.first()) {
				Blob worldBin = resultSet.getBlob("session_object");
				world = (World)BinaryTranslator.binaryToObject(worldBin.getBinaryStream());
			}
			
			statement.close();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Could not read Object", e);
		}
		finally {
			DAOFactory.freeConnection(con);
		}*/
		
		
	}
	
	public static WorldDAO getInstance() {
		
		if(worldDAO == null)
			worldDAO = new WorldDAO();
		
        return worldDAO;
    }
	
	@Override
	public boolean clear() {
		
		World world = new World();
		setActive(world);
		return false;
	}

	@Override
	public World getActive() {
		
		return world;
		
		/*if(world != null) {
			return world;
		}
		else {
			logger.log(Level.SEVERE, "Session is null but null is an invalid value");
			return null;
		}*/
	}

	@Override
	public int setActive(World world) {
		
		this.world = world;
		return 1;
		
		/*Connection con = DAOFactory.getConnection();
		PreparedStatement statement = null;
		
		try {
			statement = con.prepareStatement("select session_object from sessions where id = ?");
			statement.setInt(1, CURRENT_SESSION_ID);
			statement.setMaxRows(1);
			ResultSet result = statement.executeQuery();
			boolean isNew = true;
			
			if(result.first())
				isNew = false;
				
			this.world = world;
			Blob worldBin = new SerialBlob(BinaryTranslator.objectToBinary(world));
			statement.close();
			
			if(isNew) {
				statement = con.prepareStatement("insert into sessions(id, session_object) values (?, ?)");
				statement.setInt(1, CURRENT_SESSION_ID);
				statement.setBlob(2, worldBin);
				statement.executeUpdate();
				statement.close();
			}
			else {
				statement = con.prepareStatement("update sessions set session_object = ? where id = ?");
				statement.setBlob(1, worldBin);
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
		}*/
	}
	
	@Override
	public List<World> getHistory() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean deleteAll() {
		
		/*Connection con = DAOFactory.getConnection();
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
		}*/
		
		world = null;
		return true;
	}
}

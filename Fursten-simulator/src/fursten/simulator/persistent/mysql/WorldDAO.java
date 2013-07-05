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
import fursten.simulator.persistent.AutoSaveManager;
import fursten.simulator.persistent.Persistable;
import fursten.simulator.persistent.WorldManager;
import fursten.util.BinaryTranslator;

class WorldDAO implements WorldManager, Persistable {

	private static final Logger logger = Logger.getLogger(WorldDAO.class.getName());
	private static final int CURRENT_SESSION_ID = 1;
	
	private static WorldDAO instance = null;// = new WorldDAO();
	
	private World world;
	private boolean changed;
	
	private WorldDAO() {
		
		//Add default world
		world = new World();
		world.setName("Untitled");
		world.setWidth(10000);
		world.setHeight(10000);
	}
	
	public static WorldDAO getInstance() {
		
		if(instance == null)
			instance = new WorldDAO();
		
        return instance;
    }
	
	@Override
	public synchronized boolean clear() {
		World world = new World();
		set(world);
		changed = true;
		return false;
	}

	@Override
	public synchronized boolean reset() {
		instance = null;
		return true;
	}
	
	@Override
	public World get() {
		return world;
	}

	@Override
	public synchronized int set(World world) {
		this.world = world;
		changed = true;
		return 1;
	}
	
	public synchronized boolean deleteAll() {
		changed = true;
		world = null;
		return true;
	}

	@Override
	public boolean hasChanged() {
		return changed;
	}

	@Override
	public synchronized void clean() {
		
		if(changed)
			pushPersistent();
		
		pullPersistent();
		
	}

	@Override
	public synchronized void pullPersistent() {

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
			
			changed = false;
			statement.close();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Could not read Object", e);
		}
		finally {
			DAOFactory.freeConnection(con);
		}
	}

	@Override
	public synchronized void pushPersistent() {
		
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

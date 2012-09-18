package fursten.simulator.persistent;

public abstract class DAOManager {

	public abstract NodeManager getNodeManager();
	public abstract ResourceManager getResourceManager();
	public abstract SessionManager getSessionManager();
	
	public static DAOManager get() {
		return new fursten.simulator.persistent.mysql.DAOFactory();
	}
	
	public static PersistentTestHelper getTestHelper() {
		return new fursten.simulator.persistent.mysql.TestHelper();
	}
}
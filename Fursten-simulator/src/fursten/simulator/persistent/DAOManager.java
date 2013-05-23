package fursten.simulator.persistent;

import fursten.util.persistent.DAOTestHelper;

public abstract class DAOManager {

	public abstract NodeManager getNodeManager();
	public abstract ResourceManager getResourceManager();
	public abstract WorldManager getWorldManager();
	public abstract LinkManager getLinkManager();
	
	public static DAOManager get() {
		return new fursten.simulator.persistent.mysql.DAOFactory();
	}
	
	public static DAOTestHelper getTestHelper() {
		return new fursten.simulator.persistent.mysql.TestHelper();
	}
}

package fursten.simulator.persistent;

//import fursten.util.persistent.DAOTestHelper;

public abstract class DAOManager {

	public abstract NodeManager getNodeManager();
	public abstract ResourceManager getResourceManager();
	public abstract WorldManager getWorldManager();
	public abstract JointManager getLinkManager();
	//public abstract void reset();
	public abstract void clear();
	public abstract void load();
	
	private static DAOManager factory = null;
	
	public static DAOManager get() {
		
		if(factory == null)
			factory = new fursten.simulator.persistent.mysql.DAOFactory();
		
		return factory;
	}
	
	public static void resetAll() {
		/*get().getLinkManager().reset();
		get().getNodeManager().reset();
		get().getResourceManager().reset();
		get().getWorldManager().reset();*/
		get().getLinkManager().clear();
		get().getNodeManager().clear();
		get().getResourceManager().clear();
		get().getWorldManager().clear();
	}
	
	/*public static DAOTestHelper getTestHelper() {
		return new fursten.simulator.persistent.mysql.TestHelper();
	}*/
}

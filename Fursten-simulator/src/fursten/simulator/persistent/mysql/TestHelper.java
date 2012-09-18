package fursten.simulator.persistent.mysql;

import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.DAOTestHelper;

public class TestHelper implements DAOTestHelper {

	public boolean setUp() {
		
		DAOFactory.setDatabase("test");
		DAOManager.get().getNodeManager().deleteAll();
		DAOManager.get().getResourceManager().deleteAll();
		DAOManager.get().getSessionManager().deleteAll();
		return true;
	}

	public boolean tearDown() {
		return true;
	}
}

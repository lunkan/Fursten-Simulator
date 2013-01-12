package fursten.simulator.persistent.mysql;

import fursten.simulator.Settings;
import fursten.simulator.persistent.DAOManager;
import fursten.util.persistent.DAOTestHelper;

public class TestHelper implements DAOTestHelper {

	public boolean setUp() {
		
		Settings.getInstance().init("WebContent/WEB-INF/settings.xml", "test", null);
		DAOManager.get().getNodeManager().deleteAll();
		DAOManager.get().getResourceManager().deleteAll();
		DAOManager.get().getSessionManager().deleteAll();
		return true;
	}

	public boolean tearDown() {
		return true;
	}
}

package fursten.simulator;

import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.persistent.DAOManager;

public class TestShutDown {

	private static final Logger logger = Logger.getLogger(TestShutDown.class.getName());
	
	public static void destroy() {
		//context = contextEvent.getServletContext();
		logger.log(Level.INFO, "ShutDown JUnit Fursten simulator.");
		
		if(Settings.getInstance().settingsMode() == Settings.SettingsMode.JUNIT) {
			DAOManager.get().getNodeManager().deleteAll();
			DAOManager.get().getResourceManager().deleteAll();
			DAOManager.get().getWorldManager().deleteAll();
			DAOManager.get().getLinkManager().deleteAll();
			logger.log(Level.INFO, "ShutDown JUnit Fursten simulator complete - data cleared.");
		}
		else {
			logger.log(Level.SEVERE, "ShutDown JUnit Fursten simulator failed - settings is not in test mode.");
		}
	}
}

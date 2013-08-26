package fursten.simulator;

import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.node.NodeActivityManager;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.resource.ResourceDependencyManager;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceWrapper;

public class TestShutDown {

	private static final Logger logger = Logger.getLogger(TestShutDown.class.getName());
	
	public static void destroy() {
		//context = contextEvent.getServletContext();
		logger.log(Level.INFO, "ShutDown JUnit Fursten simulator.");
		
		ResourceKeyManager.clear();
		ResourceDependencyManager.clear();
		ResourceWrapper.clear();
		NodeActivityManager.clear();
		
		if(Settings.getInstance().settingsMode() == Settings.SettingsMode.JUNIT) {
			//DAOManager.get().reset();
			DAOManager.get().clear();
			logger.log(Level.INFO, "ShutDown JUnit Fursten simulator complete - data cleared.");
		}
		else {
			logger.log(Level.SEVERE, "ShutDown JUnit Fursten simulator failed - settings is not in test mode.");
		}
	}
}

package fursten.simulator.persistent.mysql;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import fursten.simulator.Facade;
import fursten.simulator.Settings;
import fursten.simulator.Startup;
import fursten.simulator.Status;
import fursten.simulator.persistent.DAOManager;
import fursten.util.persistent.DAOTestHelper;

public class TestHelper implements DAOTestHelper {

	private static final Logger logger = Logger.getLogger(TestHelper.class.getName());
	private ServletContext context;
	
	public boolean setUp() {
		
		logger.log(Level.INFO, "Startup Test Fursten simulator.");
		
		String settingsUrl = "WebContent/WEB-INF/settings.xml";
		Settings.getInstance().init(settingsUrl, "test", context);
		Status status = Facade.getStatus();
		
		DAOManager.get().getNodeManager().deleteAll();
		DAOManager.get().getResourceManager().deleteAll();
		DAOManager.get().getSessionManager().deleteAll();
		
		//If simulator is empty -> init blank world
		if(status == null) {
			logger.log(Level.SEVERE, "No world initiated - init default world.");
			boolean success = Facade.init("no name", 10000, 10000);
			if(!success) {
				logger.log(Level.SEVERE, "Fursten simulator could not init default world.");
				return false;
			}
			else {
				logger.log(Level.INFO, "Fursten simulator started with default settings.");
			}
		}
		else {
			logger.log(Level.INFO, "Fursten simulator started: " + status.toString());
		}
		
		return true;
	}

	public boolean tearDown() {
		logger.log(Level.INFO, "End Test Fursten simulator.");
		return true;
	}
}

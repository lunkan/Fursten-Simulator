package fursten.simulator.persistent.mysql;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import fursten.simulator.Facade;
import fursten.simulator.Settings;
import fursten.simulator.Settings.SettingsMode;
import fursten.simulator.world.World;
import fursten.simulator.persistent.DAOManager;
import fursten.util.persistent.DAOTestHelper;

/*public class TestHelper implements DAOTestHelper {

	private static final Logger logger = Logger.getLogger(TestHelper.class.getName());
	private ServletContext context;
	
	public boolean setUp() {
		
		logger.log(Level.INFO, "Startup Test Fursten simulator.");
		
		String settingsUrl = "WebContent/WEB-INF/settings.xml";
		Settings.getInstance().init(settingsUrl, Settings.SettingsMode.JUNIT);//"test");//, context);
		World instance = Facade.getWorld();
		
		DAOManager.get().getNodeManager().deleteAll();
		DAOManager.get().getResourceManager().deleteAll();
		DAOManager.get().getWorldManager().deleteAll();
		DAOManager.get().getLinkManager().deleteAll();
		
		//If simulator is empty -> init blank world
		if(instance == null) {
			logger.log(Level.SEVERE, "No world initiated - init default world.");
			World newInstance = new World();
			newInstance.setName("No name");
			newInstance.setWidth(10000);
			newInstance.setHeight(10000);
			boolean success = Facade.init(newInstance);
			if(!success) {
				logger.log(Level.SEVERE, "Fursten simulator could not init default world.");
				return false;
			}
			else {
				logger.log(Level.INFO, "Fursten simulator started with default settings.");
			}
		}
		else {
			logger.log(Level.INFO, "Fursten simulator started: " + instance.toString());
		}
		
		return true;
	}

	public boolean tearDown() {
		logger.log(Level.INFO, "End Test Fursten simulator.");
		return true;
	}
}*/

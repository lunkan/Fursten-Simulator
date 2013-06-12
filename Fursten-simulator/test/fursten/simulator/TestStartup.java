package fursten.simulator;

import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.world.World;

public class TestStartup {

	private static final Logger logger = Logger.getLogger(TestStartup.class.getName());
	
	public static void init() {
		
		logger.log(Level.INFO, "Startup JUnit test for Fursten simulator.");
		
		String settingsUrl = "junit/settings.xml";
		Settings.getInstance().init(settingsUrl, Settings.SettingsMode.JUNIT);
		
		WorldManager SM = DAOFactory.get().getWorldManager();
		World world = SM.getActive();
		
		//Simulator must be empty -> init blank world
		if(world == null) {
			logger.log(Level.INFO, "init test world.");
			World newWorld = new World();
			newWorld.setName("Test World");
			newWorld.setWidth(10000);
			newWorld.setHeight(10000);
			SM.setActive(world);
		}
		else {
			logger.log(Level.WARNING, "Simulator is not empty - JUnit test can only run on an empty world");
			//System.exit(0);
		}
	}
}


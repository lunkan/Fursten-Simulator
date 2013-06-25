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
		
		//Simulator must be empty -> init blank world
		logger.log(Level.INFO, "init test world.");
		World world = new World();
		world.setName("Test World");
		world.setWidth(10000);
		world.setHeight(10000);
		SM.set(world);
	}
}


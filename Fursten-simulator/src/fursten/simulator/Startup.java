package fursten.simulator;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import fursten.simulator.world.World;

public class Startup  implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(Startup.class.getName());
	private ServletContext context;
	
	public void contextInitialized(ServletContextEvent contextEvent) {
		
		logger.log(Level.INFO, "Startup Fursten simulator.");
		context = contextEvent.getServletContext();
		
		//context.setAttribute("TEST", "TEST_VALUE");
		String settingsUrl = context.getRealPath(File.separator) + "WEB-INF" + File.separator + "settings.xml";
		Settings.getInstance().init(settingsUrl, "default", context);
		World world = Facade.getWorld();
		
		//If simulator is empty -> init blank world
		if(world == null) {
			logger.log(Level.SEVERE, "No world initiated - init default world.");
			World newWorld = new World();
			newWorld.setName("No name");
			newWorld.setWidth(10000);
			newWorld.setHeight(10000);
			boolean success = Facade.init(newWorld);
			if(!success) {
				logger.log(Level.SEVERE, "Fursten simulator could not init default world.");
				return;
			}
			else {
				logger.log(Level.INFO, "Fursten simulator started with default settings.");
			}
		}
		else {
			logger.log(Level.INFO, "Fursten simulator started: " + world.toString());
		}
	}
	
	public void contextDestroyed(ServletContextEvent contextEvent) {
		context = contextEvent.getServletContext();
		System.out.println("Context Destroyed");
	}
}

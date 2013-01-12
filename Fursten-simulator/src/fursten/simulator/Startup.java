package fursten.simulator;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import fursten.simulator.persistent.mysql.DAOFactory;

public class Startup  implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(Startup.class.getName());
	private ServletContext context;
	
	public void contextInitialized(ServletContextEvent contextEvent) {
		
		logger.log(Level.INFO, "Startup Fursten simulator.");
		context = contextEvent.getServletContext();
		
		//context.setAttribute("TEST", "TEST_VALUE");
		String settingsUrl = context.getRealPath(File.separator) + "WEB-INF" + File.separator + "settings.xml";
		Settings.getInstance().init(settingsUrl, "default", context);
		Status status = Facade.getStatus();
		
		//If simulator is empty -> init blank world
		if(status == null) {
			logger.log(Level.SEVERE, "No world initiated - init default world.");
			boolean success = Facade.init("no name", 10000, 10000);
			if(!success) {
				logger.log(Level.SEVERE, "Fursten simulator could not init default world.");
				return;
			}
			else {
				logger.log(Level.INFO, "Fursten simulator started with default settings.");
			}
		}
		else {
			logger.log(Level.INFO, "Fursten simulator started: " + status.toString());
		}
	}
	
	public void contextDestroyed(ServletContextEvent contextEvent) {
		context = contextEvent.getServletContext();
		System.out.println("Context Destroyed");
	}
}

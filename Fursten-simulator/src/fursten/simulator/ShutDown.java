package fursten.simulator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import fursten.simulator.persistent.DAOManager;

public class ShutDown implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(ShutDown.class.getName());
	//private ServletContext context;
	
	public void contextInitialized(ServletContextEvent contextEvent) {
		
		//logger.log(Level.INFO, "ShutDown Fursten simulator.");
		//context = contextEvent.getServletContext();
		
		//DAOManager.get().getNodeManager().close();
	}
	
	public void contextDestroyed(ServletContextEvent contextEvent) {
		//context = contextEvent.getServletContext();
		logger.log(Level.INFO, "ShutDown Fursten simulator.");
		DAOManager.get().getNodeManager().close();
	}
}


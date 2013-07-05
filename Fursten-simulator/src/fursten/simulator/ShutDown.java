package fursten.simulator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import fursten.simulator.persistent.DAOManager;

public class ShutDown implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(ShutDown.class.getName());
	
	public void contextInitialized(ServletContextEvent contextEvent) {
		//...
	}
	
	public void contextDestroyed(ServletContextEvent contextEvent) {
		
		//Close autosave and push last changes to database
		Startup.autoSaveProcess.interrupt();
		
		try {
			Startup.autoSaveProcess.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		DAOManager.get().getWorldManager().pushPersistent();
		DAOManager.get().getResourceManager().pushPersistent();
		DAOManager.get().getNodeManager().pushPersistent();
		DAOManager.get().getLinkManager().pushPersistent();
		
		logger.log(Level.INFO, "ShutDown Fursten simulator. Last changes pushed to database");
	}
}


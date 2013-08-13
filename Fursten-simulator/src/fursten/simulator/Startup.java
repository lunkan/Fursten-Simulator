package fursten.simulator;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import fursten.simulator.persistent.AutoSaveManager;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.world.World;

public class Startup  implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(Startup.class.getName());
	private ServletContext context;
	
	//public static Thread autoSaveProcess;
	
	public void contextInitialized(ServletContextEvent contextEvent) {
		
		logger.log(Level.INFO, "Startup Fursten simulator.");
		context = contextEvent.getServletContext();
		
		String settingsUrl = context.getRealPath(File.separator) + "WEB-INF" + File.separator + "settings.xml";
		Settings.getInstance().init(settingsUrl, Settings.SettingsMode.DEFAULT);
		
		//Setup autosave
		/*AutoSaveManager autoSaveManager = new AutoSaveManager(Settings.getInstance().getSimulatorSettings().getAutoSaveUpdateRate());
		autoSaveManager.addPersistable(DAOManager.get().getWorldManager());
		autoSaveManager.addPersistable(DAOManager.get().getResourceManager());
		autoSaveManager.addPersistable(DAOManager.get().getNodeManager());
		autoSaveManager.addPersistable(DAOManager.get().getLinkManager());*/
		
		DAOManager.get().load();
		
		/*autoSaveProcess = new Thread(autoSaveManager);
		autoSaveProcess.start();*/
		
		logger.log(Level.SEVERE, "Fursten simulator started with default settings.");
	}
	
	public void contextDestroyed(ServletContextEvent contextEvent) {
		//context = contextEvent.getServletContext();
	}
}

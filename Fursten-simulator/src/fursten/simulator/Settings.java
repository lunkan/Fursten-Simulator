package fursten.simulator;

import java.io.File;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

public class Settings {

	private static final Logger logger = Logger.getLogger(Settings.class.getName());
	
	public enum SettingsMode {
		DEFAULT, DEBUG, JUNIT
	}
	
	private XMLConfiguration config;
	
	private SettingsMode settingsMode;
	
	//private ServletContext context;
	//private String profile;
	
	private SimulationSettings simulationSettings;
	
	private static Settings instance = new Settings(); 
	
	private Settings() {}
	
	public static Settings getInstance() {
		return instance;
    }
	
	public void init(String settingsUrl, SettingsMode settingsMode) {
		//String profile, ServletContext context) {
		
		//this.context = context;
		//this.profile = profile;
		
		this.settingsMode = settingsMode;
		
		try {
			config = new XMLConfiguration(settingsUrl);
			config.setExpressionEngine(new XPathExpressionEngine());
		} catch (ConfigurationException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	protected XMLConfiguration getConfigurator() {
		return config;
	}
	
	public SimulationSettings getSimulatorSettings() {
		
		if(simulationSettings == null) {
			simulationSettings = new SimulationSettings();
			int updatePrecision = config.getInt("simulator/update-precision");
			int geocellBase = config.getInt("simulator/geocell-base");
			
			simulationSettings.setUpdatePrecision(updatePrecision);
			simulationSettings.setGeocellBase(geocellBase);
		}
		
		return simulationSettings;
	}
	
	public SettingsMode settingsMode() {
		return settingsMode;
	}
	
	public DatabaseSettings getDatabaseSettings() {
		
		if(settingsMode != SettingsMode.JUNIT) {
			/*String driver = config.getString("databases/database[name = '" + profile + "']/driver");
			String url = config.getString("databases/database[name = '" + profile + "']/url");
			String user = config.getString("databases/database[name = '" + profile + "']/user");
			String password = config.getString("databases/database[name = '" + profile + "']/password");*/
			
			String driver = config.getString("databases/database[name = 'default']/driver");
			String url = config.getString("databases/database[name = 'default']/url");
			String user = config.getString("databases/database[name = 'default']/user");
			String password = config.getString("databases/database[name = 'default']/password");
			
			DatabaseSettings dbSettings = new DatabaseSettings();
			dbSettings.setDriver(driver);
			dbSettings.setUrl(url);
			dbSettings.setUser(user);
			dbSettings.setPassword(password);
			
			return dbSettings;
		}
		else {
			return null;
		}
	}
	
	public class SimulationSettings {
	
		private int updatePrecision;
		private int geocellBase;
		
		public int getUpdatePrecision() {
			return updatePrecision;
		}
		
		public void setUpdatePrecision(int value) {
			this.updatePrecision = value;
		}
		
		public int getGeocellBase() {
			return geocellBase;
		}
		
		public void setGeocellBase(int value) {
			this.geocellBase = value;
		}
		
		
	}
	
	public class DatabaseSettings {

		private String driver;
		private String url;
		private String user;
		private String password;
		
		public String getDriver() {
			return driver;
		}
		
		public String getUrl() {
			return url;
		}

		public String getUser() {
			return user;
		}

		public String getPassword() {
			return password;
		}
		
		public void setDriver(String value) {
			driver = value;
		}
		
		public void setUrl(String value) {
			url = value;
		}

		public void setUser(String value) {
			user = value;
		}

		public void setPassword(String value) {
			password = value;
		}
		
		public String toString() {
			return "driver:" + driver + " url:" + driver + " user:" + user;
		}
	}
}
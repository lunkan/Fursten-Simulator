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
	private XMLConfiguration config;
	private ServletContext context;
	private String profile;
	
	private static Settings instance = new Settings(); 
	
	private Settings() {}
	
	public static Settings getInstance() {
		return instance;
    }
	
	public void init(String settingsUrl, String profile, ServletContext context) {
		
		this.context = context;
		this.profile = profile;
		
		if(config == null) {
			try {
				config = new XMLConfiguration(settingsUrl);
				config.setExpressionEngine(new XPathExpressionEngine());
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected XMLConfiguration getConfigurator() {
		return config;
	}
	
	public DatabaseSettings getDatabaseSettings() {
		
		String driver = config.getString("databases/database[name = '" + profile + "']/driver");
		String url = config.getString("databases/database[name = '" + profile + "']/url");
		String user = config.getString("databases/database[name = '" + profile + "']/user");
		String password = config.getString("databases/database[name = '" + profile + "']/password");
				
		DatabaseSettings dbSettings = new DatabaseSettings();
		dbSettings.setDriver(driver);
		dbSettings.setUrl(url);
		dbSettings.setUser(user);
		dbSettings.setPassword(password);
		
		return dbSettings;
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
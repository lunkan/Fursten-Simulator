package fursten.core;

import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

public abstract class Settings {

	private static final Logger logger = Logger.getLogger(Settings.class.getName());
	private static XMLConfiguration config;
	
	public Settings() {
		
		if(config == null) {
			try {
				config = new XMLConfiguration("WebContent/WEB-INF/settings.xml");
				config.setExpressionEngine(new XPathExpressionEngine());
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected XMLConfiguration getConfigurator() {
		return config;
	}
	
	public DatabaseSettings getDatabaseSettings(String name) {
		
		String driver = config.getString("databases/database[name = '" + name + "']/driver");
		String url = config.getString("databases/database[name = '" + name + "']/url");
		String user = config.getString("databases/database[name = '" + name + "']/user");
		String password = config.getString("databases/database[name = '" + name + "']/password");
				
		DatabaseSettings dbSettings = new DatabaseSettings();
		dbSettings.setDriver(driver);
		dbSettings.setUrl(url);
		dbSettings.setUser(user);
		dbSettings.setPassword(password);
		
		return dbSettings;
	}
}
package fursten.core;

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

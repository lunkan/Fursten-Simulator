package fursten.simulator;

import fursten.core.Settings;

public class SimulatorSettings extends Settings {

	private static SimulatorSettings instance = new SimulatorSettings();
	
	private SimulatorSettings() {
		super();
	}
	
	public static SimulatorSettings getInstance() {
		return instance;
    }
	
	/*public float getNodeDeathRate() {
		float deathRate = Float.parseFloat(getConfigurator().getString("simulator.death-rate"));
		return deathRate;
	}
	
	public int getNodeRestlessness() {
		int restlessness = Integer.parseInt(getConfigurator().getString("simulator.restlessness"));
		return restlessness;
	}*/
}

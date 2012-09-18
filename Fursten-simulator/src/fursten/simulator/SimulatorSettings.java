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
}

package fursten.simulator.command;

import fursten.simulator.node.NodeActivityManager;
import fursten.simulator.resource.ResourceDependencyManager;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceWrapper;

public class CleanCommand implements SimulatorCommand {

	public static final String NAME = "Clean";

	public CleanCommand() {
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		ResourceWrapper.clear();
		ResourceDependencyManager.clear();
		NodeActivityManager.clear();
		ResourceKeyManager.clear();
		
		return null;
	}
}

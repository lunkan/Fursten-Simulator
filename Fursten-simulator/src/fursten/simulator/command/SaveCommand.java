package fursten.simulator.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.node.NodeActivityManager;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.resource.ResourceDependencyManager;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceWrapper;

public class SaveCommand {
	
	protected static final Logger logger = Logger.getLogger(SaveCommand.class.getName());
	
	public static final String NAME = "Save";

	public SaveCommand() {
	}

	public String getName() {
		return NAME;
	}

	public Object execute() throws Exception {
		
		logger.log(Level.INFO, "Execute Save Command");
		
		DAOManager.get().getWorldManager().pushPersistent();
		DAOManager.get().getResourceManager().pushPersistent();
		DAOManager.get().getNodeManager().pushPersistent();
		DAOManager.get().getLinkManager().pushPersistent();
		
		logger.log(Level.INFO, "Save completed.");
		return null;
	}
}



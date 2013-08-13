package fursten.simulator.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.persistent.DAOManager;

public class LoadCommand {
	
	protected static final Logger logger = Logger.getLogger(LoadCommand.class.getName());
	public static final String NAME = "Load";

	public LoadCommand() {
	}

	public String getName() {
		return NAME;
	}

	public Object execute() throws Exception {
		
		logger.log(Level.INFO, "Execute Load Command");
		
		DAOManager.get().getWorldManager().pullPersistent();
		DAOManager.get().getResourceManager().pullPersistent();
		DAOManager.get().getNodeManager().pullPersistent();
		DAOManager.get().getLinkManager().pullPersistent();
		
		new CleanCommand().execute();
		
		logger.log(Level.INFO, "Load completed.");
		return null;
	}
}




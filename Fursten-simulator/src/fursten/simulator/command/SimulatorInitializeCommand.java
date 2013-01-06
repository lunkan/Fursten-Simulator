package fursten.simulator.command;

import fursten.simulator.instance.Instance;
import fursten.simulator.persistent.SessionManager;
import fursten.simulator.persistent.mysql.DAOFactory;

public class SimulatorInitializeCommand implements SimulatorCommand {

	public static final String NAME = "Initialize";
	private Instance session;

	public SimulatorInitializeCommand(Instance session) {
		this.session = session;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		//Clear all
		DAOFactory.get().getResourceManager().deleteAll();
		DAOFactory.get().getNodeManager().deleteAll();
		
		SessionManager SM = DAOFactory.get().getSessionManager();
		SM.setActive(session);
		return null;
	}
}

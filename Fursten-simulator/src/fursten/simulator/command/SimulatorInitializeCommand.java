package fursten.simulator.command;

import fursten.simulator.world.World;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;

public class SimulatorInitializeCommand implements SimulatorCommand {

	public static final String NAME = "Initialize";
	private World world;

	public SimulatorInitializeCommand(World world) {
		this.world = world;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		//Clear all
		DAOFactory.get().getResourceManager().deleteAll();
		DAOFactory.get().getNodeManager().deleteAll();
		
		WorldManager SM = DAOFactory.get().getWorldManager();
		SM.setActive(world);
		return null;
	}
}

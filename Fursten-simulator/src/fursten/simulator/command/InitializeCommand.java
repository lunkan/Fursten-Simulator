package fursten.simulator.command;

import fursten.simulator.world.World;
import fursten.simulator.node.NodeActivityManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.ResourceDependencyManager;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceWrapper;

public class InitializeCommand implements SimulatorCommand {

	public static final String NAME = "Initialize";
	private World world;

	public InitializeCommand(World world) {
		this.world = world;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		//Clear all
		//DAOFactory.get().reset();
		DAOFactory.get().clear();
		
		WorldManager SM = DAOFactory.get().getWorldManager();
		System.out.println(world);
		SM.set(world);
		
		//Important! Clear resource manager cache
		new CleanCommand().execute();
		
		return null;
	}
}

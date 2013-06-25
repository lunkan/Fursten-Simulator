package fursten.simulator.persistent;

import java.util.List;

import fursten.simulator.world.World;

public interface WorldManager extends Persistable, PersistantManager {

	public int set(World world);
	public World get();
}
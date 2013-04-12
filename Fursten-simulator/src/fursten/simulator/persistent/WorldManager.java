package fursten.simulator.persistent;

import java.util.List;

import fursten.simulator.world.World;

public interface WorldManager {

	public int setActive(World world);
	public World getActive();
	public boolean clear();
	public boolean deleteAll();
	public List<World> getHistory();
}

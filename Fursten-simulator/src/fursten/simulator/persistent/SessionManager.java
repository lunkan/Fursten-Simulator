package fursten.simulator.persistent;

import java.util.List;

import fursten.simulator.instance.Instance;

public interface SessionManager {

	public int setActive(Instance session);
	public Instance getActive();
	public boolean clear();
	public boolean deleteAll();
	public List<Instance> getHistory();
}

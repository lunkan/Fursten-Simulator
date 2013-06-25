package fursten.simulator.persistent;

import java.util.List;
import java.util.Set;

import fursten.simulator.resource.Resource;

public interface ResourceManager extends Persistable, PersistantManager {

	public int put(Resource... recources);
	public int putAll(List<Resource> recources);
	
	public int remove(int... keys);
	public int removeAll(Set<Integer> keys);
	
	public Resource get(int key);
	public List<Resource> get(Set<Integer> keys);
	
	public Set<Integer> getKeys();
}


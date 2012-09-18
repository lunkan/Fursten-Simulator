package fursten.simulator.persistent;

import java.util.List;
import java.util.Set;

import fursten.simulator.resource.Resource;

public interface ResourceManager {

	public void clearCache();
	
	public int insert(Resource recource);
	public int delete(int key);
	public Resource get(int key);
	
	public int insert(List<Resource> recources);
	public int delete(Set<Integer> keys);
	public boolean deleteAll();
	public List<Resource> get(Set<Integer> keys);
	
	public Set<Integer> getKeys();
}


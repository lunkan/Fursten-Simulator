package fursten.simulator.persistent;

import java.util.List;

import fursten.simulator.resource.Resource;

public interface ResourceManager {

	public void clearCache();
	
	public int insert(Resource recource);
	public int delete(int key);
	public Resource get(int key);
	
	public int insert(List<Resource> recources);
	public int delete(List<Integer> keys);
	public boolean deleteAll();
	public List<Resource> get(List<Integer> keys);
	
	public List<Integer> getKeys();
}


package fursten.simulator.persistent;

import java.awt.Rectangle;
import java.util.List;

import fursten.simulator.node.Node;

public interface NodeManager {

	public boolean deleteByResourceKey(int resourceKey);
	public void clearCacheByResourceKey(int resourceKey);
	public void clearCache();
	public int insert(List<Node> nodes);
	public boolean delete(List<Node> nodes);
	public List<Node> get(Rectangle bounds, List<Integer> resourceKeys);
	public List<Node> get(Rectangle bounds, Integer resourceKey);
}

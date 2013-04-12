package fursten.simulator.persistent;

import java.awt.Rectangle;
import java.util.List;
import java.util.Set;

import fursten.simulator.node.Node;

public interface NodeManager {

	public int deleteByResourceKey(int resourceKey);
	public void clearCacheByResourceKey(int resourceKey);
	public void clearCache();
	public int insert(List<Node> nodes);
	public int delete(List<Node> nodes);
	public boolean deleteAll();
	public List<Node> get(Rectangle bounds, Set<Integer> resourceKeys);
	public List<Node> get(Rectangle bounds, Integer resourceKey);
	public List<Node> get(Rectangle bounds);
	public boolean containsAll(List<Node> nodes);
	public boolean contains(Node node);
}

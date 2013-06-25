package fursten.simulator.persistent;

import java.awt.Rectangle;
import java.util.List;
import java.util.Set;

import fursten.simulator.node.Node;

public interface NodeManager extends Persistable, PersistantManager {

	public int add(Node... nodes);
	public int addAll(List<Node> nodes);
	
	public List<Node> substract(Node... nodes);
	public List<Node> substractAll(List<Node> nodes);
	
	public List<Node> get(Rectangle bounds, Set<Integer> resourceKeys);
	public List<Node> get(Rectangle bounds, Integer resourceKey);
	public List<Node> get(Rectangle bounds);
	public boolean containsAll(List<Node> nodes);
	public boolean contains(Node node);
}

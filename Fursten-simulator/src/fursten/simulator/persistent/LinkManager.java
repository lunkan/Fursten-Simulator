package fursten.simulator.persistent;

import java.util.List;

import fursten.simulator.link.Link;
import fursten.simulator.node.Node;

public interface LinkManager extends Persistable, PersistantManager {

	public int add(Link... links);
	public int addAll(List<Link> links);
	
	public List<Link> remove(Link... links);
	public List<Link> removeAll(List<Link> links);
	
	public List<Link> get(Node... nodes);
	public List<Link> getAll(List<Node> nodes);
	
}

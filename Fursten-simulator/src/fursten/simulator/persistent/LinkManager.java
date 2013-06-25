package fursten.simulator.persistent;

import java.util.List;

import fursten.simulator.link.Link;
import fursten.simulator.node.Node;

public interface LinkManager extends Persistable, PersistantManager {

	public int insert(List<Link> links);
	public int delete(List<Link> links);
	
	public List<Link> get(List<Node> nodes);
	public List<Link> getByNode(Node node);
	public List<Link> getAllByNode(Node node);
}

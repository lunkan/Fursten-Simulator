package fursten.simulator.persistent;

import java.util.List;

import fursten.simulator.link.Link;
import fursten.simulator.node.Node;

public interface LinkManager {

	public void clean();
	public void close();
	public int insert(List<Link> links);
	public int delete(List<Link> links);
	
	public boolean deleteAll();
	
	/*public List<Link> get(List<Long> linkIds);
	public List<Link> getAll(List<Long> linkIds);*/
	
	public List<Link> get(List<Node> nodes);
	public List<Link> getByNode(Node node);
	public List<Link> getAllByNode(Node node);
}

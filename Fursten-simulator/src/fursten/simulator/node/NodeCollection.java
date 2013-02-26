package fursten.simulator.node;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NodeCollection {
	
	private ArrayList<Node> nodes;
	
	public NodeCollection() {
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	public void add(Node node) {
		if(nodes == null)
			nodes = new ArrayList<Node>();
		
		nodes.add(node);
	}
}
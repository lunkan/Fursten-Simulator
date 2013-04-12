package fursten.simulator.node;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NodeTransaction {
	
	private ArrayList<Node> deleteNodes;
	private ArrayList<Node> injectNodes;
	
	public NodeTransaction() {
	}
	
	public ArrayList<Node> getDeleteNodes() {
		return deleteNodes;
	}

	public void setDeleteNodes(ArrayList<Node> deleteNodes) {
		this.deleteNodes = deleteNodes;
	}
	
	public ArrayList<Node> getInjectNodes() {
		return injectNodes;
	}

	public void setInjectNodes(ArrayList<Node> injectNodes) {
		this.injectNodes = injectNodes;
	}
}
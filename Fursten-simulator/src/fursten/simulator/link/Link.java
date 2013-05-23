package fursten.simulator.link;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import fursten.simulator.node.Node;

@XmlRootElement
public class Link implements Serializable {

	static final long serialVersionUID = 10275539472837495L;

	private long id;
	private Node parentNode;
	private Node childNode;
	private float weight;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((childNode == null) ? 0 : childNode.hashCode());
		result = prime * result
				+ ((parentNode == null) ? 0 : parentNode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Link other = (Link) obj;
		if (childNode == null) {
			if (other.childNode != null)
				return false;
		} else if (!childNode.equals(other.childNode))
			return false;
		if (parentNode == null) {
			if (other.parentNode != null)
				return false;
		} else if (!parentNode.equals(other.parentNode))
			return false;
		return true;
	}

	public Link() {
		//VOObject!
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Node getParentNode() {
		return parentNode;
	}
	
	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}
	
	public Node getChildNode() {
		return childNode;
	}
	
	public void setChildNode(Node childNode) {
		this.childNode = childNode;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}
}

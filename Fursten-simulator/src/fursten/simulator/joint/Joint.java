package fursten.simulator.joint;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import fursten.simulator.node.NodePoint;
import fursten.simulator.resource.Resource.WeightGroup;

@XmlRootElement
public class Joint implements Serializable {
	
	static final long serialVersionUID = 10275539472837495L;
	
	private NodePoint nodePoint;
	private ArrayList<Link> links;
	
	public Joint() {
		links = new ArrayList<Link>();
	}
	
	public Joint(NodePoint nodePoint) {
		this.nodePoint = nodePoint;
		links = new ArrayList<Link>();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nodePoint == null) ? 0 : nodePoint.hashCode());
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
		Joint other = (Joint) obj;
		if (nodePoint == null) {
			if (other.nodePoint != null)
				return false;
		} else if (!nodePoint.equals(other.nodePoint))
			return false;
		return true;
	}

	public Joint addLink(NodePoint nodePoint, float weight) {
		
		if(links == null)
			links = new ArrayList<Link>();
		
		Link newLink = new Link(nodePoint, weight);
		int index = links.indexOf(newLink);
		
		//Merge weights if same point or add new
		if(index == -1)
			links.add(newLink);
		else 
			links.get(index).add(weight);
		
		return this;
	}
	
	public NodePoint getNodePoint() {
		return nodePoint;
	}

	public void setNodePoint(NodePoint nodePoint) {
		this.nodePoint = nodePoint;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	
	public ArrayList<Link> getLinks() {
		return this.links;
	}
	
	public String toString() {
		
		String linkStr = "[";
		if(links != null) {
			for(Link link : links) {
				linkStr += link.toString() + ",";
			}
		}
		linkStr += "]";
		
		return "Joint@"+ this.hashCode() +" [nodePoint:"+ nodePoint +" links:" + linkStr +"]";
	}
	
	public static class Link implements Serializable {

		static final long serialVersionUID = 10275539472837495L;
		
		private NodePoint nodePoint;
		private float weight;
		
		public Link() {
			//..
		}
		
		public Link(NodePoint nodePoint, float weight) {
			this.nodePoint = nodePoint;
			this.weight = weight;
		}
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((nodePoint == null) ? 0 : nodePoint.hashCode());
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
			if (nodePoint == null) {
				if (other.nodePoint != null)
					return false;
			} else if (!nodePoint.equals(other.nodePoint))
				return false;
			return true;
		}

		public void add(float value) {
			this.weight += value;
		}
		
		public NodePoint getNodePoint() {
			return nodePoint;
		}

		public void setNodePoint(NodePoint nodePoint) {
			this.nodePoint = nodePoint;
		}

		public float getWeight() {
			return weight;
		}

		public void setWeight(float weight) {
			this.weight = weight;
		}

		public String toString() {
			return "{nodePoint:"+ nodePoint +", weight:"+ weight +"}";
		}
	}
}


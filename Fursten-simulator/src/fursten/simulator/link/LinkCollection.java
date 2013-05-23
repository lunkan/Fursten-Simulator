package fursten.simulator.link;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LinkCollection {
	
	private ArrayList<Link> links;
	
	public LinkCollection() {
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	
	public void add(Link node) {
		if(links == null)
			links = new ArrayList<Link>();
		
		links.add(node);
	}
}

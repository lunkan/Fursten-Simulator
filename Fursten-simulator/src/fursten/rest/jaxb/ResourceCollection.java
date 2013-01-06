package fursten.rest.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceCollection {
	private ArrayList<ResJAXB> resourceList;
  
	public ArrayList<ResJAXB> getList() {
		return resourceList;
	}

	public void setList(ArrayList<ResJAXB> resourceList) {
		this.resourceList = resourceList;
	}
} 

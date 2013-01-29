package fursten.simulator.resource;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceIndex {
	
	private ArrayList<ResourceItem> resources;
	  
	public ArrayList<ResourceItem> getList() {
		return resources;
	}

	public void setList(ArrayList<ResourceItem> resources) {
		this.resources = resources;
	}
	
	public void add(int key, String name) {
		if(resources == null)
			resources = new ArrayList<ResourceItem>();
		
		ResourceItem item = new ResourceItem();
		item.key = key;
		item.name =	name;
		resources.add(item);
	}
	
	public static class ResourceItem implements Serializable {

		static final long serialVersionUID = 10275539472837495L;
		
		public int key;
		public String name;
		
		public String toString() {
			return "{key:"+ key +", name:"+ name +"}";
		}
	}
}


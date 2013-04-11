package fursten.simulator.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceCollection {
	
	private ArrayList<Resource> resources;
	
	public ResourceCollection() {
	}
	
	public ArrayList<Resource> getResources() {
		return resources;
	}

	public void setResources(ArrayList<Resource> resources) {
		this.resources = resources;
	}
	
	public void addDetailed(Resource resource) {
		if(resources == null)
			resources = new ArrayList<Resource>();
		
		resources.add(resource);
	}
	
	public void addSimple(Resource resource) {
		if(resources == null)
			resources = new ArrayList<Resource>();
		
		Resource simpleResource = new Resource();
		simpleResource.setKey(resource.getKey());
		simpleResource.setName(resource.getName());
		simpleResource.setThreshold(resource.getThreshold());
		resources.add(simpleResource);
	}
	
	public Set<Integer> getKeySet() {
		
		HashSet<Integer> keys = new HashSet<Integer>();
		if(resources != null) {
			for(Resource resource : resources) {
				keys.add(resource.getKey());
			}
		}
		
		return keys;
	}
}


package fursten.rest.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceObj {

	public String name;
	public int threshold;
	public ArrayList<ResourceOffspringObj> offsprings;
	public ArrayList<ResourceWeightGroups> weightgroups;
	
	//public ArrayList<HashMap<String,Float>> weights;
	
	public String toString() {
		
		String offSprStr = " offsprings:[";
		for(ResourceOffspringObj offspring : offsprings) {
			offSprStr += "resource:" + offspring.resource + " value:" + offspring.value;
		}
		offSprStr += "]";
		
		String weightgrStr = " weightgroups:[";
		for(ResourceWeightGroups weightGroup : weightgroups) {
			weightgrStr += "[" + weightGroup.toString() + "],";
		}
		weightgrStr += "]";
		
		/*String weightgrStr = " weightgroups:[";
		for(HashMap<String,Float> weightGroup : weights) {
			Iterator<String> it = weightGroup.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				weightgrStr += "{" + key + ":" + weightGroup.get(key) +"},";
			}
		}
		weightgrStr += "]";*/
		
		return "name:" + name + " threshold:" + threshold + offSprStr + weightgrStr;
	}
	
	public static class ResourceOffspringObj {

		public String resource;
		public int value;
		
		public String toString() {
			return "resource:" + resource + " value:" + value;
		}
	}
	
	public static class ResourceWeightGroups {

		public ArrayList<ResourceWeight> weights;
		
		public String toString() {
			
			String weightStr = "";
			for(ResourceWeight weight : weights) {
				weightStr += weight.toString() + ", ";
			}
			
			return weightStr;
		}
	}
	
	public static class ResourceWeight {

		public String resource;
		public int value;
		
		public String toString() {
			return "{resource:" + resource + " value:" + value + "}";
		}
	}
}

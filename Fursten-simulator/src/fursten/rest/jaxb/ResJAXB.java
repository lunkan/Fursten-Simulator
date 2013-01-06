package fursten.rest.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResJAXB {
	private String name;
	private ArrayList<String> attributes;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<String> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}
} 

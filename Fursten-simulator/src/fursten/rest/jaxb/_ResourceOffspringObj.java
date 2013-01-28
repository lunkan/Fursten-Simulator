package fursten.rest.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class _ResourceOffspringObj {

	public String resource;
	public int value;
	
	public String toString() {
		return "resource:" + resource + " value:" + value;
	}
}

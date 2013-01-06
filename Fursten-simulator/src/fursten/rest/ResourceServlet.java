package fursten.rest;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fursten.rest.jaxb.ResJAXB;
import fursten.rest.jaxb.ResourceCollection;

@Path("/resource")
public class ResourceServlet {
	
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ResourceCollection getXML() {
		ResourceCollection resColl = new ResourceCollection();
		ArrayList<ResJAXB> resourceList = new ArrayList<ResJAXB>();
		for(int i=0; i < 3; i++) {
			resourceList.add(getResModel());
		}
		
		resColl.setList(resourceList);
		return resColl;
		
		/*ResJAXB resModel = new ResJAXB();
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("attribute1");
		attributes.add("attribute2");
		resModel.setName("This is my first resource model");
		resModel.setAttributes(attributes);
		return resModel;*/
	}
	
	@GET
	@Produces({ MediaType.TEXT_XML })
	public ResourceCollection getHtml() {
		
		ResourceCollection resColl = new ResourceCollection();
		ArrayList<ResJAXB> resourceList = new ArrayList<ResJAXB>();
		for(int i=0; i < 3; i++) {
			resourceList.add(getResModel());
		}
		
		resColl.setList(resourceList);
		return resColl;
		
		/*ResJAXB resModel = new ResJAXB();
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("attribute1");
		attributes.add("attribute2");
		resModel.setName("This is my first resource model");
		resModel.setAttributes(attributes);
		return resModel;*/
	}
	
	public ResJAXB getResModel() {
		ResJAXB resModel = new ResJAXB();
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("attribute" + Math.random());
		attributes.add("attribute" + Math.random());
		resModel.setName("This is my " + Math.random() + " resource model");
		resModel.setAttributes(attributes);
		return resModel;
	}
} 
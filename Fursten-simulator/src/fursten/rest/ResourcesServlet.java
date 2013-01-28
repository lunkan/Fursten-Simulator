package fursten.rest;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import fursten.rest.jaxb.ResJAXB;
import fursten.rest.jaxb.ResourceCollection;
import fursten.rest.jaxb.ResourceObj;
import fursten.simulator.Facade;
import fursten.simulator.Status;

@Path("/resources")
public class ResourcesServlet {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	 
	//@Path("/{id}")
	
	/*@Path("{resource}")
	public ResourceServlet getResource(@PathParam("resource") String id) {
		return new ResourceServlet(uriInfo, request, id);
	}*/
	
	//@FormParam("id") String id, @Context HttpServletResponse servletResponse
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response newRootResources(ResourceObj resource, @Context HttpServletResponse servletResponse) throws IOException {
		
		//{"name":"New Resourcingas","threshold":100,"offsprings":[{"resource":"14","value":1}],"weightgroups":[{"weights":[{"resource":"12","value":35},{"resource":"11","value":37}]},{"weights":[{"resource":"12","value":35}]}],"atomat":""}
		System.out.println("ok here I'am");
		System.out.println(resource);
		
		return Response.status(Response.Status.OK).build();
		
		/*response.setStatus( SC_OK );
		   response.close();
		   
		servletResponse.setStatus(SC_OK);
		servletResponse.close();*/
	}
	
	/*@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Status getJSON() {
		Status status = Facade.getStatus();
		return status;
	}*/
	
	/*@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ResourceCollection getXML() {
		ResourceCollection resColl = new ResourceCollection();
		ArrayList<ResJAXB> resourceList = new ArrayList<ResJAXB>();
		for(int i=0; i < 3; i++) {
			resourceList.add(getResModel());
		}
		
		resColl.setList(resourceList);
		return resColl;
	}*/
	
	/*@GET
	@Produces({ MediaType.TEXT_XML })
	public ResourceCollection getHtml() {
		
		ResourceCollection resColl = new ResourceCollection();
		ArrayList<ResJAXB> resourceList = new ArrayList<ResJAXB>();
		for(int i=0; i < 3; i++) {
			resourceList.add(getResModel());
		}
		
		resColl.setList(resourceList);
		return resColl;
	}*/
	
	/*public ResJAXB getResModel() {
		ResJAXB resModel = new ResJAXB();
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("attribute" + Math.random());
		attributes.add("attribute" + Math.random());
		resModel.setName("This is my " + Math.random() + " resource model");
		resModel.setAttributes(attributes);
		return resModel;
	}*/
} 
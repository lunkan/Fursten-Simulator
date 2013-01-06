package fursten.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import fursten.simulator.Status;
import fursten.simulator.instance.Instance;

@Path("/instance")
public class InstanceServlet {
	
	/*@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Instance postXML() {
		Instance instance = new Instance();
		return instance;
	}*/
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public Instance transactionRequest(Instance instance){
		
		System.out.println("name: " + instance.getName());
		System.out.println("width: " + instance.getWidth());
		System.out.println("height: " + instance.getHeight());
		
		return instance;
	}
	
	/*@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public Response transactionRequest(ArrayList<EventData> insert){
	....}*/
	
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void postHtml(@FormParam("id") String id,
		      @FormParam("summary") String summary,
		      @FormParam("description") String description,
		      @Context HttpServletResponse servletResponse) throws IOException {
		
		System.out.println("formID: " + id);
		System.out.println("summary: " + summary);
		System.out.println("description: " + description);
		servletResponse.sendRedirect("../index.html");
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Instance getXML() {
		Instance instance = new Instance()
			.setName("New world")
			.setWidth(10000)
			.setHeight(5000);
		System.out.println("getting!");
		return instance;
	}
	
	@GET
	@Produces({ MediaType.TEXT_XML })
	public Instance getHtml() {
		Instance instance = new Instance();
		return instance;
	}
}
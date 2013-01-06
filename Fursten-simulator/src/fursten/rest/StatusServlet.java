package fursten.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fursten.simulator.Status;

@Path("/status")
public class StatusServlet {
	
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Status getXML() {
		Status session = new Status().setId(1000).setName("Jocke").setTick(0).setHeight(200).setWidth(100);
		return session;
	}
	
	@GET
	@Produces({ MediaType.TEXT_XML })
	public Status getHtml() {
		Status session = new Status().setId(1000).setName("Jocke").setTick(0).setHeight(200).setWidth(100);
		return session;
	}
} 
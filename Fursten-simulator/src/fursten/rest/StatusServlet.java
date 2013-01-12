package fursten.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fursten.simulator.Facade;
import fursten.simulator.Status;

@Path("/status")
public class StatusServlet {
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Status getXML() {
		Status status = Facade.getStatus();
		return status;
	}
	
	@GET
	@Produces({ MediaType.TEXT_XML })
	public Status getHtml() {
		Status status = Facade.getStatus();
		return status;
	}
} 
package fursten.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fursten.simulator.Facade;

@Path("/process")
public class ProcessServlet {
	
	@POST
	@Path("/run")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response run() {
		
		if(Facade.run()) 
			return Response.status(Response.Status.OK).build();
		else 
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@POST
	@Path("/clean")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response clean() {
		
		if(Facade.clean())
			return Response.status(Response.Status.OK).build();
		else 
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
}

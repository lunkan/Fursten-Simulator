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
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public Response run() {
		
		if(Facade.run())
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
}

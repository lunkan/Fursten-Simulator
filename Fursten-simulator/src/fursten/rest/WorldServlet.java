package fursten.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fursten.simulator.Facade;
import fursten.simulator.world.World;

@Path("/world")
public class WorldServlet {
	
	@PUT
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response newWorld(World world){
		
		//Create new World
		if(Facade.init(world))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public World getWorld() {
		
		World world = Facade.getWorld();
		System.out.println(world.toString());
		return world;
	}
}
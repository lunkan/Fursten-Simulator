package fursten.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceIndex;
import fursten.simulator.resource.ResourceSelection;

@Path("/resources")
public class ResourceServlet {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response newRootResource(Resource resource, @Context HttpServletResponse servletResponse) throws IOException {
		
		//Add new resource as root resource. parent=0
		if(Facade.addResource(0, resource))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@POST
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response newResource(@PathParam("id") String hexId, Resource resource) throws IOException {
		
		int id = Integer.parseInt(hexId);//, 16);
		
		//Add new resource as root resource. parent=0
		if(Facade.addResource(id, resource))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ResourceIndex getResources() {
		
		return Facade.getResourceIndex();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Resource getResource(@PathParam("id") String id) throws IOException {
		
		System.out.println("GET resource " + id);
		int key = Integer.parseInt(id);//, 16);
		System.out.println("GET resource " + key);
		ResourceSelection selection = new ResourceSelection(key);
		
		//Add new resource as root resource. parent=0
		List<Resource> resources = Facade.getResources(selection);
		if(resources != null) {
			if(resources.size() != 0) {
				return resources.get(0);
			}
		}
		
		return null;
	}
	
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editResource(@PathParam("id") String hexId, Resource resource) throws IOException {
		
		System.out.println("PUT resource " + hexId);
		System.out.println("PUT resource " + resource);
		
		//int id = Integer.parseInt(hexId);//, 16);
		
		ArrayList<Resource> resources = new ArrayList<Resource>();
		resources.add(resource);
		
		//Add new resource as root resource. parent=0
		if(Facade.editResources(null, resources))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteResources(@QueryParam("filter") Integer filter) throws IOException {
		
		System.out.println("filter " + filter);
		Set<Integer> keys;
		
		if(filter == null) {
			ResourceIndex resourceIndex = Facade.getResourceIndex();
			keys = resourceIndex.getKeySet();
		}
		else {
			keys = new HashSet<Integer>();
			keys.add(filter);
		}
		
		if(Facade.editResources(keys, null))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteResource(@PathParam("id") String hexId) throws IOException {
		
		int key = Integer.parseInt(hexId, 16);
		System.out.println("DELETE resource " + key);
		Set<Integer> keys = new HashSet<Integer>();
		keys.add(key);
		
		if(Facade.editResources(keys, null))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
} 
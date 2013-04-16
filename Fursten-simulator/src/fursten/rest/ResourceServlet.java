package fursten.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import fursten.simulator.Facade;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceCollection;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceSelectMethod;
import fursten.simulator.resource.ResourceSelection;

@Path("/resources")
public class ResourceServlet {
	
	protected static final Logger logger = Logger.getLogger(ResourceServlet.class.getName());
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf"})
	public ResourceCollection getResources(
			@QueryParam("details") Boolean details,
			@QueryParam("r") List<String> resourceKeys,
			@QueryParam("method") String method) {

		Set<Integer> resourceFilter = getResourceKeysByParam(resourceKeys, method);	
		ResourceCollection resourceCollection = new ResourceCollection();
		List<Resource> resources = Facade.getResources(new ResourceSelection(resourceFilter));
		
		for(Resource resource : resources) {
			if(details == null || details == false)
				resourceCollection.addSimple(resource);
			else
				resourceCollection.addDetailed(resource);
		}
		
		return resourceCollection;
	}
	
	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf"})
	public Resource getResource(@PathParam("id") String id) throws IOException {
		
		int key = Integer.parseInt(id);
		ResourceSelection selection = new ResourceSelection(key);
		
		List<Resource> resources = Facade.getResources(selection);
		if(resources != null) {
			if(resources.size() != 0) {
				return resources.get(0);
			}
		}
		
		return null;
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf"})
	public Response newRootResource(Resource resource, @Context HttpServletResponse servletResponse) throws IOException {
		
		//Add new resource as root resource. parent=0
		if(Facade.addResource(0, resource))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@POST
	@Path("/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf"})
	public Response newChildResource(@PathParam("id") String hexId, Resource resource) throws IOException {
		
		int id = Integer.parseInt(hexId);//, 16);
		
		//Add new resource as root resource. parent=0
		if(Facade.addResource(id, resource))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@PUT
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf"})
	public Response replaceResources(ResourceCollection resourcesCollection) throws IOException {
		
		if(Facade.editResources(null, resourcesCollection.getResources()))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf"})
	public Response editResource(@PathParam("id") String hexId, Resource resource) throws IOException {
		
		ArrayList<Resource> resources = new ArrayList<Resource>();
		resources.add(resource);
		
		//Add new resource as root resource. parent=0
		if(Facade.editResources(null, resources))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	public Response deleteResources(
			@QueryParam("r") List<String> resourceKeys,
			@QueryParam("method") String method) throws IOException {
			
		Set<Integer> keys = getResourceKeysByParam(resourceKeys, method);
		if(Facade.editResources(keys, null))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteResource(@PathParam("id") String resourceKey) throws IOException {
		
		int key = Integer.parseInt(resourceKey);
		Set<Integer> keys = new HashSet<Integer>();
		keys.add(key);
		
		if(Facade.editResources(keys, null))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	/**
	 * Helper Method
	 * @param resourceKeys
	 * @param method
	 * @return
	 */
	private Set<Integer> getResourceKeysByParam(List<String> resourceKeys, String method) {
		
		Set<Integer> keys = null;
		
		if(resourceKeys == null)
			return keys;
		
		if(resourceKeys.size() == 0)
			return keys;
		
		keys = new TreeSet<Integer>();
		for(String key : resourceKeys) {
			try {
				keys.add(Integer.parseInt(key));
			}
			catch(Exception e) {
				logger.log(Level.WARNING, "Resource key string could not be parsed to resource int");
			}
		}
		
		if(method != null && keys != null) {
			
			Set<Integer> allKeys = Facade.getResourceKeys();
			ResourceKeyManager keyManager = new ResourceKeyManager(allKeys);
			
			if(ResourceSelectMethod.CHILDREN.value.equals(method.toLowerCase()))
				keys = keyManager.getKeysByMethod(keys, ResourceSelectMethod.CHILDREN);
			else if(ResourceSelectMethod.PARENTS.value.equals(method.toLowerCase()))
				keys = keyManager.getKeysByMethod(keys, ResourceSelectMethod.PARENTS);
		}
		
		return keys;
	}
} 
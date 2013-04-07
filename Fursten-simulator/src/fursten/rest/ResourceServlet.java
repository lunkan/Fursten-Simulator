package fursten.rest;

import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import fursten.simulator.Facade;
import fursten.simulator.node.Node;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.Resource.Offspring;
import fursten.simulator.resource.Resource.Weight;
import fursten.simulator.resource.Resource.WeightGroup;
import fursten.simulator.resource.ResourceIndex;
import fursten.simulator.resource.ResourceSelection;

import org.fursten.message.proto._SimulatorProtos.MNode;
import org.fursten.message.proto._SimulatorProtos.MResource;
import org.fursten.message.proto._SimulatorProtos.MResourceStyle;
import org.fursten.message.proto._SimulatorProtos.NodeRequest;
import org.fursten.message.proto._SimulatorProtos.ResourceRequest;
import org.fursten.message.proto._SimulatorProtos.MResource.Builder;
import org.fursten.message.proto._SimulatorProtos.MResource.Tag;
import org.fursten.message.proto._SimulatorProtos.MResourceStyle.Shape;
import org.fursten.message.proto._SimulatorProtos.ResourceResponse;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.Message;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Annotation;

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
	public Response newChildResource(@PathParam("id") String hexId, Resource resource) throws IOException {
		
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
	public ResourceIndex getJSONResources() {
		
		return Facade.getResourceIndex();
	}
	
	@GET
    @Produces("application/x-protobuf")
    public ResourceResponse getProtobufResources() {
		
		org.fursten.message.proto._SimulatorProtos.ResourceResponse.Builder resourceResponseBuilder = ResourceResponse.newBuilder();
		
		List<Resource> resources = Facade.getResources(new ResourceSelection());
		for(Resource recource : resources) {
			
			Builder resourceBuilder = MResource.newBuilder();
			resourceBuilder.setKey(recource.getKey());
			resourceBuilder.setName(recource.getName());
			resourceBuilder.setThreshold(recource.getThreshold());
			
			//Weights
			if(recource.hasWeights()) {
				
				for(int i = 0; i < recource.getWeightGroups().size(); i++) {
					
					for(Weight weight : recource.getWeightGroups().get(i).weights) {
						
						resourceBuilder.addWeight(org.fursten.message.proto._SimulatorProtos.MResource.Weight.newBuilder()
							.setResourceReference(weight.resource)
							.setGroup(i)
							.setValue(weight.value)
							.build()
						);
					}
				}
			}
			
			//Offsprings
			if(recource.hasOffsprings()) {
				for(Offspring offspring : recource.getOffsprings()) {
				
					resourceBuilder.addOffspring(org.fursten.message.proto._SimulatorProtos.MResource.Offspring.newBuilder()
						.setResourceReference(offspring.resource)
						.setValue(offspring.value)
						.build()
					);
				}
			}
			
			resourceBuilder.build();
			resourceResponseBuilder.addResource(resourceBuilder);
		}
		
		resourceResponseBuilder.setSuccess(true);
		return resourceResponseBuilder.build();
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
	@Consumes("application/x-protobuf")
	public Response replaceResources(ResourceResponse resourceResponse) throws IOException {
		
		System.out.println("@Consumes(application/x-protobuf)");
		
		//Delete all resources
		System.out.println("DELETED ALL");
		Set<Integer> keys = Facade.getResourceIndex().getKeySet();
		if(keys.size() > 0)
			if(!Facade.editResources(keys, null))
				return Response.status(Response.Status.BAD_REQUEST).build();
		
		ArrayList<Resource> resources = new ArrayList<Resource>();
		for(MResource mresource : resourceResponse.getResourceList()) {
			
			//Weights
			ArrayList<WeightGroup> weightGroups = new ArrayList<WeightGroup>();
			for(org.fursten.message.proto._SimulatorProtos.MResource.Weight mweight : mresource.getWeightList()) {
				
				while(weightGroups.size() < (mweight.getGroup() +1))
					weightGroups.add(new WeightGroup());
				
				Weight weight = new Weight();
				weight.value = mweight.getValue();
				weight.resource = mweight.getResourceReference();
				weightGroups.get(mweight.getGroup()).weights.add(weight);
			}
			
			//Offsprings
			ArrayList<Offspring> offsprings = new ArrayList<Offspring>();
			for(org.fursten.message.proto._SimulatorProtos.MResource.Offspring moffspring : mresource.getOffspringList()) {
				
				Offspring offspring = new Offspring();
				offspring.resource = moffspring.getResourceReference();
				offspring.value = moffspring.getValue();
				offsprings.add(offspring);
			}
			
			Resource resource = new Resource();
			resource.setKey(mresource.getKey());
			resource.setName(mresource.getName());
			resource.setThreshold(mresource.getThreshold());
			resource.setWeightGroups(weightGroups);
			resource.setOffsprings(offsprings);
			resources.add(resource);
		}
		
		System.out.println("PARSED");
		
		if(Facade.editResources(null, resources))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
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
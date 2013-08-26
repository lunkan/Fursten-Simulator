package fursten.rest;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import fursten.simulator.Facade;
import fursten.simulator.joint.Joint;
import fursten.simulator.joint.JointCollection;
import fursten.simulator.joint.JointTransaction;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeCollection;
import fursten.simulator.node.NodePoint;
import fursten.simulator.node.NodeTransaction;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceSelectMethod;
import fursten.simulator.resource.ResourceWrapper;

@Path("/joints")
public class JointServlet {
	
	protected static final Logger logger = Logger.getLogger(JointServlet.class.getName());
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf"})
	public JointCollection getJoints(
			@QueryParam("x") Integer x,
			@QueryParam("y") Integer y,
			@QueryParam("w") Integer w,
			@QueryParam("h") Integer h,
			@QueryParam("r") List<String> resourceKeys,
			@QueryParam("recursive") Boolean recursive) {
		
		Set<Integer> resourceFilter = ResourceKeyManager.getResourceKeysByMethod(resourceKeys, ResourceSelectMethod.MATCH.value);
		Rectangle rect = getRectByParam(x, y, w, h);
		
		//Also select linked joints or not?
		boolean isRecursive = false;
		if(recursive != null)
			isRecursive = recursive.booleanValue();
		
		//Filter out resources without links
		Iterator<Integer> it = resourceFilter.iterator();
		while(it.hasNext()) {
			if(!ResourceWrapper.getWrapper(it.next()).hasLinks())
				it.remove();
		}
		
		//Collect nodepoints
		List<NodePoint> nodePoints = new ArrayList<NodePoint>();
		List<Node> nodes = Facade.getNodes(rect, resourceFilter);
		for(Node node : nodes) {
			nodePoints.add(node.getNodePoint());
		}
		
		//Retrive non-recursive joints
		List<Joint> joints = Facade.getJoints(nodePoints, isRecursive);
		JointCollection jointCollection = new JointCollection();
		jointCollection.setJoints(new ArrayList<Joint>(joints));
		
		return jointCollection;
	}
	
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf" })
	public Response replaceJoints(
			JointCollection joints,
			@QueryParam("x") Integer x,
			@QueryParam("y") Integer y,
			@QueryParam("w") Integer w,
			@QueryParam("h") Integer h,
			@QueryParam("r") List<String> resourceKeys,
			@QueryParam("recursive") Boolean recursive) throws IOException {
		
		Set<Integer> resourceFilter = ResourceKeyManager.getResourceKeysByMethod(resourceKeys, ResourceSelectMethod.MATCH.value);
		Rectangle rect = getRectByParam(x, y, w, h);
		
		//Also delete linked joints or not?
		boolean deleteRecursive = false;
		if(recursive != null)
			deleteRecursive = recursive.booleanValue();
				
		//Filter out resources without links
		Iterator<Integer> it = resourceFilter.iterator();
		while(it.hasNext()) {
			if(!ResourceWrapper.getWrapper(it.next()).hasLinks())
				it.remove();
		}
		
		//Collect deletePoints
		List<NodePoint> deletePoints = new ArrayList<NodePoint>();
		List<Node> nodes = Facade.getNodes(rect, resourceFilter);
		for(Node node : nodes) {
			deletePoints.add(node.getNodePoint());
		}
		
		if(Facade.editJoints(deletePoints, joints.getJoints(), deleteRecursive))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf" })
	public Response addjoints(
			JointCollection joints) throws IOException {
		
		if(Facade.editJoints(null, joints.getJoints(), false))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	public Response deleteJoints(
			@QueryParam("x") Integer x,
			@QueryParam("y") Integer y,
			@QueryParam("w") Integer w,
			@QueryParam("h") Integer h,
			@QueryParam("r") List<String> resourceKeys,
			@QueryParam("recursive") Boolean recursive) throws IOException {
		
		Set<Integer> resourceFilter = ResourceKeyManager.getResourceKeysByMethod(resourceKeys, ResourceSelectMethod.MATCH.value);
		Rectangle rect = getRectByParam(x, y, w, h);
		
		//Also select linked joints or not?
		boolean deleteRecursive = false;
		if(recursive != null)
			deleteRecursive = recursive.booleanValue();
				
		//Filter out resources without links
		Iterator<Integer> it = resourceFilter.iterator();
		while(it.hasNext()) {
			if(!ResourceWrapper.getWrapper(it.next()).hasLinks())
				it.remove();
		}
		
		//Collect deletePoints
		List<NodePoint> deletePoints = new ArrayList<NodePoint>();
		List<Node> nodes = Facade.getNodes(rect, resourceFilter);
		for(Node node : nodes) {
			deletePoints.add(node.getNodePoint());
		}
		
		if(Facade.editJoints(deletePoints, null, deleteRecursive))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@POST
	@Path("/transaction")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf" })
	public Response injectDeleteJoints(JointTransaction jointTransaction) throws IOException {
	
		if(Facade.editJoints(jointTransaction.getDeleteJoints(), jointTransaction.getInjectJoints(), jointTransaction.getDeleteRecursive()))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	private Rectangle getRectByParam(Integer x, Integer y, Integer w, Integer h) {
		
		Rectangle rect;
		if(x != null && y != null && w != null && h != null)
			rect = new Rectangle(x, y, w, h);
		else if(x != null && y != null)
			rect = new Rectangle(x, y, 1, 1);
		else
			rect = new Rectangle(Integer.MIN_VALUE/2, Integer.MIN_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		return rect;
	}
}

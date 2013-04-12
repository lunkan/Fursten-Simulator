package fursten.rest;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
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
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeCollection;
import fursten.simulator.node.NodeTransaction;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceSelectMethod;

@Path("/nodes")
public class NodeServlet {
	
	protected static final Logger logger = Logger.getLogger(NodeServlet.class.getName());
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf"})
	public NodeCollection getNodes(
			@QueryParam("x") Integer x,
			@QueryParam("y") Integer y,
			@QueryParam("w") Integer w,
			@QueryParam("h") Integer h,
			@QueryParam("r") List<String> resourceKeys,
			@QueryParam("method") String method) {
		
		Set<Integer> resourceFilter = getResourceKeysByParam(resourceKeys, method);
		Rectangle rect = getRectByParam(x, y, w, h);
		
		List<Node> nodes = Facade.getNodes(rect, resourceFilter);
		NodeCollection nodeCollection = new NodeCollection();
		nodeCollection.setNodes(new ArrayList<Node>(nodes));
		
		return nodeCollection;
	}
	
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf" })
	public Response replaceNodes(
			NodeCollection nodes,
			@QueryParam("x") Integer x,
			@QueryParam("y") Integer y,
			@QueryParam("w") Integer w,
			@QueryParam("h") Integer h,
			@QueryParam("r") List<String> resourceKeys,
			@QueryParam("method") String method) throws IOException {
		
		System.out.println("replaceNodes");
		Set<Integer> resourceFilter = getResourceKeysByParam(resourceKeys, method);
		Rectangle rect = getRectByParam(x, y, w, h);
		List<Node> deleteNodes = Facade.getNodes(rect, resourceFilter);
		System.out.println("deleteNodes " + deleteNodes.size());
		if(Facade.editNodes(deleteNodes, nodes.getNodes()))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf" })
	public Response addNodes(NodeCollection nodes) throws IOException {
		
		if(Facade.editNodes(null, nodes.getNodes()))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	public Response deleteNodes(
			@QueryParam("x") Integer x,
			@QueryParam("y") Integer y,
			@QueryParam("w") Integer w,
			@QueryParam("h") Integer h,
			@QueryParam("r") List<String> resourceKeys,
			@QueryParam("method") String method) {
		
		Set<Integer> resourceFilter = getResourceKeysByParam(resourceKeys, method);
		Rectangle rect = getRectByParam(x, y, w, h);
		
		List<Node> nodes = Facade.getNodes(rect, resourceFilter);
		if(Facade.editNodes(nodes, null))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@POST
	@Path("/transaction")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf" })
	public Response injectDeleteNodes(NodeTransaction nodeTransaction) throws IOException {
		System.out.println("transaction");
		System.out.println(nodeTransaction);
		if(nodeTransaction.getDeleteNodes() != null) {
		for(Node node : nodeTransaction.getDeleteNodes())
			System.out.println("D:" + node.toString());
		}
		
		if(nodeTransaction.getInjectNodes() != null) {
		for(Node node : nodeTransaction.getInjectNodes())
			System.out.println("I:" + node.toString());
		}
	
		if(Facade.editNodes(nodeTransaction.getDeleteNodes(), nodeTransaction.getInjectNodes()))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	/**
	 * OBSOLITE
	 * @param nodes
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/inject")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response injectNodes(NodeCollection nodes) throws IOException {
		
		if(Facade.editNodes(null, nodes.getNodes()))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	/**
	 * OBSOLITE
	 * @param nodes
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/remove")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeNodes(NodeCollection nodes) throws IOException {
		
		if(Facade.editNodes(nodes.getNodes(), null))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	
	private Rectangle getRectByParam(Integer x, Integer y, Integer w, Integer h) {
	
		Rectangle rect;
		if(x != null && y != null && w != null && h != null)
			rect = new Rectangle(x, y, w, h);
		else
			rect = new Rectangle(Integer.MIN_VALUE/2, Integer.MIN_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		return rect;
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
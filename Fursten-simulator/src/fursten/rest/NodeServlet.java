package fursten.rest;

import java.awt.Rectangle;
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
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeCollection;

@Path("/nodes")
public class NodeServlet {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NodeCollection getNodes(@QueryParam("x") Integer x, @QueryParam("y") Integer y, @QueryParam("w") Integer w, @QueryParam("h") Integer h ) {
		
		//Todo: add filter arguments
		Set<Integer> filter = null;
		
		Rectangle rect;
		if(x != null && y != null && w != null && h != null)
			rect = new Rectangle(x, y, w, h);
		else
			rect = new Rectangle(Integer.MIN_VALUE/2, Integer.MIN_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		List<Node> nodes = Facade.getNodes(rect, filter);
		NodeCollection nodeCollection = new NodeCollection();
		nodeCollection.setNodes(new ArrayList<Node>(nodes));
		
		return nodeCollection;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response replaceNodes(NodeCollection nodes) throws IOException {
		
		Rectangle rect = new Rectangle(Integer.MIN_VALUE/2, Integer.MIN_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE);
		List<Node> deleteNodes = Facade.getNodes(rect, null);
		
		if(Facade.editNodes(deleteNodes, nodes.getNodes()))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNode(Node node) throws IOException {
		
		//if(Facade.editNodes(null, nodes.getNodes()))
			return Response.status(Response.Status.OK).build();
		//else
		//	return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteNodes() throws IOException {
		
		Rectangle rect = new Rectangle(Integer.MIN_VALUE/2, Integer.MIN_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE);
		List<Node> nodes = Facade.getNodes(rect, null);
		
		if(Facade.editNodes(nodes, null))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
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
}
package fursten.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
import fursten.simulator.link.Link;
import fursten.simulator.link.LinkCollection;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeCollection;
import fursten.simulator.persistent.DAOManager;

@Path("/links")
public class LinkServlet {
	
	protected static final Logger logger = Logger.getLogger(LinkServlet.class.getName());
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf"})
	public LinkCollection getLinks(
			NodeCollection nodes,
			@QueryParam("recursive") Boolean recursive) {
		
		List<Link> links = Facade.getLinks(nodes.getNodes(), recursive);
		LinkCollection linkCollection = new LinkCollection();
		linkCollection.setLinks(new ArrayList<Link>(links));
		return linkCollection;
	}
	
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf" })
	public Response replaceLinks(
			LinkCollection links) throws IOException {
		
		//Fetch links to delete
		Set<Node> linkNodes = new HashSet<Node>();
		for(Link link : links.getLinks())
			linkNodes.add(link.getParentNode());
		
		List<Link> replacedLinks = Facade.getLinks(new ArrayList<Node>(linkNodes), true);
		
		if(Facade.editLinks(replacedLinks, links.getLinks()))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf" })
	public Response addLinks(
			LinkCollection links) throws IOException {
		
		if(Facade.editLinks(null, links.getLinks()))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	public Response deleteLinks(
			@QueryParam("id") List<Long> linkIds) {
		
		/*List<Link> links = DAOManager.get().getLinkManager().get(linkIds);
		if(Facade.editLinks(links, null))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.BAD_REQUEST).build();*/
		
		return null;
	}
}

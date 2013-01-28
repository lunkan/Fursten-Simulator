package fursten.rest;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import fursten.rest.jaxb.ResJAXB;
import fursten.rest.jaxb.ResourceCollection;
import fursten.simulator.Facade;
import fursten.simulator.Status;

public class ResourceServlet {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;
	public ResourceServlet(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}
} 

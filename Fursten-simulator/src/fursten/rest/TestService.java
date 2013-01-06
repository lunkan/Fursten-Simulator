package fursten.rest;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*public class Test {
  public static void main(String[] args) {
    ClientConfig config = new DefaultClientConfig();
    Client client = Client.create(config);
    WebResource service = client.resource(getBaseURI());
    // Fluent interfaces
    System.out.println(service.path("rest").path("resource").accept(MediaType.TEXT_PLAIN).get(ClientResponse.class).toString());
    // Get plain text
    System.out.println(service.path("rest").path("resource").accept(MediaType.TEXT_PLAIN).get(String.class));
    // Get XML
    System.out.println(service.path("rest").path("resource").accept(MediaType.TEXT_XML).get(String.class));
    // The HTML
    System.out.println(service.path("rest").path("resource").accept(MediaType.TEXT_HTML).get(String.class));

  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost:8989/Fursten-simulator").build();
  }
}*/



public class TestService extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
		
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    WebResource service = client.resource(getBaseURI());
	    // Fluent interfaces
	    //System.out.println(service.path("rest").path("resource").accept(MediaType.TEXT_PLAIN).get(ClientResponse.class).toString());
	    // Get plain text
	    //System.out.println(service.path("rest").path("resource").accept(MediaType.TEXT_PLAIN).get(String.class));
	    // Get XML
	    System.out.println(service.path("rest").path("resource").accept(MediaType.TEXT_XML).get(String.class));
	    // The HTML
	    System.out.println(service.path("rest").path("resource").accept(MediaType.APPLICATION_JSON).get(String.class));
	    
		response.getWriter().write("Hello, world!");
	}
	
	private static URI getBaseURI() {
	    return UriBuilder.fromUri("http://localhost:8989/Fursten-simulator").build();
	}
}
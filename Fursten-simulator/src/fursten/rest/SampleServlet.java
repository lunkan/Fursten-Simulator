package fursten.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fursten.simulator.Facade;
import fursten.simulator.sample.Sample;
import fursten.simulator.sample.SampleCollection;

@Path("/samples")
public class SampleServlet {

	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-protobuf"})
	public SampleCollection getSamples(SampleCollection samples, @QueryParam("snap") Integer snap) throws IOException {
		
		System.out.println("getSamples " + snap);
		
		List<Sample> sampleList = Facade.getSamples(samples.getSamples(), snap);
		samples.setSamples(new ArrayList<Sample>(sampleList));
		return samples;
	}
}

package fursten.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fursten.simulator.Facade;
import fursten.simulator.sample.Sample;
import fursten.simulator.sample.SampleCollection;

@Path("/samples")
public class SampleServlet {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public SampleCollection getSamples(SampleCollection samples) throws IOException {
		
		List<Sample> sampleList = Facade.getSamples(samples.getSamples());
		
		if(sampleList != null) {
			SampleCollection sampleCollection = new SampleCollection();
			sampleCollection.setSamples(new ArrayList<Sample>(sampleList));
			return sampleCollection;
		}
		else {
			return null;
		}
	}
}

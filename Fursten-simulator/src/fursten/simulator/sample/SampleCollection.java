package fursten.simulator.sample;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SampleCollection {
	
	private ArrayList<Sample> samples;
	
	public SampleCollection() {
	}
	
	public ArrayList<Sample> getSamples() {
		return samples;
	}

	public void setSamples(ArrayList<Sample> samples) {
		this.samples = samples;
	}
	
	public void add(Sample sample) {
		if(samples == null)
			samples = new ArrayList<Sample>();
		
		samples.add(sample);
	}
}

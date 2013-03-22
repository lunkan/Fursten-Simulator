package fursten.simulator.command;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.node.NodeStabilityCalculator;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.ResourceWrapper;
import fursten.simulator.sample.Sample;

public class SampleCommand implements SimulatorCommand {

	protected static final Logger logger = Logger.getLogger(SampleCommand.class.getName());
	public static final String NAME = "Sample";
	
	private NodeStabilityCalculator nodeMath;
	private ResourceManager RM;
	private List<Sample> samples;
	
	public SampleCommand(List<Sample> samples){
		this.samples = samples;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		long timeStampStart = System.currentTimeMillis();
		
		RM = DAOFactory.get().getResourceManager();
		nodeMath = NodeStabilityCalculator.getInstance();
		
		ResourceWrapper resource = null;
		Collections.sort(samples, new SampleComparator());
		
		for(Sample sample : samples) {
			
			if(resource.getResource().getKey() != sample.getR())
				resource = new ResourceWrapper(RM.get(sample.getR()));
			
			if(!resource.isStatic()) {
				float stability = nodeMath.calculateStability(sample.getX(), sample.getY(), resource);
				sample.setStability(stability);
			}
		}
		
		logger.log(Level.INFO, "Sample " + samples.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return samples;
	}
	
	class SampleComparator implements Comparator<Sample>{
		 
	    @Override
	    public int compare(Sample o1, Sample o2) {
	        return (o1.getR() > o2.getR() ? -1 : (o1.getR() == o2.getR() ? 0 : 1));
	    }
	}
}
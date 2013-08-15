package fursten.simulator.command;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.node.Node;
import fursten.simulator.node.NodeStabilityCalculator;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.ResourceWrapper;
import fursten.simulator.sample.Sample;

public class SampleCommand implements SimulatorCommand {

	protected static final Logger logger = Logger.getLogger(SampleCommand.class.getName());
	public static final String NAME = "Sample";
	
	private NodeStabilityCalculator nodeMath;
	private ResourceManager RM;
	private NodeManager NM;
	private List<Sample> samples;
	private int snapWidth;
	
	public SampleCommand(List<Sample> samples, Integer snap){
		this.samples = samples;
		
		if(snap == null)
			this.snapWidth = 1;
		else if(snap < 1)
			this.snapWidth = 1;
		else
			this.snapWidth = snap * 2;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		long timeStampStart = System.currentTimeMillis();
		
		RM = DAOFactory.get().getResourceManager();
		NM = DAOFactory.get().getNodeManager();
		nodeMath = NodeStabilityCalculator.getInstance();
		Rectangle bounds = new Rectangle();
		
		//Snap to closest nodes if snap option is set.
		bounds.setSize(this.snapWidth, this.snapWidth);
			
		Iterator<Sample> it = samples.iterator();
		while(it.hasNext()) {
			
			Sample sample = it.next();
			ResourceWrapper resource = ResourceWrapper.getWrapper(sample.getR());
			bounds.setLocation(sample.getX()-(int)Math.floor(this.snapWidth/2), sample.getY()-(int)Math.floor(this.snapWidth/2));
			
			//Fetch nodes
			List<Node> nodes = NM.get(bounds, sample.getR());
			
			if(nodes.size() == 0) {
				//No match found - in air sample. Value = 0
				float stability = nodeMath.calculateStability(sample.getX(), sample.getY(), resource);
				sample.setV(0.0f);
				sample.setStability(stability);
			}
			else {
				
				Node snapNode = null;
				
				//Snap to closest
				for(Node node : nodes) {
					if(snapNode == null) {
						snapNode = node;
					}
					else {
						int distA = (int)Math.sqrt(Math.pow(sample.getX()-snapNode.getX(), 2) + Math.pow(sample.getY()-snapNode.getY(), 2));
						int distB = (int)Math.sqrt(Math.pow(sample.getX()-node.getX(), 2) + Math.pow(sample.getY()-node.getY(), 2));
						if(distB < distA)
							snapNode = node;
					}
				}
				
				float stability = nodeMath.calculateStability(snapNode.getX(), snapNode.getY(), resource);
				sample.setX(snapNode.getX());
				sample.setY(snapNode.getY());
				sample.setV(snapNode.getV());
				sample.setStability(stability);
			}
		}
		
		logger.log(Level.INFO, "Sample " + samples.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return samples;
	}
}
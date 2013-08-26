package fursten.simulator.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.joint.Joint;
import fursten.simulator.joint.Joint.Link;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodePoint;
import fursten.simulator.persistent.JointManager;
import fursten.simulator.persistent.mysql.DAOFactory;

public class JointGetCommand implements SimulatorCommand {

	protected static final Logger logger = Logger.getLogger(NodeGetCommand.class.getName());
	public static final String NAME = "GetJoints";
	
	private boolean recursive;
	private List<NodePoint> nodePoints;
	
	public JointGetCommand(List<NodePoint> nodePoints, boolean recursive){
		
		this.recursive = recursive;
		this.nodePoints = nodePoints;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		long timeStampStart = System.currentTimeMillis();
		JointManager LM = DAOFactory.get().getLinkManager();
		Set<Joint> joints = new HashSet<Joint>();
		
		Queue<NodePoint> jointPointQueue = new LinkedList<NodePoint>(nodePoints);
		while(!jointPointQueue.isEmpty()) {
		
			NodePoint jointpoint = jointPointQueue.poll();
			Joint joint = LM.get(jointpoint);
			
			if(joint != null) {
				joints.add(joint);
				
				if(recursive) {
					for(Link link : joint.getLinks())
						jointPointQueue.add(link.getNodePoint());
				}
			}
		}
		
		logger.log(Level.INFO, "Fetched joints num:" + joints.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return new ArrayList<Joint>(joints);
	}
}

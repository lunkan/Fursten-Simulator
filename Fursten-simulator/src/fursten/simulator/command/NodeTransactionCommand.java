package fursten.simulator.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.joint.Joint;
import fursten.simulator.joint.Joint.Link;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeActivityManager;
import fursten.simulator.persistent.JointManager;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.ResourceWrapper;

public class NodeTransactionCommand implements SimulatorCommand {
	
	protected static final Logger logger = Logger.getLogger(NodeTransactionCommand.class.getName());
	public static final String NAME = "UpdateNode";
	
	private List<Node> substractNodes;
	private List<Node> insertNodes;
	
	private NodeManager NM;
	private JointManager LM;
	
	public NodeTransactionCommand(List<Node> substractNodes) {
		if(substractNodes != null)
			this.substractNodes = substractNodes;
		else
			this.substractNodes = new ArrayList<Node>();
	}

	public NodeTransactionCommand(List<Node> substractNodes, List<Node> insertNodes) {
		
		if(substractNodes != null)
			this.substractNodes = substractNodes;
		else
			this.substractNodes = new ArrayList<Node>();
		
		if(insertNodes != null)
			this.insertNodes = insertNodes;
		else
			this.insertNodes = new ArrayList<Node>();
	}

	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
	
		long timeStampStart = System.currentTimeMillis();
		
		int substractedNum = substractNodes.size();
		int deleteJoints  = 0;
		int insertedNum = 0;
		
		List<Node> substractCommonNodes = new ArrayList<Node>();
		List<Node> substractJointNodes = new ArrayList<Node>();
		
		NM = DAOFactory.get().getNodeManager();
		LM = DAOFactory.get().getLinkManager();
		
		//Substract nodes
		if(substractNodes != null) {
			
			Queue<Node> substractQueue = new LinkedList<Node>(substractNodes);
			while(!substractQueue.isEmpty()) {
			
				Node substractNode = substractQueue.poll();
				
				//Check whether we need to substract joint nodes as well
				//ToDo: Maybe set a flag on node or bit on resource key to make it faster.
				if(ResourceWrapper.getWrapper(substractNode.getR()).hasLinks()) {
				
					substractJointNodes.add(substractNode);
					
					Joint joint = LM.get(substractNode.getNodePoint());
					if(joint != null) {
						
						//Append joint nodes to substractQueue and substract nodes
						for(Link link : joint.getLinks()) {
							float substractVal = substractNode.getV() * link.getWeight();
							Node jointNode = new Node(link.getNodePoint(), substractVal);
							substractQueue.add(jointNode);
						}
					}
				}
				else {
					substractCommonNodes.add(substractNode);
				}
			}
			
			//Substract nodes
			NM.substractAll(substractCommonNodes);
			List<Node> deletedJointNodes = NM.substractAll(substractJointNodes);
			
			//We need to delete all joints for deleted nodes
			for(Node deletedJointNode : deletedJointNodes)
				LM.remove(deletedJointNode.getNodePoint());
			
			//Invalidate All
			NodeActivityManager.invalidate(substractCommonNodes);
			NodeActivityManager.invalidate(substractJointNodes);
		}
		
		//Add nodes
		if(insertNodes != null) {
			
			WorldManager SM = DAOFactory.get().getWorldManager();
			//World activeSession = SM.get();
			
			//Set<Integer> validResourceKeys = RM.getKeys();
			//Validate inserted nodes
			/*for(Node node : insertNodes) {
				if(!validResourceKeys.contains(node.getR())) {
					logger.log(Level.WARNING, "Failed adding node becouse resourceId: " + node.getR() + " does not have a resource attached to it. No nodes where added.");
					return null;
				}
				else if(!activeSession.getRect().contains(node.getX(), node.getY())) {
					logger.log(Level.WARNING, "Failed adding node becouse node: " + node.toString() + " is out of bounds. World bounds: " + activeSession.getRect().toString());
					return null;
				}
				else if(node.getV() <= 0) {
					logger.log(Level.WARNING, "Failed adding node becouse node: " + node.toString() + " has no value.");
					return null;
				}
			}*/
			
			insertedNum = NM.addAll(insertNodes);
			if(insertNodes.size() != insertedNum) {
				throw new Exception("Database may be corrupt! Nodes where added but the response number does not equal the number of of nodes inserted. Inserted:" + insertNodes.size());
			}
			
			NodeActivityManager.invalidate(insertNodes);
		}
		
		logger.log(Level.INFO, "Substracted " + substractedNum + " " + deleteJoints + " joints removed. Inserted " + insertedNum + " nodes. Time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}
}

package fursten.simulator.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.world.World;
import fursten.simulator.link.Link;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeActivityManager;
import fursten.simulator.persistent.LinkManager;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.ResourceWrapper;

public class NodeTransactionCommand implements SimulatorCommand {
	
	protected static final Logger logger = Logger.getLogger(NodeTransactionCommand.class.getName());
	public static final String NAME = "UpdateNode";
	
	private List<Node> substractNodes;
	private List<Node> insertNodes;
	
	private NodeManager NM;
	private ResourceManager RM;
	private LinkManager LM;
	
	public NodeTransactionCommand(List<Node> substractNodes) {
		this.substractNodes = substractNodes;
	}

	public NodeTransactionCommand(List<Node> substractNodes, List<Node> insertNodes) {
		this.substractNodes = substractNodes;
		this.insertNodes = insertNodes;
	}

	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
	
		long timeStampStart = System.currentTimeMillis();
		int substractedNum = 0;
		int deleteLinks  = 0;
		int deletedNum = 0;
		int insertedNum = 0;
		NM = DAOFactory.get().getNodeManager();
		RM = DAOFactory.get().getResourceManager();
		LM = DAOFactory.get().getLinkManager();
		
		List<Node> jointNodes = new ArrayList<Node>();
		
		//Delete nodes
		if(substractNodes != null) {
			
			//Remove attached joints
			int resourceKey = 0;
			boolean hasLinks = false;
			Collections.sort(substractNodes, new NodeSort());
			for(Node deleteNode : substractNodes) {
				
				if(deleteNode.getR() != resourceKey) {
					resourceKey = deleteNode.getR();
					hasLinks = ResourceWrapper.getWrapper(RM.get(resourceKey)).hasLinks();
				}
				
				if(hasLinks)
					applyLinkedNodesSubstraction(deleteNode, 1, deleteNode.getV(), jointNodes);
			}
			
			if(jointNodes.size() > 0) {
				substractNodes.addAll(jointNodes);
				List<Node> deletedJoints = NM.substract(jointNodes);
				List<Link> deletedNodeLinks = LM.get(deletedJoints);
				LM.delete(new ArrayList<Link>(deletedNodeLinks));
				deleteLinks += deletedNodeLinks.size();
				deletedNum += deletedJoints.size();
			}
			
			deletedNum += NM.substract(substractNodes).size();
			substractedNum += substractNodes.size();
			NodeActivityManager.invalidate(substractNodes);
		}
		
		//Insert nodes
		if(insertNodes != null) {
			
			WorldManager SM = DAOFactory.get().getWorldManager();
			World activeSession = SM.getActive();
			
			Set<Integer> validResourceKeys = RM.getKeys();
			
			//Validate inserted nodes
			for(Node node : insertNodes) {
				if(!validResourceKeys.contains(node.getR()))
					throw new Exception("Failed adding node becouse resourceId: " + node.getR() + " does not have a resource attached to it. No nodes where added.");
				else if(!activeSession.getRect().contains(node.getX(), node.getY()))
					throw new Exception("Failed adding node becouse node: " + node.toString() + " is out of bounds. World bounds: " + activeSession.getRect().toString());
				else if(node.getV() <= 0)
					throw new Exception("Failed adding node becouse node: " + node.toString() + " has no value.");
			}
			
			insertedNum = NM.insert(insertNodes);
			NodeActivityManager.invalidate(insertNodes);
			
			if(insertNodes.size() != insertedNum) {
				throw new Exception("Database may be corrupt! Nodes where added but the response number does not equal the number of of nodes inserted. Inserted:" + insertNodes.size());
			}
		}
		
		
		logger.log(Level.INFO, "Substracted " + substractedNum + " nodes - deleted " + deletedNum + " ("+ jointNodes.size() +" linked nodes) " + deleteLinks + " links removed. Inserted " + insertedNum + " nodes. Time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}
	
	/**
	 * Calculate the amount to abstract from nodes linked (and there by impacted) when the parent node value is reduced
	 * This method returns a list of thouse nodes that is to be reduced.
	 * @param link
	 * @param weight
	 * @param substraction
	 * @param substractedLinkNodes
	 */
	private void applyLinkedNodesSubstraction(Node node, float weight, float substraction, List<Node> substractedLinkNodes) {
		
		for(Link link : LM.getByNode(node)) {
			float weightSum = substraction * link.getWeight();
			if(weightSum > 0) {
				Node childNode = link.getChildNode().clone();
				childNode.setV(weightSum * substraction);
				substractedLinkNodes.add(childNode);
				applyLinkedNodesSubstraction(childNode, weightSum, substraction, substractedLinkNodes);
			}
		}
	}
	
	private static class NodeSort implements Comparator<Node>{
		 
	    public int compare(Node o1, Node o2) {
	        return (o1.getR() > o2.getR() ? -1 : (o1.getR() == o2.getR() ? 0 : 1));
	    }
	}
}
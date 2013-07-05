package fursten.simulator.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
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
		int deleteLinks  = 0;
		int insertedNum = 0;
		
		NM = DAOFactory.get().getNodeManager();
		RM = DAOFactory.get().getResourceManager();
		LM = DAOFactory.get().getLinkManager();
		
		//Delete nodes
		if(substractNodes != null) {
			
			List<Node> jointNodes = new ArrayList<Node>();
			Collections.sort(substractNodes, new NodeSort());
			
			Iterator<Node> it = substractNodes.iterator();
			while(it.hasNext()) {
				
				Node deleteNode = it.next();
				if(ResourceWrapper.getWrapper(RM.get(deleteNode.getR())).hasLinks()) {
					applyLinkedNodesSubstraction(deleteNode, deleteNode.getV(), jointNodes);
					substractedNum -= 1;
					it.remove();//remove joint nodes separately
				}
			}
			
			if(jointNodes.size() > 0) {
				
				List<Node> deletedJoints = NM.substractAll(jointNodes);
				List<Link> deletedNodeLinks = LM.getAll(deletedJoints);
				
				//Destroy children of all linked children of deleted parents
				ArrayList<Node> destroyedLinkNodes = new ArrayList<Node>();
				for(Link link : deletedNodeLinks) {
					Node deletedNodeLink = link.getChildNode().clone();
					deletedNodeLink.setV(Float.MAX_VALUE);
					destroyedLinkNodes.add(deletedNodeLink);
				}
				
				substractedNum += jointNodes.size();
				NM.substractAll(destroyedLinkNodes);
						
				NodeActivityManager.invalidate(jointNodes);
				NodeActivityManager.invalidate(destroyedLinkNodes);
				
				LM.removeAll(new ArrayList<Link>(deletedNodeLinks));
				deleteLinks += deletedNodeLinks.size();
				
			}
			
			NM.substractAll(substractNodes);
			NodeActivityManager.invalidate(substractNodes);
		}
		
		//Insert nodes
		if(insertNodes != null) {
			
			WorldManager SM = DAOFactory.get().getWorldManager();
			World activeSession = SM.get();
			
			Set<Integer> validResourceKeys = RM.getKeys();
			
			//Validate inserted nodes
			for(Node node : insertNodes) {
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
			}
			
			insertedNum = NM.addAll(insertNodes);
			NodeActivityManager.invalidate(insertNodes);
			
			if(insertNodes.size() != insertedNum) {
				throw new Exception("Database may be corrupt! Nodes where added but the response number does not equal the number of of nodes inserted. Inserted:" + insertNodes.size());
			}
		}
		
		
		logger.log(Level.INFO, "Substracted " + substractedNum + " " + deleteLinks + " links removed. Inserted " + insertedNum + " nodes. Time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
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
	private void applyLinkedNodesSubstraction(Node node, float substraction, List<Node> substractedLinkNodes) {
		
		substractedLinkNodes.add(node);
		
		for(Link link : LM.get(node)) {
			float weightSum = substraction * link.getWeight();
			
			if(substraction > 0) {
				Node childNode = link.getChildNode().clone();
				childNode.setV(weightSum);
				applyLinkedNodesSubstraction(childNode, weightSum, substractedLinkNodes);
			}
		}
	}
	
	private static class NodeSort implements Comparator<Node>{
		 
	    public int compare(Node o1, Node o2) {
	    	return (o1.getR() > o2.getR() ? -1 : (o1.getR() == o2.getR() ? 0 : 1));
	    }
	}
}

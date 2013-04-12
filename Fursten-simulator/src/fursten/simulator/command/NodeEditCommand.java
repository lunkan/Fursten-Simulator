package fursten.simulator.command;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.world.World;
import fursten.simulator.node.Node;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;

public class NodeEditCommand implements SimulatorCommand {
	
	protected static final Logger logger = Logger.getLogger(NodeEditCommand.class.getName());
	public static final String NAME = "UpdateNode";
	
	private List<Node> deleteNodes;
	private List<Node> insertNodes;
	
	public NodeEditCommand(List<Node> deleteNodes) {
		this.deleteNodes = deleteNodes;
	}

	public NodeEditCommand(List<Node> deleteNodes, List<Node> insertNodes) {
		this.deleteNodes = deleteNodes;
		this.insertNodes = insertNodes;
	}

	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
	
		long timeStampStart = System.currentTimeMillis();
		int deletedNum = 0;
		int insertedNum = 0;
		NodeManager NM = DAOFactory.get().getNodeManager();
		
		//Delete nodes
		if(deleteNodes != null)
			deletedNum = NM.delete(deleteNodes);
		
		//Insert nodes
		if(insertNodes != null) {
			
			WorldManager SM = DAOFactory.get().getWorldManager();
			World activeSession = SM.getActive();
			
			ResourceManager RM = DAOFactory.get().getResourceManager();
			Set<Integer> validResourceKeys = RM.getKeys();
			
			//Validate inserted nodes
			for(Node node : insertNodes) {
				if(!validResourceKeys.contains(node.getR()))
					throw new Exception("Failed adding node becouse resourceId: " + node.getR() + " does not have a resource attached to it. No nodes where added.");
				else if(!activeSession.getRect().contains(node.getX(), node.getY()))
					throw new Exception("Failed adding node becouse node: " + node.toString() + " is out of bounds. World bounds: " + activeSession.getRect().toString());
			}
			
			insertedNum = NM.insert(insertNodes);
			if(insertNodes.size() != insertedNum) {
				throw new Exception("Database may be corrupt! Nodes where added but the response number does not equal the number of of nodes inserted. Inserted:" + insertNodes.size());
			}
		}
		
		
		logger.log(Level.INFO, "Deleted " + deletedNum + " nodes, Inserted " + insertedNum + " nodes. Time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}
}

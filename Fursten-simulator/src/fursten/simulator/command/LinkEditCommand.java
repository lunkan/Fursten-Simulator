package fursten.simulator.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.link.Link;
import fursten.simulator.node.Node;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.LinkManager;
import fursten.simulator.persistent.mysql.DAOFactory;

public class LinkEditCommand implements SimulatorCommand {
	
	protected static final Logger logger = Logger.getLogger(NodeTransactionCommand.class.getName());
	public static final String NAME = "UpdateLinks";
	
	private List<Link> deleteLinks;
	private List<Link> insertLinks;
	
	public LinkEditCommand(List<Link> deleteLinks, List<Link> insertLinks) {
		this.deleteLinks = deleteLinks;
		this.insertLinks = insertLinks;
	}

	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
	
		long timeStampStart = System.currentTimeMillis();
		int deletedNum = 0;
		int insertedNum = 0;
		LinkManager LM = DAOFactory.get().getLinkManager();
		
		//Delete links
		if(deleteLinks != null) {
			deletedNum = LM.removeAll(deleteLinks).size();
		}
		
		//Insert links
		if(insertLinks != null) {
			
			//Validate that link may be created - links must be attached to nodes
			Set<Node> nodesJoints = new HashSet<Node>();
			for(Link insertLink : insertLinks) {
				nodesJoints.add(insertLink.getParentNode());
				nodesJoints.add(insertLink.getChildNode());
			}
			
			if(DAOManager.get().getNodeManager().containsAll(new ArrayList<Node>(nodesJoints))) {
				logger.log(Level.WARNING, "Can not add links becouse one or more node joint does not exist.");
				throw new Exception("Can not add links becouse one or more node joint does not exist.");
			}
			
			insertedNum = LM.addAll(insertLinks);
			if(insertLinks.size() != insertedNum) {
				throw new Exception("Database may be corrupt! Links where added but the response number does not equal the number of of links inserted. Inserted:" + insertLinks.size());
			}
		}
		
		logger.log(Level.INFO, "Deleted " + deletedNum + " nodes, Inserted " + insertedNum + " nodes. Time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}
}

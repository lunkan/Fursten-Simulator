package fursten.simulator.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.link.Link;
import fursten.simulator.node.Node;
import fursten.simulator.persistent.LinkManager;
import fursten.simulator.persistent.mysql.DAOFactory;

public class LinkGetCommand implements SimulatorCommand {

	protected static final Logger logger = Logger.getLogger(NodeGetCommand.class.getName());
	public static final String NAME = "GetLinks";
	
	private boolean recursive;
	private List<Node> linkNodes;
	
	public LinkGetCommand(List<Node> linkNodes, boolean recursive){
		
		this.recursive = recursive;
		this.linkNodes = linkNodes;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		long timeStampStart = System.currentTimeMillis();
		LinkManager LM = DAOFactory.get().getLinkManager();
		Set<Link> links = new HashSet<Link>();
		
		for(Node linkNode : linkNodes) {
			if(recursive) {
				List<Link> recursiveLinks = LM.get(linkNode);
				
				while(recursiveLinks.size() > 0) {
					
					links.addAll(recursiveLinks);
					Iterator<Link> it = recursiveLinks.iterator();
					
					while(it.hasNext()) {
						Link nextLink = it.next();
						recursiveLinks.addAll(LM.get(nextLink.getChildNode()));
						it.remove();
					}
				}
			}
			else {
				links.addAll(LM.get(linkNode));
			}
		}
		
		logger.log(Level.INFO, "Fetched links num:" + links.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return new ArrayList<Link>(links);
	}
}

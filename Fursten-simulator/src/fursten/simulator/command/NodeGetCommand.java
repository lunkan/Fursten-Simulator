package fursten.simulator.command;

import java.awt.Rectangle;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.node.Node;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;

public class NodeGetCommand implements SimulatorCommand {

	protected static final Logger logger = Logger.getLogger(NodeGetCommand.class.getName());
	public static final String NAME = "GetNodes";
	
	private Rectangle rect;
	private Set<Integer> filter;
	
	public NodeGetCommand(Rectangle rect, Set<Integer> filter){
		
		this.rect = rect;
		this.filter = filter;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
		
		long timeStampStart = System.currentTimeMillis();
		NodeManager NM = DAOFactory.get().getNodeManager();
		
		if(filter == null) {
			ResourceManager RM = DAOFactory.get().getResourceManager();
			filter = RM.getKeys();
		}
		
		List<Node> nodes = NM.get(rect, filter);
		logger.log(Level.INFO, "Fetched nodes num:" + nodes.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return nodes;
	}
}

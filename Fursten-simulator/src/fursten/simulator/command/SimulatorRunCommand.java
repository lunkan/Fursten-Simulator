package fursten.simulator.command;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.SimulatorSettings;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeStabilityCalculator;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.SessionManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;
import fursten.simulator.session.Session;

public class SimulatorRunCommand implements SimulatorCommand {
	
	protected static final Logger logger = Logger.getLogger(SimulatorRunCommand.class.getName());
	public static final String NAME = "Run";
	
	private Random rand;
	private Rectangle rect;
	private NodeStabilityCalculator nodeMath;
	private ResourceManager RM;
	private Session activeSession;
	
	public SimulatorRunCommand(Rectangle rect){
		this.rect = rect;
	}
	
	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {

		long timeStampStart = System.currentTimeMillis();
		rand = new Random();
		
		SessionManager SM = DAOFactory.get().getSessionManager();
		activeSession = SM.getActive();
		
		NodeManager NM = DAOFactory.get().getNodeManager();
		RM = DAOFactory.get().getResourceManager();
		Set<Integer> resourceKeys = RM.getKeys();
		nodeMath = NodeStabilityCalculator.getInstance();
		
		List<Node> removedNodes = new ArrayList<Node>();
		List<Node> addedNodes = new ArrayList<Node>();
		
		for(Integer resourceKey : resourceKeys) {
			
			Resource resource = RM.get(resourceKey);
			
			if(!resource.isStatic()) {
				
				for(Node node : NM.get(rect, resourceKey)) {
					
					float stability = nodeMath.calculateStability(node.getX(), node.getY(), resource);
					if(stability < 1.0f)  {
						removedNodes.add(node);
			    	}
					else {
						//get offspring that has the chans to get born - if ref = 0 no child was born
						int offspringKey = resource.getOffspringByValue(rand.nextFloat());
						if(offspringKey != 0) {
							Node spore = runSpore(node.getX(), node.getY(), offspringKey);
							if(spore != null)
								addedNodes.add(spore);
						}
			    	}
				}
			}
		}
		
		NM.delete(removedNodes);
		NM.insert(addedNodes);
		
		logger.log(Level.INFO, "Deleted " + removedNodes.size() + " & Added " + addedNodes.size() + ". time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}

	private Node runSpore(int x, int y, int r) {

		Resource resource = RM.get(r);
		
		int seedX = rand.nextInt(NodeStabilityCalculator.NODE_RADIUS*8)-(NodeStabilityCalculator.NODE_RADIUS*4);
		int seedY = rand.nextInt(NodeStabilityCalculator.NODE_RADIUS*8)-(NodeStabilityCalculator.NODE_RADIUS*4);
		
		Node spore = new Node(r);
		spore.setX(x + seedX);
		spore.setY(y + seedY);
		
		//Check that no node is out of world bounds
		if(!activeSession.getRect().contains(spore.getX(), spore.getY()))
			return null;
		
		float stability = nodeMath.calculateStability(spore.getX(), spore.getY(), resource);
		stability = nodeMath.normalizeStability(stability, resource.getThreshold());
		
		if(stability > rand.nextFloat())
    		return spore;
    	else
    		return null;
	}
}

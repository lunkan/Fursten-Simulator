package fursten.simulator.command;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.joint.Joint;
import fursten.simulator.node.NodePoint;
import fursten.simulator.persistent.JointManager;
import fursten.simulator.persistent.mysql.DAOFactory;

public class JointEditCommand implements SimulatorCommand {
	
	protected static final Logger logger = Logger.getLogger(NodeTransactionCommand.class.getName());
	public static final String NAME = "EditJoints";
	
	private List<NodePoint> deleteJoints;
	private List<Joint> putJoints;
	private boolean deleteRecursive;
	
	public JointEditCommand(List<NodePoint> deleteJoints, List<Joint> putJoints, boolean deleteRecursive) {
		this.deleteJoints = deleteJoints;
		this.putJoints = putJoints;
		this.deleteRecursive = deleteRecursive;
	}

	public String getName() {
		return NAME;
	}
	
	public Object execute() throws Exception {
	
		long timeStampStart = System.currentTimeMillis();
		JointManager LM = DAOFactory.get().getLinkManager();
		
		//Delete joints
		if(deleteJoints != null)
			LM.removeAll(deleteJoints).size();
		
		//Insert joints
		if(putJoints != null) {
			LM.putAll(putJoints);
		}
		
		logger.log(Level.INFO, "Deleted " + deleteJoints.size() + " nodes, Inserted " + putJoints.size() + " nodes. Time: " + (System.currentTimeMillis() - timeStampStart) + "ms");
		return null;
	}
}

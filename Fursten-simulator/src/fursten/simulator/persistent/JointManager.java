package fursten.simulator.persistent;

import java.util.List;

import fursten.simulator.joint.Joint;
import fursten.simulator.node.NodePoint;

public interface JointManager extends Persistable, PersistantManager {

	public void put(Joint... joints);
	public void putAll(List<Joint> joints);
	
	public List<Joint> remove(NodePoint... nodePoints);
	public List<Joint> removeAll(List<NodePoint> nodePoints);
	
	public Joint get(NodePoint nodePoint);
	public List<Joint> getAll(List<NodePoint> nodePoints);
}

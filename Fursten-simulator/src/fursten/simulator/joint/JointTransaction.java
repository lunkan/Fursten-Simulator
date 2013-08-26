package fursten.simulator.joint;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import fursten.simulator.node.NodePoint;

@XmlRootElement
public class JointTransaction {
	
	private ArrayList<NodePoint> deleteJoints;
	private ArrayList<Joint> injectJoints;
	private boolean deleteRecursive = false;
	
	public JointTransaction() {
	}
	
	public boolean getDeleteRecursive() {
		return deleteRecursive;
	}

	public void setDeleteRecursive(boolean deleteRecursive) {
		this.deleteRecursive = deleteRecursive;
	}
	
	public ArrayList<NodePoint> getDeleteJoints() {
		return deleteJoints;
	}

	public void setDeleteJoints(ArrayList<NodePoint> deletejoints) {
		this.deleteJoints = deletejoints;
	}
	
	public ArrayList<Joint> getInjectJoints() {
		return injectJoints;
	}

	public void setInjectJoints(ArrayList<Joint> injectJoints) {
		this.injectJoints = injectJoints;
	}
}
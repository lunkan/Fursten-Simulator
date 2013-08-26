package fursten.simulator.joint;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JointCollection {
	
	private ArrayList<Joint> joints;
	
	public JointCollection() {
	}
	
	public ArrayList<Joint> getJoints() {
		return joints;
	}

	public void setJoints(ArrayList<Joint> joints) {
		this.joints = joints;
	}
	
	public void add(Joint joint) {
		if(joints == null)
			joints = new ArrayList<Joint>();
		
		joints.add(joint);
	}
}


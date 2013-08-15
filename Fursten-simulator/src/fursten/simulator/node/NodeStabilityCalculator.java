package fursten.simulator.node;

import java.awt.Rectangle;
import java.util.Set;

import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceWrapper;

public class NodeStabilityCalculator {
	
	public static final int NODE_RADIUS = 1000;//1 km = 1000 m
	
	private NodeManager NM;
	private Rectangle rect;
	
	private static NodeStabilityCalculator instance = new NodeStabilityCalculator();
	private static boolean clean = false;
	
	private NodeStabilityCalculator() {
		NM = DAOFactory.get().getNodeManager();
		rect = new Rectangle();
	}
	
	public static NodeStabilityCalculator getInstance() {
		
		//If changes has been made to resources - NodeStabilityCalculator is no longer valid
		if(clean) {
			instance = new NodeStabilityCalculator();
			clean = false;
		}
		
        return instance;
    }
	
	public static void clean() {
		clean = true;
    }
	
	public float calculateStability(int x, int y, ResourceWrapper resource) {
			
		if(resource.isStatic())
			return 0.0f;
		
		//set bounds
		rect.setBounds(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS*2, NODE_RADIUS*2);
		
		float stability = Float.MAX_VALUE;
		
		//Loop all weightGroups - bottleneck approach - lowest group value determines total
		for(int weightGroup = 0; weightGroup < resource.numGroups(); weightGroup++) {
			
			float groupStability = 0f;
			
			//Loop
			Set<Integer> dependencyKeys = resource.getDependencies(weightGroup);
			for(int dependencyKey : dependencyKeys) {
			
				//Fetch and loop all dependencies = dependency and it's children.
				Set<Integer> allDependencies = ResourceKeyManager.getChildren(dependencyKey);
				allDependencies.add(dependencyKey);
				for(Node neighbor : NM.get(rect, allDependencies)){
					
					double impact = 1 - (Math.sqrt(Math.pow(x-neighbor.getX(),2) + Math.pow(y-neighbor.getY(),2)) / NodeStabilityCalculator.NODE_RADIUS);
					if(impact > 0) {
						groupStability += resource.getWeight(weightGroup, dependencyKey) * impact * neighbor.getV();
					}
				}
			
			}
			
			stability = Math.min(stability, groupStability);
		}
		
		//Self penelty is always self * -1. Acts as a base value
		//Nodes must compensate with impact to be in balance -> impact = 0
		for(Node selfNode : NM.get(rect, resource.getKey())) {
			
			int distance = (int) (Math.sqrt(Math.pow(x-selfNode.getX(),2) + Math.pow(y-selfNode.getY(),2)));
			if(distance < NODE_RADIUS) {
				
				//Use a sigmoid function to get better distribution for nodes of same type
				float sigX= 5.0f + ((float)(distance)/(float)NODE_RADIUS)*-10.0f;
				float sigmoidImpact = 1.0f / (1.0f + (float)Math.exp(-sigX));
				stability -= sigmoidImpact * selfNode.getV();//
				
				//Is it good to make stability proportional to strength?
				//Below multiplier to stability will force nodes to stay away from eachother - always
				//- no not good. Nodes must be able to stay tighter in more dense areas.
				//* Math.max(1, stability);
				
				//float impact = (NODE_RADIUS - (float)distance) / (float)NODE_RADIUS;
				//stability -= impact * selfNode.getV();
			}
		}
		
		return stability;
	}
}
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
	
	public float calculateStability(int x, int y, ResourceWrapper resource, boolean ignoreSelf) {
		
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
					
					int distance = (int) (Math.sqrt(Math.pow(x-neighbor.getX(),2) + Math.pow(y-neighbor.getY(),2)));
					if(distance < NODE_RADIUS) {
						float impact = (NODE_RADIUS - (float)distance) / (float)NODE_RADIUS;
						groupStability += resource.getWeight(weightGroup, dependencyKey) * impact;
					}
				}
			
			}
			
			stability = Math.min(stability, groupStability);
		}
		
		//Negative impact from neighbors of same type
		//Impact from self is always = -1
		for(Node neighbor : NM.get(rect, resource.getKey())) {
			
			int distance = (int) (Math.sqrt(Math.pow(x-neighbor.getX(),2) + Math.pow(y-neighbor.getY(),2)));
			if(distance == 0) {
				if(ignoreSelf) {
					stability -= 1;
				}
			}
			else if(distance < NODE_RADIUS) {
				stability -= (NODE_RADIUS - (float)distance) / (float)NODE_RADIUS;
			}
		}
		
		return stability;
	}
}
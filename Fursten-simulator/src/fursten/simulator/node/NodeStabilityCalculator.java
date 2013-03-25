package fursten.simulator.node;

import java.awt.Rectangle;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceWrapper;

public class NodeStabilityCalculator {
	
	public static final int NODE_RADIUS = 1000;//1 km = 1000 m
	
	private NodeManager NM;
	private Rectangle rect;
	
	private ResourceKeyManager RMK;
	private ResourceManager RM;
	
	private DependencyCash dependencyCache;
	
	private static NodeStabilityCalculator instance = new NodeStabilityCalculator();
	private static boolean clean = false;
	
	private NodeStabilityCalculator() {
		NM = DAOFactory.get().getNodeManager();
		rect = new Rectangle();
		
		RM = DAOFactory.get().getResourceManager();
		RMK = new ResourceKeyManager(RM.getKeys());
		
		dependencyCache = new DependencyCash();
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
	
	//defines start of sigmoid curve. Values below threshold is 0 and above 1
	public float normalizeStability(float stability, float threshold) {
		
		//sharpness of sigmoid function. If 1 -> 0=0 & 0.5=0.5 & 1=1
		float sharpness = 1f;
		
		//x input coordinate to mesure value
		stability -= threshold;
		
		//scale value which corresponds to 1.0 if x is normalized
		float normV = 1f - threshold;
		
		stability =(stability / normV * 2 - 1) * 5 * sharpness;
		return 1.0f / (1.0f + (float)Math.exp( -stability ));
	}
	
	@SuppressWarnings("unchecked")
	public float calculateStability(int x, int y, ResourceWrapper resource) {
		
		//set bounds
		rect.setBounds(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS*2, NODE_RADIUS*2);
		
		float stability = Float.MAX_VALUE;
		for(int weightGroup = 0; weightGroup < resource.numGroups(); weightGroup++) {
			
			float groupStability = 0f;
			
			//Retrive dependency from cache...
			HashSet<Integer> dependencyKeys = dependencyCache.get(resource.getKey(), weightGroup);
			if(dependencyKeys == null) {
				
				dependencyKeys = new HashSet<Integer>();
				Iterator<Integer> it = resource.getDependencies(weightGroup).iterator();
				while(it.hasNext()) {
					int dependencyKey = it.next();
					Set<Integer> matchKeys = RMK.getChildren(dependencyKey);
					matchKeys.add(dependencyKey);
					dependencyKeys.addAll(matchKeys);
				}
				
				//remove self if exist;
				dependencyKeys.remove(resource.getKey());
				dependencyCache.set(resource.getKey(), weightGroup, dependencyKeys);
			}
			
			for(Node neighbor : NM.get(rect, dependencyKeys)){
				
				int distance = (int) (Math.sqrt(Math.pow(x-neighbor.getX(),2) + Math.pow(y-neighbor.getY(),2)));
				float impact = (NODE_RADIUS - (float)distance) / (float)NODE_RADIUS;
					
				float value = 0f;
				for(int dependencyKey : resource.getDependencies(weightGroup)) {
					
					BigInteger selectBigKey = BigInteger.valueOf((long)dependencyKey);
					int shift = selectBigKey.getLowestSetBit();
					
					if((dependencyKey ^ neighbor.getR()) >>> shift == 0) {
						value += resource.getWeight(weightGroup, dependencyKey) * impact;
					}
				}
					
				groupStability += value;
			}
			
			stability = Math.min(stability, groupStability);
		}
		
		//Negative impact from neighbor of same type
		for(Node neighbor : NM.get(rect, resource.getKey())) {
			
			int distance = (int) (Math.sqrt(Math.pow(x-neighbor.getX(),2) + Math.pow(y-neighbor.getY(),2)));
			float impact = (NODE_RADIUS - (float)distance) / (float)NODE_RADIUS;
			
			if(impact > 0 && distance != 0) {	
				stability -= impact;
			}
		}
		
		return stability;
	}
	
	class DependencyCash {
		
		private HashMap<Integer, HashMap<Integer, HashSet<Integer>>> dependencyMap;
		
		public DependencyCash() {
			dependencyMap = new HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();
		}
		
		public HashSet<Integer> get(int resourceKey, int weightGroup) {
			
			try {
				return dependencyMap.get(resourceKey).get(weightGroup);
			}
			catch(Exception e) {
				return null;
			}
		}
		
		public void set(int resourceKey, int weightGroup, HashSet<Integer> dependencyKeys) {
			
			if(!dependencyMap.containsKey(resourceKey))
				dependencyMap.put(resourceKey, new HashMap<Integer, HashSet<Integer>>());
			
			dependencyMap.get(resourceKey).put(weightGroup, dependencyKeys);
		}
		
		public void clear() {
			dependencyMap.clear();
		}
	}
}
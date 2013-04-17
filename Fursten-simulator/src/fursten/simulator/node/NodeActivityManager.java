package fursten.simulator.node;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.ResourceDependencyManager;
import fursten.simulator.world.World;

public class NodeActivityManager {

	private static NodeActivityManager instance; 
	private static final int GEOCELL_DIV = 4;//65536
	private HashMap<Integer, Set<Integer>> activityMap;
	private int startTick;
	
	private NodeActivityManager() {
		activityMap = new HashMap<Integer, Set<Integer>>();
		WorldManager SM = DAOFactory.get().getWorldManager();
		World world = SM.getActive();
		startTick = world.getTick();
	}
	
	private static NodeActivityManager getInstance() {
		if(instance == null)
			instance = new NodeActivityManager();
		
		return instance;
    }
	
	public static void invalidate(List<Node> nodes) {
		getInstance()._invalidate(nodes);
	}
	
	public void _invalidate(List<Node> nodes) {
		
		for(Node node : nodes){
			
			int geoHash = toGeoHash(node.getX(), node.getY());
			
			//Find and add all resources that has a dependency of the updated resource
			Set<Integer> dependentResources = ResourceDependencyManager.getDependents(node.getR());
			for(Integer dependentResource : dependentResources) {
				
				if(!activityMap.containsKey(dependentResource))
					activityMap.put(dependentResource, new HashSet<Integer>());
				
				activityMap.get(dependentResource).add(geoHash);
			}
			
			//activityMap.get(node.getR()).add(geoHash);
			System.out.println("Invalidate Node: " + node.toString() + " #" + geoHash);
		}
	}
	
	public static Set<Integer> getInvalidResources(int tick) {
		return getInstance()._getInvalidResources(tick);
	}
	
	public Set<Integer> _getInvalidResources(int tick) {
		
		if(tick > startTick)
			return activityMap.keySet();
		
		//First tick after clear all resources are invalid
		ResourceManager RM = DAOFactory.get().getResourceManager();
		return RM.getKeys();
	}
	
	public static List<Rectangle> getInvalidRectByResourceKey(int resourceKey, int tick) {
		return getInstance()._getInvalidRectByResourceKey(resourceKey, tick);
	}
	
	public List<Rectangle> _getInvalidRectByResourceKey(int resourceKey, int tick) {
		
		List<Rectangle> invalidRegions = new ArrayList<Rectangle>();
		
		//First tick after clear all regions are invalid
		if(tick <= startTick) {
			WorldManager SM = DAOFactory.get().getWorldManager();
			World world = SM.getActive();
			Rectangle worldRect = world.getRect();
			invalidRegions.add(worldRect);
			return invalidRegions;
		}
			
		Set<Integer> geoHashSet = activityMap.get(resourceKey);
		if(geoHashSet != null) {
		
			for(Integer geoHash : geoHashSet) {
				Rectangle rect = geoHashToRect(geoHash);
				invalidRegions.add(rect);
			}
		}
		
		return invalidRegions;
	}
	
	/**
	 * Removes latest cache.
	 */
	public static void clean() {
		getInstance()._clean();
	}
	
	public void _clean() {
		activityMap.clear();
	}
	
	/**
	 * Resets to start state - first round all is invalid
	 */
	public static void clear() {
		instance = new NodeActivityManager();
	}
	
	private int toGeoHash(int x, int y) {
		
		/*
		 * Hash to geo-point-cell
		 * |0|1|2|
		 * |3|4|5|
		 * |6|7|8|
		 * etc
		 */
		int nX = x + Integer.MAX_VALUE/2;//Normalize so upper left is (0,0)
		int nY = y + Integer.MAX_VALUE/2;//Normalize so upper left is (0,0)
		nX /= (Integer.MAX_VALUE / GEOCELL_DIV);
		nY /= (Integer.MAX_VALUE / GEOCELL_DIV);
		int geoHash = (nY * GEOCELL_DIV) + nX;
		
		return geoHash;
	}
	
	private Rectangle geoHashToRect(int geoHash) {
		
		int nX = geoHash % GEOCELL_DIV;
		int nY = (geoHash - nX) / GEOCELL_DIV;
		int cellSize = (Integer.MAX_VALUE / GEOCELL_DIV);
		return new Rectangle(nX*cellSize, nY*cellSize, cellSize, cellSize);
	}
}

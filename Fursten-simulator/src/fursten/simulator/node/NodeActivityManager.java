package fursten.simulator.node;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fursten.simulator.Settings;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.ResourceDependencyManager;
import fursten.simulator.world.World;

public class NodeActivityManager {

	private static final int MAX_SIZE = (int)Math.pow(2, 30);
	
	private static NodeActivityManager instance; 
	
	private int GEOCELL_DIV;
	private int CELL_SIZE;
	
	private HashMap<Integer, Set<Long>> activityMap;
	private int startTick;
	
	private NodeActivityManager() {
		
		int geocellBase = Settings.getInstance().getSimulatorSettings().getGeocellBase();
		CELL_SIZE = (int)Math.pow(2, geocellBase);
		GEOCELL_DIV = MAX_SIZE / CELL_SIZE;
		
		activityMap = new HashMap<Integer, Set<Long>>();
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
			
			long geoHash = toGeoHash(node.getX(), node.getY());
			
			//Find and add all resources that has a dependency of the updated resource
			Set<Integer> dependentResources = ResourceDependencyManager.getDependents(node.getR());
			for(Integer dependentResource : dependentResources) {
				
				if(!activityMap.containsKey(dependentResource))
					activityMap.put(dependentResource, new HashSet<Long>());
				
				activityMap.get(dependentResource).add(geoHash);
			}
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
			
		Set<Long> geoHashSet = activityMap.get(resourceKey);
		if(geoHashSet != null) {
		
			for(Long geoHash : geoHashSet) {
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
	
	private long toGeoHash(int x, int y) {
		
		/*
		 * Hash to geo-point-cell
		 * |0|1|2|
		 * |3|4|5|
		 * |6|7|8|
		 * etc
		 */
		long nX = x + MAX_SIZE/2;//Normalize so upper left is (0,0)
		long nY = y + MAX_SIZE/2;//Normalize so upper left is (0,0)
		nX = (long)Math.floor(nX / CELL_SIZE);
		nY = (long)Math.floor(nY / CELL_SIZE);
		long geoHash = (nY * GEOCELL_DIV) + nX;
		return geoHash;
	}
	
	private Rectangle geoHashToRect(long geoHash) {
		
		long nX = geoHash % GEOCELL_DIV;
		long nY = (geoHash - nX) / GEOCELL_DIV;
		nX = (nX * CELL_SIZE) - (MAX_SIZE/2); 
		nY = (nY * CELL_SIZE) - (MAX_SIZE/2);
		return new Rectangle((int)nX, (int)nY, CELL_SIZE, CELL_SIZE);
	}
	
	//Todo: init activeIterator 
	//public static class ActiveRectIterator implements Iterator<Rectangle> {
	//}
}

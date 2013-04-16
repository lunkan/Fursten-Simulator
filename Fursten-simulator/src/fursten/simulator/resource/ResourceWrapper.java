package fursten.simulator.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fursten.simulator.Settings;
import fursten.simulator.resource.Resource.Offspring;
import fursten.simulator.resource.Resource.Weight;
import fursten.simulator.resource.Resource.WeightGroup;

public class ResourceWrapper {
	
	private static final HashMap<Resource, ResourceWrapper> wrapperPool = new HashMap<Resource, ResourceWrapper>();
	private static long cacheTimer = 0;
	private static final long CACHE_EXPIRE = 10000;
	
	private int updateRatio = -1;
	private ArrayList<Offspring> offsprings;
	private ArrayList<HashMap<Integer, Float>> weightMap;
	private Resource resource;
	
	public static ResourceWrapper getWrapper(Resource resource) throws Exception{
		
		//Clear cache every 100 sec
		if((cacheTimer + CACHE_EXPIRE) < System.currentTimeMillis()) {
			wrapperPool.clear();
			cacheTimer = System.currentTimeMillis();
		}
		
		ResourceWrapper wrapper = wrapperPool.get(resource);
		if(wrapper == null) {
			wrapper = new ResourceWrapper(resource);
			wrapperPool.put(resource, wrapper);
		}
			
		return wrapper;
	}
	
	private ResourceWrapper(Resource resource) throws Exception {
		
		if(resource == null)
			throw new Exception("Resource must not be null");
		
		this.resource = resource;
	}
	
	public Resource getResource() {
		return this.resource;
	}
	
	public ResourceWrapper setResource(Resource resource) throws Exception {
		
		if(resource == null)
			throw new Exception("Resource must not be null");
			
		this.resource = resource;
		offsprings = null;
		weightMap = null;
		return this;
	}
	
	public int getKey() {
		return this.resource.getKey();
	}
	
	public String getName() {
		return this.resource.getName();
	}
	
	public float getThreshold() {
		return this.resource.getThreshold();
	}
	
	public boolean getIsLocked() {
		return this.resource.getIsLocked();
	}
	
	public float getMortality() {
		return this.resource.getMortality();
	}
	
	public ArrayList<Offspring> getOffsprings() {
	
		if(offsprings != null)
			return offsprings;
			
		offsprings = new ArrayList<Offspring>(); 
		for(Offspring offspring : this.resource.getOffsprings()) {
			offspring.setResource(this.resource.getKey());
			offsprings.add(offspring);
		}
		for(Offspring mutaion : this.resource.getMutations()) {
			offsprings.add(mutaion);
		}
		
		return offsprings;
	}
	
	public ArrayList<HashMap<Integer, Float>> getWeightMap() {
		
		if(weightMap != null)
			return weightMap;
		
		weightMap = new ArrayList<HashMap<Integer, Float>>();
		if(this.resource.getWeightGroups() != null) {
			for(int i=0; i < this.resource.getWeightGroups().size(); i++) {
				
				weightMap.add(new HashMap<Integer, Float>());
				for(Weight weight : this.resource.getWeightGroups().get(i).getWeights()) {
					weightMap.get(i).put(weight.getResource(), weight.getValue());
				}
			}
		}
		
		return weightMap;
	}

	public float getWeight(int group, int key) {
		return getWeightMap().get(group).get(key);
	}
	
	public int numGroups() {
		return getWeightMap().size();
	}
	
	/**
	 * If there is a point of calculate the resource as run
	 * If it may have offsprings, is not locked and imoortal 
	 * @return
	 */
	public boolean isStatic() {
		if(resource.getIsLocked())
			return true;
		else if(!isBreedable() && resource.getMortality() == 0)
			return true;
		else
			return false;
	}
	
	public boolean isDependent() {
		return (getWeightMap().size() > 0);
	}
	
	public boolean isBreedable() {
		return (getOffsprings().size() > 0);
	}
	
	/**
	 * Calculate the updateRate of the resource by measuring the lowest acceptable interval between updates.
	 * Lowest acceptable interval = most frequent update (mortality or any offspring) divided by Simulator precision.
	 * @return
	 */
	public int getUpdateintervall() {
		
		if(updateRatio == -1){
			
			if(isStatic()) {
				updateRatio = 0;
			}
			else {
				float mostFrequent = resource.getMortality();
				for(Offspring offspring : getOffsprings()) {
					mostFrequent = Math.max(mostFrequent, offspring.getRatio());
				}
				
				if(mostFrequent == 0) {
					updateRatio = 0;
				}
				else {
					mostFrequent *= Settings.getInstance().getSimulatorSettings().getUpdatePrecision();
					updateRatio = Math.round(1/mostFrequent);
					
				}
			}
		}
		
		return updateRatio;
	}
	
	/**
	 * Adjusted probabilty as a function of updateInterval.
	 * The outcome of a updateInterval-adjusted random-event is base-probability * ratioBase
	 * Remember - this is not 100% equal calculate every event for every tick - we do shortcuts here
	 * @return
	 */
	public float adjustByInterval(float value) {
		return (float)(1 - Math.pow((1-value), getUpdateintervall()));
	}
	
	public Set<Integer> getDependencies() {
		
		HashSet<Integer> DependencyKeys = new HashSet<Integer>();
		for(int i = 0; i < getWeightMap().size(); i++) {
			DependencyKeys.addAll(getDependencies(i));
		}
		
		return DependencyKeys;
	}
	
	public Set<Integer> getDependencies(int group) {
		return getWeightMap().get(group).keySet();
	}
	
	public boolean isValid() {
		
		if(resource.getKey() == 0)
			return false;
		else if (resource.getName() == null || resource.getName().equals("")) {
			return false;
		}
		else if (resource.getThreshold() >= 1 || resource.getThreshold() < 0.1) {
			return false;
		}
		else {
			return true;
		}
	}
}

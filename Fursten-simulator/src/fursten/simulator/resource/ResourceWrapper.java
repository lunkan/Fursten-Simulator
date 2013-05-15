package fursten.simulator.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fursten.simulator.Settings;
import fursten.simulator.resource.Resource.Offspring;
import fursten.simulator.resource.Resource.Weight;

public class ResourceWrapper {
	
	private static final HashMap<Resource, ResourceWrapper> wrapperPool = new HashMap<Resource, ResourceWrapper>();
	
	private int updateRatio = -1;
	private float adjustedMortality = -1;
	private Set<Integer> dependencyKeys;
	private ArrayList<Offspring> offsprings;
	private ArrayList<HashMap<Integer, Float>> weightMap;
	private Resource resource;
	private int isCloning;
	
	public static ResourceWrapper getWrapper(Resource resource) throws Exception{
		
		ResourceWrapper wrapper = wrapperPool.get(resource);
		if(wrapper == null) {
			wrapper = new ResourceWrapper(resource);
			wrapperPool.put(resource, wrapper);
		}
			
		return wrapper;
	}
	
	public static void clear() {
		wrapperPool.clear();
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
		if(adjustedMortality >= 0)
			return adjustedMortality;
		
		adjustedMortality = adjustByInterval(this.resource.getMortality());
		return adjustedMortality;
	}
	
	public ArrayList<Offspring> getOffsprings() {
	
		if(offsprings != null)
			return offsprings;
		
		offsprings = new ArrayList<Offspring>();
		for(Offspring mutation : this.resource.getMutations()) {
			Offspring mutationClone = mutation.clone();
			float adjustedRation = adjustByInterval(mutationClone.getRatio());
			mutationClone.setRatio(adjustedRation);
			offsprings.add(mutationClone);
		}
		for(Offspring offspring : this.resource.getOffsprings()) {
			Offspring offspringClone = offspring.clone();
			offspringClone.setResource(this.resource.getKey());
			float adjustedRation = adjustByInterval(offspringClone.getRatio());
			offspringClone.setRatio(adjustedRation);
			offsprings.add(offspringClone);
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
	 * Resource is static If - has no offsprings and is immortal, is locked. 
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
	
	/**
	 * Resource is dependent if it has dependencies
	 * @return
	 */
	public boolean isDependent() {
		if(resource.getIsLocked())
			return false;
		
		return (getWeightMap().size() > 0);
	}
	
	/**
	 * Resource is breedable if it produce offsprings
	 * @return
	 */
	public boolean isBreedable() {
		return (getOffsprings().size() > 0);
	}
	
	/**
	 * Resource is cloning if it has self as on of it's offsprings
	 * @return
	 */
	public boolean isCloning() {
		if(isCloning > 0)
			return true;
		else if(isCloning < 0)
			return false;
		
		for(Offspring offspring : getOffsprings()) {
			if(offspring.getResource() == resource.getKey()) {
				isCloning = 1;
				return true;
			}
		}
		
		isCloning = 0;
		return false;
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
				float mostFrequent = this.resource.getMortality();
				for(Offspring mutation : this.resource.getMutations()) {
					mostFrequent = Math.max(mostFrequent, mutation.getRatio());
				}
				for(Offspring offspring : this.resource.getOffsprings()) {
					mostFrequent = Math.max(mostFrequent, offspring.getRatio());
				}
				
				if(mostFrequent == 0) {
					updateRatio = 0;
				}
				else {
					mostFrequent *= Settings.getInstance().getSimulatorSettings().getUpdatePrecision();
					updateRatio = (int)Math.ceil(1/mostFrequent);//updateRatio may not be 0 (fixed issue) changed from round
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
	private float adjustByInterval(float value) {
		return (float)(1 - Math.pow((1-value), getUpdateintervall()));
	}
	
	public Set<Integer> getDependencies() {
		
		if(dependencyKeys == null) {
			
			dependencyKeys = new HashSet<Integer>();
			
			if(isDependent()) {
				for(int i = 0; i < getWeightMap().size(); i++) {
					dependencyKeys.addAll(getDependencies(i));
				}
				
				//Add self (always dependent)
				dependencyKeys.add(resource.getKey());
			}
		}
		
		return dependencyKeys;
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

package fursten.simulator.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fursten.simulator.Settings;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource.Offspring;
import fursten.simulator.resource.Resource.Weight;

public class ResourceWrapper {
	
	private static final HashMap<Integer, ResourceWrapper> wrapperPool = new HashMap<Integer, ResourceWrapper>();
	private static ResourceWrapper currentWrapper;
	
	private int updateRatio;
	private float adjustedMortality;
	private ArrayList<Offspring> offsprings;
	private ArrayList<HashMap<Integer, Float>> weightMap;
	private Boolean hasLinks;
	private Resource resource;
	
	/*public static ResourceWrapper getWrapper(Resource resource) {
		
		if(currentWrapper != null) {
			if(currentWrapper.getKey() == resource.getKey()) {
				return currentWrapper;
			}
		}
		
		ResourceWrapper wrapper = wrapperPool.get(resource);
		if(wrapper == null) {
			wrapper = new ResourceWrapper(resource);
			wrapperPool.put(resource.getKey(), wrapper);
		}
		
		currentWrapper = wrapper;
		return wrapper;
	}*/
	
	public static ResourceWrapper getWrapper(int resourceKey) {
		
		if(currentWrapper != null) {
			if(currentWrapper.getKey() == resourceKey) {
				return currentWrapper;
			}
		}
		
		ResourceWrapper wrapper = wrapperPool.get(resourceKey);
		if(wrapper == null) {
			ResourceManager RM = DAOFactory.get().getResourceManager();
			Resource resource = RM.get(resourceKey);
			
			if(resource == null)
				return null;
				
			wrapper = new ResourceWrapper(resource);
			wrapperPool.put(resourceKey, wrapper);
		}
		
		currentWrapper = wrapper;
		return wrapper;
	}
	
	public static void clear() {
		currentWrapper = null;
		wrapperPool.clear();
	}
	
	private ResourceWrapper(Resource resource) {
		
		this.resource = resource;
		
		//UpdateRatio
		float mostFrequent = this.resource.getMortality();
		
		if(this.resource.getMutations() != null) {
			for(Offspring mutation : this.resource.getMutations()) {
				mostFrequent = Math.max(mostFrequent, mutation.getRatio());
			}
		}
		
		if(this.resource.getOffsprings() != null) {
			for(Offspring offspring : this.resource.getOffsprings()) {
				mostFrequent = Math.max(mostFrequent, offspring.getRatio());
			}
		}
		
		if(mostFrequent > 0) {
			mostFrequent *= Settings.getInstance().getSimulatorSettings().getUpdatePrecision();
			updateRatio = (int)Math.ceil(1/mostFrequent);//updateRatio may not be 0 (fixed issue) changed from round
		}
		
		//Mortality
		adjustedMortality = adjustByInterval(this.resource.getMortality());
	}
	
	/**
	 * Calculate the updateRate of the resource by measuring the lowest acceptable interval between updates.
	 * Lowest acceptable interval = most frequent update (mortality or any offspring) divided by Simulator precision.
	 * @return
	 */
	public int getUpdateintervall() {
		return updateRatio;
	}
	
	/**
	 * Adjusted probabilty as a function of updateInterval.
	 * The outcome of a updateInterval-adjusted random-event is base-probability * ratioBase
	 * Remember - this is not 100% equal calculate every event for every tick - we do shortcuts here
	 * @return
	 */
	private float adjustByInterval(float value) {
		return (float)(1 - Math.pow((1-value), updateRatio));
	}
	
	public Resource getResource() {
		return this.resource;
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
		return adjustedMortality;
	}
	
	/**
	 * Adjusted by update ratio
	 * @return
	 */
	public ArrayList<Offspring> getOffsprings() {
	
		if(offsprings != null)
			return offsprings;
		
		offsprings = new ArrayList<Offspring>();
		if(this.resource.getMutations() != null) {
			for(Offspring mutation : this.resource.getMutations()) {
				Offspring mutationClone = mutation.clone();
				float adjustedRation = adjustByInterval(mutationClone.getRatio());
				mutationClone.setRatio(adjustedRation);
				offsprings.add(mutationClone);
			}
		}
		if(this.resource.getOffsprings() != null) {
			for(Offspring offspring : this.resource.getOffsprings()) {
				Offspring offspringClone = offspring.clone();
				offspringClone.setResource(this.resource.getKey());
				float adjustedRation = adjustByInterval(offspringClone.getRatio());
				offspringClone.setRatio(adjustedRation);
				offsprings.add(offspringClone);
			}
		}
		
		return offsprings;
	}
	
	/**
	 * Adjusted by update ratio
	 * @return
	 */
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
	
	public Set<Integer> getDependencies(int group) {
		return getWeightMap().get(group).keySet();
	}
	
	/**
	 * If there is a point of calculate the resource as run
	 * Resource is static If - has no offsprings and is immortal, is locked. 
	 * @return
	 */
	public boolean isStatic() {
		if(resource.getIsLocked())
			return true;
		else if(!(getOffsprings().size() > 0) && resource.getMortality() == 0)
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
	
	public boolean hasLinks() {
		
		if(hasLinks == null) {
		
			hasLinks = new Boolean(false);
			for(Offspring offspring : getOffsprings()) {
				if(offspring.getIsLinked()) {
					hasLinks = new Boolean(true);
					break;
				}
			}
		}
		
		return hasLinks.booleanValue();
	}
	
	public boolean isValid() {
		
		if(resource.getKey() == 0)
			return false;
		if (resource.getName() == null || resource.getName().equals(""))
			return false;
		
		/*for(Offspring offspring : getOffsprings()) {
			if(offspring.getRatio() <= 0)
				return false;
			if(offspring.getCost() < 0)
				return false;
			if(offspring.getMultiplier() <= 0)
				return false;
		}*/
		
		return true;
	}
}

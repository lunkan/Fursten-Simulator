package fursten.simulator.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fursten.simulator.resource.Resource.Offspring;
import fursten.simulator.resource.Resource.Weight;
import fursten.simulator.resource.Resource.WeightGroup;

public class ResourceWrapper {
	
	private HashMap<Integer, Float> offspringMap;
	private ArrayList<HashMap<Integer, Float>> weightMap;
	private Resource resource;
	
	public ResourceWrapper() {
	}
	
	public ResourceWrapper(Integer key) {
		this.resource = new Resource();
		this.resource.setKey(key);
		this.resource.setName("untitled");
		this.resource.setOffsprings(new ArrayList<Offspring>());
		this.resource.setWeightGroups(new ArrayList<WeightGroup>());
	}
	
	public ResourceWrapper(Resource resource) {
		this.resource = resource;
	}
	
	public Resource getResource() {
		return this.resource;
	}
	
	public ResourceWrapper setResource(Resource resource) {
		this.resource = resource;
		offspringMap = null;
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
	
	public HashMap<Integer, Float> getOffspringMap() {
		
		if(offspringMap != null)
			return offspringMap;
			
		offspringMap = new HashMap<Integer, Float>(); 
		for(Offspring offspring : this.resource.getOffsprings()) {
			offspringMap.put(offspring.getResource(), offspring.getValue());
		}
		
		return offspringMap;
	}
	
	public void putOffspring(int resource, float value) {

		Offspring offspring = new Offspring();
		offspring.setResource(resource);
		offspring.setValue(value);
		this.resource.getOffsprings().add(offspring);
		
		offspringMap = null;
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
	
	public void putWeight(int group, int resource, float value) {

		while(this.resource.getWeightGroups().size() <= group) {
			WeightGroup weightGroup = new WeightGroup();
			weightGroup.setWeights(new ArrayList<Weight>());
			this.resource.getWeightGroups().add(weightGroup);
		}
		
		Weight weight = new Weight();
		weight.setResource(resource);
		weight.setValue(value);
		this.resource.getWeightGroups().get(group).getWeights().add(weight);
		
		weightMap = null;
	}
	
	public int numGroups() {
		return getWeightMap().size();
	}
	
	public boolean isStatic() {
		return (getWeightMap().size() == 0);
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

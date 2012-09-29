package fursten.simulator.resource;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Resource implements Serializable {
	
	static final long serialVersionUID = 10275539472837495L;
	
	private int key;
	private String name;
	private float threshold;
	
	private ArrayList<OffspringNode> offsprings;
	private ArrayList<HashMap<Integer, Float>> weights;
	
	public Resource(int key) {
		this.key = key;
		this.name = "untitled";
		offsprings = new ArrayList<OffspringNode>();
		weights = new ArrayList<HashMap<Integer, Float>>();
	}
	
	public int getKey() {
		return key;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	
	public boolean isStatic() {
		return (weights.size() == 0);
	}
	
	public void putOffspring(int key, float value) {

		OffspringNode offspring = new OffspringNode();
		offspring.key = key;
		offspring.value = value;
		
		//add offsprings in sorted order.
		int index = 0;
		for(int i = 0; i < offsprings.size(); i++) {
			if(offsprings.get(i).value < offspring.value)
				break;
			else
				index = i;
		}
		
		offsprings.add(index, offspring);
	}
	
	public HashMap<Integer, Float> getOffspringMap() {
		
		HashMap<Integer, Float> offspringMap = new HashMap<Integer, Float>(); 
		for(OffspringNode offspring : offsprings) {
			offspringMap.put(offspring.key, offspring.value);
		}
		
		return offspringMap;
	}
	
	public int numGroups() {
		return weights.size();
	}
	
	public void putWeight(int group, int key, float weight) {

		while(weights.size() <= group)
			weights.add(new HashMap<Integer, Float>());
		
		weights.get(group).put(key, weight);
	}
	
	public float getWeight(int group, int key) {
		
		return weights.get(group).get(key);
	}
	
	public Set<Integer> getDependencies() {
		
		HashSet<Integer> DependencyKeys = new HashSet<Integer>();
		for(int i = 0; i < weights.size(); i++) {
			DependencyKeys.addAll(getDependencies(i));
		}
		
		return DependencyKeys;
	}
	
	public Set<Integer> getDependencies(int group) {
		return weights.get(group).keySet();
	}
	
	static class OffspringNode implements Serializable {

		static final long serialVersionUID = 10275539472837495L;
		
		public int key;
		public float value;
	}
}

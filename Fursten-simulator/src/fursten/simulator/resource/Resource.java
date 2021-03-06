package fursten.simulator.resource;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Resource implements Serializable {
	
	static final long serialVersionUID = 10275539472837495L;
	
	private int key;
	private String name;
	private boolean isLocked;
	private float threshold;
	private float mortality;
	private float speed;
	
	private ArrayList<Offspring> offsprings;
	private ArrayList<Offspring> mutations;
	private ArrayList<WeightGroup> weightGroups;
	
	public Resource() {
	}
	
	public int getKey() {
		return key;
	}
	
	public void setKey(int key) {
		this.key = key;
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
	
	public boolean getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}
	
	public float getMortality() {
		return mortality;
	}

	public void setMortality(float mortality) {
		this.mortality = mortality;
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void setOffsprings(ArrayList<Offspring> offsprings) {
		this.offsprings = offsprings;
	}
	
	public ArrayList<Offspring> getOffsprings() {
		return this.offsprings;
	}
	
	public void setMutations(ArrayList<Offspring> mutations) {
		this.mutations = mutations;
	}
	
	public ArrayList<Offspring> getMutations() {
		return this.mutations;
	}
	
	public void setWeightGroups(ArrayList<WeightGroup> weightGroups) {
		this.weightGroups = weightGroups;
	}
	
	public ArrayList<WeightGroup> getWeightGroups() {
		return this.weightGroups;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (key != other.key)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString() {
		
		String weightStr = "[";
		if(weightGroups != null) {
			for(WeightGroup weightGroup : weightGroups) {
				weightStr += weightGroup.toString() + ",";
			}
		}
		weightStr += "]";
		
		
		String offspringStr = "[";
		if(offsprings != null) {
			for(Offspring offspring : offsprings) {
				if(offspring != null)
					offspringStr += offspring.toString() + ",";
			}
		}
		offspringStr += "]";
		
		String mutationsStr = "[";
		if(mutations != null) {
			for(Offspring offspring : mutations) {
				if(offspring != null)
					mutationsStr += mutations.toString() + ",";
			}
		}
		mutationsStr += "]";
		
		return "Resource@"+ this.hashCode() +":{key:"+ key +" name:" + name + " isLocked: " + isLocked + " mortality: " + mortality + " threshold:" + threshold + " speed:" + speed + " weights:" + weightStr + " offsprings:" + offspringStr + " mutations:" +  mutationsStr +"}";
	}
	
	public static class Offspring implements Serializable {

		static final long serialVersionUID = 10275539472837495L;
		
		private int resource;
		//private float value;
		
		//For every x tick a new offspring is born (aprox)
		private float ratio;
		
		//The cost in value for the parent to give birth of the offspring (in % of parent size)
		private float cost;
		
		//The size of the new offspring as multiplied of parent size (may be negative)
		private float multiplier;
		
		//If the child has a linked dependency to the parent.
		private boolean isLinked;
		
		public int getResource() {
			return resource;
		}
		
		public void setResource(int resource) {
			this.resource = resource;
		}
		
		public float getRatio() {
			return ratio;
		}
		
		public void setRatio(float ratio) {
			this.ratio = ratio;
		}
		
		public float getCost() {
			return cost;
		}
		
		public void setCost(float cost) {
			this.cost = cost;
		}
		
		public float getMultiplier() {
			return multiplier;
		}
		
		public void setMultiplier(float multiplier) {
			this.multiplier = multiplier;
		}
		
		public boolean getIsLinked() {
			return isLinked;
		}
		
		public void setIsLinked(boolean isLinked) {
			this.isLinked = isLinked;
		}
		
		public Offspring clone() {
			Offspring clone = new Offspring();
			clone.setCost(this.cost);
			clone.setIsLinked(this.isLinked);
			clone.setMultiplier(this.multiplier);
			clone.setRatio(this.ratio);
			clone.setResource(this.resource);
			return clone;
		}
	}
	
	public static class WeightGroup implements Serializable {

		static final long serialVersionUID = 10275539472837495L;
		
		private ArrayList<Weight> weights;
		
		public ArrayList<Weight> getWeights() {
			return weights;
		}
		
		public void setWeights(ArrayList<Weight> weights) {
			this.weights = weights;
		}
		
		public String toString() {
			
			String weightGroupStr = "[";
			if(weights != null) {
				for(Weight weight : weights) {
					weightGroupStr += weight.toString() + ",";
				}
			}
			weightGroupStr += "]";
			
			return weightGroupStr;
		}
	}
	
	public static class Weight implements Serializable {

		static final long serialVersionUID = 10275539472837495L;
		
		private int resource;
		private float value;
		
		public int getResource() {
			return resource;
		}
		
		public void setResource(int resource) {
			this.resource = resource;
		}
		
		public float getValue() {
			return value;
		}
		
		public void setValue(float value) {
			this.value = value;
		}
		
		public String toString() {
			return "{resource:"+ resource +", value:"+ value +"}";
		}
	}
}

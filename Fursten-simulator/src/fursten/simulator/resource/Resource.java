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

import javax.xml.bind.annotation.XmlRootElement;

//import fursten.rest.jaxb.ResourceObj.ResourceWeight;

@XmlRootElement
public class Resource implements Serializable {
	
	static final long serialVersionUID = 10275539472837495L;
	
	private int key;
	private String name;
	private float threshold;
	
	private ArrayList<Offspring> offsprings;
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
	
	public void setOffsprings(ArrayList<Offspring> offsprings) {
		this.offsprings = offsprings;
	}
	
	public ArrayList<Offspring> getOffsprings() {
		return this.offsprings;
	}
	
	public void setWeightGroups(ArrayList<WeightGroup> weightGroups) {
		this.weightGroups = weightGroups;
	}
	
	public ArrayList<WeightGroup> getWeightGroups() {
		return this.weightGroups;
	}
	
	public boolean hasWeights() {
		if (this.weightGroups == null)
			return false;
		else if(this.weightGroups.size() == 0)
			return false;
		else
			return true;
	}
	
	public boolean hasOffsprings() {
		if (this.offsprings == null)
			return false;
		else if(this.offsprings.size() == 0)
			return false;
		else
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
				offspringStr += offspring.toString() + ",";
			}
		}
		offspringStr += "]";
		
		return "Resource@"+ this.hashCode() +":{key:"+ key +" name:" + name + " threshold:" + threshold + " weights:" + weightStr + " offsprings:" + offspringStr + "}";
	}
	
	public static class Offspring implements Serializable {

		static final long serialVersionUID = 10275539472837495L;
		
		public int resource;
		public float value;
		
		public String toString() {
			return "{resource:"+ resource +", value:"+ value +"}";
		}
	}
	
	public static class WeightGroup implements Serializable {

		static final long serialVersionUID = 10275539472837495L;
		
		public ArrayList<Weight> weights;
		
		public String toString() {
			
			String weightGroupStr = "[";
			for(Weight weight : weights) {
				weightGroupStr += weight.toString() + ",";
			}
			weightGroupStr += "]";
			
			return weightGroupStr;
		}
	}
	
	public static class Weight implements Serializable {

		static final long serialVersionUID = 10275539472837495L;
		
		public int resource;
		public float value;
		
		public String toString() {
			return "{resource:"+ resource +", value:"+ value +"}";
		}
	}
}

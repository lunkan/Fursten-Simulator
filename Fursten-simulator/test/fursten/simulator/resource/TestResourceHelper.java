package fursten.simulator.resource;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.resource.Resource.Offspring;
import fursten.simulator.resource.Resource.Weight;
import fursten.simulator.resource.Resource.WeightGroup;

public class TestResourceHelper {

	public static HashMap<String, Resource> loadTestCase() {
	
		/*String settingsUrl = context.getRealPath(File.separator) + "WEB-INF" + File.separator + "settings.xml";
		
		try {
			JAXBContext context = JAXBContext.newInstance(ResourceCollection.class);
			Unmarshaller unMarshaller = context.createUnmarshaller();
			ResourceCollection resources = (ResourceCollection) unMarshaller.unmarshal(new FileInputStream("Foo.xml"));
		}
		catch(Exception e) {
			System.out.println("could not load test case");
		}*/
		
    	return null;
	}
	
	/**
	 * Generates a range of static resources. Resources will be named "[prefix]_static_[d]_[n]" where d equals to resource depth and n equals to (index +1)
	 * @param prefix Prefix to add to the beginning of each generated resource name.
	 * @param size Number of resources to be generated
	 * @return
	 */
	public static HashMap<String, Resource> setupStaticScenario(String prefix, int size) {
		
		String name = prefix + "_static";
		HashMap<String, Resource> staticResources = TestResourceHelper.generateResources(1, null, name, size, 1, true, 0, 0);

		ArrayList<Resource> resources = new ArrayList<Resource>();
    	resources.addAll(staticResources.values());
    	
    	ResourceManager RM = DAOManager.get().getResourceManager();
    	RM.insert(resources);
		
    	return staticResources;
	}
	
	/**
	 * Generates a range of static and dependent resources. Static Resources will be named "[prefix]_static_[d]_[n]" where d equals to resource depth and n equals to (index +1)
	 * Dynamic resources will be named "[prefix]_dependent_[d]_[n]". For every n dependent resource generated an additional weight will be added.
	 * First dependent resource will containing 1 weight, the third dependent resource will containing 3 weights.
	 * Weights are pointed to corresponding static resource - weight 2 refers to "[prefix]_static_2". Weights are applied random values.
	 * @param prefix Prefix to add to the beginning of each generated resource name.
	 * @param size Number of static and dependent resources to be generated. Total number of resources is size*2
	 * @param mortality Mortality value for dependent resources.
	 * @param threshold Threshold value for dependent resources.
	 * @param weight Weight value for dependent resource weights.
	 * @return
	 */
	public static HashMap<String, Resource> setupDependentScenario(String prefix, int size, float mortality, float threshold, float weight) {
		
		String staticName = prefix + "_static";
		String dependentName = prefix + "_dependent";
		HashMap<String, Resource> staticResources = TestResourceHelper.generateResources(1, null, staticName, size, 1, true, 0, 0);
		HashMap<String, Resource> dependentResources = TestResourceHelper.generateResources(1, null, dependentName, size, 1, false, mortality, threshold);
    	
		for(Resource dependentResourse : dependentResources.values()) {
			
			String resIndex = dependentResourse.getName().split("\\_")[2];
			for(int w = 1; w <= Integer.parseInt(resIndex); w++) {
				String refName = staticName + "_1_" + w;
				Resource weightRef = staticResources.get(refName);
				appendWeight(dependentResourse, 0, weightRef, weight);
			}
		}
		
		HashMap<String, Resource> resources = new HashMap<String, Resource>();
		resources.putAll(staticResources);
		resources.putAll(dependentResources);
    	
    	ResourceManager RM = DAOManager.get().getResourceManager();
    	RM.insert(new ArrayList<Resource>(resources.values()));
		
    	return resources;
	}
	
	/**
	 * Generates a range of static and dependent resources. Static Resources will be named "[prefix]_static_[d]_[n]" where d equals to resource depth and n equals to (index +1)
	 * Dependent resources will be named ""[prefix]_dependent_[d]_[n]"". For every n dependent resource generated an additional weight will be added.
	 * First dependent resource will have 1 weight group containing 1 weight, the third dependent resource will have two weight groups containing 2 resp 1 weights.
	 * For every 2 weights a new weight group is added. Weights are pointed to corresponding static resource - weight 2 refers to "[prefix]_static_2".
	 * @param prefix Prefix to add to the beginning of each generated resource name.
	 * @param size Number of static and dependent resources to be generated. Total number of resources is size*2
	 * @param mortality Mortality value for dependent resources.
	 * @param threshold Threshold value for dependent resources.
	 * @param weight Weight value for dependent resource weights.
	 * @return
	 */
	public static HashMap<String, Resource> setupGroupDependentScenario(String prefix, int size, float mortality, float threshold, float weight) {
		
		String staticName = prefix + "_static";
		String dependentName = prefix + "_dependent";
		HashMap<String, Resource> staticResources = TestResourceHelper.generateResources(1, null, staticName, size, 1, true, 0, 0);
		HashMap<String, Resource> dependentResources = TestResourceHelper.generateResources(1, null, dependentName, size, 1, false, mortality, threshold);
    	
		for(Resource dependentResourse : dependentResources.values()) {
			
			String resIndex = dependentResourse.getName().split("\\_")[2];
			for(int w = 1; w <= Integer.parseInt(resIndex); w++) {
				String refName = staticName + "_1_" + w;
				Resource weightRef = staticResources.get(refName);
				int weightGroup = w % 2;
				appendWeight(dependentResourse, weightGroup, weightRef, weight);
			}
		}
		
		HashMap<String, Resource> resources = new HashMap<String, Resource>();
		resources.putAll(staticResources);
		resources.putAll(dependentResources);
    	
    	ResourceManager RM = DAOManager.get().getResourceManager();
    	RM.insert(new ArrayList<Resource>(resources.values()));
		
    	return resources;
	}
	
	/**
	 * Generates a range of static and dependent resources. Static Resources will be named "[prefix]_static_[d]_[n]" where d equals to resource depth and n equals to (index +1)
	 * Dependent resources will be named ""[prefix]_dependent_[d]_[n]"". For every n dependent resource generated an additional weight will be added.
	 * First dependent resource will contain 1 weight, the third dependent resource will contain 3 weights.
	 * For every weight after the first 1 depth is added. Weights are pointed to corresponding static resource - weight 2 refers to "[prefix]_static_2".
	 * Mortality, Threshold and weights are applied random values.
	 * @param prefix Prefix to add to the beginning of each generated resource name.
	 * @param size Number of static and dependent resources to be generated. Total number of resources is size*2
	 * @param mortality Mortality value for dependent resources.
	 * @param threshold Threshold value for dependent resources.
	 * @param weight Weight value for dependent resource weights.
	 * @return
	 */
	public static HashMap<String, Resource> setupDepthDependentScenario(String prefix, int size, float mortality, float threshold, float weight) {
		
		String staticName = prefix + "_static";
		String dependentName = prefix + "_dependent";
		HashMap<String, Resource> staticResources = TestResourceHelper.generateResources(1, null, staticName, 1, size, true, 0, 0);
		HashMap<String, Resource> dependentResources = TestResourceHelper.generateResources(1, null, dependentName, 1, size, false, mortality, threshold);
    	
		for(Resource dependentResource : dependentResources.values()) {
			
			String resIndex = dependentResource.getName().split("\\_")[2];
			for(int w = 1; w <= Integer.parseInt(resIndex); w++) {
				String refName = staticName + "_"+ w +"_1";
				Resource weightRef = staticResources.get(refName);
				appendWeight(dependentResource, 0, weightRef, weight);
			}
		}
		
		HashMap<String, Resource> resources = new HashMap<String, Resource>();
		resources.putAll(staticResources);
		resources.putAll(dependentResources);
    	
    	ResourceManager RM = DAOManager.get().getResourceManager();
    	RM.insert(new ArrayList<Resource>(resources.values()));
		
    	return resources;
	}
	
	/**
	 * Generates a range of static and dependent resources. Static Resources will be named "[prefix]_static_[d]_[n]" where d equals to resource depth and n equals to (index +1)
	 * Dependent resources will be named "[prefix]_dependent_[d]_[n]". For every n dependent resource generated an additional weight will be added.
	 * First dependent resource will containing 1 weight, the third dependent resource will containing 3 weights.
	 * Weights are pointed to corresponding static resource - weight 2 refers to "[prefix]_static_2".
	 * All dependent resources will have self as an offspring.
	 * @param prefix Prefix to add to the beginning of each generated resource name.
	 * @param size Number of static and dependent resources to be generated. Total number of resources is size*2
	 * @param mortality Mortality value for dependent resources.
	 * @param threshold Threshold value for dependent resources.
	 * @param weight Weight value for dependent resource weights.
	 * @param ratio Offspring ratio for dependent resources offsprings.
	 * @param cost Offspring cost for dependent resources offsprings.
	 * @param multiplier Offspring multiplier for dependent resources offsprings.
	 * @param isLinked Offspring isLinked for dependent resources offsprings.
	 * @return
	 */
	public static HashMap<String, Resource> setupOffspringScenario(String prefix, int size, float mortality, float threshold, float weight, float ratio, float cost, float multiplier, boolean isLinked) {
		
		String staticName = prefix + "_static";
		String dependentName = prefix + "_dependent";
		HashMap<String, Resource> staticResources = TestResourceHelper.generateResources(1, null, staticName, size, 1, true, 0, 0);
		HashMap<String, Resource> dependentResources = TestResourceHelper.generateResources(1, null, dependentName, size, 1, false, mortality, threshold);
    	
		for(Resource dependentResourse : dependentResources.values()) {
			
			String resIndex = dependentResourse.getName().split("\\_")[2];
			for(int w = 1; w <= Integer.parseInt(resIndex); w++) {
				String refName = staticName + "_1_" + w;
				Resource weightRef = staticResources.get(refName);
				appendWeight(dependentResourse, 0, weightRef, weight);
			}
			
			appendOffspring(dependentResourse, dependentResourse, ratio, cost, multiplier, isLinked);
		}
		
		HashMap<String, Resource> resources = new HashMap<String, Resource>();
		resources.putAll(staticResources);
		resources.putAll(dependentResources);
    	
    	ResourceManager RM = DAOManager.get().getResourceManager();
    	RM.insert(new ArrayList<Resource>(resources.values()));
		
    	return resources;
	}
	
	/**
	 * Generates a range of static and dependent resources. Static Resources will be named "[prefix]_static_[d]_[n]" where d equals to resource depth and n equals to (index +1)
	 * Dependent resources will be named "[prefix]_dependent_[d]_[n]". For every n dependent resource generated an additional weight will be added.
	 * First dynamic resource will containing 1 weight, the third dynamic resource will containing 3 weights.
	 * Weights are pointed to corresponding static resource - weight 2 refers to "[prefix]_static_2".
	 * Dependent resources will have all previous dependent resources in the queue as mutations.
	 * @param prefix Prefix to add to the beginning of each generated resource name.
	 * @param size Number of static and dependent resources to be generated. Total number of resources is size*2
	 * @param mortality Mortality value for dependent resources.
	 * @param threshold Threshold value for dependent resources.
	 * @param weight Weight value for dependent resource weights.
	 * @param ratio Mutation value for dependent resource mutation.
	 * @param cost Mutation cost for dependent resources offsprings.
	 * @param multiplier Mutation multiplier for dependent resources offsprings.
	 * @param isLinked Mutation isLinked for dependent resources offsprings.
	 * @return
	 */ 
	public static HashMap<String, Resource> setupMutationScenario(String prefix, int size, float mortality, float threshold, float weight, float ratio, float cost, float multiplier, boolean isLinked) {
		
		String staticName = prefix + "_static";
		String dependentName = prefix + "_dependent";
		HashMap<String, Resource> staticResources = TestResourceHelper.generateResources(1, null, staticName, size, 1, true, 0, 0);
		HashMap<String, Resource> dependentResources = TestResourceHelper.generateResources(1, null, dependentName, size, 1, false, mortality, threshold);
    	
		List<Resource> mutationReferences = new ArrayList<Resource>();
		for(Resource dependentResourse : dependentResources.values()) {
			
			String resIndex = dependentResourse.getName().split("\\_")[2];
			for(int w = 1; w <= Integer.parseInt(resIndex); w++) {
				String refName = staticName + "_1_" + w;
				Resource weightRef = staticResources.get(refName);
				appendWeight(dependentResourse, 0, weightRef, weight);
			}
			
			for(Resource mutationRef : mutationReferences)
				appendMutation(dependentResourse, mutationRef, ratio, cost, multiplier, isLinked);
			
			mutationReferences.add(dependentResourse);
		}
		
		HashMap<String, Resource> resources = new HashMap<String, Resource>();
		resources.putAll(staticResources);
		resources.putAll(dependentResources);
    	
    	ResourceManager RM = DAOManager.get().getResourceManager();
    	RM.insert(new ArrayList<Resource>(resources.values()));
		
    	return resources;
	}
	
	/**
	 * Yet to be done.....
	 * @param prefix
	 * @param size
	 * @return
	 */
	public static HashMap<String, Resource> setupLinkScenario(String prefix, int size) {
		return null;
	}
	
	public static HashMap<String, Resource> generateResources(
			int startDepth,
			Resource parent,
			String name,
			int num,
			int depth,
			boolean isLocked,
			float mortality,
			float threshold) {
		
		HashMap<String, Resource> resources = new HashMap<String, Resource>();
		
		for(int n = 0; n < num; n++) {
		
			Resource resource = new Resource();
			
			if(parent == null)
				resource.setKey(ResourceKeyManager.getNext());
			else
				resource.setKey(ResourceKeyManager.getNext(parent.getKey()));
			
			resource.setName(name + "_" + startDepth + "_" + (n+1));
			resource.setIsLocked(isLocked);
			resource.setMortality(mortality);
			resource.setThreshold(threshold);
			resources.put(resource.getName(), resource);
			
			//Add depth to first only
			if(startDepth < depth && n == 0) {
				HashMap<String, Resource> childResources = generateResources(startDepth+1, resource, name, num, depth, isLocked, mortality, threshold);
				resources.putAll(childResources);
			}
		}
		
		return resources;
	}
	
	public static void appendWeight(
			Resource resource,
			int groupIndex,
			Resource reference,
			float value) {
		
		ArrayList<WeightGroup> weightGroups = resource.getWeightGroups();
		if(weightGroups == null)
			weightGroups = new ArrayList<WeightGroup>();
		
		while(weightGroups.size() <= groupIndex) {
			WeightGroup weightGroup = new WeightGroup();
			weightGroup.setWeights(new ArrayList<Weight>());
			weightGroups.add(weightGroup);
		}
		
		Weight weight = new Weight();
		weight.setResource(reference.getKey());
		weight.setValue(value);
		weightGroups.get(groupIndex).getWeights().add(weight);
		
		resource.setWeightGroups(weightGroups);
	}
	
	public static void appendOffspring(
			Resource resource,
			Resource resourceRef,
			float ratio,
			float cost,
			float multiplier,
			boolean isLinked) {
		
		ArrayList<Offspring> offsprings = resource.getOffsprings();
		if(offsprings == null)
			offsprings = new ArrayList<Offspring>();
		
		Offspring offspring = new Offspring();
		offspring.setResource(resourceRef.getKey());
		offspring.setRatio(ratio);
		offspring.setCost(cost);
		offspring.setIsLinked(isLinked);
		offspring.setMultiplier(multiplier);
		
		resource.setOffsprings(offsprings);
	}
	
	public static void appendMutation(
			Resource resource,
			Resource resourceRef,
			float ratio,
			float cost,
			float multiplier,
			boolean isLinked) {
		
		ArrayList<Offspring> offsprings = resource.getMutations();
		if(offsprings == null)
			offsprings = new ArrayList<Offspring>();
		
		Offspring offspring = new Offspring();
		offspring.setResource(resourceRef.getKey());
		offspring.setRatio(ratio);
		offspring.setCost(cost);
		offspring.setIsLinked(isLinked);
		offspring.setMultiplier(multiplier);
		
		resource.setMutations(offsprings);
	}
}

package fursten.simulator;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceCollection;

public class TestCaseHelper {

	private static final Logger logger = Logger.getLogger(TestCaseHelper.class.getName());
	
	public static HashMap<String, Resource> load(String testCase) {
		
		try {
			JAXBContext context = JAXBContext.newInstance(Resource.class, ResourceCollection.class);
			Unmarshaller unMarshaller = context.createUnmarshaller();
			ResourceCollection resourceCollection = (ResourceCollection) unMarshaller.unmarshal(new FileInputStream(testCase));
			
			ArrayList<Resource> resourceList = resourceCollection.getResources();
			ResourceManager RM = DAOManager.get().getResourceManager();
	    	RM.putAll(resourceList);
	    	
			HashMap<String, Resource> resourceMap = new HashMap<String, Resource>();
			for(Resource resource : resourceList) {
				resourceMap.put(resource.getName(), resource);
			}
			
			return resourceMap;
			
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Can't load test case " + testCase);
			return null;
		}
	}
}

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
	
	/*    try {
		//FileInputStream fis = (FileInputStream)getClass().getResourceAsStream("static-resources.xml");
		  
		//WebContent/WEB-INF/settings.xml
		
		//File testWsdl = new File("WebContent/index.jsp");
		File testWsdl = new File("testcase/resource/static-resources.xml");
		System.out.println("# " + testWsdl.exists());
		
		String settingsUrl = "testcase/resource/static-resources.xml";//context.getRealPath(File.separator) + "WEB-INF" + File.separator + "settings.xml";
		
		//URL url = this.getClass().getResource("/junit-settings.xml");
		//File testWsdl = new File(url.getFile());
		
		//System.out.println("# " + testWsdl.exists());
		
		// loading properties in XML format        
		//Properties pXML = new Properties();
		//pXML.loadFromXML(new FileInputStream( new File("junit-settings.xml")));
		
		//System.out.println(pXML.size());
		
	}
	catch(Exception e) {
		System.out.println("no work " + e.getMessage());
	}
	}*/
	
	public static HashMap<String, Resource> load(String testCase) {
		
		try {
			JAXBContext context = JAXBContext.newInstance(Resource.class, ResourceCollection.class);
			Unmarshaller unMarshaller = context.createUnmarshaller();
			ResourceCollection resourceCollection = (ResourceCollection) unMarshaller.unmarshal(new FileInputStream(testCase));
			
			/*System.out.println("size " + resources.getResources().size());
			
			for(Resource resource : resources.getResources()) {
				System.out.println(resource.getName());
			}*/
			
			ArrayList<Resource> resourceList = resourceCollection.getResources();
			ResourceManager RM = DAOManager.get().getResourceManager();
	    	RM.insert(resourceList);
	    	
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

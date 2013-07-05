package fursten.simulator;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import fursten.simulator.link.Link;
import fursten.simulator.link.LinkCollection;
import fursten.simulator.node.Node;
import fursten.simulator.node.NodeCollection;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.LinkManager;
import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceCollection;

public class TestCaseHelper {

	private static final Logger logger = Logger.getLogger(TestCaseHelper.class.getName());
	
	public static HashMap<String, Resource> loadResources(String testCase) {
		
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
			logger.log(Level.SEVERE, "Can't load resource test case " + testCase);
			return null;
		}
	}
	
	public static HashMap<String, Node> loadNodes(String testCase) {
		
		try {
			JAXBContext context = JAXBContext.newInstance(Node.class, NodeCollection.class);
			Unmarshaller unMarshaller = context.createUnmarshaller();
			NodeCollection nodeCollection = (NodeCollection) unMarshaller.unmarshal(new FileInputStream(testCase));
			
			ArrayList<Node> nodeList = nodeCollection.getNodes();
			NodeManager NM = DAOManager.get().getNodeManager();
	    	NM.addAll(nodeList);
	    	
	    	ResourceManager RM = DAOManager.get().getResourceManager();
	    	
	    	HashMap<String, Node> nodeMap = new HashMap<String, Node>();
			for(Node node : nodeList) {
				Resource resource = RM.get(node.getR());
				String nodeName = resource.getName() + "["+node.getX()+":"+node.getY()+"]";
				nodeMap.put(nodeName, node);
			}
	    	
			return nodeMap;
			
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Can't load node test case " + testCase);
			return null;
		}
	}
	
	public static HashMap<String, Link> loadLinks(String testCase) {
		
		try {
			JAXBContext context = JAXBContext.newInstance(Node.class, Link.class, LinkCollection.class);
			Unmarshaller unMarshaller = context.createUnmarshaller();
			LinkCollection linkCollection = (LinkCollection) unMarshaller.unmarshal(new FileInputStream(testCase));
			
			ArrayList<Link> linkList = linkCollection.getLinks();
			LinkManager LM = DAOManager.get().getLinkManager();
			LM.addAll(linkList);
	    	
	    	ResourceManager RM = DAOManager.get().getResourceManager();
	    	
	    	HashMap<String, Link> linkMap = new HashMap<String, Link>();
			for(Link link : linkList) {
				Resource parentResource = RM.get(link.getParentNode().getR());
				Resource childResource = RM.get(link.getChildNode().getR());
				
				String linkName = parentResource.getName() + "["+link.getParentNode().getX()+":"+link.getParentNode().getY()+"]";
				linkName += " " + childResource.getName() + "["+link.getChildNode().getX()+":"+link.getChildNode().getY()+"]";
				
				linkMap.put(linkName, link);
			}
	    	
			return linkMap;
			
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Can't load link test case " + testCase);
			return null;
		}
	}
	
	/*BigInteger bigIntA = BigInteger.valueOf(0);
    BigInteger bigIntB = BigInteger.valueOf(0);
    
    bigIntA = bigIntA.setBit(31);
    	BigInteger bigIntAA = bigIntA.setBit(30);
    		BigInteger bigIntAAA = bigIntAA.setBit(29);
    		BigInteger bigIntAAB = bigIntAA.setBit(28);
    	BigInteger bigIntAB = bigIntA.setBit(29);
    		BigInteger bigIntABA = bigIntAB.setBit(28);
    		BigInteger bigIntABB = bigIntAB.setBit(27);
    
    bigIntB = bigIntB.setBit(30);
    	BigInteger bigIntBA = bigIntB.setBit(29);
    		BigInteger bigIntBAA = bigIntBA.setBit(28);
    		BigInteger bigIntBAB = bigIntBA.setBit(27);
    	BigInteger bigIntBB = bigIntB.setBit(28);
    		BigInteger bigIntBBA = bigIntBB.setBit(27);
    		BigInteger bigIntBBB = bigIntBB.setBit(26);
    		
    System.out.println("bigIntA " + bigIntA.intValue());
    System.out.println("bigIntAA " + bigIntAA.intValue());
    System.out.println("bigIntAAA " + bigIntAAA.intValue());
    System.out.println("bigIntAAB " + bigIntAAB.intValue());
    System.out.println("bigIntAB " + bigIntAB.intValue());
    System.out.println("bigIntABA " + bigIntABA.intValue());
    System.out.println("bigIntABB " + bigIntABB.intValue());
    
    System.out.println("bigIntB " + bigIntB.intValue());
    System.out.println("bigIntBA " + bigIntBA.intValue());
    System.out.println("bigIntBAA " + bigIntBAA.intValue());
    System.out.println("bigIntBAB " + bigIntBAB.intValue());
    System.out.println("bigIntBB " + bigIntBB.intValue());
    System.out.println("bigIntBBA " + bigIntBBA.intValue());
    System.out.println("bigIntBBB " + bigIntBBB.intValue());
    
    BigInteger bigIntC = BigInteger.valueOf(0);
    bigIntC  = bigIntC.setBit(29);
    
    BigInteger bigIntBBC = bigIntB.setBit(27);
    System.out.println("bigIntC " + bigIntC.intValue() + " # " + "bigIntBBC " + bigIntBBC.intValue());*/
    
    
    /**/
}

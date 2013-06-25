package fursten.simulator.node;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestCaseHelper;
import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.persistent.DAOManager;
import fursten.simulator.persistent.WorldManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceCollection;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceWrapper;
import fursten.simulator.resource.TestResourceHelper;
import fursten.simulator.world.World;
import fursten.util.persistent.DAOTestHelper;

public class TestNodeActivityManager {

	//private final DAOTestHelper helper = DAOManager.getTestHelper();
	
    @Before
    public void setUp() {
    	//helper.setUp();
    	
    	System.out.println("START");
    	
    	TestStartup.init();
    	
    	try{
    	int A = ResourceKeyManager.getNext();
    	System.out.println("A:" + A);
    	int AA = ResourceKeyManager.getNext(A);
    	System.out.println("AA:" + AA);
    	int AB = ResourceKeyManager.getNext(A);
    	System.out.println("AB:" + AB);
    	int AAA = ResourceKeyManager.getNext(AA);
    	System.out.println("AAA:" + AAA);
    	int AAB = ResourceKeyManager.getNext(AA);
    	System.out.println("AAB:" + AAB);
    	int ABA = ResourceKeyManager.getNext(AB);
    	System.out.println("ABA:" + ABA);
    	int ABB = ResourceKeyManager.getNext(AB);
    	System.out.println("ABB:" + ABB);
    	//System.exit(0);
    	}
    	catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    }
    
    @After
    public void tearDown() {
        //helper.tearDown();
        TestShutDown.destroy();
    }
    
    @Test
    public void testNodeActivityManagerInvalidate() throws Exception {
    	
    	/* SETUP
    	 * Two layer of static root resources and two layers of dependent resources.
    	 * Invalidate one type of the static resources in north west and south east sections
    	 */
    	/*String prefix = "sd";
    	int numResources = 2;
    	float mortality = 0.0f;
    	float threshold = 0.0f;
    	float weightValue = 1.0f;
    	HashMap<String, Resource> resources = TestResourceHelper.setupDependentScenario(prefix, numResources, mortality, threshold, weightValue);
    	*/
    	
    	HashMap<String, Resource> resources =  TestCaseHelper.load("testcase/resource/static-resources.xml");
    	System.out.println("#--> " + resources.size());
    	
    	/*float nodeValue = 1.0f;
    	List<Node> invalidNodes = TestNodeHelper.setupArray(resources.get("sd_static_1_1"), new int[][]{{-250,0},{0,250}}, nodeValue);
    	
    	// TEST
    	NodeActivityManager.invalidate(invalidNodes);
    	Set<Integer> invalidResources = NodeActivityManager.getInvalidResources();
    	
    	List<Rectangle> invalidRects = NodeActivityManager.getInvalidRectByResourceKey(invalidResources.iterator().next());
    	Assert.assertEquals(2, invalidRects.size());
    	
    	//Rectangles should contain at least one coordinate but not both
    	for(Rectangle invalidRect : invalidRects) {
    		Assert.assertFalse(invalidRect.contains(-250, 0) && invalidRect.contains(0, 250));
    		Assert.assertFalse(!invalidRect.contains(-250, 0) && !invalidRect.contains(0, 250));
    	}
    	
    	//No invalid resources after clean
    	NodeActivityManager.clean();
    	invalidResources = NodeActivityManager.getInvalidResources();
    	Assert.assertEquals(0, invalidRects.size());*/
    }
    
    @Test
    public void testNodeActivityManagerInvalidateAll() throws Exception {
    
    	/*String prefix = "sad";
    	int numResources = 2;
    	float mortality = 0.0f;
    	float threshold = 0.0f;
    	float weightValue = 1.0f;
    	TestResourceHelper.setupDependentScenario(prefix, numResources, mortality, threshold, weightValue);
    	
    	WorldManager SM = DAOFactory.get().getWorldManager();
		World world = SM.getActive();
		Rectangle worldRect = world.getRect();
		
    	// TEST 
    	NodeActivityManager.invalidateAll();
    	Set<Integer> invalidResources = NodeActivityManager.getInvalidResources();
    	Assert.assertEquals(4, invalidResources.size());
    	
    	for(Integer invalidResource : invalidResources) {
    		List<Rectangle> invalidRects = NodeActivityManager.getInvalidRectByResourceKey(invalidResource);
	    	Assert.assertEquals(1, invalidRects.size());
	    	Rectangle invalidRect = invalidRects.get(0);
	    	Assert.assertTrue(invalidRect.equals(worldRect));
    	}
    	
    	//No invalid resources after clean
    	NodeActivityManager.clean();
    	invalidResources = NodeActivityManager.getInvalidResources();
    	Assert.assertEquals(0, invalidResources.size());*/
    }
}

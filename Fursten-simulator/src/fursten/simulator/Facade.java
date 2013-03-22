package fursten.simulator;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fursten.simulator.command.NodeGetCommand;
import fursten.simulator.command.NodeEditCommand;
import fursten.simulator.command.ResourceGetCommand;
import fursten.simulator.command.ResourceEditCommand;
import fursten.simulator.command.SampleCommand;
import fursten.simulator.command.SimulatorInitializeCommand;
import fursten.simulator.command.SimulatorRunCommand;
import fursten.simulator.instance.Instance;
import fursten.simulator.node.Node;
import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.SessionManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceKeyManager;
import fursten.simulator.resource.ResourceIndex;
import fursten.simulator.resource.ResourceIndex.ResourceItem;
import fursten.simulator.resource.ResourceSelection;
import fursten.simulator.sample.Sample;

public class Facade {

	private static final Logger logger = Logger.getLogger(Facade.class.getName());
	
	public static boolean init(String name, int width, int height) {
		
		logger.log(Level.INFO, "Calling Facade.init(name:"+name+", width:"+width+", height:"+height+")");
		
		Instance session = new Instance()
			.setName(name)
			.setWidth(width)
			.setHeight(height);
		
		try {
			new SimulatorInitializeCommand(session).execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean clear() {
		return true;
	}
	
	public static Status getStatus() {
		
		logger.log(Level.INFO, "Calling Facade.getStatus");
		
		SessionManager SM = DAOFactory.get().getSessionManager();
		Instance instance = SM.getActive();
		
		if(instance == null)
			return null;
		
		Status status = new Status()
			.setName(instance.getName())
			.setWidth(instance.getWidth())
			.setHeight(instance.getHeight())
			.setTick(instance.getTick());
		
		return status;
	}
	
	public static boolean run() {
		
		SessionManager SM = DAOFactory.get().getSessionManager();
		Instance session = SM.getActive();
		
		try {
			new SimulatorRunCommand(session.getRect()).execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Sample> getSamples(List<Sample> samples) {
		
		try {
			return (List<Sample>) new SampleCommand(samples).execute();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static boolean editNodes(List<Node> delete, List<Node> put) {
		
		try {
			new NodeEditCommand(delete, put).execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Node> getNodes(Rectangle rect, Set<Integer> filter) {
		
		try {
			return (List<Node>) new NodeGetCommand(rect, filter).execute();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean addResource(int parentKey, Resource resource) {
		
		System.out.println(resource);
		
		ResourceManager RM = DAOFactory.get().getResourceManager();
		ResourceKeyManager keyManager = new ResourceKeyManager(RM.getKeys());
		
		//Create new key by extending parent key
		int newKey = keyManager.getNext(parentKey);
		resource.setKey(newKey);
		
		List<Resource> resources = new ArrayList<Resource>();
		resources.add(resource);
		return editResources(null, resources);
	}

	public static boolean putResource(Resource resource) {
		
		List<Resource> resources = new ArrayList<Resource>();
		resources.add(resource);
		return editResources(null, resources);
	}

	public static boolean editResources(Set<Integer> delete, List<Resource> put) {
		
		try {
			new ResourceEditCommand(delete, put).execute();
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	public static ResourceIndex getResourceIndex()  {
		
		ResourceIndex resourceIndex = new ResourceIndex();
		List<Resource> resources = getResources(new ResourceSelection());
		for(Resource resource : resources) {
			resourceIndex.add(resource.getKey(), resource.getName());
		}
		
		return resourceIndex;
		
	}
	
	@SuppressWarnings("unchecked")
	public static List<Resource> getResources(ResourceSelection selection)  {
		
		try {
			return (List<Resource>) new ResourceGetCommand(selection).execute();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

package fursten.simulator;

import java.awt.Rectangle;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import fursten.simulator.command.NodeGetCommand;
import fursten.simulator.command.NodeEditCommand;
import fursten.simulator.command.ResourceGetCommand;
import fursten.simulator.command.ResourceEditCommand;
import fursten.simulator.command.SimulatorInitializeCommand;
import fursten.simulator.command.SimulatorRunCommand;
import fursten.simulator.instance.Instance;
import fursten.simulator.node.Node;
import fursten.simulator.persistent.SessionManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;
import fursten.simulator.resource.ResourceSelection;

public class Facade {

	private static final Logger logger = Logger.getLogger(Facade.class.getName());
	
	public static boolean init(String name, int width, int height) {
		
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
	
	public static Instance getStatus() {
		
		SessionManager SM = DAOFactory.get().getSessionManager();
		return SM.getActive();
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
	
	public static boolean editResources(Set<Integer> delete, List<Resource> put) {
		
		try {
			new ResourceEditCommand(delete, put).execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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

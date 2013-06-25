package fursten.simulator.node;

import java.util.ArrayList;
import java.util.List;

import fursten.simulator.persistent.NodeManager;
import fursten.simulator.persistent.mysql.DAOFactory;
import fursten.simulator.resource.Resource;

public class TestNodeHelper {

	public static List<Node> setupArray(int[][] nodeData, float nodeValue) {
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		for(int i = 0; i < nodeData.length; i++) {
		
			int x = nodeData[i][0];
			int y = nodeData[i][1];
			int r = nodeData[i][2];
			
			Node node = new Node();
			node.setV(nodeValue);
			node.setR(r);
			node.setX(x);
			node.setY(y);
			nodes.add(node);
		}
		
		NodeManager NM = DAOFactory.get().getNodeManager();
		NM.addAll(nodes);
		
		return nodes;
	}
	
	public static List<Node> setupArray(Resource resource, int[][] points, float nodeValue) {
	
		ArrayList<Node> nodes = new ArrayList<Node>();
		for(int i = 0; i < points.length; i++) {
		
			int x = points[i][0];
			int y = points[i][1];
			
			Node node = new Node();
			node.setR(resource.getKey());
			node.setV(nodeValue);
			node.setX(x);
			node.setY(y);
			nodes.add(node);
		}
		
		NodeManager NM = DAOFactory.get().getNodeManager();
		NM.addAll(nodes);
		
		return nodes;
	}
	
	/*public static List<Node> setupGrid(Resource resource, int startX, int startY, int cols, int rows, int colSpacing, int rowSpacing, float nodeValue) {
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		for(int r = 0; r < rows; r++) {
			
			for(int c = 0; c < cols; c++) {
				
				int x = startX + (c*colSpacing);
				int y = startY + (r*rowSpacing);
				
				Node node = new Node();
				node.setR(resource.getKey());
				node.setV(nodeValue);
				node.setX(x);
				node.setY(y);
				nodes.add(node);
			}
		}
		
		NodeManager NM = DAOFactory.get().getNodeManager();
		NM.insert(nodes);
		
		return nodes;
	}*/
}

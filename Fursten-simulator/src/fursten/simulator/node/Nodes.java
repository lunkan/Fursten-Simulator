package fursten.simulator.node;

import java.math.BigInteger;

public class Nodes {

	public static boolean intersect(Node nodeA, Node nodeB) {
		
		if(nodeA.getX() != nodeB.getX())
			return false;
		else if(nodeA.getY() != nodeB.getY())
			return false;
		else
			return true;
	}
	
	public static Node add(Node nodeA, Node nodeB) {
		
		nodeA.setV(nodeA.getV() + nodeB.getV());
		return nodeA;
	}
	
	public static Node substract(Node nodeA, Node nodeB) {
		
		nodeA.setV(Math.max(0, nodeA.getV() - nodeB.getV()));
		return nodeA;
	}
	
	public static String toHashString(Node node) {
		
		String x = Integer.toHexString(node.getX());
		String y = Integer.toHexString(node.getY());
		String r = Integer.toHexString(node.getR());
		return x + "." + y + "." + r;
	}
	
	public static Node toNode(String hashString) {
		
		String[] stringArgs = hashString.split("\\.",-1);
		Node node = new Node();
		node.setX(Integer.parseInt(stringArgs[0], 16));
		node.setY(Integer.parseInt(stringArgs[1], 16));
		node.setR(Integer.parseInt(stringArgs[2], 16));
		return node;
	}
}

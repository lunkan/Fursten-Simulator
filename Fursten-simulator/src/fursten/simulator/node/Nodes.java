package fursten.simulator.node;

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
	
	/*public static boolean equals(Node node) {
	
		if(node == null)
			return false;
		if(node.getX() != x)
			return false;
		if(node.getY() != y)
			return false;
		if(node.getR() != r)
			return false;
		
		return true;
	}
	
	public static boolean equals(Object object) {
		Node node = (Node)object;
		return equals(node);
	}
	
	public static boolean intersect(int[][] area) {
	
		if(area[0][0] > x || area[1][0] > y)
			return false;
		else if(area[0][0] + area[0][1] < x || area[1][0] + area[1][1] < y)
			return false;
		else
			return true;
	}*/
}

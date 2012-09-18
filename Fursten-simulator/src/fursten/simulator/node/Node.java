package fursten.simulator.node;

import java.io.Serializable;

public class Node implements Serializable {

	static final long serialVersionUID = 10275539472837495L;
	
	private int resourceReference;
	private int[] point;
	
	public Node(int resourceReference){
		this.resourceReference = resourceReference;
		point = new int[2];
	}
	
	public Node(int r, int x, int y){
		this.resourceReference = r;
		point = new int[2];
		point[0] = x;
		point[1] = y;
	}
	
	public int getR() {
		return resourceReference;
	}

	public int getX() {
		return point[0];
	}

	public int getY() {
		return point[1];
	}

	public void setX(int x) {
		point[0] = x;
	}

	public void setY(int y) {
		point[1] = y;
	}
	
	public boolean equals(Node node) {
		if(node == null)
			return false;
		if(node.getX() != point[0])
			return false;
		if(node.getY() != point[1])
			return false;
		if(node.getR() != resourceReference)
			return false;
		
		return true;
	}
	
	public boolean equals(Object object) {
		Node node = (Node)object;
		return equals(node);
	}
	
	public boolean intersect(int[][] area) {

		if(area[0][0] > point[0] || area[1][0] > point[1])
			return false;
		else if(area[0][0] + area[0][1] < point[0] || area[1][0] + area[1][1] < point[1])
			return false;
		else
			return true;
	}
	
	public Node clone() {
		Node newNode = new Node(resourceReference);
		newNode.setX(point[0]);
		newNode.setY(point[1]);
		return newNode;
	}
	
	public Node cloneToReference(int resourceReference) {
		Node newNode = new Node(resourceReference);
		newNode.setX(point[0]);
		newNode.setY(point[1]);
		return newNode;
	}
	
	public String toString() {
		return "Node [x:"+point[0]+" y:"+point[1]+" r:"+resourceReference+"]";
	}
}

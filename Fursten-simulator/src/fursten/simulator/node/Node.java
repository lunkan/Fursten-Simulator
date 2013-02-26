package fursten.simulator.node;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Node implements Serializable {

	static final long serialVersionUID = 10275539472837495L;
	
	private int r;
	private int x;
	private int y;
	/*private int[] point;*/
	
	
	public Node(){
	}
	
	public Node(int resource){
		this.r = resource;
		//point = new int[2];
	}
	
	public Node(int resource, int x, int y){
		this.r = resource;
		this.x = x;
		this.y = y;
		
		/*point = new int[2];
		point[0] = x;
		point[1] = y;*/
	}
	
	public int getR() {
		return r;
	}

	public int getX() {
		//return point[0];
		return x;
	}

	public int getY() {
		//return point[1];
		return y;
	}

	public void setR(int r) {
		//point[0] = x;
		this.r = r;
	}
	
	public void setX(int x) {
		//point[0] = x;
		this.x = x;
	}

	public void setY(int y) {
		//point[1] = y;
		this.y = y;
	}
	
	public boolean equals(Node node) {
		
		if(node == null)
			return false;
		if(node.getX() != x)
			return false;
		if(node.getY() != y)
			return false;
		if(node.getR() != r)
			return false;
		
		return true;
		
		/*if(node == null)
			return false;
		if(node.getX() != point[0])
			return false;
		if(node.getY() != point[1])
			return false;
		if(node.getR() != resource)
			return false;
		
		return true;*/
	}
	
	public boolean equals(Object object) {
		Node node = (Node)object;
		return equals(node);
	}
	
	public boolean intersect(int[][] area) {

		if(area[0][0] > x || area[1][0] > y)
			return false;
		else if(area[0][0] + area[0][1] < x || area[1][0] + area[1][1] < y)
			return false;
		else
			return true;
		
		/*if(area[0][0] > point[0] || area[1][0] > point[1])
			return false;
		else if(area[0][0] + area[0][1] < point[0] || area[1][0] + area[1][1] < point[1])
			return false;
		else
			return true;*/
	}
	
	public Node clone() {
		Node newNode = new Node(r);
		//newNode.setX(point[0]);
		//newNode.setY(point[1]);
		newNode.setX(x);
		newNode.setY(y);
		return newNode;
	}
	
	public Node cloneToReference(int resource) {
		Node newNode = new Node(resource);
		//newNode.setX(point[0]);
		//newNode.setY(point[1]);
		newNode.setX(x);
		newNode.setY(y);
		return newNode;
	}
	
	public String toString() {
		return "Node [x:"+x+" y:"+y+" r:"+r+"]";
		//return "Node [x:"+point[0]+" y:"+point[1]+" r:"+resource+"]";
	}
}

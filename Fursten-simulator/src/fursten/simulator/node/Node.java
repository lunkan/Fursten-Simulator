package fursten.simulator.node;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Node implements Serializable {

	static final long serialVersionUID = 10275539472837495L;
	
	private int r;
	private int x;
	private int y;
	
	
	public Node(){
	}
	
	public Node(int resource){
		this.r = resource;
	}
	
	public Node(int resource, int x, int y){
		this.r = resource;
		this.x = x;
		this.y = y;
	}
	
	public int getR() {
		return r;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setR(int r) {
		this.r = r;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
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
	}
	
	public Node clone() {
		Node newNode = new Node(r);
		newNode.setX(x);
		newNode.setY(y);
		return newNode;
	}
	
	public Node cloneToReference(int resource) {
		Node newNode = new Node(resource);
		newNode.setX(x);
		newNode.setY(y);
		return newNode;
	}
	
	public String toString() {
		return "Node [x:"+x+" y:"+y+" r:"+r+"]";
	}
}

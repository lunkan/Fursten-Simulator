package fursten.simulator.node;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Node implements Serializable {

	static final long serialVersionUID = 10275539472837495L;
	
	private int r;
	private int x;
	private int y;
	private float v;
	
	
	public Node(){
	}
	
	public Node(int resource){
		this.r = resource;
	}
	
	public Node(int resource, int x, int y, float v){
		this.r = resource;
		this.x = x;
		this.y = y;
		this.v = v;
	}
	
	public float getV() {
		return v;
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
	
	public void setV(float v) {
		this.v = v;
	}
	
	public Node clone() {
		Node newNode = new Node(r);
		newNode.setX(x);
		newNode.setY(y);
		newNode.setV(v);
		return newNode;
	}
	
	public Node cloneToReference(int resource) {
		Node newNode = new Node(resource);
		newNode.setX(x);
		newNode.setY(y);
		newNode.setV(v);
		return newNode;
	}
	
	public String toString() {
		return "Node [x:"+x+" y:"+y+" r:"+r+" v:"+v+"]";
	}
}

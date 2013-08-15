package fursten.simulator.sample;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Sample implements Serializable {

	static final long serialVersionUID = 10275539472837495L;
	
	private int r;
	private int x;
	private int y;
	private float v;
	private float stability;
	
	public Sample(){
	}
	
	public Sample(int resource, int x, int y, float v){
		this.r = resource;
		this.x = x;
		this.y = y;
		this.v = v;
	}
	
	public Sample(int resource, int x, int y, float v, float stability){
		this.r = resource;
		this.x = x;
		this.y = y;
		this.v = v;
		this.stability = stability;
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
	
	public float getV() {
		return v;
	}
	
	public float getStability() {
		return stability;
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
	
	public void setStability(float stability) {
		this.stability = stability;
	}
	
	public String toString() {
		return "Sample [x:"+x+" y:"+y+" r:"+r+" v:"+v+" stability:"+stability+"]";
	}
}
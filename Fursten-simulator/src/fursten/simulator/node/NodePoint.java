package fursten.simulator.node;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NodePoint implements Serializable {

	static final long serialVersionUID = 10275539472837495L;
	
	private int r;
	private int x;
	private int y;
	
	public NodePoint() {
		//...
	}
	
	public NodePoint(int x, int y, int r) {
		this.x = x;
		this.y = y;
		this.r = r;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + r;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodePoint other = (NodePoint) obj;
		if (r != other.r)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "NodePoint [r=" + r + ", x=" + x + ", y=" + y + "]";
	}
}

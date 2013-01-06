package fursten.simulator.instance;

import java.awt.Rectangle;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Instance implements Serializable {

	static final long serialVersionUID = 10275539472837495L;
	
	private String name;
	private int tick;
	private int width;
	private int height;

	public String getName() {
		if(name == null)
			return "Untitled";
			
		return name;
	}
	
	public Instance setName(String name) {
		this.name = name;
		return this;
	}
	
	public int getTick() {
		return tick;
	}
	
	public Instance setTick(int tick) {
		this.tick = tick;
		return this;
	}
	
	public int getWidth() {
		return width;
	}
	
	public Instance setWidth(int width) {
		this.width = width;
		return this;
	}
	
	public Instance setHeight(int height) {
		this.height = height;
		return this;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Rectangle getRect() {
		return new Rectangle(-getWidth()/2, -getHeight()/2, getWidth(), getHeight());
	}
	
	public String toString() {
		return "Session " + name + "@" + name + " " + getRect().toString() + ". current tick:"+tick;
	}
}

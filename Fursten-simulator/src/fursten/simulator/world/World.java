package fursten.simulator.world;

import java.awt.Rectangle;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class World implements Serializable {

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
	
	public World setName(String name) {
		this.name = name;
		return this;
	}
	
	public int getTick() {
		return tick;
	}
	
	public World setTick(int tick) {
		this.tick = tick;
		return this;
	}
	
	public int getWidth() {
		return width;
	}
	
	public World setWidth(int width) {
		this.width = width;
		return this;
	}
	
	public World setHeight(int height) {
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

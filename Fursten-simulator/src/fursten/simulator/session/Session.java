package fursten.simulator.session;

import java.awt.Rectangle;
import java.io.Serializable;

public class Session implements Serializable {

	static final long serialVersionUID = 10275539472837495L;
	public static final String DEFAULT_NAME = "Untitled";
	
	private String name;
	private int tick;
	private long id;
	
	private Rectangle bounds;
	
	public Session(){
		name = DEFAULT_NAME;
		tick = 0;
		id = System.currentTimeMillis();
		bounds = new Rectangle(-50000, -50000, 100000, 100000);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getId() {
		return id;
	}
	
	public int getTick() {
		return tick;
	}
	
	public void setTick(int tick) {
		this.tick = tick;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public String toString() {
		return "Session " + name + "@" + id + " " + bounds.toString() + ". current tick:"+tick;
	}
}

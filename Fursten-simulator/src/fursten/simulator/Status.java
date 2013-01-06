package fursten.simulator;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="status")
public class Status {

	private String name;
	private long id;
	private int width;
	private int height;
	private int tick;
	
	public String getName() {
		return name;
	}
	public Status setName(String name) {
		this.name = name;
		return this;
	}
	public long getId() {
		return id;
	}
	public Status setId(long id) {
		this.id = id;
		return this;
	}
	public int getWidth() {
		return width;
	}
	public Status setWidth(int width) {
		this.width = width;
		return this;
	}
	public int getHeight() {
		return height;
	}
	public Status setHeight(int height) {
		this.height = height;
		return this;
	}
	public int getTick() {
		return tick;
	}
	public Status setTick(int tick) {
		this.tick = tick;
		return this;
	}
}

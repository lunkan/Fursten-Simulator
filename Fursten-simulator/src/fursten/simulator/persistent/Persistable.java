package fursten.simulator.persistent;

public interface Persistable {

	public boolean hasChanged();
	public void clean();
	
	public void pullPersistent();
	public void pushPersistent();
}

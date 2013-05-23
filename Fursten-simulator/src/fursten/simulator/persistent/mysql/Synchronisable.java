package fursten.simulator.persistent.mysql;

public interface Synchronisable {

	public boolean hasChanged();
	public long getLastUpdate();
	public void clean();
	public void close();
	
	public void pullPersistent();
	public void pushPersistent();
}

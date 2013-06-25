package fursten.simulator.persistent;

import java.util.ArrayDeque;


public class AutoSaveManager implements Runnable {
	
	private int updateRate;
	private ArrayDeque<Persistable> persistables;
	private volatile boolean execute;
	
	public AutoSaveManager(int updateRate) {
		persistables = new ArrayDeque<Persistable>();
		this.updateRate = updateRate;
	}
	
	public void addPersistable(Persistable persistable) {
		persistables.add(persistable);
	}
	
	public boolean removePersistable(Persistable persistable) {
		return persistables.removeFirstOccurrence(persistable);
	}
	
	public void run() {
		
		this.execute = true;
		
		try {
			
			while(this.execute) {
				
				//Check every x seconds for changes to push to database
				Thread.sleep(updateRate);
				
				for (Persistable persistable : persistables) {
					if(persistable.hasChanged()) {
						persistable.pushPersistent();
					}
				}
			}
        }
		catch (InterruptedException e) {
            //("I wasn't done!");
			this.execute = false;
        }
	}
}

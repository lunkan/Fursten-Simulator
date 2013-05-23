package fursten.simulator.persistent.mysql;

public class PersistantSynchroniser implements Runnable {
	
	private Synchronisable synchronisable;
	
	public PersistantSynchroniser(Synchronisable parent) {
		this.synchronisable = parent;
	}
	
	public void run() {
		
		try {
			
			while(!Thread.interrupted()) {
				
				//Check every 20 seconds for changed nodeTRees to push to database
				Thread.sleep(20000);
				if(synchronisable.hasChanged()) {
					if(synchronisable.getLastUpdate() < System.currentTimeMillis()) {
						synchronisable.pushPersistent();
					}
				}
			}
        }
		catch (InterruptedException e) {
            //("I wasn't done!");
        }
	}
}

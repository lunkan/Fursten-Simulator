package fursten.simulator.persistent;

import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AutoSaveManager implements Runnable {
	
	private static final Logger logger = Logger.getLogger(AutoSaveManager.class.getName());
	
	private int updateRate;
	private ArrayDeque<Persistable> persistables;
	
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
		
		if(!Thread.interrupted())
			logger.log(Level.INFO, "AutoSave process started");
		else
			logger.log(Level.INFO, "AutoSave process could not start becouse thread is interrupted");
		
		while(!Thread.interrupted()) {
			
			int timeCounter = 0;
			while(timeCounter < updateRate) {
				
				try {
					Thread.sleep(2000);
					timeCounter += 2000;
			    } catch (InterruptedException e) {
			    	logger.log(Level.INFO, "AutoSaveManager interrupted");
			        return;
			    }
			}
		
			//Check every x seconds for changes to push to database
			boolean isUpdated = false;
			for (Persistable persistable : persistables) {
				if(persistable.hasChanged()) {
					persistable.pushPersistent();
					isUpdated = true;
				}
			}
			
			if(isUpdated)
				logger.log(Level.INFO, "Persistables uptaded. Database is in sync");
			//else
			//	logger.log(Level.INFO, "Persistables already up to date. Database is in sync");
		}
		
		logger.log(Level.INFO, "AutoSaveManager interrupted");
        return;
	}
}

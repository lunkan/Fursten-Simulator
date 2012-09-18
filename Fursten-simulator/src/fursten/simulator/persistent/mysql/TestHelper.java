package fursten.simulator.persistent.mysql;

import fursten.simulator.persistent.PersistentTestHelper;

public class TestHelper implements PersistentTestHelper {

	public boolean setUp() {
		
		DAOFactory.setDatabase("test");
		return false;
	}

	public boolean tearDown() {
		return false;
	}
}

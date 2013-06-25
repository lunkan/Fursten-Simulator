package fursten.simulator.persistent;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fursten.simulator.TestShutDown;
import fursten.simulator.TestStartup;
import fursten.simulator.world.World;

import junit.framework.TestCase;

public class TestWorldManager extends TestCase {

	private static final Logger logger = Logger.getLogger(TestWorldManager.class.getName());
	
	private WorldManager WM;
			
    @Before
    public void setUp() {
    	
        TestStartup.init();
        WM = DAOManager.get().getWorldManager();
    }

    @After
    public void tearDown() {
        TestShutDown.destroy();
    }
	
    @Test
    public void testSetGet() {
    	logger.log(Level.INFO, "Test testSetGet");
    	
    	World newWorld = new World();
    	newWorld.setName("testSetGetWorld");
    	newWorld.setWidth(10000);
    	newWorld.setHeight(5000);
    	
    	WM.set(newWorld);
    	assertEquals(WM.get(), newWorld);
    }
}



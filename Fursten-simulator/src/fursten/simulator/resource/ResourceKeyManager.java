package fursten.simulator.resource;

import java.math.BigInteger;
import java.util.Set;
import java.util.TreeSet;

import fursten.simulator.persistent.ResourceManager;
import fursten.simulator.persistent.mysql.DAOFactory;

public class ResourceKeyManager {
	
	private static ResourceKeyManager instance;
	private Set<Integer> resourceKeys;
	
	private ResourceKeyManager() {
		ResourceManager RM = DAOFactory.get().getResourceManager();
		resourceKeys = RM.getKeys();
	}
	
	private static ResourceKeyManager getInstance() {
		
		if(instance == null)
			instance = new ResourceKeyManager();
			
		return instance;
    }
	
	public static boolean containsKey(int key) {
		return getInstance()._containsKey(key);
	}
	
	public boolean _containsKey(int key) {
		return resourceKeys.contains(key);
	}
	
	public static Set<Integer> getChildren(int key) {
		return getInstance()._getChildren(key);
	}
	
	public TreeSet<Integer> _getChildren(int key) {
		
		TreeSet<Integer> results = new TreeSet<Integer>();
		int shift = Integer.numberOfTrailingZeros(key);
		
		for(Integer resourceKey : resourceKeys) {
			if(((resourceKey ^ key) >>> shift) == 0 && resourceKey != key) {
				results.add(resourceKey);
			}
		}
		
		return results;
	}
	
	public static Set<Integer> getParents(int key) {
		return getInstance()._getParents(key);
	}
	
	public TreeSet<Integer> _getParents(int key) {
		
		TreeSet<Integer> results = new TreeSet<Integer>();
		
		for(Integer resourceKey : resourceKeys) {
			
			int shift = Integer.numberOfTrailingZeros(resourceKey);
			if(((resourceKey ^ key) >>> shift) == 0 && resourceKey != key) {
				results.add(resourceKey);
			}
		}
		
		return results;
	}
	
	public static int getNext() {
		return getInstance()._getNext(0);
	}

	public static int getNext(int key) {
		return getInstance()._getNext(key);
	}
	
	public int _getNext(int key) {
		
		//if root key is '0' and lowest bit will return -1
		BigInteger resSelectKey = BigInteger.valueOf((long)key);
		int indexStart = 31;
		if(resSelectKey.getLowestSetBit() >= 0)
			indexStart = resSelectKey.getLowestSetBit()-1;
		
		for(int i=indexStart; i >= 0; i--) {
			
			BigInteger testKey = BigInteger.valueOf((long)key);
			testKey = testKey.setBit(i);
			
			if(!resourceKeys.contains(testKey.intValue())) {
				resourceKeys.add(new Integer(testKey.intValue()));//Must be added if multiple get next is called.
				return testKey.intValue();
			}
		}
		
		return -1;
	}
	
	public static void clear() {
		instance = new ResourceKeyManager();
	}
	
	public static boolean isDescendant(int descendantKey, int parentKey) {
		
		int shift = Integer.numberOfTrailingZeros(parentKey);
		if(descendantKey == parentKey)
			return false;
		else if((parentKey ^ descendantKey) >>> shift == 0)
			return true;
		else
			return false;
	}
	
	public static String keyToString(int key) {

		BigInteger cBig = BigInteger.valueOf((long)key);
		String keyString = "";
		for(int i=0; i < 32; i++) {
			if(cBig.testBit(i))
				keyString = keyString + "*";
			else
				keyString = keyString + "-";
		}

		return keyString;
	}
}
package fursten.simulator.resource;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class ResourceKeyManager {
	
	private Set<Integer> resourceKeys;
	
	public ResourceKeyManager() {
		this.resourceKeys = new HashSet<Integer>();
	}
	
	public ResourceKeyManager(Set<Integer> resourceKeys) {
		this.resourceKeys = resourceKeys;
	}
	
	public boolean containsKey(int key) {
		return resourceKeys.contains(key);
	}
	
	public TreeSet<Integer> getChildren(int key) {
		
		TreeSet<Integer> results = new TreeSet<Integer>();
		BigInteger selectBigKey = BigInteger.valueOf((long)key);
		int shift = selectBigKey.getLowestSetBit();
		
		for(Integer resourceKey : resourceKeys) {
			if(((resourceKey ^ key) >>> shift) == 0 && resourceKey != key) {
				results.add(resourceKey);
			}
		}
		
		return results;
	}
	
	public TreeSet<Integer> getParents(int key) {
		
		TreeSet<Integer> results = new TreeSet<Integer>();
		
		for(Integer resourceKey : resourceKeys) {
			
			BigInteger resBigKey = BigInteger.valueOf((long)resourceKey);
			int shift = resBigKey.getLowestSetBit();
			
			if(((resourceKey ^ key) >>> shift) == 0 && resourceKey != key) {
				results.add(resourceKey);
			}
		}
		
		return results;
	}
	
	public int getNext() {
		return getNext(0);
	}

	public int getNext(int key) {
		
		//if root key is '0' and lowest bit will return -1
		BigInteger resSelectKey = BigInteger.valueOf((long)key);
		int indexStart = 31;
		if(resSelectKey.getLowestSetBit() >= 0)
			indexStart = resSelectKey.getLowestSetBit()-1;
		
		for(int i=indexStart; i >= 0; i--) {
			
			BigInteger testKey = BigInteger.valueOf((long)key);
			testKey = testKey.setBit(i);
			
			if(!resourceKeys.contains(testKey.intValue())) {
				resourceKeys.add(testKey.intValue());//Must be added if multiple get next is called.
				return testKey.intValue();
			}
		}
		
		return -1;
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
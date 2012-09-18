package fursten.simulator.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceSelection {

	public enum Method {
		ALL, MATCH, CHILDREN, PARENTS, NEXT
	}
	
	private Set<Integer> keys;
	private Method method;
	
	public ResourceSelection() {
		keys = new HashSet<Integer>();
		method = Method.ALL;
	}
	
	public ResourceSelection(int key) {
		keys = new HashSet<Integer>();
		keys.add(key);
		method = Method.MATCH;
	}
	
	public ResourceSelection(Set<Integer> keys) {
		this.keys = keys;
		method = Method.MATCH;
	}
	
	public ResourceSelection(int key, Method method) {
		keys = new HashSet<Integer>();
		keys.add(key);
		this.method = method;
	}
	
	public ResourceSelection(Set<Integer> keys, Method method) {
		this.keys = keys;
		this.method = method;
	}
	
	public ResourceSelection setMethod(Method method) {
		this.method = method;
		return this;
	}
	
	public ResourceSelection addResourceKey(int resourceKey) {
		
		keys.add(resourceKey);
		if(this.method == Method.ALL)
			this.method = Method.MATCH;
		
		return this;
	}
	
	public ResourceSelection addResourceKeys(List<Integer> resourceKeys) {
		
		for(Integer resourceKey : resourceKeys)
			keys.add(resourceKey);
		
		if(this.method == Method.ALL)
			this.method = Method.MATCH;
		
		return this;
	}
	
	public Set<Integer> getResourceKeys() {
		return keys;
	}
	
	public Method getMethod() {
		return method;
	}
}

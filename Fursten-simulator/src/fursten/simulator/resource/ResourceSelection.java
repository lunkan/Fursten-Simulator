package fursten.simulator.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceSelection {

	private Set<Integer> keys;
	private ResourceSelectMethod method;

	public ResourceSelection() {
		keys = new HashSet<Integer>();
		method = ResourceSelectMethod.ALL;
	}

	public ResourceSelection(int key) {
		keys = new HashSet<Integer>();
		keys.add(key);
		method = ResourceSelectMethod.MATCH;
	}

	public ResourceSelection(Set<Integer> keys) {
		
		if(keys != null) {
			this.keys = keys;
			method = ResourceSelectMethod.MATCH;
		}
		else {
			new HashSet<Integer>();
			method = ResourceSelectMethod.ALL;
		}
	}

	public ResourceSelection(int key, ResourceSelectMethod method) {
		keys = new HashSet<Integer>();
		keys.add(key);
		this.method = method;
	}

	public ResourceSelection(Set<Integer> keys, ResourceSelectMethod method) {
		this.keys = keys;
		this.method = method;
	}

	public ResourceSelection setMethod(ResourceSelectMethod method) {
		this.method = method;
		return this;
	}

	public ResourceSelection addResourceKey(int resourceKey) {

		keys.add(resourceKey);
		if(this.method == ResourceSelectMethod.ALL)
			this.method = ResourceSelectMethod.MATCH;

		return this;
	}

	public ResourceSelection addResourceKeys(List<Integer> resourceKeys) {

		for(Integer resourceKey : resourceKeys)
			keys.add(resourceKey);

		if(this.method == ResourceSelectMethod.ALL)
			this.method = ResourceSelectMethod.MATCH;

		return this;
	}

	public Set<Integer> getResourceKeys() {
		return keys;
	}

	public ResourceSelectMethod getMethod() {
		return method;
	}
}
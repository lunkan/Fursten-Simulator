package fursten.simulator.resource;

public enum ResourceSelectMethod {
	
	ALL("all"),
	MATCH("match"),
	CHILDREN("children"),
	PARENTS("parents"),
	NEXT("next");
	
	public String value;

    private ResourceSelectMethod(String value) {
    	this.value = value;
    }
}

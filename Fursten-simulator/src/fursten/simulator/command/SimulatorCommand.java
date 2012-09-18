package fursten.simulator.command;

public interface SimulatorCommand {

	public Object execute() throws Exception;
	public String getName();
}

package fursten.simulator.persistent;

import java.util.List;
import fursten.simulator.session.Session;

public interface SessionManager {

	public int setActive(Session session);
	public Session getActive();
	public boolean clear();
	public List<Session> getHistory();
}

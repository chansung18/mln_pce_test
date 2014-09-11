package pcep.manager;

//import org.apache.log4j.Logger;

import pcep.PCEPConnection;
import pcep.PCEPSession;

/**
 * KEEPALIVE 메시지를 처리하는 메시지 처리자다.
 * 
 * @author Ancom
 * 
 */
public class KeepAliveMessageHandler extends MessageManager {

//	private final static Logger logger = Logger
//			.getLogger(KeepAliveMessageHandler.class);

	public KeepAliveMessageHandler(PCEPConnection con) {
		super(con);
	}

	/**
	 * KEEPALIVE 메시지를 수신했을 때, Connection 객체로 하여금 이를 처리하게 한다.
	 * 
	 * @see pcep.manager.MessageManager#handle(pcep.manager.PacketContext)
	 */
	@Override
	public void handle(PacketContext context) throws Exception {
		/*logger.debug("Session " + connection.getSessionAccessor().getSID()
				+ " : KEEPALIVE received from "
				+ connection.getServer().getAddress());*/

		if (connection.getSessionAccessor().getState() == PCEPSession.STATE_OPENWAIT) {
//			logger.debug("Illegal Open Message");
			connection.getSessionAccessor().throwError(1, 1);
			connection.getSessionAccessor().closeSession();
			return;
		}

		connection.receiveKeepAlive();
	}

}

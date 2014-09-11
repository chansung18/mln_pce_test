package pcep.manager;

//import org.apache.log4j.Logger;

import pcep.PCEPConnection;
import pcep.PCEPSession;
import pcep.packet.CommonObjectHeader;
import pcep.packet.ComputationUtils;
import pcep.packet.OpenObject;
import pcep.packet.PCEPObject;
import pcep.packet.PCEPObjectFactory;

/**
 * OPEN 메시지를 처리하는 메시지 처리자입니다.
 * 
 * @author Ancom
 * 
 */
public class OpenMessageHandler extends MessageManager {
//	private final static Logger logger = Logger
//			.getLogger(OpenMessageHandler.class);

	public OpenMessageHandler(PCEPConnection con) {
		super(con);
	}

	/**
	 * OPEN 메시지의 처리 과정 먼저 현재 세션의 상태를 살펴, SESSIONUP 상태일 경우 에러타입 1, 에러값 5의 에러 메시지를
	 * 전송한다. OPEN 메시지 안에 OPEN 객체가 없거나 제일 앞에 없는 경우, 에러타입 1, 에러값 1의 에러메시지를 전송한다.
	 * 
	 * @see pcep.manager.MessageManager#handle(pcep.manager.PacketContext)
	 */
	@Override
	public void handle(PacketContext context) throws Exception {
		OpenObject openObj = null;
		boolean isError = false;
//		if (logger.isDebugEnabled()) {
//			logger.debug("OPEN received.");
//			logger.debug("PacketHeader: " + context.getHeader());
//			logger.debug("PacketBody: "
//					+ ComputationUtils.prettyBytesToString(context
//							.getMessageContent()));
//		}

		if (connection.getSessionAccessor().getState() == PCEPSession.STATE_SESSIONUP) {
//			logger.debug("Second OPEN Message received");
			connection.getSessionAccessor().throwError(1, 5);
			return;
		}

		isError = untangle(context.getMessageContent());
		for (int i = 0; i < contentVector.size() && !isError; i += 2) {

			CommonObjectHeader coh = new CommonObjectHeader(contentVector
					.elementAt(i));

			PCEPObject obj = PCEPObjectFactory.createPCEPObject(
					PacketChain.OPEN_PT, coh, contentVector.elementAt(i + 1));

			// Message Handling
			// OPEN
			if (obj.getObjectClass() == PacketChain.OPEN_OC) {
				if (openObj == null) {
					openObj = (OpenObject) obj;
				} else {
					// error!
					connection.getSessionAccessor().throwError(1, 1);
					connection.getSessionAccessor().closeSession();
					break;
				}

			} else if (openObj == null) {
				// Doh! invalid message
				connection.getSessionAccessor().throwError(1, 1);
				connection.getSessionAccessor().closeSession();
			} else {
				connection.getSessionAccessor().throwError(1, 1);
				connection.getSessionAccessor().closeSession();
			}

			obj.handle(connection.getSessionAccessor());
		}
		if (!isError)
			connection.getSessionAccessor().reportMessageEnd(
					PacketChain.OPEN_PT);
		else {
			connection.getSessionAccessor().increaseUnknownMessageCounter();
			connection.getSessionAccessor().throwError(1, 1);
			connection.getSessionAccessor().closeSession();

		}

	}

}

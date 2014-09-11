package pcep.manager;

//import org.apache.log4j.Logger;

import pcep.PCEPConnection;
import pcep.PCEPSession;
import pcep.packet.CloseObject;
import pcep.packet.CommonObjectHeader;
import pcep.packet.ComputationUtils;
import pcep.packet.PCEPObject;
import pcep.packet.PCEPObjectFactory;

/**
 * CLOSE 메시지를 처리하는 메시지 처리자.
 * 
 * @author Ancom
 * 
 */
public class CloseMessageHandler extends MessageManager {
//	private final static Logger logger = Logger
//			.getLogger(CloseMessageHandler.class);

	/**
	 * PCEP연결을 기반으로 메시지 처리자 객체를 생성.
	 * 
	 * @param connection
	 */
	public CloseMessageHandler(PCEPConnection connection) {
		super(connection);
	}

	/**
	 * Close 메시지를 처리하는 함수. 메시지로부터 CLOSE 객체를 읽어들이고, 그것을 세션으로 전달하여 처리하게끔 한다.
	 * 
	 * @see pcep.manager.MessageManager#handle(pcep.manager.PacketContext)
	 */
	@Override
	public void handle(PacketContext context) throws Exception {
		CloseObject closeObj = null;
		boolean isError = false;

//		if (logger.isDebugEnabled()) {
//			logger.debug("Close received.");
//			logger.debug("PacketHeader: " + context.getHeader());
//			logger.debug("PacketBody: "
//					+ ComputationUtils.prettyBytesToString(context
//							.getMessageContent()));
//		}

		if (connection.getSessionAccessor().getState() == PCEPSession.STATE_OPENWAIT) {
//			logger.debug("Illegal Open Message");
			connection.getSessionAccessor().throwError(1, 1);
			connection.getSessionAccessor().closeSession();
			return;
		}

		isError = untangle(context.getMessageContent());
		connection.getSessionAccessor()
				.reportMessageStart(PacketChain.CLOSE_PT);
		for (int i = 0; i < contentVector.size() && !isError; i += 2) {

			CommonObjectHeader coh = new CommonObjectHeader(contentVector
					.elementAt(i));

			PCEPObject obj = PCEPObjectFactory.createPCEPObject(
					PacketChain.CLOSE_PT, coh, contentVector.elementAt(i + 1));

			// Message Handling
			// CLOSE
			if (obj.getObjectClass() == PacketChain.CLOSE_OC) {
				closeObj = (CloseObject) obj;

			} else if (closeObj == null) {
				isError = true;
				break;
				// connection.getSessionAccessor().throwError(6, 1);
			}
			// TODO : Process of Unknown Object
			else {
				isError = true;
				connection.getSessionAccessor().throwError(3, 1);
				break;
			}

			obj.handle(connection.getSessionAccessor());
		}
		if (!isError)
			connection.getSessionAccessor().reportMessageEnd(
					PacketChain.CLOSE_PT);
		else {
			connection.getSessionAccessor().increaseUnknownMessageCounter();
		}
	}

}

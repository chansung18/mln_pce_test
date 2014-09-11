package pcep.manager;

//import org.apache.log4j.Logger;

import pcep.PCEPConnection;
import pcep.PCEPSession;
import pcep.packet.CommonObjectHeader;
import pcep.packet.ComputationUtils;
import pcep.packet.NotificationObject;
import pcep.packet.NotifyCmpObj;
import pcep.packet.PCEPObject;
import pcep.packet.PCEPObjectFactory;
import pcep.packet.RPObject;

/**
 * PCNtf(Notification) 메시지를 처리하는 메시지 처리자.
 * 
 * @author Ancom
 * 
 */
public class PCNtfMessageHandler extends MessageManager {

//	private final static Logger logger = Logger
//			.getLogger(PCReqMessageHandler.class);

	public PCNtfMessageHandler(PCEPConnection connection) {
		super(connection);
	}

	/**
	 * PCNTF 메시지를 처리한다. PCNTF 메시지는 NOTIFICATION 객체와 RP 객체를 담을 수 있다. 이외의 경우는 오류
	 * 상황이며, 오류 메시지를 전송시킨다.
	 * 
	 * @see pcep.manager.MessageManager#handle(pcep.manager.PacketContext)
	 */
	@Override
	public void handle(PacketContext context) throws Exception {
		NotifyCmpObj notify = new NotifyCmpObj();
		NotificationObject no = null;
		RPObject rp = null;
		boolean isError = false;
//		if (logger.isDebugEnabled()) {
//			logger.debug("PCNtf received.");
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
				.reportMessageStart(PacketChain.PCNTF_PT);

		for (int i = 0; i < contentVector.size() && !isError; i += 2) {

			CommonObjectHeader coh = new CommonObjectHeader(contentVector
					.elementAt(i));

			PCEPObject obj = PCEPObjectFactory.createPCEPObject(
					PacketChain.PCNTF_PT, coh, contentVector.elementAt(i + 1));

			// Notify Handling
			// NOTIFICATION, RP
			if (obj.getObjectClass() == PacketChain.NOTIFICATION_OC) {
				// Start of Notification
				no = (NotificationObject) obj;
				if (notify == null)
					notify = new NotifyCmpObj(no);
				else
					notify.addNotificationObj(no);

			} else if (notify == null) {
				isError = true;
				break;
			} else if (obj.getObjectClass() == PacketChain.RP_OC) {
				// 
				rp = (RPObject) obj;
				if (notify != null)
					notify.addRequestID(rp);

			} else if (no == null) {
				isError = true;
				break;
			} else {
				isError = true;
				connection.getSessionAccessor().throwError(3, 1);
				break;
			}

			obj.handle(connection.getSessionAccessor());
		}

		if (no == null) {
			isError = true;
		}

		if (!isError) {
			connection.getSessionAccessor().addNotifyForRecv(notify);
			connection.getSessionAccessor().reportMessageEnd(
					PacketChain.PCNTF_PT);
		} else {
			connection.getSessionAccessor().increaseUnknownMessageCounter();

		}

//		logger.debug("PCNTF Message Handling Completed.");
	}
}

package pcep.manager;

//import org.apache.log4j.Logger;

import pcep.PCEPConnection;
import pcep.PCEPSession;
import pcep.packet.CommonObjectHeader;
import pcep.packet.ComputationUtils;
import pcep.packet.ErrorCmpObj;
import pcep.packet.OpenObject;
import pcep.packet.PCEPErrorObject;
import pcep.packet.PCEPObject;
import pcep.packet.PCEPObjectFactory;
import pcep.packet.RPObject;

/**
 * PCErr(PCEP 에러) 메시지를 처리하는 메시지 처리자.
 * 
 * @author Ancom
 * 
 */
public class PCErrMessageHandler extends MessageManager {

//	private final static Logger logger = Logger
//			.getLogger(PCReqMessageHandler.class);

	public PCErrMessageHandler(PCEPConnection connection) {
		super(connection);
	}

	/**
	 * PCERR 메시지를 처리한다. PCERR 메시지는, Error객체, RP 객체, 그리고 OPEN객체를 가질 수 있다. 이외의 경우는
	 * 오류를 발생시킨다.
	 * 
	 * 
	 * @see pcep.manager.MessageManager#handle(pcep.manager.PacketContext)
	 */
	@Override
	public void handle(PacketContext context) throws Exception {
		ErrorCmpObj error = new ErrorCmpObj();
		PCEPErrorObject oo = null;
		RPObject rp = null;
		boolean isError = false;
//		if (logger.isDebugEnabled()) {
//			logger.debug("PCERR received.");
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

//		isError = untangle(context.getMessageContent());

		connection.getSessionAccessor()
				.reportMessageStart(PacketChain.PCERR_PT);
		for (int i = 0; i < contentVector.size() && !isError; i += 2) {

			CommonObjectHeader coh = new CommonObjectHeader(contentVector
					.elementAt(i));

			PCEPObject obj = PCEPObjectFactory.createPCEPObject(
					PacketChain.PCERR_PT, coh, contentVector.elementAt(i + 1));

			// Message Handling
			// PCEPError, RP, OPEN....
			if (obj.getObjectClass() == PacketChain.PCEPERROR_OC) {
				// Start of Error
				oo = (PCEPErrorObject) obj;
				error.addErrorObj(oo);

			} else if (obj.getObjectClass() == PacketChain.RP_OC) {
				rp = (RPObject) obj;
				error.addRequestID(rp);

			} else if (obj.getObjectClass() == PacketChain.OPEN_OC) {
				OpenObject openObj = (OpenObject) obj;
				error.setOpenObj(openObj);
			} else if (oo == null) {
				// Doh!
				isError = true;
				break;
			} else {
				isError = true;
				connection.getSessionAccessor().throwError(3, 1);
				break;
			}

			obj.handle(connection.getSessionAccessor());
		}

		if (oo == null) {
			isError = true;
		}

		if (!isError) {
			connection.getSessionAccessor().addErrorForRecv(error);
			connection.getSessionAccessor().reportMessageEnd(
					PacketChain.PCERR_PT);
		} else {
			connection.getSessionAccessor().increaseUnknownMessageCounter();
		}

//		logger.debug("PCERR Message Handling Completed.");
	}
}

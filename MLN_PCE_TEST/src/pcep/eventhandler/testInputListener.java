package pcep.eventhandler;

import pcep.PCEPElement;
import pcep.packet.ERObject;
import pcep.packet.ErrorCmpObj;
import pcep.packet.NotifyCmpObj;
import pcep.packet.RequestCmpObj;
import pcep.packet.ResponseCmpObj;

public class testInputListener extends InputListener {

	public testInputListener(PCEPElement pce) {
		this.pce = pce;
	}

	@Override
	public void messageArrived(int sid, byte messageType) throws Exception {
		// Example...
//		logger.debug("Message Arrived from Session ID " + sid
//				+ " , Message Type = " + messageType + " from server "
//				+ pce.getPCEPSession(sid).getServer().getAddress());

		/** Getting RequestCmpObj **/
		RequestCmpObj reqobj = pce.getPCEPSession(sid).getSessionAccessor()
				.dequeueRecvRequestCmpObj();
		if (reqobj != null) {

			// RRO infomation
			reqobj.getRrObj();
		}

		/** Getting ResponseCmpObj **/
		ResponseCmpObj resobj = pce.getPCEPSession(sid).getSessionAccessor()
				.dequeueRecvResponseCmpObj();
		if (resobj != null) {
			// ERO
			// PathList is a vector.
			ERObject ero = resobj.getPathList().get(0).getEro();
			// Integer
			// ero.getSlu().getInterfaceID();
			// label
			// ero.getLabel().getLabelList();

			// IRO
			// SLU
			// resobj.getAttributes().getIrObj().getSlu();
		}

		/** Getting Notification **/
		NotifyCmpObj noti = pce.getPCEPSession(sid).getSessionAccessor()
				.dequeueRecvNotifyCmpObj();
		// First Notification
		if (noti != null)
			noti.getNotificationList().get(0);

		/** Getting ErrorInfo **/
		ErrorCmpObj err = pce.getPCEPSession(sid).getSessionAccessor()
				.dequeueRecvErrorCmpObj();
		if (err != null)
			err.getErrorList().get(0);
	}

}

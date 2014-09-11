package pcep.eventhandler;

import java.util.EventListener;

import pcep.manager.PacketChain;
import pcep.packet.PCEPErrorObject;
import pcep.packet.PCEPObject;

public class ErrorListener implements EventListener {

	/**
	 * 에러를 보고하는 함수.
	 * 
	 * @param errorType
	 *            에러의 종류입니다.
	 * @param errorValue
	 *            에러 값입니다.
	 */
	public void confirmError(byte errorType, byte errorValue) {
		confirmError(errorType, errorValue, null, -1);
	}

	/**
	 * 에러를 보고하는 함수.
	 * 
	 * @param errorType
	 *            에러의 종류입니다.
	 * @param errorValue
	 *            에러 값입니다.
	 * @param requestID
	 *            잘못된 Request의 ID입니다.
	 */
	public void confirmError(byte errorType, byte errorValue, int requestID) {
		confirmError(errorType, errorValue, null, requestID);
	}

	/**
	 * 에러를 보고하는 함수.
	 * 
	 * @param errorType
	 *            에러의 종류입니다.
	 * @param errorValue
	 *            에러값입니다.
	 * @param errObj
	 *            Error 정보객체입니다.
	 * @param requestID
	 *            잘못된 Request의 ID입니다.
	 */
	public void confirmError(byte errorType, byte errorValue,
			PCEPObject errObj, int requestID) {

		// Send Error
		PCEPErrorObject obj = new PCEPErrorObject(PacketChain.PCERR_PT,
				(byte) 0, errorType, errorValue);
		obj.setReqMissingTLV(requestID);

	}

}

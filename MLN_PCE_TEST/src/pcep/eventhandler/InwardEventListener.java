package pcep.eventhandler;

import java.util.EventListener;

import pcep.PCEPSession;

public interface InwardEventListener extends EventListener {

	/**
	 * 메시지가 도착했을 때 호출되는 함수. 사용자는 이 함수를 오버라이드하여, 메시지 수신시의 이벤트를 처리할 수 있다. 각 메시지
	 * 종류마다의 이벤트를 구현하여, 세션으로부터 데이터를 읽어들이고 메시지를 전송하는 것이 가능하다.
	 * 
	 * @param sid
	 *            메시지가 도착한 세션의 ID
	 * @param messageType
	 *            도착한 메시지의 종류
	 * @throws Exception
	 */
	void messageArrived(int sid, byte messageType) throws Exception;

	/**
	 * 세션이 만들어졌을 때 호출되는 함수. 사용자는 이 함수를 오버라이드함으로써 세션 생성 시의 이벤트를 처리할 수 있다.
	 * 
	 * @param sid
	 *            연결 완료된 세션의 ID
	 * @throws Exception
	 */
	void sessionUp(int sid) throws Exception;

	/**
	 * 세션이 끊어졌을 때 호출되는 함수. 사용자는 이 함수를 오버라이드함으로써 세션 종료 시의 이벤트를 처리할 수 있다.
	 * 
	 * @param s
	 *            연결이 끊어진 세션
	 * @throws Exception
	 */
	void sessionDown(PCEPSession s) throws Exception;

}
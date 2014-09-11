package pcep.eventhandler;

import java.util.EventListener;

public interface ConnectionListener extends EventListener {

	/**
	 * 연결이 성공적으로 완료되었음을 알리는 함수.
	 */
	void connectionEstablished() throws Exception;

	/**
	 * 연결이 정상적으로 종료되었음을 알리는 함수.
	 */
	void connectionClosed() throws Exception;

	/**
	 * 연결 중에 예상치 못한 에러가 발생했음을 알리는 함수.
	 */
	void connectionError(Exception e) throws Exception;

}
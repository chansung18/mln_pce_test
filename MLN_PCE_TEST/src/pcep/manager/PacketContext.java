package pcep.manager;

import pcep.PCEPSession;
import pcep.PCEPSession.SessionAccessor;
import pcep.packet.PCEPCommonHeader;

/**
 * 메시지 공통 헤더와 메시지 내용을 저장하는 객체. 메시지 내용은 바이너리로 저장되어있습니다.
 * 
 * @author Ancom
 */
public class PacketContext {
	/**
	 * 세션.
	 * 
	 * @uml.property name="session"
	 * @uml.associationEnd
	 */
	private PCEPSession session = null;
	/**
	 * 메시지 공통헤더
	 * 
	 * @uml.property name="header"
	 * @uml.associationEnd
	 */
	private PCEPCommonHeader header = null;
	/**
	 * 메시지 내용. 바이너리.
	 * 
	 * @uml.property name="messageContent"
	 */
	private byte[] messageContent = null;

	/**
	 * PCEP메시지 공통헤더와 메시지 내용(바이트)를 가지고 메시지 내용 객체를 생성합니다.
	 * 
	 * @param session
	 *            해당 세션.
	 * @param header
	 *            메시지 공통헤더.
	 * @param messageContent
	 *            메시지 내용.
	 */
	public PacketContext(PCEPSession session, PCEPCommonHeader header,
			byte[] messageContent) {
		if (session == null)
			throw new NullPointerException("session cannot be null");
		if (header == null)
			throw new NullPointerException("header cannot be null");
		if (messageContent == null)
			throw new NullPointerException("messageContent cannot be null");
		this.session = session;
		this.header = header;
		this.messageContent = messageContent;
	}

	// 
	/*
	 * public SessionAccessor getSessionAccessor() { return
	 * session.getSessionAccessor(); }
	 */

	/**
	 * 메시지의 공통헤더를 반환하는 함수.
	 * 
	 * @return
	 * @uml.property name="header"
	 */
	public PCEPCommonHeader getHeader() {
		return header;
	}

	/**
	 * 메시지 내용을 반환하는 함수.
	 * 
	 * @return
	 * @uml.property name="messageContent"
	 */
	public byte[] getMessageContent() {
		return messageContent;
	}

	/**
	 * 세션 접근자를 반환하는 함수.
	 * 
	 * @return
	 */
	public SessionAccessor getSessionAccessor() {
		return session.getSessionAccessor();
	}

}

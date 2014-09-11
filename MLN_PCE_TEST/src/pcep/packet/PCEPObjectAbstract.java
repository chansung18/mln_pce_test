package pcep.packet;

import java.util.Iterator;

import pcep.PCEPSession.SessionAccessor;

/**
 * PCEP 정보 객체와 복합 정보 객체의 추상 클래스.
 * 
 * @author Ancom
 */
public abstract class PCEPObjectAbstract {

	/**
	 * 정보객체들의 공통객체헤더.
	 * 
	 * @uml.property name="header"
	 * @uml.associationEnd
	 */
	protected CommonObjectHeader header;
	/**
	 * 메시지 타입.
	 * 
	 * @uml.property name="messageType"
	 */
	protected byte messageType;

	public PCEPObjectAbstract() {
	}

	/**
	 * 메시지 타입과 바이너리 정보를 가지고 PCEP정보객체를 생성하는 생성자. 생성 후 객체 내용을 분석합니다.
	 * 
	 * @param messageType
	 *            메시지 타입.
	 * @param data
	 *            바이너리 정보. 공통객체헤더와 객체 내용을 포함합니다.
	 */
	public PCEPObjectAbstract(byte messageType, byte[] data) {
		this.messageType = messageType;
		byte[] headerdata = new byte[4];
		byte[] objdata = new byte[4];
		for (int i = 0; i < 4; i++) {
			headerdata[i] = data[i];
			objdata[i] = data[i + 4];
		}
		header = new CommonObjectHeader(headerdata);
		analyze(objdata);
	}

	/**
	 * 객체 클래스를 반환합니다.
	 * 
	 * @return 객체 클래스 번호.
	 */
	public abstract byte getObjectClass();

	/**
	 * 객체 타입을 반환합니다.
	 * 
	 * @return 객체 타입 번호
	 */
	public abstract byte getObjectType();

	/**
	 * 정보객체의 길이를 반환합니다.
	 * 
	 * @return 객체의 길이.
	 */
	public abstract int getLength();

	/**
	 * 객체의 헤더와 내용을 바이너리 형태로 반환합니다.
	 * 
	 * @return byte 배열의 바이너리 정보
	 */
	public abstract byte[] getBinaryContents();

	/**
	 * 객체 내용을 분석하는 함수. 이 함수는 바이너리 형태의 객체 내용을 분석하여 각 객체의 구성요소에해당 값을 할당합니다.
	 * 
	 * @param data
	 */
	public abstract void analyze(byte[] data);

	/**
	 * 정보객체를 추가하는 함수. 복합정보객체(PCEPObjectComposite)가 사용합니다.
	 * 
	 * @see pcep.packet.PCEPObjectComposite
	 * @param obj
	 *            추가할 정보객체.
	 */
	protected void addObject(PCEPObjectAbstract obj) {
	}

	/**
	 * 정보객체를 제거하는 함수. 복합정보객체(PCEPObjectComposite)가 사용합니다.
	 * 
	 * @see pcep.packet.PCEPObjectComposite
	 * @param obj
	 *            제거할 정보객체.
	 */
	protected void removeObject(PCEPObjectAbstract obj) {

	}

	/**
	 * 복합정보객체 안에 들어있는 정보객체들에 대한 반복자입니다. 복합정보객체(PCEPObjectComposite)가 사용합니다.
	 * 
	 * @see pcep.packet.PCEPObjectComposite
	 * @return
	 */
	public Iterator<PCEPObjectAbstract> getIterator() {
		return null;
	}

	public abstract void handle(SessionAccessor sa);
}

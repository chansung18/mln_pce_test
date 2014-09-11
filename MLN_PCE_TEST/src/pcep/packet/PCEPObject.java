package pcep.packet;

import java.util.Iterator;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;

/**
 * PCEP 정보 객체들의 추상 클래스. 모든 PCEP 정보 객체들은 다음과 같은 함수들을 갖고 있습니다. - 메시지타입에 따라 PCEP정보
 * 객체를 생성하는 생성자. - 객체를 분석하여 처리하는 함수.
 * 
 * @author Ancom
 */
public abstract class PCEPObject extends PCEPObjectAbstract {
//	private final static Logger logger = Logger.getLogger(PCEPObject.class);
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

	public PCEPObject() {
	}

	/**
	 * 메시지 타입과 바이너리 정보를 가지고 PCEP정보객체를 생성하는 생성자. 생성 후 객체 내용을 분석합니다.
	 * 
	 * @param messageType
	 *            메시지 타입.
	 * @param data
	 *            바이너리 정보. 공통객체헤더와 객체 내용을 포함합니다.
	 */
	public PCEPObject(byte messageType, byte[] data) {
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
	 * 메시지 타입과 공통객체헤더, 객체 내용을 받아 PCEP 정보객체를 생성하는 생성자. 정보객체를 생성한 후 객체 내용을 분석합니다.
	 * 
	 * @param messageType
	 *            메시지 타입.
	 * @param header
	 *            공통객체 헤더.
	 * @param content
	 *            바이너리 형태의 객체 내용.
	 */
	public PCEPObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	/**
	 * 객체 클래스를 반환합니다.
	 * 
	 * @return 객체 클래스 번호.
	 */
	@Override
	public abstract byte getObjectClass();

	/**
	 * 객체 타입을 반환합니다.
	 * 
	 * @return 객체 타입 번호
	 */
	@Override
	public abstract byte getObjectType();

	/**
	 * 정보객체의 길이를 반환합니다.
	 * 
	 * @return 객체의 길이.
	 */
	@Override
	public int getLength() {
		// return CommonObjectHeader.getLength() + header.getObjectLength();
		return header.getLength();
	}

	/**
	 * 객체 내용을 반환합니다.
	 * 
	 * @return
	 */
	public abstract byte[] getContents();

	/**
	 * 공통객체헤더를 반환합니다.
	 * 
	 * @return 공통객체헤더 객체.
	 * @uml.property name="header"
	 */
	public CommonObjectHeader getHeader() {
		return header;
	}

	/**
	 * 공통객체헤더의 내용을 바이너리 형태로 반환합니다.
	 * 
	 * @return byte 배열의 헤더의 바이너리 정보
	 */
	public byte[] getObjHeaderContents() {
		return header.getContents();
	}

	/**
	 * 객체의 헤더와 내용을 바이너리 형태로 반환합니다.
	 * 
	 * @return byte 배열의 바이너리 정보
	 */
	@Override
	public byte[] getBinaryContents() {
		try {
			int l = getLength();
			if (l < 1) {
				l = CommonObjectHeader.getHeaderLength();
//				logger
//						.warn("Warning : Object length on header is 0 or negative!");
				return null;
			}
			byte[] data = new byte[l];
			byte[] headerdata = getObjHeaderContents();
			byte[] objdata = getContents();

			int index = 0;
			for (int i = 0; i < CommonObjectHeader.getHeaderLength(); i++)
				data[index++] = headerdata[i];
			for (int i = 0; i < header.getObjectLength(); i++)
				data[index++] = objdata[i];
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 객체 내용을 분석하는 함수. 이 함수는 바이너리 형태의 객체 내용을 분석하여 각 객체의 구성요소에해당 값을 할당합니다.
	 * 
	 * @param data
	 */
	@Override
	public abstract void analyze(byte[] data);

	@Override
	protected void addObject(PCEPObjectAbstract obj) {
	}

	@Override
	protected void removeObject(PCEPObjectAbstract obj) {

	}

	/**
	 * 복합정보객체 안에 들어있는 정보객체들에 대한 반복자입니다. 복합정보객체(PCEPObjectComposite)가 사용합니다.
	 * 
	 * @see pcep.packet.PCEPObjectComposite
	 * @return
	 */
	@Override
	public Iterator<PCEPObjectAbstract> getIterator() {
		return null;
	}

	/**
	 * 객체가 들어있는 메시지의 메시지 타입.
	 * 
	 * @return
	 * @uml.property name="messageType"
	 */
	public byte getMessageType() {
		return messageType;
	}

	@Override
	public abstract void handle(SessionAccessor sa);
}

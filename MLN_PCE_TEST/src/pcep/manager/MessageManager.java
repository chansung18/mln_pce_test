package pcep.manager;

import java.util.Vector;

//import org.apache.log4j.Logger;

import pcep.PCEPConnection;
import pcep.packet.CommonObjectHeader;
import pcep.packet.PCEPObjectComposite;

/**
 * 메시지 처리자의 추상 클래스입니다.
 * 
 * @author Ancom
 */
public abstract class MessageManager {
//	private final static Logger logger = Logger.getLogger(MessageManager.class);
	/**
	 * PCEP 연결 객체.
	 * 
	 * @uml.property name="connection"
	 * @uml.associationEnd
	 */
	protected PCEPConnection connection = null;

	/**
	 * 내용 벡터. 메시지 내용 객체의 바이너리 정보를 잘라 저장할 때 쓰입니다.
	 */
	protected Vector<byte[]> contentVector;

	/**
	 * PCEP정보객체의 집합체.
	 * 
	 * @uml.property name="objComposite"
	 * @uml.associationEnd
	 */
	protected PCEPObjectComposite objComposite;

	/**
	 * PCEP연결객체를 가지고 메시지 처리자를 초기화.
	 * 
	 * @param connection
	 */
	public MessageManager(PCEPConnection connection) {
		this.connection = connection;
		this.contentVector = new Vector<byte[]>();
		this.objComposite = new PCEPObjectComposite();
	}

	/**
	 * 메시지 종류에 따라 메시지 내용으로부터 정보를 추출하여 처리하는 함수.
	 * 
	 * @param context
	 *            처리할 메시지 내용.
	 * @throws Exception
	 */
	abstract void handle(PacketContext context) throws Exception;

	/**
	 * 바이너리 정보로부터 헤더와 내용을 분리하여 내용 벡터에 담는 함수.
	 * 
	 * @param content
	 *            바이너리 정보.
	 * @return boolean 에러 여부
	 */
	boolean untangle(byte[] content) {

		int index = 0;
		contentVector.clear();
		while (index < content.length) {

			int objLength = 0;
			try {
				byte[] objHeader = new byte[CommonObjectHeader
						.getHeaderLength()];
				byte[] obj;

				for (int i = 0; i < CommonObjectHeader.getHeaderLength(); i++)
					objHeader[i] = content[index++];

				CommonObjectHeader coh = new CommonObjectHeader(objHeader);

				objLength = coh.getObjectLength();

				obj = new byte[objLength];

				// object
				for (int i = 0; i < objLength; i++) {
					//logger.debug("content index obj : " + index	+ " objLength : " + objLength + " i  : " + i);
					obj[i] = content[index++];

				}
				// Add : Even is header, Odd is obj.
				contentVector.add(objHeader);
				contentVector.add(obj);
			} catch (NegativeArraySizeException e) {
//				logger.warn("Warning...");
//				logger.warn("Negative Array Size Exception Occurred...");
//				logger.warn("Check the sender's packet.");
//				logger.warn("Object's header may have an error or be damaged.");
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return false;

	}
}

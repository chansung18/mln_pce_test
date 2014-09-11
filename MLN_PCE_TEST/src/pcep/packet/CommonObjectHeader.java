package pcep.packet;

//import org.apache.log4j.Logger;

/**
 * 공통객체헤더. 이 헤더는 PCEP 정보 객체의 첫 부분에 붙어있으며, 길이는 4byte(32bit)입니다.
 * 
 * @author Ancom
 */
public class CommonObjectHeader {
//	private final static Logger logger = Logger
//			.getLogger(CommonObjectHeader.class);
	private static int COMMONOBJECTHEADER_LENGTH = 4;

	/**
	 * @uml.property name="objectClass"
	 */
	private byte objectClass = -1;
	/**
	 * @uml.property name="ot"
	 */
	private byte ot = -1;
	/**
	 * @uml.property name="res"
	 */
	private byte res = -1;
	/**
	 * @uml.property name="p"
	 */
	private boolean p;
	/**
	 * @uml.property name="i"
	 */
	private boolean i;

	/**
	 * 내용의 길이. 이 값은 헤더의 길이를 포함한다.
	 * 
	 * @uml.property name="objectLength"
	 */
	private int objectLength = -1; // bytes

	/**
	 * 바이너리 정보로부터 공통객체헤더를 생성.
	 * 
	 * @param data
	 *            바이너리 형태의 공통객체헤더 정보.
	 */
	public CommonObjectHeader(byte[] data) {
		// 4byte
		try {
			objectClass = data[0];
			ot = ComputationUtils.bitsToByte(data[1], 4, 4);
			res = ComputationUtils.bitsToByte(data[1], 2, 2);
			p = ComputationUtils.bitsToByte(data[1], 1, 1) == 1 ? true : false;
			i = ComputationUtils.bitsToByte(data[1], 0, 1) == 1 ? true : false;

			objectLength = ComputationUtils.byteToShort(data, 2);
		} catch (ArrayIndexOutOfBoundsException e) {
//			logger.warn("Array Index Out of Bounds Exception Occurred...");
//			logger.warn("Check the sender's packet.");
//			logger.warn("Object's header may have an error or be damaged.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 헤더의 구성요소들을 직접 인자로 받아 공통객체헤더를 생성.
	 * 
	 * @param objectClass
	 *            객체클래스 번호.
	 * @param ot
	 *            객체 타입.
	 * 
	 * @param p
	 *            P flag
	 * @param i
	 *            I flag
	 * @param length
	 *            PCEP 객체의 길이. 길이는 헤더의 길이(4Byte) 포함.
	 */
	public CommonObjectHeader(byte objectClass, byte ot, boolean p, boolean i,
			int length) {
		this.objectClass = objectClass;
		this.ot = ot;
		this.res = 0;
		this.p = p;
		this.i = i;
		this.objectLength = length;
	}

	/**
	 * 헤더의 내용을 바이너리 형태로 반환합니다.
	 * 
	 * @return byte 배열 형태의 바이너리 정보.
	 */
	public byte[] getContents() {
		byte[] data = new byte[4];

		data[0] = objectClass;
		data[1] = (byte) ((ot << 4) + (res << 2) + (p ? (1 << 1) : (0 << 1)) + (i ? 1
				: 0));
		// BigE
		data[2] = ComputationUtils.intToByte(objectLength)[2];
		data[3] = ComputationUtils.intToByte(objectLength)[3];

		return data;
	}

	/**
	 * 클래스 번호를 반환합니다.
	 * 
	 * @return 객체의 클래스 번호
	 * @uml.property name="objectClass"
	 */
	public byte getObjectClass() {
		return objectClass;
	}

	/**
	 * 객체 타입을 반환합니다.
	 * 
	 * @return 객체의 타입
	 * @uml.property name="ot"
	 */
	public byte getOt() {
		return ot;
	}

	/**
	 * @return
	 * @uml.property name="res"
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private byte getRes() {
		return res;
	}

	/**
	 * P 플래그를 반환한다.
	 * 
	 * @return P flag의 값
	 * @uml.property name="p"
	 */
	public boolean getP() {
		return p;
	}

	/**
	 * I 플래그를 반환한다.
	 * 
	 * @return I 플래그의 값
	 * @uml.property name="i"
	 */
	public boolean getI() {
		return i;
	}

	/**
	 * 객체 클래스 번호를 설정한다.
	 * 
	 * @param objectClass
	 *            객체 클래스 번호
	 */
	public void setObjectClass(byte objectClass) {
		this.objectClass = objectClass;
	}

	/**
	 * 객체 타입을 설정한다.
	 * 
	 * @param ot
	 *            객체 타입 번호
	 */
	public void setOt(byte ot) {
		this.ot = ot;
	}

	@Deprecated
	public void setRes(byte res) {
		this.res = res;
	}

	/**
	 * P 플래그를 설정한다. PCREQ/PCREP 메시지 안의 객체들에 대해서는 참으로 설정되어야 하며, PCNTF/PCERR 메시지
	 * 안의 객체들에 대해서는 거짓으로 설정되어야 한다. 만약 이에 어긋나면, 오류 메시지를 발송시킨다.
	 * 
	 * @param p
	 *            P 플래그의 값(참/거짓)
	 */
	public void setP(boolean p) {
		this.p = p;
	}

	/**
	 * I 플래그를 설정한다.
	 * 
	 * @param i
	 *            I 플래그의 값(참/거짓)
	 */
	public void setI(boolean i) {
		this.i = i;
	}

	/**
	 * 공통객체헤더의 길이(4Byte)를 반환.
	 * 
	 * @return 공통객체헤더의 길이, 4 Byte
	 */
	public static int getHeaderLength() {
		return COMMONOBJECTHEADER_LENGTH;
	}

	/**
	 * 객체의 길이를 반환.
	 * 
	 * @return 객체의 길이.
	 * @uml.property name="objectLength"
	 */
	public int getLength() {
		return objectLength;
	}

	/**
	 * 객체만의 길이(헤더 길이를 뺀 나머지)를 반환.
	 * 
	 * @return 객체의 길이에서 헤더 길이를 뺀 값.
	 * @uml.property name="objectLength"
	 */
	public int getObjectLength() {
		return objectLength - COMMONOBJECTHEADER_LENGTH;
	}

	/**
	 * 객체의 길이를 설정.
	 * 
	 * @param length
	 *            객체의 길이. 헤더의 길이 포함.
	 * @uml.property name="objectLength"
	 */
	public void setLength(int length) {
		objectLength = length;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[ObjectType: " + objectClass + ", ObjectLength: "
				+ objectLength + "]";
	}

}

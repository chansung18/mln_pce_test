package pcep.packet;

import pcep.PCEPElement;

/**
 * PCEP 메시지들의 공통헤더입니다. 이 헤더는 PCEP메시지의 첫 부분에 붙어, 메시지의 종류, 길이 등을 알려줍니다.
 * 
 * @author Ancom
 */
public class PCEPCommonHeader {

	/**
	 * 메시지공통헤더의 길이입니다.
	 */
	private static int COMMONHEADER_LENGTH = 4;// bytes
	/**
	 * @uml.property name="ver"
	 */
	private int ver = -1;
	/**
	 * @uml.property name="flags"
	 */
	private byte flags = -1;
	/**
	 * @uml.property name="type"
	 */
	private byte type = -1;

	// Length of Context
	/**
	 * @uml.property name="packetLength"
	 */
	private int packetLength = -1;

	/**
	 * 바이너리 정보로부터 메시지공통헤더를 생성합니다.
	 * 
	 * @param data
	 *            바이너리 형태의 메시지공통헤더
	 */
	public PCEPCommonHeader(byte[] data) {
		// 4 byte(32 bits)
		ver = ComputationUtils.bitsToByte(data[0], 5, 3);
		flags = ComputationUtils.bitsToByte(data[0], 0, 5);
		type = (byte) ComputationUtils.unsignedByteToInt(data[1]);
		packetLength = ComputationUtils.byteToShort(data, 2);

	}

	/**
	 * 메시지공통헤더의 구성 요소를 직접 인자로 입력받아 메시지공통헤더를 생성합니다.
	 * 
	 * @param ver
	 *            PCEP 버젼.
	 * @param flags
	 *            플래그.
	 * @param type
	 *            메시지 타입.
	 * @param length
	 *            메시지 길이.
	 */
	public PCEPCommonHeader(int ver, byte flags, byte type, int length) {
		this.ver = ver;
		this.flags = flags;
		this.type = type;
		this.packetLength = length;
	}

	/**
	 * 메시지공통헤더의 내용을 바이너리 형태로 반환합니다.
	 * 
	 * @return byte 배열 형태의 바이너리 정보
	 */
	public byte[] getContents() {
		byte[] data = new byte[4];

		data[0] = (byte) ((ver << 5) + flags);
		data[1] = type;
		// BigE
		data[2] = ComputationUtils.intToByte(packetLength)[2];
		data[3] = ComputationUtils.intToByte(packetLength)[3];

		return data;
	}

	/**
	 * 헤더 내에 기록된 PCEP 버젼을 반환합니다.
	 * 
	 * @return PCEP 버젼
	 * @uml.property name="ver"
	 */
	public int getVer() {
		return ver;
	}

	/**
	 * 플래그를 반환합니다.
	 * 
	 * @return 플래그
	 * @uml.property name="flags"
	 */
	public byte getFlags() {
		return flags;
	}

	/**
	 * 메시지 타입을 반환합니다.
	 * 
	 * @return 메시지의 타입.
	 * @uml.property name="type"
	 */
	public byte getType() {
		return type;
	}

	/**
	 * 메시지의 길이를 반환합니다.
	 * 
	 * @return 메시지의 길이
	 * @uml.property name="packetLength"
	 */
	public int getLength() {
		return packetLength;
	}

	/**
	 * 메시지만의 길이(공통헤더길이를 뺀 나머지 값)를 반환합니다.
	 * 
	 * @return 메시지의 길이에서 메시지공통헤더의 길이를 뺀 값
	 * @uml.property name="packetLength"
	 */
	public int getPacketLength() {
		return packetLength - COMMONHEADER_LENGTH;
	}

	/**
	 * 메시지공통헤더의 길이를 반환합니다.
	 * 
	 * @return 메시지공통헤더의 길이(4 byte)
	 */
	public static int getHeaderLength() {
		return COMMONHEADER_LENGTH;
	}

	/**
	 * 메시지 정보와 메시지 길이를 가지고 PCEP 메시지공통헤더를 생성합니다. 버젼은 PCEP 객체로부터 받아오며, 플래그는 0으로
	 * 고정됩니다.
	 * 
	 * @param type
	 *            메시지 타입.
	 * @param length
	 *            메시지 길이.
	 * @return 메시지공통헤더 객체.
	 */
	public static PCEPCommonHeader createPCEPCommonHeader(byte type,
			int packetlength) {
		PCEPCommonHeader hd = new PCEPCommonHeader(PCEPElement.VERSION,
				(byte) 0, type, packetlength + COMMONHEADER_LENGTH);
		return hd;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[Version: " + ver + ", PacketType: " + type
				+ ", packetLength: " + packetLength + "]";
	}

}

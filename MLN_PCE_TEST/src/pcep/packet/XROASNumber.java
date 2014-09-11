package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * XRO의 SubObject 중 하나로, AS Number를 저장하는 서브정보객체.
 * 
 * @author Ancom
 * 
 */
public class XROASNumber extends XROSubObj {

	private byte[] asNum;

	public XROASNumber(boolean x, byte messageType, byte length) {
		super(x, messageType, length);
		type = 32;
		length = 4;
		asNum = new byte[2];

	}

	public XROASNumber(boolean x, byte messageType, byte length, byte[] data) {
		super(x, messageType, length, data);
		type = 32;
		length = 4;
		asNum = new byte[2];

	}

	public XROASNumber(byte[] data) {
		super(data);
	}

	@Override
	public byte[] getContent() {
		byte[] data = new byte[length];
		data[0] = (byte) (((x) ? 1 : 0) << 7);
		data[0] += type;
		data[1] = length;
		data[2] = asNum[0];
		data[3] = asNum[1];

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		x = ComputationUtils.bitsToByte(data[0], 7, 1) == 1 ? true : false;
		type = ComputationUtils.bitsToByte(data[0], 0, 7);
		length = data[1];
		for (int i = 0; i < 2; i++)
			asNum[i] = data[i + 2];
	}

	@Override
	public void handle(SessionAccessor sa) {
		// TODO Auto-generated method stub

	}

	public byte[] getAsNum() {
		return asNum;
	}

	public void setAsNum(byte[] asNum) {
		this.asNum = asNum;
	}

}

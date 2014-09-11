package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * RRO의 서브 정보객체중 하나. AS 번호를 저장한다.
 * 
 * @author Ancom
 * 
 */
public class RROASNumber extends RROSubObj {

	private byte[] asNum;

	public RROASNumber(boolean l, byte messageType, byte length) {
		super(l, messageType, length);
		type = 32;
		length = 4;
		asNum = new byte[2];

	}

	public RROASNumber(boolean l, byte messageType, byte length, byte[] data) {
		super(l, messageType, length, data);
		type = 32;
		length = 4;
		asNum = new byte[2];

	}

	public RROASNumber(byte[] data) {
		super(data);
	}

	@Override
	public byte[] getContent() {
		byte[] data = new byte[length];
		data[0] = (byte) (((l) ? 1 : 0) << 7);
		data[0] += type;
		data[1] = length;
		data[2] = asNum[0];
		data[3] = asNum[1];

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		l = ComputationUtils.bitsToByte(data[0], 7, 1) == 1 ? true : false;
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

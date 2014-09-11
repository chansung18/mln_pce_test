package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * XRO의 SubObject 중 하나로, SRLG를 저장하는 서브정보객체.
 * 
 * @author Ancom
 * 
 */
public class XROSRLG extends XROSubObj {

	private int srlgID;
	private byte attribute;

	public XROSRLG(boolean x, byte messageType, byte length) {
		super(x, messageType, length);
		type = 34;
		length = 4;

	}

	public XROSRLG(boolean x, byte messageType, byte length, byte[] data) {
		super(x, messageType, length, data);
		type = 34;
		length = 4;

	}

	public XROSRLG(byte[] data) {
		super(data);
	}

	@Override
	public void analyze(byte[] data) {
		x = ComputationUtils.bitsToByte(data[0], 7, 1) == 1 ? true : false;
		type = ComputationUtils.bitsToByte(data[0], 0, 7);
		length = data[1];
		srlgID = ComputationUtils.byteToInt(data, 2);
		attribute = data[7];
	}

	@Override
	public byte[] getContent() {
		byte[] data = new byte[length];
		int ind = 0;
		data[ind] = (byte) (((x) ? 1 : 0) << 7);
		data[ind++] += type;
		data[ind++] = length;
		data[ind++] = 0;
		data[ind++] = attribute;

		byte[] sid = ComputationUtils.intToByte(srlgID);

		for (int i = 0; i < sid.length; i++)
			data[ind++] = sid[i];
		data[6] = 0;
		data[7] = attribute;

		return data;
	}

	@Override
	public void handle(SessionAccessor sa) {
		// TODO Auto-generated method stub

	}

	public int getSrlgID() {
		return srlgID;
	}

	public void setSrlgID(int srlgID) {
		this.srlgID = srlgID;
	}

	public byte getAttribute() {
		return attribute;
	}

	public void setAttribute(byte attribute) {
		this.attribute = attribute;
	}

}

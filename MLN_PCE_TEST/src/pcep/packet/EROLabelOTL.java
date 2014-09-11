package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * ERO의 SubObject 중 하나로, OTL형식의 Label을 저장하는 서브정보객체.
 * 
 * @author Ancom
 * 
 */
public class EROLabelOTL extends EROSubObj {

	private boolean u;
	private byte ctype;

	private int label;

	public EROLabelOTL(boolean l, byte messageType, byte length) {
		super(l, messageType, length);
		type = 3;
		this.length = 8;

	}

	public EROLabelOTL(boolean l, byte messageType, byte length, byte[] data) {
		super(l, messageType, length, data);
		type = 3;
		this.length = 8;

	}

	public EROLabelOTL(byte[] data) {
		super(data);
		type = 3;
		this.length = 8;
	}

	public EROLabelOTL(boolean l) {
		super(l);
		type = 3;
		this.length = 8;
	}

	@Override
	public byte[] getContent() {
		byte[] data = new byte[length];
		int ind = 0;
		data[ind] = (byte) (((l) ? 1 : 0) << 7);
		data[ind++] += type;
		data[ind++] = length;
		data[ind++] = (byte) (((u) ? 1 : 0) << 7);
		data[ind++] = ctype;

		byte[] labeldata = ComputationUtils.convertIntToByteArray(label);

		for (int i = 0; i < 4; i++)
			data[ind++] = labeldata[i];

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		l = ComputationUtils.bitsToByte(data[0], 7, 1) == 1 ? true : false;
		type = ComputationUtils.bitsToByte(data[0], 0, 7);
		length = data[1];
		u = (ComputationUtils.bitsToByte(data[2], 7, 1) == 1) ? true : false;
		ctype = data[3];

		label = ComputationUtils.byteToInt(data, 4);
	}

	@Override
	public void handle(SessionAccessor sa) {
		// TODO Auto-generated method stub

	}

	public boolean isU() {
		return u;
	}

	public void setU(boolean u) {
		this.u = u;
	}

	public byte getCtype() {
		return ctype;
	}

	public void setCtype(byte ctype) {
		this.ctype = ctype;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

}

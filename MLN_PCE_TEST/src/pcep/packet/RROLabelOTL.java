package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * RRO의 SubObject 중 하나로, OTL형식의 Label을 저장하는 서브정보객체.
 * 
 * @author Ancom
 * 
 */
public class RROLabelOTL extends RROSubObj {

	private boolean u;
	private byte ctype;
	private int label;

	public RROLabelOTL(boolean l, byte messageType, byte length) {
		super(l, messageType, length);
		type = 3;
		this.length = 8;

	}

	public RROLabelOTL(boolean l, byte messageType, byte length, byte[] data) {
		super(l, messageType, length, data);
		type = 3;
		this.length = 8;

	}

	public RROLabelOTL(byte[] data) {
		super(data);
		type = 3;
		this.length = 8;
	}

	public RROLabelOTL(boolean l) {
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
		// subobject adding
		/*
		 * Iterator lli = labelList.iterator(); while (lli.hasNext()) { byte[]
		 * subdata = ((Label) lli.next()).getContent();
		 * 
		 * for (int i = 0; i < subdata.length; i++) data[ind++] = subdata[i];
		 * 
		 * }
		 */

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
		/*
		 * for (int i = 0; i < (length - 4) / 16; i++) { byte[] tmp = new
		 * byte[16];
		 * 
		 * for (int j = 0; j < 16; j++) tmp[j] = data[4 + i * 16 + j];
		 * 
		 * labelList.add(new Label(tmp)); }
		 */
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

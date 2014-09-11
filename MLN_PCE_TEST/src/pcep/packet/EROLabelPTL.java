package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * ERO의 SubObject 중 하나로, PTL형식의 Label을 저장하는 서브정보객체.
 * 
 * @author Ancom
 * 
 */
public class EROLabelPTL extends EROSubObj {
	private byte[] espvid;
	private byte[] espdmac;
	private byte[] espsmac;
	private boolean u;
	private byte ctype;

	public EROLabelPTL(boolean l, byte messageType, byte length) {
		super(l, messageType, length);
		type = 3;
		this.length = 20;
		espvid = new byte[2];
		espdmac = new byte[6];
		espsmac = new byte[6];

	}

	public EROLabelPTL(boolean l, byte messageType, byte length, byte[] data) {

		type = 3;
		this.length = 20;
		this.type = messageType;
		this.data = data;

		analyze(data);

	}

	public EROLabelPTL(byte[] data) {
		this.data = data;
		espvid = new byte[2];
		espdmac = new byte[6];
		espsmac = new byte[6];
		analyze(data);

	}

	public EROLabelPTL(boolean l) {
		super(l);
		type = 3;
		this.length = 20;
	}

	@Override
	public void analyze(byte[] data) {
		l = ComputationUtils.bitsToByte(data[0], 7, 1) == 1 ? true : false;
		type = ComputationUtils.bitsToByte(data[0], 0, 7);
		length = data[1];
		u = (ComputationUtils.bitsToByte(data[2], 7, 1) == 1) ? true : false;
		ctype = data[3];

		espvid[0] = data[4];
		espvid[1] = data[5];

		for (int i = 0; i < 6; i++) {
			//TODO
			espdmac[i] = data[i + 6];
			espsmac[i] = data[i + 12];
		}

	}

	public String toStringVID() {
		StringBuffer tmp = new StringBuffer();
		tmp.append(ComputationUtils.byteToStringNormal(espvid, 0));
		return tmp.toString();
	}

	public String toStringSMAC() {
		StringBuffer tmp = new StringBuffer();
		tmp.append(ComputationUtils.byteToHexString(espsmac, 0));
		return tmp.toString();
	}

	public String toStringDMAC() {
		StringBuffer tmp = new StringBuffer();
		tmp.append(ComputationUtils.byteToHexString(espdmac, 0));
		return tmp.toString();
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

		for (int i = 0; i < 2; i++)
			data[ind++] = espvid[i];
	
		for (int i = 0; i < 6; i++)
			data[ind++] = espdmac[i];
		for (int i = 0; i < 6; i++)
			data[ind++] = espsmac[i];
		
		for (int i = 0; i < 2; i++)
			data[ind++] = 0;
		return data;
	}

	@Override
	public void handle(SessionAccessor sa) {
		// TODO 

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

	public byte[] getEspvid() {
		return espvid;
	}

	public void setEspvid(byte[] espvid) {
		this.espvid = espvid;
	}

	public byte[] getEspdmac() {
		return espdmac;
	}

	public void setEspdmac(byte[] espdmac) {
		this.espdmac = espdmac;
	}

	public byte[] getEspsmac() {
		return espsmac;
	}

	public void setEspsmac(byte[] espsmac) {
		this.espsmac = espsmac;
	}

}

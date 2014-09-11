package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * XRO의 SubObject 중 하나로, IPv4의 Prefix를 저장하는 서브정보객체.
 * 
 * @author Ancom
 * 
 */
public class XROIPv4Prefix extends XROSubObj {

	private byte[] ipv4;
	private byte prefixlength;

	public XROIPv4Prefix(boolean x) {
		super(x);
		type = 1;
		length = 8;
		ipv4 = new byte[4];
	}

	public XROIPv4Prefix(byte[] data) {
		super(data);
	}

	public XROIPv4Prefix(boolean x, byte messageType, byte length) {
		super(x, messageType, length);
		type = 1;
		length = 8;
		ipv4 = new byte[4];
	}

	public XROIPv4Prefix(boolean x, byte messageType, byte length, byte[] data) {
		super(x, messageType, length, data);

		type = 1;
		length = 8;
		ipv4 = new byte[4];
	}

	@Override
	public void analyze(byte[] data) {
		x = ComputationUtils.bitsToByte(data[0], 7, 1) == 1 ? true : false;
		type = ComputationUtils.bitsToByte(data[0], 0, 7);
		length = data[1];
		for (int i = 0; i < 4; i++)
			ipv4[i] = data[i + 2];
		prefixlength = data[6];
	}

	@Override
	public byte[] getContent() {
		byte[] data = new byte[length];
		data[0] = (byte) (((x) ? 1 : 0) << 7);
		data[0] += type;
		data[1] = length;
		for (int i = 0; i < 4; i++)
			data[i + 2] = ipv4[i];
		data[6] = prefixlength;

		return data;
	}

	@Override
	public void handle(SessionAccessor sa) {
		// TODO Auto-generated method stub

	}

	public byte[] getIpv4() {
		return ipv4;
	}

	public void setIpv4(byte[] ipv4) {
		this.ipv4 = ipv4;
	}

	public byte getPrefixlength() {
		return prefixlength;
	}

	public void setPrefixlength(byte prefixlength) {
		this.prefixlength = prefixlength;
	}

}

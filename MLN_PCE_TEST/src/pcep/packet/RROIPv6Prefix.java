package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * RRO의 SubObject 중 하나로, IPv6의 Prefix를 저장하는 서브정보객체.
 * 
 * @author Ancom
 * 
 */
public class RROIPv6Prefix extends RROSubObj {

	private byte[] ipv6;
	private byte prefixlength;

	public RROIPv6Prefix(boolean l, byte messageType, byte length) {
		super(l, messageType, length);
		type = 2;
		length = 20;
		ipv6 = new byte[16];
	}

	public RROIPv6Prefix(boolean l, byte messageType, byte length, byte[] data) {
		super(l, messageType, length, data);
		type = 2;
		length = 20;
		ipv6 = new byte[16];
	}

	public RROIPv6Prefix(byte[] data) {
		super(data);
	}

	@Override
	public byte[] getContent() {
		byte[] data = new byte[length];
		data[0] = (byte) (((l) ? 1 : 0) << 7);
		data[0] += type;
		data[1] = length;
		for (int i = 0; i < 16; i++)
			data[i + 2] = ipv6[i];
		data[18] = prefixlength;

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		l = ComputationUtils.bitsToByte(data[0], 7, 1) == 1 ? true : false;
		type = ComputationUtils.bitsToByte(data[0], 0, 7);
		length = data[1];
		for (int i = 0; i < 16; i++)
			ipv6[i] = data[i + 2];
		prefixlength = data[18];
	}

	@Override
	public void handle(SessionAccessor sa) {
		// TODO Auto-generated method stub

	}

	public byte[] getIpv6() {
		return ipv6;
	}

	public void setIpv6(byte[] ipv6) {
		this.ipv6 = ipv6;
	}

	public byte getPrefixlength() {
		return prefixlength;
	}

	public void setPrefixlength(byte prefixlength) {
		this.prefixlength = prefixlength;
	}

}
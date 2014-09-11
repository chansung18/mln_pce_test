package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * XRO의 SubObject 중 하나로, SLU를 저장하는 서브정보객체.
 * 
 * @author Ancom
 * 
 */
public class XROSLU extends XROSubObj {

	private byte attribute;
	private int routerID;
	private int interfaceID;

	public XROSLU(boolean x, byte messageType, byte length) {
		super(x, messageType, length);
		type = 4;
		length = 12;

	}

	public XROSLU(boolean x, byte messageType, byte length, byte[] data) {
		super(x, messageType, length, data);
		type = 4;
		length = 12;

	}

	public XROSLU(boolean l) {
		super(l);
		type = 4;
		length = 12;
	}

	public XROSLU(byte[] data) {
		super(data);
		type = 4;
		length = 12;
	}

	@Override
	public void analyze(byte[] data) {
		x = ComputationUtils.bitsToByte(data[0], 7, 1) == 1 ? true : false;
		type = ComputationUtils.bitsToByte(data[0], 0, 7);
		length = data[1];
		attribute = data[3];
		routerID = ComputationUtils.byteToInt(data, 4);
		interfaceID = ComputationUtils.byteToInt(data, 8);
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

		byte[] rid = ComputationUtils.intToByte(routerID);
		byte[] iid = ComputationUtils.intToByte(interfaceID);

		for (int i = 0; i < rid.length; i++)
			data[ind++] = rid[i];
		for (int i = 0; i < rid.length; i++)
			data[ind++] = iid[i];

		return data;
	}

	@Override
	public void handle(SessionAccessor sa) {
		// TODO Auto-generated method stub

	}

	public int getRouterID() {
		return routerID;
	}

	public void setRouterID(int routerID) {
		this.routerID = routerID;
	}

	public int getInterfaceID() {
		return interfaceID;
	}

	public void setInterfaceID(int interfaceID) {
		this.interfaceID = interfaceID;
	}

	public byte getAttribute() {
		return attribute;
	}

	public void setAttribute(byte attribute) {
		this.attribute = attribute;
	}

}

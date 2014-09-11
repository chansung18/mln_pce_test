package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;

/**
 * IRO의 SubObject 중 하나로, SLU를 저장하는 서브정보객체.
 * 
 * @author Ancom
 * 
 */
public class IROSLU extends IROSubObj {
//	protected final static Logger logger = Logger.getLogger(IROSLU.class);

	private int routerID;
	private int interfaceID;

	public IROSLU(boolean l, byte messageType, byte length) {
		super(l, messageType, length);
		type = 4;
		length = 12;

	}

	public IROSLU(boolean l, byte messageType, byte length, byte[] data) {
		super(l, messageType, length, data);
		type = 4;
		length = 12;

	}

	public IROSLU(boolean l) {
		super(l);
		type = 4;
		length = 12;
	}

	public IROSLU(byte[] data) {
		super(data);
	}

	@Override
	public byte[] getContent() {
		byte[] data = new byte[length];
		int ind = 0;
		data[ind] = (byte) (((l) ? 1 : 0) << 7);
		data[ind++] += type;
		data[ind++] = length;
		data[ind++] = 0;
		data[ind++] = 0;

		byte[] rid = ComputationUtils.intToByte(routerID);
		byte[] iid = ComputationUtils.intToByte(interfaceID);

		for (int i = 0; i < rid.length; i++)
			data[ind++] = rid[i];
		for (int i = 0; i < rid.length; i++)
			data[ind++] = iid[i];

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		l = ComputationUtils.bitsToByte(data[0], 7, 1) == 1 ? true : false;
		type = ComputationUtils.bitsToByte(data[0], 0, 7);
		length = data[1];
		routerID = ComputationUtils.byteToInt(data, 4);
		interfaceID = ComputationUtils.byteToInt(data, 8);
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

}

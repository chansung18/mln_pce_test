package pcep.packet;

import java.util.Vector;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * Synchronize Vector 객체
 * 
 * @author Ancom
 */
public class SVECObject extends PCEPObject {

//	private final static Logger logger = Logger
//			.getLogger(BandwidthObject.class);

	/**
	 * @uml.property name="reserved"
	 */
	private byte reserved = 0;
	/**
	 * @uml.property name="flags"
	 */
	private int flags;
	/**
	 * @uml.property name="unsignedFlags"
	 */
	private int unsignedFlags;
	/**
	 * @uml.property name="s"
	 */
	private boolean s;
	/**
	 * @uml.property name="n"
	 */
	private boolean n;
	/**
	 * @uml.property name="l"
	 */
	private boolean l;
	/**
	 * @uml.property name="requestIDList"
	 */
	private Vector<Integer> requestIDList = new Vector<Integer>();

	public SVECObject(byte messageType, byte[] data) {
		this.messageType = messageType;
		byte[] headerdata = new byte[4];
		byte[] objdata = new byte[4];
		for (int i = 0; i < 4; i++) {
			headerdata[i] = data[i];
			objdata[i] = data[i + 4];
		}
		header = new CommonObjectHeader(headerdata);
		analyze(objdata);

	}

	public SVECObject(byte messageType, byte reserved, int flags,
			Vector<Integer> rlist) {
		this.messageType = messageType;
		this.reserved = reserved;
		this.flags = flags;
		this.requestIDList = rlist;

		// Header
		this.header = new CommonObjectHeader(PacketChain.SVEC_OC,
				PacketChain.SVEC_OT, false, false, 4 + (rlist.size() * 4)
						+ CommonObjectHeader.getHeaderLength());

		analyzeFlags(flags);

	}

	public SVECObject(byte messageType, byte reserved, int uflags, boolean s,
			boolean n, boolean l, Vector<Integer> rlist) {
		this.messageType = messageType;
		this.reserved = reserved;
		this.unsignedFlags = uflags;
		this.s = s;
		this.n = n;
		this.l = l;
		this.requestIDList = rlist;

		this.flags = (byte) ((uflags << 3) + ((s ? 1 : 0) << 2)
				+ ((n ? 1 : 0) << 1) + ((l ? 1 : 0) << 1));

		// Header
		this.header = new CommonObjectHeader(PacketChain.SVEC_OC,
				PacketChain.SVEC_OT, false, false, 4 + (rlist.size() * 4)
						+ CommonObjectHeader.getHeaderLength());

	}

	public SVECObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];
		byte[] flag = ComputationUtils.intToByte(flags);
		data[0] = reserved;
		// BigE
		for (int i = 0; i < 3; i++)
			data[i + 1] = flag[i + 1];

		// Vector Iteration
		for (int i = 0; i < header.getObjectLength() - 1; i++) {
			byte[] tmp = ComputationUtils.intToByte(requestIDList.get(i));
			for (int j = 0; j < 4; j++)
				data[(i + 1) * 4 + j] = tmp[j];
		}

		return data;

	}

	@Override
	public void analyze(byte[] data) {
		reserved = data[0];
		flags = ComputationUtils.byteToIntExtraction(data, 1, 3);

		// Vector iteration
		int end = header.getObjectLength() / 4;
		for (int i = 1; i < end; i++)
			requestIDList.add(ComputationUtils.byteToInt(data, i * 4));
		analyzeFlags(flags);
	}

	public void analyzeFlags(int flag) {
		byte[] buf = ComputationUtils.intToByte(flag);
		// BigE
		this.s = ComputationUtils.bitsToByte(buf[1], 2, 1) == 1 ? true : false;
		this.n = ComputationUtils.bitsToByte(buf[1], 1, 1) == 1 ? true : false;
		this.l = ComputationUtils.bitsToByte(buf[1], 0, 1) == 1 ? true : false;
		this.unsignedFlags = flag >> 3;
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.SVEC_OC;
	}

	@Override
	public byte getObjectType() {
		return PacketChain.SVEC_OT;
	}

	/**
	 * @return
	 * @uml.property name="reserved"
	 */
	public byte getReserved() {
		return reserved;
	}

	/**
	 * @param reserved
	 * @uml.property name="reserved"
	 */
	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}

	/**
	 * @return
	 * @uml.property name="flags"
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * @param flags
	 * @uml.property name="flags"
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	/**
	 * @return
	 * @uml.property name="unsignedFlags"
	 */
	public int getUnsignedFlags() {
		return unsignedFlags;
	}

	/**
	 * @param unsignedFlags
	 * @uml.property name="unsignedFlags"
	 */
	public void setUnsignedFlags(int unsignedFlags) {
		this.unsignedFlags = unsignedFlags;
	}

	/**
	 * @return
	 * @uml.property name="s"
	 */
	public boolean isS() {
		return s;
	}

	/**
	 * @param s
	 * @uml.property name="s"
	 */
	public void setS(boolean s) {
		this.s = s;
	}

	/**
	 * @return
	 * @uml.property name="n"
	 */
	public boolean isN() {
		return n;
	}

	/**
	 * @param n
	 * @uml.property name="n"
	 */
	public void setN(boolean n) {
		this.n = n;
	}

	/**
	 * @return
	 * @uml.property name="l"
	 */
	public boolean isL() {
		return l;
	}

	/**
	 * @param l
	 * @uml.property name="l"
	 */
	public void setL(boolean l) {
		this.l = l;
	}

	/**
	 * @return
	 * @uml.property name="requestIDList"
	 */
	public Vector<Integer> getRequestIDList() {
		return requestIDList;
	}

	@Override
	public void handle(SessionAccessor sa) {
		if (messageType == PacketChain.PCREQ_PT) {
			// Report LNS
			// Report vector
			// (l,n,s, vector)
//			logger.debug("Report SVec - L : " + l + " N : " + n + " S : " + s);
			sa.inputSVEC(requestIDList, l, n, s);

		}
	}

}
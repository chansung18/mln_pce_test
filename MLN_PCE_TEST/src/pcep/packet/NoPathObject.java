package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * NOPATH 정보 객체
 * 
 * @author Ancom
 */
public class NoPathObject extends PCEPObject {

//	private final static Logger logger = Logger.getLogger(NoPathObject.class);
	private final static int NOPATHTLV_TYPE = 1;
	private final static int NOPATHTLV_LENGTH = 4;

	/**
	 * @uml.property name="ni"
	 */
	private byte ni = -1;
	/**
	 * @uml.property name="c"
	 */
	private boolean c;
	/**
	 * @uml.property name="flags"
	 */
	private int flags = -1;
	/**
	 * @uml.property name="unsignedFlags"
	 */
	private int unsignedFlags = 0;
	/**
	 * @uml.property name="reserved"
	 */
	@Deprecated
	private byte reserved = 0;
	/**
	 * @uml.property name="nOPATHTLV"
	 * @uml.associationEnd
	 */
	private PCEPTLV NOPATHTLV = null;

	public NoPathObject(byte messageType, byte[] data) {
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

	public NoPathObject(byte messageType, byte ni, boolean c, int uflags) {
		this.messageType = messageType;
		this.ni = ni;
		this.c = c;
		this.unsignedFlags = uflags;
		this.flags = ((c ? 1 : 0) << 15) + this.unsignedFlags;
		this.reserved = 0;
		// Header
		this.header = new CommonObjectHeader(PacketChain.NOPATH_OC,
				PacketChain.NOPATH_OT, false, false, 4 + CommonObjectHeader
						.getHeaderLength());
	}

	public NoPathObject(byte messageType, byte ni, int flags) {
		this.messageType = messageType;
		this.ni = ni;

		this.flags = flags;

		this.reserved = 0;
		// Header
		this.header = new CommonObjectHeader(PacketChain.NOPATH_OC,
				PacketChain.NOPATH_OT, false, false, 4 + CommonObjectHeader
						.getHeaderLength());

		analyzeFlags(flags);
	}

	public NoPathObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	@Override
	public void analyze(byte[] data) {
		this.ni = data[0];

		this.c = ComputationUtils.bitsToByte(data[1], 7, 1) == 1 ? true : false;
		this.unsignedFlags = (ComputationUtils.bitsToByte(data[1], 0, 7) << 8)
				+ data[2];
		this.flags = (data[1] << 8) + data[2];

		this.reserved = data[3];

		// NOPATHTLV
		if (data.length > 4)
			setNoPathTLV(ComputationUtils.byteToInt(data, 8));
	}

	public void analyzeFlags(int flag) {
		byte[] buf = ComputationUtils.intToByte(flag);

		// BigE
		this.c = (ComputationUtils.bitsToByte(buf[3], 7, 1) == 1 ? true : false);

		this.unsignedFlags = (flag << 1) >> 1;
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];
		int index = 0;

		// byte[] buf = ComputationUtils.intToByte(((c ? 1 : 0) << 15) + flags);
		byte[] buf = ComputationUtils.intToByte(flags);

		data[index++] = ni;
		// BigE
		data[index++] = buf[2];
		data[index++] = buf[3];
		data[index++] = reserved;

		if (NOPATHTLV != null) {
			byte[] tlv = NOPATHTLV.getContents();
			for (int i = 0; i < tlv.length; i++)
				data[index++] = tlv[i];
		}

		return data;
	}

	@Override
	public byte getObjectType() {
		return PacketChain.NOPATH_OT;
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.NOPATH_OC;
	}

	/**
	 * @return
	 * @uml.property name="ni"
	 */
	public byte getNi() {
		return ni;
	}

	/**
	 * @param ni
	 * @uml.property name="ni"
	 */
	public void setNi(byte ni) {
		this.ni = ni;
	}

	/**
	 * @return
	 * @uml.property name="c"
	 */
	public boolean isC() {
		return c;
	}

	/**
	 * @param c
	 * @uml.property name="c"
	 */
	public void setC(boolean c) {
		this.c = c;
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
	 * @uml.property name="reserved"
	 */
	@Deprecated
	public byte getReserved() {
		return reserved;
	}

	/**
	 * @param reserved
	 * @uml.property name="reserved"
	 */
	@Deprecated
	public void setReserved(byte reserved) {
		this.reserved = reserved;
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

	public void setNoPathTLV(int value) {
		if (NOPATHTLV == null) {
			NOPATHTLV = new PCEPTLV(NOPATHTLV_TYPE, NOPATHTLV_LENGTH);
			NOPATHTLV.addValue(value);
			header.setLength(NOPATHTLV.getTLVLength() + header.getLength());
		}
	}

	public int getReason() {
		if (NOPATHTLV != null)
			return NOPATHTLV.getValue(0);
		else
			return 0;
	}

	@Override
	public void handle(SessionAccessor sa) {
//		logger.debug("NOPATH > NI : " + ni + " C : " + c);
		if (messageType == PacketChain.PCREP_PT) {
			// unsigned flag and reserved check
			if (unsignedFlags != 0 || reserved != 0)
				// invoke error!
				;

			// NOPATHTLV
			if (NOPATHTLV != null)
//				logger.debug("NOPATHTLV : " + NOPATHTLV.getValue(0));

			// Report NoPathObject - session!
			// (NI, C, NOPATHTLV)
//			logger.debug("Report NoPathObject");
			if (NOPATHTLV != null)
				sa.inputNoPath(ni, c, NOPATHTLV.getValue(0));
		}
//		logger.debug("NOPATHTLV Processing Completed");
	}

}

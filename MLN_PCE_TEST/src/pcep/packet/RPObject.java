package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * RP 정보 객체.
 * 
 * @author Ancom
 */
public class RPObject extends PCEPObject {

//	private final static Logger logger = Logger.getLogger(RPObject.class);

	/**
	 * @uml.property name="flags"
	 */
	private int flags = -1;
	/**
	 * @uml.property name="requestIDNumber"
	 */
	private int requestIDNumber = -1;
	/**
	 * @uml.property name="unsignedFlags"
	 */
	private int unsignedFlags = -1;
	/**
	 * @uml.property name="VSPT"
	 */
	private boolean vspt;
	/**
	 * @uml.property name="o"
	 */
	private boolean o;
	/**
	 * @uml.property name="b"
	 */
	private boolean b;
	/**
	 * @uml.property name="r"
	 */
	private boolean r;
	/**
	 * @uml.property name="pri"
	 */
	private byte pri;

	public RPObject(byte messageType, byte[] data) {
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

	public RPObject(byte messageType, byte flags, int requestedIDNumber) {
		this.messageType = messageType;
		this.flags = flags;
		this.requestIDNumber = requestedIDNumber;
		// Header
		this.header = new CommonObjectHeader(PacketChain.RP_OC,
				PacketChain.RP_OT, false, false, 8 + CommonObjectHeader
						.getHeaderLength());
		analyzeFlags(this.flags);
	}

	public RPObject(byte messageType, boolean vspt, boolean o, boolean b, boolean r,
			byte pri, int requestedIDNumber) {
		this.messageType = messageType;
		this.pri = pri;
		this.vspt = vspt;
		this.r = r;
		this.b = b;
		this.o = o;

		this.flags = 0;
		this.flags += pri;
		if (r)
			this.flags += 1 << 3;
		if (b)
			this.flags += 1 << 4;
		if (o)
			this.flags += 1 << 5;
		if (vspt)
			this.flags += 1 << 6;

		this.requestIDNumber = requestedIDNumber;
		// Header
		this.header = new CommonObjectHeader(PacketChain.RP_OC,
				PacketChain.RP_OT, true, false, 8 + CommonObjectHeader
						.getHeaderLength());
		if (messageType == PacketChain.PCERR_PT
				|| messageType == PacketChain.PCNTF_PT)
			this.header.setP(false);

	}

	public RPObject(byte messageType, CommonObjectHeader header, byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	@Override
	public void analyze(byte[] data) {
		flags = ComputationUtils.byteToInt(data, 0);
		requestIDNumber = ComputationUtils.byteToInt(data, 4);

		analyzeFlags(flags);
	}

	public void analyzeFlags(int flag) {
//		logger.debug("called RPObject::analyzeFlags()");
		byte[] buf = ComputationUtils.intToByte(flag);
//		logger.debug("flag buf[] : " + ComputationUtils.prettyBytesToString(buf));

		// BigE
		this.vspt = ComputationUtils.bitsToByte(buf[3], 6, 1) == 1 ? true : false;
		this.o = ComputationUtils.bitsToByte(buf[3], 5, 1) == 1 ? true : false;
		this.b = ComputationUtils.bitsToByte(buf[3], 4, 1) == 1 ? true : false;
		this.r = ComputationUtils.bitsToByte(buf[3], 3, 1) == 1 ? true : false;
		this.pri = ComputationUtils.bitsToByte(buf[3], 0, 3);

		this.unsignedFlags = flag >> 6;
	}

	@Override
	public byte[] getContents() {
		try {
			byte[] data = new byte[header.getObjectLength()];
			byte[] flag = ComputationUtils.intToByte(flags);
			byte[] rid = ComputationUtils.intToByte(requestIDNumber);

			for (int i = 0; i < 4; i++) {
				data[i] = flag[i];
				data[i + 4] = rid[i];
			}
			return data;
		} catch (NegativeArraySizeException e) {
//			logger.warn("Object length is malformed!");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public byte getObjectType() {
		return PacketChain.RP_OT;
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.RP_OC;
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
	 * @uml.property name="requestIDNumber"
	 */
	public int getRequestIDNumber() {
		return requestIDNumber;
	}

	/**
	 * @param requestIDNumber
	 * @uml.property name="requestIDNumber"
	 */
	public void setRequestIDNumber(int requestIDNumber) {
		this.requestIDNumber = requestIDNumber;
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
	 * @uml.property name="vspt"
	 */
	public boolean isVSPT() {
		return vspt;
	}

	/**
	 * @param vspt
	 * @uml.property name="vspt"
	 */
	public void setVSPT(boolean vspt) {
		this.vspt = vspt;
		if (vspt)
			this.flags |= 1 << 6;
		else
			this.flags &= 0xBF;
	}
	
	/**
	 * @return
	 * @uml.property name="o"
	 */
	public boolean isO() {
		return o;
	}

	/**
	 * @param o
	 * @uml.property name="o"
	 */
	public void setO(boolean o) {
		this.o = o;
		if (o)
			this.flags |= 1 << 5;
		else
			this.flags &= (0xFFFF) << 5;
	}

	/**
	 * @return
	 * @uml.property name="b"
	 */
	public boolean isB() {
		return b;
	}

	/**
	 * @param b
	 * @uml.property name="b"
	 */
	public void setB(boolean b) {
		this.b = b;
		if (b)
			this.flags |= 1 << 4;
		else
			this.flags |= (~0) << 4;
	}

	/**
	 * @return
	 * @uml.property name="r"
	 */
	public boolean isR() {
		return r;
	}

	/**
	 * @param r
	 * @uml.property name="r"
	 */
	public void setR(boolean r) {
		this.r = r;
		if (r)
			this.flags |= 1 << 3;
	}

	/**
	 * @return
	 * @uml.property name="pri"
	 */
	public byte getPri() {
		return pri;
	}

	/**
	 * @param pri
	 * @uml.property name="pri"
	 */
	public void setPri(byte pri) {
		this.pri = pri;
	}

	@Override
	public void handle(SessionAccessor sa) {
		// Message Type Checking
//		logger.debug("Message Type Checking on RP");
		if (messageType == PacketChain.PCERR_PT
				|| messageType == PacketChain.PCNTF_PT) {
			if (header.getP() == true) {
				// Error!
//				logger.debug("P flag error.");
				sa.throwError(10, 1);
			}
		} else if (messageType == PacketChain.PCREQ_PT) {
			if (header.getP() == false) {
				// Error!
//				logger.debug("P flag error.");
				sa.throwError(10, 1, requestIDNumber);

			} else {
				// check
//				logger.debug("Flags on RP :" + Integer.toHexString(flags));
				// Priority Process
//				logger.debug("Priority Process on RP :" + pri);
				// Bi-directional?
//				logger.debug("Bi-directional Process on RP :" + b);

				// handling
				// Reoptimization Process
//				logger.debug("Reoptimization Process on RP :" + r);
				// It needs handler-aspect process: check RRO
				// Strict/loose
//				logger.debug("Strict/loose Policy Process on RP :" + o);
				// VSPT procedure?
//				logger.debug("VSPT Procedure on RP :" + vspt);
				// request-ID process
//				logger.debug("request-ID process on RP - rid : "
//						+ requestIDNumber);
				if (requestIDNumber == 0)
					sa.throwError(8, 0);
				else {
					// report to PCE - use session!
//					logger.debug("report RP to PCE");
					sa.inputRP(requestIDNumber, vspt, o, b, r, pri);
				}
			}

		}
	}
}
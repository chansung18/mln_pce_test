package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * EndPoints ������������
 * 
 * @author Ancom
 */
public class EndPointsObject extends PCEPObject {

//	private final static Logger logger = Logger
//			.getLogger(EndPointsObject.class);

	/**
	 * @uml.property name="isIPv6Enable"
	 */
	private boolean isIPv6Enable = false;
	/**
	 * @uml.property name="srcIPv4"
	 */
	private int srcIPv4 = 0;
	/**
	 * @uml.property name="desIPv4"
	 */
	private int desIPv4 = 0;

	// private String srcIPv4str;
	// private String desIPv4str;
	/**
	 * @uml.property name="srcIPv6"
	 */
	private byte[] srcIPv6 = null;
	/**
	 * @uml.property name="desIPv6"
	 */

	private byte[] desIPv6 = null;
	
	public EndPointsObject(byte messageType, byte[] data) {
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

	public EndPointsObject(byte messageType, int srcIPv4, int desIPv4) {
		this.messageType = messageType;
		// IPv4!!!!
		this.srcIPv4 = srcIPv4;
		this.desIPv4 = desIPv4;
		this.isIPv6Enable = false;
		// Header
		this.header = new CommonObjectHeader(PacketChain.ENDPOINTS_OC,
				PacketChain.ENDPOINTS_IPV4_OT, true, false,
				8 + CommonObjectHeader.getHeaderLength());
	}

	public EndPointsObject(byte messageType, byte[] srcIPv6, byte[] desIPv6) {
		this.messageType = messageType;
		// IPv4!!!!
		this.srcIPv6 = srcIPv6;
		this.desIPv6 = desIPv6;
		this.isIPv6Enable = true;
		// Header
		this.header = new CommonObjectHeader(PacketChain.ENDPOINTS_OC,
				PacketChain.ENDPOINTS_IPV6_OT, true, false,
				32 + CommonObjectHeader.getHeaderLength());
	}

	public EndPointsObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	public String ipToString(int ip) {
		String result = new String();
		byte[] ipstr = ComputationUtils.intToByte(ip);

		for (int i = 0; i < ipstr.length; i++) {
			result += ipstr[i];
			if (i < ipstr.length - 1)
				result += ".";
		}

		return result;
	}

	@Override
	public void analyze(byte[] data) {
		if (header.getOt() == 1) {
			this.srcIPv4 = ComputationUtils.byteToInt(data, 0);
			this.desIPv4 = ComputationUtils.byteToInt(data, 4);
			// setSrcIPv4str(ipToString(srcIPv4));
			// setDesIPv4str(ipToString(desIPv4));

		} else if (header.getOt() == 2) {
			// int4
			for (int i = 0; i < 64; i++) {
				srcIPv6[i] = data[i];
				desIPv6[i] = data[i + 64];
			}

		} else {
			// Doh!
		}
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];
		byte[] src, des;
		if (header.getOt() == 1) {
			src = ComputationUtils.intToByte(srcIPv4);
			des = ComputationUtils.intToByte(desIPv4);

			for (int i = 0; i < src.length; i++) {
				data[i] = src[i];
				data[i + ((isIPv6Enable) ? 64 : 4)] = des[i];
			}

		} else if (header.getOt() == 2) {

			src = srcIPv6;
			des = desIPv6;

			for (int i = 0; i < src.length; i++) {
				data[i] = src[i];
				data[i + ((isIPv6Enable) ? 64 : 4)] = des[i];
			}

		} else {
			// Doh!
		}

		return data;
	}

	@Override
	public byte getObjectType() {
		return (isIPv6Enable) ? PacketChain.ENDPOINTS_IPV6_OT
				: PacketChain.ENDPOINTS_IPV4_OT;
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.ENDPOINTS_OC;
	}

	/**
	 * @return
	 * @uml.property name="isIPv6Enable"
	 */
	public boolean isIPv6Enable() {
		return isIPv6Enable;
	}

	/**
	 * @param isIPv6Enable
	 * @uml.property name="isIPv6Enable"
	 */
	public void setIPv6Enable(boolean isIPv6Enable) {
		this.isIPv6Enable = isIPv6Enable;
	}

	/**
	 * @return
	 * @uml.property name="srcIPv4"
	 */
	public int getSrcIPv4() {
		return srcIPv4;
	}

	/**
	 * @param srcIPv4
	 * @uml.property name="srcIPv4"
	 */
	public void setSrcIPv4(int srcIPv4) {
		this.srcIPv4 = srcIPv4;
		// setSrcIPv4str(ipToString(srcIPv4));

	}

	/**
	 * @return
	 * @uml.property name="desIPv4"
	 */
	public int getDesIPv4() {
		return desIPv4;
	}

	/**
	 * @param desIPv4
	 * @uml.property name="desIPv4"
	 */
	public void setDesIPv4(int desIPv4) {
		this.desIPv4 = desIPv4;
		// setDesIPv4str(ipToString(desIPv4));
	}

	/*
	 * public String getSrcIPv4str() { return srcIPv4str; }
	 * 
	 * public void setSrcIPv4str(String srcIPv4str) { this.srcIPv4str =
	 * srcIPv4str; StringTokenizer st = new StringTokenizer(srcIPv4str, ".");
	 * byte[] ipstr = new byte[4]; int i = 0; while (st.hasMoreTokens()) {
	 * ipstr[i] = (byte) Integer.parseInt(st.nextToken()); }
	 * 
	 * setSrcIPv4(ComputationUtils.byteToInt(ipstr));
	 * 
	 * }
	 * 
	 * public String getDesIPv4str() { return desIPv4str; }
	 * 
	 * public void setDesIPv4str(String desIPv4str) { this.desIPv4str =
	 * desIPv4str;
	 * 
	 * StringTokenizer st = new StringTokenizer(desIPv4str, "."); byte[] ipstr =
	 * new byte[4]; int i = 0; while (st.hasMoreTokens()) { ipstr[i] = (byte)
	 * Integer.parseInt(st.nextToken()); }
	 * 
	 * setDesIPv4(ComputationUtils.byteToInt(ipstr));
	 * 
	 * }
	 */

	/**
	 * @return
	 * @uml.property name="srcIPv6"
	 */
	public byte[] getSrcIPv6() {
		return srcIPv6;
	}

	/**
	 * @param srcIPv6
	 * @uml.property name="srcIPv6"
	 */
	public void setSrcIPv6(byte[] srcIPv6) {
		this.srcIPv6 = srcIPv6;
	}

	/**
	 * @return
	 * @uml.property name="desIPv6"
	 */
	public byte[] getDesIPv6() {
		return desIPv6;
	}

	/**
	 * @param desIPv6
	 * @uml.property name="desIPv6"
	 */
	public void setDesIPv6(byte[] desIPv6) {
		this.desIPv6 = desIPv6;
	}

	@Override
	public void handle(SessionAccessor sa) {
		// Check P
//		logger.debug("Header check : P -" + header.getP() + " i - "
//				+ header.getI());
		if (messageType == PacketChain.PCREQ_PT) {
			if (!header.getP()) {
				// Error!
//				logger.debug("Error : P on EndPointObject must be true");
				sa.throwError(10, 1);
			} else {
				if (isIPv6Enable) {
					// Report ipv6
					// (srcIPv6, desIPv6)
//					logger.debug("Report EP : IPv6");
					sa.inputEndPoints(srcIPv6, desIPv6);
				} else {
					// Report ipv4
					// (srcIPv4, desIPv4)
//					logger.debug("Report EP : IPv4");
					sa.inputEndPoints(srcIPv4, desIPv4);
				}
			}
		}
	}

}

package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * LoadBalance를 저장하는 객체
 * 
 * @author Ancom
 */
public class LoadBalancingObject extends PCEPObject {

//	private final static Logger logger = Logger
//			.getLogger(LoadBalancingObject.class);

	/**
	 * @uml.property name="reserved"
	 */
	@Deprecated
	private int reserved = 0;
	/**
	 * @uml.property name="flags"
	 */
	private byte flags = 0;
	/**
	 * @uml.property name="maxLSP"
	 */
	private byte maxLSP;
	/**
	 * @uml.property name="minBandwidth"
	 */
	private int minBandwidth;

	public LoadBalancingObject(byte messageType, byte[] data) {
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

	public LoadBalancingObject(byte messageType, byte flags, byte maxLSP,
			int minBandwidth) {
		this.messageType = messageType;
		this.reserved = 0;
		this.flags = flags;
		this.maxLSP = maxLSP;
		this.minBandwidth = minBandwidth;

		// Header
		this.header = new CommonObjectHeader(PacketChain.LOADBALANCING_OC,
				PacketChain.LOADBALANCING_OT, false, false,
				8 + CommonObjectHeader.getHeaderLength());

	}

	public LoadBalancingObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];

		byte[] res = ComputationUtils.intToByte(reserved);
		byte[] minband = ComputationUtils.intToByte(minBandwidth);

		data[0] = res[0];
		data[1] = res[1];
		data[2] = flags;
		data[3] = maxLSP;
		for (int i = 0; i < 4; i++)
			data[i + 4] = minband[i];

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		reserved = ComputationUtils.byteToShort(data, 0);
		flags = data[2];
		maxLSP = data[3];
		minBandwidth = ComputationUtils.byteToInt(data, 4);
	}

	@Override
	public byte getObjectType() {
		return PacketChain.LOADBALANCING_OT;
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.LOADBALANCING_OC;
	}

	/**
	 * @return
	 * @uml.property name="reserved"
	 */
	@Deprecated
	public int getReserved() {
		return reserved;
	}

	/**
	 * @param reserved
	 * @uml.property name="reserved"
	 */
	@Deprecated
	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

	/**
	 * @return
	 * @uml.property name="flags"
	 */
	public byte getFlags() {
		return flags;
	}

	/**
	 * @param flags
	 * @uml.property name="flags"
	 */
	public void setFlags(byte flags) {
		this.flags = flags;
	}

	/**
	 * @return
	 * @uml.property name="maxLSP"
	 */
	public byte getMaxLSP() {
		return maxLSP;
	}

	/**
	 * @param maxLSP
	 * @uml.property name="maxLSP"
	 */
	public void setMaxLSP(byte maxLSP) {
		this.maxLSP = maxLSP;
	}

	/**
	 * @return
	 * @uml.property name="minBandwidth"
	 */
	public int getMinBandwidth() {
		return minBandwidth;
	}

	/**
	 * @param minBandwidth
	 * @uml.property name="minBandwidth"
	 */
	public void setMinBandwidth(int minBandwidth) {
		this.minBandwidth = minBandwidth;
	}

	@Override
	public void handle(SessionAccessor sa) {
		// Report - session
		// (Max-LSP, Min-Bandwidth)
//		logger.debug("Reporting LBO - max_lsp :" + maxLSP + " minband : "
//				+ minBandwidth);

	}

}
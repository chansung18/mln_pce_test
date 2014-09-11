package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * Bandwidth 정보객체
 * 
 * @author Ancom
 */
public class BandwidthObject extends PCEPObject {

//	private final static Logger logger = Logger
//			.getLogger(BandwidthObject.class);

	/**
	 * @uml.property name="bandwidth"
	 */
	private int bandwidth;
	/**
	 * @uml.property name="requested"
	 */
	private boolean requested;

	public BandwidthObject(byte messageType, byte[] data) {
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

	public BandwidthObject(byte messageType, int bandwidth, boolean requested) {
		this.messageType = messageType;
		this.bandwidth = bandwidth;
		this.requested = requested;

		// Header
		this.header = new CommonObjectHeader(PacketChain.BANDWIDTH_OC,
				(requested) ? PacketChain.BANDWIDTH_REQUESTED_OT
						: PacketChain.BANDWIDTH_EXISTING_OT, false, false,
				4 + CommonObjectHeader.getHeaderLength());

	}

	public BandwidthObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];
		data = ComputationUtils.intToByte(bandwidth);
		return data;
	}

	@Override
	public void analyze(byte[] data) {
		this.bandwidth = ComputationUtils.byteToInt(data);
		if (header.getOt() == PacketChain.BANDWIDTH_REQUESTED_OT)
			requested = true;
		else
			requested = false;
	}

	@Override
	public byte getObjectType() {
		// return requested ? PacketChain.BANDWIDTH_REQUESTED_OT
		// : PacketChain.BANDWIDTH_EXISTING_OT;
		return header.getOt();
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.BANDWIDTH_OC;
	}

	/**
	 * @return
	 * @uml.property name="bandwidth"
	 */
	public int getBandwidth() {
		return bandwidth;
	}

	/**
	 * @param bandwidth
	 * @uml.property name="bandwidth"
	 */
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

	/**
	 * @return
	 * @uml.property name="requested"
	 */
	public boolean isRequested() {
		return requested;
	}

	/**
	 * @param requested
	 * @uml.property name="requested"
	 */
	public void setRequested(boolean requested) {
		this.requested = requested;
		if (requested)
			header.setOt(PacketChain.BANDWIDTH_REQUESTED_OT);
		else
			header.setOt(PacketChain.BANDWIDTH_EXISTING_OT);
	}

	@Override
	public void handle(SessionAccessor sa) {
		// Report Bandwidth
//		logger.debug("report bandwidth : " + bandwidth);
		if (messageType == PacketChain.PCREQ_PT
				|| messageType == PacketChain.PCREP_PT)
			sa.inputBandwidth(bandwidth, requested);

	}

}

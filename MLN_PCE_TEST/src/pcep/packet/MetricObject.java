package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * Metric 정보 객체.
 * 
 * @author Ancom
 */
public class MetricObject extends PCEPObject {

//	private final static Logger logger = Logger
//			.getLogger(BandwidthObject.class);

	/**
	 * @uml.property name="reserved"
	 */
	@Deprecated
	private int reserved = 0;
	/**
	 * @uml.property name="flags"
	 */
	private byte flags;
	/**
	 * @uml.property name="unsignedFlags"
	 */
	private byte unsignedFlags = 0;
	/**
	 * @uml.property name="c"
	 */
	private boolean c;
	/**
	 * @uml.property name="b"
	 */
	private boolean b;
	/**
	 * @uml.property name="type"
	 */
	private byte type;
	/**
	 * @uml.property name="metricValue"
	 */
	private int metricValue;

	public MetricObject(byte messageType, byte[] data) {
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

	public MetricObject(byte messageType, byte flags, byte type, int metricValue) {
		this.messageType = messageType;
		this.reserved = 0;
		this.flags = flags;
		this.type = type;
		this.metricValue = metricValue;
		// Header
		this.header = new CommonObjectHeader(PacketChain.METRIC_OC,
				PacketChain.METRIC_OT, false, false, 8 + CommonObjectHeader
						.getHeaderLength());
		analyzeFlags(flags);
	}

	public MetricObject(byte messageType, byte uflags, boolean c, boolean b,
			byte type, int metricValue) {
		this.messageType = messageType;
		this.reserved = 0;
		this.unsignedFlags = uflags;
		this.c = c;
		this.b = b;
		this.type = type;
		this.metricValue = metricValue;

		this.flags = (byte) ((uflags << 2) + ((c ? 1 : 0) << 1) + ((b ? 1 : 0) << 0));

		// Header
		this.header = new CommonObjectHeader(PacketChain.METRIC_OC,
				PacketChain.METRIC_OT, false, false, 8 + CommonObjectHeader
						.getHeaderLength());

	}

	public MetricObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];

		byte[] res = ComputationUtils.intToByte(reserved);
		byte[] mv = ComputationUtils.intToByte(metricValue);

		data[0] = res[0];
		data[1] = res[1];
		data[2] = flags;
		data[3] = type;
		for (int i = 0; i < 4; i++)
			data[i + 4] = mv[i];

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		reserved = ComputationUtils.byteToShort(data, 0);
		flags = data[2];
		type = data[3];
		metricValue = ComputationUtils.byteToInt(data, 4);

		analyzeFlags(flags);
	}

	public void analyzeFlags(byte flag) {

		this.c = ComputationUtils.bitsToByte(flag, 1, 1) == 1 ? true : false;
		this.b = ComputationUtils.bitsToByte(flag, 0, 1) == 1 ? true : false;
		this.unsignedFlags = ComputationUtils.bitsToByte(flag, 2, 6);
	}

	@Override
	public byte getObjectType() {
		return PacketChain.METRIC_OT;
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.METRIC_OC;
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
	 * @uml.property name="unsignedFlags"
	 */
	public byte getUnsignedFlags() {
		return unsignedFlags;
	}

	/**
	 * @param unsignedFlags
	 * @uml.property name="unsignedFlags"
	 */
	public void setUnsignedFlags(byte unsignedFlags) {
		this.unsignedFlags = unsignedFlags;
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
	}

	/**
	 * @return
	 * @uml.property name="type"
	 */
	public byte getType() {
		return type;
	}

	/**
	 * @param type
	 * @uml.property name="type"
	 */
	public void setType(byte type) {
		this.type = type;
	}

	/**
	 * @return
	 * @uml.property name="metricValue"
	 */
	public int getMetricValue() {
		return metricValue;
	}

	/**
	 * @param metricValue
	 * @uml.property name="metricValue"
	 */
	public void setMetricValue(int metricValue) {
		this.metricValue = metricValue;
	}

	@Override
	public void handle(SessionAccessor sa) {
		// Report - session
		// (b, c, t, value)
//		logger.debug("Report Metric : b - " + b + " c -" + c + "type - " + type
//				+ " metricvalue -" + metricValue);
		if (messageType == PacketChain.PCREQ_PT
				|| messageType == PacketChain.PCREP_PT) {
			//
			if (messageType == PacketChain.PCREQ_PT && c && b)
				// Error! b must false when c is true
				;
			sa.inputMetric(b, c, type, metricValue);
		}

	}
}
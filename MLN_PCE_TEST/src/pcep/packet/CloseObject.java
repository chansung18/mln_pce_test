package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * Close 정보객체
 * 
 * @author Ancom
 */
public class CloseObject extends PCEPObject {

//	private final static Logger logger = Logger.getLogger(CloseObject.class);

	/**
	 * @uml.property name="reserved"
	 */
	@Deprecated
	private int reserved = 0;
	/**
	 * @uml.property name="flags"
	 */
	private byte flags = -1;
	/**
	 * @uml.property name="reason"
	 */
	private byte reason = 0;

	public CloseObject(byte messageType, byte[] data) {
		// super(messageType, data);
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

	public CloseObject(byte messageType, byte flags, byte reason) {
		this.messageType = messageType;
		this.reserved = 0;
		this.flags = flags;
		this.reason = reason;

		// Header
		this.header = new CommonObjectHeader(PacketChain.CLOSE_OC,
				PacketChain.CLOSE_OT, false, false, 4 + CommonObjectHeader
						.getHeaderLength());

	}

	public CloseObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	/**
	 * @return
	 * @uml.property name="reserved"
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private int getReserved() {
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
	 * @uml.property name="reason"
	 */
	public byte getReason() {
		return reason;
	}

	/**
	 * @param reason
	 * @uml.property name="reason"
	 */
	public void setReason(byte reason) {
		this.reason = reason;
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[4];

		// BigE
		data[0] = ComputationUtils.intToByte(reserved)[2];
		data[1] = ComputationUtils.intToByte(reserved)[3];
		data[2] = flags;
		data[3] = reason;

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		this.reserved = ComputationUtils.byteToShort(data, 0);
		this.flags = data[2];
		this.reason = data[3];
	}

	@Override
	public byte getObjectType() {
		return PacketChain.CLOSE_OT;
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.CLOSE_OC;
	}

	@Override
	public void handle(SessionAccessor sa) {
		// Check reason..
//		logger.debug("Reason : " + this.getReason());

		// Report - session!
//		logger.debug("Report Close");
		sa.inputClose(flags, reason);

		// TODO:cancelling pending requests...
		// logger.debug("Cancelling Pending Requests....");

//		logger.debug("Invoking TCP connection closing....");
		try {
			sa.disconnectRemote();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

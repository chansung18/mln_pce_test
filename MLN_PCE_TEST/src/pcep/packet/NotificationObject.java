package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * NOTIFICATION 정보객체.
 * 
 * @author Ancom
 */
public class NotificationObject extends PCEPObject {

//	private final static Logger logger = Logger
//			.getLogger(NotificationObject.class);
	private final static int OVERLOADED_DURATIONTLV_TYPE = 2;
	private final static int OVERLOADED_DURATIONTLV_LENGTH = 4;

	/**
	 * @uml.property name="reserved"
	 */
	@Deprecated
	private byte reserved = 0;
	/**
	 * @uml.property name="flags"
	 */
	private byte flags = 0;
	/**
	 * @uml.property name="notificationType"
	 */
	private byte notificationType;
	/**
	 * @uml.property name="notificationValue"
	 */
	private byte notificationValue;

	/**
	 * @uml.property name="oVERLOADED_DURATIONTLV"
	 * @uml.associationEnd
	 */
	private PCEPTLV OVERLOADED_DURATIONTLV = null;

	public NotificationObject(byte messageType, byte[] data) {
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

	public NotificationObject(byte messageType, byte reserved, byte flags,
			byte nt, byte nv) {
		this.reserved = reserved;
		this.flags = flags;
		this.notificationType = nt;
		this.notificationValue = nv;

		// Header
		this.header = new CommonObjectHeader(PacketChain.NOTIFICATION_OC,
				PacketChain.NOTIFICATION_OT, false, false,
				4 + CommonObjectHeader.getHeaderLength());

	}

	public NotificationObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];
		int index = 0;
		data[index++] = reserved;
		data[index++] = flags;
		data[index++] = notificationType;
		data[index++] = notificationValue;

		if (OVERLOADED_DURATIONTLV != null) {
			byte[] tlv = OVERLOADED_DURATIONTLV.getContents();
			for (int i = 0; i < tlv.length; i++)
				data[index++] = tlv[i];
		}

		return data;
	}

	@Override
	public void analyze(byte[] data) {

		reserved = data[0];
		flags = data[1];
		notificationType = data[2];
		notificationValue = data[3];

		// NOPATHTLV
		if (data.length > 4)
			setOverloadedDurationTLV(ComputationUtils.byteToInt(data, 4));
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.NOTIFICATION_OC;
	}

	@Override
	public byte getObjectType() {
		return PacketChain.NOTIFICATION_OT;
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
	@Deprecated
	public void setReserved(byte reserved) {
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
	 * @uml.property name="notificationType"
	 */
	public byte getNotificationType() {
		return notificationType;
	}

	/**
	 * @param notificationType
	 * @uml.property name="notificationType"
	 */
	public void setNotificationType(byte notificationType) {
		this.notificationType = notificationType;
	}

	/**
	 * @return
	 * @uml.property name="notificationValue"
	 */
	public byte getNotificationValue() {
		return notificationValue;
	}

	/**
	 * @param notificationValue
	 * @uml.property name="notificationValue"
	 */
	public void setNotificationValue(byte notificationValue) {
		this.notificationValue = notificationValue;
	}

	public void setOverloadedDurationTLV(int value) {
		if (OVERLOADED_DURATIONTLV == null) {
			OVERLOADED_DURATIONTLV = new PCEPTLV(OVERLOADED_DURATIONTLV_TYPE,
					OVERLOADED_DURATIONTLV_LENGTH);
			OVERLOADED_DURATIONTLV.addValue(value);
			header.setLength(OVERLOADED_DURATIONTLV.getTLVLength()
					+ header.getLength());
		}
	}

	@Override
	public void handle(SessionAccessor sa) {
		// type checking
		if (messageType == PacketChain.PCNTF_PT) {
//			logger.debug("Report Ntf - NT :" + notificationType + " nv : "
//					+ notificationValue);
			// NOPATHTLV
			if (OVERLOADED_DURATIONTLV != null) {
//				logger.debug("OVERLOADED_DURATIONTLV : "
//						+ OVERLOADED_DURATIONTLV.getValue(0));
				sa.inputNotification(notificationType, notificationValue,
						OVERLOADED_DURATIONTLV.getValue(0));
			} else {
				sa.inputNotification(notificationType, notificationValue, -1);
			}

			// Report - session
			// (NT, NV, OverloadDurationTLV)

		} else {
			// ,......
		}

	}

}
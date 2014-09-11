package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * PCEP ERROR 정보객체
 * 
 * @author Ancom
 */
public class PCEPErrorObject extends PCEPObject {

//	private final static Logger logger = Logger
//			.getLogger(PCEPErrorObject.class);
	private final static int REQMISSINGTLV_TYPE = 3;
	private final static int REQMISSINGTLV_LENGTH = 4;

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
	 * @uml.property name="errorType"
	 */
	private byte errorType;
	/**
	 * @uml.property name="errorValue"
	 */
	private byte errorValue;

	/**
	 * @uml.property name="rEQMISSINGTLV"
	 * @uml.associationEnd
	 */
	private PCEPTLV REQMISSINGTLV = null;

	public PCEPErrorObject(byte messageType, byte[] data) {
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

	public PCEPErrorObject(byte messageType, byte flags, byte errorType,
			byte errorValue) {
		this.messageType = messageType;
		this.reserved = 0;
		this.flags = flags;
		this.errorType = errorType;
		this.errorValue = errorValue;

		// Header
		this.header = new CommonObjectHeader(PacketChain.PCEPERROR_OC,
				PacketChain.PCEPERROR_OT, false, false, 4 + CommonObjectHeader
						.getHeaderLength());

	}

	public PCEPErrorObject(byte messageType, CommonObjectHeader header,
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
		data[index++] = errorType;
		data[index++] = errorValue;

		if (REQMISSINGTLV != null) {
			byte[] tlv = REQMISSINGTLV.getContents();
			for (int i = 0; i < tlv.length; i++)
				data[index++] = tlv[i];
		}

		return data;
	}

	@Override
	public void analyze(byte[] data) {

		reserved = data[0];
		flags = data[1];
		errorType = data[2];
		errorValue = data[3];

		// ERRTLV
		if (data.length > 4)
			setReqMissingTLV(ComputationUtils.byteToInt(data, 4));
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.PCEPERROR_OC;
	}

	@Override
	public byte getObjectType() {
		return PacketChain.PCEPERROR_OT;
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
	 * @uml.property name="errorType"
	 */
	public byte getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType
	 * @uml.property name="errorType"
	 */
	public void setErrorType(byte errorType) {
		this.errorType = errorType;
	}

	/**
	 * @return
	 * @uml.property name="errorValue"
	 */
	public byte getErrorValue() {
		return errorValue;
	}

	/**
	 * @param errorValue
	 * @uml.property name="errorValue"
	 */
	public void setErrorValue(byte errorValue) {
		this.errorValue = errorValue;
	}

	public void setReqMissingTLV(int value) {
		if (REQMISSINGTLV == null) {
			REQMISSINGTLV = new PCEPTLV(REQMISSINGTLV_TYPE,
					REQMISSINGTLV_LENGTH);
			REQMISSINGTLV.addValue(value);
			header.setLength(REQMISSINGTLV.getTLVLength() + header.getLength());
		}
	}

	@Override
	public void handle(SessionAccessor sa) {
		if (messageType == PacketChain.PCERR_PT) {

//			logger.debug("Report PCEPerr - eT :" + errorType + " ev : "
//					+ errorValue);
			// REQMISSINGTLV
			if (REQMISSINGTLV != null) {
//				logger.debug("REQMISSINGTLV - RequestID : "
//						+ REQMISSINGTLV.getValue(0));

				sa.inputPCEPError(errorType, errorValue, REQMISSINGTLV
						.getValue(0));
			} else {
				sa.inputPCEPError(errorType, errorValue, -1);
			}
			// Reprot - session
			// (ET, EV, TLV.....)

		}

	}

}
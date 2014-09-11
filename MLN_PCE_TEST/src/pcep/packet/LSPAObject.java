package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * LSPA 정보 객체
 * 
 * @author Ancom
 * 
 */
public class LSPAObject extends PCEPObject {

//	private final static Logger logger = Logger.getLogger(LSPAObject.class);

	private byte setupPrio;
	private byte holdingPrio;
	private byte flags;
	@SuppressWarnings("unused")
	private byte unsignedFlags = 0;
	private boolean l;
	@Deprecated
	private byte reserved = 0;

	public LSPAObject(byte messageType, byte[] data) {
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

	public LSPAObject(byte messageType, byte setupPrio, byte holdingPrio,
			byte flags, byte reserved) {
		this.messageType = messageType;
		this.setupPrio = setupPrio;
		this.holdingPrio = holdingPrio;
		this.flags = flags;
		this.reserved = reserved;

		// Header
		this.header = new CommonObjectHeader(PacketChain.LSPA_OC,
				PacketChain.LSPA_OT, false, false, 16 + CommonObjectHeader
						.getHeaderLength());

		analyzeFlags(flags);
	}

	public LSPAObject(byte messageType, byte setupPrio, byte holdingPrio,
			byte uflags, boolean l, byte reserved) {
		this.messageType = messageType;
		this.setupPrio = setupPrio;
		this.holdingPrio = holdingPrio;
		this.unsignedFlags = uflags;
		this.l = l;
		this.reserved = reserved;

		this.flags = (byte) ((uflags << 1) + ((l ? 1 : 0) << 1));

		// Header
		this.header = new CommonObjectHeader(PacketChain.LSPA_OC,
				PacketChain.LSPA_OT, false, false, 16 + CommonObjectHeader
						.getHeaderLength());

	}

	public LSPAObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	public byte getSetupPrio() {
		return setupPrio;
	}

	public void setSetupPrio(byte setupPrio) {
		this.setupPrio = setupPrio;
	}

	public byte getHoldingPrio() {
		return holdingPrio;
	}

	public void setHoldingPrio(byte holdingPrio) {
		this.holdingPrio = holdingPrio;
	}

	public byte getFlags() {
		return flags;
	}

	public void setFlags(byte flags) {
		this.flags = flags;
	}

	public boolean isL() {
		return l;
	}

	public void setL(boolean l) {
		this.l = l;
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];

		// TODO: 1,2,3????

		// 4th
		data[12] = setupPrio;
		data[13] = holdingPrio;
		data[14] = flags;
		data[15] = reserved;

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		// TODO: 1,2,3????

		// 4th
		setupPrio = data[12];
		holdingPrio = data[13];
		flags = data[14];
		reserved = data[15];
		analyzeFlags(flags);
	}

	public void analyzeFlags(byte flag) {

		this.l = ComputationUtils.bitsToByte(flag, 0, 1) == 1 ? true : false;
		this.unsignedFlags = (byte) (flag >> 1);
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.LSPA_OC;
	}

	@Override
	public byte getObjectType() {
		return PacketChain.LSPA_OT;
	}

	@Override
	public void handle(SessionAccessor sa) {
//		logger.debug("LSPA - handling needs to be implemented");
	}

}
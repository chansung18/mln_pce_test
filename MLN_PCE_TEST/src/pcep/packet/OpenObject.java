package pcep.packet;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * OPEN 정보객체
 * 
 * @author Ancom
 */
public class OpenObject extends PCEPObject {

	// private final static Logger logger = Logger.getLogger(OpenObject.class);

	/**
	 * @uml.property name="ver"
	 */
	private int ver;
	/**
	 * @uml.property name="flags"
	 */
	private byte flags;
	/**
	 * @uml.property name="keepalive"
	 */
	private byte keepalive; // seconds. reccomend: 30s
	/**
	 * @uml.property name="deadtimer"
	 */
	private byte deadtimer; // seconds. recommend: keepalive x4
	/**
	 * @uml.property name="sid"
	 */
	private byte sid;

	// TODO: Repository for Optional TLV

	public OpenObject(byte messageType, byte[] data) {
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

	public OpenObject(byte messageType, int ver, byte flags, byte keepalive,
			byte deadtimer, byte sid) {
		this.messageType = messageType;
		this.ver = ver;
		this.flags = flags;
		this.keepalive = keepalive;
		this.deadtimer = deadtimer;
		this.sid = sid;

		// Header
		this.header = new CommonObjectHeader(PacketChain.OPEN_OC,
				PacketChain.OPEN_OT, false, false, 4 + CommonObjectHeader
						.getHeaderLength());

	}

	public OpenObject(byte messageType, CommonObjectHeader header,
			byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	/**
	 * @return
	 * @uml.property name="ver"
	 */
	public int getVer() {
		return ver;
	}

	/**
	 * @return
	 * @uml.property name="flags"
	 */
	public byte getFlags() {
		return flags;
	}

	/**
	 * @return
	 * @uml.property name="keepalive"
	 */
	public byte getKeepalive() {
		return keepalive;
	}

	/**
	 * @return
	 * @uml.property name="deadtimer"
	 */
	public byte getDeadtimer() {
		return deadtimer;
	}

	/**
	 * @return
	 * @uml.property name="sid"
	 */
	public byte getSid() {
		return sid;
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[4];

		data[0] = (byte) ((byte) (ver << 5) + flags);
		data[1] = keepalive;
		data[2] = deadtimer;
		data[3] = sid;

		return data;
	}

	@Override
	public byte getObjectType() {
		return PacketChain.OPEN_OT;
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.OPEN_OC;
	}

	@Override
	public void analyze(byte[] data) {
		ver = ComputationUtils.bitsToByte(data[0], 5, 3);
		flags = ComputationUtils.bitsToByte(data[0], 0, 5);
		keepalive = (byte) ComputationUtils.unsignedByteToInt(data[1]);
		deadtimer = (byte) ComputationUtils.unsignedByteToInt(data[2]);
		sid = (byte) ComputationUtils.unsignedByteToInt(data[3]);
	}

	@Override
	public byte[] getObjHeaderContents() {
		return header.getContents();
	}

	@Override
	public void handle(SessionAccessor sa) {

		if (messageType == PacketChain.PCERR_PT) {

		} else if (messageType == PacketChain.OPEN_PT) {

			sa.inputOpen(keepalive, deadtimer, sid);
		} else {
			// May be error.....
		}

	}

}

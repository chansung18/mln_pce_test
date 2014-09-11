package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * XRO의 SubObject.
 * 
 * @author Ancom
 * 
 */
public abstract class XROSubObj {

	protected boolean x;
	protected byte type;
	protected byte length;
	protected byte[] data;

	public XROSubObj(boolean x) {
		this.x = x;
		data = null;
	}

	public XROSubObj(byte[] data) {
		this.data = data;
		analyze(data);
	}

	public XROSubObj(boolean x, byte[] data) {
		this.x = x;
		this.data = data;
		analyze(data);
	}

	public XROSubObj(boolean x, byte messageType, byte length) {

		this.x = x;
		this.type = messageType;
		this.length = length;
		data = null;
	}

	public XROSubObj(boolean x, byte messageType, byte length, byte[] data) {

		this.x = x;
		this.type = messageType;
		this.length = length;

		this.data = data;

		analyze(data);
	}

	public abstract void analyze(byte[] data);

	public abstract void handle(SessionAccessor sa);

	public boolean isX() {
		return x;
	}

	public byte getType() {
		return type;
	}

	public byte getLength() {
		return length;
	}

	public byte[] getData() {
		return data;
	}

	public abstract byte[] getContent();

}

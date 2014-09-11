package pcep.packet;

import pcep.PCEPSession.SessionAccessor;

/**
 * IRO의 SubObject.
 * 
 * @author Ancom
 * 
 */
public abstract class IROSubObj {

	protected boolean l;
	protected byte type;
	protected byte length;
	protected byte[] data;

	public IROSubObj(boolean l) {
		this.l = l;
		data = null;
	}

	public IROSubObj(byte[] data) {
		this.data = data;
		analyze(data);
	}

	public IROSubObj(boolean l, byte[] data) {
		this.l = l;
		this.data = data;
		analyze(data);
	}

	public IROSubObj(boolean l, byte messageType, byte length) {

		this.l = l;
		this.type = messageType;
		this.length = length;
		data = null;
	}

	public IROSubObj(boolean l, byte messageType, byte length, byte[] data) {

		this.l = l;
		this.type = messageType;
		this.length = length;
		this.data = data;

		analyze(data);
	}

	public abstract void analyze(byte[] data);

	public abstract void handle(SessionAccessor sa);

	public boolean isL() {
		return l;
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

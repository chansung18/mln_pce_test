package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;

/**
 * RRO의 SubObject.
 * 
 * @author Ancom
 * 
 */
public abstract class RROSubObj {

//	protected final static Logger logger = Logger.getLogger(RROSubObj.class);
	protected boolean l;
	protected byte type;
	protected byte length;
	protected byte[] data;

	public RROSubObj(boolean l) {
		this.l = l;
		data = null;
	}

	public RROSubObj(byte[] data) {
		this.data = data;
		analyze(data);
	}

	public RROSubObj(boolean l, byte[] data) {
		this.l = l;
		this.data = data;
		analyze(data);
	}

	public RROSubObj(boolean l, byte messageType, byte length) {

		this.l = l;
		this.type = messageType;
		this.length = length;
		data = null;
	}

	public RROSubObj(boolean l, byte messageType, byte length, byte[] data) {

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

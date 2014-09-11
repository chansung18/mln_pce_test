package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;

/**
 * ERO의 SubObject.
 * 
 * @author Ancom
 * 
 */
public abstract class EROSubObj {

//	protected final static Logger logger = Logger.getLogger(EROSubObj.class);
	protected boolean l;
	protected byte type;
	protected byte length;
	protected byte[] data;

	public EROSubObj() {
	}

	public EROSubObj(boolean l) {
		this.l = l;
		data = null;
	}

	public EROSubObj(byte[] data) {
		this.data = data;
		analyze(data);
	}

	public EROSubObj(boolean l, byte[] data) {
		this.l = l;
		this.data = data;
		analyze(data);
	}

	public EROSubObj(boolean l, byte messageType, byte length) {

		this.l = l;
		this.type = messageType;
		this.length = length;
		data = null;
	}

	public EROSubObj(boolean l, byte messageType, byte length, byte[] data) {

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

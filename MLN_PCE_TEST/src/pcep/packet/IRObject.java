package pcep.packet;

import java.util.Iterator;
import java.util.Vector;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * IRO 정보객체. IRO 정보객체는 여러 종류의 서브정보객체를 담을 수 있다.
 * 
 * @author Ancom
 * 
 */
public class IRObject extends PCEPObject {

//	private final static Logger logger = Logger.getLogger(IRObject.class);

	Vector<IROSubObj> subObjects = new Vector<IROSubObj>();

	public IRObject(byte messageType, byte[] data) {
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

	public IRObject(byte messageType) {
		this.messageType = messageType;
		// Header
		this.header = new CommonObjectHeader(PacketChain.IRO_OC,
				PacketChain.IRO_OT, false, false, 0 + CommonObjectHeader
						.getHeaderLength());

	}

	public IRObject(byte messageType, CommonObjectHeader header, byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];

		int ind = 0;

		// subobject adding
		Iterator<IROSubObj> soi = subObjects.iterator();
		while (soi.hasNext()) {

			byte[] subdata = (soi.next()).getContent();

			for (int i = 0; i < subdata.length; i++)
				data[ind++] = subdata[i];

		}

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		int datalength = 0;
		for (int i = 0; i < data.length; i += datalength) {
			datalength = data[i + 1];
			byte type = ComputationUtils.bitsToByte(data[i], 0, 7);
			byte[] tmp = new byte[datalength];
			for (int j = 0; j < datalength; j++) {
				tmp[j] = data[i + j];
			}

			// new subobject
			IROSubObj newsub;
			switch (type) {

			case 4:
				newsub = new IROSLU(tmp);
				break;

			default:
				newsub = null;
				break;
			}

			addSubObject(newsub);

		}

	}

	public void addSubObject(IROSubObj newsub) {
		int length = header.getLength() + newsub.length;
		header.setLength(length);
		subObjects.add(newsub);
	}

	public void delSubObject(IROSubObj delsub) {
		int length = header.getLength() - delsub.length;
		header.setLength(length);
		subObjects.remove(delsub);
	}

	public IROSubObj popEROSubObj() {
		return subObjects.remove(0);
	}

	public Iterator<IROSubObj> getSubObjectIterator() {
		return subObjects.iterator();
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.IRO_OC;
	}

	@Override
	public byte getObjectType() {
		return PacketChain.IRO_OT;
	}

	@Override
	public void handle(SessionAccessor sa) {
//		logger.debug("IRO - Subobjects");
		for (int i = 0; i < subObjects.size(); i++) {
			subObjects.get(i).handle(sa);
		}

	}

}
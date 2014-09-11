package pcep.packet;

import java.util.Iterator;
import java.util.Vector;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * RRO 정보객체. RRO 정보객체는 여러 종류의 서브정보객체를 담을 수 있다.
 * 
 * @author Ancom
 * 
 */
public class RRObject extends PCEPObject {

//	private final static Logger logger = Logger.getLogger(RRObject.class);
	protected Vector<RROSubObj> subObjects = new Vector<RROSubObj>();

	public RRObject(byte messageType, byte[] data) {
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

	public RRObject(byte messageType) {
		this.messageType = messageType;
		// Header
		this.header = new CommonObjectHeader(PacketChain.RRO_OC,
				PacketChain.RRO_OT, false, false, 0 + CommonObjectHeader
						.getHeaderLength());

	}

	public RRObject(byte messageType, CommonObjectHeader header, byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];
		int ind = 0;
		// subobject adding
		Iterator<RROSubObj> soi = subObjects.iterator();
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
			RROSubObj newsub = null;
			switch (type) {
			case 1:
				newsub = new RROIPv4Prefix(tmp);
				break;
			case 2:
				newsub = new RROIPv6Prefix(tmp);
				break;
			case 3:
				if (datalength < 20)
					newsub = new RROLabelOTL(tmp);
				else
					newsub = new RROLabelPTL(tmp);
				break;
			case 4:
				newsub = new RROSLU(tmp);
				break;
			case 32:
				newsub = new RROASNumber(tmp);
				break;
			default:
				newsub = null;
				break;
			}

			addSubObject(newsub);

		}
	}

	public void addSubObject(RROSubObj newsub) {
		int length = header.getLength() + newsub.length;
		header.setLength(length);
		subObjects.add(newsub);
	}

	public void delSubObject(RROSubObj delsub) {
		int length = header.getLength() - delsub.length;
		header.setLength(length);
		subObjects.remove(delsub);
	}

	public RROSubObj popEROSubObj() {
		return subObjects.remove(0);
	}

	public Iterator<RROSubObj> getSubObjectIterator() {
		return subObjects.iterator();
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.RRO_OC;
	}

	@Override
	public byte getObjectType() {
		return PacketChain.RRO_OT;
	}

	@Override
	public void handle(SessionAccessor sa) {
//		logger.debug("RRO - Subobjects");
		for (int i = 0; i < subObjects.size(); i++) {
			subObjects.get(i).handle(sa);
		}
	}

}
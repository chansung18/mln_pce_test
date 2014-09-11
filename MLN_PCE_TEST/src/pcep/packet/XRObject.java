package pcep.packet;

import java.util.Iterator;
import java.util.Vector;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * XRO 정보객체. XRO 정보객체는 여러 종류의 서브정보객체를 담을 수 있다.
 * 
 * @author Ancom
 * 
 */
public class XRObject extends PCEPObject {

//	private final static Logger logger = Logger.getLogger(ERObject.class);

	short flags = 0;
	boolean f = false;
	Vector<XROSubObj> subObjects = new Vector<XROSubObj>();

	public XRObject(byte messageType, byte[] data) {
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

	public XRObject(byte messageType) {
		this.messageType = messageType;
		// Header
		this.header = new CommonObjectHeader(PacketChain.XRO_OC,
				PacketChain.XRO_OT, false, false, 4 + CommonObjectHeader
						.getHeaderLength());

	}

	public XRObject(byte messageType, CommonObjectHeader header, byte[] content) {
		this.messageType = messageType;
		this.header = header;

		analyze(content);

	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];

		int ind = 0;
		// reserved
		data[ind++] = 0;
		data[ind++] = 0;
		// flag
		// BigE
		byte[] flg = ComputationUtils.intToByte(flags);
		data[ind] = flg[ind++];
		data[ind] = flg[ind++];

		// subobject adding
		Iterator<XROSubObj> soi = subObjects.iterator();
		while (soi.hasNext()) {
			byte[] subdata = (soi.next()).getContent();

			for (int i = 0; i < subdata.length; i++)
				data[ind++] = subdata[i];

		}

		return data;
	}

	@Override
	public void analyze(byte[] data) {
		// reserved = (short) ComputationUtils.byteToShort(data, 0);
		flags = (short) ComputationUtils.byteToShort(data, 2);
		f = (flags == 1) ? true : false;
		int datalength = 0;

		for (int i = 4; i < data.length; i += datalength) {
			datalength = data[i + 1];
			byte type = ComputationUtils.bitsToByte(data[i], 0, 7);
			byte[] tmp = new byte[datalength];
			for (int j = 0; j < datalength; j++) {
				tmp[j] = data[i + j];
			}

			// new subobject
			XROSubObj newsub;
			switch (type) {
			case 1:
				newsub = new XROIPv4Prefix(tmp);
				break;
			case 2:
				newsub = new XROIPv6Prefix(tmp);
				break;
			case 4:
				newsub = new XROSLU(tmp);
				break;
			case 34:
				newsub = new XROSRLG(tmp);
				break;
			case 32:
				newsub = new XROASNumber(tmp);
				break;
			default:
				newsub = null;
				break;
			}

			addSubObject(newsub);

		}

	}

	public void addSubObject(XROSubObj newsub) {
		int length = header.getLength() + newsub.length;
		header.setLength(length);
		subObjects.add(newsub);
	}

	public void delSubObject(XROSubObj delsub) {
		int length = header.getLength() - delsub.length;
		header.setLength(length);
		subObjects.remove(delsub);
	}

	public XROSubObj popEROSubObj() {
		return subObjects.remove(0);
	}

	public Iterator<XROSubObj> getSubObjectIterator() {
		return subObjects.iterator();
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.XRO_OC;
	}

	@Override
	public byte getObjectType() {
		return PacketChain.XRO_OT;
	}

	@Override
	public void handle(SessionAccessor sa) {
//		logger.debug("XRO - Subobjects");
		for (int i = 0; i < subObjects.size(); i++)
			subObjects.get(i).handle(sa);
	}

}

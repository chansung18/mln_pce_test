package pcep.packet;

import java.util.Iterator;
import java.util.Vector;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.manager.PacketChain;

/**
 * ERO 정보객체. ERO 정보객체는 여러 종류의 서브정보객체를 담을 수 있다.
 * 
 * @author Ancom
 * 
 */
public class ERObject extends PCEPObject {

//	protected final static Logger logger = Logger.getLogger(ERObject.class);

	protected Vector<EROSubObj> subObjects = new Vector<EROSubObj>();

	public ERObject(byte messageType, byte[] data) {
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

	public ERObject(byte messageType) {
		this.messageType = messageType;

		// Header
		this.header = new CommonObjectHeader(PacketChain.ERO_OC,
				PacketChain.ERO_OT, false, false, 0 + CommonObjectHeader
						.getHeaderLength());

	}

	public ERObject(byte messageType, CommonObjectHeader header, byte[] content) {
		this.messageType = messageType;
		this.header = header;
		analyze(content);
	}
	
	public int Size()
	{
		return subObjects.size();
	}

	@Override
	public byte[] getContents() {
		byte[] data = new byte[header.getObjectLength()];

		int ind = 0;

		// adding subobject
		Iterator<EROSubObj> soi = subObjects.iterator();
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
			EROSubObj newsub = null;
			switch (type) {
			case 1:
				newsub = new EROIPv4Prefix(tmp);
				break;
			case 2:
				newsub = new EROIPv6Prefix(tmp);
				break;
			case 3:

				if (datalength < 20)
					newsub = new EROLabelOTL(tmp);
				else {
					newsub = new EROLabelPTL(tmp);
				}
				break;
			case 4:

				newsub = new EROSLU(tmp);
				break;
			case 32:
				newsub = new EROASNumber(tmp);
				break;
			default:
				newsub = null;
				break;
			}

			// commented by Beak Du Sung. 2011.1.18 :	addSubObject(newsub);
			// added by Beak Du Sung ----
			   // Header length, already applied the subobjects length..
			subObjects.add(newsub);
			//--------------------
		}

	}

	public void addSubObject(EROSubObj newsub) {
		int length = header.getLength() + newsub.length;
		header.setLength(length);
		subObjects.add(newsub);
	}

	public void delSubObject(EROSubObj delsub) {
		int length = header.getLength() - delsub.length;
		header.setLength(length);
		subObjects.remove(delsub);
	}

	public EROSubObj popEROSubObj() {
		return subObjects.remove(0);
	}

	public Iterator<EROSubObj> getSubObjectIterator() {
		return subObjects.iterator();
	}

	@Override
	public byte getObjectClass() {
		return PacketChain.ERO_OC;
	}

	@Override
	public byte getObjectType() {
		return PacketChain.ERO_OT;
	}

	@Override
	public void handle(SessionAccessor sa) {
//		logger.debug("ERO - Subobjects");
		for (int i = 0; i < subObjects.size(); i++) {
			subObjects.get(i).handle(sa);
		}
	}

}
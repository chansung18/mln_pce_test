package pcep.packet;

import java.util.Iterator;

/**
 * PCEP 메시지 객체. PCEP 메시지 객체는 PCEP메시지공통헤더와, 메시지와 관련된 여러 정보객체들로 이루어져 있다.
 * 
 * @author Ancom
 */
public class PCEPMessage {
	// private final static Logger logger = Logger.getLogger(PCEPMessage.class);
	/**
	 * 메시지의 헤더.
	 * 
	 * @uml.property name="packetHeader"
	 * @uml.associationEnd
	 */
	private PCEPCommonHeader packetHeader = null;
	/**
	 * 메시지가 담고있는 복합정보객체. 복합정보객체는, 여러 정보객체들과 또 다른 복합정보객체들을 포함합니다.
	 * 
	 * @uml.property name="objectComposite"
	 * @uml.associationEnd
	 */
	private PCEPObjectComposite objectComposite = null;

	/**
	 * 메시지공통헤더와 정보객체를 이용하여 PCEP메시지를 생성합니다.
	 * 
	 * @param ph
	 *            메시지 공통헤더.
	 * @param ob
	 *            정보객체.
	 */
	public PCEPMessage(PCEPCommonHeader ph, PCEPObject ob) {
		this.packetHeader = ph;
		this.objectComposite = new PCEPObjectComposite();
		if (ob != null) {
			addPCEPObject(ob);
		}
	}

	/**
	 * 메시지 공통헤더만을 가지고 PCEP메시지를 생성합니다. KEEPALIVE 메시지의 경우, 포함하는 객체가 없으므로 이러한 방법으로
	 * 생성됩니다.
	 * 
	 * @param ph
	 *            메시지 공통헤더.
	 */
	public PCEPMessage(PCEPCommonHeader ph) {
		this.packetHeader = ph;
		this.objectComposite = new PCEPObjectComposite();
	}

	/**
	 * 메시지의 종류를 반환합니다.
	 * 
	 * @return
	 */
	public byte getPacketType() {
		return packetHeader.getType();
	}

	/**
	 * 메시지의 길이를 반환합니다.
	 * 
	 * @return
	 */
	public int getLength() {
		return packetHeader.getPacketLength();
	}

	/**
	 * 메시지 안의 정보객체의 헤더를 바이너리 형태로 반환합니다.
	 * 
	 * @return
	 */
	public byte[] getHeaderContents() {
		return packetHeader.getContents();
	}

	/**
	 * PCEP 정보객체를 복합정보객체에 추가합니다.
	 * 
	 * @param obj
	 *            추가할 정보객체.
	 */
	public void addPCEPObject(PCEPObjectAbstract obj) {
		objectComposite.addObject(obj);
	}

	/**
	 * PCEP 정보객체를 복합정보객체에 추가합니다.
	 * 
	 * @param obj
	 *            추가할 정보객체.
	 */
	public void removePCEPObject(PCEPObjectAbstract obj) {
		objectComposite.removeObject(obj);
	}

	/**
	 * 메시지 안의 복합객체의 Iterator를 반환한다.
	 * 
	 * @return Object 객체들의 Iterator
	 */
	public Iterator<PCEPObjectAbstract> getObjectIterator() {
		return objectComposite.getIterator();
	}

	/**
	 * 메시지의 내용을 바이너리 형태로 반환한다.
	 * 
	 * @return byte 배열 형태의 바이너리 정보
	 */
	public byte[] getContents() {
		byte[] data = new byte[getLength()];
		int index = 0;
		Iterator<PCEPObjectAbstract> iter = getObjectIterator();

		while (iter.hasNext()) {

			PCEPObjectAbstract obj = iter.next();

			byte[] tmpdata = obj.getBinaryContents();

			for (int i = 0; i < tmpdata.length; i++)
				data[index++] = tmpdata[i];

		}

		return data;
	}
}

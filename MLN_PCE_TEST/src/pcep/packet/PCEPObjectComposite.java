package pcep.packet;

import java.util.Iterator;
import java.util.Vector;

import pcep.PCEPSession.SessionAccessor;

/**
 * PCEP 복합정보객체. 여러 정보객체를 포함하는 객체.
 * 
 * @author Ancom
 * 
 */
public class PCEPObjectComposite extends PCEPObjectAbstract {
	// private final static Logger logger = Logger
	// .getLogger(PCEPObjectComposite.class);
	protected Vector<PCEPObjectAbstract> objectList = new Vector<PCEPObjectAbstract>();

	@Override
	public void addObject(PCEPObjectAbstract obj) {
		objectList.add(obj);
	}

	@Override
	public void removeObject(PCEPObjectAbstract obj) {
		objectList.remove(obj);
	}

	@Override
	public Iterator<PCEPObjectAbstract> getIterator() {
		this.arrange();
		return objectList.iterator();
	}

	@Override
	public void analyze(byte[] data) {

	}

	// public abstract Iterator<PCEPObjectAbstract> createIterator();

	@Override
	public byte getObjectClass() {
		return 0;
	}

	@Override
	public byte getObjectType() {
		return 0;
	}

	@Override
	public void handle(SessionAccessor sa) {

	}

	@Override
	public byte[] getBinaryContents() {

		byte[] data = new byte[this.getLength()];
		int index = 0;
		Iterator<PCEPObjectAbstract> iter = this.getIterator();

		while (iter.hasNext()) {

			PCEPObjectAbstract obj = iter.next();

			byte[] tmpdata = obj.getBinaryContents();
			for (int i = 0; i < tmpdata.length; i++)
				data[index++] = tmpdata[i];

		}

		return data;
	}

	/**
	 * 내부 객체들을 바이너리화하기 위해 정렬하는 함수.
	 */
	public void arrange() {

	}
	
	public void arrange(PCEPObjectComposite container)
	{
		
	}

	@Override
	public int getLength() {
		return 0;
	}
}

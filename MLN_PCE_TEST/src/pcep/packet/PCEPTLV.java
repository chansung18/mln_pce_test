package pcep.packet;

import java.util.Vector;

/**
 * PCEP에서 사용하는 공통 TLV 형식
 * 
 * @author Ancom
 */
public class PCEPTLV {
	/**
	 * @uml.property name="type"
	 */
	private int type;
	/**
	 * @uml.property name="length"
	 */
	private int length;
	private final Vector<Integer> values;

	public PCEPTLV(int type, int length) {
		this.type = type;
		this.length = length; // in byte
		values = new Vector<Integer>();
	}

	public void addValue(int value) {
		values.add(value);
	}

	public int getValue(int index) {
		return values.get(index);
	}

	/**
	 * @return
	 * @uml.property name="type"
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 * @uml.property name="type"
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return
	 * @uml.property name="length"
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length
	 * @uml.property name="length"
	 */
	public void setLength(int length) {
		this.length = length;
	}

	public int getTLVLength() {
		return 4 + length;
	}

	public byte[] getContents() {
		byte[] data = new byte[4 + length];
		int index = 0;

		byte[] t = ComputationUtils.intToByte(type);
		byte[] l = ComputationUtils.intToByte(length);

		// BigE
		data[index++] = t[2];
		data[index++] = t[3];
		data[index++] = l[2];
		data[index++] = l[3];

		for (int i = 0; i < values.size(); i++) {
			byte[] v = ComputationUtils.intToByte(values.get(i));

			for (int j = 0; j < 4; j++)
				data[index++] = v[j];
		}

		return data;
	}

}

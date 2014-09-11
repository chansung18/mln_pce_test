package pcep.packet;

/**
 * 각종 연산을 돕는 함수들로 이루어진 클래스입니다.
 * 
 * @author Ancom
 * 
 */
public class ComputationUtils {
	// private final static Logger logger =
	// Logger.getLogger(ComputationUtils.class);

	/**
	 * 16진수를 표현하는 함수.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String prettyBytesToString(byte[] bytes) {
		StringBuffer received = new StringBuffer();
		received.append("{");
		received.append("\n");

		String dump = HexDump.hexDump(bytes);
		received.append(dump);

		// for (int i=0; i<bytes.length; i++) {
		// received.append("'" + bytes[i] + "',");
		// }

		received.append("}");

		return received.toString();
	}

	/**
	 * 바이트 배열을 정수형으로 변환합니다.
	 * 
	 * @param buf
	 *            바이트 배열.
	 * @return 변환된 정수.
	 */
	public static int byteToInt(byte[] buf) {
		return byteToInt(buf, 0);
	}

	/**
	 * 바이트 배열을 정수형으로 변환합니다.
	 * 
	 * @param buf
	 *            바이트 배열.
	 * @param start
	 *            변환 시작 위치.
	 * @return 변환된 정수.
	 */
	public static int byteToInt(byte[] buf, int start) {
		int i = 0;
		int pos = start;

		int tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 24;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 16;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 8;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 0;
		i += tmp;

		return i;
	}

	/**
	 * 바이트 배열에서 주어진 구간을 정수로 변환합니다.
	 * 
	 * @param buf
	 *            바이트 배열.
	 * @param start
	 *            변환 시작 위치.
	 * @param numOfByte
	 *            변환할 바이트 길이.
	 * @return 변환된 정수.
	 */
	public static int byteToIntExtraction(byte[] buf, int start, int numOfByte) {
		int i = 0;
		int pos = start;

		int tmp;
		for (int j = numOfByte - 1; j >= 0; j--) {
			tmp = unsignedByteToInt(buf[pos++]) << j * 8;
			i += tmp;
		}

		return i;
	}

	/**
	 * 바이트 배열을 Short형으로 변환합니다.
	 * 
	 * @param buf
	 *            바이트 배열.
	 * @param start
	 *            시작 위치.
	 * @return 변환된 정수.
	 */
	public static int byteToShort(byte[] buf, int start) {
		int i = 0;
		int pos = start;

		int tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 8;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 0;
		i += tmp;

		return i;
	}

	/**
	 * 바이트배열을 Long형 정수로 변환합니다.
	 * 
	 * @param buf
	 *            바이트 배열.
	 * @param start
	 *            시작 위치.
	 * @return 변환된 Long형 정수.
	 */
	public static long byteToLong(byte[] buf, int start) {
		long i = 0;
		int pos = start;

		int tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 56;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 48;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 40;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 32;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 24;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 16;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 8;
		i += tmp;
		tmp = unsignedByteToInt(buf[pos++]) << 0;
		i += tmp;

		return i;
	}

	/**
	 * 1바이트(8bit) 내에 표현된 비트 구간을 잘라냅니다.
	 * 
	 * @param buf
	 * @param offset
	 * @param bitLength
	 * @return
	 */
	public static byte bitsToByte(byte buf, int offset, int bitLength) {
		byte bits = 0;
		if (bitLength < 1)
			// Impossible!
			return 0;

		int tmp;
		tmp = ((buf << (8 - (byte) (offset + bitLength))) & 0xff);
		// tmp = ((buf << (offset)) & 0xff);
		bits = (byte) ((tmp >>> (8 - bitLength)) & 0xff);

		return bits;
	}

	public static long secondsToMillis(int seconds) {
		return seconds * 1000L;
	}

	public static int millisToSeconds(long millis) {
		return (int) (millis / 1000L);
	}

	public static int unsignedByteToInt(byte i) {
		if (i < 0) {
			return (i & 0x7F) + 0x80;
		} else {
			return i;
		}
	}

	/**
	 * 정수를 바이트 배열로 변환합니다.
	 * 
	 * @param buf
	 *            변환할 정수.
	 * @return 바이트 배열.
	 */
	public static byte[] intToByte(int buf) {
		byte[] toSend = new byte[4];

		toSend[3] = (byte) ((buf >> 0) & 0xFF);
		toSend[2] = (byte) ((buf >> 8) & 0xFF);
		toSend[1] = (byte) ((buf >> 16) & 0xFF);
		toSend[0] = (byte) ((buf >> 24) & 0xFF);

		return toSend;
	}

	/**
	 * 정수를 바이트 배열로 변환합니다.
	 * 
	 * @param i
	 *            변환할 정수.
	 * @return 바이트 배열.
	 */
	public static byte[] convertIntToByteArray(int i) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((i >> 24) & (0xFF));
		bytes[1] = (byte) ((i >> 16) & (0xFF));
		bytes[2] = (byte) ((i >> 8) & (0xFF));
		bytes[3] = (byte) ((i >> 0) & (0xFF));

		return bytes;
	}

	/**
	 * Long형 정수를 바이트 배열로 변환합니다.
	 * 
	 * @param buf
	 *            Long형 정수.
	 * @return 바이트 배열.
	 * 
	 */
	public static byte[] longToByte(long buf) {
		byte[] toSend = new byte[8];

		toSend[7] = (byte) ((buf >> 0) & 0xFF);
		toSend[6] = (byte) ((buf >> 8) & 0xFF);
		toSend[5] = (byte) ((buf >> 16) & 0xFF);
		toSend[4] = (byte) ((buf >> 25) & 0xFF);
		toSend[3] = (byte) ((buf >> 32) & 0xFF);
		toSend[2] = (byte) ((buf >> 40) & 0xFF);
		toSend[1] = (byte) ((buf >> 48) & 0xFF);
		toSend[0] = (byte) ((buf >> 56) & 0xFF);

		return toSend;
	}

	public static long unsignedIntToLong(int i) {
		long plus = 0;
		plus -= (i & 0x80000000);
		i &= 0x7FFFFFFF;

		return (i + plus);
	}

	/**
	 * 바이트 배열을 문자열화한다.
	 * 
	 * @param data
	 *            바이트 배열.
	 * @param startIndex
	 *            문자열화를 시작할 위치
	 * @return 변환된 문자열
	 */
	public static String byteToString(byte[] data, int startIndex) {
		int counter = 0;

		while (((counter + startIndex) < data.length)
				&& (data[counter + startIndex] != 0)) {
			counter++;
		}

		byte[] desc = new byte[counter];
		System.arraycopy(data, startIndex, desc, 0, counter);

		return new String(desc);
	}

	/**
	 * 바이트 배열을 16진수 형식으로 문자열화한다.
	 * 
	 * @param data
	 *            바이트 배열.
	 * @param startIndex
	 *            문자열화를 시작할 위치
	 * @return 변환된 문자열
	 */
	public static String byteToHexString(byte[] data, int startIndex) {
		int counter = 0;
		StringBuffer str = new StringBuffer();
		while (((counter + startIndex) < data.length)
				&& (data[counter + startIndex] != 0)) {
			counter++;
		}

		byte[] desc = new byte[counter];
		System.arraycopy(data, startIndex, desc, 0, counter);

		for (int i = 0; i < desc.length; i++) {
			str.append(HexDump.byteToHexString(desc[i]));
			str.append(" ");
		}

		return str.toString();
	}

	/**
	 * 바이트 배열을 문자열화한다. 숫자나 영문자에 대해서만 변환하며, 그 이외의 값들은 온점(.)으로 표현한다.
	 * 
	 * @param data
	 *            바이트 배열.
	 * @param startIndex
	 *            문자열화를 시작할 위치
	 * @return 변환된 문자열
	 */
	public static String byteToStringNormal(byte[] data, int startIndex) {
		int counter = 0;
		StringBuffer str = new StringBuffer();
		while (((counter + startIndex) < data.length)
				&& (data[counter + startIndex] != 0)) {
			counter++;
		}

		byte[] desc = new byte[counter];
		System.arraycopy(data, startIndex, desc, 0, counter);

		for (int i = 0; i < desc.length; i++) {

			if (((desc[i] & 0xff) > 0x001f) && ((desc[i] & 0xff) < 0x007f)) {
				Character ch = new Character((char) desc[i]);
				str.append(ch);
			} else {
				str.append(".");
			}

		}

		return str.toString();
	}

}

/**
 * 16진수 처리를 위한 함수들로 이루어진 클래스입니다.
 * 
 * @author Ancom
 * 
 */
class HexDump {

	public static String byteToHexString(byte b) {
		return intToHexString(b, 2, '0');
	}

	public static String intToHexString(int num, int width, char fill) {
		String result = "";

		if (num == 0) {
			result = "0";
			width--;
		} else {
			while (num != 0 && width > 0) {
				String tmp = Integer.toHexString(num & 0xf);
				result = tmp + result;
				num = (num >> 4);
				width--;
			}
		}
		for (; width > 0; width--) {
			result = fill + result;
		}
		return result;
	}

	public static String hexDump(byte data[]) {
		return hexDump(data, data.length);
	}

	public static String hexDump(byte data[], int length) {
		int i;
		int j;
		final int bytesPerLine = 16;
		String result = "";

		for (i = 0; i < length; i += bytesPerLine) {
			// print the offset as a 4 digit hex number
			result = result + intToHexString(i, 4, '0') + "  ";

			// print each byte in hex
			for (j = i; j < length && (j - i) < bytesPerLine; j++) {
				result = result + byteToHexString(data[j]) + " ";
			}

			// skip over to the ascii dump column
			for (; 0 != (j % bytesPerLine); j++) {
				result = result + "   ";
			}
			result = result + "  |";

			// print each byte in ascii
			for (j = i; j < length && (j - i) < bytesPerLine; j++) {
				if (((data[j] & 0xff) > 0x001f) && ((data[j] & 0xff) < 0x007f)) {
					Character ch = new Character((char) data[j]);
					result = result + ch;
				} else {
					result = result + ".";
				}
			}
			result = result + "|";
			result += "\n";
		}
		return result;
	}

}

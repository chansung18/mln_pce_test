package pcep.element;

public final class Server {

	// private final static Logger LOGGER = Logger.getLogger(Server.class);

	/**
	 * 서버의 주소.
	 * */
	protected String m_address = null;

	/** 서버의 포트 */
	protected int m_port = -1;

	/**
	 * @param address
	 *            서버의 주소.
	 * @param port
	 *            서버의 포트.
	 * @throws NullPointerException
	 *             주소가 Null값인 경우 발생한다.
	 * @throws IllegalArgumentException
	 *             포트가 0 ~ 65535 사이의 값이 아닌 경우 발생한다.
	 */
	public Server(String address, int port) {
		if (address == null)
			throw new NullPointerException("address cannot be null");
		if (port < 0 || port > 65535)
			throw new IllegalArgumentException(
					"port cannot be less than 0 and grather than 65535");
		m_address = address;
		m_port = port;
	}

	/**
	 * 서버의 주소를 반환.
	 * 
	 * @return
	 */
	public String getAddress() {
		return m_address;
	}

	/**
	 * 서버의 포트를 반환.
	 * 
	 * @return 서버의 포트
	 */
	public int getPort() {
		return m_port;
	}

	@Override
	public String toString() {
		return "[" + m_address + ":" + m_port + "]";
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		// 서버와 포트가 같으면 두 서버 객체는 같은 것이다.
		if (((Server) o).getAddress().equals(m_address)
				&& ((Server) o).getPort() == m_port)
			return true;
		return false;
	}

}
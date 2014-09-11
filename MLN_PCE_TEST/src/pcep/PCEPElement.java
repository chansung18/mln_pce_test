package pcep;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.PriorityQueue;
import java.util.Vector;

import javax.swing.event.EventListenerList;

//import org.apache.log4j.Logger;

import pcep.element.Server;
import pcep.eventhandler.InputListener;
import pcep.eventhandler.InwardEventListener;

/**
 * PCEP 프로토콜 전반을 다루는 객체.
 * 
 * @author Ancom
 */
public class PCEPElement {

//	private final static Logger logger = Logger.getLogger(PCEPElement.class);

	/**
	 * PCEP 버젼. Version 1.
	 */
	public static final int VERSION = 1;
	// PCEP Session Manaager
	/**
	 * 세션을 관리하는 객체
	 */
	Vector<PCEPSession> sessionList = null;
	PriorityQueue<Byte> nextID = new PriorityQueue<Byte>();

	private final EventListenerList listeners = new EventListenerList();
	/**
	 * 연결을 받기 위한 쓰레드 객체
	 * 
	 * @uml.property name="st"
	 * @uml.associationEnd
	 */
	ServerThread st = null;

	/**
	 * PCEP가 사용하는 포트. 기본으로 4189번 포트를 사용.
	 */
	private int port = 4189;
	/**
	 * PCEP 객체의 최다 연결수. 기본 값 100.
	 */
	private int connectionNumber = 100;

	public void init() {

		// Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
		addInwardListener(new InputListener(this));
		sessionList = new Vector<PCEPSession>();
		nextID.add((byte) 0);
	}

	/**
	 * 기본 포트와 기본 최다연결수를 가지고 PCEP객체를 만드는 함수. 세션리스트를 초기화하고, 서버쓰레드를 동작시킨다.
	 */
	public PCEPElement() {
		init();
		st = new ServerThread(this, port, connectionNumber);
		st.start();
	}

	/**
	 * 지정된 포트와 지정된 최다연결수를 가지고 PCEP객체를 만드는 함수. 세션리스트를 초기화하고, 서버쓰레드를 동작시킨다.
	 * 
	 * @param port
	 *            연결을 받을 포트번호.
	 * @param connectionNumber
	 *            최다 허용 연결 수.
	 */
	public PCEPElement(int port, int connectionNumber) {
		this.port = port;
		this.connectionNumber = connectionNumber;
		init();
		st = new ServerThread(this, this.port, this.connectionNumber);
		st.start();
	}

	public PCEPElement(Server server)
	{
		init();
		createSession(server);
	}
	/**
	 * 새로운 PCEP세션을 세션리스트에 추가하는 함수.
	 * 
	 * @param s
	 *            추가할 세션.
	 */
	public void addPCEPSession(PCEPSession s) {

		byte id = nextID.poll();
		if (nextID.isEmpty())
			nextID.add((byte) (id + 1));
		s.setSID(id);
		sessionList.add(s);

	}

	/**
	 * 세션 리스트에서 세션을 제거하는 함수.
	 * 
	 * @param s
	 *            제거할 세션.
	 */
	public void removePCEPSession(PCEPSession s) {
		nextID.add(s.getSid());
		sessionList.remove(s);
	}

	public void removePCEPSessionById(int sid) {
		nextID.add((byte) sid);
		sessionList.remove(getPCEPSession(sid));
	}

	/**
	 * 해당 세션ID를 갖는 세션을 반환하는 함수.
	 * 
	 * @param sid
	 *            찾을 세션의 세션ID.
	 * @return
	 */
	public PCEPSession getPCEPSession(int sid) {
		int size = sessionList.size();
		for (int i = 0; i < size; i++) {
			PCEPSession s = sessionList.get(i);
			if (s.getSid() == sid)
				return s;
		}
		return null;
	}

	public Vector<PCEPSession> getSessionList() {
		return sessionList;
	}

	/**
	 * 연결 정보를 가지고 새로운 PCEP 세션을 만드는 함수. PCC의 경우 사용.
	 * 
	 * @param server
	 *            연결할 서버 정보입니다.
	 * @return
	 */
	public PCEPSession createSession(Server server) {
//		logger.debug("Create Session");
		// Session creation
		PCEPSession s = new PCEPSession(this, server);
		// Add Session to session list.
		addPCEPSession(s);
		return s;

	}

	/**
	 * 연결이 들어온 경우, 서버쓰레드에서 소켓을 받아와 새로운 PCEP세션을 만드는 함수. PCE의 경우 사용.
	 * 
	 * @param s
	 *            서버 쓰레드에서 받아오는 연결된 소켓 객체.
	 * @return
	 */
	public PCEPSession createSession(Socket s) {

		// Session existence checking
		// If same PCEP peer tries to set up second TCP connection,
		// it stops the connection and send error message.
		Server sv = new Server(s.getInetAddress().getHostAddress(), s.getPort());
		int cnt = sessionList.size();
		for (int i = 0; i < cnt; i++) {
			PCEPSession tmpSession = sessionList.get(i);
			if (tmpSession.getServer().equals(sv)
					&& (tmpSession.getSessionAccessor().getState() == PCEPSession.STATE_SESSIONUP)) {
				tmpSession.getSessionAccessor().throwError(9, 1);
				return null;
			}
		}

		PCEPSession newSession = new PCEPSession(this);
		try {
			newSession.establishedConnection(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Add Session to session list.
		this.addPCEPSession(newSession);
		return newSession;
	}

	/**
	 * PCEP객체를 닫고 모든 연결을 종료시키는 함수.
	 */
	public void close() {
		if (st != null) {

			st.state = 0;
			// Let all session close...
			try {
				for (int i = 0; i < sessionList.size(); i++) {
					sessionList.get(i).close();
				}
				new Socket().connect(new InetSocketAddress(InetAddress
						.getByName("127.0.0.1"), port));

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// st.stop();
		}
	}

	public void processInwardEvent(int sid, byte messageType) throws Exception {
		InwardEventListener[] inwardListeners = listeners
				.getListeners(InwardEventListener.class);
		for (int i = 0; i < inwardListeners.length; i++) {
			InwardEventListener inwardListener = inwardListeners[i];
			inwardListener.messageArrived(sid, messageType);
		}
	}

	public void notifySessionUp(int sid) throws Exception {
		InwardEventListener[] inwardListeners = listeners
				.getListeners(InwardEventListener.class);
		for (int i = 0; i < inwardListeners.length; i++) {
			InwardEventListener inwardListener = inwardListeners[i];
			inwardListener.sessionUp(sid);
		}
	}

	public void notifySessionDown(PCEPSession s) throws Exception {
		// Session deletion
		removePCEPSessionById(s.getSid());
		// Session Down event handling
		InwardEventListener[] inwardListeners = listeners
				.getListeners(InwardEventListener.class);
		for (int i = 0; i < inwardListeners.length; i++) {
			InwardEventListener inwardListener = inwardListeners[i];
			inwardListener.sessionDown(s);
		}

	}

	/**
	 * 입력 이벤트 리스너를 추가하는 함수.
	 * 
	 * @param inwardListener
	 *            추가할 InwardEventListener 객체.
	 */
	public void addInwardListener(InwardEventListener inwardListener) {

		if (inwardListener == null)
			throw new NullPointerException("connectionListener cannot be null");
		listeners.add(InwardEventListener.class, inwardListener);
	}

	/**
	 * 입력 이벤트 리스너를 제거하는 함수.
	 * 
	 * @param inwardListener
	 *            제거할 InwardEventListener 객체.
	 */
	public void removeInwardListener(InwardEventListener inwardListener) {
		if (inwardListener == null)
			throw new NullPointerException("connectionListener cannot be null");
		listeners.remove(InwardEventListener.class, inwardListener);
	}

	// Session Working
	/**
	 * 외부로부터의 연결을 감지하는 쓰레드 객체.
	 * 
	 * @author Ancom
	 */
	class ServerThread extends Thread {
		/**
		 * @uml.property name="element"
		 * @uml.associationEnd
		 */
		PCEPElement element;
		ServerSocket ss;
		Socket s;
		private int port = 4189;
		private int connectionNumber = 0;
		private int currentConnectedNumber = 0;
		char buf[] = new char[256];
		public int state = 1;

		/**
		 * 주어진 포트와 최다연결수를 가지고 쓰레드 정보를 초기화.
		 * 
		 * @param e
		 *            이 서버 쓰레드를 갖는 PCEP객체.
		 * @param port
		 *            연결을 받을 포트.
		 * @param connectionNumber
		 *            최다 허용 연결 수.
		 */
		public ServerThread(PCEPElement e, int port, int connectionNumber) {
			this.element = e;
			this.port = port;
			this.connectionNumber = connectionNumber;

		}

		@Override
		public void run() {
			try {
//				logger.debug("Server Started " + port);
				ss = new ServerSocket(port);
				while (true) {
					if (currentConnectedNumber >= connectionNumber)
						continue;

					s = ss.accept();
					// Session Creation
					if (state != 0) {
						createSession(s);
						currentConnectedNumber = sessionList.size();
					} else
						break;

				}
				s.close();
				ss.close();
//				logger.debug("Server Closed " + port);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class ShutdownHookThread extends Thread {

		// 프로세스가 종료되면, 연결되어있던 모든 세션을 종료한다.
		@Override
		public void run() {
			close();
		}
	}
}

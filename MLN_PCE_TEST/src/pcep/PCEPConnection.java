package pcep;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.event.EventListenerList;

//import org.apache.log4j.Logger;

import pcep.PCEPSession.SessionAccessor;
import pcep.element.Server;
import pcep.eventhandler.ConnectionListener;
import pcep.eventhandler.NormalConnectionListener;
import pcep.manager.PacketChain;
import pcep.manager.PacketContext;
import pcep.packet.CloseObject;
import pcep.packet.OpenObject;
import pcep.packet.PCEPCommonHeader;
import pcep.packet.PCEPMessage;
import pcep.packet.PCEPObjectAbstract;

/**
 * PCEP의 소켓 통신을 담당하는 객체
 * 
 * @author Ancom
 */
public class PCEPConnection {
//	private final static Logger logger = Logger.getLogger(PCEPConnection.class);
	/**
	 * 이 연결 객체를 생성하는 세션 객체를 저장하며 연결 객체를 초기화.
	 * 
	 * @uml.property name="session"
	 * @uml.associationEnd
	 */
	private PCEPSession session = null;

	/**
	 * 메시지 큐.
	 */
	private final ConcurrentLinkedQueue<PCEPMessage> senderQueue = new ConcurrentLinkedQueue<PCEPMessage>();

	// Event Handler!
	/**
	 * 통신 이벤트 리스너.
	 */
	private final EventListenerList listeners = new EventListenerList();

	/**
	 * 메시지 처리를 담당하는 객체.
	 * 
	 * @uml.property name="packetChain"
	 * @uml.associationEnd
	 */
	private PacketChain packetChain = null;

	/**
	 * 연결을 관리하는 쓰레드.
	 * 
	 * @uml.property name="connectionThread"
	 * @uml.associationEnd
	 */
	private ConnectionThread connectionThread = null;

	/**
	 * 연결 대상(서버) 객체 PCC가 사용.
	 * 
	 * @uml.property name="server"
	 * @uml.associationEnd
	 */
	private Server server = null;

	/**
	 * 연결 시간. 기본값은 60초. 연결 시도 시 이 시간을 넘겨서 연결이 이루어지지않으면 연결이 실패한 것으로 간주한다.
	 */
	private final int connectTime = 60 * 1000; // ms

	/**
	 * 연결 재시도 카운터.
	 */
	private int connectRetry = 0;
	/**
	 * 최다 연결 재시도 회수. 기본값은 5.
	 */
	private final int connectMaxRetry = 5;
	/**
	 * OPEN 메시지 재송신 카운터
	 */
	private int openRetry = 0;
	/**
	 * EEPALIVE 메시지 송신 주기. 마지막 메시지를 보낸 뒤 이 시간이 흐르면, 아직 세션이 살아있음을 알리기 위해
	 * KEEPALIVE 메시지를 보냅니다.
	 */
	private int keepalivetime = 30 * 1000;
	/**
	 * DEADTIME : 이 시간이 경과될 때까지 PCEP 상대로부터 아무런 메시지가 도착하지 않는다면, PCEP는 해당 세션이 죽었다고
	 * 판단하고 연결을 종료합니다.
	 * 
	 */
	private int deadtime = 240 * 1000;

	/**
	 * OPENWAIT : OPENWAIT 상태에서, 상대로부터 OPEN 메시지를 기다리는 시간.
	 * 
	 */
	private final int openwait = 60 * 1000;
	/**
	 * KEEPWAIT : KEEPWAIT 상태에서, 상대로부터 KEEPALIVE 메시지를 기다리는 시간.
	 */
	private final int keepwait = 60 * 1000;
	private int synctime = 60 * 1000;

	long connectTimer = 0; // sec
	long openWaitTimer = 0;
	long keepAliveTimer = 0;
	long deadTimer = 0;
	long syncTimer = 0;
	long sleepTimer = 0;

	private long keepwaiter = 0;

	/**
	 * RemoteOK 상태입니다. RemoteOK 상태는 시스템이 적합한 OPEN 메시지를 받았을 때 참이 됩니다.
	 * 
	 * @uml.property name="remoteOK"
	 */
	private boolean remoteOK = false;
	/**
	 * LocalOK 상태입니다. LocalOK 상태는 보낸 OPEN 메시지에 대한 응답으로써 KEEPALIVE 메시지를 수신했을 때 참이
	 * 됩니다.
	 * 
	 * @uml.property name="localOK"
	 */
	private boolean localOK = false;

	/**
	 * KeepAlive를 사용할지 말지 결정할 때 사용하는 플래그. Keepalive 대기 시간을 0으로 설정하면 Keepalive
	 * 메커니즘을 사용하지 않는다.
	 */
	private boolean useKA = true;

	/**
	 * 이 연결 객체를 생성하는 세션 객체를 저장하며 연결 객체를 초기화합니다.
	 * 
	 * @param session
	 *            이 연결 객체를 갖고 있는 세션 객체입니다. 생성할 때 주어집니다.
	 */
	public PCEPConnection(PCEPSession session) {
		if (session == null)
			throw new NullPointerException("session cannot be null");
		this.session = session;
		this.packetChain = new PacketChain(this);
		addConnectionListener(new NormalConnectionListener(this));
		// Idle state
	}

	/**
	 * 서버를 설정합니다.
	 * 
	 * @param server
	 *            PCC의 경우, 연결하려는 PCE의 서버 객체가 됩니다.
	 * @uml.property name="server"
	 */
	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * 서버 객체를 반환합니다.
	 * 
	 * @return 세션이 가진 서버 객체
	 * @uml.property name="server"
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * 이 객체가 가긴 세션 접근자 객체를 반환합니다.
	 * 
	 * @return PCEPConnection 객체가 가진 세션 접근자
	 */
	public SessionAccessor getSessionAccessor() {
		return session.getSessionAccessor();
	}

	// Connect
	/**
	 * 연결 대상(서버)로 연결을 시도하는 함수. PCC의 경우 사용합니다.
	 * 
	 * @param server
	 *            서버 정보를 담고 있는 Server 객체입니다.
	 * @throws Exception
	 */
	public void connect(Server server) throws Exception {
		if (server == null)
			throw new NullPointerException("server cannot be null");
		this.server = server;

		try {
			connectionThread = new ConnectionThread(getSessionAccessor()
					.getKeepalive(), getSessionAccessor().getDeadtimer(),
					getSessionAccessor().getSynctimer());

			// initiate Connect Timer
			connectTimer = System.currentTimeMillis();
			// Move to TCP Pending State.
			getSessionAccessor().setState(PCEPSession.STATE_TCPPENDING);

			// Connection Trying Loop
			// If Connection fails,
			// Connection Retry++
			// and retry to connect until connection timer < max connection
			// if it fails so many times, goto Idle and shut up.
			while (!connectionThread.openConnection(server.getAddress(), server
					.getPort())) {
				connectRetry++;
				Thread.sleep(connectRetry * 1000);
//				logger.debug("Retry Connecting... " + connectRetry);
				connectTimer = System.currentTimeMillis();
				if (connectRetry >= connectMaxRetry) {
					getSessionAccessor().setState(PCEPSession.STATE_IDLE);
					break;
				}
			}
			notifyConnectionEstablished();
		} catch (IOException e) {
			throw new Exception("Unable to connect to PCE: " + server, e);
		}
	}

	/**
	 * 연결 대상(서버)로 연결을 시도하는 함수.
	 * 
	 * @throws Exception
	 */
	public void connectToServer() throws Exception {
		connect(this.server);
	}

	/**
	 * 받은 TCP 연결을 가지고 PCEP 연결을 맺는 함수. PCE의 경우 사용.
	 * 
	 * @param socket
	 *            소켓 객체.
	 * @throws Exception
	 */
	public void connect(Socket socket) throws Exception {
		if (socket == null)
			throw new NullPointerException("socket can't be null.");

		try {
			connectionThread = new ConnectionThread(getSessionAccessor()
					.getKeepalive(), getSessionAccessor().getDeadtimer(),
					getSessionAccessor().getSynctimer());

			// Initiate Connect Timer
			connectTimer = System.currentTimeMillis();
			// Move to TCP Pending State.
			getSessionAccessor().setState(PCEPSession.STATE_TCPPENDING);
			setServer(new Server(socket.getInetAddress().getHostAddress(),
					socket.getPort()));

			connectionThread.openConnection(socket);

			// Notify connection established
			notifyConnectionEstablished();
		} catch (IOException e) {
			notifyConnectionError(new Exception("Unable to connect to PCE ", e));

		}
	}

	// Disconnect
	/**
	 * 연결을 종료하는 함수. 종료시키기 전에 상대에게 연결 종료를 통지할 지 willSendMessage 인자를 통해 결정할 수
	 * 있습니다.
	 * 
	 * @param willSendMessage
	 *            연결 종료 메시지를 전송할 지 결정하는 플래그. 참이면 연결 종료 메시지를 전송.
	 * @param value
	 *            연결 종료의 값. 메시지를 전송할 때 사용
	 * @throws Exception
	 */
	public void disconnect(boolean willSendMessage, int value) throws Exception {
		try {

			if (connectionThread != null) {
				connectionThread.closeConnection(willSendMessage, value);
				connectionThread = null;
			}
			server = null;

		} catch (IOException e) {
//			logger.error("IOException occured while trying to disconnect", e);
			throw new Exception("Unable to close connection to server", e);
		}
	}

	/**
	 * 연결 이벤트 리스너를 추가하는 함수.
	 * 
	 * @param connectionListener
	 *            ConnectionLister 객체
	 */
	public void addConnectionListener(ConnectionListener connectionListener) {
		if (connectionListener == null)
			throw new NullPointerException("connectionListener cannot be null");
		listeners.add(ConnectionListener.class, connectionListener);
	}

	/**
	 * 연결 이벤트 리스너를 제거하는 함수.
	 * 
	 * @param connectionListener
	 *            제거할 ConnectionListener 객체
	 */
	public void removeConnectionListener(ConnectionListener connectionListener) {
		if (connectionListener == null)
			throw new NullPointerException("connectionListener cannot be null");
		listeners.remove(ConnectionListener.class, connectionListener);
	}

	/**
	 * 연결이 되었을 때 연결 이벤트 리스너들에게 연결 이벤트를 발생시키는 함수.
	 * 
	 * @throws Exception
	 */
	protected void notifyConnectionEstablished() throws Exception {
		ConnectionListener[] connectionListeners = listeners
				.getListeners(ConnectionListener.class);
		for (int i = 0; i < connectionListeners.length; i++) {
			ConnectionListener connectionListener = connectionListeners[i];
			connectionListener.connectionEstablished();
		}
	}

	/**
	 * 연결이 종료되었을 때 연결 이벤트 리스너들에게 연결 종료 이벤트를 발생시키는 함수.
	 * 
	 * @throws Exception
	 */
	protected void notifyConnectionClosed() throws Exception {
		// session.getSessionAccessor().setSessionState(SessionState.DISCONNECTED);
		ConnectionListener[] connectionListeners = listeners
				.getListeners(ConnectionListener.class);
		for (int i = 0; i < connectionListeners.length; i++) {
			ConnectionListener connectionListener = connectionListeners[i];
			connectionListener.connectionClosed();
		}
	}

	/**
	 * 연결 오류 시, 연결 이벤트 리스너들에게 연결 오류 이벤트를 발생시키는 함수.
	 * 
	 * @param e
	 * @throws Exception
	 */
	protected void notifyConnectionError(final Exception e) throws Exception {
		ConnectionListener[] connectionListeners = listeners
				.getListeners(ConnectionListener.class);
		for (int i = 0; i < connectionListeners.length; i++) {
			ConnectionListener connectionListener = connectionListeners[i];
			connectionListener.connectionError(e);
		}
	}

	/**
	 * RemoteOK 상태를 전환하는 함수.
	 */
	public void toggleRemoteOK() {
		connectionThread.toggleRemoteOK();
	}

	/**
	 * RemoteOK 상태를 반환하는 함수. RemoteOK 상태는 적합한 OPEN 메시지를 받았을 때 참이 됨.
	 * 
	 * @return 현재의 RemoteOK 상태
	 * @uml.property name="remoteOK"
	 */
	public boolean isRemoteOK() {
		return remoteOK;
	}

	/**
	 * RemoteOK 상태를 설정하는 함수.
	 * 
	 * @param remoteOK
	 *            설정할 RemoteOK의 상태
	 * @uml.property name="remoteOK"
	 */
	public void setRemoteOK(boolean remoteOK) {
		this.remoteOK = remoteOK;
	}

	/**
	 * LocalOK 상태를 반환하는 함수. LocalOK 상태는 보낸 OPEN 메시지가 유효하다는 의미에서 KEEPALIVE 메시지를
	 * 받았을 때 참이 된다.
	 * 
	 * @return 현재 LocalOK 상태
	 * @uml.property name="localOK"
	 */
	public boolean isLocalOK() {
		return localOK;
	}

	/**
	 * LocalOK 상태를 설정하는 함수
	 * 
	 * @param localOK
	 *            설정할 LocalOK의 값
	 * @uml.property name="localOK"
	 */
	public void setLocalOK(boolean localOK) {
		this.localOK = localOK;
	}

	/**
	 * OPEN 메시지를 분석하고, 세션 특성을 확인하여 확정짓는 함수.
	 * 
	 * @param keepalive
	 *            PCEP 상대로부터 수신한 KEEPALIVE 속성
	 * @param deadtimer
	 *            PCEP 상대로부터 수신한 DEADTIMER 속성
	 * @param sid
	 *            PCEP 상대의 세션 ID
	 */
	public void checkOpen(byte keepalive, byte deadtimer, byte sid) {
		// Check PCEP Characteristic

		// Session Characteristic negotiating phase is omitted.
		// Instead, session characteristics are determined by following method:
		// 1) If received KEEPALIVE is greater than this, it changes its
		// KEEPALIVE for received.
		// 2) If received DEADTIMER is greater than this, it changes its
		// DEADTIMER for received.
		// 3) If received KEEPALIVE is 0(no keepalive), it changes its KEEPALIVE
		// and DEADTIMER as 0.(disable keepalive)
		if (keepalivetime != 0 && keepalive * 1000 > this.keepalivetime) {
			this.getSessionAccessor().setKeepalive(keepalive);
			this.keepalivetime = keepalive * 1000;

		}
		if (keepalive == 0 || keepalivetime == 0) {
			useKA = false;
			this.keepalivetime = 0;
		}

		if (deadtimer * 1000 > this.deadtime) {
			this.getSessionAccessor().setDeadtimer(deadtimer);
			this.deadtime = deadtimer * 1000;
		}
		if (!useKA) {
			this.getSessionAccessor().setDeadtimer(0);
			this.deadtime = 0;
		}

		if (false) {
			// Dead Block, Because the negotiating phase is omitted.

			// Characteristic is wrong
			// Error....
//			logger.debug("OPEN Fail! send PCErr...");

			// OpenRetry Counter checking
			// Open Retrying is done by 2 times.
			// After 2 times failure, it disconnect and release the resources
			// for this PCEP peer.
			if (openRetry++ > 2) {
				try {
					// Close
					getSessionAccessor().closeSession();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// Send error
				getSessionAccessor().throwError(1, 3);
				// getSessionAccessor().throwError(1, 4);

				// Setting new characteristic

				// Retry
				try {
					sendOpen();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} else {
			// All Session characteristic OK
//			logger.debug("Negotiated Ch: KA = " + keepalivetime / 1000
//					+ ", DT = " + deadtime / 1000);
			// Open!!
			toggleRemoteOK();
			// Invoke to send KEEPALIVE message
//			logger.debug("OPEN OK. Send Keepalive...");
			try {
				sendKeepAlive();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// moves to Keepwait state
			getSessionAccessor().setState(PCEPSession.STATE_KEEPWAIT);
		}
	}

	/**
	 * KEEPALIVE 메시지를 받은 후, SessionUp 상태로 이동하고 KEEPALIVE 대기 시계를 초기화한다.
	 */
	public void receiveKeepAlive() {
		// Ok, moves to SessionUp state.
		setLocalOK(true);
		getSessionAccessor().setState(PCEPSession.STATE_SESSIONUP);
		// Initiate KeepWaiter(Now for DeadTimer)
		deadTimer = System.currentTimeMillis();
		keepwaiter = deadTimer;
	}

	/**
	 * KEEPALIVE 메시지를 발송하는 함수.
	 * 
	 * @throws IOException
	 */
	protected void sendKeepAlive() throws IOException {
		sendMessage(new PCEPMessage(PCEPCommonHeader.createPCEPCommonHeader(
				PacketChain.KEEPALIVE_PT, 0)));
		/*logger.debug("Session " + getSessionAccessor().getSID()
				+ " : KEEPALIVE sending to " + getServer().getAddress());*/
	}

	/**
	 * OPEN 메시지를 발송하는 함수.
	 * 
	 * @throws IOException
	 */
	public void sendOpen() throws IOException {
		// Create Open Object with preset characteristics.
		OpenObject openObj = new OpenObject(PacketChain.OPEN_PT,
				PCEPElement.VERSION, (byte) 0, (byte) getSessionAccessor()
						.getKeepalive(), (byte) getSessionAccessor()
						.getDeadtimer(), getSessionAccessor().getSID());
		sendMessage(new PCEPMessage(PCEPCommonHeader.createPCEPCommonHeader(
				PacketChain.OPEN_PT, openObj.getLength()), openObj));
		// Initiate KeepWait Timer
		keepwaiter = System.currentTimeMillis();
	}

	/**
	 * 연결 종료(CLOSE) 메시지를 보내는 함수.
	 * 
	 * @param value
	 *            종료값.
	 * @throws IOException
	 */
	public void sendClose(int value) throws IOException {

		CloseObject closeObj = new CloseObject(PacketChain.CLOSE_PT, (byte) 0,
				(byte) value);
		connectionThread.sendMessage(new PCEPMessage(PCEPCommonHeader
				.createPCEPCommonHeader(PacketChain.CLOSE_PT, closeObj
						.getLength()), closeObj));

	}

	/**
	 * 주어진 메시지를 메시지 큐 안에 넣습니다.
	 * 
	 * @param outwardMessage
	 *            전송할 PCEP메시지입니다.
	 * @throws IOException
	 */
	protected void sendMessage(PCEPMessage outwardMessage) throws IOException {
		senderQueue.add(outwardMessage);
	}

	/**
	 * 연결을 관리하는 쓰레드입니다.
	 * 
	 * @author Ancom
	 */
	private class ConnectionThread extends Thread {

		private static final int HEADER_LENGTH = 4;

		private Socket socket = null;
		private BufferedInputStream dataInput = null;
		private BufferedOutputStream dataOutput = null;
		/**
		 * 연결이 활성화되었는지 가리키는 플래그.
		 * 
		 * @uml.property name="active"
		 */
		private boolean active = true;

		/**
		 * 연결 쓰레드 생성을 위해서는 KEEPALIVE 대기 시간, DEADTIME, SYNCTIME이 필요합니다.
		 * 
		 * @param ka
		 *            KEEPALIVE 대기 시간. 단위는 초(sec.)
		 * @param dt
		 *            DEADTIME 대기 시간. 단위는 초(sec.)
		 * @param st
		 *            SYNCTIME 대기 시간. 단위는 초(sec.)
		 */
		public ConnectionThread(int ka, int dt, int st) {
			keepalivetime = ka * 1000;
			deadtime = dt * 1000;
			synctime = st * 1000;

		}

		@Override
		public void run() {
			try {
				long t1 = System.currentTimeMillis();
				long delayed_time = 10;
				keepAliveTimer = t1;
				deadTimer = t1;
				syncTimer = t1;
				sleepTimer = t1;
				keepwaiter = t1;
				int sleepTime = 10; // ms

				// Connection Loop
				while (active) {
					t1 = System.currentTimeMillis();

					//if (t1 >= sleepTimer + sleepTime)
					//	sleepTimer = t1;

					// Handling Input Messages
					handleInput();
					// Handling Output Messages
					if (!senderQueue.isEmpty()) {
						handleOutput();
					}

					// After OPEN accepted...
					if (remoteOK) {

						// KeepWait Checker.
						// If KeepWait is expired when KeepAlive mechanism is
						// used,
						// It sends error message and close the connection.
						if (useKA && t1 >= keepwait + keepwaiter + delayed_time) {

							keepwaiter = System.currentTimeMillis();
							if (getSessionAccessor().getState() == PCEPSession.STATE_KEEPWAIT) {// Error!
//								logger.debug("keepwait expired...");
								active = false;
								// disconnect
								getSessionAccessor().throwError(1, 7);
								getSessionAccessor().closeSession();

								continue;
							}
						}

						// KeepAlive Mechanism
						// If KeepAlive timer is expired,
						// it sends a keepalive message.
						if (useKA && t1 >= keepAliveTimer + keepalivetime) {
							keepAliveTimer = t1;
							sendKeepAlive();
						}
						// DeadTimer
						// If no keepalive or other message arrive until
						// deadtimer is expired,
						// and also keepalive mechanism is used,
						// it close the connection.
						if (useKA && t1 >= deadTimer + deadtime) {
//							logger.debug("DeadTimer expired... ");
							deadTimer = t1;
							// Let's die..
//							logger.debug("Session dead.");
							active = false;
							disconnect(true, 2);
						}

						// SyncTime
						// If SyncTimer is expired,
						// it initiate the synctimer and do synchronization.
						if (t1 >= syncTimer + synctime) {
							syncTimer = t1;
							// TODO: Synchronization
						}
					}
					// Before OPEN message arrives
					else {
						// In OpenWait state,
						// if openwait timer is expired but no OPEN message has
						// arrived,
						// then it sends error message and close connection.
						if (getSessionAccessor().getState() == PCEPSession.STATE_OPENWAIT
								&& t1 >= openwait + keepwaiter) {
							// Error!
////							logger.debug("opendead. t :" + t1 + " timer :"
//									+ (openwait + keepwaiter) + " " + openwait
//									+ " " + keepwaiter + " "
//									+ (t1 >= openwait + keepwaiter));
							getSessionAccessor().throwError(1, 2);
							// disconnect
							getSessionAccessor().closeSession();
						}
					}
					Thread.sleep(sleepTime);

				}
				// If the process exits the connection loop,
				// it means that the connection should be closed.
				// Release resources...
				dataInput = null;
				dataOutput = null;
				socket.close();
			} catch (Exception e) {
				try {
					active = false;
					notifyConnectionError(e);
				} catch (Exception ex2) {
//					logger.warn("Unable to notify listeners", e);
				}
			}
		}

		/**
		 * 들어온 패킷에 대한 처리를 하는 함수.
		 * 
		 * @throws Exception
		 */
		private void handleInput() throws Exception {
			byte[] headerData = new byte[HEADER_LENGTH];

			if (dataInput.available() > 0) {
				dataInput.read(headerData);
				// Decode the packet
				decodePacket(new PCEPCommonHeader(headerData));

				refreshDeadTimer();
			}
			
			headerData= null;
		}
		
		private void refreshDeadTimer()
		{
			// DeadTimer initiated
			deadTimer = System.currentTimeMillis();
		}

		/**
		 * 전송 메시지 큐가 빌 때까지 메시지를 빼내어 보내는 함수.
		 * 
		 * @throws IOException
		 */
		private void handleOutput() throws IOException {
			while (!senderQueue.isEmpty() && !socket.isClosed()
					&& dataOutput != null) {
				PCEPMessage outwardMessage = senderQueue.poll();
				sendMessage(outwardMessage);
			}
		}

		/**
		 * RemoteOK 상태를 바꾸는 함수.
		 */
		private void toggleRemoteOK() {
			if (remoteOK)
				remoteOK = false;
			else
				remoteOK = true;
		}

		/**
		 * 주어진 서버 주소와 포트에 연결을 시도하는 함수. PCC가 사용합니다.
		 * 
		 * @param host
		 *            서버의 주소.
		 * @param port
		 *            서버의 포트.
		 * @throws IOException
		 */
		private boolean openConnection(String host, int port) {
			// add runtime checking for port
			socket = new Socket();
			try {
				SocketAddress socketAddress = new InetSocketAddress(InetAddress
						.getByName(host), port);
				int socketTimeoutInMiliseconds = connectTime;
				// session.getConfiguration().getSocketTimeoutInMiliseconds();
				// socket.connect(socketAddress, socketTimeoutInMiliseconds);

				socket.connect(socketAddress, socketTimeoutInMiliseconds);
				socket.setKeepAlive(true);

				dataInput = new BufferedInputStream(socket.getInputStream());
				dataOutput = new BufferedOutputStream(socket.getOutputStream());
				start();
//				logger.debug("Connected! " + socketAddress);
				getSessionAccessor().setState(PCEPSession.STATE_OPENWAIT);
				return true;
			} catch (UnknownHostException e) {
//				logger.debug("Connection Failed - Unkown Host ");
				return false;
			} catch (SocketTimeoutException e) {
//				logger.debug("Connection Timed out... ");
				return false;
			} catch (SocketException e) {
//				logger.debug("Connection Failed - Socket Error ");
				return false;
			} catch (IOException e) {
//				logger.debug("Connection Failed - IO Error ");
				return false;
			}

		}

		/**
		 * 들어온 TCP 연결에 대해 새로운 PCEP 연결을 하는 함수. PCE가 사용.
		 * 
		 * @param socket
		 *            서버 쓰레드 등에서 TCP 연결이 수립된 채 넘어오는 소켓 객체.
		 * @throws IOException
		 */
		private boolean openConnection(Socket socket) {
			int socketTimeoutInMiliseconds = connectTime;
			// session.getConfiguration().getSocketTimeoutInMiliseconds();
			this.socket = socket;
			try {
				this.socket.setKeepAlive(true);
				this.socket.setSoTimeout(socketTimeoutInMiliseconds);

				dataInput = new BufferedInputStream(socket.getInputStream());
				dataOutput = new BufferedOutputStream(socket.getOutputStream());

				start();

//				logger.debug("Connection Accepted!");
				getSessionAccessor().setState(PCEPSession.STATE_OPENWAIT);
				return true;
			} catch (UnknownHostException e) {
//				logger.debug("Connection Failed - Unkown Host ");
				return false;
			} catch (SocketTimeoutException e) {
//				logger.debug("Connection Timed out... ");
				return false;
			} catch (SocketException e) {
//				logger.debug("Connection Failed - Socket Error ");
				return false;
			} catch (IOException e) {
//				logger.debug("Connection Failed - IO Error ");
				return false;
			}

		}

		/**
		 * 연결을 끊는 함수. willSendMessage 플래그 따라 연결 종료 메시지를 전송.
		 * 
		 * @param willSendMessage
		 *            연결 종료 메시지를 보낼 지 결정하는 플래그. 참이면 연결 종료 메시지를 전송.(먼저 연결을 종료하는
		 *            경우)
		 * @throws IOException
		 */
		private void closeConnection(boolean willSendMessage, int value)
				throws IOException {
//			logger.debug("Closing connection...");
			if (willSendMessage)
				sendClose(value);
			remoteOK = false;
			active = false;
			// Moves to Idle state
//			logger.debug("Session closed...");
			getSessionAccessor().setState(PCEPSession.STATE_IDLE);

		}

		/**
		 * 메시지를 TCP 통신으로 전송하는 함수.
		 * 
		 * @param outwardMessage
		 *            전송할 PCEP메시지.
		 * @throws IOException
		 */
		private synchronized void sendMessage(PCEPMessage outwardMessage) {

			// Write Message Header
			try {
				dataOutput.write(outwardMessage.getHeaderContents());
				if (outwardMessage.getLength() > 0) {
					// dataOutput.write(outwardMessage.getContents());

					Iterator<PCEPObjectAbstract> i = outwardMessage
							.getObjectIterator();
					while (i.hasNext()) {
						PCEPObjectAbstract obj = i.next();
						byte[] s = obj.getBinaryContents();
						if (s != null)
							dataOutput.write(s);

					}

				}
				dataOutput.flush();
				keepAliveTimer = System.currentTimeMillis();
			} catch (IOException e) {
//				logger.debug("Cannot send packets... May be a socket error. ");
				// e.printStackTrace();
			}

		}

		/**
		 * 메시지의 헤더에 따라 메시지를 해독하는 함수.
		 * 
		 * @param header
		 *            들어온 메시지의 공통헤더. 메시지의 종류, 길이 등의 정보를 담고 있다.
		 * @throws Exception
		 */
		private void decodePacket(PCEPCommonHeader header) throws Exception {
			byte[] keyBytes = new byte[header.getPacketLength()];
			dataInput.read(keyBytes);
			PacketContext context = new PacketContext(session, header, keyBytes);
			packetChain.sendToChain(context);
		}

	}
}

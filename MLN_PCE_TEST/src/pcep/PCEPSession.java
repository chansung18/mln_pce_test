package pcep;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

//import org.apache.log4j.Logger;

import pcep.element.Server;
import pcep.manager.PacketChain;
import pcep.packet.ErrorCmpObj;
import pcep.packet.NotifyCmpObj;
import pcep.packet.PCEPCommonHeader;
import pcep.packet.PCEPErrorObject;
import pcep.packet.PCEPMessage;
import pcep.packet.PCEPObject;
import pcep.packet.RequestCmpObj;
import pcep.packet.ResponseCmpObj;
import pcep.packet.SVECObject;

/**
 * PCEP 세션. PCEP 객체는 여러 개의 세션을 가질 수 있다. 각 세션 당 하나의 연결을 갖는다.
 * 
 * @author Ancom
 */

public class PCEPSession {
//	private final static Logger logger = Logger.getLogger(PCEPSession.class);

	public static final byte STATE_IDLE = 0;
	public static final byte STATE_TCPPENDING = 1;
	public static final byte STATE_OPENWAIT = 2;
	public static final byte STATE_KEEPWAIT = 3;
	public static final byte STATE_SESSIONUP = 4;

	/**
	 * 세션의 연결을 관리하는 객체.
	 * 
	 * @uml.property name="connection"
	 * @uml.associationEnd
	 */
	private PCEPConnection connection;

	/**
	 * 다른 객체에서 세션에 접근을 할 때 사용하는 객체.
	 * 
	 * @uml.property name="sessionAccessor"
	 * @uml.associationEnd
	 */
	private SessionAccessor sessionAccessor = null;

	/**
	 * PCE 객체.
	 * 
	 * @uml.property name="pce"
	 * @uml.associationEnd
	 */
	private PCEPElement pce = null;

	/**
	 * 세션 생성 FSM 상태.
	 */
	private byte state = 0;

	/**
	 * 세션 ID.
	 */
	private byte sid = 0;

	// Variables
	/**
	 * KEEPALIVE 메시지 대기 시간.
	 * 
	 * @uml.property name="keepalive"
	 */
	private int keepalive = 30;
	/**
	 * DEADTIMER.
	 * 
	 * @uml.property name="deadtimer"
	 */
	private int deadtimer = 120;
	/**
	 * SYNCTIMER
	 * 
	 * @uml.property name="synctimer"
	 */
	private int synctimer = 60;

	private int unknownRequestCounter = 0;
	private int unknownMessageCounter = 0;
	/**
	 * 최대 미확인 요청 허용 수. 기본값은 5. 이 회수를 넘어서 미확인 요청이 들어올 경우 세션은 종료됩니다.
	 * 
	 * @uml.property name="mAX_UNKNOWN_REQUESTS"
	 */
	private int MAX_UNKNOWN_REQUESTS = 5;
	/**
	 * 최대 미확인 메시지 허용 수. 기본값은 5. 이 회수를 넘어서 미확인 메시지가 들어올 경우 세션은 종료됩니다.
	 * 
	 * @uml.property name="mAX_UNKNOWN_MESSAGES"
	 */
	private int MAX_UNKNOWN_MESSAGES = 5;

	// Interface to PCE - Send
	/**
	 * PCEP객체로부터 전달되어 세션을 통해 나가는 Request 객체들의 큐.
	 */
	@Deprecated
	private Vector<RequestCmpObj> send_ReqList;

	/**
	 * PCEP객체로부터 전달되어 세션을 통해 나가는 Response 객체들의 큐.
	 */
	@Deprecated
	private Vector<ResponseCmpObj> send_RepList;
	/**
	 * PCEP객체로부터 전달되어 세션을 통해 나가는 Notify 객체들의 큐.
	 */
	@Deprecated
	private Vector<NotifyCmpObj> send_NtfList;
	/**
	 * PCEP객체로부터 전달되어 세션을 통해 나가는 Error 객체들의 큐.
	 */
	@Deprecated
	private Vector<ErrorCmpObj> send_ErrList;

	// Interface to PCE - Recv
	protected Vector<PCEPObject> recv_ObjQueue;

	/**
	 * 상대로부터 전송되어와 세션을 통해 PCEP객체에 들어가는 Request 객체들의 큐.
	 */
	private Vector<RequestCmpObj> recv_ReqList;
	/**
	 * 상대로부터 전송되어와 세션을 통해 PCEP객체에 들어가는 Response 객체들의 큐.
	 */
	private Vector<ResponseCmpObj> recv_RepList;
	/**
	 * 상대로부터 전송되어와 세션을 통해 PCEP객체에 들어가는 Notify 객체들의 큐.
	 */
	private Vector<NotifyCmpObj> recv_NtfList;
	/**
	 * 상대로부터 전송되어와 세션을 통해 PCEP객체에 들어가는 Error 객체들의 큐.
	 */
	private Vector<ErrorCmpObj> recv_ErrList;

	/**
	 * SVEC를 처리하는 큐.
	 */
	@Deprecated
	private Vector<SVECObject> svecList;

	// Constructor
	/**
	 * PCEP 세션들을 관리하는 PCEP객체를 가지고 PCEP세션을 만든다.
	 * 
	 * @param pce
	 *            PCEPElement 객체.
	 */
	public PCEPSession(PCEPElement pce) {
		init(pce, null);
	}

	/**
	 * PCEP 세션들을 관리하는 PCEP객체와 접속할 서버 정보를 가지고 PCEP 세션을 초기화. PCC로써 연결할 경우 사용된다.
	 * 
	 * @param pce
	 *            PCEPElement 객체.
	 * @param server
	 *            접속할 서버 정보 객체.
	 */
	public PCEPSession(PCEPElement pce, Server server) {
		init(pce, server);

	}

	/**
	 * PCEP 세션을 초기화하는 함수. 각종 큐들과 연결 정보를 초기화한다.
	 * 
	 * @param pce
	 *            PCEP 객체.
	 * @param server
	 *            접속할 서버 정보.
	 */
	public void init(PCEPElement pce, Server server) {
		this.pce = pce;
		sessionAccessor = new SessionAccessor();
		connection = new PCEPConnection(this);
		connection.setServer(server);

		recv_ObjQueue = new Vector<PCEPObject>();

		send_ReqList = new Vector<RequestCmpObj>();
		send_RepList = new Vector<ResponseCmpObj>();
		send_NtfList = new Vector<NotifyCmpObj>();
		send_ErrList = new Vector<ErrorCmpObj>();

		recv_ReqList = new Vector<RequestCmpObj>();
		recv_RepList = new Vector<ResponseCmpObj>();
		recv_NtfList = new Vector<NotifyCmpObj>();
		recv_ErrList = new Vector<ErrorCmpObj>();

		svecList = new Vector<SVECObject>();
	}

	public void connect() throws Exception {
		connection.connectToServer();
	}

	/**
	 * 세션을 닫는 함수.
	 */
	public void close() {
		try {
			connection.disconnect(true, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		connection = null;
	}

	// getter and setters
	public PCEPConnection getPCEPConnection() {
		return connection;
	}

	/**
	 * 세션 접속자를 얻어내는 함수. PCEP세션 객체에 접근하기 위해서 세션 접속자를 사용합니다.
	 * 
	 * @return
	 * @uml.property name="sessionAccessor"
	 */
	public SessionAccessor getSessionAccessor() {
		return sessionAccessor;
	}

	/**
	 * 연결하고 있는 서버의 정보를 얻어오는 함수. PCC의 경우, 접속하고 있는 PCE(서버)의 정보를 얻어옵니다.
	 * 
	 * @return
	 */
	public Server getServer() {
		return connection.getServer();
	}

	/**
	 * 세션 ID를 설정해주는 함수.
	 * 
	 * @param sid
	 */
	public void setSID(byte sid) {
		this.sid = sid;

	}

	/**
	 * @return
	 * @uml.property name="keepalive"
	 */
	public int getKeepalive() {
		return keepalive;
	}

	/**
	 * @param keepalive
	 * @uml.property name="keepalive"
	 */
	public void setKeepalive(int keepalive) {
		this.keepalive = keepalive;
	}

	/**
	 * @return
	 * @uml.property name="deadtimer"
	 */
	public int getDeadtimer() {
		return deadtimer;
	}

	/**
	 * @param deadtimer
	 * @uml.property name="deadtimer"
	 */
	/**
	 * @param deadtimer
	 */
	public void setDeadtimer(int deadtimer) {
		this.deadtimer = deadtimer;
	}

	/**
	 * @return
	 * @uml.property name="synctimer"
	 */
	public int getSynctimer() {
		return synctimer;
	}

	/**
	 * @param synctimer
	 * @uml.property name="synctimer"
	 */
	public void setSynctimer(int synctimer) {
		this.synctimer = synctimer;
	}

	/**
	 * @return
	 * @uml.property name="mAX_UNKNOWN_REQUESTS"
	 */
	public int getMAX_UNKNOWN_REQUESTS() {
		return MAX_UNKNOWN_REQUESTS;
	}

	/**
	 * 
	 * 세션 생성 FSM 상태를 반환한다.
	 * 
	 * @return 현재 세션의 상태.
	 */
	public byte getState() {
		return state;
	}

	/**
	 * @param max_unknown_requests
	 * @uml.property name="mAX_UNKNOWN_REQUESTS"
	 */
	public void setMAX_UNKNOWN_REQUESTS(int max_unknown_requests) {
		MAX_UNKNOWN_REQUESTS = max_unknown_requests;
	}

	/**
	 * @return
	 * @uml.property name="mAX_UNKNOWN_MESSAGES"
	 */
	public int getMAX_UNKNOWN_MESSAGES() {
		return MAX_UNKNOWN_MESSAGES;
	}

	/**
	 * @param max_unknown_messages
	 * @uml.property name="mAX_UNKNOWN_MESSAGES"
	 */
	public void setMAX_UNKNOWN_MESSAGES(int max_unknown_messages) {
		MAX_UNKNOWN_MESSAGES = max_unknown_messages;
	}

	public void establishedConnection(Socket socket) throws Exception {

		connection.connect(socket);
	}

	public byte getSid() {
		return sid;
	}

	public void setSid(byte sid) {
		this.sid = sid;
	}

	public PCEPSession getThisSession() {
		return this;
	}

	/**
	 * 세션 접속자 객체.
	 * 
	 * @author Ancom
	 * 
	 */
	public class SessionAccessor {

		/**
		 * 주어진 PCEP메시지를 전송하는 함수.
		 * 
		 * @param outwardMessage
		 *            전송할 PCEP메시지.
		 * @throws IOException
		 */
		public void sendMessage(PCEPMessage outwardMessage) throws IOException {
			connection.sendMessage(outwardMessage);
		}

		/**
		 * 세션을 종료시키는 함수
		 */
		public void closeSession() {
			close();
		}

		/**
		 * 연결을 종료하는 함수.
		 * 
		 * @throws Exception
		 */
		public void disconnect() throws Exception {
			connection.disconnect(true, 1);
		}

		/**
		 * 상대방이 먼저 연결을 종료할 경우, 이 세션의 연결을 종료하는 함수.
		 * 
		 * @throws Exception
		 */
		public void disconnectRemote() throws Exception {
			connection.disconnect(false, 0);
		}

		/**
		 * 세션 ID를 가져오는 함수.
		 * 
		 * @return 이 세션의 ID
		 */
		public byte getSID() {
			return sid;
		}

		/**
		 * KEEPALIVE 대기 시간을 가져오는 함수.
		 * 
		 * @return 현재 설정된 KeepAlive의 값
		 */
		public int getKeepalive() {
			return keepalive;
		}

		/**
		 * KEEPALIVE 대기 시간을 설정하는 함수.
		 * 
		 * @param ka
		 *            KEEPALIVE 대기 시간. 단위는 초(sec.)
		 */
		public void setKeepalive(int ka) {
			keepalive = ka;
		}

		/**
		 * DEADTIMER를 반환하는 함수.
		 * 
		 * @return 현재 설정된 DeadTimer의 값
		 */
		public int getDeadtimer() {
			return deadtimer;
		}

		/**
		 * DEADTIMER를 설정하는 함수.
		 * 
		 * @param dt
		 *            DEADTIMER. 단위는 초(sec.)
		 */
		public void setDeadtimer(int dt) {
			deadtimer = dt;
		}

		/**
		 * SyncTimer를 반환하는 함수.
		 * 
		 * @return 현재 설정된 SyncTimer의 값
		 */
		public int getSynctimer() {
			return synctimer;
		}

		/**
		 * SyncTimer를 설정하는 함수.
		 * 
		 * @param st
		 *            SyncTimer. 단위는 초(sec.)
		 */
		public void setSynctimer(int st) {
			synctimer = st;
		}

		/**
		 * 확인되지 않은 요청의 최다 허용수를 반환합니다.
		 * 
		 * @return 설정된 MAX_UNKOWN_REQUESTS의 값
		 */
		public int getMAX_UNKNOWN_REQUESTS() {
			return MAX_UNKNOWN_REQUESTS;
		}

		/**
		 * 확인되지 않은 요청의 최다 허용수를 설정합니다.
		 * 
		 * @param max_unknown_requests
		 *            설정할 MAX_UNKOWN_REQUESTS의 값
		 */
		public void setMAX_UNKNOWN_REQUESTS(int max_unknown_requests) {
			MAX_UNKNOWN_REQUESTS = max_unknown_requests;
		}

		/**
		 * 확인되지 않은 메시지의 최다 허용수를 반환합니다.
		 * 
		 * @return 설정된 MAX_UNKOWN_MESSAGES의 값
		 */
		public int getMAX_UNKNOWN_MESSAGES() {
			return MAX_UNKNOWN_MESSAGES;
		}

		/**
		 * 확인되지않은 메시지의 최다 허용수를 설정합니다.
		 * 
		 * @param max_unknown_messages
		 *            설정할 MAX_UNKOWN_MESSAGES의 값
		 */
		public void setMAX_UNKNOWN_MESSAGES(int max_unknown_messages) {
			MAX_UNKNOWN_MESSAGES = max_unknown_messages;
		}

		/**
		 * 연결 객체를 반환합니다.
		 * 
		 * @return 현재의 PCEPConnection 객체
		 */
		public PCEPConnection getConnection() {
			return connection;
		}

		/**
		 * RemoteOK의 상태를 전환하는 함수.
		 */
		public void toggleRemoteOK() {
			connection.toggleRemoteOK();
		}

		/**
		 * 세션 생성 FSM의 상태를 반환한다.
		 * 
		 * @return 현재 세션의 연결 상태
		 */
		public byte getState() {
			return state;
		}

		/**
		 * 세션의 상태를 주어진 상태값으로 바꾼다.
		 * 
		 * @param s
		 *            상태값
		 */
		protected void setState(byte s) {
			// 세션의 상태를 주어진 상태값으로 바꾼다.
			byte prev_state = state;
			state = s;
			// 세션의 상태가 SESSIONUP 상태로 바뀌었을 때,
			// 이를 PCEP 객체에게 알린다.
			if (prev_state != state && state == PCEPSession.STATE_SESSIONUP)
				try {
					pce.notifySessionUp(sid);
				} catch (Exception e) {
					e.printStackTrace();
				}
			// 세션의 상태가 IDLE 상태로 바뀌었을 때,
			// 이를 PCEP 객체에게 알린다.
			else if (prev_state != state && state == PCEPSession.STATE_IDLE)
				try {
					pce.notifySessionDown(getThisSession());
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		/**
		 * Request 송신 큐에 새 Request 객체를 적재하는 함수.
		 * 
		 * @param request
		 *            Request 정보 객체입니다.
		 */
		@Deprecated
		public void addRequestForSend(RequestCmpObj request) {
			send_ReqList.add(request);
		}

		/**
		 * Response 송신 큐에 새 Response 객체를 적재하는 함수.
		 * 
		 * @param response
		 *            Response 정보 객체입니다.
		 */
		@Deprecated
		public void addResponseForSend(ResponseCmpObj response) {
			send_RepList.add(response);
		}

		/**
		 * Notify 송신 큐에 새 Notify 객체를 적재하는 함수.
		 * 
		 * @param notify
		 *            Notify 정보 객체입니다.
		 */
		@Deprecated
		public void addNotifyForSend(NotifyCmpObj notify) {
			send_NtfList.add(notify);
		}

		/**
		 * Error 송신 큐에 새 Error 객체를 적재하는 함수.
		 * 
		 * @param error
		 *            Error 정보 객체입니다.
		 */
		@Deprecated
		public void addErrorForSend(ErrorCmpObj error) {
			send_ErrList.add(error);
		}

		/**
		 * Request 수신 큐에 새 Request 객체를 적재하는 함수.
		 * 
		 * @param request
		 *            Request 정보 객체입니다.
		 */
		public void addRequestForRecv(RequestCmpObj request) {
			recv_ReqList.add(request);
		}

		/**
		 * Response 수신 큐에 새 Response 객체를 적재하는 함수.
		 * 
		 * @param response
		 *            Response 정보 객체입니다.
		 */
		public void addResponseForRecv(ResponseCmpObj response) {
			recv_RepList.add(response);
		}

		/**
		 * Notify 수신 큐에 새 Notify 객체를 적재하는 함수.
		 * 
		 * @param notify
		 *            Notify 정보 객체입니다.
		 */
		public void addNotifyForRecv(NotifyCmpObj notify) {
			recv_NtfList.add(notify);
		}

		/**
		 * Error 수신 큐에 새 Error 객체를 적재하는 함수.
		 * 
		 * @param error
		 *            ERROR 정보 객체입니다.
		 */
		public void addErrorForRecv(ErrorCmpObj error) {
			recv_ErrList.add(error);
		}

		/**
		 * SVEC 큐에 새 SVEC 객체를 적재하는 함수.
		 * 
		 * @param svec
		 *            SVEC 객체입니다.
		 */
		public void addSVEC(SVECObject svec) {
			svecList.add(svec);
		}

		// toward PCE
		/**
		 * PCEP 객체로 들어가는 메시지의 시작을 알리는 함수.
		 * 
		 * @param sid
		 *            해당 세션ID입니다.
		 * @param messageType
		 *            들어가는 메시지의 메시지 종류입니다.
		 */
		@Deprecated
		public void reportMessageStart(int sid, byte messageType) {

		}

		/**
		 * PCEP 객체로 들어가는 메시지의 끝을 알리는 함수.
		 * 
		 * @param sid
		 *            해당 세션ID입니다.
		 * @param messageType
		 *            들어가는 메시지의 메시지 종류입니다.
		 * @throws Exception
		 */
		public void reportMessageEnd(int sid, byte messageType)
				throws Exception {
			pce.processInwardEvent(sid, messageType);
		}

		/**
		 * 수신 메시지 큐로부터 PCEP 요청 복합 객체를 뽑아 반환한다.
		 * 
		 * @return PCEP 요청 복합 객체
		 */
		public RequestCmpObj dequeueRecvRequestCmpObj() {

			if (!recv_ReqList.isEmpty()) {
				return recv_ReqList.remove(0);
			} else {
				return null;
			}
		}

		/**
		 * 수신 메시지 큐로부터 PCEP 답변 복합 객체를 뽑아 반환한다.
		 * 
		 * @return PCEP 답변 복합 객체
		 */
		public ResponseCmpObj dequeueRecvResponseCmpObj() {
			if (!recv_RepList.isEmpty()) {
				return recv_RepList.remove(0);
			} else {
				return null;
			}
		}

		/**
		 * 수신 메시지 큐로부터 PCEP 알림 복합 객체를 뽑아 반환한다.
		 * 
		 * @return PCEP 요청 복합 객체
		 */
		public NotifyCmpObj dequeueRecvNotifyCmpObj() {
			if (!recv_NtfList.isEmpty()) {
				return recv_NtfList.remove(0);
			} else {
				return null;
			}
		}

		/**
		 * 수신 메시지 큐로부터 PCEP 오류 복합 객체를 뽑아 반환한다.
		 * 
		 * @return PCEP 오류 복합 객체
		 */
		public ErrorCmpObj dequeueRecvErrorCmpObj() {
			if (!recv_ErrList.isEmpty()) {
				return recv_ErrList.remove(0);
			} else {
				return null;
			}
		}

		@Deprecated
		public void inputNoPath(int sid, byte ni, boolean c, int value) {
			// TODO Auto-generated method stub

		}

		@Deprecated
		public void inputEndPoints(int sid, int srcIPv4, int desIPv4) {
			// TODO Auto-generated method stub

		}

		@Deprecated
		public void inputEndPoints(int sid, byte[] srcIPv6, byte[] desIPv6) {
			// TODO Auto-generated method stub

		}

		@Deprecated
		public void inputBandwidth(int sid, int bandwidth, boolean requested) {
			// TODO Auto-generated method stub

		}

		@Deprecated
		public void inputMetric(int sid, boolean b, boolean c, byte type,
				int metricValue) {
			// TODO Auto-generated method stub

		}

		@Deprecated
		public void inputNotification(int sid, byte notificationType,
				byte notificationValue, int value) {
			// TODO Auto-generated method stub

		}

		@Deprecated
		public void inputPCEPError(int sid, byte errorType, byte errorValue,
				int value) {
			// TODO Auto-generated method stub
		}

		@Deprecated
		public void inputRP(int sid, int requestIDNumber, boolean vspt, boolean o, boolean b,
				boolean r, byte pri) {
			// TODO Auto-generated method stub

		}

		@Deprecated
		public void inputSVEC(int sid, Vector<Integer> requestIDList,
				boolean l, boolean n, boolean s) {
			// TODO Auto-generated method stub

		}

		@Deprecated
		public void inputClose(int sid, byte flags, byte reason) {
			// TODO Auto-generated method stub
		}

		/**
		 * PCEP 객체로 들어가는 메시지의 시작을 알리는 함수.
		 * 
		 * @param messageType
		 *            들어가는 메시지의 메시지 종류입니다.
		 */
		@Deprecated
		public void reportMessageStart(byte messageType) {
			reportMessageStart(sid, messageType);
		}

		/**
		 * PCEP 객체로 들어가는 메시지의 끝을 알리는 함수.
		 * 
		 * @param sid
		 *            해당 세션ID입니다.
		 * @param messageType
		 *            들어가는 메시지의 메시지 종류입니다.
		 * @throws Exception
		 */
		public void reportMessageEnd(byte messageType) throws Exception {
			reportMessageEnd(sid, messageType);
		}

		/**
		 * PCEP 에러를 처리하는 함수.
		 * 
		 * @param errorType
		 *            에러의 종류.
		 * @param errorValue
		 *            에러값.
		 */
		public void throwError(int errorType, int errorValue) {
			throwError(errorType, errorValue, 0);
		}

		/**
		 * PCEP 에러를 처리하는 함수.
		 * 
		 * @param errorType
		 *            에러의 종류.
		 * @param errorValue
		 *            에러값.
		 * @param missedRID
		 *            빠진 RequestID 값.
		 */
		public void throwError(int errorType, int errorValue, int missedRID) {
//			logger.debug("Error : Error Type =" + errorType + ", Value ="
//					+ errorValue + ", Missed RID = " + missedRID);
			// Error 객체 생성
			PCEPErrorObject err = new PCEPErrorObject(PacketChain.PCERR_PT,
					(byte) 0, (byte) errorType, (byte) errorValue);
			// PCErr 메시지 객체 생성
			PCEPMessage pcerr = new PCEPMessage(PCEPCommonHeader
					.createPCEPCommonHeader(PacketChain.PCERR_PT, err
							.getLength()));
			// PCErr 메시지 객체에 Error객체를 담는다.
			pcerr.addPCEPObject(err);

			// 메시지 전송
			try {
//				logger.debug("Error Report Sending...");
				sendMessage(pcerr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void inputNoPath(byte ni, boolean c, int value) {
			inputNoPath(sid, ni, c, value);

		}

		public void inputEndPoints(int srcIPv4, int desIPv4) {
			inputEndPoints(sid, srcIPv4, desIPv4);

		}

		public void inputEndPoints(byte[] srcIPv6, byte[] desIPv6) {
			inputEndPoints(sid, srcIPv6, desIPv6);

		}

		public void inputBandwidth(int bandwidth, boolean requested) {
			inputBandwidth(sid, bandwidth, requested);

		}

		public void inputMetric(boolean b, boolean c, byte type, int metricValue) {
			inputMetric(sid, b, c, type, metricValue);

		}

		public void inputNotification(byte notificationType,
				byte notificationValue, int value) {
			inputNotification(sid, notificationType, notificationValue, value);

		}

		public void inputPCEPError(byte errorType, byte errorValue, int value) {
			inputPCEPError(getSID(), errorType, errorValue, value);

		}

		public void inputRP(int requestIDNumber, boolean vspt, boolean o, boolean b,
				boolean r, byte pri) {
			inputRP(sid, requestIDNumber, vspt, o, b, r, pri);

		}

		public void inputSVEC(Vector<Integer> requestIDList, boolean l,
				boolean n, boolean s) {
			inputSVEC(sid, requestIDList, l, n, s);

		}

		public void inputOpen(byte keepalive, byte deadtimer, byte sid) {
//			logger.debug("Session Charcteristics - KeepAlive : " + keepalive
//					+ " DeadTime : " + deadtimer);
			connection.checkOpen(keepalive, deadtimer, sid);
		}

		public void inputClose(byte flags, byte reason) {
			inputClose(sid, flags, reason);
		}

		public void increaseUnknownRequestCounter() {
//			logger.debug("Unknown Request received...");
			if (++unknownRequestCounter >= MAX_UNKNOWN_REQUESTS)
				try {
					connection.disconnect(true, 4);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		public void increaseUnknownMessageCounter() {
//			logger.debug("Unknown Message received...");
			if (++unknownMessageCounter >= MAX_UNKNOWN_MESSAGES)
				try {
					connection.disconnect(true, 3);
				} catch (Exception e) {
					e.printStackTrace();
				}

		}
	}
}

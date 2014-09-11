package pcep.manager;

import java.util.HashMap;

//import org.apache.log4j.Logger;

import pcep.PCEPConnection;

/**
 * 각종 메시지를 처리하는 패킷 체인.
 * 
 * @author Ancom
 * 
 */
public class PacketChain {

	public static final byte OPEN_PT = 0x0001;
	public static final byte OPEN_OC = 0x0001;
	public static final byte OPEN_OT = 0x0001;
	public static final byte KEEPALIVE_PT = 0x0002;
	public static final byte PCREQ_PT = 0x0003;
	public static final byte PCREP_PT = 0x0004;
	public static final byte PCNTF_PT = 0x0005;
	public static final byte PCERR_PT = 0x0006;
	public static final byte CLOSE_PT = 0x0007;
	public static final byte CLOSE_OC = 0x000F;
	public static final byte CLOSE_OT = 0x0001;
	public static final byte RP_OC = 0x0002;
	public static final byte RP_OT = 0x0001;
	public static final byte NOPATH_OC = 0x0003;
	public static final byte NOPATH_OT = 0x0001;
	public static final byte ENDPOINTS_OC = 0x0004;
	public static final byte ENDPOINTS_IPV4_OT = 0x0001;
	public static final byte ENDPOINTS_IPV6_OT = 0x0002;
	public static final byte BANDWIDTH_OC = 0x0005;
	public static final byte BANDWIDTH_REQUESTED_OT = 0x0001;
	public static final byte BANDWIDTH_EXISTING_OT = 0x0002;
	public static final byte METRIC_OC = 0x0006;
	public static final byte METRIC_OT = 0x0001;
	public static final byte ERO_OC = 0x0007;
	public static final byte ERO_OT = 0x0001;
	public static final byte RRO_OC = 0x0008;
	public static final byte RRO_OT = 0x0001;
	public static final byte LSPA_OC = 0x0009;
	public static final byte LSPA_OT = 0x0001;
	public static final byte IRO_OC = 0x000A;
	public static final byte IRO_OT = 0x0001;
	public static final byte SVEC_OC = 0x000B;
	public static final byte SVEC_OT = 0x0001;
	public static final byte XRO_OC = 0x0011;
	public static final byte XRO_OT = 0x0001;
	public static final byte NOTIFICATION_OC = 0x000C;
	public static final byte NOTIFICATION_OT = 0x0001;
	public static final byte PCEPERROR_OC = 0x000D;
	public static final byte PCEPERROR_OT = 0x0001;
	public static final byte LOADBALANCING_OC = 0x000E;
	public static final byte LOADBALANCING_OT = 0x0001;

//	private final static Logger logger = Logger.getLogger(PacketChain.class);

	/**
	 * 메시지 처리자를 관리하는 객체.
	 */
	private final HashMap<Integer, MessageManager> messageManagers;
	/**
	 * PCEP 연결 객체.
	 * 
	 * @uml.property name="connection"
	 * @uml.associationEnd
	 */
	private PCEPConnection connection = null;

	/**
	 * PCEP연결 객체를 가지고 패킷 체인 객체를 생성합니다.
	 * 
	 * @param connection
	 *            PCEP연결객체.
	 */
	public PacketChain(PCEPConnection connection) {
		this.connection = connection;
		messageManagers = new HashMap<Integer, MessageManager>();
		registerDefaultManagers();
	}

	/**
	 * 메시지 처리자를 등록하는 함수.
	 * 
	 * @param packetType
	 *            해당 메시지타입.
	 * @param messageManager
	 *            등록할 메시지 처리자.
	 */
	public void registerMessageManager(int packetType,
			MessageManager messageManager) {
		if (messageManager == null)
			throw new NullPointerException("packetManager cannot be null");
		messageManagers.put(new Integer(packetType), messageManager);
	}

	/**
	 * 메시지 처리자를 해지하는 함수.
	 * 
	 * @param packetType
	 *            해당 메시지타입.
	 */
	public void unregisterMessageManager(int packetType) {
		messageManagers.remove(new Integer(packetType));
	}

	/**
	 * 메시지 내용을 해당 메시지 처리자로 보내 처리하게 하는 함수.
	 * 
	 * @param packageContent
	 *            처리할 메시지 내용.
	 * @throws Exception
	 */
	public void sendToChain(PacketContext packageContent) throws Exception {
		MessageManager messageManager = messageManagers.get(new Integer(
				packageContent.getHeader().getType()));
		if (messageManager == null) {
//			logger.warn("Unknown package.");
//			logger.warn("PacketHeader: " + packageContent.getHeader());

			return;
		}

		messageManager.handle(packageContent);
	}

	/**
	 * 기본 메시지 처리자들을 등록하는 함수.
	 */
	private void registerDefaultManagers() {
		registerMessageManager(OPEN_PT, new OpenMessageHandler(connection));
		registerMessageManager(KEEPALIVE_PT, new KeepAliveMessageHandler(
				connection));
		registerMessageManager(CLOSE_PT, new CloseMessageHandler(connection));
		registerMessageManager(PCREQ_PT, new PCReqMessageHandler(connection));
		registerMessageManager(PCREP_PT, new PCRepMessageHandler(connection));
		registerMessageManager(PCNTF_PT, new PCNtfMessageHandler(connection));
		registerMessageManager(PCERR_PT, new PCErrMessageHandler(connection));

	}

}

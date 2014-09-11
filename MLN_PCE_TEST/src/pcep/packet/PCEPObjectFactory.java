package pcep.packet;

//import org.apache.log4j.Logger;

import pcep.manager.PacketChain;

/**
 * 정보객체를 생성하는 팩토리 클래스입니다.
 * 
 * @author Ancom
 * 
 */
public class PCEPObjectFactory {

//	private final static Logger logger = Logger
//			.getLogger(PCEPObjectFactory.class);

	/**
	 * 메시지타입과 공통객체헤더, 객체 내용을 가지고 정보객체를 생성합니다.
	 * 
	 * @param messageType
	 *            메시지 타입
	 * @param header
	 *            공통객체헤더
	 * @param content
	 *            바이너리 형태의 객체 내용
	 * @return 생성된 PCEP 정보 객체
	 */
	public static PCEPObject createPCEPObject(byte messageType,
			CommonObjectHeader header, byte[] content) {

		PCEPObject obj = null;

		switch (header.getObjectClass()) {
		case PacketChain.OPEN_OC:
			obj = new OpenObject(messageType, header, content);
			break;
		case PacketChain.RP_OC:
			obj = new RPObject(messageType, header, content);
			break;
		case PacketChain.NOPATH_OC:
			obj = new NoPathObject(messageType, header, content);
			break;
		case PacketChain.ENDPOINTS_OC:
			obj = new EndPointsObject(messageType, header, content);
			break;
		case PacketChain.BANDWIDTH_OC:
			obj = new BandwidthObject(messageType, header, content);
			break;
		case PacketChain.METRIC_OC:
			obj = new MetricObject(messageType, header, content);
			break;
		case PacketChain.ERO_OC:
			obj = new ERObject(messageType, header, content);
			break;
		case PacketChain.RRO_OC:
			obj = new RRObject(messageType, header, content);
			break;
		case PacketChain.LSPA_OC:
			obj = new LSPAObject(messageType, header, content);
			break;
		case PacketChain.IRO_OC:
			obj = new IRObject(messageType, header, content);
			break;
		case PacketChain.XRO_OC:
			obj = new XRObject(messageType, header, content);
			break;
		case PacketChain.SVEC_OC:
			obj = new SVECObject(messageType, header, content);
			break;
		case PacketChain.NOTIFICATION_OC:
			obj = new NotificationObject(messageType, header, content);
			break;
		case PacketChain.PCEPERROR_OC:
			obj = new PCEPErrorObject(messageType, header, content);
			break;
		case PacketChain.LOADBALANCING_OC:
			obj = new LoadBalancingObject(messageType, header, content);
			break;
		case PacketChain.CLOSE_OC:
			obj = new CloseObject(messageType, header, content);
			break;
		default:
//			logger.debug("An error on creating PCEP Object...");
			obj = null;
			break;
		}

		return obj;
	}
}

package pcep.manager;

//import org.apache.log4j.Logger;

import pcep.PCEPConnection;
import pcep.PCEPSession;
import pcep.packet.BandwidthObject;
import pcep.packet.CommonObjectHeader;
import pcep.packet.ComputationUtils;
import pcep.packet.EndPointsObject;
import pcep.packet.IRObject;
import pcep.packet.LSPAObject;
import pcep.packet.LoadBalancingObject;
import pcep.packet.MetricObject;
import pcep.packet.PCEPObject;
import pcep.packet.PCEPObjectFactory;
import pcep.packet.RPObject;
import pcep.packet.RRObject;
import pcep.packet.RequestCmpObj;
import pcep.packet.SVECObject;
import pcep.packet.XRObject;

/**
 * PCReq(Request) 메시지를 처리하는 메시지 처리자.
 * 
 * @author Ancom
 * 
 */
public class PCReqMessageHandler extends MessageManager {

//	private final static Logger logger = Logger
//			.getLogger(PCReqMessageHandler.class);

	public PCReqMessageHandler(PCEPConnection connection) {
		super(connection);
	}

	/**
	 * PCREQ 메시지의 처리 PCREQ는 다음과 같은 포맷을 따른다.
	 * 
	 * <PCReq Message>::= <Common Header> [<svec-list>] <request-list>
	 * 
	 * where:
	 * 
	 * <svec-list>::=<SVEC>[<svec-list>]
	 * <request-list>::=<request>[<request-list>]
	 * 
	 * <request>::= <RP> <END-POINTS> [<LSPA>] [<BANDWIDTH>] [<metric-list>]
	 * [<RRO>[<BANDWIDTH>]] [<IRO>] [<LOAD-BALANCING>]
	 * 
	 * where:
	 * 
	 * <metric-list>::=<METRIC>[<metric-list>]
	 * 
	 * 위의 포맷에 맞지 않는다면 오류를 발생시킨다.
	 * 
	 * 
	 * @see pcep.manager.MessageManager#handle(pcep.manager.PacketContext)
	 */
	@Override
	public void handle(PacketContext context) throws Exception {
		RequestCmpObj request = null;
		RPObject rp = null;
		boolean isEndPointsObjectPassed = true;
		boolean[] isMetricObjectPassed = { false, false };
		boolean passedRP_B = false;
		boolean isThereRROLastTime = true;
		boolean isThereBANDWIDTHLastTime = true;
		boolean isThereRROBANDWIDTHLastTime = true;
		boolean isError = false;

//		if (logger.isDebugEnabled()) {
//			logger.debug("PCREQ received.");
//			logger.debug("PacketHeader: " + context.getHeader());
//			logger.debug("PacketBody: "
//					+ ComputationUtils.prettyBytesToString(context
//							.getMessageContent()));
//		}

		if (connection.getSessionAccessor().getState() == PCEPSession.STATE_OPENWAIT) {
//			logger.debug("Illegal Open Message");
			connection.getSessionAccessor().throwError(1, 1);
			connection.getSessionAccessor().closeSession();
			return;
		}

		isError = untangle(context.getMessageContent());
		connection.getSessionAccessor()
				.reportMessageStart(PacketChain.PCREQ_PT);

		for (int i = 0; i < contentVector.size() && !isError; i += 2) {

			CommonObjectHeader coh = new CommonObjectHeader(contentVector
					.elementAt(i));
			PCEPObject obj = PCEPObjectFactory.createPCEPObject(
					PacketChain.PCREQ_PT, coh, contentVector.elementAt(i + 1));
			// Request Handling
			// RP, ERO, LSPA, BANDWIDTH, METRIC, IRO, RRO, LOADBALANCING, SVEC
			// RP handling....
			if (obj.getObjectClass() == PacketChain.RP_OC) {
				// Start of Request
				rp = (RPObject) obj;
				if (request != null)
					connection.getSessionAccessor().addRequestForRecv(request);
				request = new RequestCmpObj(rp);

				if (!(isThereRROLastTime && isThereRROBANDWIDTHLastTime)) {
					isError = true;
					connection.getSessionAccessor().throwError(6, 2);
					break;

				} else if (!isEndPointsObjectPassed) {
					isError = true;
					connection.getSessionAccessor().throwError(6, 3);
					break;
				}

				isMetricObjectPassed[(passedRP_B = rp.isB()) ? 1 : 0] = false;
				isEndPointsObjectPassed = false;
				isThereRROLastTime = false;
				isThereBANDWIDTHLastTime = false;
				isThereRROBANDWIDTHLastTime = true;

			} else if (request == null) {
				isError = true;
				connection.getSessionAccessor().throwError(6, 1);
				break;
			} else if (obj.getObjectClass() == PacketChain.ENDPOINTS_OC) {
				if (isEndPointsObjectPassed)
					continue;
				else {
					EndPointsObject epObj = (EndPointsObject) obj;
					request.setEpObj(epObj);
					isEndPointsObjectPassed = true;
				}
			} else if (obj.getObjectClass() == PacketChain.METRIC_OC) {
				if (isMetricObjectPassed[passedRP_B ? 1 : 0])
					continue;
				else {
					MetricObject metric = (MetricObject) obj;
					request.addMetric(metric);
					isMetricObjectPassed[passedRP_B ? 1 : 0] = true;
				}
			} else if (obj.getObjectClass() == PacketChain.SVEC_OC) {
				// Quo vadis domine?
				SVECObject svec = (SVECObject) obj;
				connection.getSessionAccessor().addSVEC(svec);
			} else if (obj.getObjectClass() == PacketChain.RRO_OC) {
				// R!!
				if (rp != null & rp.isR()) {
					RRObject rro = (RRObject) obj;
					request.setRRO(rro);
					isThereRROLastTime = true;
				}
			} else if (obj.getObjectClass() == PacketChain.BANDWIDTH_OC) {

				if (!isThereBANDWIDTHLastTime) {
					// It should be a normal Bandwidth...
					BandwidthObject bandwidthObj = (BandwidthObject) obj;
					request.setBandwidthObj(bandwidthObj);

					isThereBANDWIDTHLastTime = true;
				}
				//
				else if (rp != null & rp.isR()
				// && !isRequestedBandwidthNotZero)
				)
					isThereRROBANDWIDTHLastTime = true;
				// isThereBANDWIDTHLastTime = false;
				// TODO: It should also check place after RRO!!

			} else if (obj.getObjectClass() == PacketChain.LSPA_OC) {
				// ....
				LSPAObject lspa = (LSPAObject) obj;
				request.setLspaObj(lspa);

			} else if (obj.getObjectClass() == PacketChain.IRO_OC) {
				IRObject iro = (IRObject) obj;
				request.setIrObj(iro);

			} else if (obj.getObjectClass() == PacketChain.XRO_OC) {
				XRObject xro = (XRObject) obj;
				request.setXrObj(xro);
			} else if (obj.getObjectClass() == PacketChain.LOADBALANCING_OC) {
				// ....
				LoadBalancingObject lbo = (LoadBalancingObject) obj;
				request.setLbObj(lbo);
			}

			else if (rp == null) {
				// Doh! SVEC can come first... but IT can be checked!
				isError = true;
				connection.getSessionAccessor().throwError(6, 1);
				break;
			} else {
				isError = true;
				connection.getSessionAccessor().throwError(4, 1);
				break;
			}
			obj.handle(connection.getSessionAccessor());

		}

		// final request must be processed...
		if (request == null) {
			isError = true;
		}
		if (!isError) {
			// Report to Session
			connection.getSessionAccessor().addRequestForRecv(request);
			connection.getSessionAccessor().reportMessageEnd(
					PacketChain.PCREQ_PT);
		} else {
			connection.getSessionAccessor().increaseUnknownRequestCounter();

		}
//		logger.debug("PCREQ Message Handling Completed.");

	}
}

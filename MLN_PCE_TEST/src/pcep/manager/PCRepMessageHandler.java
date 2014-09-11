package pcep.manager;

//import org.apache.log4j.Logger;

import pcep.PCEPConnection;
import pcep.PCEPSession;
import pcep.packet.AttrCmpObj;
import pcep.packet.BandwidthObject;
import pcep.packet.CommonObjectHeader;
import pcep.packet.ComputationUtils;
import pcep.packet.ERObject;
import pcep.packet.IRObject;
import pcep.packet.LSPAObject;
import pcep.packet.MetricObject;
import pcep.packet.NoPathObject;
import pcep.packet.PCEPObject;
import pcep.packet.PCEPObjectFactory;
import pcep.packet.PathCmpObj;
import pcep.packet.RPObject;
import pcep.packet.ResponseCmpObj;
import pcep.packet.XRObject;

/**
 * PCRep(Response) 메시지를 처리하는 메시지 처리자.
 * 
 * @author Ancom
 * 
 */
public class PCRepMessageHandler extends MessageManager {

//	private final static Logger logger = Logger
//			.getLogger(PCRepMessageHandler.class);

	public PCRepMessageHandler(PCEPConnection connection) {
		super(connection);
	}

	/**
	 * PCREP 메시지의 처리 PCREP 메시지가 다음의 포맷에 맞게 만들어졌는지를 확인한다.
	 * 
	 * <PCRep Message> ::= <Common Header> <response-list>
	 * 
	 * where:
	 * 
	 * <response-list>::=<response>[<response-list>]
	 * 
	 * <response>::=<RP> [<NO-PATH>] [<attribute-list>] [<path-list>]
	 * 
	 * <path-list>::=<path>[<path-list>]
	 * 
	 * <path>::= <ERO><attribute-list>
	 * 
	 * where:
	 * 
	 * <attribute-list>::=[<LSPA>] [<BANDWIDTH>] [<metric-list>] [<IRO>]
	 * 
	 * <metric-list>::=<METRIC>[<metric-list>]
	 * 
	 * 위의 포맷에 맞지 않는 객체나 형식이 온다면, 오류를 발생시킨다.
	 * 
	 * @see pcep.manager.MessageManager#handle(pcep.manager.PacketContext)
	 */
	@Override
	public void handle(PacketContext context) throws Exception {
		ResponseCmpObj response = null;
		PathCmpObj path = null;
		AttrCmpObj attr = null;
		RPObject rp = null;

		boolean[] isMetricObjectPassed = { false, false };
		boolean passedRP_B = false;
		boolean isError = false;

//		if (logger.isDebugEnabled()) {
//			logger.debug("PCREP received.");
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
				.reportMessageStart(PacketChain.PCREP_PT);
		for (int i = 0; i < contentVector.size() && !isError; i += 2) {

			CommonObjectHeader coh = new CommonObjectHeader(contentVector
					.elementAt(i));

			PCEPObject obj = PCEPObjectFactory.createPCEPObject(
					PacketChain.PCREP_PT, coh, contentVector.elementAt(i + 1));

			// Reply Handling
			// RP, ERO, LSPA, BANDWIDTH, METRIC, IRO, NOPATH
			if (obj.getObjectClass() == PacketChain.RP_OC) {
				// Start of a Reply
				rp = (RPObject) obj;

				// update path, attributes
				if (path != null) {
					if (attr != null) {
						path.setAttributes(attr);
					}
					response.addPath(path);
				} else {
					if (attr != null) {
						response.setAttributes(attr);
					}
				}
				if (response != null)
					// report to session
					connection.getSessionAccessor()
							.addResponseForRecv(response);
				path = null;
				attr = null;
				response = new ResponseCmpObj(rp);

				isMetricObjectPassed[(passedRP_B = rp.isB()) ? 1 : 0] = false;

			} else if (response == null) {
				isError = true;
				break;
			} else if (obj.getObjectClass() == PacketChain.ERO_OC) {
				// Start of Path
				ERObject ero = (ERObject) obj;
//				logger.debug("ERO length : "+ ero.getLength());
				// attributes of response should be announced
				if (path != null) {
					if (attr != null) {
						path.setAttributes(attr);
					}
					response.addPath(path);
				} else {
					if (attr != null) {
						response.setAttributes(attr);
					}
				}
				attr = null;
				path = new PathCmpObj(ero);
			} else if (obj.getObjectClass() == PacketChain.NOPATH_OC) {
				NoPathObject npo = (NoPathObject) obj;
				response.setNpObj(npo);
			} else if (obj.getObjectClass() == PacketChain.LSPA_OC) {
				// if there exists Path Object, then it belongs to that
				if (attr == null)
					attr = new AttrCmpObj();
				LSPAObject lspa = (LSPAObject) obj;
				attr.setLspaObj(lspa);
				// response.
			} else if (obj.getObjectClass() == PacketChain.IRO_OC) {
				if (attr == null)
					attr = new AttrCmpObj();
				IRObject iro = (IRObject) obj;
				attr.setIrObj(iro);
				// ....
			} else if (obj.getObjectClass() == PacketChain.XRO_OC) {
				if (attr == null)
					attr = new AttrCmpObj();
				XRObject xro = (XRObject) obj;
				attr.setXrObj(xro);
				// ....
			} else if (obj.getObjectClass() == PacketChain.BANDWIDTH_OC) {
				// ....
				if (attr == null)
					attr = new AttrCmpObj();
				BandwidthObject bandwidth = (BandwidthObject) obj;
				attr.setBandwidthObj(bandwidth);
			}

			else if (obj.getObjectClass() == PacketChain.METRIC_OC) {
				if (isMetricObjectPassed[passedRP_B ? 1 : 0])
					continue;
				else {

					MetricObject metric = (MetricObject) obj;
					if (attr == null)
						attr = new AttrCmpObj();
					attr.addMetric(metric);
					isMetricObjectPassed[passedRP_B ? 1 : 0] = true;
				}
			} else if (rp == null) {
				// Doh!
				isError = true;
				connection.getSessionAccessor().throwError(6, 1);
				break;
			}

			else {
				isError = true;
				connection.getSessionAccessor().throwError(3, 1);
				break;
			}

			obj.handle(connection.getSessionAccessor());
		}

		if (path != null) {
			if (attr != null) {
				path.setAttributes(attr);
			}
			response.addPath(path);
		} else {
			if (attr != null) {
				response.setAttributes(attr);
			}
		}

		if (response == null)
			isError = true;

		if (!isError) {
			connection.getSessionAccessor().addResponseForRecv(response);
			connection.getSessionAccessor().reportMessageEnd(
					PacketChain.PCREP_PT);
		} else {
			connection.getSessionAccessor().increaseUnknownMessageCounter();

		}
//		logger.debug("PCREP Message Handling Completed.");
	}
}

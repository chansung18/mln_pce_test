package pcep.packet;

import java.util.Vector;

/**
 * 요청 정보에 대한 복합 객체 RP 객체, EndPoints 객체, LSPA 객체, Bandwidth 객체, Metric 리스트, IRO
 * 객체, LoadBalance 객체 및 XRO 객체들을 저장.
 * 
 * @author Ancom
 */
public class RequestCmpObj extends PCEPObjectComposite {

	/**
	 * @uml.property name="rpObj"
	 * @uml.associationEnd
	 */
	private RPObject rpObj = null;
	/**
	 * @uml.property name="epObj"
	 * @uml.associationEnd
	 */
	private EndPointsObject epObj = null;
	/**
	 * @uml.property name="lspaObj"
	 * @uml.associationEnd
	 */
	private LSPAObject lspaObj = null;
	/**
	 * @uml.property name="bandwidthObj"
	 * @uml.associationEnd
	 */
	private BandwidthObject bandwidthObj = null;
	/**
	 * @uml.property name="metricList"
	 */
	private Vector<MetricObject> metricList = new Vector<MetricObject>();
	/**
	 * @uml.property name="rrObj"
	 * @uml.associationEnd
	 */
	private RROCmpObj rrObj = null;
	/**
	 * @uml.property name="irObj"
	 * @uml.associationEnd
	 */
	private IRObject irObj = null;
	/**
	 * @uml.property name="lbObj"
	 * @uml.associationEnd
	 */
	private LoadBalancingObject lbObj = null;
	private XRObject xrObj = null;

	public RequestCmpObj(RPObject rpObj) {
		this.rpObj = rpObj;
	}

	public RequestCmpObj(RPObject rpObj, EndPointsObject epObj) {
		this.rpObj = rpObj;
		this.epObj = epObj;
	}

	/**
	 * @param rpObj
	 * @uml.property name="rpObj"
	 */
	public void setRpObj(RPObject rpObj) {
		this.rpObj = rpObj;
	}

	/**
	 * @param epObj
	 * @uml.property name="epObj"
	 */
	public void setEpObj(EndPointsObject epObj) {
		this.epObj = epObj;
	}

	/**
	 * @return
	 * @uml.property name="lspaObj"
	 */
	public LSPAObject getLspaObj() {
		return lspaObj;
	}

	/**
	 * @param lspaObj
	 * @uml.property name="lspaObj"
	 */
	public void setLspaObj(LSPAObject lspaObj) {
		this.lspaObj = lspaObj;
	}

	/**
	 * @return
	 * @uml.property name="bandwidthObj"
	 */
	public BandwidthObject getBandwidthObj() {
		return bandwidthObj;
	}

	/**
	 * @param bandwidthObj
	 * @uml.property name="bandwidthObj"
	 */
	public void setBandwidthObj(BandwidthObject bandwidthObj) {
		this.bandwidthObj = bandwidthObj;
	}

	/**
	 * @return
	 * @uml.property name="metricList"
	 */
	public Vector<MetricObject> getMetricList() {
		return metricList;
	}

	/**
	 * @param metricList
	 * @uml.property name="metricList"
	 */
	public void setMetricList(Vector<MetricObject> metricList) {
		this.metricList = metricList;
	}

	/**
	 * @return
	 * @uml.property name="rrObj"
	 */
	public RROCmpObj getRrObj() {
		return rrObj;
	}

	/**
	 * @param rrObj
	 * @uml.property name="rrObj"
	 */
	public void setRrObj(RROCmpObj rrObj) {
		this.rrObj = rrObj;
	}

	/**
	 * @return
	 * @uml.property name="irObj"
	 */
	public IRObject getIrObj() {
		return irObj;
	}

	/**
	 * @param irObj
	 * @uml.property name="irObj"
	 */
	public void setIrObj(IRObject irObj) {
		this.irObj = irObj;
	}

	/**
	 * @return
	 * @uml.property name="lbObj"
	 */
	public LoadBalancingObject getLbObj() {
		return lbObj;
	}

	/**
	 * @param lbObj
	 * @uml.property name="lbObj"
	 */
	public void setLbObj(LoadBalancingObject lbObj) {
		this.lbObj = lbObj;
	}

	/**
	 * @return
	 * @uml.property name="rpObj"
	 */
	public RPObject getRpObj() {
		return rpObj;
	}

	/**
	 * @return
	 * @uml.property name="epObj"
	 */
	public EndPointsObject getEpObj() {
		return epObj;
	}

	// RP
	public int getRequestID() {
		return rpObj.getRequestIDNumber();
	}

	public byte getPriority() {
		return rpObj.getPri();
	}

	public boolean getFlagVSPT() {
		return rpObj.isVSPT();
	}
	
	public boolean getFlagO() {
		return rpObj.isO();
	}

	public boolean getFlagB() {
		return rpObj.isB();
	}

	public boolean getFlagR() {
		return rpObj.isR();
	}

	// EndPoint
	public int getSourceIPv4() {
		return epObj.getSrcIPv4();
	}

	public int getDestinationIPv4() {
		return epObj.getDesIPv4();
	}

	public byte[] getSourceIPv6() {
		return epObj.getSrcIPv6();
	}

	public byte[] getDestinationIPv6() {
		return epObj.getDesIPv6();
	}

	// Bandwidth
	public int getBandwidth() {
		return bandwidthObj.getBandwidth();
	}

	public boolean isBandwidthRequested() {
		return bandwidthObj.isRequested();
	}

	// RRO
	public void setRRO(RRObject rro) {
		rrObj = new RROCmpObj(rro);
	}

	// Metric
	public void addMetric(MetricObject metric) {
		metricList.add(metric);
	}

	// LoadBalancing
	public byte getMaxLSP() {
		return lbObj.getMaxLSP();
	}

	public int getMinBandwidth() {
		return lbObj.getMinBandwidth();
	}

	public XRObject getXrObj() {
		return xrObj;
	}

	public void setXrObj(XRObject xrObj) {
		this.xrObj = xrObj;
	}
	
	//<request>::= <RP> <END-POINTS> [<LSPA>] [<BANDWIDTH>] [<metric-list>]
    //[<RRO>[<BANDWIDTH>]] [<IRO>] [<LOAD-BALANCING>]
	
	@Override
	public int getLength() {
		int rplen = (rpObj == null ? 0 : rpObj.getLength());
		int eplen = (epObj == null ? 0 : epObj.getLength());
		int lspalen = (lspaObj == null ? 0 : lspaObj.getLength());
		int banlen = (bandwidthObj == null ? 0 : bandwidthObj.getLength());
		int metriclistlen = 0;
		if( !metricList.isEmpty())
		{
			int mlsize = metricList.size();
		
			for( int i =0; i < mlsize; i++)
			{
				metriclistlen += metricList.get(i).getLength();
			}
		}
		int rrocmplen = (rrObj == null ? 0 : rrObj.getLength());;
		
		int irolen = (irObj == null ? 0 : irObj.getLength());
		int lblen = (lbObj == null ? 0 : lbObj.getLength());
		
		int xrolen = (xrObj == null ? 0 : xrObj.getLength());
		return rplen + eplen + lspalen + banlen+metriclistlen+rrocmplen+irolen+lblen+xrolen;
				
	}

	@Override
	public void arrange() {
		this.objectList = new Vector<PCEPObjectAbstract>();
		if (rpObj != null)
			addObject(rpObj);
		if (epObj != null)
			addObject(epObj);
		
		if (lspaObj != null)
			addObject(lspaObj);
		if (bandwidthObj != null)
			addObject(bandwidthObj);
		
		if(!metricList.isEmpty())
		{
			int plsize =metricList.size();
			for( int i =0; i < plsize; i++)
			{
				addObject(metricList.get(i));
			}
		}
		
		if (rrObj != null)
			rrObj.arrange(this);

		if (irObj != null)
			addObject(irObj);
		if (lbObj != null)
			addObject(lbObj);
		if (xrObj != null)
			addObject(xrObj);
		
	}
	

}

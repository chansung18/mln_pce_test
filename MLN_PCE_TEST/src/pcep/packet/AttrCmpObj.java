package pcep.packet;

import java.util.Vector;

/**
 * Attribute 복합객체. LSPA, Bandwidth, Metric의 리스트, IRO, XRO를 담는 객체다.
 * 
 * @author Ancom
 */
public class AttrCmpObj extends PCEPObjectComposite {

	// private final static Logger logger = Logger.getLogger(AttrCmpObj.class);
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
	 * @uml.property name="irObj"
	 * @uml.associationEnd
	 */
	private IRObject irObj = null;
	private XRObject xrObj = null;

	private int length = 0;

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
		length += lspaObj.getLength();

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
		length += bandwidthObj.getLength();
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
		length += irObj.getLength();

	}

	public XRObject getXrObj() {
		return xrObj;
	}

	public void setXrObj(XRObject xrObj) {
		this.xrObj = xrObj;
		length += xrObj.getLength();
	}

	// Metric
	public void addMetric(MetricObject metric) {
		metricList.add(metric);
		length += metric.getLength();
	}

	// Bandwidth
	public int getBandwidth() {
		return bandwidthObj.getBandwidth();
	}

	public boolean isBandwidthRequested() {
		return bandwidthObj.isRequested();
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public void arrange() {
		this.objectList = new Vector<PCEPObjectAbstract>();
		arrange(this);
	}

	public void arrange(PCEPObjectComposite container) {
		if (lspaObj != null)
			container.addObject(lspaObj);
		if (bandwidthObj != null)
			container.addObject(bandwidthObj);
		for (int i = 0; i < metricList.size(); i++)
			container.addObject(metricList.get(i));
		if (irObj != null)
			container.addObject(irObj);
		if (xrObj != null)
			container.addObject(xrObj);

	}

}

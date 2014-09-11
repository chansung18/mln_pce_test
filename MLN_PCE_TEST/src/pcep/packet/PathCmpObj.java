package pcep.packet;

import java.util.Vector;

/**
 * PATH 복합 정보 객체. ERO 객체와 관련 Attribute 복합 정보객체를 저장한다.
 * 
 * @author Ancom
 */
public class PathCmpObj extends PCEPObjectComposite {

	// private final static Logger logger = Logger.getLogger(PathCmpObj.class);
	/**
	 * @uml.property name="ero"
	 * @uml.associationEnd
	 */
	private ERObject ero = null;
	/**
	 * @uml.property name="attributes"
	 * @uml.associationEnd
	 */
	private AttrCmpObj attributes = null;

	public PathCmpObj(ERObject ero) {
		this.ero = ero;
		attributes = null;
	}

	public PathCmpObj(ERObject ero, AttrCmpObj attr) {
		this.ero = ero;
		this.attributes = attr;
	}

	/**
	 * @return
	 * @uml.property name="ero"
	 */
	public ERObject getEro() {
		return ero;
	}

	/**
	 * @param ero
	 * @uml.property name="ero"
	 */
	public void setEro(ERObject ero) {
		this.ero = ero;
	}

	/**
	 * @return
	 * @uml.property name="attributes"
	 */
	public AttrCmpObj getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 * @uml.property name="attributes"
	 */
	public void setAttributes(AttrCmpObj attributes) {
		this.attributes = attributes;
	}

	@Override
	public int getLength() {
		return ero.getLength()
				+ (attributes == null ? 0 : attributes.getLength());
	}

	@Override
	public void arrange() {
		this.objectList = new Vector<PCEPObjectAbstract>();
		arrange(this);

	}
	
	public void arrange(PCEPObjectComposite container)
	{
		if (ero != null)
			container.addObject(ero);
		if (attributes != null)
			attributes.arrange(container);
	}

	// ERO

	// Attributes
	// LSPA
	// IRO
	// Metric
	public void addMetric(MetricObject metric) {
		if (attributes == null)
			attributes = new AttrCmpObj();
		attributes.addMetric(metric);
	}

	public Vector<MetricObject> getMetricList() {
		if (attributes != null)
			return attributes.getMetricList();
		else
			return null;
	}

	// Bandwidth
	public int getBandwidth() {
		if (attributes != null)
			return attributes.getBandwidth();
		else
			return -1;
	}

	public boolean isBandwidthRequested() {
		if (attributes != null)
			return attributes.isBandwidthRequested();
		else
			return false;
	}

	public void setBandwidthObj(BandwidthObject bandwidthObj) {
		if (attributes == null)
			attributes = new AttrCmpObj();
		attributes.setBandwidthObj(bandwidthObj);
	}

	public IRObject getIrObj() {
		if (attributes != null)
			return attributes.getIrObj();
		else
			return null;
	}

	/**
	 * @param irObj
	 * @uml.property name="irObj"
	 */
	public void setIrObj(IRObject irObj) {
		if (attributes == null)
			attributes = new AttrCmpObj();
		attributes.setIrObj(irObj);

	}

	// LSPA
	// IRO

	public XRObject getXrObj() {
		if (attributes != null)
			return attributes.getXrObj();
		else
			return null;
	}

	public void setXrObj(XRObject xrObj) {
		if (attributes == null)
			attributes = new AttrCmpObj();
		attributes.setXrObj(xrObj);
	}

	public LSPAObject getLspaObj() {
		if (attributes != null)
			return attributes.getLspaObj();
		else
			return null;
	}

	/**
	 * @param lspaObj
	 * @uml.property name="lspaObj"
	 */
	public void setLspaObj(LSPAObject lspaObj) {
		if (attributes == null)
			attributes = new AttrCmpObj();
		attributes.setLspaObj(lspaObj);

	}
}

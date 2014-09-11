package pcep.packet;

import java.util.Vector;

/**
 * 응답을 위한 복합 정보객체 관련 정보 객체들이 저장된다. 경로가 없는 경우를 위한 NOPATH 객체, 경로가 있는 경우를 위한 RP 객체와
 * Attribute 복합 객체, 그리고 Path 복합 객체를 저장할 수 있다.
 * 
 * @author Ancom
 */
public class ResponseCmpObj extends PCEPObjectComposite {
	/**
	 * @uml.property name="rpObj"
	 * @uml.associationEnd
	 */
	private RPObject rpObj = null;
	/**
	 * @uml.property name="npObj"
	 * @uml.associationEnd
	 */
	private NoPathObject npObj = null;
	/**
	 * @uml.property name="attributes"
	 * @uml.associationEnd
	 */
	private AttrCmpObj attributes = null;
	/**
	 * @uml.property name="pathList"
	 */
	private Vector<PathCmpObj> pathList = new Vector<PathCmpObj>();

	public ResponseCmpObj(RPObject rp) {
		this.rpObj = rp;
	}

	/**
	 * @return
	 * @uml.property name="rpObj"
	 */
	public RPObject getRpObj() {
		return rpObj;
	}

	/**
	 * @param rpObj
	 * @uml.property name="rpObj"
	 */
	public void setRpObj(RPObject rpObj) {
		this.rpObj = rpObj;
	}

	/**
	 * @return
	 * @uml.property name="npObj"
	 */
	public NoPathObject getNpObj() {
		return npObj;
	}

	/**
	 * @param npObj
	 * @uml.property name="npObj"
	 */
	public void setNpObj(NoPathObject npObj) {
		this.npObj = npObj;
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

	/**
	 * @return
	 * @uml.property name="pathList"
	 */
	public Vector<PathCmpObj> getPathList() {
		return pathList;
	}

	/**
	 * @param pathList
	 * @uml.property name="pathList"
	 */
	public void setPathList(Vector<PathCmpObj> pathList) {
		this.pathList = pathList;
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

	// NoPath
	public byte getNatureOfIssue() {
		return npObj.getNi();
	}

	public boolean getFlagC() {
		return npObj.isC();
	}

	public int getReason() {
		return npObj.getReason();
	}

	// Attributes

	// Metric
	public void addMetric(MetricObject metric) {
		attributes.addMetric(metric);
	}

	public Vector<MetricObject> getMetricList() {
		return attributes.getMetricList();
	}

	// Bandwidth
	public int getBandwidth() {
		return attributes.getBandwidth();
	}

	public boolean isBandwidthRequested() {
		return attributes.isBandwidthRequested();
	}

	// Paths
	public void addPath(PathCmpObj path) {
		pathList.add(path);
	}
	
	//<response>::=<RP> [<NO-PATH>] [<attribute-list>] [<path-list>]
	
	@Override
	public int getLength() {
		int rplen = (rpObj == null ? 0 : rpObj.getLength());
		int nplen = (npObj == null ? 0 : npObj.getLength());
		int attrlistlen = (attributes == null ? 0 : attributes.getLength());
		int pathlistlen = 0;
		if( !pathList.isEmpty())
		{
			int plsize =pathList.size();
		
			for( int i =0; i < plsize; i++)
			{
				pathlistlen += pathList.get(i).getLength();
			}
		}
		return rplen + nplen + attrlistlen + pathlistlen;
				
	}

	@Override
	public void arrange() {
		this.objectList = new Vector<PCEPObjectAbstract>();
		if (rpObj != null)
			addObject(rpObj);
		if (npObj != null)
			addObject(npObj);
		else if (attributes != null)
			attributes.arrange(this);
		if(!pathList.isEmpty())
		{
			int plsize =pathList.size();
			for( int i =0; i < plsize; i++)
			{
				pathList.get(i).arrange(this);
			}
		}
	}
}

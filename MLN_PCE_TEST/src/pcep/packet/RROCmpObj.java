package pcep.packet;

import java.util.Vector;

/**
 * RRO와 관련된 정보 객체들을 같이 저장하는 객체. RRO 객체와, 그에 관련된 Bandwidth 객체를 저장한다.
 * 
 * @author Ancom
 */
public class RROCmpObj extends PCEPObjectComposite {
	/**
	 * @uml.property name="rro"
	 * @uml.associationEnd
	 */
	private RRObject rro;
	/**
	 * @uml.property name="rBandwidth"
	 * @uml.associationEnd
	 */
	private BandwidthObject rBandwidth;

	public RROCmpObj(RRObject rro) {
		this.rro = rro;
	}

	/**
	 * @return
	 * @uml.property name="rro"
	 */
	public RRObject getRro() {
		return rro;
	}

	/**
	 * @param rro
	 * @uml.property name="rro"
	 */
	public void setRro(RRObject rro) {
		this.rro = rro;
	}

	/**
	 * @return
	 * @uml.property name="rBandwidth"
	 */
	public BandwidthObject getRBandwidth() {
		return rBandwidth;
	}

	/**
	 * @param bandwidth
	 * @uml.property name="rBandwidth"
	 */
	public void setRBandwidth(BandwidthObject bandwidth) {
		rBandwidth = bandwidth;
	}

	// TODO: get RRO

	// Bandwidth
	public int getBandwidth() {
		return rBandwidth.getBandwidth();
	}

	public boolean isBandwidthRequested() {
		return rBandwidth.isRequested();
	}
	
	public int getLength()
	{
		return rro.getLength() + (rBandwidth == null ? 0 : rBandwidth.getLength());
	}
	
	public void arrange(){
		this.objectList = new Vector<PCEPObjectAbstract>();
		arrange(this);
	}
	
	public void arrange(PCEPObjectComposite container)
	{
		container.addObject(rro);
		if(rBandwidth != null)
			container.addObject(rBandwidth);
	}
}

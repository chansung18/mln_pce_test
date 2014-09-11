package pcep.packet;

import java.util.Vector;

/**
 * 에러 복합 객체. Error 객체의 리스트와 RP 객체의 리스트, 그리고 OPEN 객체 등을 저장한다.
 * 
 * @author Ancom
 */
public class ErrorCmpObj extends PCEPObjectComposite {
	/**
	 * @uml.property name="requestIDList"
	 */
	Vector<RPObject> requestIDList;
	/**
	 * @uml.property name="errorList"
	 */
	Vector<PCEPErrorObject> errorList;
	/**
	 * @uml.property name="openObj"
	 * @uml.associationEnd
	 */
	OpenObject openObj = null;

	public ErrorCmpObj() {
		errorList = new Vector<PCEPErrorObject>();
		requestIDList = new Vector<RPObject>();

	}

	public ErrorCmpObj(PCEPErrorObject no) {
		errorList = new Vector<PCEPErrorObject>();
		requestIDList = new Vector<RPObject>();

		errorList.add(no);
	}

	public void addErrorObj(PCEPErrorObject no) {
		errorList.add(no);
	}

	public void addRequestID(RPObject rp) {
		requestIDList.add(rp);
	}

	/**
	 * @return
	 * @uml.property name="requestIDList"
	 */
	public Vector<RPObject> getRequestIDList() {
		return requestIDList;
	}

	/**
	 * @return
	 * @uml.property name="errorList"
	 */
	public Vector<PCEPErrorObject> getErrorList() {
		return errorList;
	}

	/**
	 * @return
	 * @uml.property name="openObj"
	 */
	public OpenObject getOpenObj() {
		return openObj;
	}

	/**
	 * @param openObj
	 * @uml.property name="openObj"
	 */
	public void setOpenObj(OpenObject openObj) {
		this.openObj = openObj;
	}
	
	@Override
	public int getLength() {
		
		int rplistlen = 0;
		if( !requestIDList.isEmpty())
		{
			int rpsize = requestIDList.size();
		
			for( int i =0; i < rpsize; i++)
			{
				rplistlen += requestIDList.get(i).getLength();
			}
		}
		int errlistlen = 0;
		if( !requestIDList.isEmpty())
		{
			int errsize = errorList.size();
		
			for( int i =0; i < errsize; i++)
			{
				errlistlen += errorList.get(i).getLength();
			}
		}
		int openlen = 0;
		if( this.openObj != null)
			openlen = openObj.getLength();
		
		return rplistlen + errlistlen + openlen;
				
	}

	@Override
	public void arrange() {
		this.objectList = new Vector<PCEPObjectAbstract>();
		if(!requestIDList.isEmpty())
		{
			int rpsize =requestIDList.size();
			for( int i =0; i < rpsize; i++)
			{
				addObject(requestIDList.get(i));
			}
		}
		if(!errorList.isEmpty())
		{
			int errsize =errorList.size();
			for( int i =0; i < errsize; i++)
			{
				addObject(errorList.get(i));
			}
		}
		if( this.openObj != null)
			addObject(this.openObj);
		
		
	}

}

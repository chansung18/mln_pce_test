package pcep.packet;

import java.util.Vector;

/**
 * Notify 복합 정보 객체. NOTIFICATION 객체의 리스트와 관련 RP 객체의 리스트를 저장한다.
 * 
 * @author Ancom
 */
public class NotifyCmpObj extends PCEPObjectComposite {
	/**
	 * @uml.property name="requestIDList"
	 */
	Vector<RPObject> requestIDList;
	/**
	 * @uml.property name="notificationList"
	 */
	Vector<NotificationObject> notificationList;

	public NotifyCmpObj() {
		notificationList = new Vector<NotificationObject>();
		requestIDList = new Vector<RPObject>();
	}

	public NotifyCmpObj(NotificationObject no) {
		notificationList = new Vector<NotificationObject>();
		requestIDList = new Vector<RPObject>();

		notificationList.add(no);
	}

	public void addNotificationObj(NotificationObject no) {
		notificationList.add(no);
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
	 * @uml.property name="notificationList"
	 */
	public Vector<NotificationObject> getNotificationList() {
		return notificationList;
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
		int ntflistlen = 0;
		if( !notificationList.isEmpty())
		{
			int ntsize = notificationList.size();
		
			for( int i =0; i < ntsize; i++)
			{
				ntflistlen += notificationList.get(i).getLength();
			}
		}
		
		return rplistlen + ntflistlen;
				
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
		if(!notificationList.isEmpty())
		{
			int ntfsize =notificationList.size();
			for( int i =0; i < ntfsize; i++)
			{
				addObject(notificationList.get(i));
			}
		}
		
		
	}
}

package pcep.eventhandler;

//import org.apache.log4j.Logger;

import pcep.PCEPConnection;

/**
 * ���� �̺�Ʈ �������Դϴ�.
 * 
 * @author Ancom
 */
public class NormalConnectionListener implements ConnectionListener {
//	private final static Logger logger = Logger
//			.getLogger(NormalConnectionListener.class);

	/**
	 * @uml.property name="connection"
	 * @uml.associationEnd
	 */
	PCEPConnection connection = null;

	public NormalConnectionListener(PCEPConnection con) {
		super();
		connection = con;
	}

	//@Override
	public void connectionClosed() throws Exception {
		// TODO Auto-generated method stub

	}

	//@Override
	public void connectionError(Exception e) throws Exception {
		// TODO Auto-generated method stub

	}

	//@Override
	public void connectionEstablished() throws Exception {
//		logger.debug("Connection Establshed.");
//
//		// Open Message Sending
//		logger.debug("Request Open Message");
		connection.sendOpen();

	}
}

package pcep.eventhandler;

//import org.apache.log4j.Logger;

import pcep.PCEPElement;
import pcep.PCEPSession;

public class InputListener implements InwardEventListener {

//	protected final static Logger logger = Logger
//			.getLogger(InputListener.class);
	protected PCEPElement pce;

	public InputListener() {

	}

	public InputListener(PCEPElement pce) {
		this.pce = pce;
	}

	//@Override
	public void messageArrived(int sid, byte messageType) throws Exception {

	}

	//@Override
	public void sessionDown(PCEPSession s) throws Exception {

	}

	//@Override
	public void sessionUp(int sid) throws Exception {

	}

}

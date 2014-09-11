package dynamic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.text.html.HTMLDocument.Iterator;

import Test.PathComputationManager;
import control.DijkstraShortestPathAlg;
import control.YenTopKShortestPathsAlg;
import model.Graph;
import model.Path;
import model.Vertex;
import model.WatchUtil;
import model.abstracts.BaseVertex;


import pcep.manager.PacketChain;
import pcep.packet.BandwidthObject;
import pcep.packet.EndPointsObject;
import pcep.packet.PathCmpObj;
import pcep.packet.IRObject;
import pcep.packet.MetricObject;
import pcep.packet.NoPathObject;
import pcep.packet.RPObject;
import pcep.packet.XRObject;

// ��������� import ��������� ���.

public class Alg_1 extends AbstractCommand  
{
	/**
	 *  int sid, RPObject rp, EndPointsObject endpoint, Vector rset_vec
	 */
	
	PathComputationManager PCM;
	
	public Alg_1()
	{
//		SystemManager.otllogger.out.setAdditivity(true);
//		SystemManager.otllogger.out.debug("[Algorithm Class: " + this.getClass() + "] ��� ������������ ���������������..");
//		SystemManager.otllogger.out.setAdditivity(false);
	}
	

	public void setPCM(PathComputationManager PCM) 
	{
		this.PCM = PCM;
	}
	
	
//	public void computePath(int sid, RPObject rp, EndPointsObject endpoint, BandwidthObject bandwidth, Vector<MetricObject> metricList, IRObject iro, XRObject xro, Vector rset_vec)
	public void computePath(int sid, RPObject rp, EndPointsObject endpoint, BandwidthObject bandwidth, Vector<MetricObject> metricList, IRObject iro, XRObject xro)
	{
//		SystemManager.otllogger.out.debug("������ ��������� ��������������� ��������� ==> " + "RPObject rp, EndPointsObject endpoint");
	
		// ��������� ������ ������ start
		
//		PCM.etriComputation(sid, rp, endpoint, bandwidth, metricList, iro, xro, rset_vec);
//		PCM.etriComputation(sid, rp, endpoint, bandwidth, metricList, iro, xro);
	
		PCM.etriComputation(sid, rp, endpoint, bandwidth, metricList, iro, xro);
		
		// ��������� ������ ������ end
	}
	
//	public PathCmpObj computePathforBRPC(EndPointsObject endpoint, BandwidthObject bandwidth, Vector<MetricObject> metricList, IRObject iro, XRObject xro)
//	{
////		SystemManager.otllogger.out.debug("������ ��������� ��������������� ���������(BRPC) ==> " + "EndPointsObject endpoint");
//		// ��������� ������ ������ start
////		return PCM.etriComputationforBRPC(endpoint, bandwidth, metricList, iro, xro);
//		// ��������� ������ ������ end
//	}
}

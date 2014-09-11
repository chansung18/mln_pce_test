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

// 반드시 import 해줘야 함.

public class Alg_1 extends AbstractCommand  
{
	/**
	 *  int sid, RPObject rp, EndPointsObject endpoint, Vector rset_vec
	 */
	
	PathComputationManager PCM;
	
	public Alg_1()
	{
//		SystemManager.otllogger.out.setAdditivity(true);
//		SystemManager.otllogger.out.debug("[Algorithm Class: " + this.getClass() + "] 이 동적으로 로딩되었음..");
//		SystemManager.otllogger.out.setAdditivity(false);
	}
	

	public void setPCM(PathComputationManager PCM) 
	{
		this.PCM = PCM;
	}
	
	
//	public void computePath(int sid, RPObject rp, EndPointsObject endpoint, BandwidthObject bandwidth, Vector<MetricObject> metricList, IRObject iro, XRObject xro, Vector rset_vec)
	public void computePath(int sid, RPObject rp, EndPointsObject endpoint, BandwidthObject bandwidth, Vector<MetricObject> metricList, IRObject iro, XRObject xro)
	{
//		SystemManager.otllogger.out.debug("다음 조합의 경로계산이 수행됨 ==> " + "RPObject rp, EndPointsObject endpoint");
	
		// 함수화 시킨 부분 start
		
//		PCM.etriComputation(sid, rp, endpoint, bandwidth, metricList, iro, xro, rset_vec);
		PCM.etriComputation(sid, rp, endpoint, bandwidth, metricList, iro, xro);
	
		// 함수화 시킨 부분 end
	}
	
//	public PathCmpObj computePathforBRPC(EndPointsObject endpoint, BandwidthObject bandwidth, Vector<MetricObject> metricList, IRObject iro, XRObject xro)
//	{
////		SystemManager.otllogger.out.debug("다음 조합의 경로계산이 수행됨(BRPC) ==> " + "EndPointsObject endpoint");
//		// 함수화 시킨 부분 start
////		return PCM.etriComputationforBRPC(endpoint, bandwidth, metricList, iro, xro);
//		// 함수화 시킨 부분 end
//	}
}

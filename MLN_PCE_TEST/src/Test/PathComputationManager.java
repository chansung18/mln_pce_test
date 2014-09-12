package Test;

import java.util.List;
import java.util.Vector;

import control.YenTopKShortestPathsAlg;
import model.Graph;
import model.Path;
import model.Vertex;
import model.WatchUtil;
import model.abstracts.BaseVertex;
import pcep.packet.BandwidthObject;
import pcep.packet.EndPointsObject;
import pcep.packet.IRObject;
import pcep.packet.MetricObject;
import pcep.packet.RPObject;
import pcep.packet.XRObject;

public class PathComputationManager {
	List<TELink> TELinkList;
	
	public void setTELinkList(List<TELink> list) {
		TELinkList = list;
	}
	
	public void etriComputation(int sid, RPObject rp, EndPointsObject endpoint, BandwidthObject bandwidth, Vector<MetricObject> metricList, IRObject iro, XRObject xro) {
		System.out.println("etri Computation 수행....ㅜ");
		
		int src = endpoint.getSrcIPv4();
		int dst = endpoint.getDesIPv4();

		String srcStr = WatchUtil.convertAddr(src);
		String dstStr = WatchUtil.convertAddr(dst);
		
		System.out.println("  |--Source Address : " + srcStr + "(" + src + ")");
		System.out.println("  |--Destination Address : " + dstStr + "(" + dst + ")");
		
		Graph graph = null;
		Path path = null;
		
		Vector<String> veNeList = new Vector<String>();
		
		for( int i = 0; i < TELinkList.size(); i++ ) {
//			String temp_row =  	rset.getString("NodeId")				+ "/" + rset.getString("InterfaceId") + "/" +
//								rset.getString("RemoteNodeId") 			+ "/" + rset.getString("RemoteInterfaceId") + "/" +
//								rset.getString("EncodingType")			+ "/" + rset.getString("OperationStatus") + "/" +
//								rset.getString("RowStatus") 			+ "/" + rset.getString("MaxBandwidth") + "/" +
//								rset.getString("AvailableBandwidth") 	+ "/" + rset.getString("Metric");
			
			String temp_row = TELinkList.get(i).getNodeId() + "/" + TELinkList.get(i).getInterfaceId() + "/" +
							  TELinkList.get(i).getRemoteNodeId() + "/" + TELinkList.get(i).getRemoteInterfaceId() + "/" + 
							  " " + "/" + " " + "/" +
							  " " + "/" + " " + "/" +
							  TELinkList.get(i).getAvailableBandwidth() + "/" +  " / " + TELinkList.get(i).getMetricLevel();
			
			veNeList.add(temp_row);
		}
		
		graph = setUp(veNeList);
		
//		Vector<BaseVertex> tmpVtxs = graph._vertex_pair_weight_if_index.get(new Long(src));
//		System.out.println("1 => " + graph._vertex_pair_weight_if_index);
		
//		BaseVertex tmpVtx = tmpVtxs.get(0);
//		System.out.println("2 => " + tmpVtx.get + ", " + tmpVtx);
		
		System.out.println("경로 계산이 시작되었다....ㅎㄷㄷ...제대로 되려나..?");
		
		List<Path> unlimited_shortest_paths_list = new Vector<Path>();
		
		YenTopKShortestPathsAlg yenAlg2 = new YenTopKShortestPathsAlg(graph);
		unlimited_shortest_paths_list = yenAlg2.get_shortest_paths(graph.get_vertex(src), 
																  graph.get_vertex(dst), 
																  10);
		
		if( unlimited_shortest_paths_list.size() > 0 ) {
			for( int i = 0; i < unlimited_shortest_paths_list.size(); i++ ) {
				Path tmpPath = (Path)unlimited_shortest_paths_list.get(i);
				
				List<BaseVertex> vertices = tmpPath.get_vertices();
				
				System.out.println((i+1) + " : " + vertices);
				
				for( int j = 0; j < vertices.size(); j++ ) {
					Vertex vtx = (Vertex) vertices.get(j);
					
					System.out.println("  |--" + (j+1) + " Vertex : " + vtx.get_ifIndex() + ", " + vtx.get_remote_ifIndex());
				}
				
				System.out.println();
//				tmpPath.
			}
		}
	}
	
	public Graph setUp(Vector<String> veNeList) {
		Graph graph = new Graph(veNeList);
		return graph;
	}
	
	public void filerUsedLink(List<TELink> TELinkList) {
		
	}
	
	public void reduceGraph(XRObject xro, List<TELink> TELinkList) {
		
	}
}

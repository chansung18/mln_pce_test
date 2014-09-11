package dynamic;

import java.util.Vector;

import Test.PathComputationManager;
import pcep.packet.RPObject;
import pcep.packet.PathCmpObj;
import pcep.packet.EndPointsObject;
import pcep.packet.BandwidthObject;
import pcep.packet.MetricObject;
import pcep.packet.IRObject;
import pcep.packet.XRObject;

public abstract class AbstractCommand 
{
	public abstract void setPCM(PathComputationManager PCM);
	
//	public abstract void computePath(int sid, RPObject rp, EndPointsObject endpoint, BandwidthObject bandwidth, Vector<MetricObject> metricList, IRObject iro, XRObject xro, Vector rset_vec);
	public abstract void computePath(int sid, RPObject rp, EndPointsObject endpoint, BandwidthObject bandwidth, Vector<MetricObject> metricList, IRObject iro, XRObject xro);
//	public abstract PathCmpObj computePathforBRPC(EndPointsObject endpoint, BandwidthObject bandwidth, Vector<MetricObject> metricList, IRObject iro, XRObject xro);
	
}

package Test;


public class TELink {
	public String nodeId;
	public String remoteNodeId;
	
	public String interfaceId;
	public String remoteInterfaceId;
	
	public String portName;
	public String remotePortName;
	
	public String ifName;
	public String remoteIfName;
	
	public boolean isOperStatus;
	
	public CommonVariable.SwitchingType switchingType;
	public CommonVariable.EncodingType encodingType;
	
	public CommonVariable.ComputationLevel computationLevel;
	public int	   metricLevel;
	
	public double maxBanswidth;
	public double availableBandwidth;
	public double availableMaxBandwidth;
	public double availableMinBandwidth;
	
	public String availableTsNumbers;
	public String trailId;
	public boolean isServiceMappingAllowed;
	
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getRemoteNodeId() {
		return remoteNodeId;
	}
	public void setRemoteNodeId(String remoteNodeId) {
		this.remoteNodeId = remoteNodeId;
	}
	public String getInterfaceId() {
		return interfaceId;
	}
	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}
	public String getRemoteInterfaceId() {
		return remoteInterfaceId;
	}
	public void setRemoteInterfaceId(String remoteInterfaceId) {
		this.remoteInterfaceId = remoteInterfaceId;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public String getRemotePortName() {
		return remotePortName;
	}
	public void setRemotePortName(String remotePortName) {
		this.remotePortName = remotePortName;
	}
	public String getIfName() {
		return ifName;
	}
	public void setIfName(String ifName) {
		this.ifName = ifName;
	}
	public String getRemoteIfName() {
		return remoteIfName;
	}
	public void setRemoteIfName(String remoteIfName) {
		this.remoteIfName = remoteIfName;
	}
	public boolean isOperStatus() {
		return isOperStatus;
	}
	public void setOperStatus(boolean isOperStatus) {
		this.isOperStatus = isOperStatus;
	}
	public CommonVariable.SwitchingType getSwitchingType() {
		return switchingType;
	}
	public void setSwitchingType(CommonVariable.SwitchingType switchingType) {
		this.switchingType = switchingType;
	}
	public CommonVariable.EncodingType getEncodingType() {
		return encodingType;
	}
	public void setEncodingType(CommonVariable.EncodingType encodingType) {
		this.encodingType = encodingType;
	}
	public CommonVariable.ComputationLevel getComputationLevel() {
		return computationLevel;
	}
	public void setComputationLevel(CommonVariable.ComputationLevel computationLevel) {
		this.computationLevel = computationLevel;
	}
	public int getMetricLevel() {
		return metricLevel;
	}
	public void setMetricLevel(int metricLevel) {
		this.metricLevel = metricLevel;
	}
	public double getMaxBanswidth() {
		return maxBanswidth;
	}
	public void setMaxBanswidth(double maxBanswidth) {
		this.maxBanswidth = maxBanswidth;
	}
	public double getAvailableBandwidth() {
		return availableBandwidth;
	}
	public void setAvailableBandwidth(double availableBandwidth) {
		this.availableBandwidth = availableBandwidth;
	}
	public double getAvailableMaxBandwidth() {
		return availableMaxBandwidth;
	}
	public void setAvailableMaxBandwidth(double availableMaxBandwidth) {
		this.availableMaxBandwidth = availableMaxBandwidth;
	}
	public double getAvailableMinBandwidth() {
		return availableMinBandwidth;
	}
	public void setAvailableMinBandwidth(double availableMinBandwidth) {
		this.availableMinBandwidth = availableMinBandwidth;
	}
	public String getAvailableTsNumbers() {
		return availableTsNumbers;
	}
	public void setAvailableTsNumbers(String availableTsNumbers) {
		this.availableTsNumbers = availableTsNumbers;
	}
	public String getTrailId() {
		return trailId;
	}
	public void setTrailId(String trailId) {
		this.trailId = trailId;
	}
	public boolean isServiceMappingAllowed() {
		return isServiceMappingAllowed;
	}
	public void setServiceMappingAllowed(boolean isServiceMappingAllowed) {
		this.isServiceMappingAllowed = isServiceMappingAllowed;
	}
}

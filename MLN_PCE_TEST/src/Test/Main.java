package Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import model.WatchUtil;
import pcep.manager.PacketChain;
import pcep.packet.BandwidthObject;
import pcep.packet.EndPointsObject;
import pcep.packet.IRObject;
import pcep.packet.MetricObject;
import pcep.packet.RPObject;
import pcep.packet.XRObject;
import dynamic.AbstractCommand;
import dynamic.Alg_1;

public class Main {
	public static void main(String args[]) {
		System.out.println("hello world here!");
		
		/*
		 * | Field                 | Type         | Null | Key | Default | Extra |
+-----------------------+--------------+------+-----+---------+-------+
| node_id               | varchar(100) | YES  |     | NULL    |       |
| nterface_id           | varchar(100) | YES  |     | NULL    |       |
| interface_name        | varchar(100) | YES  |     | NULL    |       |
| available_bw          | int(11)      | YES  |     | NULL    |       |
| remote_node_id        | varchar(100) | YES  |     | NULL    |       |
| remote_interface_id   | varchar(100) | YES  |     | NULL    |       |
| remote_interface_name | varchar(100) | YES  |     | NULL    |       |

		 */
		
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean insertionMode = true;
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("TED에 데이터를 추가 하시겠습니까..? (y or n) ");
		if( scanner.next().equals("n") ) {
			insertionMode = false;
		} else {
			insertionMode = true;
		}
		
		while( insertionMode ) {
			System.out.println("TED에 데이터를 입력해 주세요...쩝..");
			
			System.out.print("NODE ID : ");
			String nodeId = scanner.next();
			
			System.out.print("INTERFACE ID : ");
			String interfaceId = scanner.next();
			
			System.out.print("INTERFACE NAME : ");
			String interfaceName = scanner.next();
			
			System.out.print("AVAILABLE BANDWIDTH : ");
			int availableBandwidth = scanner.nextInt();
			
			System.out.print("REMOTE NODE ID : ");
			String remoteNodeId = scanner.next();
			
			System.out.print("REMOTE INTERFACE ID : ");
			String remoteInterfaceId = scanner.next();
			
			System.out.print("REMOTE INTERFACE NAME : ");
			String remoteInterfaceName = scanner.next();
			
			System.out.println("현재까지 입력된 데이터...");
			System.out.println("NODE ID : " + nodeId + 
							   "\nINTERFACE ID : " + interfaceId + 
							   "\nINTERFACE NAME : " + interfaceName + 
							   "\nAVAILABLE BANDWIDTH : " + availableBandwidth + 
							   "\nREMOTE NODE ID : " + remoteNodeId + 
							   "\nREMOTE INTERFACE ID : " + remoteInterfaceId + 
							   "\nREMOTE INTERFACE NAME : " + remoteInterfaceName);
			
			System.out.println("위의 데이터를 활용하여 TED 내용을 추가 합니다...");
			
			try {
				Statement statement = connection.createStatement();
				statement.executeUpdate("INSERT INTO ptn_ted "
						+ "(node_id, nterface_id, interface_name, available_bw, remote_node_id, remote_interface_id, remote_interface_name) "
						+ "values "
						+ "('" + nodeId + "'"
						+ ", '" + interfaceId + "'"
						+ ", '" + interfaceName + "'"
						+ ", " + availableBandwidth + ""
						+ ", '" + remoteNodeId + "'"
						+ ", '" + remoteInterfaceId + "'"
						+ ", '" + remoteInterfaceName + "')"
				);
				
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("TED에 추가하던 도중 오류가 발생 하였습니다...");
			}
			
			System.out.println("TED에 정상적으로 데이터 추가가 완료 되었습니다...");
			
			System.out.print("데이터를 더 추가 하시겠나요..? (y or n)");
			if( scanner.next().equals("n") ) {
				insertionMode = false;
			} else {
				insertionMode = true;
			}
		}
		
		System.out.println("TED로 부터 데이터를 로딩 합니다...");
		
		/*
		 * Statement st = con.createStatement();
String sql = ("SELECT * FROM posts ORDER BY id DESC LIMIT 1;");
ResultSet rs = st.executeQuery(sql);
if(rs.next()) { 
 int id = rs.getInt("first_column_name"); 
 String str1 = rs.getString("second_column_name");
}
		 */

		List<TELink> TELinkList = new ArrayList<TELink>();
		
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM ptn_ted");
			
			while( rs.next() ) {
				TELink tmpTELink = new TELink();
				tmpTELink.setNodeId(rs.getString("node_id"));
				tmpTELink.setInterfaceId(rs.getString("nterface_id"));
				tmpTELink.setIfName(rs.getString("interface_name"));
				tmpTELink.setAvailableBandwidth(rs.getInt("available_bw"));
				tmpTELink.setRemoteNodeId(rs.getString("remote_node_id"));
				tmpTELink.setRemoteInterfaceId(rs.getString("remote_interface_id"));
				tmpTELink.setRemoteIfName(rs.getString("remote_interface_name"));
				tmpTELink.setMetricLevel(1);
				
				TELinkList.add(tmpTELink);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("TED 로딩에 문제가 발생했네여...;");
		}
		
		System.out.println("TED 로딩 성공적으로 완료 했습니다... 로딩된 개체수는 (" + TELinkList.size() + ")");
		
		//Get TE Links from TED
//			Vector
			
		//Make Graph
		PathComputationManager pcm = new PathComputationManager();
		pcm.setTELinkList(TELinkList);
		
		AbstractCommand command = new Alg_1();
		command.setPCM(pcm);
		
		int srcAddr = (int)WatchUtil.getAddrLong("10.254.254.101");
		int destAddr = (int)WatchUtil.getAddrLong("10.254.254.104");
		
		EndPointsObject endpoint = new EndPointsObject(PacketChain.PCREQ_PT, srcAddr, destAddr);
		
		command.computePath(0, null, endpoint, null, null, null, null);
		
		
		//Computation..
	}
}
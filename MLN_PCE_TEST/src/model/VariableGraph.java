package model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import control.DijkstraShortestPathAlg;
import model.abstracts.BaseVertex;


public class VariableGraph extends Graph
{
//	Set<Integer> _rem_vertex_id_set = new HashSet<Integer>();
	Set<Long> _rem_vertex_id_set = new HashSet<Long>();
//	Set<Pair<Integer, Integer>> _rem_edge_set = new HashSet<Pair<Integer, Integer>>();
	Set<Pair<Long, Long>> _rem_edge_set = new HashSet<Pair<Long, Long>>();

	/**(
	 * Default constructor
	 */
	public VariableGraph(){};
	
	/**
	 * Constructor 1
	 * 
	 * @param data_file_name
	 */
	public VariableGraph(String data_file_name)
	{
		super(data_file_name);
	}
	
	/**
	 * Constructor 2
	 * 
	 * @param graph
	 */
	public VariableGraph(Graph graph)
	{
		super(graph);
	}

	/**
	 * Set the set of vertices to be removed from the graph
	 * 
	 * @param _rem_vertex_list
	 */
//	public void set_rem_vertex_id_list(Collection<Integer> _rem_vertex_list)
//	{
//		this._rem_vertex_id_set.addAll(_rem_vertex_list);
//	}
	public void set_rem_vertex_id_list(Collection<Long> _rem_vertex_list)
	{
		this._rem_vertex_id_set.addAll(_rem_vertex_list);
	}

	/**
	 * Set the set of edges to be removed from the graph
	 * 
	 * @param _rem_edge_hashcode_set
	 */
//	public void set_rem_edge_hashcode_set(Collection<Pair<Integer, Integer>> rem_edge_collection)
//	{
//		_rem_edge_set.addAll(rem_edge_collection);
//	}
	public void set_rem_edge_hashcode_set(Collection<Pair<Long, Long>> rem_edge_collection)
	{
		_rem_edge_set.addAll(rem_edge_collection);
	}
	
	/**
	 * Add an edge to the set of removed edges
	 * 
	 * @param edge
	 */
//	public void remove_edge(Pair<Integer, Integer> edge)
//	{
//		_rem_edge_set.add(edge);
//	}
	public void remove_edge(Pair<Long, Long> edge)
	{
		_rem_edge_set.add(edge);
	}	
	
	/**
	 * Add a vertex to the set of removed vertices
	 * 
	 * @param vertex_id
	 */
//	public void remove_vertex(Integer vertex_id)
//	{
//		_rem_vertex_id_set.add(vertex_id);
//	}
	public void remove_vertex(Long vertex_id)
	{
		_rem_vertex_id_set.add(vertex_id);
	}
	
	public void recover_removed_edges()
	{
		_rem_edge_set.clear();
	}

//	public void recover_removed_edge(Pair<Integer, Integer> edge)
//	{
//		_rem_edge_set.remove(edge);
//	}
	public void recover_removed_edge(Pair<Long, Long> edge)
	{
		_rem_edge_set.remove(edge);
	}
	
	public void recover_removed_vertices()
	{
		_rem_vertex_id_set.clear();
	}
	
//	public void recover_removed_vertex(Integer vertex_id)
//	{
//		_rem_vertex_id_set.remove(vertex_id);
//	}
	public void recover_removed_vertex(Long vertex_id)
	{
		_rem_vertex_id_set.remove(vertex_id);
	}
	
	/**
	 * Return the weight associated with the input edge.
	 * 
	 * @param source
	 * @param sink
	 * @return
	 */
	public double get_edge_weight(BaseVertex source, BaseVertex sink)
	{
//		int source_id = source.get_id();
//		int sink_id = sink.get_id();
		long source_id = source.get_id();
		long sink_id = sink.get_id();
		
//		if(_rem_vertex_id_set.contains(source_id) || _rem_vertex_id_set.contains(sink_id) || _rem_edge_set.contains(new Pair<Integer, Integer>(source_id, sink_id)))
		if(_rem_vertex_id_set.contains(source_id) || _rem_vertex_id_set.contains(sink_id) || _rem_edge_set.contains(new Pair<Long, Long>(source_id, sink_id)))
		{
			return Graph.DISCONNECTED;
		}
		return super.get_edge_weight(source, sink);
	}

	/**
	 * Return the weight associated with the input edge.
	 * 
	 * @param source
	 * @param sink
	 * @return
	 */
	public double get_edge_weight_of_graph(BaseVertex source, BaseVertex sink)
	{
		return super.get_edge_weight(source, sink);
	}
	
	/**
	 * Return the set of fan-outs of the input vertex.
	 * 
	 * @param vertex
	 * @return
	 */
//	public Set<BaseVertex> get_adjacent_vertices(BaseVertex vertex)
	public Vector<BaseVertex> get_adjacent_vertices(BaseVertex vertex)
	{
//		Set<BaseVertex> ret_set = new HashSet<BaseVertex>();
		Vector<BaseVertex> ret_set = new Vector<BaseVertex>();
//		int starting_vertex_id = vertex.get_id();
		long starting_vertex_id = vertex.get_id();
		if(!_rem_vertex_id_set.contains(starting_vertex_id))
		{
//			Set<BaseVertex> adj_vertex_set = super.get_adjacent_vertices(vertex);
			Vector<BaseVertex> adj_vertex_set = super.get_adjacent_vertices(vertex);
			for(BaseVertex cur_vertex : adj_vertex_set)
			{
//				int ending_vertex_id = cur_vertex.get_id();
				long ending_vertex_id = cur_vertex.get_id();
//				if(_rem_vertex_id_set.contains(ending_vertex_id) || _rem_edge_set.contains(new Pair<Integer,Integer>(starting_vertex_id, ending_vertex_id)))
				if(_rem_vertex_id_set.contains(ending_vertex_id) || _rem_edge_set.contains(new Pair<Long,Long>(starting_vertex_id, ending_vertex_id)))
				{
					continue;
				}
				
				// 
				ret_set.add(cur_vertex);
			}
		}
		return ret_set;
	}

	/**
	 * Get the set of vertices preceding the input vertex.
	 * 
	 * @param vertex
	 * @return
	 */
//	public Set<BaseVertex> get_precedent_vertices(BaseVertex vertex)
	public Vector<BaseVertex> get_precedent_vertices(BaseVertex vertex)
	{
//		Set<BaseVertex> ret_set = new HashSet<BaseVertex>();
		Vector<BaseVertex> ret_set = new Vector<BaseVertex>();
		if(!_rem_vertex_id_set.contains(vertex.get_id()))
		{
//			int ending_vertex_id = vertex.get_id();
			long ending_vertex_id = vertex.get_id();
//			Set<BaseVertex> pre_vertex_set = super.get_precedent_vertices(vertex);
			Vector<BaseVertex> pre_vertex_set = super.get_precedent_vertices(vertex);
			for(BaseVertex cur_vertex : pre_vertex_set)
			{
//				int starting_vertex_id = cur_vertex.get_id();
//				if(_rem_vertex_id_set.contains(starting_vertex_id) || _rem_edge_set.contains(new Pair<Integer, Integer>(starting_vertex_id, ending_vertex_id))) 
				long starting_vertex_id = cur_vertex.get_id();
				if(_rem_vertex_id_set.contains(starting_vertex_id) || _rem_edge_set.contains(new Pair<Long,Long>(starting_vertex_id, ending_vertex_id)))
				{
					continue;
				}
				
				//
				ret_set.add(cur_vertex);
			}
		}
		return ret_set;
	}

	/**
	 * Get the list of vertices in the graph, except those removed.
	 * @return
	 */
	public List<BaseVertex> get_vertex_list()
	{
		List<BaseVertex> ret_list = new Vector<BaseVertex>();
		for(BaseVertex cur_vertex : super.get_vertex_list())
		{
			if(_rem_vertex_id_set.contains(cur_vertex.get_id())) continue;
			ret_list.add(cur_vertex);
		}
		return ret_list;
	}

	/**
	 * Get the vertex corresponding to the input 'id', if exist. 
	 * 
	 * @param id
	 * @return
	 */
	public BaseVertex get_vertex(int id)
	{
		if(_rem_vertex_id_set.contains(id))
		{
			return null;
		}else
		{
			return super.get_vertex(id);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("Welcome to the class VariableGraph!");
		
		VariableGraph graph = new VariableGraph("data/test_50");
//		graph.remove_vertex(13);
//		graph.remove_vertex(12);
//		graph.remove_vertex(10);
//		graph.remove_vertex(23);
//		graph.remove_vertex(47);
//		graph.remove_vertex(49);
//		graph.remove_vertex(3);
		graph.remove_vertex(13l);
		graph.remove_vertex(12l);
		graph.remove_vertex(10l);
		graph.remove_vertex(23l);
		graph.remove_vertex(47l);
		graph.remove_vertex(49l);
		graph.remove_vertex(3l);
//		graph.remove_edge(new Pair<Integer, Integer>(26, 41));
		graph.remove_edge(new Pair<Long, Long>(26l, 41l)); // �ڿ� ���Ĺ�l�� ����
		DijkstraShortestPathAlg alg = new DijkstraShortestPathAlg(graph);
		System.out.println(alg.get_shortest_path(graph.get_vertex(0), graph.get_vertex(20)));
	}
}

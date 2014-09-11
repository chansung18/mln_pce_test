package model.abstracts;

import java.util.List;
import java.util.Set;
import java.util.Vector;

public interface BaseGraph
{
	List<BaseVertex> get_vertex_list();
	
	double get_edge_weight(BaseVertex source, BaseVertex sink);
//	Set<BaseVertex> get_adjacent_vertices(BaseVertex vertex);
	Vector<BaseVertex> get_adjacent_vertices(BaseVertex vertex);
//	Set<BaseVertex> get_precedent_vertices(BaseVertex vertex);
	Vector<BaseVertex> get_precedent_vertices(BaseVertex vertex);
}

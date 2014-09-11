package model.abstracts;

public interface BaseEdge
{
	int get_weight();
	
	BaseVertex get_start_vertex();
	BaseVertex get_end_vertex();
}

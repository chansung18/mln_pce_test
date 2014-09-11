package model;

import model.abstracts.BaseVertex;

public class Vertex implements BaseVertex, Comparable<Vertex>
{
//	private static int CURRENT_VERTEX_NUM = 0;
//	private int _id = CURRENT_VERTEX_NUM++;
//	private double _weight = 0;
	
	private static long CURRENT_VERTEX_NUM = 0;
	private long _id = CURRENT_VERTEX_NUM++;
	private double _weight = 0;
	// ischoi
	private int _ifIndex = 0; 
	private int _remote_ifIndex = 0;
	
	// ischoi
	public void set_ifIndex(int index)
	{
		_ifIndex = index;
	}
	public int get_ifIndex()
	{
		return _ifIndex;
	}
	public void set_remote_ifIndex(int remoteIndex)
	{
		_remote_ifIndex = remoteIndex;
	}
	public int get_remote_ifIndex()
	{
		return _remote_ifIndex;
	}
	/**
	 * 
	 */
//	public int get_id()
//	{
//		return _id;
//	}
	public long get_id()
	{
		return _id;
	}

	public String toString()
	{
		return ""+_id;
	}

	public double get_weight()
	{
		return _weight;
	}
	
	public void set_id(long id)
	{
		_id = id;
	}
	
	public void set_weight(double status)
	{
		_weight = status;
	}
	
	public int compareTo(Vertex r_vertex)
	{
		double diff = this._weight - r_vertex._weight;
		if(diff > 0)
			return 1;
		else if(diff < 0)
			return -1;
		else 
			return 0;
	}
	
	public static void reset()
	{
		CURRENT_VERTEX_NUM = 0;
	}
}

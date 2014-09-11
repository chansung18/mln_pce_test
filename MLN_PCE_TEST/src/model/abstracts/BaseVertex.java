package model.abstracts;

public interface BaseVertex
{
//	int get_id();
	long get_id();
	double get_weight();
	void set_id(long id);
	void set_weight(double weight);
	
	// ischoi
	public void set_ifIndex(int index);

	public int get_ifIndex();

	public void set_remote_ifIndex(int remoteIndex);

	public int get_remote_ifIndex();

}

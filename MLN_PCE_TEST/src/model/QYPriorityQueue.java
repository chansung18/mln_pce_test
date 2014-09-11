package model;

import java.util.LinkedList;
import java.util.List;

import model.abstracts.BaseElementWithWeight;


public class QYPriorityQueue<E extends BaseElementWithWeight>
{
	List<E> _element_weight_pair_list = new LinkedList<E>();
	int _limit_size = -1;
	boolean _is_incremental = false; 
	
	/**
	 * Default constructor. 
	 */
	public QYPriorityQueue(){};
	
	/**
	 * Constructor. 
	 * @param limit_size
	 */
	public QYPriorityQueue(int limit_size, boolean is_incremental)
	{
		_limit_size = limit_size;
		_is_incremental = is_incremental;
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return _element_weight_pair_list.toString();
	}
	
	/**
	 * Binary search is exploited to find the right position 
	 * of the new element. 
	 * @param weight
	 * @return the position of the new element
	 */
	private int _bin_locate_pos(double weight, boolean is_incremental)
	{
		int mid = 0;
		int low = 0;
		int high = _element_weight_pair_list.size() - 1;
		//
		while(low <= high)
		{
			mid = (low+high)/2;
			if(_element_weight_pair_list.get(mid).get_weight() == weight)
				return mid+1;
			
			if(is_incremental)
			{
				if(_element_weight_pair_list.get(mid).get_weight() < weight)
				{
					high = mid - 1;
				}else
				{
					low = mid + 1;
				}	
			}else
			{
				if(_element_weight_pair_list.get(mid).get_weight() > weight)
				{
					high = mid - 1;
				}else
				{
					low = mid + 1;
				}
			}	
		}
		return low;
	}
	
	/**
	 * Add a new element in the queue. 
	 * @param element
	 */
	public void add(E element)
	{
		_element_weight_pair_list.add(_bin_locate_pos(element.get_weight(), _is_incremental), element);
		
		if(_limit_size > 0 && _element_weight_pair_list.size() > _limit_size)
		{
			int size_of_results = _element_weight_pair_list.size();
			_element_weight_pair_list.remove(size_of_results-1);			
		}
	}
	
	/**
	 * It only reflects the size of the current results.
	 * @return
	 */
	public int size()
	{
		return _element_weight_pair_list.size();
	}
	
	/**
	 * Get the i th element. 
	 * @param i
	 * @return
	 */
	public E get(int i)
	{
		if(i >= _element_weight_pair_list.size())
		{
			System.err.println("The result :" + i +" doesn't exist!!!");
		}
		return _element_weight_pair_list.get(i);
	}
	
	/**
	 * Get the first element, and then remove it from the queue. 
	 * @return
	 */
	public E poll()
	{
		E ret = _element_weight_pair_list.get(0);
		_element_weight_pair_list.remove(0);
		return ret;
	}
	
	/**
	 * Check if it's empty.
	 * @return
	 */
	public boolean isEmpty()
	{
		return _element_weight_pair_list.isEmpty();
	}
	
}

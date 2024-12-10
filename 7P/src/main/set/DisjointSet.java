/** CS515 Assignment 7P

Name: Nick Gibson

Section: 1

Date: 11/11/24

Collaboration Declaration:

Collaboration: None

*/

package set;
//package set;

import java.util.HashMap;
import java.util.Map;

public class DisjointSet<T extends Comparable<T>> {

	Map<T, T> parents;
	Map<T, Integer> depths;
	public int size;

	public DisjointSet() {
		parents = new HashMap<T, T>();
		depths = new HashMap<T, Integer>();
		size = 0;
	}


	public int size() {
		
		return this.size;
	}

	/**
	 * Creates a singleton from the parameter.
	 */
	public void createSet(T _t) {
        
		parents.put(_t, _t);
		depths.put(_t,0);
		size++;

		
	}

	/**
	 * Finds and returns the representative of the set which contains the
	 * parameter. Implements path compression.
	 */
	public T findSet(T _t) {

		if(_t != parents.get(_t)) {

			parents.put(_t, findSet(parents.get(_t)));
		}

		return parents.get(_t);


	}

	/**
	 * Combines the sets which contain the parameters.
	 */
	public void unionSets(T _x, T _y) {
		
		T _xID = findSet(_x);
		T _yID = findSet(_y);

		if(_xID == _yID) {
			return;
		}

		if(depths.get(_xID) > depths.get(_yID)) {

			parents.put(_yID,_xID);
		} else {

			parents.put(_xID, _yID);

			if(depths.get(_xID) == depths.get(_yID)) {
				depths.put(_yID, depths.get(_yID) + 1);
			}
		}

		size--;
	}
}

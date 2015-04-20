package org.jax.mgi.searchtool_wi.results;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ResultContainer {

	List scoreSortedResults;
	HashMap<String, AbstractResult> mappedResults = new HashMap<String, AbstractResult>();

	public ResultContainer(List<AbstractResult> l) {

		scoreSortedResults = l;

		// fill key->result mapping
		for (Iterator<AbstractResult> iter = l.iterator(); iter.hasNext();) {
			AbstractResult current = iter.next();
			mappedResults.put(current.getResultKey(), current);
		}
	}

	/**
	 * Returns a list of hits, with a given starting and stopping point;
	 * Expected start/stop values are 1,10 for the first ten, rather than
	 * 0,9 (e.g. like array references)
	 *
	 * @param start
	 * @param stop
	 * @return
	 */
	public List getHits(int start, int stop) {

		int startIndex = start;
		int stopIndex = stop;

		if (startIndex < 1) {startIndex = 1;}

		if (stopIndex > scoreSortedResults.size()) {
			stopIndex = scoreSortedResults.size();
		}

		return scoreSortedResults.subList((startIndex - 1), stopIndex);

	}

	/**
	 * Get a list of this in a given range, from start to start + offset.
	 *
	 * @param start
	 * @param range
	 * @return
	 */
	public List getHitsByRange(int start, int range) {
		return getHits(start, start + range);
	}

	/**
	 * Get a list of hits, starting from the the first hit, until the offset.
	 *
	 * @param x
	 * @return
	 */
	public List getTopHits(int x) {
		return getHits(1, x);
	}

	public int size() {
		return scoreSortedResults.size();
	}

}

package org.jax.mgi.searchtool_wi.searches;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jax.mgi.searchtool_wi.dataAccess.IndexAccessor;
import org.jax.mgi.searchtool_wi.results.AbstractResult;
import org.jax.mgi.searchtool_wi.results.ResultSorter;
import org.jax.mgi.searchtool_wi.utils.SearchInput;
import org.jax.mgi.shr.config.Configuration;
import org.jax.mgi.shr.timing.TimeStamper;

/**
 * Abstract class in which all concrete searches will extend.  Implements a
 * Template Method pattern.  Extending classes need only define a 'gatherData'
 * method.  Sorting and finalization of results implemented here.  Clients
 * instantiate a concrete Search, and call the search() method (defined here)
 */
public abstract class AbstractSearch {

	// Configuration knowledge
	protected Configuration config;

	// Timer for timing information
	protected TimeStamper timer;

	// Access layer to the indexes
	protected IndexAccessor indexAccessor;

	// filled as the search progresses, and will be returned from the search
	private List resultList;

	// logger
	private static Logger logger = Logger.getLogger(AbstractSearch.class.getName());

	public AbstractSearch(Configuration c) {
		config = c;
		indexAccessor = new IndexAccessor(c);
		timer = new TimeStamper();
	}

	/**
	 * The Template Method, encapsulating a series of run-time steps, some of
	 *  which are defined here in the abstract, and some to be defined by the
	 *  extending classes.
	 * @param SearchInput object
	 * @return List of sorted, finalized, result objects
	 */
	public final List search (SearchInput searchInput) throws Exception {
		// start the logging timer
		timer.record("");

		// 'hook' method; if the searches have specific modifications or
		// preparations of the input, they may over-ride this method
		processInput(searchInput); //hook

		// abstract method; extending classes MUST over-ride
		resultList = gatherData(searchInput); //abstract

		// finalization (scoring, match sorting) of all result objects
		AbstractResult result;
		for (Iterator iter = resultList.iterator(); iter.hasNext();) {
			result = (AbstractResult) iter.next();
			result.finalizeResult();
		}

		// sort the results
		Collections.sort(resultList, new ResultSorter());

		// log entry into the log at the end of the search
		logger.info("---------------------------------------------");
		logger.info(timer.toString());

		return resultList;
	}

	/**
	 * This is the abstract method all extending classes must implement
	 */
	abstract List gatherData (SearchInput si) throws Exception;

	/**
	 * This is a "hook" method all extending classes *may* implement is any
	 * pre-processing of the input object is required for the given search
	 */
	void processInput(SearchInput si) {};

}

package org.jax.mgi.searchtool_wi.searches;

// Standard Java Classes
import java.util.*;

// Logging
import org.apache.log4j.Logger;

// Search Tool Classes
import org.jax.mgi.searchtool_wi.dataAccess.IndexAccessor;
import org.jax.mgi.searchtool_wi.results.AbstractResult;
import org.jax.mgi.searchtool_wi.results.ResultSorter;
import org.jax.mgi.searchtool_wi.utils.SearchInput;

// MGI Shared Classes
import org.jax.mgi.shr.config.Configuration;
import org.jax.mgi.shr.timing.TimeStamper;


/**
* Abstract class in which all concrete searches will extend.  Implements a
* Template Method pattern.  Extending classes need only define a 'gatherData'
* method.  Sorting and finalization of results implemented here.  Clients
* instantiate a concrete Search, and call the search() method (defined here)
*/
public abstract class AbstractSearch {

  //--------//
  // Fields
  //--------//

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

  //-------------//
  // Constructor //
  //-------------//
  public AbstractSearch(Configuration c)
  {
    config = c;
    indexAccessor = new IndexAccessor(c);
    timer = new TimeStamper();

  }

  //----------------//
  // Template Method
  //----------------//

  /**
  * The Template Method, encapsulating a series of run-time steps, some of
  *  which are defined here in the abstract, and some to be defined by the
  *  extending classes.
  * @param SearchInput object
  * @return List of sorted, finalized, result objects
  */
  public final List search (SearchInput searchInput)
    throws Exception
  {
    // start the logging timer
    timer.record("");

    // 'hook' method; if the searches have specific modifications or
    // preparations of the input, they may over-ride this method
    processInput(searchInput); //hook

    // abstract method; extending classes MUST over-ride
    resultList= gatherData(searchInput); //abstract

    // finalization (scoring, match sorting) of all result objects
    finalizeResults();

    // sort the results
    scoreSort();

    // log entry into the log at the end of the search
    logger.info("---------------------------------------------");
    logger.info(timer.toString());

    return resultList;
  }

  //-----------------//
  // Abstract Methods
  //-----------------//

  /**
  * This is the abstract method all extending classes must implement
  */
  abstract List gatherData (SearchInput si) throws Exception;


  //----------------//
  // 'Hook' Methods
  //----------------//

  /**
  * This is a "hook" method all extending classes *may* implement is any
  * pre-processing of the input object is required for the given search
  */
  void processInput(SearchInput si) {};


  //-----------------//
  // Concrete Methods
  //-----------------//

  /**
  * For every result object, call it's finalizeResult method.  This is
  * essentially a post-process step for things we want to do once (per result
  * object).  For example: sorting, scoring, data decorations...
  */
  private void finalizeResults() {

    AbstractResult result;
    for (Iterator iter = resultList.iterator(); iter.hasNext();) {
        result = (AbstractResult) iter.next();
        result.finalizeResult();
    }
  }

  /**
  * The result objects should be returned in a score-sorted order.
  */
  private void scoreSort () {

    ResultSorter resultSorter = new ResultSorter();
    Collections.sort(resultList, resultSorter);
    return;
  }

}

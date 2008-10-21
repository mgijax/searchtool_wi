package org.jax.mgi.searchtool_wi.lookup;

import java.util.*;

import org.jax.mgi.searchtool_wi.results.QS_MarkerResultContainer;
import org.jax.mgi.shr.config.Configuration;


/**
* In-memory cache to hold marker result-sets of/for user search strings.
*
* All accessor methods to this object should be synchronized.  Use care to
* minimize logic, avoiding possible access bottle-necks to this class.
*/
public class QS_MarkerResultCache {

  //--------//
  // Fields
  //--------//

  // configuration inforation
  private Configuration config;

  // Time-ordered list of input strings
  private LinkedList markerContainerOrderQue;

  // Time-ordered list of input strings
  private Map<String,QS_MarkerResultContainer> markerResultContainers;

  private static final int resultQueSize = 20;

  //-------------//
  // Constructor //
  //-------------//
  public QS_MarkerResultCache(Configuration c)
  {
    config = c;

    // Pre-fill que with empty strings; serves to initialize to it's run-time
    // size.  No size-checking logic will be required, as accessor execution
    // will only add/remove one result-set (maintaining this initial size)
    markerContainerOrderQue = new LinkedList();
    for (int count=0; count < resultQueSize; count++ ) {
        markerContainerOrderQue.add( new String("") );
    }

    // Initialize holder of result-sets
    markerResultContainers = new HashMap<String, QS_MarkerResultContainer>();
  }

  //-------------------------//
  // Synchronized  Accessors
  //-------------------------//

  /**
  * Retrieves a marker result-set, if it's cached
  * @param user's input string
  * @return a QS_MarkerResultContainer, or NULL is none found in cache
  */
  public synchronized QS_MarkerResultContainer getMarkerContainer(String inputString) {
    return markerResultContainers.get(inputString);
  }

  /**
  * Add marker result-set to cache
  * @param String - User's input string
  * @param QS_MarkerResultContainer - Result set to cache
  *
  * Note that no logic is used; leveraging speed of map-based storage and
  * que's end-point access (for trimming out the oldest results)
  */
  public synchronized void addMarkerContainer (String inputStr, QS_MarkerResultContainer mrc) {

    // adding entries to the que and mapping
    markerResultContainers.put(inputStr, mrc);
    markerContainerOrderQue.addFirst(inputStr);

    // remove the last que entry, and it's respective entry in the map
    markerResultContainers.remove( markerContainerOrderQue.removeLast() );
  }

}

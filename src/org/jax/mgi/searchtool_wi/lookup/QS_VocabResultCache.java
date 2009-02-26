package org.jax.mgi.searchtool_wi.lookup;

// Standard Java Classes
import java.util.*;

// Quick Search Classes
import org.jax.mgi.searchtool_wi.results.QS_VocabResultContainer;

/**
* In-memory cache to hold vocab result-sets of/for user search strings.
*
* All accessor methods to this object should be synchronized.  Use care to
* minimize logic, avoiding possible access bottle-necks to this class.
*/
public class QS_VocabResultCache {

  //--------//
  // Fields
  //--------//

  // Time-ordered list of input strings
  private LinkedList vocabContainerOrderQue;

  // Time-ordered list of input strings
  private Map<String,QS_VocabResultContainer> vocabResultContainers;

  //-------------//
  // Constructor //
  //-------------//
  public QS_VocabResultCache(String resultCacheSizeStr)
  {
    int resultCacheSize = new Integer(resultCacheSizeStr).intValue();

    // Pre-fill que with empty strings; serves to initialize to it's run-time
    // size.  No size-checking logic will be required, as accessor execution
    // will only add/remove one result-set (maintaining this initial size)
    vocabContainerOrderQue = new LinkedList();
    for (int count=0; count < resultCacheSize; count++ ) {
        vocabContainerOrderQue.add( new String("") );
    }

    // Initialize holder of result-sets
    vocabResultContainers = new HashMap<String, QS_VocabResultContainer>();
  }

  //-------------------------//
  // Synchronized  Accessors
  //-------------------------//

  /**
  * Retrieves a vocab result-set, if it's cached
  * @param user's input string
  * @return a QS_VocabResultContainer, or NULL is none found in cache
  */
  public synchronized QS_VocabResultContainer getVocabContainer(String inputString) {
    return vocabResultContainers.get(inputString);
  }

  /**
  * Add vocab result-set to cache
  * @param String - User's input string
  * @param QS_VocabResultContainer - Result set to cache
  *
  * Note that no logic is used; leveraging speed of map-based storage and
  * que's end-point access (for trimming out the oldest results)
  */
  public synchronized void addVocabContainer (String inputStr, QS_VocabResultContainer vrc) {

    // adding entries to the que and mapping
    vocabResultContainers.put(inputStr, vrc);
    vocabContainerOrderQue.addFirst(inputStr);

    // remove the last que entry, and it's respective entry in the map
    vocabResultContainers.remove( vocabContainerOrderQue.removeLast() );
  }
}

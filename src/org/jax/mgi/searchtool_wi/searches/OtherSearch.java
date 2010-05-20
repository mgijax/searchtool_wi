package org.jax.mgi.searchtool_wi.searches;

// Standard Java Classes
import java.util.*;
import java.io.IOException;

// MGI Shared Classes
import org.jax.mgi.shr.config.Configuration;

// Logging
import org.apache.log4j.Logger;

// Lucene Classes
import org.apache.lucene.search.*;

// Quick Search Specific
import org.jax.mgi.searchtool_wi.matches.OtherMatch;
import org.jax.mgi.searchtool_wi.matches.OtherMatchFactory;
import org.jax.mgi.searchtool_wi.results.OtherResult;
import org.jax.mgi.searchtool_wi.utils.SearchInput;
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
* This concrete search is responsible for gathering all data required for
* the Quick Search to display the "Other Bucket"
*/
public class OtherSearch extends AbstractSearch {

  //--------//
  // Fields
  //--------//

  // Set up the logger
  private static Logger log
    = Logger.getLogger(OtherSearch.class.getName());

  // results to be returned
  private HashMap <String, OtherResult> searchResults
    = new HashMap <String, OtherResult>();

  // Match factories we'll need generate the matches
  OtherMatchFactory otherMatchFactory
    = new OtherMatchFactory(config);

  //--------------//
  // Constructors
  //--------------//
  public OtherSearch(Configuration c)
  {
    super(c);
  }

  //----------------------------------------//
  // Over-ridden Abstract gatherData Method //
  //----------------------------------------//

  /**
  * gatherData() is an over-ridden method from the AbstractSearch class, and
  *   is responsible for gathering all results objects
  * @param a SearchInput object
  * @return List of result objects; these are not yet 'finalized', scored,
  *   or sorted, as the framework will take care of that
  */
  public List gatherData(SearchInput searchInput)
    throws Exception
  {
    timer.record("---Other gatherData Started---");

    searchOtherExact(searchInput);

    timer.record("Other - Exact Search Done");

    return new ArrayList( searchResults.values() );
  }

  /**
  * Searches the OtherExact index.  Iterates through results, adding a match
  * for each hit it finds
  */
  private void searchOtherExact(SearchInput searchInput)
    throws Exception
  {
    Hit hit;
    List <Hit> hits =  indexAccessor.searchOtherExactByLargeToken(searchInput);
    log.info("Other Exact Hits: " + hits.size());

    for (Iterator <Hit> iter = hits.iterator(); iter.hasNext();)
    {
        // get the hit, and build a match
        hit = iter.next();
        handleOtherExactHit(hit);
    }

  }

  private void handleOtherExactHit (Hit hit)
    throws Exception
  {
    OtherResult otherResult;
    OtherMatch otherMatch;

    // info about the result
    otherResult = getOther(hit.get(IndexConstants.COL_DB_KEY), hit.get(IndexConstants.COL_DATA_TYPE));
    otherResult.setAccId(hit.get(IndexConstants.COL_DATA));

    // match generation and assignment
    otherMatch = otherMatchFactory.getMatch(hit);
    otherResult.addExactMatch(otherMatch);
  }

  //-------------------------//
  // Private Utility Methods
  //-------------------------//

  /**
  * Convenience method to interface with the result set; If a given entry has
  * already been created, it will be returned.  If not, a new one is
  * generated, placed in the result set, and then returned.
  */
  private OtherResult getOther(String otherKey, String dataType)
  {
    if (searchResults.containsKey(otherKey+"::"+dataType)) {
        return (OtherResult)searchResults.get( otherKey+"::"+dataType);
    }
    else {
        OtherResult other = new OtherResult(config);
        other.setDbKey(otherKey);
        other.setDataType(dataType);
        searchResults.put(other.getDbKey()+"::"+other.getDataType(), other);
        return other;
    }
  }

}
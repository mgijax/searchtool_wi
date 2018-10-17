package org.jax.mgi.searchtool_wi.searches;

// Standard Java Classes
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.codehaus.jackson.map.ObjectMapper;

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
import org.jax.mgi.searchtool_wi.utils.AccessionSummaryRow;
import org.jax.mgi.searchtool_wi.results.JsonSummaryResponse;
import org.jax.mgi.searchtool_wi.utils.ResultSetMetaData;

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
    
    if (searchResults.size() == 0) {
		if (searchInput.getSearchString().matches("^[rR][sS][0-9]+$")) {
			for (OtherResult result : searchAccessionUrl(searchInput)) {
				searchResults.put(result.getAccId(), result);
			}
			timer.record("Other - fewi/accession/ Search Done");
		} else {
			timer.record("Bypassing fewi/accession/ Search for string (" + searchInput.getSearchString() + ")");
		}
    }

    return new ArrayList( searchResults.values() );
  }

  /**
   * For certain IDs (currently consensus SNP IDs), get data from the fewi's accession/ URL, since we
   * no longer index these IDs for the search tool.
   */
  private List<OtherResult> searchAccessionUrl(SearchInput searchInput) {
	String searchUrl = config.get("FEWI_URL") + "accession/json?id=" + searchInput.getSearchString();
	StringBuffer s = new StringBuffer();
	List<OtherResult> results = new ArrayList<OtherResult>();
	  
	// read JSON from searchUrl
	try {
	    URL url = new URL(searchUrl);
	    URLConnection urlConn = url.openConnection();
	    HttpURLConnection conn = null;

	    if (urlConn instanceof HttpURLConnection) {
	    	conn = (HttpURLConnection) urlConn;
	    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    	String current = in.readLine();
	    	while (current != null) {
	    		if (s.length() > 0) {
	    			s.append("\n");
	    		}
	    		s.append(current);
	    		current = in.readLine();
		    }
	    	in.close();
	    	conn.disconnect();
	    } else {
	    	timer.record("FEWI_URL is not an HTTP URL");
	    	return results;
	    }

	} catch (MalformedURLException mue) {
	    timer.record("MalformedURLException : " + mue.getMessage());
	    return results;
	} catch (IOException ioe) {
	    timer.record("IOException : " + ioe.getMessage());
	    return results;
	}

	// use jackson ObjectMapper to convert to JsonSummaryResponse
	ObjectMapper mapper = new ObjectMapper();
	try {
		JsonSummaryResponse response = (JsonSummaryResponse) mapper.readValue(s.toString(), JsonSummaryResponse.class);

		// iterate over AccessionSummaryRow objects and turn into OtherResult objects
		for (Object obj : response.getSummaryRows()) {
			AccessionSummaryRow row = (AccessionSummaryRow) obj;
			OtherResult result = new OtherResult(config);
			result.setAccId(row.getAccId());
			result.setDataType(row.getDisplayType());
			result.setDbKey(row.getAccId());
			result.setOptionalDescription(row.getDescription());

			OtherMatch match = new OtherMatch();
			match.setDataType(row.getDisplayType());
			match.setDbKey(row.getAccId());
			match.setAccKey(row.getAccId());
			match.setDisplayableType(row.getDisplayType());
			match.setMatchedText(searchInput.getSearchString());
			match.setSearchScore(1); 
			result.addExactMatch(match);

			results.add(result);
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
	return results;
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

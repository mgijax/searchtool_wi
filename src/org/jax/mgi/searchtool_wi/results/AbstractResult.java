package org.jax.mgi.searchtool_wi.results;

// standard java
import java.util.*;

// searchtool classes
import org.jax.mgi.searchtool_wi.matches.AbstractMatch;
import org.jax.mgi.searchtool_wi.matches.MatchSorter;

// MGI homegrown classes
import org.jax.mgi.shr.config.Configuration;

/**
* An AbstractResult is an uninstantiatable parent of concrete Results;
* Results represent resultant data types (markers, vocab, IDs) to be sent back
* to the user; it encapsulates the functionality and knowledge to be shared
* between all concrete results; whenever possible, code should be written to
* this general higher-level abstraction. Also see searchtool_wi wiki entry.
*/
public abstract class AbstractResult {

  // --------//
  // Fields
  // --------//

  // filled at the time the concrete class is instantiated
  protected Configuration config;

  // DB key of the result; all results have DB keys
  protected String dbKey;

  // best match (highest scoring) for this result
  protected AbstractMatch bestMatch;

  // to sort the matches
  protected MatchSorter matchSorter = new MatchSorter();


  // -------------//
  // Constructor //
  // -------------//
  public AbstractResult(Configuration c) {
    config = c;
  }

  // -----------------//
  // Abstract Methods
  // -----------------//

  /**
  * Derives and returns the score for the result
  */
  public abstract Float getScore();

  /**
  * Returns the best match for the result
  */
  public abstract AbstractMatch getBestMatch();

  /**
  * Returns the string for alpha numeric sorting of results.  This is used
  * if the scores (from getScore) are the same
  */
  public abstract String getAlphaSortBy();

  /**
  * Any one-time functionality that needs to happen prior to being sent to
  * the display layer should be defined here.  The method is called by the
  * search framework.
  */
  public abstract void finalizeResult();

  // ----------------//
  // DB Key Handling
  // ----------------//

  /**
  * Returns the database key of the object hit
  * @return String - the database key of object hit
  */
  public String getDbKey() {
    return dbKey;
  }
  /**
  * Set database key of the object hit
  * @param String - the database key of object hit
  */
  public void setDbKey(String s) {
    dbKey = s;
  }
  /**
  * Set database key of the object hit
  * @param Integer - the database key of object hit
  */
  public void setDbKey(Integer i) {
    dbKey = i.toString();
  }
  /**
  * Set database key of the object hit
  * @param int - the database key of object hit
  */
  public void setDbKey(int i) {
    Integer newI = new Integer(i);
    setDbKey(newI);
  }

  /**
  * Returns the result key of the object hit; normally, this is the DB key
  *    This may be overridden
  * @return String - the result key of object hit
  */
  public String getResultKey() {
    return dbKey;
  }

  // --------//
  // Display
  // --------//

  /**
  * Returns the HTML for rendering "Star" scores for this result
  * @return String
  */
  public String getStarScore() {

    String star = "<img src='" + config.get("QUICKSEARCH_URL")
      + "darkStarSmall.gif' width='9' height='8'>";

    if ( bestMatch.isTier1()) {
        return star + star + star + star;
    }
    else if ( bestMatch.isTier2()){
        return star + star + star;
    }
    else if ( bestMatch.isTier3()){
        return star  + star;
    }
    return star;
  }

  /**
  * Returns the HTML for rendering debug values for this result
  * @return String
  */
  public String getDebugDisplay() {

    String debugMsg = "";

    if ( bestMatch.isTier1()) {
        debugMsg += "Tier 1 <br>";
    }
    else if ( bestMatch.isTier2()){
        debugMsg += "Tier 2 <br>";
    }
    else if ( bestMatch.isTier3()){
        debugMsg += "Tier 3 <br>";
    }
    else {
        debugMsg += "Tier 4 <br>";
    }
    debugMsg += "db_key->&nbsp;" + this.getDbKey() + "<br/>";
    debugMsg += "score->&nbsp;" + this.getScore() + "<br/>";
    return debugMsg;
  }

}

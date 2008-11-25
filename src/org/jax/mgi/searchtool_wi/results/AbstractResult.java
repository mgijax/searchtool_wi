package org.jax.mgi.searchtool_wi.results;

// standard java
import java.util.*;

// searchtool classes
import org.jax.mgi.searchtool_wi.matches.AbstractMatch;
import org.jax.mgi.searchtool_wi.matches.MatchSorter;

// MGI homegrown classes
import org.jax.mgi.shr.config.Configuration;

/**
* Abstract class in which all concrete results will extend.
*/
public abstract class AbstractResult {

  // --------//
  // Fields
  // --------//

  // filled at the time the concrete class is instantiated
  protected Configuration config;

  // DB key of the result
  protected String dbKey;

  // best match
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

  public abstract Float getScore();

  public abstract AbstractMatch getBestMatch();

  public abstract String getAlphaSortBy();

  public abstract void finalizeResult();

  // ----------------//
  // DB Key Handling
  // ----------------//

  // DB Key accessors
  public String getDbKey() {
    return dbKey;
  }
  public void setDbKey(String s) {
    dbKey = s;
  }
  public void setDbKey(Integer i) {
    dbKey = i.toString();
  }
  public void setDbKey(int i) {
    Integer newI = new Integer(i);
    setDbKey(newI);
  }


  // --------//
  // Display
  // --------//

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

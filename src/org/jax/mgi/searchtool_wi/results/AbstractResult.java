package org.jax.mgi.searchtool_wi.results;

import java.util.*;

import org.jax.mgi.shr.config.Configuration;

/**
* Abstract class in which all concrete results will extend.
*/
public abstract class AbstractResult {

  // --------//
  // Fields
  // --------//

  // filled at the time the concrete class is instantiated
  Configuration config;

  // DB key of the result
  String dbKey;

  // flag indicating this result has a match to the entire user input string
  boolean hasExactInputStrMatch = false;
  boolean hasExactInputTokenMatch = false;

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

  // ----------------//
  // Content Flags
  // ----------------//

  public boolean hasExactInputStrMatch() {
    return hasExactInputStrMatch;
  }
  public void flagExactInputStrMatch() {
   hasExactInputStrMatch = true;
  }
  public boolean hasExactInputTokenMatch() {
    return hasExactInputTokenMatch;
  }
  public void flagExactInputTokenMatch() {
   hasExactInputTokenMatch = true;
  }

  // --------//
  // Display
  // --------//

  public String getStarScore() {
    Float score = getScore();
    String star = "<img src='" + config.get("QUICKSEARCH_URL")
      + "darkStarSmall.gif' width='9' height='8'>";

    if ( getScore() > 1000 ) {
        return star + star + star;
    }
    else if ( getScore() > 100 ){
        return star + star;
    }
    return star;
  }
}

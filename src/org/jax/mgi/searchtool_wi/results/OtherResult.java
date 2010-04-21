package org.jax.mgi.searchtool_wi.results;

// standard java
import java.util.*;

// lucene
import org.apache.lucene.search.*;

// MGI homegrown classes
import org.jax.mgi.shr.config.Configuration;

import org.jax.mgi.searchtool_wi.lookup.OtherDisplayLookup;
import org.jax.mgi.searchtool_wi.matches.AbstractMatch;
import org.jax.mgi.searchtool_wi.matches.MatchSorter;
import org.jax.mgi.searchtool_wi.matches.OtherMatch;

/**
* An OtherResult represents an ID object resulting from a textual match
* with the user's input string.
* See AbstractResult for additional implementation details, and Search Tool
* wiki documentation for design and usage patterns
*/
public class OtherResult extends AbstractResult {

  // uniquely identify this object
  private String accId;
  private String data_type;

  // exact matches to this vocab term
  private ArrayList<OtherMatch> exactMatches = new ArrayList<OtherMatch>();

  // best scores
  private Float derivedScore = new Float(0.0);
  private Float bestExactMatchScore = new Float(0.0);

  private Float term_score = new Float(0.0);
  private Float exact_score = new Float(0.0);
  private Boolean has_exact = false;

  // lookup for gathering display string, so we can order alpha-numerically
  private static OtherDisplayLookup otherDisplayLookup = OtherDisplayLookup.getOtherDisplayLookup();

  // -------------//
  // Constructor //
  // -------------//

  /**
  * Constructs the MarkerResult, calling the parent class constructor with config
  */
  public OtherResult(Configuration c) {
      super(c);
  }

  // ----------------------------//
  // Overriding parent abstracts
  // ----------------------------//

  /**
  * returns the score for the result
  * @return Float - the score of this result
  */
  public Float getScore() {
      return derivedScore;
  }

  /**
  * Returns the string for alpha numeric sorting of results.  This is used
  * if the scores (from getScore) are the same
  */
  public String getAlphaSortBy() {
      return getBestMatch().getMatchedText();
  }

  /**
  * returns the best match for the result
  * @return AbstractMatch - the best match
  */
  public AbstractMatch getBestMatch() {
    return bestMatch;
  }

  /**
  * Any one-time functionality that needs to happen prior to being sent to
  * the display layer should be defined here.  The method is called by the
  * search framework.
  */
  public void finalizeResult() {
      derivedScore = bestExactMatchScore;

      if (hasExact()){
          OtherMatch oem = exactMatches.get(0);
          bestMatch = oem;
      }
  }

  // -----------------//
  // Field Accessors
  // -----------------//

  /**
  * Returns the AccID of this match
  * @return String - AccID
  */
  public String getAccId() {
      return accId;
  }

  /**
  * Set AccID of this match
  * @param String - AccID
  */
  public void setAccId(String s) {
      accId = s;
  }

  /**
  * Returns the data type of this match
  * @return String - Data Type
  */
  public String getDataType() {
      return data_type;
  }

  /**
  * Set data type of this match
  * @param String - Data Type
  */
  public void setDataType(String s) {
      data_type = s;
  }

  // ----------------//
  // Exact Matches
  // ----------------//
  /**
  * Adds an Exact Match to this result
  * @param OtherMatch
  */
  public void addExactMatch(OtherMatch vem) {
      has_exact = true;
      exactMatches.add(vem);
  }

  /**
  * returns the exact matches
  * @return List of matches
  */
  public List getExactMatches() {
      return exactMatches;
  }

  /**
  * returns true if this result has an exact match
  * @return Boolean
  */
  public Boolean hasExact() {
      return has_exact;
  }

}

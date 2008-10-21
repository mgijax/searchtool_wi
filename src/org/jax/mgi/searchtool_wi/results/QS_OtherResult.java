package org.jax.mgi.searchtool_wi.results;

import java.util.*;

import org.jax.mgi.searchtool_wi.lookup.OtherDisplayLookup;
import org.jax.mgi.searchtool_wi.matches.AbstractMatch;
import org.jax.mgi.searchtool_wi.matches.MatchSorter;
import org.jax.mgi.searchtool_wi.matches.OtherMatch;
import org.jax.mgi.shr.config.Configuration;
import org.apache.lucene.search.Hit;

public class QS_OtherResult extends AbstractResult {

  // uniquely identify this object
  private String accId;
  private String data_type;

  // exact matches to this vocab term
  private ArrayList<OtherMatch> exactMatches = new ArrayList<OtherMatch>();

  // to sort the matches
  private MatchSorter matchSorter = new MatchSorter();

  // best scores
  private Float derivedScore = new Float(0.0);
  private Float bestExactMatchScore = new Float(0.0);

  private Float term_score = new Float(0.0);
  private Float exact_score = new Float(0.0);
  private Boolean has_exact = false;

  // best match
  private AbstractMatch bestMatch;

  // lookup for gathering display string, so we can order alpha-numerically
  private static OtherDisplayLookup otherDisplayLookup = OtherDisplayLookup.getOtherDisplayLookup();

  // -------------//
  // Constructor //
  // -------------//
  public QS_OtherResult(Configuration c) {
      super(c);
  }

  // ----------------------------//
  // Overriding parent abstracts
  // ----------------------------//

  // primary sort
  public Float getScore() {
      return derivedScore;
  }

  // secondary sort
  public String getAlphaSortBy() {
      return getBestMatch().getMatchedText();
  }

  // finalization tasks
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

  public String getAccId() {
      return accId;
  }

  public void setAccId(String s) {
      accId = s;
  }

  public String getDataType() {
      return data_type;
  }

  public void setDataType(String s) {
      data_type = s;
  }

  // ----------------//
  // Exact Matches
  // ----------------//
  public void addExactMatch(OtherMatch vem) {
      has_exact = true;
      exactMatches.add(vem);
  }

  public List getExactMatches() {
      return exactMatches;
  }

  public Boolean hasExact() {
      return has_exact;
  }

  // Best Match

  public AbstractMatch getBestMatch() {
      return bestMatch;
    }

}

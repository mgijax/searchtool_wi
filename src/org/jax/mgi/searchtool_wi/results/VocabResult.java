package org.jax.mgi.searchtool_wi.results;

// standard java
import java.util.*;

// lucene
import org.apache.lucene.search.*;

// MGI homegrown classes
import org.jax.mgi.shr.config.Configuration;

import org.jax.mgi.searchtool_wi.lookup.VocabDisplayCache;
import org.jax.mgi.searchtool_wi.matches.AbstractMatch;
import org.jax.mgi.searchtool_wi.matches.MatchSorter;
import org.jax.mgi.searchtool_wi.matches.VocabMatch;
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
* A VocabResult represents a vocabulary term resulting from a textual match
* with the user's input string.
* See AbstractResult for additional implementation details, and Search Tool
* wiki documentation for design and usage patterns
*/
public class VocabResult extends AbstractResult {

  // --------//
  // Fields
  // --------//

  private static VocabDisplayCache vocabDisplayCache
    = VocabDisplayCache.getVocabDisplayCache();

  // vocabulary from which this term originates
  private String vocabulary;

  // exact matches to this vocab term
  private ArrayList<VocabMatch> exactMatches = new ArrayList<VocabMatch>();

  // inexact matches to this vocab term
  private ArrayList<VocabMatch> inexactMatches = new ArrayList<VocabMatch>();

  // scoring
  private Float derivedScore = new Float(0.0);
  private Float bestExactMatchScore = new Float(0.0);
  private Float bestInexactScoreScore = new Float(0.0);
  private Float annotScore = new Float(0.0);
  private Float term_score = new Float(0.0);
  private Float exact_score = new Float(0.0);
  private Boolean has_annot = false;
  private Boolean has_exact = false;

  // -------------//
  // Constructor //
  // -------------//

  /**
  * Constructs the MarkerResult, calling the parent class constructor with config
  */
  public VocabResult(Configuration c) {
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
      return vocabDisplayCache.getVocab(this).getName();
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

    // sort our matches arrays, and save off highest score
    if (exactMatches.size() > 0) {
        Collections.sort(exactMatches, matchSorter);
        VocabMatch vem = (VocabMatch) exactMatches.get(0);
        bestExactMatchScore = vem.getScore();
        bestMatch = vem;
    }
    if (inexactMatches.size() > 0) {
        Collections.sort(inexactMatches, matchSorter);
        VocabMatch vm = (VocabMatch) inexactMatches.get(0);
        bestInexactScoreScore = vm.getScore();
        if (bestMatch == null) {
            bestMatch = vm;
        }
    }

    derivedScore = bestExactMatchScore + bestInexactScoreScore;

  }

  // ------------//
  // Vocabulary
  // ------------//

  /**
  * returns the vocabulary type
  * @return String
  */
  public String getVocabulary() {
    return vocabulary;
  }

  /**
  * Sets the vocabulary for this result
  * @param String - Vocab
  */
  public void setVocabulary(String s) {
      vocabulary = s;
  }

  // ----------------//
  // Exact Matches
  // ----------------//

  /**
  * Adds an Exact Match to this result
  * @param VocabMatch
  */
  public void addExactMatch(VocabMatch vem) {
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

  // -----------------//
  // Inexact Matches
  // -----------------//

  /**
  * Adds an Inexact Match to this result
  * @param VocabMatch
  */
  public void addInexactMatch(VocabMatch vm) {
    inexactMatches.add(vm);
  }

  /**
  * returns the inexact matches
  * @return List of matches
  */
  public List getInexactMatches() {
    return inexactMatches;
  }

}

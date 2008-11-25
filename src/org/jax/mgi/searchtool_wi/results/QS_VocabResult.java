package org.jax.mgi.searchtool_wi.results;

import java.util.*;

// logging
import org.apache.log4j.Logger;

import org.jax.mgi.searchtool_wi.lookup.MarkerVocabSearchCache;
import org.jax.mgi.searchtool_wi.lookup.VocabDisplayCache;
import org.jax.mgi.searchtool_wi.matches.AbstractMatch;
import org.jax.mgi.searchtool_wi.matches.MatchSorter;
import org.jax.mgi.searchtool_wi.matches.VocabMatch;
import org.jax.mgi.shr.config.Configuration;
import org.apache.lucene.search.Hit;
import org.jax.mgi.shr.searchtool.IndexConstants;

public class QS_VocabResult extends AbstractResult {

  // --------//
  // Fields
  // --------//

  private static Logger logger = Logger.getLogger(QS_VocabResult.class.getName());

  private static VocabDisplayCache vocabDisplayCache = VocabDisplayCache.getVocabDisplayCache();

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
  public QS_VocabResult(Configuration c) {
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
      return vocabDisplayCache.getVocab(this).getName();
  }

  // best match for this result
  public AbstractMatch getBestMatch() {
    return bestMatch;
  }

  // finalization tasks
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
  public String getVocabulary() {
      return vocabulary;
  }

  public void setVocabulary(String s) {
      vocabulary = s;
  }

  // ----------------//
  // Exact Matches
  // ----------------//
  public void addExactMatch(VocabMatch vem) {
      has_exact = true;
      exactMatches.add(vem);
  }

  public List getExactMatches() {
      return exactMatches;
  }

  public Boolean hasExact() {
      return has_exact;
  }

  // -----------------//
  // Inexact Matches
  // -----------------//
  public void addInexactMatch(VocabMatch vm) {
      inexactMatches.add(vm);
  }

  public List getInexactMatches() {
      return inexactMatches;
  }

}

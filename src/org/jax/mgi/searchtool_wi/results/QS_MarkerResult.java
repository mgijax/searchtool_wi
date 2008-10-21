package org.jax.mgi.searchtool_wi.results;

import java.util.*;

import org.apache.lucene.search.*;

import org.jax.mgi.searchtool_wi.lookup.MarkerDisplayCache;
import org.jax.mgi.searchtool_wi.lookup.MarkerVocabSearchCache;
import org.jax.mgi.searchtool_wi.matches.AbstractMatch;
import org.jax.mgi.searchtool_wi.matches.MarkerMatch;
import org.jax.mgi.searchtool_wi.matches.MatchSorter;
import org.jax.mgi.searchtool_wi.matches.MarkerMatch;
import org.jax.mgi.searchtool_wi.matches.MarkerVocabMatch;
import org.jax.mgi.shr.config.Configuration;

public class QS_MarkerResult extends AbstractResult {

  // -------//
  // Fields
  // -------//

  // number of total matches
  private int matchCount;

  // to sort the matches
  private MatchSorter matchSorter = new MatchSorter();

  // list of high-level types of matches
  private List allNomenMatches;
  private List allVocMatches;

  // lists of specific types of matches
  private ArrayList<MarkerMatch> exactMarkerMatches
    = new ArrayList<MarkerMatch>();
  private  ArrayList<MarkerMatch> nomenMatches
    = new ArrayList<MarkerMatch>();
  private ArrayList<MarkerVocabMatch> adMatches
    = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> goMatches
    = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> mpMatches
    = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> omimMatches
    = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> omimOrthoMatches
    = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> pirsfMatches
    = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> ipMatches
    = new ArrayList<MarkerVocabMatch>();

  // scores
  private Float derivedScore    = new Float(0.0);
  private Float bestVocabScore  = new Float(0.0);
  private Float bestNomenScore  = new Float(0.0);

  // best match
  private AbstractMatch bestMatch;
  private MarkerVocabMatch bestVocabMatch;
  private MarkerMatch bestNomenMatch;

  // data lookups
  private static MarkerVocabSearchCache markerVocabSearchCache
    = MarkerVocabSearchCache.getMarkerVocabSearchCache();
  private static MarkerDisplayCache markerDisplayCache
    = MarkerDisplayCache.getMarkerDisplayCache();

  // holds unique key values for matches; used to avoid dupe hits across
  // different indexes (exact/inexact)
  private HashSet handledNomenMatches   = new HashSet();

  // -------------//
  // Constructor //
  // -------------//
  public QS_MarkerResult(Configuration c) {
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
    return markerDisplayCache.getMarker(this).getSymbol().toLowerCase();
  }

  // finalization tasks
  public void finalizeResult() {

    // sort our match arrays
    if (exactMarkerMatches.size() > 0) {
        Collections.sort(exactMarkerMatches, matchSorter);
    }
    if (nomenMatches.size() > 0) {
        Collections.sort(nomenMatches, matchSorter);
    }
    if (adMatches.size() > 0) {
        Collections.sort(adMatches, matchSorter);
    }
    if (goMatches.size() > 0) {
        Collections.sort(goMatches, matchSorter);
    }
    if (omimMatches.size() > 0) {
        Collections.sort(omimMatches, matchSorter);
    }
    if (omimOrthoMatches.size() > 0) {
        Collections.sort(omimOrthoMatches, matchSorter);
    }
    if (mpMatches.size() > 0) {
        Collections.sort(mpMatches, matchSorter);
    }
    if (pirsfMatches.size() > 0) {
        Collections.sort(pirsfMatches, matchSorter);
    }
    if (ipMatches.size() > 0) {
        Collections.sort(ipMatches, matchSorter);
    }

    // Best vocab match
    allVocMatches = getAllMarkerVocabMatches();
    if (allVocMatches.size() > 0) {
        bestVocabMatch = (MarkerVocabMatch)allVocMatches.get(0);
        bestVocabScore = bestVocabMatch.getScore();
    }

    // Best Nomen match
    allNomenMatches = getAllMarkerNomenMatches();
    if (allNomenMatches.size() > 0) {
        bestNomenMatch = (MarkerMatch)allNomenMatches.get(0);
        bestNomenScore = bestNomenMatch.getScore();
    }

    // Best overall match (prefer nomen over vocab if tied)
    if ( bestVocabScore > bestNomenScore ) {
        bestMatch = bestVocabMatch;
    }else {
        bestMatch = bestNomenMatch;
    }

    // derive the score for this result object
    if ( this.hasExactInputStrMatch() ){
        derivedScore = new Float(200000); // Exact match to entire input
    }
    else if ( this.hasExactInputTokenMatch() ){
        derivedScore = new Float(100000); // Exact to space-delin user input
    }
    else{  // if no exact matches, than score by best match
        derivedScore = bestMatch.getScore();
    }

    matchCount = getAllMarkerVocabMatches().size()
               + getAllMarkerNomenMatches().size();

  }

  // -----------//
  // Best Match
  // -----------//
  public AbstractMatch getBestMatch() {
    return bestMatch;
  }


  // ----------------//
  // Exact Matches
  // ----------------//

  public void addExactMatch(MarkerMatch mem) {
    exactMarkerMatches.add(mem);
    handledNomenMatches.add( mem.getUniqueKey() );
  }

  public List getExactMatches() {
    return exactMarkerMatches;
  }

  public boolean hasExactMatches() {
    boolean b = false;
    if (exactMarkerMatches.size() > 0) {
        b = true;
    }
    return b;
  }

  // ----------------//
  // Marker Matches
  // ----------------//

  public void addNomenMatch(MarkerMatch nm) {
    if (!handledNomenMatches.contains( nm.getUniqueKey() ) ) {
        nomenMatches.add(nm);
    }
  }

  public List getNomenMatches() {
    return nomenMatches;
  }

  public boolean hasMarkerMatches() {
    boolean b = false;
    if (nomenMatches.size() > 0) {
        b = true;
    }
    return b;
  }

  // ---------------//
  // Vocab Matches
  // ---------------//

  // AD
  public void addAdMatch(MarkerVocabMatch vm) {
    adMatches.add(vm);
  }
  public List getAdMatches() {
    return adMatches;
  }

  // MP
  public void addMpMatch(MarkerVocabMatch vm) {
    mpMatches.add(vm);
  }
  public List getMpMatches() {
    return mpMatches;
  }

  // GO
  public void addGoMatch(MarkerVocabMatch vm) {
    goMatches.add(vm);
  }
  public List getGoMatches() {
    return goMatches;
  }

  // OMIM
  public void addOmimMatch(MarkerVocabMatch vm) {
    omimMatches.add(vm);
  }
  public List getOmimMatches() {
    return omimMatches;
  }

  // OMIM Ortho
  public void addOmimOrthoMatch(MarkerVocabMatch vm) {
    omimOrthoMatches.add(vm);
  }
  public List getOmimOrthoMatches() {
    return omimOrthoMatches;
  }

  // PIRSF
  public void addPirsfMatch(MarkerVocabMatch vm) {
    pirsfMatches.add(vm);
  }
  public List getPirsfMatches() {
    return pirsfMatches;
  }

  // InterProt
  public void addIpMatch(MarkerVocabMatch vm) {
    ipMatches.add(vm);
  }
  public List getIpMatches() {
    return ipMatches;
  }

  // ---------------------------//
  // Group retrieval of matches
  // ---------------------------//

  public List getAllMarkerVocabMatches() {
    if (allVocMatches == null) {
      allVocMatches = new ArrayList();
      allVocMatches.addAll(adMatches);
      allVocMatches.addAll(mpMatches);
      allVocMatches.addAll(goMatches);
      allVocMatches.addAll(omimMatches);
      allVocMatches.addAll(omimOrthoMatches);
      allVocMatches.addAll(pirsfMatches);
      allVocMatches.addAll(ipMatches);
      Collections.sort(allVocMatches, matchSorter);
    }
    return allVocMatches;
  }

  public List getAllMarkerNomenMatches() {
    if (allNomenMatches == null) {
      allNomenMatches = new ArrayList();
      allNomenMatches.addAll(exactMarkerMatches);
      allNomenMatches.addAll(nomenMatches);
      Collections.sort(allNomenMatches, matchSorter);
    }
    return allNomenMatches;
  }

  public int getMatchCount () {
    return matchCount;
  }

}

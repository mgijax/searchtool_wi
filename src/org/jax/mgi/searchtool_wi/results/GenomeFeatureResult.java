package org.jax.mgi.searchtool_wi.results;

// standard java
import java.util.*;

// lucene
import org.apache.lucene.search.*;

// MGI homegrown classes
import org.jax.mgi.shr.config.Configuration;

// searchtool classes
import org.jax.mgi.searchtool_wi.lookup.GenomeFeatureDisplayCache;
import org.jax.mgi.searchtool_wi.matches.AbstractMatch;
import org.jax.mgi.searchtool_wi.matches.MatchSorter;
import org.jax.mgi.searchtool_wi.matches.MarkerNomenMatch;
import org.jax.mgi.searchtool_wi.matches.AlleleNomenMatch;
import org.jax.mgi.searchtool_wi.matches.MarkerVocabMatch;
import org.jax.mgi.searchtool_wi.utils.ScoreConstants;

/**
* A MarkerResult represents a marker object resulting from a textual match
* with the user's input string.
* See AbstractResult for additional implementation details, and Search Tool
* wiki documentation for design and usage patterns
*/
public class GenomeFeatureResult extends AbstractResult {

  // -------//
  // Fields
  // -------//

  // type of genomic feature (marker, allele, etc...)
  private String genomeFeatureType = "";

  // number of total matches
  private int matchCount;

  // list of high-level types of matches
  private List allNomenMatches;
  private List allVocMatches;

  // lists of specific types of matches
  private ArrayList<MarkerNomenMatch> markerNomenMatches = new ArrayList<MarkerNomenMatch>();
  private ArrayList<AlleleNomenMatch> alleleNomenMatches = new ArrayList<AlleleNomenMatch>();
  private ArrayList<MarkerVocabMatch> adMatches = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> goMatches = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> mpMatches = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> omimMatches = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> omimOrthoMatches = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> pirsfMatches = new ArrayList<MarkerVocabMatch>();
  private ArrayList<MarkerVocabMatch> ipMatches = new ArrayList<MarkerVocabMatch>();

  // scores
  private Float derivedScore    = new Float(0.0);
  private Float bestVocabScore  = new Float(0.0);
  private Float bestNomenScore  = new Float(0.0);
  private Map   resultBoostMap  = ScoreConstants.getMarkerResultBoostMap();

  // matches
  private AbstractMatch bestVocabMatch;
  private AbstractMatch bestNomenMatch;

  // data lookups
  private static GenomeFeatureDisplayCache gfDisplayCache
    = GenomeFeatureDisplayCache.getGenomeFeatureDisplayCache();

  // holds unique key values for matches; used to avoid dupe hits across
  // different indexes (exact/inexact)
  private HashSet handledMarkerNomenMatches   = new HashSet();
  private HashSet handledAlleleNomenMatches   = new HashSet();

  // -------------//
  // Constructor //
  // -------------//

  /**
  * Constructs the MarkerResult, calling the parent class constructor with config
  */
  public GenomeFeatureResult(Configuration c) {
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
    return gfDisplayCache.getGenomeFeature(this).getSymbol().toLowerCase();
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

    // sort our match arrays
    if (markerNomenMatches.size() > 0) {
        Collections.sort(markerNomenMatches, matchSorter);
    }
    if (alleleNomenMatches.size() > 0) {
        Collections.sort(alleleNomenMatches, matchSorter);
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
        bestNomenMatch = (AbstractMatch)allNomenMatches.get(0);
        bestNomenScore = bestNomenMatch.getScore();
    }

    // Best overall match (prefer nomen over vocab if tied)
    if ( bestVocabScore > bestNomenScore ) {
        bestMatch = bestVocabMatch;
    }else {
        bestMatch = bestNomenMatch;
    }

    // match count
    matchCount = getAllMarkerVocabMatches().size()
               + getAllMarkerNomenMatches().size();

    // derive the score for this result object
    if ( this.bestMatch.isTier1() ){
        derivedScore = new Float(100000);
        derivedScore += getBestMatchTypeBoost(); //flat type boost
    }
    else if ( this.bestMatch.isTier2() ){
        derivedScore = new Float(10000);
        derivedScore += this.bestMatch.getScore(); //use score of best match
    }
    else if ( this.bestMatch.isTier3() ){
        derivedScore = new Float(1000);
        derivedScore += getBestMatchTypeBoost(); //flat type boost
    }
    else{  // if no exact matches, than score by best match
        derivedScore = bestMatch.getScore();
    }
  }

  // -------------------//
  // Genome Feature Type
  // -------------------//

  public void setGenomeFeatureType(String s) {
    genomeFeatureType = s;
  }
  public String getGenomeFeatureType() {
    return genomeFeatureType;
  }

  public boolean isMarker() {
    boolean b = false;
    if (genomeFeatureType.equals("MARKER")) {
        b = true;
    }
    return b;
  }
  public boolean isAllele() {
    boolean b = false;
    if (genomeFeatureType.equals("ALLELE")) {
        b = true;
    }
    return b;
  }

  // ----------//
  // Cache key
  // ----------//
  public String getCacheKey() {
    return genomeFeatureType + "_" + dbKey;
  }

  /**
  * Returns the result key of the object hit; overridding method in Abstract
  * @return String - the result key of object hit
  */
  public String getResultKey() {
    return getCacheKey();
  }

  // ----------------//
  // Nomen Matches
  // ----------------//

  /**
  * Adds a nomen match to this result
  * @param MarkerNomenMatch
  */
  public void addMarkerNomenMatch(MarkerNomenMatch nm) {
    if (!handledMarkerNomenMatches.contains( nm.getUniqueKey() ) ) {
        markerNomenMatches.add(nm);
        handledMarkerNomenMatches.add( nm.getUniqueKey() );
    }
  }

  /**
  * Adds a nomen match to this result
  * @param AlleleNomenMatch
  */
  public void addAlleleNomenMatch(AlleleNomenMatch nm) {
    if (!handledAlleleNomenMatches.contains( nm.getUniqueKey() ) ) {
        alleleNomenMatches.add(nm);
        handledAlleleNomenMatches.add( nm.getUniqueKey() );
    }
  }

  /**
  * Returns the nomen matches of this result
  * @return List of Match Objects
  */
  public List getNomenMatches() {
    return markerNomenMatches;
  }

  /**
  * returns true if this result has nomen matches
  * @return Boolean
  */
  public boolean hasMarkerMatches() {
    boolean b = false;
    if (markerNomenMatches.size() > 0) {
        b = true;
    }
    return b;
  }

  // ---------------//
  // Vocab Matches
  // ---------------//

  /**
  * Adds a AD match to this result
  * @param MarkerVocabMatch
  */
  public void addAdMatch(MarkerVocabMatch vm) {
    adMatches.add(vm);
  }
  /**
  * Returns AD Matches
  * @return List of Match Objects
  */
  public List getAdMatches() {
    return adMatches;
  }

  /**
  * Adds a MP match to this result
  * @param MarkerVocabMatch
  */
  public void addMpMatch(MarkerVocabMatch vm) {
    mpMatches.add(vm);
  }
  /**
  * Returns MP Matches
  * @return List of Match Objects
  */
  public List getMpMatches() {
    return mpMatches;
  }

  /**
  * Adds a GO match to this result
  * @param MarkerVocabMatch
  */
  public void addGoMatch(MarkerVocabMatch vm) {
    goMatches.add(vm);
  }
  /**
  * Returns GO Matches
  * @return List of Match Objects
  */
  public List getGoMatches() {
    return goMatches;
  }

  /**
  * Adds a OMIM match to this result
  * @param MarkerVocabMatch
  */
  public void addOmimMatch(MarkerVocabMatch vm) {
    omimMatches.add(vm);
  }
  /**
  * Returns OMIMMatches
  * @return List of Match Objects
  */
  public List getOmimMatches() {
    return omimMatches;
  }

  /**
  * Adds a OMIM Ortho match to this result
  * @param MarkerVocabMatch
  */
  public void addOmimOrthoMatch(MarkerVocabMatch vm) {
    omimOrthoMatches.add(vm);
  }
  /**
  * Returns OMIM Ortho Matches
  * @return List of Match Objects
  */
  public List getOmimOrthoMatches() {
    return omimOrthoMatches;
  }

  /**
  * Adds a PIRSF match to this result
  * @param MarkerVocabMatch
  */
  public void addPirsfMatch(MarkerVocabMatch vm) {
    pirsfMatches.add(vm);
  }
  /**
  * Returns PIRSF Matches
  * @return List of Match Objects
  */
  public List getPirsfMatches() {
    return pirsfMatches;
  }

  /**
  * Adds a InterPro match to this result
  * @param MarkerVocabMatch
  */
  public void addIpMatch(MarkerVocabMatch vm) {
    ipMatches.add(vm);
  }
  /**
  * Returns InterPro Matches
  * @return List of Match Objects
  */
  public List getIpMatches() {
    return ipMatches;
  }

  // -------------------------//
  // Best Match Type Boosting
  // -------------------------//

  /**
  * Returns the boost for this match; some results need to be boosted
  * depending on the type of the best match
  */
  public Float getBestMatchTypeBoost()
  {
    Float matchTypeBoost = new Float(0.0);
    String matchType = getBestMatch().getDataType();
    if (resultBoostMap.containsKey(matchType)) {
        matchTypeBoost = (Float)resultBoostMap.get(matchType);
    }
    return matchTypeBoost;
  }

  // ---------------------------//
  // Group retrieval of matches
  // ---------------------------//

  /**
  * Returns all marker vocabulary matches
  * @return List of Match Objects
  */
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

  /**
  * Returns all nomenclature matches
  * @return List of Match Objects
  */
  public List getAllMarkerNomenMatches() {
    if (allNomenMatches == null) {
      allNomenMatches = new ArrayList();
      allNomenMatches.addAll(markerNomenMatches);
      allNomenMatches.addAll(alleleNomenMatches);
      Collections.sort(allNomenMatches, matchSorter);
    }
    return allNomenMatches;
  }

  /**
  * Returns the number of matches in this result object
  * @return int - match count
  */
  public int getMatchCount () {
    return matchCount;
  }

}

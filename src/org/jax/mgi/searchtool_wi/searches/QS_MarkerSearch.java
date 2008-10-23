package org.jax.mgi.searchtool_wi.searches;

// Standard Java Classes
import java.util.*;
import java.io.IOException;

// Logging
import org.apache.log4j.Logger;

// Lucene Classes
import org.apache.lucene.search.*;

import org.jax.mgi.searchtool_wi.lookup.MarkerVocabSearchCache;
import org.jax.mgi.searchtool_wi.matches.MatchTypeScorer;
import org.jax.mgi.searchtool_wi.matches.MarkerMatch;
import org.jax.mgi.searchtool_wi.matches.MarkerMatchFactory;
import org.jax.mgi.searchtool_wi.matches.MarkerVocabMatch;
import org.jax.mgi.searchtool_wi.matches.MarkerVocabMatchFactory;
import org.jax.mgi.searchtool_wi.results.QS_MarkerResult;
import org.jax.mgi.searchtool_wi.utils.SearchHelper;
import org.jax.mgi.searchtool_wi.utils.SearchInput;
import org.jax.mgi.searchtool_wi.utils.ScoreConstants;
import org.jax.mgi.shr.config.Configuration;
import org.jax.mgi.shr.timing.TimeStamper;

import org.jax.mgi.shr.searchtool.IndexConstants;

/**
* This concrete Search is responsible for gathering all data required for
* the Quick Search to display the "Marker Bucket"
*/
public class QS_MarkerSearch extends AbstractSearch
{
  //--------//
  // Fields
  //--------//

  // logger
  private static Logger logger = Logger.getLogger(QS_MarkerSearch.class.getName());

  // search cache; will get singleton reference in constructor
  private MarkerVocabSearchCache markerVocabSearchCache;

  // results to be returned
  private HashMap searchResults = new HashMap();

  // holds matches as they're generated
  private HashSet exactInputStrMatches      = new HashSet();
  private HashSet exactNomenMatches         = new HashSet();
  private HashSet markerNomenMatches        = new HashSet();
  private HashSet adMatches                 = new HashSet();
  private HashSet goMatches                 = new HashSet();
  private HashSet omimMatches               = new HashSet();
  private HashSet omimOrthoMatches          = new HashSet();
  private HashSet mpMatches                 = new HashSet();
  private HashSet pirsfMatches              = new HashSet();
  private HashSet ipMatches                 = new HashSet();

  // holds vocab terms already handled; If we hit a term multiple ways,
  // we need only handle the best (first)
  private HashSet handledAdTerms            = new HashSet();
  private HashSet handledMpTerms            = new HashSet();
  private HashSet handledGoTerms            = new HashSet();
  private HashSet handledOmimTerms          = new HashSet();
  private HashSet handledOmimOrthoTerms     = new HashSet();
  private HashSet handledPirsfTerms         = new HashSet();
  private HashSet handledIpTerms            = new HashSet();

  // both the "and" and the "or" search use the same index; if the have
  // an "and" hit, do not keep the "or" hit for the same document
  private HashSet handledDocIDs = new HashSet();

  // Match factories we'll need generate the matches
  MarkerMatchFactory markerMatchFactory
    = new MarkerMatchFactory(config);
  MarkerVocabMatchFactory markerVocabMatchFactory
    = new MarkerVocabMatchFactory(config);

  // Match Type Scorers we'll need to score the matches

  MatchTypeScorer markerExactTypeScorer =
    new MatchTypeScorer(ScoreConstants.getMarkerExactScoreMap() );
  MatchTypeScorer markerAndTypeScorer =
    new MatchTypeScorer(ScoreConstants.getMrkAndScoreMap() );
  MatchTypeScorer markerOrTypeScorer =
    new MatchTypeScorer(ScoreConstants.getMrkOrScoreMap() );
  MatchTypeScorer markerOrWeightTypeScorer =
    new MatchTypeScorer(ScoreConstants.getMrkOrWeightMap() );


  //-------------//
  // Constructor //
  //-------------//
  public QS_MarkerSearch(Configuration c)
  {
    super(c);

    markerVocabSearchCache =
      MarkerVocabSearchCache.getMarkerVocabSearchCache();
  }

  //-----------------------------//
  // Over-ridden Abstract Method //
  //-----------------------------//

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

    timer.record("---Marker gatherData Started---");

    // exact nomenclature matches
    searchExactMatches(searchInput);
    timer.record("Marker - Done searching Exact");

    // exact vocabulary matches
    searchVocabExactMatches(searchInput);
    timer.record("Marker - Done searching Vocab Exact");

    // 'and' matches
    searchAndMatches(searchInput);
    timer.record("Marker - Done searching 'and' matches");

    // 'or' matches
    searchOrMatches(searchInput);
    timer.record("Marker - Done searching 'or' matches");

    // assign matches
    assignMatches();
    timer.record("Marker - Done Assigning Matches");

    return new ArrayList( searchResults.values() );
  }

  //---------------------------------------------------------- Private Methods

  //---------------------//
  // Search Exact Matches
  //---------------------//
  private void searchExactMatches (SearchInput searchInput)
    throws Exception
  {

    Hit hit;
    Hits hits;
    String markerID;

    // search for matches of entire user input string
    hits =  indexAccessor.searchMarkerExact(searchInput);
    logger.debug("MarkerSearch.searchExactMatches number of hits ->"
        + hits.length());
    for (Iterator hitIter = hits.iterator(); hitIter.hasNext();)
    {
        // get the hit, and build a match
        hit = (Hit) hitIter.next();
        handleExactInputStrHit(hit);
    }

    // search each token against marker names/synonyms
    List nameSynonymHits =  indexAccessor.searchMarkerExactByBigToken(searchInput);
    logger.debug("MarkerSearch.searchMarkerExactByBigToken ->"
        + nameSynonymHits.size());
    for (Iterator hitIter = nameSynonymHits.iterator(); hitIter.hasNext();)
    {
        // get the hit, and build a match
        hit = (Hit) hitIter.next();
        handleExactNomenHit(hit);
    }

    // search each token against marker IDs
    List idHits =  indexAccessor.searchMarkerAccID(searchInput);
    logger.debug("MarkerSearch.searchMarkerAccID ->"
        + idHits.size());
    for (Iterator hitIter = idHits.iterator(); hitIter.hasNext();)
    {
        // get the hit, and build a match
        hit = (Hit) hitIter.next();
        handleExactNomenHit(hit);
    }

    // search each token against marker symbols
    List symbolHits =  indexAccessor.searchMarkerSymbolExact(searchInput);
    logger.debug("MarkerSearch.searchMarkerSymbolExact ->"
        + symbolHits.size());
    for (Iterator hitIter = symbolHits.iterator(); hitIter.hasNext();)
    {
        // get the hit, and build a match
        hit = (Hit) hitIter.next();
        handleExactNomenHit(hit);
    }

    return;
  }

  private void handleExactInputStrHit (Hit hit)
    throws IOException
  {
    MarkerMatch mem = markerMatchFactory.getMatch(hit);
    exactInputStrMatches.add(mem);
    markerExactTypeScorer.addScore(mem);
    return;
  }

  private void handleExactNomenHit (Hit hit)
    throws IOException
  {
    MarkerMatch mem = markerMatchFactory.getMatch(hit);
    exactNomenMatches.add(mem);
    markerExactTypeScorer.addScore(mem);
    return;
  }

  //----------------------------//
  // Search Exact Vocab Matches
  //----------------------------//
  private void searchVocabExactMatches (SearchInput searchInput)
    throws Exception
  {

    Hit hit;
    Hits hits;
    String vocabID;

    hits =  indexAccessor.searchMarkerVocabExact(searchInput);
    logger.debug("searchVocabExactMatches searchMarkerVocabExact ->" + hits.length());
    for (Iterator iter = hits.iterator(); iter.hasNext();)
    {
        hit = (Hit) iter.next();
        handleVocabExactHit(hit);
    }

    // search each token against vocab IDs
    List idHits =  indexAccessor.searchMarkerVocabAccID(searchInput);
    logger.debug("searchVocabExactMatches searchMarkerVocabAccID ->"
        + idHits.size());
    for (Iterator hitIter = idHits.iterator(); hitIter.hasNext();)
    {
        // get the hit, and build a match
        hit = (Hit) hitIter.next();
        handleVocabExactHit(hit);
    }

    return;
  }

  private void handleVocabExactHit (Hit hit)
    throws Exception
  {
    MarkerVocabMatch markerVocabMatch;
    List childIDs;
    String childTermKey;

    // anatomical dictionary hit
    if ( SearchHelper.isAD(hit.get(IndexConstants.COL_VOCABULARY)) ) {

      //ensure we haven't already done this term
      if (!handledAdTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
        markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
        markerVocabMatch.addScore(ScoreConstants.VOC_EXACT_BOOST);
        adMatches.add(markerVocabMatch);
        handledAdTerms.add( markerVocabMatch.getDbKey() );

        //children of this term;
        childIDs = markerVocabSearchCache.getAdChildTerms(hit.get(IndexConstants.COL_DB_KEY));
        if (childIDs != null) {
          for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
            childTermKey = (String)childIter.next();
            if ( !handledAdTerms.contains(childTermKey) ) {
              markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
              markerVocabMatch.addScore(1000);

              // change db key to reflect the 'down-dag' term we're dealing
              // with, and penalize the scores of these, slightly
              markerVocabMatch.setDbKey(childTermKey);
              markerVocabMatch.addScore(-0.0001);

              adMatches.add(markerVocabMatch);
              handledAdTerms.add( markerVocabMatch.getDbKey() );
            }
          }
        }
      }
    }

    // gene ontology hit
    else if ( SearchHelper.isGO(hit.get(IndexConstants.COL_VOCABULARY)) ) {

      //ensure we haven't already done this term
      if (!handledGoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
        markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
        markerVocabMatch.addScore(1000);
        goMatches.add(markerVocabMatch);
        handledGoTerms.add( markerVocabMatch.getDbKey() );

        //children of this term;
        childIDs = markerVocabSearchCache.getGoChildTerms(hit.get(IndexConstants.COL_DB_KEY));
        if (childIDs != null) {
          for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
            childTermKey = (String)childIter.next();
            if ( !handledGoTerms.contains(childTermKey) ) {
              markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
              markerVocabMatch.addScore(1000);

              // change db key to reflect the 'down-dag' term we're dealing
              // with, and penalize the scores of these, slightly
              markerVocabMatch.setDbKey(childTermKey);
              markerVocabMatch.addScore(-0.0001);

              goMatches.add(markerVocabMatch);
              handledGoTerms.add( markerVocabMatch.getDbKey() );
            }
          }
        }
      }
    }

    // mammalian phenotype hit
    else if ( SearchHelper.isMP(hit.get(IndexConstants.COL_VOCABULARY)) ) {

      //ensure we haven't already done this term
      if (!handledMpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
        markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
        markerVocabMatch.addScore(1000);
        mpMatches.add(markerVocabMatch);
        handledMpTerms.add( markerVocabMatch.getDbKey() );

        //children of this term;
        childIDs = markerVocabSearchCache.getMpChildTerms(hit.get(IndexConstants.COL_DB_KEY));
        if (childIDs != null) {
          for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
            childTermKey = (String)childIter.next();
            if ( !handledMpTerms.contains(childTermKey) ) {
              markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
              markerVocabMatch.addScore(1000);

              // change db key to reflect the 'down-dag' term we're dealing
              // with, and penalize the scores of these, slightly
              markerVocabMatch.setDbKey(childTermKey);
              markerVocabMatch.addScore(-0.0001);

              mpMatches.add(markerVocabMatch);
              handledMpTerms.add( markerVocabMatch.getDbKey() );
            }
          }
        }
      }
    }

    // OMIM hit (no chasing down the dag)
    else if ( SearchHelper.isOMIM(hit.get(IndexConstants.COL_VOCABULARY)) ) {

      //ensure we haven't already done this term
      if (!handledOmimTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
        markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
        markerVocabMatch.addScore(1000);
        omimMatches.add(markerVocabMatch);
        handledOmimTerms.add( markerVocabMatch.getDbKey() );

      }
    }

    // OMIM ORTHO hit (no chasing down the dag)
    else if ( SearchHelper.isOMIMORTHO(hit.get(IndexConstants.COL_VOCABULARY)) ) {
      //ensure we haven't already done this term
      if (!handledOmimOrthoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
        markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
        markerVocabMatch.addScore(1000);
        omimOrthoMatches.add(markerVocabMatch);
        handledOmimOrthoTerms.add( markerVocabMatch.getDbKey() );

      }
    }

    // PIRSF hit (no chasing down the dag)
    else if ( SearchHelper.isPIRSF(hit.get(IndexConstants.COL_VOCABULARY)) ) {

      //ensure we haven't already done this term
      if (!handledPirsfTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
        markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
        markerVocabMatch.addScore(1000);
        pirsfMatches.add(markerVocabMatch);
        handledPirsfTerms.add( markerVocabMatch.getDbKey() );

      }
    }

    // Interprot hit (no chasing down the dag)
    else if ( SearchHelper.isIP(hit.get(IndexConstants.COL_VOCABULARY)) ) {

      //ensure we haven't already done this term
      if (!handledIpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
        markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
        markerVocabMatch.addScore(1000);
        ipMatches.add(markerVocabMatch);
        handledIpTerms.add( markerVocabMatch.getDbKey() );

      }
    }

    else { // shouldn't be hitting this part of the code, so log value
      logger.error("***Bad Object Type in Exact Vocab MarkerSearch ->"
        + hit.get(IndexConstants.COL_VOCABULARY) );
    }
  }


  //-------------------------//
  // Search for 'AND' matches
  //-------------------------//
  private void searchAndMatches (SearchInput searchInput)
    throws Exception
  {

    // defined outside loops to avoid repeated instantiation of references
    Hit hit;
    String termKey;
    MarkerMatch markerMatch;
    MarkerVocabMatch markerVocabMatch;
    List childIDs;
    String childTermKey;

    // execute the search, and handle each textual match
    Hits hits =  indexAccessor.searchMarkerAnd(searchInput);
    logger.debug("MarkerSearch.searchAndMatches number of hits ->" + hits.length());

    // examine each hit
    for (Iterator iter = hits.iterator(); iter.hasNext();) {

      hit = (Hit) iter.next();

      // mark this doc ID as having been handled; will skip in 'or' search
      handledDocIDs.add( hit.getId() );

      // marker nomen
      if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.MARKER_TYPE_NAME)) {
          markerMatch = markerMatchFactory.getMatch(hit);
          markerAndTypeScorer.addScore(markerMatch);
          markerNomenMatches.add(markerMatch);
      }

      // anatomical dictionary hit
      else if ( SearchHelper.isAD(hit) ) {

        //ensure we haven't already done this term
        if (!handledAdTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
          markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
          markerVocabMatch.addScore(100);
          adMatches.add(markerVocabMatch);
          handledAdTerms.add( markerVocabMatch.getDbKey() );

          //children of this term;
          childIDs = markerVocabSearchCache.getAdChildTerms(hit.get(IndexConstants.COL_DB_KEY));
          if (childIDs != null) {
            for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
              childTermKey = (String)childIter.next();
              if ( !handledAdTerms.contains(childTermKey) ) {
                markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
                markerVocabMatch.addScore(100);

                // change db key to reflect the 'down-dag' term we're dealing
                // with, and penalize the scores of these, slightly
                markerVocabMatch.setDbKey(childTermKey);
                markerVocabMatch.addScore(-0.0001);

                adMatches.add(markerVocabMatch);
                handledAdTerms.add( markerVocabMatch.getDbKey() );
              }
            }
          }
        }
      }

      // gene ontology hit
      else if ( SearchHelper.isGO(hit) ) {

        //ensure we haven't already done this term
        if (!handledGoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
          markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
          markerVocabMatch.addScore(100);
          goMatches.add(markerVocabMatch);
          handledGoTerms.add( markerVocabMatch.getDbKey() );

          //children of this term;
          childIDs = markerVocabSearchCache.getGoChildTerms(hit.get(IndexConstants.COL_DB_KEY));
          if (childIDs != null) {
            for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
              childTermKey = (String)childIter.next();
              if ( !handledGoTerms.contains(childTermKey) ) {
                markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
                markerVocabMatch.addScore(100);

                // change db key to reflect the 'down-dag' term we're dealing
                // with, and penalize the scores of these, slightly
                markerVocabMatch.setDbKey(childTermKey);
                markerVocabMatch.addScore(-0.0001);

                goMatches.add(markerVocabMatch);
                handledGoTerms.add( markerVocabMatch.getDbKey() );
              }
            }
          }
        }
      }

      // mammalian phenotype hit
      else if ( SearchHelper.isMP(hit) ) {

        //ensure we haven't already done this term
        if (!handledMpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
          markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
          markerVocabMatch.addScore(100);
          mpMatches.add(markerVocabMatch);
          handledMpTerms.add( markerVocabMatch.getDbKey() );

          //children of this term;
          childIDs = markerVocabSearchCache.getMpChildTerms(hit.get(IndexConstants.COL_DB_KEY));
          if (childIDs != null) {
            for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
              childTermKey = (String)childIter.next();
              if ( !handledMpTerms.contains(childTermKey) ) {
                markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
                markerVocabMatch.addScore(100);

                // change db key to reflect the 'down-dag' term we're dealing
                // with, and penalize the scores of these, slightly
                markerVocabMatch.setDbKey(childTermKey);
                markerVocabMatch.addScore(-0.0001);

                mpMatches.add(markerVocabMatch);
                handledMpTerms.add( markerVocabMatch.getDbKey() );
              }
            }
          }
        }
      }

      // OMIM hit (no chasing down the dag)
      else if ( SearchHelper.isOMIM(hit) ) {

        //ensure we haven't already done this term
        if (!handledOmimTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
          markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
          markerVocabMatch.addScore(100);
          omimMatches.add(markerVocabMatch);
          handledOmimTerms.add( markerVocabMatch.getDbKey() );

        }
      }

      // OMIM Orthohit (no chasing down the dag)
      else if ( SearchHelper.isOMIMORTHO(hit) ) {

        //ensure we haven't already done this term
        if (!handledOmimOrthoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
          markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
          markerVocabMatch.addScore(100);
          omimOrthoMatches.add(markerVocabMatch);
          handledOmimOrthoTerms.add( markerVocabMatch.getDbKey() );

        }
      }

      // PIRSF hit (no chasing down the dag)
      else if ( SearchHelper.isPIRSF(hit) ) {

        //ensure we haven't already done this term
        if (!handledPirsfTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
          markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
          markerVocabMatch.addScore(100);
          pirsfMatches.add(markerVocabMatch);
          handledPirsfTerms.add( markerVocabMatch.getDbKey() );

        }
      }

      // Interprot hit (no chasing down the dag)
      else if ( SearchHelper.isIP(hit) ) {

        //ensure we haven't already done this term
        if (!handledIpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
          markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
          markerVocabMatch.addScore(100);
          ipMatches.add(markerVocabMatch);
          handledIpTerms.add( markerVocabMatch.getDbKey() );

        }
      }
    } //each hit by iterator

    return;
  }


  //-------------------------//
  // Search for 'OR' matches
  //-------------------------//
  private void searchOrMatches (SearchInput searchInput)
    throws Exception
  {
    // defined outside loops to avoid repeated instantiation of references
    Hit hit;
    String termKey;
    MarkerMatch markerMatch;
    MarkerVocabMatch markerVocabMatch;
    List childIDs;
    String childTermKey;

    // execute the search, and handle each textual match
    Hits hits =  indexAccessor.searchMarkerOr(searchInput);
    logger.debug("MarkerSearch.searchOrMatches number of hits ->" + hits.length());
    for (Iterator iter = hits.iterator(); iter.hasNext();) {

      hit = (Hit) iter.next();

      // skip this match if we already handled it in the 'AND' search
      if ( !handledDocIDs.contains( hit.getId() ) )
      {
          // marker nomen
          if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.MARKER_TYPE_NAME)) {
              markerMatch = markerMatchFactory.getMatch(hit);
              markerOrWeightTypeScorer.addLuceneWeight(markerMatch);
              markerOrTypeScorer.addScore(markerMatch);
              markerNomenMatches.add(markerMatch);
          }

          // anatomical dictionary hit
          else if ( SearchHelper.isAD(hit) ) {

            //ensure we haven't already done this term
            if (!handledAdTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
              markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
              adMatches.add(markerVocabMatch);
              handledAdTerms.add( markerVocabMatch.getDbKey() );

              //children of this term;
              childIDs = markerVocabSearchCache.getAdChildTerms(hit.get(IndexConstants.COL_DB_KEY));
              if (childIDs != null) {
                for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
                  childTermKey = (String)childIter.next();
                  if ( !handledAdTerms.contains(childTermKey) ) {
                    markerVocabMatch = markerVocabMatchFactory.getMatch(hit);

                    // change db key to reflect the 'down-dag' term we're dealing
                    // with, and penalize the scores of these, slightly
                    markerVocabMatch.setDbKey(childTermKey);
                    markerVocabMatch.addScore(-0.0001);

                    adMatches.add(markerVocabMatch);
                    handledAdTerms.add( markerVocabMatch.getDbKey() );
                  }
                }
              }
            }
          }

          // gene ontology hit
          else if ( SearchHelper.isGO(hit) ) {

            //ensure we haven't already done this term
            if (!handledGoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
              markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
              goMatches.add(markerVocabMatch);
              handledGoTerms.add( markerVocabMatch.getDbKey() );

              //children of this term;
              childIDs = markerVocabSearchCache.getGoChildTerms(hit.get(IndexConstants.COL_DB_KEY));
              if (childIDs != null) {
                for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
                  childTermKey = (String)childIter.next();
                  if ( !handledGoTerms.contains(childTermKey) ) {
                    markerVocabMatch = markerVocabMatchFactory.getMatch(hit);

                    // change db key to reflect the 'down-dag' term we're dealing
                    // with, and penalize the scores of these, slightly
                    markerVocabMatch.setDbKey(childTermKey);
                    markerVocabMatch.addScore(-0.0001);

                    goMatches.add(markerVocabMatch);
                    handledGoTerms.add( markerVocabMatch.getDbKey() );
                  }
                }
              }
            }
          }

          // mammalian phenotype hit
          else if ( SearchHelper.isMP(hit) ) {

            //ensure we haven't already done this term
            if (!handledMpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
              markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
              mpMatches.add(markerVocabMatch);
              handledMpTerms.add( markerVocabMatch.getDbKey() );

              //children of this term;
              childIDs = markerVocabSearchCache.getMpChildTerms(hit.get(IndexConstants.COL_DB_KEY));
              if (childIDs != null) {
                for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
                  childTermKey = (String)childIter.next();
                  if ( !handledMpTerms.contains(childTermKey) ) {
                    markerVocabMatch = markerVocabMatchFactory.getMatch(hit);

                    // change db key to reflect the 'down-dag' term we're dealing
                    // with, and penalize the scores of these, slightly
                    markerVocabMatch.setDbKey(childTermKey);
                    markerVocabMatch.addScore(-0.0001);

                    mpMatches.add(markerVocabMatch);
                    handledMpTerms.add( markerVocabMatch.getDbKey() );
                  }
                }
              }
            }
          }

          // OMIM hit (no chasing down the dag)
          else if ( SearchHelper.isOMIM(hit) ) {

            //ensure we haven't already done this term
            if (!handledOmimTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
              markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
              omimMatches.add(markerVocabMatch);
              handledOmimTerms.add( markerVocabMatch.getDbKey() );

            }
          }

          // OMIM Ortho hit (no chasing down the dag)
          else if ( SearchHelper.isOMIMORTHO(hit) ) {

            //ensure we haven't already done this term
            if (!handledOmimOrthoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
              markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
              omimOrthoMatches.add(markerVocabMatch);
              handledOmimOrthoTerms.add( markerVocabMatch.getDbKey() );

            }
          }

          // PIRSF hit (no chasing down the dag)
          else if ( SearchHelper.isPIRSF(hit) ) {

            //ensure we haven't already done this term
            if (!handledPirsfTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
              markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
              pirsfMatches.add(markerVocabMatch);
              handledPirsfTerms.add( markerVocabMatch.getDbKey() );

            }
          }

          // Interprot hit (no chasing down the dag)
          else if ( SearchHelper.isIP(hit) ) {

            //ensure we haven't already done this term
            if (!handledIpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
              markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
              ipMatches.add(markerVocabMatch);
              handledIpTerms.add( markerVocabMatch.getDbKey() );

            }
          }

          else { // shouldn't be hitting this part of the code, so log value
            logger.error("***Bad Object Type in 'OR' MarkerSearch ->"
              + hit.get(IndexConstants.COL_OBJ_TYPE) );
          }
      }
    } //each hit by iterator

    return;
  }

  //-------------------------//
  // Marker Result Retrieval
  //-------------------------//

  /**
  * Convenience method to interface with the result set; If a given entry has
  * already been created, it will be returned.  If not, a new one is
  * generated, placed in the result set, and then returned.
  */
  private QS_MarkerResult getMarker(String markerKey) {

    if (searchResults.containsKey(markerKey)) {
        return (QS_MarkerResult)searchResults.get(markerKey);
    }
    else {
        QS_MarkerResult marker = new QS_MarkerResult(config);
        marker.setDbKey(markerKey);
        searchResults.put(marker.getDbKey(), marker);
        return marker;
    }
  }

  //-----------------------//
  // Assignment of Matches
  //-----------------------//

  private void assignMatches() {

    List markerKeys;
    MarkerMatch mem;
    MarkerVocabMatch markerVocabMatch;
    MarkerMatch nm;
    QS_MarkerResult thisMarker;

    // assign exact input string matches to their markers
    for (Iterator iter = exactInputStrMatches.iterator(); iter.hasNext();) {
        mem = (MarkerMatch)iter.next();
        thisMarker = getMarker( mem.getDbKey() );
        thisMarker.addExactMatch(mem);
        thisMarker.flagExactInputStrMatch();
    }

    // assign exact matches to their markers
    for (Iterator iter = exactNomenMatches.iterator(); iter.hasNext();) {
        mem = (MarkerMatch)iter.next();
        thisMarker = getMarker( mem.getDbKey() );
        thisMarker.addExactMatch(mem);
        thisMarker.flagExactInputTokenMatch();
    }

    // assign nomen matches to their markers
    for (Iterator iter = markerNomenMatches.iterator(); iter.hasNext();) {
        nm = (MarkerMatch)iter.next();
        thisMarker = getMarker( nm.getDbKey() );
        thisMarker.addNomenMatch(nm);
    }

    // assign AD matches to their markers
    for (Iterator iter = adMatches.iterator(); iter.hasNext();) {

        markerVocabMatch = (MarkerVocabMatch)iter.next();
        markerKeys = markerVocabSearchCache.getAdAnnotMarkers(markerVocabMatch.getDbKey());
        if (markerKeys != null) {
            for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
                thisMarker = getMarker((String)mrkKeyIter.next());
                thisMarker.addAdMatch(markerVocabMatch);
            }
        }
    }

    // assign GO matches to their markers
    for (Iterator iter = goMatches.iterator(); iter.hasNext();) {

        markerVocabMatch = (MarkerVocabMatch)iter.next();
        markerKeys = markerVocabSearchCache.getGoAnnotMarkers(markerVocabMatch.getDbKey());
        if (markerKeys != null) {
            for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
                thisMarker = getMarker((String)mrkKeyIter.next());
                thisMarker.addGoMatch(markerVocabMatch);
            }
        }
    }

    // assign MP matches to their markers
    for (Iterator iter = mpMatches.iterator(); iter.hasNext();) {

        markerVocabMatch = (MarkerVocabMatch)iter.next();
        markerKeys = markerVocabSearchCache.getMpAnnotMarkers(markerVocabMatch.getDbKey());
        if (markerKeys != null) {
            for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
                thisMarker = getMarker((String)mrkKeyIter.next());
                thisMarker.addMpMatch(markerVocabMatch);
            }
        }
    }

    // assign Omim matches to their markers
    for (Iterator iter = omimMatches.iterator(); iter.hasNext();) {

        markerVocabMatch = (MarkerVocabMatch)iter.next();
        markerKeys = markerVocabSearchCache.getOmimAnnotMarkers(markerVocabMatch.getDbKey());
        if (markerKeys != null) {
            for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
                thisMarker = getMarker((String)mrkKeyIter.next());
                thisMarker.addOmimMatch(markerVocabMatch);
            }
        }
    }

    // assign Omim ortho matches to their markers
    for (Iterator iter = omimOrthoMatches.iterator(); iter.hasNext();) {

        markerVocabMatch = (MarkerVocabMatch)iter.next();
        markerKeys = markerVocabSearchCache.getOmimOrthoAnnotMarkers(markerVocabMatch.getDbKey());
        if (markerKeys != null) {
            for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
                thisMarker = getMarker((String)mrkKeyIter.next());
                thisMarker.addOmimOrthoMatch(markerVocabMatch);

            }
        }
    }

    // assign PIRSF matches to their markers
    for (Iterator iter = pirsfMatches.iterator(); iter.hasNext();) {

        markerVocabMatch = (MarkerVocabMatch)iter.next();
        markerKeys = markerVocabSearchCache.getPsAnnotMarkers(markerVocabMatch.getDbKey());

        if (markerKeys != null) {
            for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
                thisMarker = getMarker((String)mrkKeyIter.next());
                thisMarker.addPirsfMatch(markerVocabMatch);
            }
        }
    }

    // assign ip matches to their markers
    for (Iterator iter = ipMatches.iterator(); iter.hasNext();) {

        markerVocabMatch = (MarkerVocabMatch)iter.next();
        markerKeys = markerVocabSearchCache.getIpAnnotMarkers(markerVocabMatch.getDbKey());

        if (markerKeys != null) {
            for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
                thisMarker = getMarker((String)mrkKeyIter.next());
                thisMarker.addIpMatch(markerVocabMatch);
            }
        }
    }

  }

}

package org.jax.mgi.searchtool_wi.searches;

// Standard Java Classes
import java.util.*;
import java.io.IOException;

// MGI Shared Classes
import org.jax.mgi.shr.config.Configuration;

// Logging
import org.apache.log4j.Logger;

// Lucene Classes
import org.apache.lucene.search.*;

// Quick Search Specific
import org.jax.mgi.searchtool_wi.matches.VocabMatch;
import org.jax.mgi.searchtool_wi.matches.VocabMatchFactory;
import org.jax.mgi.searchtool_wi.matches.MatchTypeScorer;
import org.jax.mgi.searchtool_wi.results.QS_VocabResult;
import org.jax.mgi.searchtool_wi.utils.SearchInput;
import org.jax.mgi.searchtool_wi.utils.ScoreConstants;
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
* This concrete search is responsible for gathering all data required for
* the Quick Search to display the "Vocab Bucket"
*/
public class QS_VocabSearch extends AbstractSearch {

  //--------//
  // Fields
  //--------//

  // Set up the logger
  private static Logger logger
    = Logger.getLogger(QS_VocabSearch.class.getName());

  // results to be returned
  private HashMap <String, QS_VocabResult> searchResults
    = new HashMap <String, QS_VocabResult>();

  // both the "and" and the "or" search use the same index; if the have
  // an "and" hit, do not keep the "or" hit for the same document
  private HashSet handledDocIDs = new HashSet();

  // Match factories we'll need generate the matches
  VocabMatchFactory vocabMatchFactory = new VocabMatchFactory(config);

  // Match Type Scorers we'll need to score the matches
  MatchTypeScorer vocabExactTypeScorer =
    new MatchTypeScorer(ScoreConstants.getVocabExactScoreMap() );
  MatchTypeScorer vocabInexactTypeScorer =
    new MatchTypeScorer(ScoreConstants.getVocabInexactScoreMap() );

  //--------------//
  // Constructors
  //--------------//
  public QS_VocabSearch(Configuration c)
  {
    super(c);
  }

  //----------------------------------------//
  // Over-ridden Abstract gatherData Method //
  //----------------------------------------//

  /**
  * gatherData() is an over-ridden method from the AbstractSearch class, and
  *   is responsible for gathering all results objects
  * @param a SearchInput object
  * @return List of result objects; these are not yet 'finalized', scored,
  *   or sorted, as the framework will take care of that
  */
  public List gatherData(SearchInput si)
    throws Exception
  {
    timer.record("---Vocab gatherData Started---");

    searchVocabExact(si);
    timer.record("Vocab - Exact Search Done");

    searchVocabAnd(si);
    timer.record("Vocab - 'AND' Search Done");

    searchVocabLargeToken(si);
    timer.record("Vocab - LargeToken Search Done");

    searchVocabOr(si);
    timer.record("Vocab - 'OR' Search Done");

    return new ArrayList( searchResults.values() );
  }

  //---------------------------------------------------------- Private Methods

  //---------------------//
  // Search Exact Matches
  //---------------------//

  /**
  * Searches the VocabExact index.  Iterates through results, adding a match
  * for each hit finds
  */
  private void searchVocabExact(SearchInput searchInput)
    throws Exception
  {
    Hit hit;
    String vocabID;
    VocabMatch vocabMatch;
    QS_VocabResult vocabResult;

    // execute the search
    Hits hits =  indexAccessor.searchVocabExactByWholeTerm(searchInput);
    logger.debug("VocabSearch.searchVocabExactByWholeTerm hits -> " + hits.length());

    // for each hit, make a match and assign it to a result
    for (Iterator iter = hits.iterator(); iter.hasNext();)
    {
        // get the hit, and build a match
        hit = (Hit) iter.next();

        // score/flag match
        vocabMatch = vocabMatchFactory.getMatch(hit);
        vocabMatch.flagAsTier1();
        vocabExactTypeScorer.addScore(vocabMatch);

        // assign the match to the correct result
        vocabResult = getVocabResult(
            hit.get(IndexConstants.COL_DB_KEY),
            hit.get(IndexConstants.COL_VOCABULARY));
        vocabResult.addExactMatch(vocabMatch);
    }
  }


  //----------------------------//
  // Search Large Token Matches
  //----------------------------//

  /**
  * Searches for LargeTokens.  Iterates through results, adding a match
  * for each hit finds
  */
  private void searchVocabLargeToken(SearchInput searchInput)
    throws Exception
  {
    Hit hit;
    String vocabID;
    VocabMatch vocabMatch;
    QS_VocabResult vocabResult;

    // execute the search
    List idHits =  indexAccessor.searchVocabAccIDByLargeToken(searchInput);
    logger.debug("VocabSearch.searchVocabAccIDByLargeToken hits -> " + idHits.size());

    // for each hit, make a match and assign it to a result
    for (Iterator hitIter = idHits.iterator(); hitIter.hasNext();)
    {
        // get the hit, and build a match
        hit = (Hit) hitIter.next();

        // generate/score/flag match
        vocabMatch = vocabMatchFactory.getMatch(hit);
        vocabExactTypeScorer.addScore(vocabMatch);
        if (searchInput.getLargeTokenCount() == 1) {
            vocabMatch.flagAsTier1();
        }
        else {vocabMatch.flagAsTier3();}

        // assign the match to the correct result
        vocabResult = getVocabResult(
            hit.get(IndexConstants.COL_DB_KEY),
            hit.get(IndexConstants.COL_VOCABULARY));
        vocabResult.addExactMatch(vocabMatch);
    }
  }


  //-----------------------//
  // Search Inexact Matches
  //-----------------------//

  /**
  * Searches the VocabByField index.  Iterates through results, adding a match
  * for each hit finds
  */
  private void searchVocabAnd(SearchInput si)
    throws Exception
  {
    Hit hit;
    QS_VocabResult vocabResult;
    VocabMatch vocabMatch;

    // execute the search
    Hits hits =  indexAccessor.searchVocabAnd(si);
    logger.debug("VocabSearch.searchVocabAnd hits -> " + hits.length());

    // for each hit, make a match and assign it to a result
    for (Iterator iter = hits.iterator(); iter.hasNext();)
    {
        // get the hit, build a match, and score the match
        hit = (Hit) iter.next();
        vocabMatch = vocabMatchFactory.getMatch(hit);
        handledDocIDs.add( vocabMatch.getLuceneDocID() );
        vocabMatch.flagAsTier2();
        vocabInexactTypeScorer.addScore(vocabMatch);

        // get/make a vocabResult, and assign this match to it
        vocabResult = getVocabResult(
            hit.get(IndexConstants.COL_DB_KEY),
            hit.get(IndexConstants.COL_VOCABULARY));
        vocabResult.addInexactMatch(vocabMatch);
    }
  }

  /**
  * Searches the VocabByField index.  Iterates through results, adding a match
  * for each hit finds
  */
  private void searchVocabOr(SearchInput si)
    throws Exception
  {
    Hit hit;
    QS_VocabResult vocabResult;
    VocabMatch vocabMatch;

    // execute the search
    Hits hits =  indexAccessor.searchVocabOr(si);
    logger.debug("VocabSearch.searchVocabOr hits -> " + hits.length());

    // for each hit, make a match and assign it to a result
    for (Iterator iter = hits.iterator(); iter.hasNext();)
    {
        hit = (Hit) iter.next();

        // skip this match if we already handled it in the 'AND' search
        if ( !handledDocIDs.contains( hit.getId() ) )
        {
            // build a match, and score the match
            vocabMatch = vocabMatchFactory.getMatch(hit);
            vocabInexactTypeScorer.addScore(vocabMatch);

            // get/make a vocabResult, and assign this match to it
            vocabResult = getVocabResult(
                hit.get(IndexConstants.COL_DB_KEY),
                hit.get(IndexConstants.COL_VOCABULARY));
            vocabResult.addInexactMatch(vocabMatch);
        }
    }
  }

  //-------------------------//
  // Private Utility Methods
  //-------------------------//

  /**
  * Convenience method to interface with the result set; If a given entry has
  * already been created, it will be returned.  If not, a new one is
  * generated, placed in the result set, and then returned.
  */
  private QS_VocabResult getVocabResult(String vocabKey, String voc_type) {

    if (searchResults.containsKey( vocabKey + voc_type )) {
        return (QS_VocabResult)searchResults.get( vocabKey + voc_type );
    }
    else {
        QS_VocabResult vocab = new QS_VocabResult(config);
        vocab.setDbKey(vocabKey);
        vocab.setVocabulary(voc_type);
        searchResults.put(vocab.getDbKey()+vocab.getVocabulary(), vocab);
        return vocab;
    }
  }

}

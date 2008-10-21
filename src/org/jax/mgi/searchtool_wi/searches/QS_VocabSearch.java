package org.jax.mgi.searchtool_wi.searches;

// Standard Java Classes
import java.util.*;
import java.io.IOException;

// Logging
import org.apache.log4j.Logger;

// Lucene Classes
import org.apache.lucene.search.*;
import org.apache.lucene.index.CorruptIndexException;

// Quick Search Specific
import org.jax.mgi.searchtool_wi.matches.VocabMatch;
import org.jax.mgi.searchtool_wi.matches.VocabMatchFactory;
import org.jax.mgi.searchtool_wi.matches.MatchTypeScorer;
import org.jax.mgi.searchtool_wi.results.QS_VocabResult;
import org.jax.mgi.searchtool_wi.utils.SearchInput;
import org.jax.mgi.searchtool_wi.utils.ScoreConstants;
import QS_Commons.IndexConstants;

// MGI Shared Classes
import org.jax.mgi.shr.config.Configuration;


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
  MatchTypeScorer vocabTypeScorer =
    new MatchTypeScorer(ScoreConstants.getVocabScoreMap() );

  //--------------//
  // Constructors
  //--------------//
  public QS_VocabSearch(Configuration c)
  {
    super(c);
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
  public List gatherData(SearchInput si)
    throws Exception
  {
    timer.record("---Vocab gatherData Started---");

    searchVocabExact(si);
    timer.record("Vocab - Exact Search Done");

    searchVocabAnd(si);
    timer.record("Vocab - 'AND' Search Done");

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

    Hits hits =  indexAccessor.searchVocabExact(searchInput);
    logger.debug("VocabSearch.searchVocabExact hits -> " + hits.length());
    for (Iterator iter = hits.iterator(); iter.hasNext();)
    {
        // get the hit, and build a match
        hit = (Hit) iter.next();
        handleVocabExactHit(hit);
    }

    // search each token against vocab IDs
    List idHits =  indexAccessor.searchVocabAccID(searchInput);
    logger.debug("VocabSearch.searchVocabAccID hits -> " + idHits.size());
    for (Iterator hitIter = idHits.iterator(); hitIter.hasNext();)
    {
        // get the hit, and build a match
        hit = (Hit) hitIter.next();
        handleVocabExactHit(hit);
    }
  }

  private void handleVocabExactHit (Hit hit)
    throws Exception
  {
    VocabMatch vocabMatch;
    QS_VocabResult vocabResult;

    // get and/or make a vocabResult, and assign this match to it
    vocabMatch = vocabMatchFactory.getMatch(hit);
    vocabExactTypeScorer.addScore(vocabMatch);
    vocabMatch.addScore(ScoreConstants.VOC_EXACT_BOOST); //for an exact match

    vocabResult = getVocabResult(
        hit.get(IndexConstants.COL_DB_KEY),
        hit.get(IndexConstants.COL_VOCABULARY));
    vocabResult.addExactMatch(vocabMatch);
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

    Hits hits =  indexAccessor.searchVocabAnd(si);
    logger.debug("VocabSearch.searchVocabAnd hits -> " + hits.length());
    for (Iterator iter = hits.iterator(); iter.hasNext();)
    {
        // get the hit, build a match, and score the match
        hit = (Hit) iter.next();
        vocabMatch = vocabMatchFactory.getMatch(hit);
        handledDocIDs.add( vocabMatch.getLuceneDocID() );
        vocabMatch.addScore(ScoreConstants.VOC_AND_BOOST); // for an 'AND' match
        vocabTypeScorer.addScore(vocabMatch);

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

    Hits hits =  indexAccessor.searchVocabOr(si);
    logger.debug("VocabSearch.searchVocabOr hits -> " + hits.length());
    for (Iterator iter = hits.iterator(); iter.hasNext();)
    {
        hit = (Hit) iter.next();

        // skip this match if we already handled it in the 'AND' search
        if ( !handledDocIDs.contains( hit.getId() ) )
        {
            // build a match, and score the match
            vocabMatch = vocabMatchFactory.getMatch(hit);
            vocabTypeScorer.addScore(vocabMatch);

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

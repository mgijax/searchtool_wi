package org.jax.mgi.searchtool_wi.matches;

// standard java
import java.util.*;

// Quick Search Shared Classes
import org.jax.mgi.shr.searchtool.IndexConstants;

// logging
import org.apache.log4j.Logger;

/**
* A MatchTypeScorer will increase or decrease the value of a given match
* depending on the object type of the match; written to the AbstractMatch match
* abstraction
*/
public class MatchTypeScorer {

  // --------//
  // Fields
  // --------//

  // holds the typeValue->scoreIncrease mapping
  private Map typeScoreMap;
  private Float defaultScore = new Float(0.0);

  // logging
  private static Logger logger = Logger.getLogger(MatchTypeScorer.class.getName());

  // -------------//
  // Constructor //
  // -------------//

  /**
  * Hidded default constructor; needs the score mapping
  */
  private MatchTypeScorer() {};

  /**
  * Constructs a MatchTypeScorer with a objectType->scoreIncrease mapping
  */
  public MatchTypeScorer(Map m) {
    typeScoreMap = m;
    if ( typeScoreMap.containsKey("default") ) {
        defaultScore = (Float)typeScoreMap.get("default");
    }
  }

  // ----------//
  // Scoring
  // ----------//

  /**
  * Adds score the match; looks to see if the match's type should be scored
  * higher or lower, and takes appropriate action
  */
  public AbstractMatch addScore(AbstractMatch abstractMatch) {

    String dataType = abstractMatch.getDataType();

    if (typeScoreMap.containsKey(dataType)) {
        abstractMatch.addScore((Float) typeScoreMap.get(dataType));
    }
    else {
        abstractMatch.addScore(defaultScore);
    }


    return abstractMatch;
  }

  /**
  * Adds weight to the lucene score for this match; looks to see if the
  * match's type should be weighted, and takes appropriate action
  */
  public AbstractMatch addLuceneWeight(AbstractMatch abstractMatch) {

    String dataType = abstractMatch.getDataType();

    if (typeScoreMap.containsKey(dataType)) {
        abstractMatch.addLuceneWeight((Float) typeScoreMap.get(dataType));
    }
    else {
        abstractMatch.addLuceneWeight(defaultScore);
    }

    return abstractMatch;
  }

} // public class MatchTypeScorer

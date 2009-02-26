package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;

// Quick Search Classes
import org.jax.mgi.shr.searchtool.IndexConstants;
import org.jax.mgi.searchtool_wi.utils.DisplayHelper;

/**
* An AbstractMatch is an uninstantiatable parent of concrete Matches; Matches
* represent the translation of a Lucene textual hit to a local object, and
* encapsulate the functionality and knowledge to be shared between all
* concrete matches; whenever possible, code should be written to this
* general higher-level abstraction. Also see AbstractMatchFactory.java and
* searchtool_wi wiki entry for Matches.
*/
public abstract class AbstractMatch {

  //--------//
  // Fields
  //--------//

  // data fields all matches have; AbstractMatchFactory will set these
  private String db_key = "";
  private String dataType = "";
  private String matchedText = "";
  private String displayableType = "";
  private String provider = "";

  // unique key provided by the index; used as join-point between indexes
  private String uniqueKey;

  // lucene's information regarding the hit
  private Float searchScore;
  private int luceneDocID;

  // additional scoring layer; set by search layer to weight this match
  // additiveScore - increases the score of this match by a flat ammount
  // luceneWeight - increase/decrease the importantance of the lucene score
  private Float  additiveScore      = new Float(0.0);
  private Float  luceneWeight       = new Float(1.0);

  // tiering flags; used to indicate which sorting tier this match should
  // fall within; search layer set these dependant upon which index the match
  // is being generated from
  private boolean isTier1 = false;
  private boolean isTier2 = false;
  private boolean isTier3 = false;

  // derived score of this match; only derived once, and kept
  Float derivedScore;

  //----------------//
  // Field Accessors
  //----------------//

  /**
  * Returns the database key of the object hit
  * @return String - the database key of object hit
  */
  public String getDbKey() {
    return db_key;
  }
  /**
  * Set database key of the object hit
  * @param String - the database key of object hit
  */
  public void setDbKey(String s) {
    if (s != null) {db_key = s;}
  }

  /**
  * Returns the original text (before it was put into the index) of the hit
  * @return String - original text
  */
  public String getMatchedText() {
    return matchedText;
  }
  /**
  * Sets the original text (before it was put into the index) of the hit
  * @param String - original text
  */
  public void setMatchedText(String s) {
    if (s != null) {matchedText = s;}
  }

  /**
  * Returns the data type of the object hit
  * @return String - data type
  */
  public String getDataType() {
    return dataType;
  }
  /**
  * Sets the data type of the object hit
  * @param String - data type
  */
  public void setDataType(String s) {
    if (s != null) {dataType = s;}
  }

  /**
  * Returns the displayable version of the data type
  * @return String - displayable data type
  */
  public String getDisplayableType() {
    return displayableType;
  }
  /**
  * Sets the displayable version of the data type
  * @param String - displayable data type
  */
  public void setDisplayableType(String s) {
    if (s != null) {displayableType = s;}
  }

  /**
  * Returns the provider of the data
  * @return String - provider
  */
  public String getProvider() {
    return provider;
  }
  /**
  * Sets the provider of the data
  * @param String - provider
  */
  public void setProvider(String s) {
    if (s != null) {provider = s;}
  }

  /**
  * Returns the index ID of the Lucened Hit that match was generated from
  * @return int - Index ID of Lucene Hit
  */
  public int getLuceneDocID() {
    return luceneDocID;
  }
  /**
  * Sets the index ID of the Lucened Hit that match was generated from
  * @param int - Index ID of Lucene Hit
  */
  public void setLuceneDocID(int i) {
    luceneDocID = i;
  }

  /**
  * Returns the unique key value
  * @return String - unique key
  */
  public String getUniqueKey() {
    return uniqueKey;
  }
  /**
  * Sets the unique key value
  * @param String - unique key
  */
  public void setUniqueKey(String s) {
    if (s != null) {uniqueKey = s;}
  }

  // ------------------//
  // Tiering & Sorting
  // ------------------//

  /**
  * Sets this match as belonging in tier 1
  */
  public void flagAsTier1() {
   isTier1 = true;
  }
  /**
  * Is this tier 1?
  * @return Boolean
  */
  public boolean isTier1() {
    return isTier1;
  }
  /**
  * Sets this match as belonging in tier 2
  */
  public void flagAsTier2() {
   isTier2 = true;
  }
  /**
  * Is this tier 2?
  * @return Boolean
  */
  public boolean isTier2() {
    return isTier2;
  }
  /**
  * Sets this match as belonging in tier 3
  */
  public void flagAsTier3() {
   isTier3 = true;
  }
  /**
  * Is this tier 3?
  * @return Boolean
  */
  public boolean isTier3() {
    return isTier3;
  }

  /**
  * Returns the String by which this match should be alphanumerically sorted;
  * might be over-ridden in concrete classes
  * @return String - to be sorted by
  */
  public String getAlphaSortBy() {
    return matchedText;
  }

  //----------------------------------------------------//
  // Scoring - derived score, lucene score, modifiers
  //----------------------------------------------------//

  /**
  * Return the derived score of this match -
  * @return Float - the score
  */
  public Float getScore()
  {
    if (derivedScore == null)
    {
        // add to score depending on the tier
        int tierScore = 0;
        if (isTier1) {
            tierScore = 100000;
        }else if (isTier2) {
            tierScore = 10000;
        }else if (isTier3) {
            tierScore = 1000;
        }

        // pull all scoring together to get actual score for the match
        derivedScore = (searchScore * luceneWeight) + additiveScore + tierScore;
    }
    return derivedScore;
  }

  //----- Search Score -----

  /**
  * Return the Lucene score of this match
  * @return Float - the Lucene score
  */
  public Float getSearchScore() {
    return searchScore;
  }
  /**
  * Set the derived score of this match
  * @param Float - the Lucene score
  */
  public void setSearchScore(Float f) {
    searchScore = f;
  }
  /**
  * Set the derived score of this match
  * @param float - the Lucene score
  */
  public void setSearchScore(float f) {
    setSearchScore(new Float(f));
  }

  //----- Additive Score -----

  /**
  * Return the score to be added to this match by external increase
  * @return Float - Total additive score
  */
  public Float getAdditiveScore() {
    return additiveScore;
  }
  /**
  * Add to this match's score
  * @param Float - score to add
  */
  public void addScore(Float f) {
    additiveScore = additiveScore + f;
  }
  /**
  * Add to this match's score; convenience method; calls addScore(Float)
  * @param float - score to add
  */
  public void addScore(float f) {
    addScore(new Float(f));
  }
  /**
  * Add to this match's score; convenience method; calls addScore(Float)
  * @param int - score to add
  */
  public void addScore(int i) {
    addScore(new Float(i));
  }
  /**
  * Add to this match's score; convenience method; calls addScore(Float)
  * @param double - score to add
  */
  public void addScore(double d) {
    addScore(new Float(d));
  }

  //----- Lucene Weighting -----

  /**
  * Return weight to this match's lucene score
  * @Return Float - weight to add
  */
  public Float getLuceneWeight() {
      return luceneWeight;
  }

  /**
  * Add weight to this match's lucene score
  * @param Float - weight to add
  */
  public void addLuceneWeight(Float f) {
      luceneWeight = luceneWeight + f;
  }
  /**
  * Add weight to this match's lucene score; convenience method
  * @param double - weight to add
  */
  public void addLuceneWeight(float f) {
      addLuceneWeight(new Float(f));
  }
  /**
  * Add weight to this match's lucene score; convenience method
  * @param double - weight to add
  */
  public void addLuceneWeight(double d) {
      addLuceneWeight(new Float(d));
  }

  //------//
  // Misc
  //------//

  /**
  * Provide generic display for this match; extending classes may over-ride
  * @return String - displayable value
  */
  public String display() {
    return "<span class='matchDisplayableType'>"
      + DisplayHelper.superscript(displayableType)
      + "</span>" + " : "
      + DisplayHelper.superscript(DisplayHelper.shortenDisplayStr(matchedText))
      + " " + provider;
  }

  /**
  * Determines if this match is to an Accession ID
  * @return Boolean
  */
  public boolean isAccID() {

    boolean isAccID = false;
    if ( this.dataType.equals(IndexConstants.ACCESSION_ID)
      || this.dataType.equals(IndexConstants.VOC_ACCESSION_ID)
      || this.dataType.equals(IndexConstants.ALLELE_ACCESSION_ID)
      || this.dataType.equals(IndexConstants.ORTH_ACCESSION_ID)
      || this.dataType.equals(IndexConstants.ES_ACCESSION_ID)) {
        isAccID = true;
    }
    return isAccID;

  }


}

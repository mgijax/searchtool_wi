package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;

// Quick Search Classes
import QS_Commons.IndexConstants;
import org.jax.mgi.searchtool_wi.utils.DisplayHelper;

/**
* An AbstractMatch is an uninstantiatable parent of concrete Matches; Matches
* represent the translation of a Lucene textual hit to a local object, and
* encapsulate the functionality and knowledge to be shared between all
* concrete matches; whenever possible, code should be written to this
* general higher-level abstraction. Also see AbstractMatchFactory.java
*/
public abstract class AbstractMatch {

  //--------//
  // Fields
  //--------//

  // lucene's information regarding the hit
  private Float searchScore;
  private int luceneDocID;

  // fields AbstractMatchFactory will attempt to fill; some may be missing,
  // and their values are initialized with an empty string
  private String db_key;
  private String dataType;
  private String matchedText = "";
  private String displayableType = "";
  private String provider = "";

  // our secondary scoring layer, set by search layer to weight this match
  private Float  additiveScore      = new Float(0.0);
  private Float  luceneWeight       = new Float(1.0);

  // unique key provided by the index; used as join-point between indexes
  private String uniqueKey;

  //----------------//
  // Basic Accessors
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

  //---------//
  // Sorting
  //---------//

  /**
  * Returns the String by which this match should be alphanumerically sorted
  * @return String - to be sorted by
  */
  public String getAlphaSortBy() {
    return matchedText;
  }

  //----------------------------------------------------//
  // Scoring - derived score, lucene score, modifiers
  //----------------------------------------------------//

  /**
  * Return the derived score of this match (search score + additive score)
  * @return Float - the score
  */
  public Float getScore() {
    return (searchScore * luceneWeight) + additiveScore;
  }

  //---------------//
  // Lucene Score
  //---------------//

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

  //---------------//
  // Additive Score
  //---------------//

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

  //------------------//
  // Lucene Weighting
  //------------------//

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

  //----------------//
  // Generic Display
  //----------------//

  /**
  * Get displayable value for this match
  * @return String - displayable value
  */
  public String display() {
    return "<span class='matchDisplayableType'>"
      + DisplayHelper.superscript(displayableType)
      + "</span>" + " : "
      + DisplayHelper.superscript(DisplayHelper.shortenDisplayStr(matchedText))
      + " " + provider;
  }

  //------//
  // Misc
  //------//

  /**
  * Determines if this match is to an Accession ID
  * @return String - displayable value
  */
  public boolean isAccID() {

    boolean isAccID = false;
    if ( this.dataType.equals(IndexConstants.ACCESSION_ID) ) {
        isAccID = true;
    }
    return isAccID;

  }


}

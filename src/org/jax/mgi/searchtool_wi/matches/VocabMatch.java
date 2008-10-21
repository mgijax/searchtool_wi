package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;

// Quick Search Classes
import QS_Commons.IndexConstants;

/**
* A VocabMatch represents a match to a vocab term (ID, term, synonym etc)
*/
public class VocabMatch extends AbstractMatch {

  // --------//
  // Fields
  // --------//

  // Data & Values specific to a Vocab Match
  private String vocabulary = new String("");

  // ----------------//
  // Basic Accessors
  // ----------------//

  /**
  * Returns vocabulary of this match
  *
  * @return String - vocabulary
  */
  public String getVocabulary() {
    return vocabulary;
  }

  /**
  * Set vocabulary of this match
  *
  * @param String -
  *                vocabulary
  */
  public void setVocabulary(String s) {
    vocabulary = s;
  }

}

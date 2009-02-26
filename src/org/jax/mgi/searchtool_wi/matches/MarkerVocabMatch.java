package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;

// Quick Search Classes
import org.jax.mgi.shr.searchtool.IndexConstants;
import org.jax.mgi.searchtool_wi.lookup.VocabDisplay;
import org.jax.mgi.searchtool_wi.lookup.VocabDisplayCache;
import org.jax.mgi.searchtool_wi.utils.DisplayHelper;

/**
* A MarkerVocabMatch represents a vocabulary match which will be traced to
* markers annotated to the vocab term
*/
public class MarkerVocabMatch extends AbstractMatch {

  // --------//
  // Fields
  // --------//

  // Data & Values specific to a Vocab Match
  private String vocabulary = new String("");
  private String ancestorKey = new String("");
  private boolean isChildMatch = false;

  // reference to Voc Display Cache singleton created at servlet instantiation
  private VocabDisplayCache vocabDisplayCache
    = VocabDisplayCache.getVocabDisplayCache();


  // ----------------//
  // Basic Accessors
  // ----------------//

  /**
  * Returns vocabulary of this match
  * @return String - vocabulary
  */
  public String getVocabulary() {
    return vocabulary;
  }

  /**
  * Set vocabulary of this match
  * @param String - vocabulary
  */
  public void setVocabulary(String s) {
    vocabulary = s;
  }

  /**
  * Sets the ancestor key of this match (if we chased down a DAG)
  * @param String - ancestor key
  */

  public void setAncestorKey(String s) {
    ancestorKey = s;
  }

  /**
  * Returns ancestor key of this match (if we chased down a DAG)
  * @return String - ancestor key
  */

  public String getAncestorKey() {
    return ancestorKey;
  }

  /**
  * Returns a boolean of whether or not this is a "Child Match"
  */
  public boolean isChildMatch() {
    boolean b = false;
    if ( getDbKey() != getAncestorKey() ) {
        b = true;
    }
    return b;
  }

  //---------//
  // Display
  //---------//

  /**
  * Get displayable value for this match.  Overridding abstract implementation
  *  to acomplish display requirements when it's an Accession ID
  * @return String - displayable value
  */
  public String display() {

    String returnString = new String();

    // get vocab display cache entries
    VocabDisplay vocabDisplay = vocabDisplayCache.getVocab(this);
    VocabDisplay parentVocabDisplay = vocabDisplayCache.getParentVocab(this);

    if ( isAccID() ) {

        if ( isChildMatch() ) {
            returnString = "<span class='matchDisplayableType'>"
              + DisplayHelper.superscript( this.getDisplayableType() )
              + "</span>" + " : "
              + DisplayHelper.superscript( vocabDisplay.getName() )
              + " (subterm of " + parentVocabDisplay.getAcc_id() + ")";
        }
        else {
            returnString = "<span class='matchDisplayableType'>"
              + DisplayHelper.superscript( this.getDisplayableType() )
              + "</span>" + " : "
              + DisplayHelper.superscript( vocabDisplay.getName() )
              + " (" + vocabDisplay.getAcc_id() + ")";
        }
    }
    else {

        returnString = "<span class='matchDisplayableType'>"
          + DisplayHelper.superscript( this.getDisplayableType() )
          + "</span>" + " : "
          + DisplayHelper.superscript( vocabDisplay.getName() );
    }

    return  returnString;
  }


}

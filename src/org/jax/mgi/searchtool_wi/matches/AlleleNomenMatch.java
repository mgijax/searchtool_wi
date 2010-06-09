package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;

// Quick Search Classes
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
* A AlleleNomenMatch represents a match to an allele
*/
public class AlleleNomenMatch extends AbstractMatch {

  // --------//
  // Fields
  // --------//

  // Data & Values specific to a Marker Match
  private String isCurrent = "";

  // ----------------//
  // Basic Accessors
  // ----------------//

  /**
  * Returns flag which signified if this is current nomenclature
  * @return String - current nomenclature
  */
  public String getIsCurrent() {
    return isCurrent;
  }

  /**
  * Set flag which signified if this is current nomenclature
  * @param String - current nomenclature
  */
  public void setIsCurrent(String s) {
    if (s != null) {isCurrent = s;}
  }

}

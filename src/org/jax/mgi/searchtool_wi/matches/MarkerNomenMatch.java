package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;

// Quick Search Classes
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
* A MarkerNomenMatch represents a match to a marker (ID, nomenclature, symbols etc)
*/
public class MarkerNomenMatch extends AbstractMatch {

  // --------//
  // Fields
  // --------//

  // Data & Values specific to a Marker Match
  private String organismKey = "";
  private String isCurrent = "";

  // ----------------//
  // Basic Accessors
  // ----------------//

  /**
  * Returns the organism key of the marker object hit
  * @return String - the organism key
  */
  public String getOrganismKey() {
    return organismKey;
  }

  /**
  * Set organism key of the marker object hit
  * @param String - the organism key
  */
  public void setOrganismKey(String s) {
    if (s != null) {organismKey = s;}
  }

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

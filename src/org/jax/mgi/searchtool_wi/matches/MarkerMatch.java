package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;

// Quick Search Classes
import QS_Commons.IndexConstants;

/**
* A MarkerMatch represents a match to a marker (ID, nomenclature, symbols etc)
*/
public class MarkerMatch extends AbstractMatch {

  // --------//
  // Fields
  // --------//

  // Data & Values specific to a Marker Match
  private String organismKey = new String("");
  private String isCurrent = new String("");

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

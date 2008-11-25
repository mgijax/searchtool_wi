package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;

// Quick Search Classes
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
* An OtherMatch represents an 'other' match, used to fill the catch-all generic
* 'other' bucket
*/
public class OtherMatch extends AbstractMatch {

  // --------//
  // Fields
  // --------//

  // Data & Values specific to an "Other" Match
  private String organism   = "";
  private String acc_key    = "";
  private String preferred  = "";
  private String logical_db = "";

  // ----------------//
  // Basic Accessors
  // ----------------//

  /**
  * Returns the organism key of the object hit
  * @return String - the organism key
  */
  public String getOrganism() {
    return organism;
  }

  /**
  * Set organism key of the object hit
  * @param String - the organism key
  */
  public void setOrganism(String s) {
    organism = s;
  }

  /**
  * Returns the accession key of the object hit
  * @return String - the accession key
  */
  public String getAccKey() {
    return acc_key;
  }

  /**
  * Sets the accession key of the object hit
  * @param String - the accession key
  */
  public void setAccKey(String s) {
    acc_key = s;
  }

  /**
   * Gets the preferred value of the object hit.
   * @return String - The preferred value
   */

  public String getPreferred () {
      return preferred;
  }

  /**
   * Sets the preferred value of the object hit.
   * @param String - The preferred value.
   */

  public void setPreferred (String s) {
      preferred = s;
  }

  /**
   * Gets the logical DB field.
   * @return String - The logical db information.
   */

  public String getLogicalDb () {
      return logical_db;
  }

  /**
   * Sets the logical DB field.
   * @param String - The logical DB value
   */

  public void setLogicalDb (String s) {
      logical_db = s;
  }

}

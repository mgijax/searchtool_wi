package org.jax.mgi.searchtool_wi.results;

// standard java
import java.util.*;

public class OtherResultContainer extends ResultContainer {

  // -------------//
  // Constructor
  // -------------//
  public OtherResultContainer(List l) {
    super(l);
  }

  // -------------------------//
  // Data Specific Accessors
  // -------------------------//
  public OtherResult getByKey(String key) {
    return (OtherResult) mappedResults.get(key);
  }

  // ------------------//
  // Mapped Accessors
  // ------------------//
  public int size() {
    return scoreSortedResults.size();
  }

}

package org.jax.mgi.searchtool_wi.results;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class QS_OtherResultContainer extends ResultContainer {

  // -------------//
  // Constructor
  // -------------//
  public QS_OtherResultContainer(List l) {
      super(l);
  }

  // -------------------------//
  // Data Specific Accessors
  // -------------------------//
  public QS_OtherResult getByKey(String key) {
      return (QS_OtherResult) mappedResults.get(key);
  }

  // ------------------//
  // Mapped Accessors
  // ------------------//
  public int size() {
      return scoreSortedResults.size();
  }

}

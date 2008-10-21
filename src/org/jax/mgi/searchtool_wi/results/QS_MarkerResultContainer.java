package org.jax.mgi.searchtool_wi.results;

import java.util.*;

import org.jax.mgi.searchtool_wi.lookup.MarkerDisplay;
import org.jax.mgi.searchtool_wi.lookup.MarkerDisplayCache;

public class QS_MarkerResultContainer extends ResultContainer {

  private MarkerDisplayCache markerDisplayCache;
  private String stringOfSymbols = new String("");

  // ------------//
  // Constructor
  // ------------//
  public QS_MarkerResultContainer(List l) {

    super(l);

    // get the cache
    markerDisplayCache = MarkerDisplayCache.getMarkerDisplayCache();

    // pre-make symbol list for this result set
    StringBuilder sb = new StringBuilder();
    QS_MarkerResult thisMarkerResult; // search result built by model
    MarkerDisplay thisMarkerDisplay; // pulled from cache for given marker
    for (Iterator iter = scoreSortedResults.iterator(); iter.hasNext();) {
        thisMarkerResult = (QS_MarkerResult) iter.next();
        thisMarkerDisplay = markerDisplayCache.getMarker(thisMarkerResult);
        sb = sb.append("\"" + thisMarkerDisplay.getSymbol() + "\",");
    }

    stringOfSymbols = sb.toString();
    if( stringOfSymbols.length() > 0){
        stringOfSymbols = stringOfSymbols.substring(0, stringOfSymbols.length() -1 );
    }
  }

  // ----------//
  // Accessors
  // ----------//

  public QS_MarkerResult getMarkerByKey(String markerKey) {
      return (QS_MarkerResult) mappedResults.get(markerKey);
  }

  public String getStringOfSymbols() {
      return stringOfSymbols;
  }

  // /////////////////
  // Mapped Accessors
  // /////////////////
  public int size() {
      return scoreSortedResults.size();
  }

}

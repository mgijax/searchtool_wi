package org.jax.mgi.searchtool_wi.results;

// standard java
import java.util.*;

import org.jax.mgi.searchtool_wi.lookup.MarkerDisplay;
import org.jax.mgi.searchtool_wi.lookup.GenomeFeatureDisplayCache;

public class GenomeFeatureResultContainer extends ResultContainer {

  private GenomeFeatureDisplayCache gfDisplayCache;
  private String stringOfSymbols = new String("");

  // ------------//
  // Constructor
  // ------------//
  public GenomeFeatureResultContainer(List l) {

    super(l);

    // get the cache
    gfDisplayCache = GenomeFeatureDisplayCache.getGenomeFeatureDisplayCache();

    // pre-make symbol list for this result set
    StringBuilder sb = new StringBuilder();
    GenomeFeatureResult thisMarkerResult; // search result built by model
    MarkerDisplay thisMarkerDisplay; // pulled from cache for given marker
    for (Iterator iter = scoreSortedResults.iterator(); iter.hasNext();) {
        thisMarkerResult = (GenomeFeatureResult) iter.next();
        thisMarkerDisplay = gfDisplayCache.getGenomeFeature(thisMarkerResult);
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

  public GenomeFeatureResult getMarkerByKey(String markerKey) {
    return (GenomeFeatureResult) mappedResults.get(markerKey);
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

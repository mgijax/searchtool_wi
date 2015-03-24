package org.jax.mgi.searchtool_wi.results;

import java.util.Iterator;
import java.util.List;

import org.jax.mgi.searchtool_wi.lookup.GenomeFeatureDisplay;
import org.jax.mgi.searchtool_wi.lookup.GenomeFeatureDisplayCache;

public class GenomeFeatureResultContainer extends ResultContainer {

	private GenomeFeatureDisplayCache gfDisplayCache;
	private String stringOfSymbols = new String("");

	public GenomeFeatureResultContainer(List l) {

		super(l);

		// get the cache
		gfDisplayCache = GenomeFeatureDisplayCache.getGenomeFeatureDisplayCache();

		// pre-make symbol list for this result set
		StringBuilder sb = new StringBuilder();
		GenomeFeatureResult thisMarkerResult; // search result built by model
		GenomeFeatureDisplay thisGenomeFeatureDisplay; // pulled from cache for given marker
		for (Iterator iter = scoreSortedResults.iterator(); iter.hasNext();) {
			thisMarkerResult = (GenomeFeatureResult) iter.next();
			thisGenomeFeatureDisplay = gfDisplayCache.getGenomeFeature(thisMarkerResult);

			sb = sb.append("\"" + thisGenomeFeatureDisplay.getBatchForwardValue() + "\",");
		}

		stringOfSymbols = sb.toString();
		if( stringOfSymbols.length() > 0){
			stringOfSymbols = stringOfSymbols.substring(0, stringOfSymbols.length() -1 );
		}
	}

	public GenomeFeatureResult getResultByKey(String resultKey) {
		return (GenomeFeatureResult) mappedResults.get(resultKey);
	}

	public String getStringOfSymbols() {
		return stringOfSymbols;
	}

	public int size() {
		return scoreSortedResults.size();
	}

}

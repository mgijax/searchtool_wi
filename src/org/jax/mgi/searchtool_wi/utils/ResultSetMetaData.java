package org.jax.mgi.searchtool_wi.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// copied for convenience from fewi
public class ResultSetMetaData {
    Map<String, List<String>> highlights = new HashMap<String, List<String>> ();
	Map<String, Integer> counts = new HashMap<String, Integer> ();
    
    public ResultSetMetaData() {};
    
    public ResultSetMetaData(Map<String, List<String>> highlights) {
        this.highlights = highlights;
    };
    
    public Map<String, List<String>> getHighlights() {
		return highlights;
	}

	public void setHighlights(Map<String, List<String>> highlights) {
		this.highlights = highlights;
	}
 
	public Map<String, Integer> getCounts() {
		return counts;
	}

	public void setCounts(Map<String, Integer> counts) {
		this.counts = counts;
	}
	
	public void addCount(String countName, Integer count){
		counts.put(countName, count);
	}
	
	public Integer getCount(String countName){
		if (counts.containsKey(countName)) {
			return counts.get(countName);
		}
		else return new Integer(0);
	}
}

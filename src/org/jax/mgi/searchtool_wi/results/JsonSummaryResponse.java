package org.jax.mgi.searchtool_wi.results;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.searchtool_wi.utils.AccessionSummaryRow;
import org.jax.mgi.searchtool_wi.utils.ResultSetMetaData;

/**
 * JSON Response Object -- copied and modified for convenience from fewi
 */
public class JsonSummaryResponse {


    //////////////////////////////////////////////////////////////////////////
    //  INTERNAL FIELDS
    //////////////////////////////////////////////////////////////////////////

	// Result Objects of a given search
	protected List<AccessionSummaryRow> summaryRows = new ArrayList<AccessionSummaryRow>();

	// Total number of possible results
	protected int totalCount = -1;
	
	protected ResultSetMetaData meta;



    //////////////////////////////////////////////////////////////////////////
    //  BASIC ACCESSORS
    //////////////////////////////////////////////////////////////////////////


	/**
	 * Get the summary rows
	 */
	public List<AccessionSummaryRow> getSummaryRows() {
		return summaryRows;
	}

	/**
	 * Set the summary rows
	 */
	public void setSummaryRows(List<AccessionSummaryRow> summaryRows) {
		this.summaryRows = summaryRows;
	}


    /**
	 * Get the total number of possible results (-1 means unspecified)
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * Set the total number of possible results
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public ResultSetMetaData getMeta() {
		return meta;
	}

	public void setMeta(ResultSetMetaData meta) {
		this.meta = meta;
	}
}

package org.jax.mgi.searchtool_wi.utils;

/**
 * bean for collecting accession result data from fewi's accession/ URL
 */
public class AccessionSummaryRow {

	//-------------------
	// instance variables
	//-------------------

	private String objectType;
	private String displayType;
	private String logicalDb;
	private String accId;
	private String mgiLink;
	private String description;

	//--------
	// methods
	//--------
	
	public AccessionSummaryRow() {}
	
	public AccessionSummaryRow(String objectType, String displayType, String logicalDb, String accId, String mgiLink,
			String description) {
		super();
		this.objectType = objectType;
		this.displayType = displayType;
		this.logicalDb = logicalDb;
		this.accId = accId;
		this.mgiLink = mgiLink;
		this.description = description;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public String getLogicalDb() {
		return logicalDb;
	}

	public void setLogicalDb(String logicalDb) {
		this.logicalDb = logicalDb;
	}

	public String getAccId() {
		return accId;
	}

	public void setAccId(String accId) {
		this.accId = accId;
	}

	public String getMgiLink() {
		return mgiLink;
	}

	public void setMgiLink(String mgiLink) {
		this.mgiLink = mgiLink;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	} 
}

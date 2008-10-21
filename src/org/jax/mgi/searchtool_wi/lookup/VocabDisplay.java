package org.jax.mgi.searchtool_wi.lookup;

import java.util.*;

public class VocabDisplay {

    private String db_key;
    private String geneids;
    private String name;
    private String typeDisplay;
    private String vocabType;
    private String annotCount;
    private String annotObjectCount;
    private String annotDisplay;
    private String annotObjectType;
    private String markerCount;
    private String acc_id;
    private String dag_count;
    private String childids;
    private ArrayList <String> splitgenes;
    private ArrayList <String> splitchild;


    // Key
    public String getDbKey() {
        if (this.db_key == null) {
            return "";
        }
        return this.db_key;
    }
    public void setDbKey(String s) {
        db_key = s;
    }

    // Type
    public String getVocabType() {
        if (this.vocabType == null) {
            return "";
        }
        return this.vocabType;
    }
    public void setVocabType(String s) {
        vocabType = s;
    }

    public String getAcc_id() {
        if (this.acc_id == null) {
            return "";
        }
        return this.acc_id;
    }
    public void setAcc_id(String s) {
        acc_id = s;
    }

    public String getDag_count() {
        if (this.dag_count == null) {
            return "0";
        }
        return this.dag_count;
    }
    public void setDag_count(String s) {
        dag_count = s;
    }

    public String getGeneIds() {
        if (this.geneids == null) {
            return "";
        }
        return this.geneids;
    }
    public void setGeneIds(String s) {
        this.geneids = s;
        if (!s.equals(""))
        {
        this.splitgenes = new ArrayList <String> (Arrays.asList(geneids.split(",")));
        }
    }

    public Integer getGeneCount()
    {
    	if (this.geneids == null || this.geneids.equals("")) {
    		return 0;
    	}
    	else
    	{
    		return splitgenes.size();
    	}
    }

    public String getChildIds() {
        if (this.childids == null) {
            return "";
        }
        return this.childids;
    }
    public void setChildIds(String s) {
        this.childids = s;
        if (!s.equals(""))
        {
        this.splitchild = new ArrayList <String> (Arrays.asList(childids.split(",")));
        }
    }

    public ArrayList <String> getChildSplit()
    {
    	return this.splitchild;
    }

    public ArrayList <String> getGeneSplit()
    {
    	return this.splitgenes;
    }

    // Name
    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }
    public void setName(String s) {
        this.name = s;
    }

    // Type Display
    public String getTypeDisplay() {
        if (this.typeDisplay == null) {
            return "";
        }
        return this.typeDisplay;
    }
    public void setTypeDisplay(String s) {
        this.typeDisplay = s;
    }

    // Annotation Count
    public String getAnnotCount() {
        if (this.annotCount == null) {
            return "";
        }
        return this.annotCount;
    }
    public void setAnnotCount(String s) {
        this.annotCount = s;
    }

    // Annotation Object Count
    public String getAnnotObjectCount() {
        if (this.annotObjectCount == null) {
            return "";
        }
        return this.annotObjectCount;
    }
    public void setAnnotObjectCount(String s) {
        this.annotObjectCount = s;
    }

    // Annotation Object Type
    public String getAnnotObjectType() {
        if (this.annotObjectType == null) {
            return "Objects";
        }
        if (this.annotObjectType.equals("8"))
        {
        	return "Assays";
        }
        if (this.annotObjectType.equals("2"))
        {
        	return "Markers";
        }
        if (this.annotObjectType.equals("12"))
        {
        	return "Genotypes";
        }
        return "Objects";
    }
    public void setAnnotObjectType(String s) {
        this.annotObjectType = s;
    }

    // Marker Count
	    public String getMarkerCount() {
	        if (this.markerCount == null) {
	            return "";
	        }
	        return this.markerCount;
	    }
	    public void setMarkerCount(String s) {
	        this.markerCount = s;
    }
		public String getAnnotDisplay() {
			return annotDisplay;
		}
		public void setAnnotDisplay(String annotDisplay) {
			this.annotDisplay = annotDisplay;
		}

}

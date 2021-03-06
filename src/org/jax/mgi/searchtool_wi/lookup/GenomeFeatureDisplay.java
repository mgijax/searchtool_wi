package org.jax.mgi.searchtool_wi.lookup;

import java.util.*;

public class GenomeFeatureDisplay {

    private String db_key;
    private String objectType;
    private String symbol;
    private String name;
    private String markerType;
    private String chromosome;
    private String mgiID;
    private String cacheKey;
    private String strand = "";
    private String locDisplay = "";
    private String batchForwardValue = "";


    // Key
    public String getDbKey() {
        if (this.db_key == null) {
            return "";
        }
        return db_key;
    }
    public void setDbKey(String s) {
        db_key = s;
    }

    // Object type - marker/allele/etc...
    public String getObjectType() {
        if (this.objectType == null) {
            return "";
        }
        return objectType;
    }
    public void setObjectType(String s) {
        objectType = s;
    }

    // Type
    public String getMarkerType() {
        if (this.markerType == null) {
            return "";
        }
        return markerType;
    }
    public void setMarkerType(String s) {
        markerType = s;
    }

    // Symbol
    public String getSymbol() {
        if (this.symbol == null) {
            return "";
        }
        return symbol;
    }
    public void setSymbol(String s) {
        this.symbol = s;
    }

    // Name
    public String getName() {
        if (this.name == null) {
            return "";
        }
        return name;
    }
    public void setName(String s) {
        this.name = s;
    }

    // Chromosome
    public String getChromosome() {
        if (this.chromosome == null) {
            return "";
        }
        return chromosome;
    }
    public void setChromosome(String s) {
        this.chromosome = s;
    }

    // MGI ID
    public String getMgiId() {
        if (this.chromosome == null) {
            return "";
        }
        return mgiID;
    }
    public void setMgiId(String s) {
        this.mgiID = s;
    }


    // Strand
    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        if (strand != null) {
            this.strand = strand;
        }
    }

    // location display
    public String getLocDisplay() {
        return locDisplay;
    }

    public void setLocDisplay(String s) {
        if (s != null) {
            this.locDisplay = s;
        }
    }

    // forward to batch value
    public String getBatchForwardValue() {
        return batchForwardValue;
    }

    public void setBatchForwardValue(String s) {
        if (s != null) {
            this.batchForwardValue = s;
        }
    }


    // Cache Key
    public String getCacheKey() {
        if (this.cacheKey == null) {
            cacheKey = objectType + "_" + db_key;
        }
        return cacheKey;
    }

}

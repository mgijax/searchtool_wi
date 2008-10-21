package org.jax.mgi.searchtool_wi.lookup;

import java.util.*;

public class MarkerDisplay {

    private String db_key;
    private String symbol;
    private String name;
    private String markerType;
    private String chromosome;
    private String mgiID;


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
}

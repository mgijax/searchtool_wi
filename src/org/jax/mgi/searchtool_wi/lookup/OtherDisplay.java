package org.jax.mgi.searchtool_wi.lookup;

/**
 * This class encapsulates all of the information needed to display for an other object type.
 *
 * Its important to note, that for the other bucket, this is the information needed to display
 * for the object being pointed to by the match, not the accession id's display information.
 * @author mhall
 *
 */

public class OtherDisplay {

    private String db_key;
    private String name;
    private String dataType;
    private String qualifier1;

    /**
     * Gets the qualifier1, which is the subtype for the object.
     * @return
     */

    public String getQualifier1() {
        return qualifier1;
    }

    /**
     * Sets the qualifier1, which is the subtype for the object, if it has one.
     * @param qualifier1
     */

    public void setQualifier1(String qualifier1) {
        this.qualifier1 = qualifier1;
    }

    /**
     * Gets the db key
     *
     * @return
     */

    public String getDbKey() {
        if (this.db_key == null) {
            return "";
        }
        return this.db_key;
    }

    /**
     * Sets the db key.
     *
     * @param s
     */

    public void setDbKey(String s) {
        db_key = s;
    }

    /**
     * Return the type of the object.
     *
     * @return
     */

    public String getDataType() {
        if (this.dataType == null) {
            return "";
        }
        return this.dataType;
    }

    /**
     * Sets the type of the object.
     * @param s
     */

    public void setDataType(String s) {
        dataType = s;
    }

    /**
     * Returns the object display name.
     * @return
     */
    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }

    /**
     * Sets the display name.
     *
     * @param s
     */

    public void setName(String s) {
        this.name = s;
    }

}

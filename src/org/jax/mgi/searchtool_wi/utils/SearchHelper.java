package org.jax.mgi.searchtool_wi.utils;

import org.apache.lucene.search.Hit;
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
* @module SearchHelper
*
* The SearchHelper object provides utility methods to aid searches
*/
public class SearchHelper
{

  //----------------------//
  // Vocab Identification //
  //----------------------//

	  // Protein Isoform Ontology
	  public static boolean isProtIso (Hit hit) {

	    boolean isProtIso = false;
	    try{
	        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.PROTEOFORM_NAME)) {
	        	isProtIso = true;
	        }
	    }
	    catch (Exception e) {e.printStackTrace();}
	    return isProtIso;
	  }
	  public static boolean isProtIso (String str) {

	    boolean isProtIso = false;
	    if (str.equals(IndexConstants.PROTEOFORM_NAME)) {
	        isProtIso = true;
	    }
	    return isProtIso;
	  }

	
	// AD
  public static boolean isAD (Hit hit) {

    boolean isAD = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.AD_TYPE_NAME)) {
            isAD = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isAD;
  }
  public static boolean isAD (String str) {

    boolean isAD = false;
    if (str.equals(IndexConstants.AD_TYPE_NAME)) {
        isAD = true;
    }
    return isAD;
  }
  
  // EMAPA
  public static boolean isEMAPA (Hit hit) {

    boolean isAD = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.EMAPA_TYPE_NAME)) {
            isAD = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isAD;
  }
  public static boolean isEMAPA (String str) {

    boolean isAD = false;
    if (str.equals(IndexConstants.EMAPA_TYPE_NAME)) {
        isAD = true;
    }
    return isAD;
  }

  // EMAPS
  public static boolean isEMAPS (Hit hit) {

    boolean isAD = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.EMAPS_TYPE_NAME)) {
            isAD = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isAD;
  }
  public static boolean isEMAPS (String str) {

    boolean isAD = false;
    if (str.equals(IndexConstants.EMAPS_TYPE_NAME)) {
        isAD = true;
    }
    return isAD;
  }

  // MP
  public static boolean isMP (Hit hit) {

    boolean isMP = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.MP_TYPE_NAME)) {
            isMP = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isMP;
  }
  public static boolean isMP (String str) {

    boolean isMP = false;
    if (str.equals(IndexConstants.MP_TYPE_NAME)) {
        isMP = true;
    }
    return isMP;
  }


  // GO
  public static boolean isGO (Hit hit) {

    boolean isGO = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.GO_TYPE_NAME)) {
            isGO = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isGO;
  }
  public static boolean isGO (String str) {

    boolean isGO = false;
    if (str.equals(IndexConstants.GO_TYPE_NAME)) {
        isGO = true;
    }
    return isGO;
  }


  // Disease Ontology (DO)
  public static boolean isDo (Hit hit) {

    boolean isDo = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.DO_TYPE_NAME)) {
            isDo = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isDo;
  }
  public static boolean isDo (String str) {

    boolean isDo = false;
    if (str.equals(IndexConstants.DO_TYPE_NAME)) {
        isDo = true;
    }
    return isDo;
  }


  // Disease Ontology (DO) ORTHO
  public static boolean isDoORTHO (Hit hit) {

    boolean isDoORTHO = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.DO_ORTH_TYPE_NAME)) {
            isDoORTHO = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isDoORTHO;
  }
  public static boolean isDoORTHO (String str) {

    boolean isDoORTHO = false;
    if (str.equals(IndexConstants.DO_ORTH_TYPE_NAME)) {
        isDoORTHO = true;
    }
    return isDoORTHO;
  }


  // PIRSF
  public static boolean isPIRSF (Hit hit) {

    boolean isPIRSF = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.PIRSF_TYPE_NAME)) {
            isPIRSF = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isPIRSF;
  }
  public static boolean isPIRSF (String str) {

    boolean isPIRSF = false;
    if (str.equals(IndexConstants.PIRSF_TYPE_NAME)) {
        isPIRSF = true;
    }
    return isPIRSF;
  }


  // IP
  public static boolean isIP (Hit hit) {

    boolean isIP = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.INTERPRO_TYPE_NAME)) {
            isIP = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isIP;
  }
  public static boolean isIP (String str) {

    boolean isIP = false;
    if (str.equals(IndexConstants.INTERPRO_TYPE_NAME)) {
        isIP = true;
    }
    return isIP;
  }

}


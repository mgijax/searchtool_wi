package org.jax.mgi.searchtool_wi.utils;

import java.util.*;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
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


  // OMIM
  public static boolean isOMIM (Hit hit) {

    boolean isOMIM = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.OMIM_TYPE_NAME)) {
            isOMIM = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isOMIM;
  }
  public static boolean isOMIM (String str) {

    boolean isOMIM = false;
    if (str.equals(IndexConstants.OMIM_TYPE_NAME)) {
        isOMIM = true;
    }
    return isOMIM;
  }


  // OMIM ORTHO
  public static boolean isOMIMORTHO (Hit hit) {

    boolean isOMIMORTHO = false;
    try{
        if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.OMIM_ORTH_TYPE_NAME)) {
            isOMIMORTHO = true;
        }
    }
    catch (Exception e) {e.printStackTrace();}
    return isOMIMORTHO;
  }
  public static boolean isOMIMORTHO (String str) {

    boolean isOMIMORTHO = false;
    if (str.equals(IndexConstants.OMIM_ORTH_TYPE_NAME)) {
        isOMIMORTHO = true;
    }
    return isOMIMORTHO;
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


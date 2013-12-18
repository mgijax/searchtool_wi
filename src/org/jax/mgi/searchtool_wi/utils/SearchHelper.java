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
public class SearchHelper {

	//----------------------//
	// Vocab Identification //
	//----------------------//

//	// AD
//	public static boolean isAD(Hit hit) {
//		try {
//			return IndexConstants.AD_TYPE_NAME.equals(hit.get(IndexConstants.COL_OBJ_TYPE));
//		} catch (Exception e) {e.printStackTrace();}
//		return false;
//	}
//	public static boolean isAD(String str) {
//		return IndexConstants.AD_TYPE_NAME.equals(str);
//	}

	// MP
	public static boolean isMP(Hit hit) {
		try {
			return IndexConstants.MP_TYPE_NAME.equals(hit.get(IndexConstants.COL_OBJ_TYPE));
		} catch (Exception e) { e.printStackTrace(); }
		return false;
	}
	public static boolean isMP (String str) {
		return IndexConstants.MP_TYPE_NAME.equals(str);
	}

	// GO
	public static boolean isGO (Hit hit) {
		try {
			return IndexConstants.GO_TYPE_NAME.equals(hit.get(IndexConstants.COL_OBJ_TYPE));
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	public static boolean isGO (String str) {
		return IndexConstants.GO_TYPE_NAME.equals(str);
	}

	// OMIM
	public static boolean isOMIM (Hit hit) {
		try {
			return IndexConstants.OMIM_TYPE_NAME.equals(hit.get(IndexConstants.COL_OBJ_TYPE));
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	public static boolean isOMIM (String str) {
		return IndexConstants.OMIM_TYPE_NAME.equals(str);
	}

	// OMIM ORTHO
	public static boolean isOMIMORTHO (Hit hit) {
		try {
			return IndexConstants.OMIM_ORTH_TYPE_NAME.equals(hit.get(IndexConstants.COL_OBJ_TYPE));
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	public static boolean isOMIMORTHO (String str) {
		return IndexConstants.OMIM_ORTH_TYPE_NAME.equals(str);
	}

	// PIRSF
	public static boolean isPIRSF (Hit hit) {
		try {
			return IndexConstants.PIRSF_TYPE_NAME.equals(hit.get(IndexConstants.COL_OBJ_TYPE));
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	public static boolean isPIRSF (String str) {
		return IndexConstants.PIRSF_TYPE_NAME.equals(str);
	}

	// IP
	public static boolean isIP (Hit hit) {
		try {
			return IndexConstants.INTERPRO_TYPE_NAME.equals(hit.get(IndexConstants.COL_OBJ_TYPE));
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	public static boolean isIP (String str) {
		return IndexConstants.INTERPRO_TYPE_NAME.equals(str);
	}
	
	//EMAPA
	public static boolean isEMAPA(Hit hit) {
		try {
			return IndexConstants.EMAPA_TYPE_NAME.equals(hit.get(IndexConstants.COL_OBJ_TYPE));
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	public static boolean isEMAPA (String str) {
		return IndexConstants.EMAPA_TYPE_NAME.equals(str);
	}
}
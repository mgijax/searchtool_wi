package org.jax.mgi.searchtool_wi.lookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.jax.mgi.searchtool_wi.dataAccess.IndexReaderContainer;
import org.jax.mgi.shr.config.Configuration;
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
 * The VocabInfoCache object provides a persistent, in-memory
 * container for search tool related display data.
 *
 * @is a singleton for holding vocab display data
 * @has vocab display data
 * @does
 *
 */

public class VocabInfoCache {
	// Set up the logger
	private static Logger logger = Logger.getLogger(VocabInfoCache.class.getName());

	// Create the ONLY instance of this class
	private static final VocabInfoCache _theInstance = new VocabInfoCache();

	// private constructor to avoid outside instantiation
	private VocabInfoCache(){}

	private static boolean loadNeeded = true;

	// vocabID -> child ID list
	private static HashMap mpChildIds             = new HashMap();
	private static HashMap goChildIds             = new HashMap();
	private static HashMap adChildIds             = new HashMap();
	private static HashMap emapaChildIds          = new HashMap();
	private static HashMap emapsChildIds          = new HashMap();
	private static HashMap doChildIds             = new HashMap();
	private static HashMap doOrthoChildIds        = new HashMap();
	private static HashMap psChildIds             = new HashMap();
	private static HashMap ipChildIds             = new HashMap();

	// vocabID -> annotated genes list
	private static HashMap mpAnnotMarkers         = new HashMap();
	private static HashMap goAnnotMarkers         = new HashMap();
	private static HashMap adAnnotMarkers         = new HashMap();
	private static HashMap emapaAnnotMarkers      = new HashMap();
	private static HashMap emapsAnnotMarkers      = new HashMap();
	private static HashMap doAnnotMarkers         = new HashMap();
	private static HashMap doOrthoAnnotMarkers    = new HashMap();
	private static HashMap psAnnotMarkers         = new HashMap();
	private static HashMap protIsoAnnotMarkers    = new HashMap();
	private static HashMap ipAnnotMarkers         = new HashMap();

	// vocabID -> annotated alleles list
	private static HashMap mpAnnotAlleles         = new HashMap();
	private static HashMap doAnnotAlleles         = new HashMap();


	// IndexReaderContainer
	private static IndexReaderContainer irc;

	public static VocabInfoCache getVocabInfoCache(Configuration stConfig) {
		irc = IndexReaderContainer.getIndexReaderContainer(stConfig);

		if (loadNeeded) {
			load(stConfig);
		}
		return _theInstance;
	}

	public static VocabInfoCache getVocabInfoCache() {
		return _theInstance;
	}


	public List getMpChildTerms(String termKey){
		return (List)mpChildIds.get(termKey);
	}
	public List getGoChildTerms(String termKey){
		return (List)goChildIds.get(termKey);
	}
	public List getAdChildTerms(String termKey){
		return (List)adChildIds.get(termKey);
	}
	public List getEmapaChildTerms(String termKey){
		return (List)emapaChildIds.get(termKey);
	}
	public List getEmapsChildTerms(String termKey){
		return (List)emapsChildIds.get(termKey);
	}
	public List getDoChildTerms(String termKey){
		return (List)doChildIds.get(termKey);
	}
	public List getDoOrthoChildTerms(String termKey){
		return (List)doOrthoChildIds.get(termKey);
	}
	public List getPsChildTerms(String termKey){
		return (List)psChildIds.get(termKey);
	}
	public List getIpChildTerms(String termKey){
		return (List)ipChildIds.get(termKey);
	}
	public List getMpAnnotMarkers(String termKey){
		return (List)mpAnnotMarkers.get(termKey);
	}
	public List getGoAnnotMarkers(String termKey){
		return (List)goAnnotMarkers.get(termKey);
	}
	public List getAdAnnotMarkers(String termKey){
		return (List)adAnnotMarkers.get(termKey);
	}
	public List getEmapaAnnotMarkers(String termKey){
		return (List)emapaAnnotMarkers.get(termKey);
	}
	public List getEmapsAnnotMarkers(String termKey){
		return (List)emapsAnnotMarkers.get(termKey);
	}
	public List getDoAnnotMarkers(String termKey){
		return (List)doAnnotMarkers.get(termKey);
	}
	public List getDoOrthoAnnotMarkers(String termKey){
		return (List)doOrthoAnnotMarkers.get(termKey);
	}
	public List getPsAnnotMarkers(String termKey){
		return (List)psAnnotMarkers.get(termKey);
	}
	public List getProtIsoAnnotMarkers(String termKey){
		return (List)protIsoAnnotMarkers.get(termKey);
	}
	public List getIpAnnotMarkers(String termKey){
		return (List)ipAnnotMarkers.get(termKey);
	}
	public List getMpAnnotAlleles(String termKey){
		return (List)mpAnnotAlleles.get(termKey);
	}
	public List getDoAnnotAlleles(String termKey){
		return (List)doAnnotAlleles.get(termKey);
	}

	private static void load(Configuration stConfig) {
		Document doc;

		logger.info("VocabInfoCache loading...");
		try {

			IndexReader ir = irc.getGenomeFeatureVocabReader();

			// for each index entry, map the term to it's annotated objects
			for (int count=0; count<ir.maxDoc(); count++ ) {

				doc = ir.document(count);

				// gather the child IDs of a given vocab term
				if(!doc.get(IndexConstants.COL_CHILD_IDS).equals("")) {

					ArrayList objectKeys = new ArrayList( Arrays.asList(doc.get(IndexConstants.COL_CHILD_IDS).split(",")) );

					// put the data in the proper mapping
					if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.AD_TYPE_NAME)) {
						adChildIds.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.EMAPA_TYPE_NAME)){
						emapaChildIds.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.EMAPS_TYPE_NAME)){
						emapsChildIds.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.MP_TYPE_NAME)){
						mpChildIds.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.GO_TYPE_NAME)){
						goChildIds.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.DO_TYPE_NAME)){
						doChildIds.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.DO_ORTH_TYPE_NAME)){
						doOrthoChildIds.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.INTERPRO_TYPE_NAME)){
						ipChildIds.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.PIRSF_TYPE_NAME)){
						psChildIds.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					}
				}
				// gather the objects annotated to this vocab term
				if( !doc.get(IndexConstants.COL_FEATURE_IDS).equals("") && doc.get(IndexConstants.COL_OBJ_TYPE).equals("MARKER")) {
					ArrayList objectKeys = new ArrayList( Arrays.asList(doc.get(IndexConstants.COL_FEATURE_IDS).split(",")) );

					// put the data in the proper mapping
					if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.AD_TYPE_NAME)) {
						adAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.EMAPA_TYPE_NAME)){
						emapaAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.EMAPS_TYPE_NAME)){
						emapsAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.MP_TYPE_NAME)){
						mpAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.GO_TYPE_NAME)){
						goAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.DO_TYPE_NAME)){
						doAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.DO_ORTH_TYPE_NAME)){
						doOrthoAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.INTERPRO_TYPE_NAME)){
						ipAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.PIRSF_TYPE_NAME)){
						psAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.PROTEOFORM_NAME)){
						protIsoAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), objectKeys);
					}
				}

				// gather the alleles annotated to this vocab term
				if( !doc.get(IndexConstants.COL_FEATURE_IDS).equals("") && doc.get(IndexConstants.COL_OBJ_TYPE).equals("ALLELE")) {

					ArrayList alleleKeys = new ArrayList( Arrays.asList(doc.get(IndexConstants.COL_FEATURE_IDS).split(",")) );

					// put the data in the proper mapping
					if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.MP_TYPE_NAME)){
						mpAnnotAlleles.put(doc.get(IndexConstants.COL_DB_KEY), alleleKeys);
					} else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.DO_TYPE_NAME)){
						doAnnotAlleles.put(doc.get(IndexConstants.COL_DB_KEY), alleleKeys);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		loadNeeded = false;
		logger.info("GenomeFeature VocabSearchCache finished loading...");
	}

}


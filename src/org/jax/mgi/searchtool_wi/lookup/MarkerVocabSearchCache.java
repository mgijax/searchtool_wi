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
* The MarkerVocabSearchCache object provides a persistent, in-memory
* container for search tool related display data.
*
* @is a singleton for holding vocab display data
* @has vocab display data
* @does
*
*/

public class MarkerVocabSearchCache
{
    // Set up the logger

    private static Logger log = Logger.getLogger(MarkerVocabSearchCache.class.getName());

    //------------------//
    //Singleton setup
    //------------------//

    // Create the ONLY instance of this class
    private static final MarkerVocabSearchCache _theInstance =
        new MarkerVocabSearchCache();

    // private constructor to avoid outside instantiation
    private MarkerVocabSearchCache(){}

    //------------------//
    // class variables
    //------------------//

    private static boolean loadNeeded = true;

    // vocabID -> child ID list
    private static HashMap mpChildIds             = new HashMap();
    private static HashMap goChildIds             = new HashMap();
    private static HashMap adChildIds             = new HashMap();
    private static HashMap omimChildIds           = new HashMap();
    private static HashMap omimOrthoChildIds      = new HashMap();
    private static HashMap psChildIds             = new HashMap();
    private static HashMap ipChildIds             = new HashMap();

    // vocabID -> annotated genes list
    private static HashMap mpAnnotMarkers         = new HashMap();
    private static HashMap goAnnotMarkers         = new HashMap();
    private static HashMap adAnnotMarkers         = new HashMap();
    private static HashMap omimAnnotMarkers       = new HashMap();
    private static HashMap omimOrthoAnnotMarkers  = new HashMap();
    private static HashMap psAnnotMarkers         = new HashMap();
    private static HashMap ipAnnotMarkers         = new HashMap();

    // vocabID -> annotated alleles list
    private static HashMap mpAnnotAlleles         = new HashMap();
    private static HashMap omimAnnotAlleles       = new HashMap();


    // IndexReaderContainer
    private static IndexReaderContainer irc;

    //----------------------------//
    // Singleton retrieval method
    //----------------------------//

    public static MarkerVocabSearchCache getMarkerVocabSearchCache(Configuration stConfig)
    {
        irc = IndexReaderContainer.getIndexReaderContainer(stConfig);

        if (loadNeeded) {
            load(stConfig);
        }
        return _theInstance;
    }

    public static MarkerVocabSearchCache getMarkerVocabSearchCache()
    {
        return _theInstance;
    }


    //------------------------------//
    // Child Term Retrieval Methods //
    //------------------------------//

    public List getMpChildTerms(String termKey){
        return (List)mpChildIds.get(termKey);
    }
    public List getGoChildTerms(String termKey){
        return (List)goChildIds.get(termKey);
    }
    public List getAdChildTerms(String termKey){
        return (List)adChildIds.get(termKey);
    }
    public List getOmimChildTerms(String termKey){
        return (List)omimChildIds.get(termKey);
    }
    public List getOmimOrthoChildTerms(String termKey){
        return (List)omimOrthoChildIds.get(termKey);
    }
    public List getPsChildTerms(String termKey){
        return (List)psChildIds.get(termKey);
    }
    public List getIpChildTerms(String termKey){
        return (List)ipChildIds.get(termKey);
    }


    //------------------------------------//
    // Annotated Marker Retrieval Methods //
    //------------------------------------//

    public List getMpAnnotMarkers(String termKey){
        return (List)mpAnnotMarkers.get(termKey);
    }
    public List getGoAnnotMarkers(String termKey){
        return (List)goAnnotMarkers.get(termKey);
    }
    public List getAdAnnotMarkers(String termKey){
        return (List)adAnnotMarkers.get(termKey);
    }
    public List getOmimAnnotMarkers(String termKey){
        return (List)omimAnnotMarkers.get(termKey);
    }
    public List getOmimOrthoAnnotMarkers(String termKey){
        return (List)omimOrthoAnnotMarkers.get(termKey);
    }
    public List getPsAnnotMarkers(String termKey){
        return (List)psAnnotMarkers.get(termKey);
    }
    public List getIpAnnotMarkers(String termKey){
        return (List)ipAnnotMarkers.get(termKey);
    }


    //------------------------------------//
    // Annotated Allele Retrieval Methods //
    //------------------------------------//

    public List getMpAnnotAlleles(String termKey){
        return (List)mpAnnotAlleles.get(termKey);
    }
    public List getOmimAnnotAlleles(String termKey){
        return (List)omimAnnotAlleles.get(termKey);
    }


    /////////////////
    //private methods
    /////////////////

    private static void load(Configuration stConfig)
    {
        Document doc;

        log.info("MarkerVocabSearchCache loading...");
        try {

          IndexReader ir = irc.getMarkerVocabDagReader();

          // for each index entry, map the term to it's annotated markers
          for (int count=0; count<ir.maxDoc(); count++ ) {

            doc = ir.document(count);

            // gather the child IDs of a given vocab term
            if(!doc.get(IndexConstants.COL_CHILD_IDS).equals("")) {

                ArrayList markerKeys =
                    new ArrayList( Arrays.asList(doc.get(IndexConstants.COL_CHILD_IDS).split(",")) );

                // put the data in the proper mapping
                if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.AD_TYPE_NAME)) {
                  adChildIds.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.MP_TYPE_NAME)){
                  mpChildIds.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.GO_TYPE_NAME)){
                  goChildIds.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.OMIM_TYPE_NAME)){
                  omimChildIds.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.OMIM_ORTH_TYPE_NAME)){
                  omimOrthoChildIds.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.INTERPRO_TYPE_NAME)){
                  ipChildIds.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.PIRSF_TYPE_NAME)){
                  psChildIds.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
            }
            // gather the markers annotated to this vocab term
            if( !doc.get(IndexConstants.COL_FEATURE_IDS).equals("")
              && doc.get(IndexConstants.COL_OBJ_TYPE).equals("MARKER"))

            {
                ArrayList markerKeys =
                    new ArrayList( Arrays.asList(doc.get("feature_ids").split(",")) );

                // put the data in the proper mapping
                if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.AD_TYPE_NAME)) {
                  adAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.MP_TYPE_NAME)){
                  mpAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.GO_TYPE_NAME)){
                  goAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.OMIM_TYPE_NAME)){
                  omimAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.OMIM_ORTH_TYPE_NAME)){
                  omimOrthoAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.INTERPRO_TYPE_NAME)){
                  ipAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.PIRSF_TYPE_NAME)){
                  psAnnotMarkers.put(doc.get(IndexConstants.COL_DB_KEY), markerKeys);
                }
            }

            // gather the alleles annotated to this vocab term
            if( !doc.get(IndexConstants.COL_FEATURE_IDS).equals("")
              && doc.get(IndexConstants.COL_OBJ_TYPE).equals("ALLELE"))

            {

                ArrayList alleleKeys =
                    new ArrayList( Arrays.asList(doc.get("feature_ids").split(",")) );

                // put the data in the proper mapping
                if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.MP_TYPE_NAME)){
                  mpAnnotAlleles.put(doc.get(IndexConstants.COL_DB_KEY), alleleKeys);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.OMIM_TYPE_NAME)){
                  omimAnnotAlleles.put(doc.get(IndexConstants.COL_DB_KEY), alleleKeys);
                }
                else {
					System.out.println("-------?-----" + doc.get(IndexConstants.COL_VOCABULARY));
				}


            }

          }
        }
        catch (Exception e) {
              e.printStackTrace();
        }

        loadNeeded = false;
        log.info("GenomeFeature VocabSearchCache finished loading...");
        return;
    }

}


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
    private static HashMap mpChildIds                   = new HashMap();
    private static HashMap goChildIds                   = new HashMap();
    private static HashMap adChildIds                   = new HashMap();
    private static HashMap omimChildIds                 = new HashMap();
    private static HashMap omimOrthoChildIds            = new HashMap();
    private static HashMap psChildIds                   = new HashMap();
    private static HashMap ipChildIds                   = new HashMap();

    // vocabID -> annotated genes list
    private static HashMap mpAnnotMarkers               = new HashMap();
    private static HashMap goAnnotMarkers               = new HashMap();
    private static HashMap adAnnotMarkers               = new HashMap();
    private static HashMap omimAnnotMarkers             = new HashMap();
    private static HashMap omimOrthoAnnotMarkers        = new HashMap();
    private static HashMap psAnnotMarkers               = new HashMap();
    private static HashMap ipAnnotMarkers               = new HashMap();

    // vocabID -> raw_data (display data)
    private static HashMap mpAnnotDisplayData           = new HashMap();
    private static HashMap goAnnotDisplayData           = new HashMap();
    private static HashMap adAnnotDisplayData           = new HashMap();
    private static HashMap omimAnnotDisplayData         = new HashMap();
    private static HashMap omimOrthoAnnotDisplayData    = new HashMap();
    private static HashMap psAnnotDisplayData           = new HashMap();
    private static HashMap ipAnnotDisplayData           = new HashMap();

    // vocabID -> type display (Displayprefix)
    private static HashMap mpAnnotTypeDisplay           = new HashMap();
    private static HashMap goAnnotTypeDisplay           = new HashMap();
    private static HashMap adAnnotTypeDisplay           = new HashMap();
    private static HashMap omimAnnotTypeDisplay         = new HashMap();
    private static HashMap omimOrthoAnnotTypeDisplay    = new HashMap();
    private static HashMap psAnnotTypeDisplay           = new HashMap();
    private static HashMap ipAnnotTypeDisplay           = new HashMap();

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
    // Display Data Retrieval Methods //
    //------------------------------------//

    public List getMpAnnotDisplayData(String termKey){
        return (List)mpAnnotDisplayData.get(termKey);
    }
    public List getGoAnnotDisplayData(String termKey){
        return (List)goAnnotDisplayData.get(termKey);
    }
    public List getAdAnnotDisplayData(String termKey){
        return (List)adAnnotDisplayData.get(termKey);
    }
    public List getOmimAnnotDisplayData(String termKey){
        return (List)omimAnnotDisplayData.get(termKey);
    }
    public List getOmimOrthoAnnotDisplayData(String termKey){
        return (List)omimOrthoAnnotDisplayData.get(termKey);
    }
    public List getPsAnnotDisplayData(String termKey){
        return (List)psAnnotDisplayData.get(termKey);
    }
    public List getIpAnnotDisplayData(String termKey){
        return (List)ipAnnotDisplayData.get(termKey);
    }

    //------------------------------------//
    // Data Type Retrieval Methods //
    //------------------------------------//

    public List getMpAnnotTypeDisplay(String termKey){
        return (List)mpAnnotTypeDisplay.get(termKey);
    }
    public List getGoAnnotTypeDisplay(String termKey){
        return (List)goAnnotTypeDisplay.get(termKey);
    }
    public List getAdAnnotTypeDisplay(String termKey){
        return (List)adAnnotTypeDisplay.get(termKey);
    }
    public List getOmimAnnotTypeDisplay(String termKey){
        return (List)omimAnnotTypeDisplay.get(termKey);
    }
    public List getOmimOrthoAnnotTypeDisplay(String termKey){
        return (List)omimOrthoAnnotTypeDisplay.get(termKey);
    }
    public List getPsAnnotTypeDisplay(String termKey){
        return (List)psAnnotTypeDisplay.get(termKey);
    }
    public List getIpAnnotTypeDisplay(String termKey){
        return (List)ipAnnotTypeDisplay.get(termKey);
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
            if(!doc.get(IndexConstants.COL_GENE_IDS).equals("")) {

                ArrayList markerKeys =
                    new ArrayList( Arrays.asList(doc.get("gene_ids").split(",")) );

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

            // gather the display data to this vocab term
            if(!doc.get(IndexConstants.COL_RAW_DATA).equals("")) {

                String displayData = doc.get(IndexConstants.COL_RAW_DATA);

                // put the data in the proper mapping
                if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.AD_TYPE_NAME)) {
                  adAnnotDisplayData.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.MP_TYPE_NAME)){
                  mpAnnotDisplayData.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.GO_TYPE_NAME)){
                  goAnnotDisplayData.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.OMIM_TYPE_NAME)){
                  omimAnnotDisplayData.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.OMIM_ORTH_TYPE_NAME)){
                  omimOrthoAnnotDisplayData.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.INTERPRO_TYPE_NAME)){
                  ipAnnotDisplayData.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.PIRSF_TYPE_NAME)){
                  psAnnotDisplayData.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
            }

            // gather the type display data to this vocab term
            if(!doc.get(IndexConstants.COL_TYPE_DISPLAY).equals("")) {

                String displayData = doc.get(IndexConstants.COL_RAW_DATA);

                // put the data in the proper mapping
                if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.AD_TYPE_NAME)) {
                  adAnnotTypeDisplay.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.MP_TYPE_NAME)){
                  mpAnnotTypeDisplay.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.GO_TYPE_NAME)){
                  goAnnotTypeDisplay.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.OMIM_TYPE_NAME)){
                  omimAnnotTypeDisplay.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.OMIM_ORTH_TYPE_NAME)){
                  omimOrthoAnnotTypeDisplay.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.INTERPRO_TYPE_NAME)){
                  ipAnnotTypeDisplay.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
                else if (doc.get(IndexConstants.COL_VOCABULARY).equals(IndexConstants.PIRSF_TYPE_NAME)){
                  psAnnotTypeDisplay.put(doc.get(IndexConstants.COL_DB_KEY), displayData);
                }
            }
          }
        }
        catch (Exception e) {
              e.printStackTrace();
        }

        loadNeeded = false;
        log.info("MarkerVocabSearchCache finished loading...");
        return;
    }

}


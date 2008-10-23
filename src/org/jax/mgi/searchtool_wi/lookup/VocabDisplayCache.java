package org.jax.mgi.searchtool_wi.lookup;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.jax.mgi.searchtool_wi.dataAccess.IndexReaderContainer;
import org.jax.mgi.searchtool_wi.exception.CacheNotLoadedException;
import org.jax.mgi.searchtool_wi.matches.VocabMatch;
import org.jax.mgi.searchtool_wi.matches.MarkerVocabMatch;
import org.jax.mgi.searchtool_wi.results.QS_VocabResult;
import org.jax.mgi.shr.config.Configuration;

import org.jax.mgi.shr.searchtool.IndexConstants;

//////////////////////////////////////////////////////////////////////////////
/**
* @module VocabDisplayCache
*/

/**  The VocabDisplayCache object provides a presistent, in-memory container
*   for search tool related display data.
*
* @is a singleton for holding vocab display data
* @has vocab display data
* @does
*
*/
public class VocabDisplayCache
{

    // Set up the logger
    private static Logger log = Logger.getLogger(VocabDisplayCache.class.getName());
    private static IndexReaderContainer irc = null;

    /////////////////
    //Singleton setup
    /////////////////

    // Create the ONLY instance of this class
    private static final VocabDisplayCache _theInstance =
        new VocabDisplayCache();

    // private constructor to avoid outside instantiation
    private VocabDisplayCache(){}

    /////////////////////
    // class variables
    /////////////////////

    // vocabKey -> VocabDisplay object
    private static Boolean loadNeeded = true;

    private static HashMap<String, VocabDisplay> vocabCacheMap;
    private static HashMap<String, VocabDisplay> vocabADCacheMap;

    /////////////////////////////
    // Singleton retrieval method
    /////////////////////////////

    public static VocabDisplayCache getVocabDisplayCache(Configuration stConfig)
    {
        if (irc == null)
        {
            irc = IndexReaderContainer.getIndexReaderContainer(stConfig);
        }
        load(stConfig);
        return _theInstance;
    }

    public static VocabDisplayCache getVocabDisplayCache()
    {
        return _theInstance;
    }

    /////////////////////////
    // Data Retrieval Methods
    /////////////////////////

     public VocabDisplay getVocab(QS_VocabResult s)
    {

        if (s.getVocabulary().equals(IndexConstants.AD_TYPE_NAME)) {
            return (VocabDisplay)vocabADCacheMap.get(s.getDbKey());
        }
        else {
            return (VocabDisplay)vocabCacheMap.get(s.getDbKey());
        }
    }

     public VocabDisplay getVocab(VocabMatch vm)
    {

        VocabDisplay vocabDisplay;

        if (vm.getVocabulary().equals(IndexConstants.AD_TYPE_NAME)) {
            vocabDisplay = (VocabDisplay)vocabADCacheMap.get(vm.getDbKey());
        }
        else {
            vocabDisplay = (VocabDisplay)vocabCacheMap.get(vm.getDbKey());
        }
        return vocabDisplay;
    }

     public VocabDisplay getVocab(MarkerVocabMatch vm)
    {

        VocabDisplay vocabDisplay;

        if (vm.getVocabulary().equals(IndexConstants.AD_TYPE_NAME)) {
            vocabDisplay = (VocabDisplay)vocabADCacheMap.get(vm.getDbKey());
        }
        else {
            vocabDisplay = (VocabDisplay)vocabCacheMap.get(vm.getDbKey());
        }
        return vocabDisplay;
    }

     public VocabDisplay getParentVocab(MarkerVocabMatch vm)
    {

        VocabDisplay vocabDisplay;

        if (vm.getVocabulary().equals(IndexConstants.AD_TYPE_NAME)) {
            vocabDisplay = (VocabDisplay)vocabADCacheMap.get(vm.getAncestorKey());
        }
        else {
            vocabDisplay = (VocabDisplay)vocabCacheMap.get(vm.getAncestorKey());
        }
        return vocabDisplay;
    }

    public VocabDisplay getAdTerm(String s) {
        return (VocabDisplay)vocabADCacheMap.get(s);
    }
    public VocabDisplay getMpTerm(String s) {
        return (VocabDisplay)vocabCacheMap.get(s);
    }
    public VocabDisplay getGoTerm(String s) {
        return (VocabDisplay)vocabCacheMap.get(s);
    }
    public VocabDisplay getOmimTerm(String s) {
        return (VocabDisplay)vocabCacheMap.get(s);
    }
    public VocabDisplay getPirsfTerm(String s) {
        return (VocabDisplay)vocabCacheMap.get(s);
    }
    public VocabDisplay getIpTerm(String s) {
        return (VocabDisplay)vocabCacheMap.get(s);
    }

    /////////////////
    //private methods
    /////////////////

    private static void getDAGMarkerCounts(HashMap <String, VocabDisplay> vocCache)
    {
        VocabDisplay current;
        VocabDisplay child;
        String key;
        String childKey;
        ArrayList <String> childList;
        ArrayList <String> geneList;
        HashSet <String> allGenes;
        int count;
        for (Iterator <String> iter = vocCache.keySet().iterator(); iter.hasNext();)
        {
            count = 0;
            allGenes = new HashSet <String> ();
            key = iter.next();
            current = vocCache.get(key);


            if (! current.getGeneIds().equals(""))
            {
                geneList = current.getGeneSplit();

                for (Iterator <String> geneIter = geneList.iterator(); geneIter.hasNext();)
                {
                    allGenes.add(geneIter.next());
                }
            }
            if (! current.getChildIds().equals(""))
            {

                childList = current.getChildSplit();

                for (Iterator <String> iter2 = childList.iterator(); iter2.hasNext();)
                {
                    childKey = iter2.next();

                    if (vocCache.containsKey(childKey))
                    {
                    child = vocCache.get(childKey);
                    if (!child.getGeneIds().equals(""))
                    {
                        geneList = child.getGeneSplit();
                        for (Iterator <String> geneIter = geneList.iterator(); geneIter.hasNext();)
                        {
                            allGenes.add(geneIter.next());
                        }
                    }
                    }
                }
            }
            current.setDag_count(new Integer(allGenes.size()).toString());
        }
    }

    private static void load(Configuration stConfig)
    {
        Document doc;
        HashMap <String, VocabDisplay> newVocabCache = new HashMap <String, VocabDisplay>();
        HashMap <String, VocabDisplay> newVocabADCache = new HashMap <String, VocabDisplay>();

        log.info("VocabDisplayCache loading...");
        try {

          IndexReader ir = irc.getVocabDisplayReader();

          for (int count=0; count<ir.maxDoc(); count++ ) {

            VocabDisplay thisVocabDisplay = new VocabDisplay();

            // flesh out the display cache object
            doc = ir.document(count);
            thisVocabDisplay.setDbKey(doc.get(IndexConstants.COL_DB_KEY));
            thisVocabDisplay.setGeneIds(doc.get(IndexConstants.COL_GENE_IDS));
            thisVocabDisplay.setName(doc.get(IndexConstants.COL_CONTENTS));
            thisVocabDisplay.setVocabType(doc.get(IndexConstants.COL_VOCABULARY));
            thisVocabDisplay.setTypeDisplay(doc.get(IndexConstants.COL_TYPE_DISPLAY));
            thisVocabDisplay.setAnnotCount(doc.get(IndexConstants.COL_ANNOT_COUNT));
            thisVocabDisplay.setAnnotObjectCount(doc.get(IndexConstants.COL_ANNOT_OBJECTS));
            thisVocabDisplay.setAnnotDisplay(doc.get(IndexConstants.COL_ANNOT_DISPLAY));
            thisVocabDisplay.setAnnotObjectType(doc.get(IndexConstants.COL_ANNOT_OBJECT_TYPE));
            thisVocabDisplay.setMarkerCount(doc.get(IndexConstants.COL_MARKER_COUNT));
            thisVocabDisplay.setAcc_id(doc.get(IndexConstants.COL_ACC_ID));
            thisVocabDisplay.setChildIds(doc.get(IndexConstants.COL_CHILD_IDS));

            if (thisVocabDisplay.getVocabType().equals("AD"))
            {
                newVocabADCache.put(thisVocabDisplay.getDbKey(), thisVocabDisplay);
            }
            else
            {
            newVocabCache.put(thisVocabDisplay.getDbKey(), thisVocabDisplay);
            }

          }

        }
          catch (Exception e) {
        }

        vocabCacheMap = newVocabCache;
        vocabADCacheMap = newVocabADCache;

        getDAGMarkerCounts(vocabCacheMap);
        getDAGMarkerCounts(vocabADCacheMap);

        loadNeeded = false;
        log.info("VocabDisplayCache finished loading...");

        return;
    }

}


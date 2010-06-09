package org.jax.mgi.searchtool_wi.lookup;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.jax.mgi.searchtool_wi.dataAccess.IndexReaderContainer;
import org.jax.mgi.searchtool_wi.exception.QuickSearchException;
import org.jax.mgi.searchtool_wi.results.GenomeFeatureResult;
import org.jax.mgi.shr.config.Configuration;
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
 *  The GenomeFeatureDisplayCache object provides a persistent, in-memory container
 *  for search tool related display data.  We pre-generate the display values for
 *  run-time performance
 */
public class GenomeFeatureDisplayCache
{
    // Set up the logger
    private static Logger log = Logger.getLogger(GenomeFeatureDisplayCache.class.getName());
    /////////////////
    //Singleton setup
    /////////////////

    // Create the ONLY instance of this class
    private static final GenomeFeatureDisplayCache _theInstance =
        new GenomeFeatureDisplayCache();

    // private constructor to avoid outside instantiation
    private GenomeFeatureDisplayCache(){}

    /////////////////////
    // class variables
    /////////////////////

    // markerKey -> GenomeFeatureDisplay object
    private static HashMap genomeFeatureCacheMap;

    private static Boolean loadNeeded = true;

    private static IndexReaderContainer irc = null;


    /////////////////////////////
    // Singleton retrieval method
    /////////////////////////////

    public static GenomeFeatureDisplayCache
        getGenomeFeatureDisplayCache(Configuration config)
    {

        if (irc == null) {
          irc = IndexReaderContainer.getIndexReaderContainer(config);
        }
        load(config);

        return _theInstance;
    }

    public static GenomeFeatureDisplayCache getGenomeFeatureDisplayCache() {
        if (loadNeeded) {
            throw new QuickSearchException(
                    "You tried use an unintialized GenomeFeatureDisplayCache,"
                            + " it must first be initialized with a config "
                            + "object.");
        } else {
            return _theInstance;
        }
    }


    /////////////////////////
    // Data Retrieval Methods
    /////////////////////////

    public GenomeFeatureDisplay getGenomeFeature(GenomeFeatureResult gfResult)
    {

        GenomeFeatureDisplay thisGenomeFeatureDisplay
            = (GenomeFeatureDisplay)genomeFeatureCacheMap.get(gfResult.getCacheKey());

        //if we don't have the requested object, returning an empty
        if (thisGenomeFeatureDisplay==null) {
            thisGenomeFeatureDisplay = new GenomeFeatureDisplay();
        }

        return thisGenomeFeatureDisplay;
    }

    public GenomeFeatureDisplay getMarker(String dbKey)
    {
        GenomeFeatureDisplay thisGenomeFeatureDisplay
            = (GenomeFeatureDisplay)genomeFeatureCacheMap.get(dbKey);

        //if we don't have the requested object, returning an empty
        if (thisGenomeFeatureDisplay==null) {
            thisGenomeFeatureDisplay = new GenomeFeatureDisplay();
        }

        return thisGenomeFeatureDisplay;
    }


    /////////////////
    //private methods
    /////////////////

    private static void load(Configuration stConfig)
    {

        Document doc;
        String cacheKey;
        HashMap newMarkerCache = new HashMap();

        log.info("GenomeFeatureDisplayCache loading...");
        try {

          IndexReader ir = irc.getGenomeFeatureDisplayReader();

          for (int count=0; count<ir.maxDoc(); count++ ) {

            GenomeFeatureDisplay thisGenomeFeatureDisplay = new GenomeFeatureDisplay();

            // flesh out the display cache object
            doc = ir.document(count);

			cacheKey = doc.get(IndexConstants.COL_OBJECT_TYPE)
			  + "_" + doc.get(IndexConstants.COL_DB_KEY);

            thisGenomeFeatureDisplay.setDbKey(doc.get(IndexConstants.COL_DB_KEY));
            thisGenomeFeatureDisplay.setObjectType(doc.get(IndexConstants.COL_OBJECT_TYPE));
            thisGenomeFeatureDisplay.setSymbol(doc.get(IndexConstants.COL_FEATURE_SYMBOL));
            thisGenomeFeatureDisplay.setName(doc.get(IndexConstants.COL_FEATURE_NAME));
            thisGenomeFeatureDisplay.setMarkerType(doc.get(IndexConstants.COL_FEATURE_TYPE));
            thisGenomeFeatureDisplay.setChromosome(doc.get(IndexConstants.COL_CHROMOSOME));
            thisGenomeFeatureDisplay.setMgiId(doc.get(IndexConstants.COL_MGI_ID));
            thisGenomeFeatureDisplay.setStrand(doc.get(IndexConstants.COL_STRAND));
            thisGenomeFeatureDisplay.setLocDisplay(doc.get(IndexConstants.COL_LOC_DISPLAY));
            thisGenomeFeatureDisplay.setBatchForwardValue(doc.get(IndexConstants.COL_BATCH_FORWARD_VALUE));

            newMarkerCache.put(thisGenomeFeatureDisplay.getCacheKey(), thisGenomeFeatureDisplay);

          }

        }
          catch (Exception e) {
              e.printStackTrace();
        }

        genomeFeatureCacheMap = newMarkerCache;
        loadNeeded = false;
        log.info("GenomeFeatureDisplayCache finished loading...");

        return;
    }

}


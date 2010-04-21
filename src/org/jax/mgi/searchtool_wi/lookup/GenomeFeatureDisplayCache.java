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

    // markerKey -> MarkerDisplay object
    private static HashMap genomeFeatureCacheMap;

    private static Boolean loadNeeded = true;

    private static IndexReaderContainer irc = null;


    /////////////////////////////
    // Singleton retrieval method
    /////////////////////////////

    public static GenomeFeatureDisplayCache
        getGenomeFeatureDisplayCache(Configuration config)
    {

        if (irc == null)
        {
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

    public MarkerDisplay getGenomeFeature(GenomeFeatureResult gfResult)
    {

        MarkerDisplay thisMarkerDisplay
            = (MarkerDisplay)genomeFeatureCacheMap.get(gfResult.getCacheKey());

        //if we don't have the requested object, returning an empty
        if (thisMarkerDisplay==null) {
            thisMarkerDisplay = new MarkerDisplay();
        }

        return thisMarkerDisplay;
    }

    public MarkerDisplay getMarker(String dbKey)
    {
        MarkerDisplay thisMarkerDisplay
            = (MarkerDisplay)genomeFeatureCacheMap.get(dbKey);

        //if we don't have the requested object, returning an empty
        if (thisMarkerDisplay==null) {
            thisMarkerDisplay = new MarkerDisplay();
        }

        return thisMarkerDisplay;
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

          IndexReader ir = irc.getMarkerDisplayReader();

          for (int count=0; count<ir.maxDoc(); count++ ) {

            MarkerDisplay thisMarkerDisplay = new MarkerDisplay();

            // flesh out the display cache object
            doc = ir.document(count);

			cacheKey = doc.get(IndexConstants.COL_OBJECT_TYPE)
			  + "_" + doc.get(IndexConstants.COL_DB_KEY);

            thisMarkerDisplay.setDbKey(doc.get(IndexConstants.COL_DB_KEY));
            thisMarkerDisplay.setObjectType(doc.get(IndexConstants.COL_OBJECT_TYPE));
            thisMarkerDisplay.setSymbol(doc.get(IndexConstants.COL_MARKER_SYMBOL));
            thisMarkerDisplay.setName(doc.get(IndexConstants.COL_MARKER_NAME));
            thisMarkerDisplay.setMarkerType(doc.get(IndexConstants.COL_MARKER_TYPE));
            thisMarkerDisplay.setChromosome(doc.get(IndexConstants.COL_CHROMOSOME));
            thisMarkerDisplay.setMgiId(doc.get(IndexConstants.COL_MGI_ID));
            thisMarkerDisplay.setStrand(doc.get(IndexConstants.COL_STRAND));
            thisMarkerDisplay.setLocDisplay(doc.get(IndexConstants.COL_LOC_DISPLAY));

            newMarkerCache.put(thisMarkerDisplay.getCacheKey(), thisMarkerDisplay);

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


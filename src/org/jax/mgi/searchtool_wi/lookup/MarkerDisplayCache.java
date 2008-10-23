package org.jax.mgi.searchtool_wi.lookup;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.jax.mgi.searchtool_wi.dataAccess.IndexReaderContainer;
import org.jax.mgi.searchtool_wi.exception.CacheNotLoadedException;
import org.jax.mgi.searchtool_wi.results.QS_MarkerResult;
import org.jax.mgi.shr.config.Configuration;

import org.jax.mgi.shr.searchtool.IndexConstants;

//////////////////////////////////////////////////////////////////////////////
/**
* @module MarkerDisplayCache
* @author pf
*/

/**  The MarkerDisplayCache object provides a presistent, in-memory container
*   for search tool related display data.
*
* @is a singleton for holding marker display data
* @has marker display data
* @does
*
*/
public class MarkerDisplayCache
{
    // Set up the logger
    private static Logger log = Logger.getLogger(MarkerDisplayCache.class.getName());
    /////////////////
    //Singleton setup
    /////////////////

    // Create the ONLY instance of this class
    private static final MarkerDisplayCache _theInstance =
        new MarkerDisplayCache();

    // private constructor to avoid outside instantiation
    private MarkerDisplayCache(){}

    /////////////////////
    // class variables
    /////////////////////

    // markerKey -> MarkerDisplay object
    private static HashMap markerCacheMap;

    private static Boolean loadNeeded = true;

    private static IndexReaderContainer irc = null;

    /////////////////////////////
    // Singleton retrieval method
    /////////////////////////////

    //////////////////////////////////////////////////////////////////////////
    public static MarkerDisplayCache getMarkerDisplayCache(Configuration config)
    {

        if (irc == null)
        {
        irc = IndexReaderContainer.getIndexReaderContainer(config);
        }

        load(config);

        return _theInstance;
    }

    public static MarkerDisplayCache getMarkerDisplayCache()
    {
        if (loadNeeded)
        {
            throw new CacheNotLoadedException("You tried use an unintialized MarkerDisplayCache," +
                " it must first be initialized with a config object.");
        }
        else
        {
            return _theInstance;
        }
    }

    /////////////////////////
    // Data Retrieval Methods
    /////////////////////////

    public MarkerDisplay getMarker(QS_MarkerResult markerResult)
    {
        MarkerDisplay thisMarkerDisplay
            = (MarkerDisplay)markerCacheMap.get(markerResult.getDbKey());

        //if we don't have the requested object, returning an empty
        if (thisMarkerDisplay==null) {
            thisMarkerDisplay = new MarkerDisplay();
        }

        return thisMarkerDisplay;
    }

    public MarkerDisplay getMarker(String dbKey)
    {
        MarkerDisplay thisMarkerDisplay
            = (MarkerDisplay)markerCacheMap.get(dbKey);

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
        HashMap newMarkerCache = new HashMap();

        log.info("MarkerDisplayCache loading...");
        try {

          IndexReader ir = irc.getMarkerDisplayReader();

          for (int count=0; count<ir.maxDoc(); count++ ) {

            MarkerDisplay thisMarkerDisplay = new MarkerDisplay();

            // flesh out the display cache object
            doc = ir.document(count);

            thisMarkerDisplay.setDbKey(doc.get(IndexConstants.COL_DB_KEY));
            thisMarkerDisplay.setSymbol(doc.get(IndexConstants.COL_MARKER_SYMBOL));
            thisMarkerDisplay.setName(doc.get(IndexConstants.COL_MARKER_NAME));
            thisMarkerDisplay.setMarkerType(doc.get(IndexConstants.COL_MARKER_TYPE));
            thisMarkerDisplay.setChromosome(doc.get(IndexConstants.COL_CHROMOSOME));
            thisMarkerDisplay.setMgiId(doc.get(IndexConstants.COL_MGI_ID));

            newMarkerCache.put(thisMarkerDisplay.getDbKey(), thisMarkerDisplay);

          }

        }
          catch (Exception e) {
              e.printStackTrace();
        }

        markerCacheMap = newMarkerCache;
        loadNeeded = false;
        log.info("MarkerDisplayCache finished loading...");

        return;
    }

}


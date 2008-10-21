package org.jax.mgi.searchtool_wi.lookup;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;

// home-grown classes
import org.jax.mgi.searchtool_wi.dataAccess.IndexSearcherContainer;
import org.jax.mgi.searchtool_wi.exception.LookupInvalidException;
import org.jax.mgi.searchtool_wi.results.QS_OtherResult;
import org.jax.mgi.searchtool_wi.servlet.Search;
import org.jax.mgi.shr.config.Configuration;
import QS_Commons.IndexConstants;

//////////////////////////////////////////////////////////////////////////////
/**
* @module OtherDisplayLookup
* @author mhall
*/

/**  The OtherDisplayCache object provides a presistent, in-memory container
*   for search tool related display data.
*
* @is a singleton for holding other display data
* @has other display data
* @does
*
*/
public class OtherDisplayLookup
{

    // Set up the logger

    private static Logger log = Logger.getLogger(OtherDisplayLookup.class.getName());

    /////////////////
    //Singleton setup
    /////////////////

    // Create the ONLY instance of this class
    private static final OtherDisplayLookup _theInstance =
        new OtherDisplayLookup();

    // private constructor to avoid outside instantiation
    private OtherDisplayLookup(){}



    /////////////////////
    // class variables
    /////////////////////

    // otherKey -> OtherDisplay object
    private static HashMap otherCacheMap = new HashMap();

    private static Boolean loadNeeded = true;

    private static IndexSearcherContainer isc;

    /////////////////////////////
    // Singleton retrieval method
    /////////////////////////////

    //////////////////////////////////////////////////////////////////////////
    public static OtherDisplayLookup getOtherDisplayLookup(Configuration config)
    {
        init(config);

        loadNeeded = false;

        return _theInstance;
    }

    public static OtherDisplayLookup getOtherDisplayLookup()
    {
        if (loadNeeded)
        {
            throw new LookupInvalidException("You tried use an unintialized Other DisplayCache," +
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

    public OtherDisplay getOther(QS_OtherResult otherResult)
    {
        OtherDisplay thisOtherDisplay = (OtherDisplay)otherCacheMap.get(otherResult.getDbKey()+"::"+otherResult.getDataType());

        //if we don't have the requested object, look it up, and add it to the cache
        // if it is STILL null after this, return a null object.
        if (thisOtherDisplay==null) {
        	thisOtherDisplay = lookupOther(otherResult.getDbKey(), otherResult.getDataType());
        	otherCacheMap.put(otherResult.getDbKey()+"::"+otherResult.getDataType(), thisOtherDisplay);
        }

        return thisOtherDisplay;
    }

    private OtherDisplay lookupOther(String dbKey, String type)
    {

    	Term termKey = new Term(IndexConstants.COL_DB_KEY, dbKey);
    	Term termType = new Term(IndexConstants.COL_DATA_TYPE, type);

    	TermQuery t1 = new TermQuery(termKey);
    	TermQuery t2 = new TermQuery(termType);
    	BooleanQuery bq = new BooleanQuery();

    	bq.add(t1, BooleanClause.Occur.MUST);
    	bq.add(t2, BooleanClause.Occur.MUST);

    	IndexSearcher searcher = isc.getOtherDisplayIndex();

    	OtherDisplay thisOtherDisplay = new OtherDisplay();

    	try{
    	Hits results = searcher.search(bq);
    	Hit hit;

    	if (results.length() != 1)
    	{
    		log.error("There is a problem with the index, this should toss an exception! Length: "+results.length());
            for (Iterator iter = results.iterator(); iter.hasNext();) {

                // get the hit, and build a match
                hit = (Hit) iter.next();
                log.error(hit.getDocument());
    		}
    	}
    	else
    	{
	    	Document doc = results.doc(0);

            for (Iterator iter = results.iterator(); iter.hasNext();) {

                // get the hit, and build a match
                hit = (Hit) iter.next();
    		}

	        thisOtherDisplay.setDbKey(doc.get(IndexConstants.COL_DB_KEY));
	        thisOtherDisplay.setName(doc.get(IndexConstants.COL_OTHER_NAME));
	        thisOtherDisplay.setDataType(doc.get(IndexConstants.COL_DATA_TYPE));
	        thisOtherDisplay.setQualifier1(doc.get(IndexConstants.COL_QUALIFIER1));
	        return thisOtherDisplay;
    	}

    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
		return thisOtherDisplay;
    }

    /////////////////
    //private methods
    /////////////////
    //
  private static void init(Configuration stConfig)
  {


      log.info("OtherDisplayLookup loading...");
      isc = IndexSearcherContainer.getIndexSearcherContainer(stConfig);
      log.info("Other Display Lookup finished loading...");
      return;
  }

}


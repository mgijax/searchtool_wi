package org.jax.mgi.searchtool_wi.lookup;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.jax.mgi.searchtool_wi.dataAccess.IndexAccessor;
import org.jax.mgi.searchtool_wi.dataAccess.IndexReaderContainer;
import org.jax.mgi.searchtool_wi.dataAccess.IndexSearcherContainer;
import org.jax.mgi.shr.config.Configuration;

import org.jax.mgi.shr.searchtool.IndexConstants;

/**
 *
 * @author      mhall
 *
 * @is          Cache to check the existence of given tokens.  This is a 
 *              singleton.
 * @has         None
 * @does        Provides a boolean methods, hasToken returning true if the
 *              token exists in the cache.
 */

public class TokenExistanceCache {

    private static final TokenExistanceCache theInstance = 
        new TokenExistanceCache();
    private static HashSet<String> tokenSet = new HashSet<String>();
    private static Logger log = 
        Logger.getLogger(TokenExistanceCache.class.getName());
    private static IndexAccessor indexAccessor;
    private static IndexSearcherContainer isc;
    private static IndexReaderContainer irc;

    private TokenExistanceCache() {
    }

    /**
     * Get the ONLY instance of the TokenExistanceCache
     *
     * @param config
     * @return TokenExistanceCache
     */

    public static TokenExistanceCache 
        getTokenExistanceCache(Configuration config) {

        // Setup the instance of the object.

        indexAccessor = new IndexAccessor(config);
        isc = IndexSearcherContainer.getIndexSearcherContainer(config);
        irc = IndexReaderContainer.getIndexReaderContainer(config);
        load(config);

        return theInstance;
    }

    /**
     * Accepts as an argument a string to check the existence of.
     * If it cannot find it initially in the cache, we then perform a
     * lazy lookup into the other index, to see if it exists there.
     *
     * @param token
     * @return Boolean
     */

    public Boolean hasToken(String token) throws Exception {
        if (tokenSet.contains(token) ) {
            return true;
        } else {
            return lookupOther(token);
        }
    }

    /**
     * This is used internally to lookup an other (non marker, non vocab)
     * accession Id, only if all else has failed.  Once it has been looked up
     * once, it is stored in cache from that point forward.
     * @param token
     * @return
     * @throws Exception
     */
    
    private Boolean lookupOther(String token) throws Exception {

        // This is the only spot in the IndexAccessor api that breaks
        // the model of only accepting search inputs.  This is done
        // for simplicities sake at this level, since we are already dealing
        // with items at a token level.
        
        Hits hit = indexAccessor.searchOtherExactByWholeTerm(token);

        if (hit != null && hit.length() > 0) {
            tokenSet.add(token);
            return true;
        }
        return false;
    }

    /**
     * Load up the cache with the tokens from the various data sources.
     * This takes a bit to initialize, over 25 seconds added to the servers
     * startup time.
     * @param stConfig
     */
    
    private static void load(Configuration stConfig) {

        log.info("TokenExistanceCache Loading....");

        try {
            IndexReader ir = irc.getNonIDTokenReader();

            TermEnum te = ir.terms();

            while (te.next()) {
                tokenSet.add(te.term().text());
            }

            // Marker Acc Id's

            IndexSearcher is = isc.getMarkerAccIDIndex();

            Term markerKey = new Term(IndexConstants.COL_DATA_TYPE, 
                    IndexConstants.ACCESSION_ID);
            TermQuery mQuery = new TermQuery(markerKey);

            Term orthKey = new Term(IndexConstants.COL_DATA_TYPE, 
                    IndexConstants.ORTH_ACCESSION_ID);
            TermQuery oQuery = new TermQuery(orthKey);

            Term alleleKey = new Term(IndexConstants.COL_DATA_TYPE, 
                    IndexConstants.ALLELE_ACCESSION_ID);
            TermQuery aQuery = new TermQuery(alleleKey);

            Term esCellKey = new Term(IndexConstants.COL_DATA_TYPE, 
                    IndexConstants.ES_ACCESSION_ID);
            TermQuery eQuery = new TermQuery(esCellKey);

            BooleanQuery bq = new BooleanQuery();

            // Join all of the queries together in an or query.  This in effect
            // pulls out all of the data for all of these types.
            
            bq.add(mQuery, BooleanClause.Occur.SHOULD);
            bq.add(oQuery, BooleanClause.Occur.SHOULD);
            bq.add(aQuery, BooleanClause.Occur.SHOULD);
            bq.add(eQuery, BooleanClause.Occur.SHOULD);

            try {
                Hits hits = is.search(bq);

                for (Iterator iter = hits.iterator(); iter.hasNext();) {
                    Hit current = (Hit) iter.next();
                    tokenSet.add(current.get(IndexConstants.COL_DATA));
                }
            }
            catch (Exception e) {
                log.error(e.getStackTrace().toString());
            }

            //Vocab AccId's

            is = isc.getVocabAccIDIndex();

            Term vocabKey = new Term(IndexConstants.COL_DATA_TYPE, 
                    IndexConstants.ACCESSION_ID);

            TermQuery vQuery = new TermQuery(vocabKey);

            try
            {
                Hits hits = is.search(vQuery);
                for (Iterator iter = hits.iterator(); iter.hasNext();)
                {
                    Hit current = (Hit) iter.next();
                    tokenSet.add(current.get(IndexConstants.COL_DATA));

                }
            }
            catch (Exception e)
            {
                log.error(e.getStackTrace().toString());
            }

        } catch (Exception e) {
            log.error(e.getStackTrace().toString());
        }

        log.info("TokenExistanceCache finished loading...");

    }

}

package org.jax.mgi.searchtool_wi.dataAccess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.jax.mgi.searchtool_wi.utils.SearchInput;
import org.jax.mgi.shr.config.Configuration;

import org.jax.mgi.shr.searchtool.IndexConstants;
import org.jax.mgi.shr.searchtool.MGIAnalyzer;
import org.jax.mgi.shr.searchtool.StemmedMGIAnalyzer;

/**
 * This class provides the API between the indexes and the search tool. By
 * using this API the searchtool needn't concern itself as to how its indexes
 * are named, nor does it need any knowledge about how to perform searches
 * against them.
 *
 * @author mhall
 *
 * @has Various Analyzers, an IndexSearcherContainer, and Queries.
 *
 * These are used in the various searches to complete the task of searching
 * a given data source.
 *
 * @does This object encapsulates all the various searches into a single
 * interface.  The interface consistently takes a SearchInput object, with
 * the exception of some very special cases.
 */

public class IndexAccessor {

    private IndexSearcherContainer isc;
    private Logger log = Logger.getLogger(IndexAccessor.class.getName());

    // Set up the various analyzers that we use as class variables.

    private Analyzer standard_analyzer = new StandardAnalyzer();
    private Analyzer mgi_analyzer = new MGIAnalyzer();
    private Analyzer stemmed_mgi_analyzer = new StemmedMGIAnalyzer();
    private PerFieldAnalyzerWrapper perField_analyzer =
        new PerFieldAnalyzerWrapper(standard_analyzer);

    // Set up the various query parsers as class variables.

    private QueryParser qp_pre;
    private QueryParser qp_snow;
    private QueryParser qp_large_token;

    private BooleanQuery bq = new BooleanQuery();

    private Configuration config;

    private Query query = null;

    private Hits hits = null;

    public IndexAccessor(Configuration stConfig) {

        // Set up the per field analyzer wrapper.  This is used for inexact
        // searches.

        perField_analyzer.addAnalyzer("data", mgi_analyzer);
        perField_analyzer.addAnalyzer("sdata", stemmed_mgi_analyzer);

        config = stConfig;

        // Set up the various query parsers.  They are each used to generate
        // specific queries based upon a search string that has been passed to
        // them.

        qp_snow = new QueryParser(IndexConstants.COL_SDATA, stemmed_mgi_analyzer);
        qp_pre = new QueryParser(IndexConstants.COL_DATA, mgi_analyzer);
        qp_large_token = new QueryParser(IndexConstants.COL_DATA, new KeywordAnalyzer());

        // Override some of the default lucene behaviors for query parsers.

        qp_snow.setAllowLeadingWildcard(true);
        qp_pre.setAllowLeadingWildcard(true);
        qp_large_token.setAllowLeadingWildcard(true);
        qp_large_token.setDefaultOperator(QueryParser.Operator.AND);
        BooleanQuery.setMaxClauseCount(100000);

        // Initialize the reference to the IndexSearcherContainer singleton.

        isc = IndexSearcherContainer.getIndexSearcherContainer(config);
    }

    /**
     * Search for marker ID's by Large Tokens.
     * @param si - A searchInput object
     * @return A List of hits
     * @throws Exception
     */

    public List<Hit> searchMarkerAccIDByLargeToken(SearchInput si)
            throws Exception {
        IndexSearcher searcher = isc.getMarkerAccIDIndex();

        List<String> tokens = si.getLargeTokenList();
        ArrayList<Hit> hitsList = new ArrayList<Hit>();

        for (Iterator<String> tokenIter = tokens.iterator(); tokenIter.hasNext();) {
            String token = (String) tokenIter.next();
            Term t = new Term(IndexConstants.COL_DATA, token);

            TermQuery tq = new TermQuery(t);
            hits = searcher.search(tq);

            for (Iterator<Hit> hitIter = hits.iterator(); hitIter.hasNext();) {
                hitsList.add((Hit) hitIter.next());
            }
        }

        return hitsList;
    }

    /**
     * Search for marker ID's by Whole Term
     * @param si
     * @return
     * @throws Exception
     */

    public Hits searchMarkerAccIDByWholeTerm(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerAccIDIndex();
        Term t = new Term(IndexConstants.COL_DATA, si.getWholeTermSearchString());
        TermQuery tq = new TermQuery(t);
        hits = searcher.search(tq);

        return hits;
    }

    /**
     * Search Marker Related Vocabulary Acc ID's by the large tokens contained
     * in the search string.
     * @param si
     * @return
     * @throws Exception
     */

    public List<Hit> searchMarkerVocabAccIDByLargeToken(SearchInput si)
            throws Exception {

        IndexSearcher searcher = isc.getMarkerVocabAccIDIndex();

        List<String> tokens = si.getLargeTokenList();
        ArrayList<Hit> hitsList = new ArrayList<Hit>();

        for (Iterator<String> tokenIter = tokens.iterator(); tokenIter.hasNext();) {
            String token = (String) tokenIter.next();
            Term t = new Term(IndexConstants.COL_DATA, token);
            TermQuery tq = new TermQuery(t);
            hits = searcher.search(tq);

            for (Iterator<Hit> hitIter = hits.iterator(); hitIter.hasNext();) {
                hitsList.add((Hit) hitIter.next());
            }
        }

        return hitsList;
    }

    /**
     * Search Marker Related Vocabulary Accession ID's by the Whole Term.
     * @param si
     * @return
     * @throws Exception
     */

    public Hits searchMarkerVocabAccIDByWholeTerm(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerVocabAccIDIndex();
        Term t = new Term(IndexConstants.COL_DATA, si.getWholeTermSearchString());
        TermQuery tq = new TermQuery(t);

        hits = searcher.search(tq);

        return hits;
    }

    /**
     * Search Marker Symbols By the Large Tokens contained in the search
     * string.
     *
     * Unlike other "Exact" searches, this one allows prefix searching to
     * process.
     *
     * @param si
     * @return A List of Hits
     * @throws Exception
     */

    public List<Hit> searchMarkerSymbolExactByLargeToken(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerSymbolIndex();

        List<String> tokens = si.getEscapedLargeTokenList();

        ArrayList<Hit> hitsList = new ArrayList<Hit>();

        for (Iterator<String> tokenIter = tokens.iterator(); tokenIter.hasNext();) {

            String token = (String) tokenIter.next();
            log.debug("Search Symbol Exact Token: |" + token + "|");

            Query symbol_query = qp_large_token.parse(token);

            hits = searcher.search(symbol_query);

            for (Iterator<Hit> hitIter = hits.iterator(); hitIter.hasNext();) {
                hitsList.add((Hit) hitIter.next());
            }
        }

        return hitsList;
    }

    /**
     * Search marker symbols by whole term.  This also allows the use of prefix
     * searches.
     *
     * @param si
     * @return
     * @throws Exception
     */

    public Hits searchMarkerSymbolExactByWholeTerm(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerSymbolIndex();

        Query whole_symbol_query = qp_large_token.parse(si.getEscapedWholeTermSearchString());

        hits = searcher.search(whole_symbol_query);

        return hits;
    }

    /**
     * Search marker non symbol nomenclature via large tokens contained in
     * the search string.
     *
     * @param si
     * @return
     * @throws Exception
     */

    public List<Hit> searchMarkerExactByLargeToken(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerExactIndex();

        List<String> tokens = si.getLargeTokenList();
        ArrayList<Hit> hitsList = new ArrayList<Hit>();

        for(String token: tokens) {

            Term t = new Term(IndexConstants.COL_DATA, token);

            TermQuery tq = new TermQuery(t);

            hits = searcher.search(tq);
            
            for (Iterator<Hit> hitIter = hits.iterator(); hitIter.hasNext();) {
                hitsList.add((Hit) hitIter.next());
            }
        }

        return hitsList;
    }

    /**
     * Search Marker non symbol nomenclature by Whole Term.
     * @param si
     * @return
     * @throws Exception
     */

    public Hits searchMarkerExactByWholeTerm(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerExactIndex();
        Term t = new Term(IndexConstants.COL_DATA, si.getWholeTermSearchString());
        TermQuery tq = new TermQuery(t);

        hits = searcher.search(tq);

        return hits;
    }

    /**
     * Search vocabulary nomenclature by whole term.
     * @param si
     * @return
     * @throws Exception
     */

    public Hits searchMarkerVocabExactByWholeTerm(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerVocabExactIndex();
        Term t = new Term(IndexConstants.COL_DATA, si.getWholeTermSearchString());
        TermQuery tq = new TermQuery(t);
        hits = searcher.search(tq);
        return hits;
    }

    /**
     * Search the other exact indexes by large token.
     * @param si
     * @return A List of hits.
     * @throws Exception
     */

    public List searchOtherExactByLargeToken(SearchInput si) throws Exception {

        IndexSearcher searcher = isc.getOtherExactIndex();

        List<String> tokens = si.getLargeTokenList();
        ArrayList<Hit> hitsList = new ArrayList<Hit>();

        for(String token: tokens) {

            Term t = new Term(IndexConstants.COL_DATA, token);
            TermQuery tq = new TermQuery(t);
            hits = searcher.search(tq);

            for (Iterator<Hit> hitIter = hits.iterator(); hitIter.hasNext();) {
                hitsList.add((Hit) hitIter.next());
            }

        }

        return hitsList;

    }

    /**
     * Search for Other Objects by Whole term.
     *
     * This is primarily used in the token existence cache.
     *
     * @param si
     * @return
     * @throws Exception
     */

    public Hits searchOtherExactByWholeTerm(String token) throws Exception {

        IndexSearcher searcher = isc.getOtherExactIndex();

        Term t = new Term(IndexConstants.COL_DATA, token);
        TermQuery tq = new TermQuery(t);
        hits = searcher.search(tq);

        return hits;

    }

    /**
     * Search markers via small tokens, with an and search.
     * @param si
     * @return
     * @throws Exception
     */

    public Hits searchMarkerAnd(SearchInput si) throws Exception {
        qp_snow.setDefaultOperator(QueryParser.Operator.AND);
        qp_pre.setDefaultOperator(QueryParser.Operator.AND);
        bq = new BooleanQuery();
        return searchMarker(si.getTransformedLowerCaseString(), si.getTransformedLowerCaseString());
    }

    /**
     * Search Markers via Small Tokens, with an or search.
     * @param si
     * @return
     * @throws Exception
     */

    public Hits searchMarkerOr(SearchInput si) throws Exception {
        qp_snow.setDefaultOperator(QueryParser.Operator.OR);
        qp_pre.setDefaultOperator(QueryParser.Operator.OR);
        bq = new BooleanQuery();
        return searchMarker(si.getTransformedLowerCaseStringOr(), si.getTransformedLowerCaseStringOr());
    }

    /**
     * This method does the work of searching markers by small tokens.
     * Its currently wrapped by either searchMarkerAnd or searchMarkerOr
     * @param si Unstemmed version of the transformed search string
     * @param si_stemmed Stemmed version of the transformed search string
     * @return
     * @throws Exception
     */

    private Hits searchMarker(String si, String si_stemmed) throws Exception {
        IndexSearcher searcher = isc.getMarkerInexactIndex();

        Query query_snow;
        Query query_pre;

        // If we don't have a blank search string, set up the query.

        if (!si.equals("")) {
            query_pre = qp_pre.parse(si);
            bq.add(query_pre, BooleanClause.Occur.SHOULD);

            String[] temp = si.split("\\s");

            Boolean flag = true;
            for (int i = 0; i < temp.length; i++) {
                if (temp[i].endsWith("*")) {
                    flag = false;
                }
            }

            if (flag) {
                query_snow = qp_snow.parse(si_stemmed);
                bq.add(query_snow, BooleanClause.Occur.SHOULD);
            }
        }

        // Search, no matter what.

        hits = searcher.search(bq);
        log.debug("marker search (" + hits.length() + " hits): " + bq.toString());

        return hits;

    }

    /**
     * Search the vocabulary items via small tokens, and as an
     * And search.
     * @param si SearchInput
     * @return
     * @throws Exception
     */

    public Hits searchVocabAnd(SearchInput si) throws Exception {
        qp_snow.setDefaultOperator(QueryParser.Operator.AND);
        qp_pre.setDefaultOperator(QueryParser.Operator.AND);
        bq = new BooleanQuery();
        return searchVocab(si.getTransformedLowerCaseString(), si
                .getTransformedLowerCaseString());
    }

    /**
     * Search the vocabulary items, via small tokens, and as an
     * Or search.
     * @param si SearchInput
     * @return
     * @throws Exception
     */

    public Hits searchVocabOr(SearchInput si) throws Exception {
        qp_snow.setDefaultOperator(QueryParser.Operator.OR);
        qp_pre.setDefaultOperator(QueryParser.Operator.OR);
        bq = new BooleanQuery();
        return searchVocab(si.getTransformedLowerCaseStringOr(), si
                .getTransformedLowerCaseStringOr());
    }

    /**
     * This method is what actually does the searching against the various
     * vocabulary items.  Currently access to this is wrapped in either
     * searchVocabAnd and searchVocabOr
     * @param si Unstemmed version of the transformed search string
     * @param si_stemmed Stemmed version of the transformed search string
     * @return
     * @throws Exception
     */

    private Hits searchVocab(String si, String si_stemmed) throws Exception {

        IndexSearcher searcher = isc.getVocabInexact();

        // Build the compound query.
        Query query_snow;
        Query query_pre;

        // If the query string isn't blank, set up the query.

        if (!si.equals("")) {

            String[] temp = si.split("\\s");

            Boolean flag = true;
            for (int i = 0; i < temp.length; i++) {
                if (temp[i].endsWith("*")) {
                    flag = false;
                }
            }

            if (flag) {
                query_snow = qp_snow.parse(si_stemmed);
                bq.add(query_snow, BooleanClause.Occur.SHOULD);
            }

            query_pre = qp_pre.parse(si);
            bq.add(query_pre, BooleanClause.Occur.SHOULD);
        }

        // Search no matter what.

        hits = searcher.search(bq);
        log.debug("vocab search (" + hits.length() + " hits): " + bq.toString());

        return hits;
    }

    /**
     * Search vocabulary by Whole Term
     * @param si SearchInput
     * @return Hits
     * @throws Exception
     */

    public Hits searchVocabExactByWholeTerm(SearchInput si) throws Exception {

        IndexSearcher searcher = isc.getVocabExactIndex();
        Term t = new Term(IndexConstants.COL_DATA, si
                .getWholeTermSearchString());
        TermQuery tq = new TermQuery(t);

        hits = searcher.search(tq);
        return hits;
    }

    /**
     * Search vocabulary accession id's by large tokens contained in the search
     * string.
     *
     * @param si The searchinput object.
     * @return A List of Hit objects
     * @throws Exception
     */

    public List<Hit> searchVocabAccIDByLargeToken(SearchInput si)
            throws Exception {
        IndexSearcher searcher = isc.getVocabAccIDIndex();

        List<String> tokens = si.getLargeTokenList();
        ArrayList<Hit> hitsList = new ArrayList<Hit>();

        for (Iterator<String> tokenIter = tokens.iterator(); tokenIter
                .hasNext();) {
            String token = (String) tokenIter.next();
            Term t = new Term(IndexConstants.COL_DATA, token);
            TermQuery tq = new TermQuery(t);
            hits = searcher.search(tq);

            for (Iterator<Hit> hitIter = hits.iterator(); hitIter.hasNext();) {
                hitsList.add((Hit) hitIter.next());
            }
        }

        return hitsList;
    }

    /**
     * Search vocabulary accession id's by whole search string.
     * @param si The searchinput object.
     * @return A hits object.
     * @throws Exception
     */

    public Hits searchVocabAccIDByWholeTerm(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getVocabAccIDIndex();
        Term t = new Term(IndexConstants.COL_DATA, si
                .getWholeTermSearchString());
        TermQuery tq = new TermQuery(t);

        hits = searcher.search(tq);

        return hits;
    }
}

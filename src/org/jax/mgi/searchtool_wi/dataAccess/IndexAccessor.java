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
 * This class provides the API between the indexes and the search tool. By using
 * this API the searchtool needn't concern itself as to how its indexes are
 * named, nor does it need any knowledge about how to perform searches against
 * them.
 *
 * @author mhall
 *
 *
 *
 */

public class IndexAccessor {

    private IndexSearcherContainer isc;
    private Logger log = Logger.getLogger(IndexAccessor.class.getName());
    private Analyzer standard_analyzer = new StandardAnalyzer();
    private Analyzer mgi_analyzer = new MGIAnalyzer();
    private Analyzer stemmed_mgi_analyzer = new StemmedMGIAnalyzer();
    private PerFieldAnalyzerWrapper perField_analyzer = new PerFieldAnalyzerWrapper(
            standard_analyzer);

    private QueryParser qp_pre;
    private QueryParser qp_snow;

    private QueryParser qp_big_token;
    private BooleanQuery bq = new BooleanQuery();

    private Configuration config;

    private Query query = null;

    private Hits hits = null;

    public IndexAccessor(Configuration stConfig) {
        perField_analyzer.addAnalyzer("data", mgi_analyzer);
        perField_analyzer.addAnalyzer("sdata", stemmed_mgi_analyzer);
        config = stConfig;
        qp_snow = new QueryParser(IndexConstants.COL_SDATA,
                stemmed_mgi_analyzer);
        qp_pre = new QueryParser(IndexConstants.COL_DATA, mgi_analyzer);
        qp_big_token = new QueryParser(IndexConstants.COL_DATA,
                new KeywordAnalyzer());

        qp_snow.setAllowLeadingWildcard(true);
        qp_pre.setAllowLeadingWildcard(true);
        qp_big_token.setAllowLeadingWildcard(true);
        BooleanQuery.setMaxClauseCount(100000);
        isc = IndexSearcherContainer.getIndexSearcherContainer(config);
    }

    public List<Hit> searchMarkerAccID(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerAccIDIndex();

        List<String> tokens = si.getSpaceSepTrailingPunctRemovedList();
        ArrayList<Hit> hitsList = new ArrayList<Hit>();

        for (Iterator<String> tokenIter = tokens.iterator(); tokenIter
                .hasNext();) {
            String token = (String) tokenIter.next();
            //Term t = new Term(IndexConstants.COL_DATA, token.toLowerCase().replaceAll("\"", ""));
            Term t = new Term(IndexConstants.COL_DATA, token);

            log.info("The AccID Query: " + t);

            TermQuery tq = new TermQuery(t);
            hits = searcher.search(tq);

            for (Iterator<Hit> hitIter = hits.iterator(); hitIter.hasNext();) {
                hitsList.add((Hit) hitIter.next());
            }
        }

        return hitsList;
    }

    public List<Hit> searchMarkerVocabAccID(SearchInput si) throws Exception {

        IndexSearcher searcher = isc.getMarkerVocabAccIDIndex();

        List<String> tokens = si.getSpaceSepTrailingPunctRemovedList();
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

    public List<Hit> searchMarkerSymbolExact(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerSymbolIndex();

        List<String> tokens = si.getSpaceSepEscapedTrailingPunctRemovedList();

        log.info("Token List Size: " + tokens.size());

        ArrayList<Hit> hitsList = new ArrayList<Hit>();

        for (Iterator<String> tokenIter = tokens.iterator(); tokenIter
                .hasNext();) {

            String token = (String) tokenIter.next();
            System.out.println("Symbol Token: |" + token + "|");

            // This is the old, incorrect way for doing this.

            //Term t = new Term(IndexConstants.COL_DATA, token.toLowerCase().replaceAll("\"", ""));
            //Term t = new Term(IndexConstants.COL_DATA, token);

            //TermQuery tq = new TermQuery(t);

            Query symbol_query = qp_big_token.parse(token);

            System.out.println("The Symbol Query: " + symbol_query);

            hits = searcher.search(symbol_query);

            System.out.println("The Symbol Hits: " + hits.length());

            for (Iterator<Hit> hitIter = hits.iterator(); hitIter.hasNext();) {
                hitsList.add((Hit) hitIter.next());
            }
        }

        return hitsList;
    }


    public List<Hit> searchMarkerExactByBigToken(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerExactIndex();

        List<String> tokens = si.getSpaceSepEscapedTrailingPunctRemovedList();
        //List<String> tokens = si.getSpaceSepTrailingPunctRemovedList();
        ArrayList<Hit> hitsList = new ArrayList<Hit>();

        for (Iterator<String> tokenIter = tokens.iterator(); tokenIter
                .hasNext();) {

            String token = (String) tokenIter.next();
            log.info("Marker Exact Token: " + token);

            Term t = new Term(IndexConstants.COL_DATA, token);

            TermQuery tq = new TermQuery(t);

            log.info("The Marker Exact Query: " + tq);

            hits = searcher.search(tq);

            log.info("BIGTOKENHITS: " + hits.length());

            for (Iterator<Hit> hitIter = hits.iterator(); hitIter.hasNext();) {
                hitsList.add((Hit) hitIter.next());
            }
        }

        return hitsList;
    }

    public Hits searchMarkerExact(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getMarkerExactIndex();
        Term t = new Term(IndexConstants.COL_DATA, si
                .getStrippedLowerCaseSearchString());
        TermQuery tq = new TermQuery(t);

        hits = searcher.search(tq);

        return hits;
    }

    public Hits searchMarkerVocabExact(SearchInput si) throws Exception {

        IndexSearcher searcher = isc.getMarkerVocabExactIndex();
        Term t = new Term(IndexConstants.COL_DATA, si
                .getStrippedLowerCaseSearchString());
        TermQuery tq = new TermQuery(t);
        hits = searcher.search(tq);

        return hits;
    }

    public List searchOtherExact(SearchInput si) throws Exception {

        IndexSearcher searcher = isc.getOtherExactIndex();

        List<String> tokens = si.getSpaceSepTrailingPunctRemovedList();
        ArrayList<Hit> hitsList = new ArrayList<Hit>();

        for (Iterator<String> tokenIter = tokens.iterator(); tokenIter
                .hasNext();) {

            String token = tokenIter.next();

            //log.debug("Token: " +token);

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
     * This is used in the token existance cache.
     * @param si
     * @return
     * @throws Exception
     */

    public Hits searchOtherExact(String token) throws Exception {

        IndexSearcher searcher = isc.getOtherExactIndex();
        // log.debug("Token: " +token);

        Term t = new Term(IndexConstants.COL_DATA, token);
        TermQuery tq = new TermQuery(t);
        hits = searcher.search(tq);

        return hits;

    }

    public Hits searchMarkerAnd(SearchInput si) throws Exception {
        qp_snow.setDefaultOperator(QueryParser.Operator.AND);
        qp_pre.setDefaultOperator(QueryParser.Operator.AND);
        bq = new BooleanQuery();
        return searchMarker(si.getTransformedLowerCaseString(), si
                .getTransformedLowerCaseString());
    }

    public Hits searchMarkerOr(SearchInput si) throws Exception {
        qp_snow.setDefaultOperator(QueryParser.Operator.OR);
        qp_pre.setDefaultOperator(QueryParser.Operator.OR);
        bq = new BooleanQuery();
        return searchMarker(si.getTransformedLowerCaseString(), si
                .getTransformedLowerCaseString());
    }

    public Hits searchMarker(String si, String si_stemmed) throws Exception {
        IndexSearcher searcher = isc.getMarkerInexactIndex();

        Query query_snow;
        Query query_pre;

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

        log.debug("Query:" + bq);

        hits = searcher.search(bq);

        return hits;

    }

    public Hits searchVocabAnd(SearchInput si) throws Exception {
        qp_snow.setDefaultOperator(QueryParser.Operator.AND);
        qp_pre.setDefaultOperator(QueryParser.Operator.AND);
        return searchVocab(si.getTransformedLowerCaseString(), si
                .getTransformedLowerCaseString());
    }

    public Hits searchVocabOr(SearchInput si) throws Exception {
        qp_snow.setDefaultOperator(QueryParser.Operator.OR);
        qp_pre.setDefaultOperator(QueryParser.Operator.OR);
        return searchVocab(si.getTransformedLowerCaseString(), si
                .getTransformedLowerCaseString());
    }

    public Hits searchVocab(String si, String si_stemmed) throws Exception {

        IndexSearcher searcher = isc.getVocabInexact();

        // Build the compound query.
        Query query_snow;
        Query query_pre;

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

        hits = searcher.search(bq);

        return hits;

    }

    public Hits searchVocabExact(SearchInput si) throws Exception {

        IndexSearcher searcher = isc.getVocabExactIndex();
        Term t = new Term(IndexConstants.COL_DATA, si
                .getStrippedLowerCaseSearchString());
        TermQuery tq = new TermQuery(t);

        hits = searcher.search(tq);
        return hits;
    }

    public List<Hit> searchVocabAccID(SearchInput si) throws Exception {
        IndexSearcher searcher = isc.getVocabAccIDIndex();

        List<String> tokens = si.getSpaceSepTrailingPunctRemovedList();
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
}

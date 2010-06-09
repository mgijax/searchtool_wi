package org.jax.mgi.searchtool_wi.dataAccess;

import org.apache.log4j.Logger;
import org.apache.lucene.search.IndexSearcher;
import org.jax.mgi.shr.config.Configuration;
import org.jax.mgi.shr.searchtool.MGISimilarity;

/**
 * @is A singleton - holds IndexSearchers
 * @has Various IndexSearchers
 * @does Creates/Returns the only instances of IndexSearchers
 */

public class IndexSearcherContainer {

    // Single instance of IndexSearcherContainer
    private static IndexSearcherContainer searcherInstance =
        new IndexSearcherContainer();

    // Searchers
    private IndexSearcher otherExactIndex = null;
    private IndexSearcher otherDisplayIndex = null;
    private IndexSearcher vocabInexactIndex = null;
    private IndexSearcher markerExactIndex = null;
    private IndexSearcher markerAccIDIndex = null;
    private IndexSearcher markerSymbolIndex = null;
    private IndexSearcher markerVocabAccIDIndex = null;
    private IndexSearcher markerVocabExactIndex = null;
    private IndexSearcher vocabExact = null;
    private IndexSearcher vocabAccID = null;
    private IndexSearcher markerInexactIndex = null;

    private static MGISimilarity mgis = new MGISimilarity();

    // logging
    private static Logger logger =
        Logger.getLogger(IndexSearcherContainer.class.getName());

    /**
     * Private default constructor, enforcing singleton pattern
     */
    private IndexSearcherContainer() {}

    /**
     * Singleton retrieval method, that returns THE IndexReaderContainer &
     *   builds instance if needed (first request)
     * @param stConfig
     * @return IndexReaderContainer
     */
    public static IndexSearcherContainer getIndexSearcherContainer(
            Configuration stConfig) {

        // initialize instance, if needed
        if (searcherInstance.markerExactIndex == null) {

            String baseDir = stConfig.get("INDEX_DIR");

            // GenomeFeature Searches
            searcherInstance.setGenomeFeatureExactIndex(baseDir + "genomeFeatureExact/index");
            searcherInstance.setGenomeFeatureAccIDIndex(baseDir + "genomeFeatureAccID/index");
            searcherInstance.setGenomeFeatureSymbolIndex(baseDir + "genomeFeatureSymbol/index");
            searcherInstance.setGenomeFeatureInexactIndex(baseDir + "genomeFeatureInexact/index");
            searcherInstance.setGenomeFeatureVocabAccIDIndex(baseDir
                    + "genomeFeatureVocabAccID/index");
            searcherInstance.setGenomeFeatureVocabExactIndex(baseDir
                    + "genomeFeatureVocabExact/index");

            // Vocab Searches
            searcherInstance.setVocabInexact(baseDir + "vocabInexact/index");
            searcherInstance.setVocabExactIndex(baseDir + "vocabExact/index");
            searcherInstance.setVocabAccIDIndex(baseDir + "vocabAccID/index");

            // 'Other' ID Searches
            searcherInstance.setOtherExactIndex(baseDir + "otherExact/index");
            searcherInstance.setOtherDisplayIndex(baseDir + "otherDisplay/index");


        }
        return searcherInstance;
    }


    /**************************
    * Genome Feature Searches *
    **************************/

    /**
     * Get the Marker Inexact Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getMarkerInexactIndex() {
        return markerInexactIndex;
    }

    /**
     * Set the Marker Inexact Index
     * @param - path to the index
     */
    private void setGenomeFeatureInexactIndex(String Index) {
        this.markerInexactIndex = setIndex(Index);
    }

    /**
     * Get the Marker Exact Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getMarkerExactIndex() {
        return markerExactIndex;
    }

    /**
     * Set the Marker Exact Index
     * @param - path to the index
     */
    private void setGenomeFeatureExactIndex(String Index) {
        this.markerExactIndex = setIndex(Index);
    }

    /**
     * Get the Marker Acc ID Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getMarkerAccIDIndex() {
        return markerAccIDIndex;
    }

    /**
     * Set the Marker Acc ID Index
     * @param - path to the index
     */
    private void setGenomeFeatureAccIDIndex(String Index) {
        this.markerAccIDIndex = setIndex(Index);
    }

    /**
     * Get the Marker Symbol Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getMarkerSymbolIndex() {
        return markerSymbolIndex;
    }

    /**
     * Set the Marker Symbol Index
     * @param - path to the index
     */
    private void setGenomeFeatureSymbolIndex(String Index) {
        this.markerSymbolIndex = setIndex(Index);
    }

    /**
     * Get the Marker Vocab Acc ID Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getMarkerVocabAccIDIndex() {
        return markerVocabAccIDIndex;
    }

    /**
     * Set the Marker Vocab Acc ID Index
     * @param - path to the index
     */
    private void setGenomeFeatureVocabAccIDIndex(String vocabIndex) {
        this.markerVocabAccIDIndex = setIndex(vocabIndex);
    }

    /**
     * Get the Marker Vocab Exact Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getMarkerVocabExactIndex() {
        return markerVocabExactIndex;
    }

    /**
     * Set the Marker Vocab Exact Index
     * @param - path to the index
     */
    private void setGenomeFeatureVocabExactIndex(String vocabIndex) {
        this.markerVocabExactIndex = setIndex(vocabIndex);
    }


    /*****************
    * Vocab Searches *
    *****************/

    /**
     * Get the Vocab Inexact Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getVocabInexact() {
        return vocabInexactIndex;
    }

    /**
     * Set the Vocab Inexact Index
     * @param - path to the index
     */
    private void setVocabInexact(String vocabIndex) {
        this.vocabInexactIndex = setIndex(vocabIndex);
    }

    /**
     * Get the Vocab Exact Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getVocabExactIndex() {
        return vocabExact;
    }

    /**
     * Set the Vocab Exact Index
     * @param - path to the index
     */
    private void setVocabExactIndex(String vocabIndex) {
        this.vocabExact = setIndex(vocabIndex);
    }

    /**
     * Get the Vocab Acc ID Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getVocabAccIDIndex() {
        return vocabAccID;
    }

    /**
     * Set the Vocab Acc ID Index
     * @param - path to the index
     */
    private void setVocabAccIDIndex(String vocabIndex) {
        this.vocabAccID = setIndex(vocabIndex);
    }


    /**********************
    * 'Other' ID Searches *
    **********************/

    /**
     * Get the Other Exact Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getOtherExactIndex() {
        return otherExactIndex;
    }

    /**
     * Set the Other Exact Index
     * @param - path to the index
     */
    private void setOtherExactIndex(String Index) {
        this.otherExactIndex = setIndex(Index);
    }

    /**
     * Get the Other Display Index Searcher.
     * @return IndexSearcher
     */
    public IndexSearcher getOtherDisplayIndex() {
        return otherDisplayIndex;
    }

    /**
     * Set the Other Display Index
     * @param - path to the index
     */
    private void setOtherDisplayIndex(String Index) {
        this.otherDisplayIndex = setIndex(Index);
    }


    /***************************
    * Private Internal Methods *
    ***************************/

    /**
     * The generic index fetcher that all of the set methods call.
     * @param index
     * @return
     */
    private IndexSearcher setIndex(String index) {
        IndexSearcher is = null;
        try {
            is = new IndexSearcher(index);
            is.setSimilarity(mgis);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        return is;
    }



}

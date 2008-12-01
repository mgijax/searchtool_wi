package org.jax.mgi.searchtool_wi.dataAccess;

import org.apache.log4j.Logger;
import org.apache.lucene.search.IndexSearcher;
import org.jax.mgi.shr.config.Configuration;

import org.jax.mgi.shr.searchtool.MGISimilarity;

/**
 * @author mhall
 * 
 * @is A Singleton container object, that handles supplying the various
 * IndexSearchers to objects requesting them.
 * 
 * @has References to several Lucene IndexSearchers
 * 
 * @does Returns references to various IndexSearchers. Initializing them all
 * upon first invocation.
 */

public class IndexSearcherContainer {

    private static Logger log = 
        Logger.getLogger(IndexSearcherContainer.class.getName());
    private static IndexSearcherContainer instance = 
        new IndexSearcherContainer();
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
    private static String baseDir = null;
    private static MGISimilarity mgis = new MGISimilarity();

    /**
     * Private no argument constructor, this object is a singleton and 
     * should never be directly constructed.
     */
    
    private IndexSearcherContainer() {
    }

    /**
     * The singleton get method.  This returns the single instance of this 
     * class.  It will also serve to initialize the class if this is its first
     * invocation.
     * 
     * @param stConfig
     * @return The one and only instance of this class.
     */
    
    public static IndexSearcherContainer getIndexSearcherContainer(
            Configuration stConfig) {

        baseDir = stConfig.get("INDEX_DIR");
        
        // If the marker exact index searcher is currently null, that means
        // that we are in an uninitialized state.  So we will therefore 
        // initialize the object now.
        
        if (instance.getMarkerExactIndex() == null) {
            instance.setOtherExactIndex(baseDir + "otherExact/index");
            instance.setOtherDisplayIndex(baseDir + "otherDisplay/index");
            instance.setVocabInexact(baseDir + "vocabInexact/index");
            instance.setVocabExactIndex(baseDir + "vocabExact/index");
            instance.setVocabAccIDIndex(baseDir + "vocabAccID/index");
            instance.setMarkerExactIndex(baseDir + "markerExact/index");
            instance.setMarkerAccIDIndex(baseDir + "markerAccID/index");
            instance.setMarkerSymbolIndex(baseDir + "markerSymbol/index");
            instance.setMarkerInexactIndex(baseDir + "markerInexact/index");
            instance.setMarkerVocabAccIDIndex(baseDir
                    + "markerVocabAccID/index");
            instance.setMarkerVocabExactIndex(baseDir
                    + "markerVocabExact/index");
        }
        return instance;
    }

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
            log.error(e.getStackTrace());
        }
        return is;
    }

    /**
     * Get the Other Exact Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getOtherExactIndex() {
        return otherExactIndex;
    }

    /**
     * Set the Other Exact Index
     * @param Index The path to the index
     */
    
    private void setOtherExactIndex(String Index) {
        this.otherExactIndex = setIndex(Index);
    }

    /**
     * Get the Other Display Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getOtherDisplayIndex() {
        return otherDisplayIndex;
    }

    /**
     * Set the Other Display Index
     * @param Index The path to the index
     */
    
    private void setOtherDisplayIndex(String Index) {
        this.otherDisplayIndex = setIndex(Index);
    }

    /**
     * Get the Marker Inexact Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getMarkerInexactIndex() {
        return markerInexactIndex;
    }

    /**
     * Set the Marker Inexact Index
     * @param Index The path to the index
     */
    
    private void setMarkerInexactIndex(String Index) {
        this.markerInexactIndex = setIndex(Index);
    }

    /**
     * Get the Marker Exact Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getMarkerExactIndex() {
        return markerExactIndex;
    }

    /**
     * Set the Marker Exact Index
     * @param Index The path to the index
     */
    
    private void setMarkerExactIndex(String Index) {
        this.markerExactIndex = setIndex(Index);
    }

    /**
     * Get the Marker Acc ID Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getMarkerAccIDIndex() {
        return markerAccIDIndex;
    }

    /**
     * Set the Marker Acc ID Index
     * @param Index The path to the index
     */
    
    private void setMarkerAccIDIndex(String Index) {
        this.markerAccIDIndex = setIndex(Index);
    }

    /**
     * Get the Marker Symbol Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getMarkerSymbolIndex() {
        return markerSymbolIndex;
    }

    /**
     * Set the Marker Symbol Index
     * @param Index The path to the index
     */
    
    private void setMarkerSymbolIndex(String Index) {
        this.markerSymbolIndex = setIndex(Index);
    }

    /**
     * Get the Vocab Inexact Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getVocabInexact() {
        return vocabInexactIndex;
    }

    /**
     * Set the Vocab Inexact Index
     * @param Index The path to the index
     */

    private void setVocabInexact(String vocabIndex) {
        this.vocabInexactIndex = setIndex(vocabIndex);
    }

    /**
     * Get the Vocab Exact Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getVocabExactIndex() {
        return vocabExact;
    }

    /**
     * Set the Vocab Exact Index
     * @param Index The path to the index
     */
    
    private void setVocabExactIndex(String vocabIndex) {
        this.vocabExact = setIndex(vocabIndex);
    }

    /**
     * Get the Vocab Acc ID Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getVocabAccIDIndex() {
        return vocabAccID;
    }

    /**
     * Set the Vocab Acc ID Index
     * @param Index The path to the index
     */
    
    private void setVocabAccIDIndex(String vocabIndex) {
        this.vocabAccID = setIndex(vocabIndex);
    }

    /**
     * Get the Marker Vocab Acc ID Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getMarkerVocabAccIDIndex() {
        return markerVocabAccIDIndex;
    }

    /**
     * Set the Marker Vocab Acc ID Index
     * @param Index The path to the index
     */
    
    private void setMarkerVocabAccIDIndex(String vocabIndex) {
        this.markerVocabAccIDIndex = setIndex(vocabIndex);
    }

    /**
     * Get the Marker Vocab Exact Index Searcher.
     * @return An Index Searcher
     */
    
    public IndexSearcher getMarkerVocabExactIndex() {
        return markerVocabExactIndex;
    }

    /**
     * Set the Marker Vocab Exact Index
     * @param Index The path to the index
     */
    
    private void setMarkerVocabExactIndex(String vocabIndex) {
        this.markerVocabExactIndex = setIndex(vocabIndex);
    }
}

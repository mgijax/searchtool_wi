package org.jax.mgi.searchtool_wi.dataAccess;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.jax.mgi.shr.config.Configuration;

/**
 * 
 * @author mhall
 * 
 * @is A singleton container, which supplies an IndexReaders to objects
 * requesting them.
 * 
 * @has Various IndexReaders
 * 
 * @does Upon initialization creates the only copies of the various index
 * readers, and passees them out upon request.
 */

public class IndexReaderContainer {

    private static IndexReaderContainer instance = new IndexReaderContainer();
    private IndexReader markerExactReader = null;
    private IndexReader markerInexactReader = null;
    private IndexReader markerDisplayReader = null;
    private IndexReader vocabExactReader = null;
    private IndexReader vocabDisplayReader = null;
    private IndexReader markerVocabDagReader = null;
    private IndexReader nonIDTokenReader = null;

    private static Logger log = 
        Logger.getLogger(IndexReaderContainer.class.getName());

    private static String baseDir = null;

    /**
     * Private default constructor, this object is a singleton, so you never 
     * construct it!
     */
    
    private IndexReaderContainer() {
    }

    /**
     * Singleton calling method, that returns the instance of the IRC
     * @param stConfig
     * @return The one and only instance of the IRC.
     */
    
    public static IndexReaderContainer getIndexReaderContainer(
            Configuration stConfig) {
        
        baseDir = stConfig.get("INDEX_DIR");

        // If the Marker Exact Reader is null, we are uninitialized.  So
        // go ahead and initialize all of the readers.
        
        if (instance.markerExactReader == null) {
            instance.setMarkerExactReader(baseDir + "markerExact/index");
            instance.setMarkerInexactReader(baseDir + "markerInexact/index");
            instance.setMarkerDisplayReader(baseDir + "markerDisplay/index");
            instance.setMarkerVocabDagReader(baseDir + "markerVocabDag/index");
            instance.setVocabExactReader(baseDir + "vocabExact/index");
            instance.setVocabDisplayReader(baseDir + "vocabDisplay/index");
            instance.setNonIDTokenReader(baseDir + "nonIDToken/index");
        }

        return instance;
    }

    /**
     * Get the Marker Exact IndexReader
     * @return An IndexReader
     */
    
    public IndexReader getMarkerExactReader() {
        return markerExactReader;
    }

    /**
     * Set the Marker Exact IndexReader
     * @param path - The path to the index.
     */
    
    private void setMarkerExactReader(String path) {
        markerExactReader = setReader(path);
    }

    /**
     * Get the Marker Inexact IndexReader
     * @return An IndexReader
     */
    
    public IndexReader getMarkerInexactReader() {
        return markerInexactReader;
    }

    /**
     * Set the Marker Inexact IndexReader
     * @param path - The path to the index.
     */
    
    private void setMarkerInexactReader(String path) {
        markerInexactReader = setReader(path);
    }

    /**
     * Get the Vocab Exact IndexReader
     * @return An IndexReader
     */
    
    public IndexReader getVocabExactReader() {
        return vocabExactReader;
    }

    /**
     * Set the Vocab Exact IndexReader
     * @param path - The path to the index.
     */
    
    private void setVocabExactReader(String path) {
        vocabExactReader = setReader(path);
    }

    /**
     * Get the Vocab Dag IndexReader
     * @return An IndexReader
     */
    
    public IndexReader getVocabDisplayReader() {
        return vocabDisplayReader;
    }

    /**
     * Set the Vocab Display IndexReader
     * @param path - The path to the index.
     */
    
    private void setVocabDisplayReader(String path) {
        vocabDisplayReader = setReader(path);
    }

    /**
     * Get the Marker Display IndexReader
     * @return An IndexReader
     */
    
    public IndexReader getMarkerDisplayReader() {
        return markerDisplayReader;
    }

    /**
     * Set the Marker Display IndexReader
     * @param path - The path to the index.
     */
    
    private void setMarkerDisplayReader(String path) {
        markerDisplayReader = setReader(path);
    }

    /**
     * Get the Marker Vocab Dag IndexReader
     * @return An IndexReader
     */
    
    public IndexReader getMarkerVocabDagReader() {
        return markerVocabDagReader;
    }

    /**
     * Set the Marker Vocab Dag IndexReader
     * @param path - The path to the index.
     */
    
    private void setMarkerVocabDagReader(String path) {
        markerVocabDagReader = setReader(path);
    }

    /**
     * Get the Non ID Token IndexReader
     * @return An IndexReader
     */
    
    public IndexReader getNonIDTokenReader() {
        return nonIDTokenReader;
    }

    /**
     * Set the Non ID Token IndexReader
     * @param path - The path to the index.
     */
    
    private void setNonIDTokenReader(String path) {
        nonIDTokenReader = setReader(path);
    }

    /**
     * Generic setReader that all the more specific set methods use.
     * @return An IndexReader
     */
    
    private IndexReader setReader(String path) {
        IndexReader ir = null;
        try {
            ir = IndexReader.open(path);
        } catch (Exception e) {
            log.error(e.getStackTrace().toString());
        }
        return ir;
    }
}

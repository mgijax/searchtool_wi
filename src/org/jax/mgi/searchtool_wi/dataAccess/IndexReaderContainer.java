package org.jax.mgi.searchtool_wi.dataAccess;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.jax.mgi.shr.config.Configuration;

/**
 * @is A singleton - holds IndexReaders
 * @has Various IndexReaders
 * @does Creates/Returns the only instances of IndexReaders
 */

public class IndexReaderContainer {

    // Single instance of IndexReaderContainer
    private static IndexReaderContainer readerInstance = new IndexReaderContainer();

    // Readers
    private IndexReader genomeFeatureDisplayReader = null;
    private IndexReader vocabDisplayReader = null;
    private IndexReader genomeFeatureVocabReader = null;
    private IndexReader nonIDTokenReader = null;

    private static Logger logger =
        Logger.getLogger(IndexReaderContainer.class.getName());

    /**
     * Private default constructor, enforcing singleton pattern
     */
    private IndexReaderContainer() {}

    /**
     * Singleton retrieval method, that returns THE IndexReaderContainer &
     *   builds instance if needed (first request)
     * @param stConfig
     * @return IndexReaderContainer
     */
    public static IndexReaderContainer getIndexReaderContainer(
            Configuration stConfig) {

        // initialize instance, if needed
        if (readerInstance.genomeFeatureDisplayReader == null) {

            String baseDir = stConfig.get("INDEX_DIR");
            
            readerInstance.setGenomeFeatureDisplayReader(baseDir + "genomeFeatureDisplay/index");
            readerInstance.setVocabDisplayReader(baseDir + "vocabDisplay/index");
            readerInstance.setGenomeFeaturerVocabReader(baseDir + "genomeFeatureVocabDag/index");
            readerInstance.setNonIDTokenReader(baseDir + "nonIDToken/index");
        }
        return readerInstance;
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
     * Get the Genome Feature Display IndexReader
     * @return An IndexReader
     */
    public IndexReader getGenomeFeatureDisplayReader() {
        return genomeFeatureDisplayReader;
    }

    /**
     * Set the Genome Feature Display IndexReader
     * @param path - The path to the index.
     */
    private void setGenomeFeatureDisplayReader(String path) {
        genomeFeatureDisplayReader = setReader(path);
    }

    /**
     * Get the Genome Feature Vocab Dag IndexReader
     * @return An IndexReader
     */
    public IndexReader getGenomeFeatureVocabReader() {
        return genomeFeatureVocabReader;
    }

    /**
     * Set the Genome Feature Vocab Dag IndexReader
     * @param path - The path to the index.
     */
    private void setGenomeFeaturerVocabReader(String path) {
        genomeFeatureVocabReader = setReader(path);
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
        	logger.error("Can't find index in the following location: " + path);
            logger.error(e.getStackTrace().toString());
        }
        return ir;
    }
}

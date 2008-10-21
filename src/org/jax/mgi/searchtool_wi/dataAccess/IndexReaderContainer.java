package org.jax.mgi.searchtool_wi.dataAccess;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.jax.mgi.shr.config.Configuration;

/**
 *
 * @author mhall
 * @is A singleton container, which supplies an IndexReaders to objects
 *     requesting them.
 * @has Various IndexReaders
 * @does Upon initialization creates the only copies of the various index
 *       readers, and passees them out upon request.
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

    private static Logger log = Logger.getLogger(IndexReaderContainer.class.getName());

    private static String baseDir = null;

    private IndexReaderContainer() {
    }

    public static IndexReaderContainer getIndexReaderContainer(Configuration stConfig) {
        baseDir = stConfig.get("INDEX_DIR");

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

    public IndexReader getMarkerExactReader() {
        return markerExactReader;
    }

    private void setMarkerExactReader(String path) {
        markerExactReader = getReader(path);
    }

    public IndexReader getMarkerInexactReader() {
        return markerInexactReader;
    }

    private void setMarkerInexactReader(String path) {
        markerInexactReader = getReader(path);
    }

    public IndexReader getVocabExactReader() {
        return vocabExactReader;
    }

    private void setVocabExactReader(String path) {
        vocabExactReader = getReader(path);
    }

    public IndexReader getVocabDisplayReader() {
        return vocabDisplayReader;
    }

    private void setVocabDisplayReader(String path) {
        vocabDisplayReader = getReader(path);
    }

    public IndexReader getMarkerDisplayReader() {
        return markerDisplayReader;
    }

    private void setMarkerDisplayReader(String path) {
        markerDisplayReader = getReader(path);
    }

    public IndexReader getMarkerVocabDagReader() {
        return markerVocabDagReader;
    }

    private void setMarkerVocabDagReader(String path) {
        markerVocabDagReader = getReader(path);
    }

    public IndexReader getNonIDTokenReader() {
        return nonIDTokenReader;
    }

    private void setNonIDTokenReader(String path) {
        nonIDTokenReader = getReader(path);
    }

    private IndexReader getReader(String path) {
        IndexReader ir = null;
        try {
            ir = IndexReader.open(path);
        } catch (Exception e) {
            log.error(e.getStackTrace().toString());
        }
        return ir;
    }
}

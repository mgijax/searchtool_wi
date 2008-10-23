package org.jax.mgi.searchtool_wi.dataAccess;

import org.apache.log4j.Logger;
import org.apache.lucene.search.IndexSearcher;
import org.jax.mgi.shr.config.Configuration;

import org.jax.mgi.shr.searchtool.MGISimilarity;

/**
 *
 * @author mhall
 * @is A Singleton container object, that handles supplying the various
 *     IndexSearchers to objects requesting them.
 * @has References to several Lucene IndexSearchers
 * @does Returns references to various IndexSearchers. Initializing them all
 *       upon first invokation.
 */

public class IndexSearcherContainer {

    private static Logger log = Logger.getLogger(IndexSearcherContainer.class.getName());
    private static IndexSearcherContainer instance = new IndexSearcherContainer();
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
    private static String baseDir = "/export/search/index/";
    private static MGISimilarity mgis = new MGISimilarity();

    private IndexSearcherContainer() {
    }

    public static IndexSearcherContainer getIndexSearcherContainer(Configuration stConfig) {

        baseDir = stConfig.get("INDEX_DIR");
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
            instance.setMarkerVocabAccIDIndex(baseDir + "markerVocabAccID/index");
            instance.setMarkerVocabExactIndex(baseDir + "markerVocabExact/index");
        }
        return instance;
    }

    private IndexSearcher getIndex(String index) {
        IndexSearcher is = null;
        try {
            is = new IndexSearcher(index);
            is.setSimilarity(mgis);
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        return is;
    }

    public IndexSearcher getOtherExactIndex() {
        return otherExactIndex;
    }

    private void setOtherExactIndex(String Index) {
        this.otherExactIndex = getIndex(Index);
    }

    public IndexSearcher getOtherDisplayIndex() {
        return otherDisplayIndex;
    }

    private void setOtherDisplayIndex(String Index) {
        this.otherDisplayIndex = getIndex(Index);
    }

    public IndexSearcher getMarkerInexactIndex() {
        return markerInexactIndex;
    }

    private void setMarkerInexactIndex(String Index) {
        this.markerInexactIndex = getIndex(Index);
    }

    public IndexSearcher getMarkerExactIndex() {
        return markerExactIndex;
    }

    private void setMarkerExactIndex(String Index) {
        this.markerExactIndex = getIndex(Index);
    }

    public IndexSearcher getMarkerAccIDIndex() {
        return markerAccIDIndex;
    }

    private void setMarkerAccIDIndex(String Index) {
        this.markerAccIDIndex = getIndex(Index);
    }

    public IndexSearcher getMarkerSymbolIndex() {
        return markerSymbolIndex;
    }

    private void setMarkerSymbolIndex(String Index) {
        this.markerSymbolIndex = getIndex(Index);
    }

    public static void main(String[] args) {
        log.debug("Testing");
    }

    public IndexSearcher getVocabInexact() {
        return vocabInexactIndex;
    }

    private void setVocabInexact(String vocabIndex) {
        this.vocabInexactIndex = getIndex(vocabIndex);
    }

    public IndexSearcher getVocabExactIndex() {
        return vocabExact;
    }

    private void setVocabExactIndex(String vocabIndex) {
        this.vocabExact = getIndex(vocabIndex);
    }

    public IndexSearcher getVocabAccIDIndex() {
        return vocabAccID;
    }

    private void setVocabAccIDIndex(String vocabIndex) {
        this.vocabAccID = getIndex(vocabIndex);
    }

    public IndexSearcher getMarkerVocabAccIDIndex() {
        return markerVocabAccIDIndex;
    }

    private void setMarkerVocabAccIDIndex(String vocabIndex) {
        this.markerVocabAccIDIndex = getIndex(vocabIndex);
    }

    public IndexSearcher getMarkerVocabExactIndex() {
        return markerVocabExactIndex;
    }

    private void setMarkerVocabExactIndex(String vocabIndex) {
        this.markerVocabExactIndex = getIndex(vocabIndex);
    }
}

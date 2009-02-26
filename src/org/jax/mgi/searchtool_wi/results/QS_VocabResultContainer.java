package org.jax.mgi.searchtool_wi.results;

// standard java
import java.util.*;

import org.jax.mgi.shr.searchtool.IndexConstants;

public class QS_VocabResultContainer extends ResultContainer {

  HashMap<String, QS_VocabResult> goMappedResults = new HashMap<String, QS_VocabResult>();
  HashMap<String, QS_VocabResult> omimMappedResults = new HashMap<String, QS_VocabResult>();
  HashMap<String, QS_VocabResult> adMappedResults = new HashMap<String, QS_VocabResult>();
  HashMap<String, QS_VocabResult> mpMappedResults = new HashMap<String, QS_VocabResult>();
  HashMap<String, QS_VocabResult> psMappedResults = new HashMap<String, QS_VocabResult>();
  HashMap<String, QS_VocabResult> ipMappedResults = new HashMap<String, QS_VocabResult>();

  // ////////////
  // Constructor
  // ////////////
  public QS_VocabResultContainer(List l) {

      super(l);

      for (Iterator<QS_VocabResult> iter = l.iterator(); iter.hasNext();) {
          // seperate the score-sorted list into individual vocabs
          QS_VocabResult current = iter.next();
          if (current.getVocabulary().equals(IndexConstants.GO_TYPE_NAME)) {
              goMappedResults.put(current.getDbKey().toString(), current);
          } else if (current.getVocabulary().equals(IndexConstants.OMIM_TYPE_NAME)) {
              omimMappedResults.put(current.getDbKey().toString(), current);
          } else if (current.getVocabulary().equals(IndexConstants.MP_TYPE_NAME)) {
              mpMappedResults.put(current.getDbKey().toString(), current);
          } else if (current.getVocabulary().equals(IndexConstants.AD_TYPE_NAME)) {
              adMappedResults.put(current.getDbKey().toString(), current);
          } else if (current.getVocabulary().equals(IndexConstants.INTERPRO_TYPE_NAME)) {
              ipMappedResults.put(current.getDbKey().toString(), current);
          } else if (current.getVocabulary().equals(IndexConstants.PIRSF_TYPE_NAME)) {
              psMappedResults.put(current.getDbKey().toString(), current);
          }

      }
  }

  // GO
  public QS_VocabResult getGoByKey(String key) {
      return (QS_VocabResult) goMappedResults.get(key);
  }

  // Omim
  public QS_VocabResult getOmimByKey(String key) {
      return (QS_VocabResult) omimMappedResults.get(key);
  }

  // MP
  public QS_VocabResult getMpByKey(String key) {
      return (QS_VocabResult) mpMappedResults.get(key);
  }

  // AD
  public QS_VocabResult getAdByKey(String key) {
      return (QS_VocabResult) adMappedResults.get(key);
  }

  // PIRSF
  public QS_VocabResult getPsByKey(String key) {
      return (QS_VocabResult) psMappedResults.get(key);
  }

  // Inter Prot
  public QS_VocabResult getIpByKey(String key) {
      return (QS_VocabResult) ipMappedResults.get(key);
  }

  // /////////////////
  // Mapped Accessors
  // /////////////////
  public int size() {
      return scoreSortedResults.size();
  }

}

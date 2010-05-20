package org.jax.mgi.searchtool_wi.results;

// standard java
import java.util.*;

import org.jax.mgi.shr.searchtool.IndexConstants;

public class VocabResultContainer extends ResultContainer {

  HashMap<String, VocabResult> goMappedResults = new HashMap<String, VocabResult>();
  HashMap<String, VocabResult> omimMappedResults = new HashMap<String, VocabResult>();
  HashMap<String, VocabResult> adMappedResults = new HashMap<String, VocabResult>();
  HashMap<String, VocabResult> mpMappedResults = new HashMap<String, VocabResult>();
  HashMap<String, VocabResult> psMappedResults = new HashMap<String, VocabResult>();
  HashMap<String, VocabResult> ipMappedResults = new HashMap<String, VocabResult>();

  // ////////////
  // Constructor
  // ////////////
  public VocabResultContainer(List l) {

      super(l);

      for (Iterator<VocabResult> iter = l.iterator(); iter.hasNext();) {
          // seperate the score-sorted list into individual vocabs
          VocabResult current = iter.next();
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
  public VocabResult getGoByKey(String key) {
      return (VocabResult) goMappedResults.get(key);
  }

  // Omim
  public VocabResult getOmimByKey(String key) {
      return (VocabResult) omimMappedResults.get(key);
  }

  // MP
  public VocabResult getMpByKey(String key) {
      return (VocabResult) mpMappedResults.get(key);
  }

  // AD
  public VocabResult getAdByKey(String key) {
      return (VocabResult) adMappedResults.get(key);
  }

  // PIRSF
  public VocabResult getPsByKey(String key) {
      return (VocabResult) psMappedResults.get(key);
  }

  // Inter Prot
  public VocabResult getIpByKey(String key) {
      return (VocabResult) ipMappedResults.get(key);
  }

  // /////////////////
  // Mapped Accessors
  // /////////////////
  public int size() {
      return scoreSortedResults.size();
  }

}
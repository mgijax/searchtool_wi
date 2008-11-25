package org.jax.mgi.searchtool_wi.utils;

// Standard Java Classes
import java.util.*;

// Quick Search Classes
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
* ScoreConstants defines score-related static final constants and holds
* dataType->scoreModifier mappings.  The mapping entries are normally a
* IndexConstant -> ScoreConstant pair
*
* The implementation of map loading will load the static maps *once* when the
* class is first loaded in the JVM by the class loader (not once per thread)
*/
public class ScoreConstants {

  public static final Float ZERO_SCORE             = new Float(0.0);
  public static final Float VOC_AND_BOOST          = new Float(100.0);
  public static final Float VOC_EXACT_BOOST        = new Float(1000.0);

  //-------------------------------------------------//
  // Marker Exact Match Additive Scores (ME_ prefix)
  //-------------------------------------------------//

  /**
  * Return exact marker score map
  * @return map (typeValue -> score to add)
  */
  public static Map getMarkerExactScoreMap() {
    return markerExactScoreMap;
  }

  private static final Float ME_ACCID_MRK_SCORE         = new Float(37.0);
  private static final Float ME_ACCID_ALL_SCORE         = new Float(36.0);
  private static final Float ME_ACCID_ORTHO_SCORE       = new Float(35.0);
  private static final Float ME_SYMBOL_SCORE            = new Float(34.0);
  private static final Float ME_NAME_SCORE              = new Float(33.0);
  private static final Float ME_OLD_SYMBOL_SCORE        = new Float(32.0);
  private static final Float ME_OLD_NAME_SCORE          = new Float(31.0);
  private static final Float ME_SYNONYM_SCORE           = new Float(30.0);
  private static final Float ME_ALL_SYMBOL_SCORE        = new Float(22.0);
  private static final Float ME_ALL_NAME_SCORE          = new Float(21.0);
  private static final Float ME_ALL_SYNONYM_SCORE       = new Float(20.0);
  private static final Float ME_ORTHO_HUM_SYMBOL_SCORE  = new Float(14.0);
  private static final Float ME_ORTHO_RAT_SYMBOL_SCORE  = new Float(13.0);
  private static final Float ME_ORTHO_SYMBOL_SCORE      = new Float(12.0);
  private static final Float ME_ORTHO_NAME_SCORE        = new Float(11.0);
  private static final Float ME_ORTHO_SYNONYM_SCORE     = new Float(10.0);
  private static final Float ME_VOC_ACCID_SCORE         = new Float(7.0);
  private static final Float ME_VOC_TERM_SCORE          = new Float(6.0);
  private static final Float ME_VOC_SYNONYM_SCORE       = new Float(5.0);
  private static final Float ME_VOC_NOTE_SCORE          = new Float(4.0);
  private static final Float ME_DEFAULT                 = new Float(0.0);

  private static Map markerExactScoreMap = loadMarkerExactScoreMap();
  private static final Map loadMarkerExactScoreMap() {
    Map scoreMap = new HashMap();
    scoreMap.put(IndexConstants.ACCESSION_ID,           ME_ACCID_MRK_SCORE);
    scoreMap.put(IndexConstants.ALLELE_ACCESSION_ID,    ME_ACCID_ALL_SCORE);
    scoreMap.put(IndexConstants.ORTH_ACCESSION_ID,      ME_ACCID_ORTHO_SCORE);
    scoreMap.put(IndexConstants.MARKER_SYMBOL,          ME_SYMBOL_SCORE);
    scoreMap.put(IndexConstants.MARKER_NAME,            ME_NAME_SCORE);
    scoreMap.put(IndexConstants.OLD_MARKER_SYMBOL,      ME_OLD_SYMBOL_SCORE);
    scoreMap.put(IndexConstants.OLD_MARKER_NAME,        ME_OLD_NAME_SCORE);
    scoreMap.put(IndexConstants.MARKER_SYNOYNM,         ME_SYNONYM_SCORE);
    scoreMap.put(IndexConstants.ALLELE_SYMBOL,          ME_ALL_SYMBOL_SCORE);
    scoreMap.put(IndexConstants.ALLELE_SYNONYM,         ME_ALL_SYNONYM_SCORE);
    scoreMap.put(IndexConstants.ALLELE_NAME,            ME_ALL_NAME_SCORE);
    scoreMap.put(IndexConstants.ORTHOLOG_SYMBOL_HUMAN,  ME_ORTHO_HUM_SYMBOL_SCORE);
    scoreMap.put(IndexConstants.ORTHOLOG_SYMBOL_RAT,    ME_ORTHO_RAT_SYMBOL_SCORE);
    scoreMap.put(IndexConstants.ORTHOLOG_SYMBOL,        ME_ORTHO_SYMBOL_SCORE);
    scoreMap.put(IndexConstants.ORTHOLOG_NAME,          ME_ORTHO_NAME_SCORE);
    scoreMap.put(IndexConstants.ORTHOLOG_SYNONYM,       ME_ORTHO_SYNONYM_SCORE);
    scoreMap.put(IndexConstants.VOC_ACCESSION_ID,       ME_VOC_ACCID_SCORE);
    scoreMap.put(IndexConstants.VOCAB_TERM,             ME_VOC_TERM_SCORE);
    scoreMap.put(IndexConstants.VOCAB_SYNONYM,          ME_VOC_SYNONYM_SCORE);
    scoreMap.put(IndexConstants.VOCAB_NOTE,             ME_VOC_NOTE_SCORE);
    scoreMap.put("default",                             ME_DEFAULT);
    return scoreMap;
  }

  //-------------------------------------------------//
  // Marker "AND" Match Additive Scores (MA_ prefix)
  //-------------------------------------------------//

  /**
  * Return "AND" search marker score map
  * @return map (typeValue -> score to add)
  */
  public static Map getMrkAndScoreMap() {
    return mrkAndScoreMap;
  }

  // Marker Nomen "AND" (MNA) Match Additive Scores
  private static final Float MA_SYMBOL_ADD           = new Float(104.5);
  private static final Float MA_NAME_ADD             = new Float(104.4);
  private static final Float MA_SYNONYM_ADD          = new Float(104.3);
  private static final Float MA_SYMBOL_OLD_ADD       = new Float(104.2);
  private static final Float MA_NAME_OLD_ADD         = new Float(104.1);
  private static final Float MA_ALL_SYMBOL_ADD       = new Float(103.2);
  private static final Float MA_ALL_SYNONYM_ADD      = new Float(103.1);
  private static final Float MA_ALL_NAME_ADD         = new Float(103.0);
  private static final Float MA_ORTHO_HUM_SYMBOL_ADD = new Float(102.5);
  private static final Float MA_ORTHO_RAT_SYMBOL_ADD = new Float(102.4);
  private static final Float MA_ORTHO_SYMBOL_ADD     = new Float(102.3);
  private static final Float MA_ORTHO_NAME_ADD       = new Float(102.2);
  private static final Float MA_ORTHO_SYNONYM_ADD    = new Float(102.1);
  private static final Float MA_VOC_TERM_ADD         = new Float(101.3);
  private static final Float MA_VOC_SYNONYM_ADD      = new Float(101.2);
  private static final Float MA_VOC_NOTE_ADD         = new Float(101.1);
  private static final Float MA_DEFAULT              = new Float(100.0);

  private static Map mrkAndScoreMap = loadMrkAndScoreMap();
  private static Map loadMrkAndScoreMap() {
    Map scoreMap = new HashMap();
    scoreMap.put(IndexConstants.MARKER_SYMBOL,          MA_SYMBOL_ADD);
    scoreMap.put(IndexConstants.MARKER_NAME,            MA_NAME_ADD);
    scoreMap.put(IndexConstants.MARKER_SYNOYNM,         MA_SYNONYM_ADD);
    scoreMap.put(IndexConstants.OLD_MARKER_SYMBOL,      MA_SYMBOL_OLD_ADD);
    scoreMap.put(IndexConstants.OLD_MARKER_NAME,        MA_NAME_OLD_ADD);
    scoreMap.put(IndexConstants.ALLELE_SYMBOL,          MA_ALL_SYMBOL_ADD);
    scoreMap.put(IndexConstants.ALLELE_SYNONYM,         MA_ALL_SYNONYM_ADD);
    scoreMap.put(IndexConstants.ALLELE_NAME,            MA_ALL_NAME_ADD);
    scoreMap.put(IndexConstants.ORTHOLOG_SYMBOL_HUMAN,  MA_ORTHO_HUM_SYMBOL_ADD);
    scoreMap.put(IndexConstants.ORTHOLOG_SYMBOL_RAT,    MA_ORTHO_RAT_SYMBOL_ADD);
    scoreMap.put(IndexConstants.ORTHOLOG_SYMBOL,        MA_ORTHO_SYMBOL_ADD);
    scoreMap.put(IndexConstants.ORTHOLOG_NAME,          MA_ORTHO_NAME_ADD);
    scoreMap.put(IndexConstants.ORTHOLOG_SYNONYM,       MA_ORTHO_SYNONYM_ADD);
    return scoreMap;
  }

  //-----------------------------------------------------------//
  // Marker "OR" Match Additive Scores and Weights (MO_ prefix)
  //-----------------------------------------------------------//

    //-----------//
    // Additive
    //-----------//

  /**
  * Return "OR" search marker score map
  * @return map (typeValue -> score to add)
  */
  public static Map getMrkOrScoreMap() {
    return mrkOrScoreMap;
  }

  private static final Float MO_SYMBOL_ADD            = new Float(0.7);

  private static Map mrkOrScoreMap = loadMrkOrScoreMap();
  private static Map loadMrkOrScoreMap() {
    Map scoreMap = new HashMap();
    scoreMap.put(IndexConstants.MARKER_SYMBOL,          MO_SYMBOL_ADD);
    scoreMap.put(IndexConstants.MARKER_NAME,            ZERO_SCORE);
    scoreMap.put(IndexConstants.MARKER_SYNOYNM,         ZERO_SCORE);
    scoreMap.put(IndexConstants.OLD_MARKER_SYMBOL,      ZERO_SCORE);
    scoreMap.put(IndexConstants.OLD_MARKER_NAME,        ZERO_SCORE);
    scoreMap.put(IndexConstants.ALLELE_SYMBOL,          ZERO_SCORE);
    scoreMap.put(IndexConstants.ALLELE_SYNONYM,         ZERO_SCORE);
    scoreMap.put(IndexConstants.ALLELE_NAME,            ZERO_SCORE);
    scoreMap.put(IndexConstants.ORTHOLOG_SYMBOL_HUMAN,  ZERO_SCORE);
    scoreMap.put(IndexConstants.ORTHOLOG_SYMBOL_RAT,    ZERO_SCORE);
    scoreMap.put(IndexConstants.ORTHOLOG_SYMBOL,        ZERO_SCORE);
    scoreMap.put(IndexConstants.ORTHOLOG_NAME,          ZERO_SCORE);
    scoreMap.put(IndexConstants.ORTHOLOG_SYNONYM,       ZERO_SCORE);
    return scoreMap;
  }

    //----------------//
    // Multiplicative
    //----------------//

  /**
  * Return "OR" search marker weight map
  * @return map (typeValue -> weight to multiplicatively modify lucene score)
  */
  public static Map getMrkOrWeightMap() {
    return mrkOrWeightMap;
  }

  // Marker "OR" (MO) Increased Weighting
  private static final Float MO_SYMBOL_WEIGHT           = new Float(0.14);
  private static final Float MO_NAME_WEIGHT             = new Float(0.13);
  private static final Float MO_SYNONYM_WEIGHT          = new Float(0.12);
  private static final Float MO_SYMBOL_OLD_WEIGHT       = new Float(0.11);
  private static final Float MO_NAME_OLD_WEIGHT         = new Float(0.10);
  private static final Float MO_ALL_SYMBOL_WEIGHT       = new Float(0.08);
  private static final Float MO_ALL_SYNONYM_WEIGHT      = new Float(0.07);
  private static final Float MO_ALL_NAME_WEIGHT         = new Float(0.06);
  private static final Float MO_ORTHO_HUM_SYMBOL_WEIGHT = new Float(0.05);
  private static final Float MO_ORTHO_RAT_SYMBOL_WEIGHT = new Float(0.04);
  private static final Float MO_ORTHO_SYMBOL_WEIGHT     = new Float(0.03);
  private static final Float MO_ORTHO_NAME_WEIGHT       = new Float(0.02);
  private static final Float MO_ORTHO_SYNONYM_WEIGHT    = new Float(0.01);

  private static Map mrkOrWeightMap = loadMrkOrWeightMap();
  private static Map loadMrkOrWeightMap() {
    Map scoreMap = new HashMap();
    scoreMap.put(IndexConstants.MARKER_SYMBOL,          MO_SYMBOL_WEIGHT);
    scoreMap.put(IndexConstants.MARKER_NAME,            MO_NAME_WEIGHT);
    scoreMap.put(IndexConstants.MARKER_SYNOYNM,         MO_SYNONYM_WEIGHT);
    scoreMap.put(IndexConstants.OLD_MARKER_SYMBOL,      MO_SYMBOL_OLD_WEIGHT);
    scoreMap.put(IndexConstants.OLD_MARKER_NAME,        MO_NAME_OLD_WEIGHT);
    scoreMap.put(IndexConstants.ALLELE_SYMBOL,          MO_ALL_SYMBOL_WEIGHT);
    scoreMap.put(IndexConstants.ALLELE_NAME,            MO_ALL_NAME_WEIGHT);
    scoreMap.put(IndexConstants.ALLELE_SYNONYM,         MO_ALL_SYNONYM_WEIGHT);
    scoreMap.put(IndexConstants.ORTHOLOG_SYMBOL,        MO_ORTHO_SYMBOL_WEIGHT);
    scoreMap.put(IndexConstants.ORTHOLOG_NAME,          MO_ORTHO_NAME_WEIGHT);
    scoreMap.put(IndexConstants.ORTHOLOG_SYNONYM,       MO_ORTHO_SYNONYM_WEIGHT);
    scoreMap.put("default",                             ZERO_SCORE);
    return scoreMap;
  }


  //-----------------------------------------------------------//
  // Vocab Exact Matches
  //-----------------------------------------------------------//

  /**
  * Return exact vocab match score map
  * @return map (typeValue -> score to add)
  */
  public static Map getVocabExactScoreMap() {
    return vocabExactScoreMap;
  }

  // Vocab Exact Additive Match Scores (VE)
  public static final Float VE_VOC_ACCID_BOOST     = new Float(5.0);
  public static final Float VE_VOC_TERM_BOOST      = new Float(4.0);
  public static final Float VE_VOC_SYNONYM_BOOST   = new Float(3.0);
  public static final Float VE_VOC_NOTE_BOOST      = new Float(2.0);

  private static Map vocabExactScoreMap = loadVocabExactScoreMap();
  private static Map loadVocabExactScoreMap() {
    Map scoreMap = new HashMap();
    scoreMap.put(IndexConstants.ACCESSION_ID,   VE_VOC_ACCID_BOOST);
    scoreMap.put(IndexConstants.VOCAB_TERM,     VE_VOC_TERM_BOOST);
    scoreMap.put(IndexConstants.VOCAB_SYNONYM,  VE_VOC_SYNONYM_BOOST);
    scoreMap.put(IndexConstants.VOCAB_NOTE,     VE_VOC_NOTE_BOOST);
    return scoreMap;
  }

  //-----------------------------------------------------------//
  // Vocab InExact Matches
  //-----------------------------------------------------------//

  /**
  * Return vocab match score map
  * @return map (typeValue -> score to add)
  */
  public static Map getVocabInexactScoreMap() {
    return vocabScoreMap;
  }

  // Vocab Inexact Additive Match Scores (VI)
  public static final Float VI_VOC_TERM_BOOST      = new Float(0.3);
  public static final Float VI_VOC_SYNONYM_BOOST   = new Float(0.2);
  public static final Float VI_VOC_NOTE_BOOST      = new Float(0.1);

  private static Map vocabScoreMap = loadVocabScoreMap();
  private static Map loadVocabScoreMap() {
    Map scoreMap = new HashMap();
    scoreMap.put(IndexConstants.VOCAB_TERM,     VI_VOC_TERM_BOOST);
    scoreMap.put(IndexConstants.VOCAB_SYNONYM,  VI_VOC_SYNONYM_BOOST);
    scoreMap.put(IndexConstants.VOCAB_NOTE,     VI_VOC_NOTE_BOOST);
    return scoreMap;
  }

  //-------------------------------------------------//
  // Marker Result Type Modifiers
  //-------------------------------------------------//

  /**
  * Return map containing type boosting
  * @return map (typeValue -> boost to add)
  */
  public static Map getMarkerResultBoostMap() {
    return markerResultBoostMap;
  }

  private static final Float MR_ACCID_MRK_BOOST         = new Float(37.0);
  private static final Float MR_ACCID_ALL_BOOST         = new Float(36.0);
  private static final Float MR_ACCID_ORTHO_BOOST       = new Float(35.0);
  private static final Float MR_SYMBOL_BOOST            = new Float(34.0);
  private static final Float MR_NAME_BOOST              = new Float(33.0);
  private static final Float MR_OLD_SYMBOL_BOOST        = new Float(32.0);
  private static final Float MR_OLD_NAME_BOOST          = new Float(31.0);
  private static final Float MR_SYNONYM_BOOST           = new Float(30.0);
  private static final Float MR_ALL_SYMBOL_BOOST        = new Float(22.0);
  private static final Float MR_ALL_NAME_BOOST          = new Float(21.0);
  private static final Float MR_ALL_SYNONYM_BOOST       = new Float(20.0);
  private static final Float MR_ORTHO_HUM_SYMBOL_BOOST  = new Float(14.0);
  private static final Float MR_ORTHO_RAT_SYMBOL_BOOST  = new Float(13.0);
  private static final Float MR_ORTHO_SYMBOL_BOOST      = new Float(12.0);
  private static final Float MR_ORTHO_NAME_BOOST        = new Float(11.0);
  private static final Float MR_ORTHO_SYNONYM_BOOST     = new Float(10.0);
  private static final Float MR_VOC_ACCID_BOOST         = new Float(7.0);
  private static final Float MR_VOC_TERM_BOOST          = new Float(6.0);
  private static final Float MR_VOC_SYNONYM_BOOST       = new Float(5.0);
  private static final Float MR_VOC_NOTE_BOOST          = new Float(4.0);

  private static Map markerResultBoostMap = loadMarkerResultBoostMap();
  private static final Map loadMarkerResultBoostMap() {
    Map boostMap = new HashMap();
    boostMap.put(IndexConstants.ACCESSION_ID,           MR_ACCID_MRK_BOOST);
    boostMap.put(IndexConstants.ALLELE_ACCESSION_ID,    MR_ACCID_ALL_BOOST);
    boostMap.put(IndexConstants.ORTH_ACCESSION_ID,      MR_ACCID_ORTHO_BOOST);
    boostMap.put(IndexConstants.MARKER_SYMBOL,          MR_SYMBOL_BOOST);
    boostMap.put(IndexConstants.MARKER_NAME,            MR_NAME_BOOST);
    boostMap.put(IndexConstants.OLD_MARKER_SYMBOL,      MR_OLD_SYMBOL_BOOST);
    boostMap.put(IndexConstants.OLD_MARKER_NAME,        MR_OLD_NAME_BOOST);
    boostMap.put(IndexConstants.MARKER_SYNOYNM,         MR_SYNONYM_BOOST);
    boostMap.put(IndexConstants.ALLELE_SYMBOL,          MR_ALL_SYMBOL_BOOST);
    boostMap.put(IndexConstants.ALLELE_SYNONYM,         MR_ALL_SYNONYM_BOOST);
    boostMap.put(IndexConstants.ALLELE_NAME,            MR_ALL_NAME_BOOST);
    boostMap.put(IndexConstants.ORTHOLOG_SYMBOL_HUMAN,  MR_ORTHO_HUM_SYMBOL_BOOST);
    boostMap.put(IndexConstants.ORTHOLOG_SYMBOL_RAT,    MR_ORTHO_RAT_SYMBOL_BOOST);
    boostMap.put(IndexConstants.ORTHOLOG_SYMBOL,        MR_ORTHO_SYMBOL_BOOST);
    boostMap.put(IndexConstants.ORTHOLOG_NAME,          MR_ORTHO_NAME_BOOST);
    boostMap.put(IndexConstants.ORTHOLOG_SYNONYM,       MR_ORTHO_SYNONYM_BOOST);
    boostMap.put(IndexConstants.VOC_ACCESSION_ID,       MR_VOC_ACCID_BOOST);
    boostMap.put(IndexConstants.VOCAB_TERM,             MR_VOC_TERM_BOOST);
    boostMap.put(IndexConstants.VOCAB_SYNONYM,          MR_VOC_SYNONYM_BOOST);
    boostMap.put(IndexConstants.VOCAB_NOTE,             MR_VOC_NOTE_BOOST);
    return boostMap;
  }


}

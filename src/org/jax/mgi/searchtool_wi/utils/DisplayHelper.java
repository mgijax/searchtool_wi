package org.jax.mgi.searchtool_wi.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.jax.mgi.searchtool_wi.dataAccess.IndexReaderContainer;
import org.jax.mgi.searchtool_wi.lookup.MarkerDisplay;
import org.jax.mgi.searchtool_wi.lookup.MarkerDisplayCache;
import org.jax.mgi.searchtool_wi.lookup.VocabDisplay;
import org.jax.mgi.searchtool_wi.lookup.VocabDisplayCache;
import org.jax.mgi.searchtool_wi.matches.AbstractMatch;
import org.jax.mgi.searchtool_wi.results.QS_MarkerResult;
import org.jax.mgi.searchtool_wi.results.QS_VocabResult;
import org.jax.mgi.shr.config.Configuration;

import QS_Commons.IndexConstants;
import QS_Commons.MGIAnalyzer;
import QS_Commons.StemmedMGIAnalyzer;

/** The DisplayHelper object provides static utility methods for display
* @module DisplayHelper
*/
public class DisplayHelper
{

  //-------------------------------//
  // Class Fields & Initialization
  //-------------------------------//

  // Set up the logger
  private static Logger logger
    = Logger.getLogger(DisplayHelper.class.getName());

  private Configuration stConfig;
  private static MarkerDisplayCache markerDisplayCache;
  private static VocabDisplayCache vocabDisplayCache;
  private static IndexReaderContainer irc;

  // Analyzers
  static Analyzer standard_analyzer    = new StandardAnalyzer();
  static Analyzer keyword_analyzer     = new KeywordAnalyzer();
  static Analyzer snowball_analyzer    = new SnowballAnalyzer("English");
  static Analyzer mgi_analyzer         = new MGIAnalyzer();
  static Analyzer stemmed_mgi_analyzer = new StemmedMGIAnalyzer();

  //--------------//
  // Constructors
  //--------------//

  /**
  * Hiding the default constructor; must be passed config for usage of
  * non-static methods.  The static method calls in this object are
  * intended to be used without class instantiation.
  */
  private DisplayHelper(){}
  public DisplayHelper(Configuration c ,
                       MarkerDisplayCache mdc,
                       VocabDisplayCache vdc)
  {
    stConfig = c;
    irc = IndexReaderContainer.getIndexReaderContainer(stConfig);

    // Setup display caches
    markerDisplayCache = mdc;
    vocabDisplayCache = vdc;
  }


  //---------------------//
  // Public Class Methods
  //---------------------//

  //---------------------------------------------------------- OverLib Pop-ups

  public String getOverlibUserDocString() {
    return "<div class=\\\'detailRowType\\\'>See <a href=\\\'"
      + stConfig.get("USERDOCS_URL")
      + "searchtool_help.shtml\\\'>Using the Quick Search Tool</a> "
      + "for more information and examples.</div>";
  }

  public String getScoreMouseOver()
  {
    String star = "<img src=" + stConfig.get("QUICKSEARCH_URL")
      + "darkStarSmall.gif>";
    String scoreMouseOver = "return overlib('Score is based on similarity between your text and IDs, nomenclature, and vocabulary term text in MGIs database.<br/>" + star + star + star + " - perfect match between you search and matched text<br/>&nbsp;&nbsp;" + star + star + " - all words in your search appear in matched text<br/>&nbsp;&nbsp;&nbsp;&nbsp;" + star + " - some words in your search appear in matched text ', STICKY, CAPTION, 'Score', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')";

    return scoreMouseOver;
  }

  public String getVocabBestMatchMouseOver() {

    String message = "<div class=\\\'detailRowType\\\'>Displaying matches "
      + "to vocabulary terms, synonyms, and"
      + " definitions based on text similarity to your search text.  Not "
      + "displaying subterms of matched terms.</div>"
      + getOverlibUserDocString();

    String bestMatchMouseOver = "return overlib('"
      + message
      + "', STICKY, CAPTION, 'Best Match', HAUTO, BELOW, OFFSETY, 20,"
      + " WIDTH, 300, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')";

    return bestMatchMouseOver;
  }

  public String getMarkerBestMatchMouseOver() {

    String message = "<div class=\\\'detailRowType\\\'>For each genome feature, displaying the most relevant associated "
      + "vocabulary term, ID or nomenclature.  Displayed vocabulary terms may be "
      + "subterms of the best matching term, e.g., query matched &quot;hippocampus"
      + "&quot; and subterm associated with the genome feature is &quot;dentate gyrus&quot;.</div>"
      + getOverlibUserDocString();

    String bestMatchMouseOver = "return overlib('"
      + message
      + "', STICKY, CAPTION, 'Best Match', HAUTO, BELOW, OFFSETY, 20,"
      + " WIDTH, 300, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')";

    return bestMatchMouseOver;
  }

  //------------------------------------------------------- Search Detail Info

  public String getWhyMatchSearchDetails (SearchInput si) throws IOException{
      // The top part of the message text will ALWAYS appear, so simply construct it.

      String topMessageText = "<div class='small'>" +getSearchTermDisplay(si)
                  + " " + getStemmedSearchTermDisplay(si) + "</div>";

      String bottomMessageText = "";

      bottomMessageText += getZeroHitTokensString(si);

      // The bottom part of the message text is only brought back if a missing large
      // token is found.  So we want to suppress its div if that's the case.

      if (!bottomMessageText.equals("")) {
          bottomMessageText = "<div class='small'>" + bottomMessageText + "</div>";
      }

      return topMessageText + bottomMessageText;
  }

 /**
  * Returns the zero hit tokens string.
  * @param  Searchinput si - The encapsulation of the users search string.
  * @return String - The displayable zero hit token string.
  */

  public String getZeroHitTokensString(SearchInput si) {

      String messageText ="";

      String warningIcon = "<img src=" + stConfig.get("QUICKSEARCH_URL")
      + "redwarning.gif style='vertical-align: middle'>";

      List<String> zeroHitTokens = si.getZeroHitTokens();

      // First Check to see if we have zero token hits:
      // If we do we need to display something slightly different on the link section.


      if (zeroHitTokens.size() > 0) {
          messageText += "<span>"
                  + warningIcon
                  + "</span><span style='margin-bottom: 100px;'>Could not find the term(s): "
                  +	"<span class='redText'>";

          // The first word that we print out needs no comma, so set a flag to
          // keep track.

          int first = 0;

          for (Iterator<String> iter = zeroHitTokens.iterator(); iter
                  .hasNext();) {
              String token = (String) iter.next();

              // Print out the message

              if (first == 0) {
                  messageText += superscript(token.replaceAll("\\\\", ""));
                  first = 1;
              } else {
                  messageText += "</span>, <span class='redText'>"
                          + superscript(token.replaceAll("\\\\", ""));
              }
          }
          // Close out the span.
          messageText += "</span>.</span>";
      }

      return messageText;
  }

  public String getSearchTermDisplay (SearchInput si) throws IOException{
      // Display the search terms

      String text = "";

      List<String> tokenizedInputString = si.getTokenizedLittleInputString();

      if (tokenizedInputString.size() > 0) {
          text += "<span>You searched for: <span class=\"italic\">";

          for (Iterator<String> iter = tokenizedInputString.iterator(); iter.hasNext();) {
              String token = (String) iter.next();

              // Overlib cannot handle quotes in its input, so for ones that we want
              // to display we need to use the html encoding.

              text += " " + token.replaceAll("\"", "&quot;");
          }

          text += ".</span></span>";

      }

      return text;
  }

  public String getStemmedSearchTermDisplay(SearchInput si) throws IOException{
      // Display the stemmed search terms, if applicable

      String text = "";

      List<String> stemmedTokenizedInputString = si.getStemmedTokenizedLittleInputString();

      if (stemmedTokenizedInputString.size() > 0 && !si.hasPrefix()) {
          text += "<span>Also searched for: <span class=\"italic\">";

          for (Iterator<String> iter = stemmedTokenizedInputString.iterator(); iter
                  .hasNext();) {
              String token = (String) iter.next();
              if (token.contains("\"")) {
                  String[] catcher = token.split("\"");

                  String[] subCatcher = catcher[1].split("\\s");

                  String newToken = "&quot;";

                  for (int j = 0; j < subCatcher.length; j++) {
                      if (j == 0) {
                          newToken += subCatcher[j] + "-";
                      } else {
                          newToken += " " + subCatcher[j] + "-";
                      }

                  }

                  newToken += "&quot;";
                  text += " " + newToken;

              } else {
                  text += " " + token.replaceAll("\"", "&quot;") + "-";
              }
          }

          text += ".</span><span>";
      }

      return text;
  }


  /**
   * Returns the Search Details Link.
   * @param si The encapsulation of the users search string.
   * @return String with the fully formed search details link.
   * @throws IOException
   */
  public String getSearchDetailOnClick(SearchInput si) throws IOException {

        String warningIconOverLib = "<img src=" + stConfig.get("QUICKSEARCH_URL")
                + "redwarning.gif style=\\\'vertical-align: middle\\\'>";
        String messageText = "<div class='small'>";
        String text = "";

        // Need to know if we have zero hit tokens
        List<String> zeroHitTokens = si.getZeroHitTokens();

        messageText += getZeroHitTokensString(si);

        // Create the text for the hyperlink section of the message.

        messageText += " See <a href='#' onClick=\"";

        // Create the Text of the Overlib popup
        text += "<div class=\\\'small\\\'>";

        // Display the search terms

        List<String> tokenizedInputString = si.getTokenizedLittleInputString();

        if (tokenizedInputString.size() > 0) {
            text += "<div class=\\\'detailRowType\\\'><span class=\\\'detailHeaderType\\\'>" +
            		"Search Terms ";

            if (zeroHitTokens.size() > 0) {
                text += warningIconOverLib;
            }

            text += "</span> <br> Searching for matches to <span class=\\\'italic\\\'>";

            for (Iterator<String> iter = tokenizedInputString.iterator(); iter.hasNext();) {
                String token = (String) iter.next();

                // Overlib cannot handle quotes in its input, so for ones that we want
                // to display we need to use the html encoding.

                text += " " + token.replaceAll("\"", "&quot;");
            }

            text += ".</span>";

            if (zeroHitTokens.size() > 0) {
                text += " Could not find the term(s): ";
                text += "<span class=\\\'redText\\\'>";
                int first = 0;
                for (Iterator<String> iter = zeroHitTokens.iterator(); iter
                        .hasNext();) {
                    String token = (String) iter.next();

                    // Single quotes mess up the overlib, we must escape them

                    if (first == 0) {
                        text += superscript(token).replaceAll("'", "\\\\\\'");
                        first = 1;
                    } else {
                        text += "</span>, <span class=\\\'redText\\\'>"
                                + superscript(token).replaceAll("'", "\\\\\\'");
                    }
                }
                text += "</span>";
                text += ". Searched for best alternative matches.";

            }

            text += "</div>";

        }

        // Display the stemmed search terms, if applicable

        List<String> stemmedTokenizedInputString = si.getStemmedTokenizedLittleInputString();

        if (stemmedTokenizedInputString.size() > 0 && !si.hasPrefix()) {
            text += "<div class=\\\'detailRowType\\\'><span class=\\\'detailHeaderType\\\'>" +
            		"Stemming </span><br> When possible words are &quot;" +
            		"stemmed&quot; to a root word by removing common suffixes. " +
            		"Also searching for matches to <span class=\\\'italic\\\'>";

            for (Iterator<String> iter = stemmedTokenizedInputString.iterator(); iter
                    .hasNext();) {
                String token = (String) iter.next();
                if (token.contains("\"")) {
                    String[] catcher = token.split("\"");

                    String[] subCatcher = catcher[1].split("\\s");

                    String newToken = "&quot;";

                    for (int j = 0; j < subCatcher.length; j++) {
                        if (j == 0) {
                            newToken += subCatcher[j] + "-";
                        } else {
                            newToken += " " + subCatcher[j] + "-";
                        }

                    }

                    newToken += "&quot;";
                    text += " " + newToken;

                } else {
                    text += " " + token.replaceAll("\"", "&quot;") + "-";
                }
            }

            text += ".</span></div>";
        }

        // Display the stop words message.

        if (si.hasStopWords()) {
            text += "<div class=\\\'detailRowType\\\'><span class=\\\'detailHeaderType\\\'>" +
            		"Stop Words</span><br>The search tries to match common words like " +
            		"<span class=\\\'italic\\\'>the</span>, " +
            		"<span class=\\\'italic\\\'>and</span>, " +
            		"<span class=\\\'italic\\\'>of</span>, " +
            		"or <span class=\\\'italic\\\'>with</span> only " +
            		"in current symbols and names for mouse genome features, " +
            		"or in phrases using quotation marks.</div>";
        }


        // Display any boolean detected messages if need be.

        if (si.hasBoolean()) {
            text += "<div class=\\\'detailRowType\\\'><span class=\\\'detailHeaderType\\\'>" +
                "And, Not, and Or</span><br>The search handles " +
                "<span class=\\\'smallCaps\\\'>And</span>, <span class=\\\'smallCaps\\\'>Not</span>" +
                ", and <span class=\\\'smallCaps\\\'>Or</span> as " +
                "Stop Words.  They cannot be used for Boolean logic.</div>";
        }

        // Is this a prefix search?

        if (si.hasPrefix()) {
            text += "<div class=\\\'detailRowType\\\'><span class=\\\'detailHeaderType\\\'>" +
                "Wildcard *</span><br>Your search contains a wildcard.  The search " +
                "tries to match terms not ending in a wildcard exactly.  Accession " +
                "IDs may not use a wildcard.</div>";
        }

        // Are there quotes?

        if (si.hasQuotes()) {
            text += "<div class=\\\'detailRowType\\\'><span class=\\\'detailHeaderType\\\'>" +
                "Quotes &quot; &quot;</span><br>Your search contains quotation marks." +
                "  The search handles terms enclosed in quotation marks as a phrase.</div>";
        }

        // The Case insensitive statement is always present, add it to the text.

        text += "<div class=\\\'detailRowType\\\'><span class=\\\'detailHeaderType\\\'>" +
            "Case</span><br>Searches are not case sensitive; searching for embryo," +
            " Embryo, or eMbRyO will return the same results.</div>";

        text += getOverlibUserDocString();

        text += "</div>";

        return messageText
          + "return overlib('"
          + text
          + "', STICKY, CAPTION, 'Quick Search Details for this search:', HAUTO, BELOW, WIDTH, 375, CLOSECLICK, CLOSETEXT, 'Close X')"
          + "\">Details</a> for this Search.</div>";
    }


  //------------------------------------------------------- Marker "Why Match"

  public boolean needsMrkWhyMatchLink (QS_MarkerResult markerResult)
  {
    boolean needsLink = false;
    if (markerResult.getMatchCount() > 1) {
        needsLink = true;
    }
    return needsLink;
  }

  public String getMrkWhyMatchURL (QS_MarkerResult mResult, String query)
  {
    return "'Search.do?query="
      + getEncodedUrl(query) + "&page=markerDetails&markerKey="
      + mResult.getDbKey() + "'";
  }

  public String getMrkWhyMatchText (QS_MarkerResult markerResult)
  {
    String linkText = "&nbsp;&nbsp;and " + (markerResult.getMatchCount() -1 ) + " more...";
    return linkText;
  }

  /**
   * Helper method that creates a tooltip for the markerbucket.
   * @param markerResult The markerResult we want to create the tooltip for.
   * @return A An anchor tag, containing the overlib javascript to create the tooltip.
   */

  public static String getMarkerScoreMouseOver (QS_MarkerResult markerResult)
  {
    MarkerDisplay markerDisplay = markerDisplayCache.getMarker(markerResult);

    int nomenMatches        = markerResult.getAllMarkerNomenMatches().size();
    int adMatches           = markerResult.getAdMatches().size();
    int mpMatches           = markerResult.getMpMatches().size();
    int goMatches           = markerResult.getGoMatches().size();
    int omimMatches         = markerResult.getOmimMatches().size();
    int omimOrthoMatches    = markerResult.getOmimOrthoMatches().size();
    int pirsfMatches        = markerResult.getPirsfMatches().size();
    int ipMatches           = markerResult.getIpMatches().size();

    String caption = "see matches for " + markerDisplay.getSymbol(); // + MarkerDisplay.getSymbol
    String contents = "";

    if (nomenMatches != 0) {
        contents = contents + nomenMatches + " Nomenclature Matches<br/>";
    }
    if (adMatches != 0) {
        contents = contents + adMatches + " Expression<br/>";
    }
    if (mpMatches != 0) {
        contents = contents + mpMatches + " Phenotype <br/>";
    }
    if (goMatches != 0) {
        contents = contents + goMatches + " Function<br/>";
    }
    if (omimMatches != 0) {
        contents = contents + omimMatches + " Disease Model<br/>";
    }
    if (omimOrthoMatches != 0) {
        contents = contents + omimOrthoMatches + " Disease Ortholog<br/>";
    }
    if (pirsfMatches != 0) {
        contents = contents + pirsfMatches + " Protein Family<br/>";
    }
    if (ipMatches != 0) {
        contents = contents + ipMatches + " Protein Domain<br/>";
    }


    String scoreOverlib = "onMouseOver=\"return overlib('" + contents
      + "', CAPTION, '" + caption
      + "', RIGHT, BELOW, WIDTH, 200, DELAY, 200, TIMEOUT, 3000);\" onMouseOut=\"nd();\" ";

    return scoreOverlib;
  }

  //------------------------------------------------------- Vocab Annotations

  /**
  * Creates the URLs for the various vocabularies annotation section.
  *
  * It examines the vocabResult that it has been passed, determines its type
  * and then outputs a result based on that.
  *
  * @param vocabResult
  * @return String containing the <a href tag for that particular vocabulary.
  *
  */

  public String vocabAnnotation (QS_VocabResult vocabResult)
  {
    String vocab = vocabResult.getVocabulary();
    VocabDisplay vocabDisplay = vocabDisplayCache.getVocab(vocabResult);

    String url = "";
    if (vocab.equals(IndexConstants.AD_TYPE_NAME)) {
        url ="<a href = '" + stConfig.get("WI_URL") + "searches/expression_report.cgi?_Structure_key=" + vocabDisplay.getDbKey() + "&sort=Gene%20Symbol&returnType=assay%20results&substructures=substructures'>" + vocabDisplay.getAnnotDisplay()+ "</a>";
    }
    else if (vocab.equals(IndexConstants.MP_TYPE_NAME)) {
        url = "<a href='"+stConfig.get("JAVAWI_URL")+"WIFetch?page=mpAnnotSummary&id=" + vocabDisplay.getAcc_id() + "'> "+ vocabDisplay.getAnnotDisplay()+"</a>";
    }
    else if (vocab.equals(IndexConstants.GO_TYPE_NAME)) {
        url = "<a href='"+ stConfig.get("WI_URL")+"searches/GOannot_report.cgi?id=" + vocabDisplay.getAcc_id() + "'> "+vocabDisplay.getAnnotDisplay()+"</a>";
    }
    else if (vocab.equals(IndexConstants.OMIM_TYPE_NAME)) {
        url = "<a href='"+ stConfig.get("JAVAWI_URL")+"WIFetch?page=humanDisease&key=" + vocabDisplay.getDbKey()+ "'>"+vocabDisplay.getAnnotDisplay()+"</a>";
    }
    else if (vocab.equals(IndexConstants.PIRSF_TYPE_NAME))
    {
    	url = "<a href='"+ stConfig.get("JAVAWI_URL")+"WIFetch?page=pirsfDetail&key=" + vocabDisplay.getDbKey()+ "'>"+vocabDisplay.getAnnotDisplay()+"</a>";
    }
    else if (vocab.equals(IndexConstants.INTERPRO_TYPE_NAME))
    {
    	url = "<a href ='" + stConfig.get("WI_URL")+"searches/marker_report.cgi?op%3Ago_term=contains&go_term=&interpro="+ vocabDisplay.getAcc_id() +"&clone=&sort=Nomenclature&*limit=500'>"+vocabDisplay.getAnnotDisplay()+"</a>";
    }
    else
    {
    	url = vocab;
    }

    return url;
  }


  //---------------------------------------------------------- Utility Methods

  /**
  * For a given collection, create a comma delimited string
  * @param c the source Collection
  * @return Comma delimited string
  */
  public static String commaDelimit (Collection c)
  {
    String commaDelimString = new String("");

    for (Iterator i = c.iterator(); i.hasNext(); ) {
        String nextValue = (String)i.next();

        if (nextValue != null) {
            if (!commaDelimString.equals("")) {
                    commaDelimString = commaDelimString + ", ";
            }

        commaDelimString = commaDelimString + nextValue;
        }
    }

    return commaDelimString;
  }

  /** convert all "<" and ">" pairs in 's' to be HTML superscript tags.
  * @param s the source String
  * @return String as 's', but with the noted replacement made.  returns
  *    null if 's' is null.
  */
  public static String superscript (String s)
  {
      return superscript (s, "<", ">");
  }

  /** convert all 'start' and 'stop' pair in 's' to be HTML
  *    superscript tags.
  * @param s the source String
  * @param start the String which indicates the position for the HTML
  *    superscript start tag "<SUP>"
  * @param stop the String which indicates the position for the HTML
  *    superscript stop tag "</SUP>"
  * @return String as 's', but with the noted replacement made.  returns
  *    null if 's' is null.  returns 's' if either 'start' or 'stop' is
  *    null.
  */
  public static String superscript (String s, String start, String stop)
  {
    if (s == null){
        return null;            // no source string
    }

    if ((start == null) || (stop == null)){
        return s;               // no start/stop string
    }

    // Otherwise, find the first instance of 'start' and 'stop' in 's'.
    // If either does not appear, then short-circuit and just return 's'
    // as-is.
    int startPos = s.indexOf(start);
    if (startPos == -1)
    {
        return s;
    }

    int stopPos = s.indexOf(stop);
    if (stopPos == -1)
    {
        return s;
    }

    int startLen = start.length();  // how many chars to cut out for start
    int stopLen = stop.length();    // how many chars to cut out for stop
    int sectionStart = 0;       // position of char starting section

    StringBuffer sb = new StringBuffer();

    while ((startPos != -1) && (stopPos != -1))
    {
        sb.append (s.substring(sectionStart, startPos));
        sb.append ("<SUP>");
        sb.append (s.substring(startPos + startLen, stopPos));
        sb.append ("</SUP>");

        sectionStart = stopPos + stopLen;
        startPos = s.indexOf(start, sectionStart);
        stopPos = s.indexOf(stop, sectionStart);
    }
    sb.append (s.substring(sectionStart));

    return sb.toString();
  }

  /**
   * @param
   * @return
   */
  public static String getEncodedUrl (String s)
  {
    String encodedurl = "";
    try{
        encodedurl = URLEncoder.encode(s,"UTF-8");
    }catch(UnsupportedEncodingException uee){
        System.err.println(uee);
    }
    return encodedurl;
  }

  /** convert string representation of int, adding commas
   * @param string value of the integer
   * @return string value of the interger, with commas added
   */
  public static String commaFormatIntStr (String numIn)
  {
    String numOut;

    // get default number formatting
    NumberFormat form;
    form = NumberFormat.getInstance();

    // and apply that format to the input string
    numOut = form.format( new Long(numIn) );

    return numOut;
  }

  /**
   * A helper method that returns a truncated version of a string for display.
   * @param strIn The string to be transformed.
   * @return The truncated version of the string.
   */
  public static String shortenDisplayStr ( String strIn)
  {
    String strOut = new String(strIn);
    if (strOut.length() > 60) {
        strOut = strOut.substring(0,59);
        if (strOut.substring(44,59).contains(" ")) {
            strOut = strOut.substring(0,strOut.lastIndexOf(" ")); //errors if no " "
        }
        strOut = strOut + "...";
    }
    return strOut;
  }

  /** Shorted the displayed score shorted for readability
  * @param s the source of the score
  * @return String no more than 5 characters long
  */
  public static String getShortScore (float f)
  {
    Float newF = new Float(f);
    if (newF.toString().length() < 5) {
        return newF.toString();
    }
    return newF.toString().substring(0,5);
  }

  /** Visual representation of match's score
  * @param match - match to score
  * @return String - HTML for displaying this matches representative score
  */
  public String getMatchStarScore(AbstractMatch match) {

    String star = "<img src='" + stConfig.get("QUICKSEARCH_URL")
      + "darkStarSmall.gif' width='9' height='8'>";

    if ( match.getScore() > 1000 ) {
        return star + star + star;
    }
    else if ( match.getScore() > 100 ){
        return star + star;
    }
    return star;
  }

}


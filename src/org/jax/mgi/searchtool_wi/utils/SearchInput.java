package org.jax.mgi.searchtool_wi.utils;

import java.util.*;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.StopAnalyzer;

import org.jax.mgi.shr.searchtool.AnalyzerUtils;
import org.jax.mgi.shr.searchtool.MGITokenCountAnalyzer;
import org.jax.mgi.shr.searchtool.StemmedMGITokenCountAnalyzer;

/**
* Search Input object, that stores the search string and transforms it in various ways.
*/
public class SearchInput {

  // Set up the logger
  private static Logger log = Logger.getLogger(SearchInput.class.getName());

  private String searchString = new String("");
  private String cacheString = new String("");
  private List<String> zeroHitTokens   = new ArrayList<String>();
  private HashMap formParameters = new HashMap();

  //-------------//
  // Constructors
  //-------------//

  public SearchInput(){}
  public SearchInput(HttpServletRequest req)
  {
    formParameters = new HashMap(req.getParameterMap());

    // Check for specific parameters, and take appropriate action
    if (this.hasFormParameter("query")) {

        String[] searchStringArray = (String[])formParameters.get("query");
        searchString = searchStringArray[0];

        if (searchString == null){
            searchString = "";
        }
    }

    // setup cache string for result-set caching
    cacheString =  searchString.toLowerCase();
  }

  //-----------------------//
  // Form Parameter Access
  //-----------------------//

  /**
  * Checks user inputs for a given form paramete
  * @param String parameter form name
  * @return boolean
  */
  public boolean hasFormParameter (String parm)
  {
    if (formParameters.containsKey(parm) ) {
        return true;
    }
    return false;
  }

  /**
  * Returns the value of a request parameter.
  * @param String parameter form name
  * @returns String parameter value
  * Ensure the form parameterhas only one value. Use getParameterValues for
  * multi-valued parameters.  This mirrors the ServletRequest interface.
  */
  public String getParameter (String parm)
  {
    String returnValue = new String("");
    String[] parmValueArray = (String[])formParameters.get(parm);
    if (parmValueArray != null) {
        returnValue = parmValueArray[0];
    }
    return returnValue;
  }

  /**
  * Returns the value (in String[]) of a request parameter.
  * @param String parameter form name
  * @returns String[] of parameter values
  * This mirrors the ServletRequest interface.
  */
  public String[] getParameterValues (String parm)
  {
    String[] returnValue = new String[0];
    String[] parmValueArray = (String[])formParameters.get(parm);
    if (parmValueArray != null) {
        returnValue = parmValueArray;
    }
    return returnValue;
  }

  /**
  * Returns boolean flag if the search has been submitted with a filter
  * @returns boolean existance of filter
  */
  public boolean hasFilter ()
  {
    boolean hasFilter = false;

    if (hasFormParameter("exclude") || hasFormParameter("incOnly")) {
        hasFilter = true;
    }
    return hasFilter;
  }

  //----------------------------------------//
  // Cache string access for a given request
  //----------------------------------------//
  public String getCacheString() {
      return cacheString;
  }

  //------------------------------------------//
  // Parsed Tokens and IDs from Search String
  //------------------------------------------//

  /** Add a parsed token we've verified will have no hits
  */
  public void addZeroHitToken (String s) {zeroHitTokens.add(s);}

  /** Return zero hit tokens
  */
  public List<String> getZeroHitTokens () {
      return zeroHitTokens;
  }

  //----------------------//
  // Search String Access
  //----------------------//


  /**
  * Returns the user's unmodified search string.
  * @return String representing the search string.
  */
  public String getSearchString() {

    if (searchString == null)
    {
        return "";
    }
      return searchString.trim();
  }


  /** Sets the user's search string.
  * @param String search string
  */
  public void setSearchString(String s) {
      searchString = s;
  }

  /**
  *
  */
  public List getStemmedTokenizedLittleInputString ()
    throws IOException
  {
    StemmedMGITokenCountAnalyzer tca = new StemmedMGITokenCountAnalyzer();
    ArrayList <String> tokens2 = new ArrayList <String>();
    String [] catcher = getTransformedLowerCaseString().split("\"");

    for (int i = 0; i < catcher.length; i++)
    {
        // We are not in quotes, so add the token.
        if (i % 2 == 0) {
              tokens2.addAll(AnalyzerUtils.getTokenList(tca, catcher[i]));
          }

        // We are in items within quotes
        else {

            ArrayList quotesTokens = AnalyzerUtils.getTokenList(tca, catcher[i]);

            if (quotesTokens.size() > 0) {
                String quotesToken = "\"";
                int first = 0;
                for (Iterator iter = quotesTokens.iterator(); iter.hasNext();) {
                    if (first == 0) {
                        quotesToken += (String) iter.next();
                        first = 1;
                    }
                    else {
                        quotesToken += " " +(String) iter.next();
                    }
                }
                quotesToken += "\"";
                tokens2.add(quotesToken);
            }
        }
    }
    return tokens2;
  }

  /**
  *
  */
  public List getTokenizedLittleInputString ()
    throws IOException
  {
    MGITokenCountAnalyzer tca = new MGITokenCountAnalyzer();
    ArrayList <String> tokens = new ArrayList <String>();
    String [] catcher = getTransformedLowerCaseString().split("\"");

    for (int i = 0; i < catcher.length; i++)
    {
        if (i % 2 == 0) {
            String [] subCatcher = catcher[i].split("\\s");
            for (int j = 0; j < subCatcher.length; j++) {
                tokens.add(subCatcher[j]);
            }
        }

        // We are in items within quotes
        else {

            ArrayList quotesTokens = AnalyzerUtils.getTokenList(tca, catcher[i]);

            if (quotesTokens.size() > 0) {
                String quotesToken = "\"";
                int first = 0;
                for (Iterator iter = quotesTokens.iterator(); iter.hasNext();) {
                    if (first == 0) {
                        quotesToken += (String) iter.next();
                        first = 1;
                    }
                    else {
                        quotesToken += " " +(String) iter.next();
                    }
                }
                quotesToken += "\"";

                tokens.add(quotesToken);
            }
        }
    }
    return tokens;
  }

  /**
  *
  */
  public String getTransformedLowerCaseString() {

    // transform everything to lowercase

    String search_string = this.searchString.toLowerCase();

    String[] catcher = search_string.split("\"");
    String[] subCatcher;
    String outString = "";

    for (int i = 0; i < catcher.length; i++) {
        if (i % 2 == 0) {
            // We are in a part that isn't within quotation marks, so now
            // we need to check to see
            // if it contains other special characters. To do that we need
            // to first split it up
            // into its component parts.

            subCatcher = catcher[i].split("\\s");

            for (int j = 0; j < subCatcher.length; j++) {

                String work_string = removeTrailingPunct(subCatcher[j]);

                if (isPrefix(work_string))
                {
                    // Since we've found a prefix string do the following

                    // Replace all punctuation with whitespace

                    work_string = work_string.replaceAll("\\W", " ");

                    // Remove one trailing whitespace, created by removing the prefix search.

                    work_string = work_string.replaceAll("\\s$", "");

                    String pattern = ".*\\s$";

                    /*
                     * Check to see if the pattern is still terminated by a space.
                     * If so, trim it off and keep the * off the end, as its no longer
                     * a valid prefix search.
                     */

                    if (Pattern.matches(pattern, work_string)) {
                        work_string = work_string.replaceAll("\\s*$", "");
                    }

                    /*
                     * Otherwise add it back into the string on the end.
                     */
                    else {
                        work_string += "*";
                    }

                    outString += " " + work_string;

                }
                else
                {
                    if (isID(work_string))
                    {
                        // Since this IS an ID, replace all its punctation with whitespace, and enclose in
                        // quotes.

                        outString += " \"" + work_string.replaceAll("\\W", " ") + "\"";
                    }
                    else
                    {
                        // Since its not an ID we replace all its puncutation with whitespace, and add
                        // it to the stream.

                        outString += " " + work_string.replaceAll("\\W", " ");
                    }
                }

            }
        } else {
            // Since we are in quotes, replace the punctuation with whitespace, and re-enclose in quotes.
            outString += " \"" + catcher[i].replaceAll("\\W", " ") + "\"";
        }
    }

    return trimWhitespace(outString);
  }

  /**
   *
   * @return
   */
  public String getTransformedLowerCaseStringOr()
  {
    String search_string = this.searchString.toLowerCase();
    String[] catcher = search_string.split("\"");
    String[] subCatcher;
    String outString = "";

    for (int i = 0; i < catcher.length; i++) {
        if (i % 2 == 0) {
            // We are in a part that isn't within quotation marks, so now
            // we need to check to see if it contains other special
            // characters. To do that we need to first split it up
            // into its component parts.

            subCatcher = catcher[i].split("\\s");

            for (int j = 0; j < subCatcher.length; j++) {

                String work_string = removeTrailingPunct(subCatcher[j]);

                if (isPrefix(work_string)) {

                    // Replace all punctuation with whitespace
                    work_string = work_string.replaceAll("\\W", " ");

                    // Remove one trailing whitespace, created by removing
                    // the prefix search.
                    work_string = work_string.replaceAll("\\s$", "");

                    String pattern = ".*\\s$";

                    /*
                     * Check to see if the pattern is still terminated by a
                     * space. If so, trim it off and keep the * off the end,
                     * as its no longer a valid prefix search.
                     */

                    if (Pattern.matches(pattern, work_string)) {
                        work_string = work_string.replaceAll("\\s*$", "");
                    }

                    /*
                     * Otherwise add it back into the string on the end.
                     */
                    else {
                        work_string += "*";
                    }

                    outString += " " + work_string;

                } else {
                    if (isID(work_string)) {
                        // Since this IS an ID, replace all its punctation
                        // with whitespace, and enclose in
                        // quotes.

                        outString += " \""
                                + work_string.replaceAll("\\W", " ") + "\"";
                    } else {
                        // Since its not an ID we replace all its
                        // puncutation with whitespace, and add
                        // it to the stream.

                        // Replace the special characters, and then split on
                        // the resulting whitespace.

                        String[] subTokens = work_string.replaceAll("\\W+",
                                " ").split(" ");

                        for (int k = 0; k < subTokens.length; k++) {

                            // If the token length is not 1, add it to the
                            // string for processing in the query
                            // If it is, that means its a single character
                            // which can be safely ignored.

                            if (subTokens[k].length() != 1
                                    && !(subTokens[k].length() == 2 && Pattern
                                            .matches("[0-9][0-9]",
                                                    subTokens[k]))) {
                                outString += " " + subTokens[k];
                            }
                        }
                    }
                }
            }
        } else {
            // Since we are in quotes, replace the punctuation with
            // whitespace, and re-enclose in quotes.

            // outString += " \"" +
            // transformID(catcher[i]).replaceAll("\\W", " ") + "\"";
            outString += " \"" + catcher[i].replaceAll("\\W", " ") + "\"";
        }
    }
    return trimWhitespace(outString);
  }

  /**
   * Return a List of all the large tokens contained in the search string.
   *
   * This is however unique in that it removes double quotes before
   * performing this operation.  As its intended usage is to be able
   * to tell us if individual words doesn't exists in the indexes,
   * with their punctuation intact.
   *
   * @return
   */
  public List <String> getTokenizedInputString() {

    List<String> tokens = new ArrayList<String>();
    String work_string = this.searchString;
    String [] temp_tokens;

    try {
        work_string = searchString.replaceAll("\"", " ").toLowerCase();
        temp_tokens = work_string.split("\\s");
        String noPunctToken;

        for (int i=0; i < temp_tokens.length; i++)
        {
            noPunctToken = removeTrailingPunct(temp_tokens[i]);
            if (noPunctToken != null && ! noPunctToken.equals("")&& ! noPunctToken.equals(" ") && ! isPrefix(noPunctToken)) {
                tokens.add(noPunctToken);
            }

        }
    }
    catch (Exception e) {
        log.error(e);
    }

    return tokens;
  }

  /**
  * Returns a list of large tokens found in the users String.
  */
  public List <String> getEscapedLargeTokenList() {
    String[] catcher = this.searchString.toLowerCase().split("\"");
    String[] subCatcher;
    List <String> tokens = new ArrayList<String>();

    for (int i = 0; i < catcher.length; i++)
    {
        if (i % 2 == 0) {
            subCatcher = catcher[i].split("\\s");
            for (int j = 0; j < subCatcher.length; j++) {
                if (! subCatcher[j].equals("")) {
                    String temp_transformed = removeTrailingPunct(subCatcher[j]);
                    if (temp_transformed != null && ! temp_transformed.equals("")) {
                        tokens.add(trimWhitespace(escapeString(temp_transformed, true)));
                    }
                }
            }
        }
        else {
            tokens.add(trimWhitespace(escapeString(catcher[i],false)));
        }
    }

    return tokens;
  }

  /**
   * Returns the uses input string broken on spaces, w/ trailing
   * punctuation removed, with items contained in quotes
   * remaining as intact tokens.
   */
  public List <String> getLargeTokenList() {
    String[] catcher = this.searchString.toLowerCase().split("\"");
    String[] subCatcher;
    List <String> tokens = new ArrayList<String>();


    for (int i = 0; i < catcher.length; i++)
    {
        if (i % 2 == 0) {
            subCatcher = catcher[i].split("\\s");
            for (int j = 0; j < subCatcher.length; j++) {
                if (! subCatcher[j].equals("")) {
                    String temp_transformed = removeTrailingPunct(subCatcher[j]);
                    if (temp_transformed != null && ! temp_transformed.equals("")) {
                        tokens.add(temp_transformed);
                    }
                }
            }
        }
        else {
            tokens.add(trimWhitespace(catcher[i]));
        }
    }

    return tokens;
  }

  /**
  * Returns Count of Big Tokens in a given search string.
  * @return int - Count of the tokens in the given input string.
  */
   public int getLargeTokenCount() {
       return getLargeTokenList().size();
   }

  /**
   * Returns Count of the little tokens in a given search string.
   * @return int - Count of the tokens in the given input string.
   */
  public int getFilteredSmallTokenCount() {
    return getFilteredSmallTokenList().length;
  }

  /**
   *
   */
  public String [] getFilteredSmallTokenList()
  {
    String temp_string = trimWhitespace(getTransformedLowerCaseStringOr().replaceAll("\\W", " "));
    return temp_string.split("\\s");
  }

  /**
   * Returns Count of the little tokens in a given search string.
   * @return int - Count of the tokens in the given input string.
   */
  public int getSmallTokenCount() {
    return getSmallTokenList().length;
  }

  /**
   *
   */
  public String [] getSmallTokenList()
  {
    String temp_string = trimWhitespace(getTransformedLowerCaseString().replaceAll("\\W", " "));
    return temp_string.split("\\s");
  }


  /** Return a transformed search string.
  * @return String representation of the search string, with all extra spaces
  *   stripped out, and all letters converted into lowercase.
  */
  public String getWholeTermSearchString(){
    return trimWhitespace(this.searchString.replaceAll("\"", "").toLowerCase());
  }

  /**
   *
   */
  public String getEscapedWholeTermSearchString()
  {
    String[] catcher = this.searchString.toLowerCase().split("\"");
    String[] subCatcher;
    //List <String> tokens = new ArrayList<String>();

    String queryString = "";

    for (int i = 0; i < catcher.length; i++)
    {
        if (i % 2 == 0) {
            subCatcher = catcher[i].split("\\s");
            for (int j = 0; j < subCatcher.length; j++) {
                if (! subCatcher[j].equals("")) {
                    String temp_transformed = removeTrailingPunct(subCatcher[j]);
                    if (temp_transformed != null && ! temp_transformed.equals("")) {
                        queryString += " " + escapeString(temp_transformed, true);
                    }
                }
            }
        }
        else {
            queryString += " \"" + trimWhitespace(escapeString(catcher[i],false)) +"\"";
        }
    }
    return trimWhitespace(queryString);
  }

  /**
   *
   */
  public Boolean hasQuotes () {
    String pattern = ".*\".*";
    return Pattern.matches(pattern, searchString);
  }

  /**
   * Does the search string have a prefix search?
   * @return Boolean
   */
  public Boolean hasPrefix ()
  {
    String search_string = this.searchString.toLowerCase();
    String[] catcher = search_string.split("\"");
    String[] subCatcher;

    for (int i = 0; i < catcher.length; i++) {
        if (i % 2 == 0) {
            // We are in a part that isn't within quotation marks, so now
            // we need to check to see if its a prefix search.

            subCatcher = catcher[i].split("\\s");

            for (int j = 0; j < subCatcher.length; j++) {
                String work_string = removeTrailingPunct(subCatcher[j]);
                if (isPrefix(work_string)) {
                    return true;
                }
            }
        }
    }
    return false;
  }

  /**
   * Does the search string have a Boolean keyword in it?
   * @return Boolean
   */
  public Boolean hasBoolean() {
    String pattern = ".* (AND|OR|NOT) .*";
    return Pattern.matches(pattern, searchString);
  }

  /**
   * Does the search string have stopwords?
   * @return Boolean
   */
  public Boolean hasStopWords() {

    // Construct the stop list pattern, and do some regex with it.
    // it MAY make more sense to do this only ONCE.  I need to think
    // about this a bit.

    String [] stopList = StopAnalyzer.ENGLISH_STOP_WORDS;
    String stopPattern =".* (";

    for (int i = 0; i < stopList.length; i++) {
        if (i == 0) {
            stopPattern += stopList[i];
        }
        else {
            stopPattern += "|"+stopList[i];
        }

    }
    stopPattern += ") .*";

    // Are there any stop words? Pad the searchstring with a space in order to catch
    // leading or trailing stopwords.
    return Pattern.matches(stopPattern, " " +searchString.toLowerCase()+ " ");
  }

  /**
   * Check to see if we have any excluded small tokens in our stream.
   *
   * If so we will return true.
   * @return Boolean
   */
  public Boolean hasExcludedWords()
  {
    String [] smallTokens = getSmallTokenList();

    for (int i = 0; i < smallTokens.length; i++) {
        if (Pattern.matches("[a-zA-z]", smallTokens[i])) {
            return true;
        }
        if (Pattern.matches("[0-9]{1,2}", smallTokens[i])) {
            return true;
        }
    }
    return false;
  }

  /**
   * Escape all lucene characters from a given string.
   * @param text
   * @return
   */
  private String trimWhitespace(String text) {
    return text.replaceAll("\\s+", " ").replaceAll("^\\s", "").replaceAll("\\s$", "");
  }

  /**
   * Escape the lucene relevant special characters from the search string.
   *
   * @param text The String to be escaped
   * @param preservePrefix If this is set to true, we preserve prefix
   * searches.  If its false they get escaped just like anything else.
   *
   * @return
   */
  private String escapeString (String text, Boolean preservePrefix)
  {
    String [] regex_patterns = {"\\\\", "\\!", "\\(", "\\)", "\\[", "\\]",
            "\\{", "\\}", "\\+", "\\-", "\\&\\&", "\\|\\|", "\\*", "\\?",
            "\\:", "\\~"};

    String [] replacement_patterns = {"\\\\\\\\", "\\\\!", "\\\\(", "\\\\)",
            "\\\\[", "\\\\]", "\\\\{", "\\\\}", "\\\\+", "\\\\-",
            "\\\\&&", "\\\\||", "\\\\*", "\\\\?", "\\\\:", "\\\\~"};

    // Escape all Lucene sensitive special characters.
    for (int i=0; i< regex_patterns.length; i++) {
        text = text.replaceAll(regex_patterns[i], replacement_patterns[i]);
    }

    // Make sure prefix searches are intact, if they are intended to be as such.
    if (preservePrefix) {
        text = text.replaceAll("\\\\\\*$", "*");
    }

    return text;
  }

  /**
   * Is the string an recognized valid accession id?
   * @param token The string to check
   * @return Boolean
   */
  private Boolean isID (String token)
  {
    // The generic ID pattern, this should catch the bulk of what is in
    // the acc_accession table, add new generic patterns here.

    String common_id_pattern = "(mp|go|mgi|j|ma|mgc|hgnc):[0-9]+$";

    // Special cases - Put any new patterns after this comment

    String dots_id_pattern = "dt\\.[0-9]+$";
    String mgd_mrk_id_pattern = "mgd-mrk-[0-9]+$";
    String ec_number_id_pattern = "[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+";

    if (Pattern.matches(common_id_pattern, token)) {
        return true;
    }

    if (Pattern.matches(dots_id_pattern, token)) {
        return true;
    }

    if (Pattern.matches(mgd_mrk_id_pattern, token)) {
        return true;
    }

    if (Pattern.matches(ec_number_id_pattern, token)) {
        return true;
    }

    return false;
  }

  /**
   * Remove any number of trailing punctuation from the search string.
   * @param token The String to remove punctuation from.
   * @return The transformed string.
   */
  private String removeTrailingPunct(String token) {
    return token.replaceAll("[.,:#;]+$", "");
  }

  /**
   * Is this string a prefix search?
   * @param token The String to check.
   * @return Boolean
   */
  private Boolean isPrefix (String token)
  {
    String regex_pattern =".*\\*$";
    return Pattern.matches(regex_pattern, token);
  }


}

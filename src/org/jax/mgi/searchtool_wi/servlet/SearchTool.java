package org.jax.mgi.searchtool_wi.servlet;

// Standard Java Classes
import java.util.*;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Logging
import org.apache.log4j.Logger;

// Search Tool Classes
import org.jax.mgi.searchtool_wi.lookup.MarkerDisplayCache;
import org.jax.mgi.searchtool_wi.lookup.MarkerVocabSearchCache;
import org.jax.mgi.searchtool_wi.lookup.OtherDisplayLookup;
import org.jax.mgi.searchtool_wi.lookup.QS_MarkerResultCache;
import org.jax.mgi.searchtool_wi.lookup.QS_VocabResultCache;
import org.jax.mgi.searchtool_wi.lookup.TokenExistanceCache;
import org.jax.mgi.searchtool_wi.lookup.VocabDisplayCache;
import org.jax.mgi.searchtool_wi.results.QS_MarkerResultContainer;
import org.jax.mgi.searchtool_wi.results.QS_OtherResultContainer;
import org.jax.mgi.searchtool_wi.results.QS_VocabResultContainer;
import org.jax.mgi.searchtool_wi.searches.QS_MarkerSearch;
import org.jax.mgi.searchtool_wi.searches.QS_OtherSearch;
import org.jax.mgi.searchtool_wi.searches.QS_VocabSearch;
import org.jax.mgi.searchtool_wi.exception.QuickSearchException;
import org.jax.mgi.searchtool_wi.utils.SearchInput;
import org.jax.mgi.searchtool_wi.utils.WebTemplate;

// MGI Shared Classes
import org.jax.mgi.shr.config.WebAppCfg;

/**
* A Search is a servlet who's purpose is to recieve HttpServletRequest
* objects, determine which page is being requested, retreive the data
* for the requested page, and forward to the view layer.
*
* Please see sw:Searchtool_wi software documentation in the MGI wiki for
* more information
*/
public class SearchTool extends HttpServlet {

  // objects filled at instantiation; init()
  private static WebAppCfg stConfig;
  private static WebTemplate webTemplate;
  private static MarkerDisplayCache markerDisplayCache;
  private static VocabDisplayCache vocabDisplayCache;
  private static OtherDisplayLookup otherDisplayLookup;
  private static MarkerVocabSearchCache markerVocabSearchCache;
  private static Logger logger;
  private static ServletContext servletContext;

  private static QS_MarkerResultCache markerResultCache;
  private static QS_VocabResultCache vocabResultCache;
  private static TokenExistanceCache tokenExistanceCache;


  //---------------//
  // public methods
  //---------------//

  /**
  * This is a "hook" in parent class, called <i>once</i> at initialization,
  * after JBoss instantiates the servlet.  This is used to setup our
  * in-memory caches, template, configuration, logger etc...
  */
  public void init () {

    // Servlet Context - access to parameters in web.xml
    servletContext = getServletContext();

    // Setup a Configuration object
    String glocalConfigLoc = servletContext.getInitParameter("globalConfig");
    try {
      stConfig = (WebAppCfg)WebAppCfg.load(glocalConfigLoc, false);

      // move some web.xml parameters to our native mgi config object
      stConfig.set("INDEX_DIR", servletContext.getInitParameter("indexDir"));
    }
    catch (Exception e) { e.printStackTrace(); }

    // log4j logging
    logger = Logger.getLogger(Search.class.getName());

    // Setup display caches and web template for the JSPs to use
    markerDisplayCache = MarkerDisplayCache.getMarkerDisplayCache(stConfig);
    vocabDisplayCache = VocabDisplayCache.getVocabDisplayCache(stConfig);
    otherDisplayLookup = OtherDisplayLookup.getOtherDisplayLookup(stConfig);
    webTemplate = new WebTemplate(getServletContext().getInitParameter("templateLoc"));
    webTemplate.addCss("searchTool.css");
    webTemplate.addJs("searchTool.js");

    // Setup any servlet level caches the searches or servlet may need
    markerVocabSearchCache = MarkerVocabSearchCache.getMarkerVocabSearchCache(stConfig);
    tokenExistanceCache = TokenExistanceCache.getTokenExistanceCache(stConfig);

    // Caching of result sets returned from Search execution
    markerResultCache
      = new QS_MarkerResultCache(servletContext.getInitParameter("resultCacheSize"));
    vocabResultCache
      = new QS_VocabResultCache(servletContext.getInitParameter("resultCacheSize"));
  }


  /**
  * handle a submission which came via the GET method over HTTP.
  * @param req information about the request (parameters, client info, etc.)
  * @param res information for the response (the writer to be used to reach
  *    the user's browser, etc.)
  */
  public void doGet (HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException
  {
    this.doRequest (req, res);
    return;
  }


  /** handle a submission which came via the POST method over HTTP.
  * @param req information about the request (parameters, client info, etc.)
  * @param res information for the response (the writer to be used to reach
  *    the user's browser, etc.)
  */
  public void doPost (HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
  {
    this.doRequest (req, res);
    return;
  }

  //-------------------------------------------------------- protected methods

  /** handle a submission/request; chained from doPost() or doGet()
  * @param req information about the request (parameters, client info, etc.)
  * @param res information for the response (the writer to be used to reach
  *    the user's browser, etc.)
  */
  protected void doRequest(HttpServletRequest request, HttpServletResponse response)
  {
    try {
        // Setup search input object for this request, and log the request
        SearchInput searchInput = new SearchInput(request);
        searchInput.setSearchString(request.getParameter("query"));
        logger.info("*User's Input String ->" + request.getParameter("query"));

        // pass the input string downhill in the request object
        request.setAttribute("query", request.getParameter("query"));

        // add references to in-memory objects the display layer may need
        request.setAttribute("MarkerDisplayCache", markerDisplayCache);
        request.setAttribute("VocabDisplayCache", vocabDisplayCache);
        request.setAttribute("OtherDisplayLookup", otherDisplayLookup);
        request.setAttribute("WebTemplate", webTemplate);
        request.setAttribute("Configuration", stConfig);
        request.setAttribute("SearchInput", searchInput);

        // search for tokens that won't match anything, to later notify the user
        parseSearchString(searchInput);

        // validate the input string; if invalid, will forward to error page
        validateUserInput(request, response, searchInput);

        // Determine which page to display.  Private 'sendTo' methods handle
        // forwarding to our display layer
        if (request.getParameter("page") == null) {
            sendToSummary(request, response, searchInput);
        }
        else if ( request.getParameter("page").equals("summary") ) {
            sendToSummary(request, response, searchInput);
        }
        else if ( request.getParameter("page").equals("marker") ) {
            sendToMarker(request, response, searchInput);
        }
        else if ( request.getParameter("page").equals("markerDetails") ) {
            sendToMarkerDetails(request, response, searchInput);
        }
        else if ( request.getParameter("page").equals("vocab") ) {
            sendToVocab(request, response, searchInput);
        }
        else if ( request.getParameter("page").equals("vocabDetails") ) {
            sendToVocabDetails(request, response, searchInput);
        }
        else if ( request.getParameter("page").equals("error") ) {
            QuickSearchException qse = new QuickSearchException("Requested Error Page");
            sendToError(request, response, qse);
        }
        else { // if page wasn't NULL, but had an unrecognized value
            sendToSummary(request, response, searchInput);
        }
    }
    catch (Exception e) {
        QuickSearchException qse = new QuickSearchException(e);
        sendToError(request, response, qse);
    }
  }

  //---------------------------------------------------------- private methods

  //-----------------//
  // Page Generation
  //-----------------//

  /**
  * MAIN SUMMARY - Gather the data, and forward to the view layer
  */
  private void sendToSummary(HttpServletRequest request,
    HttpServletResponse response, SearchInput searchInput)
    throws Exception
  {
    // Search for markers results
    QS_MarkerResultContainer markerResultContainer = getMarkerResults(searchInput);
    request.setAttribute("MarkerResultContainer", markerResultContainer);

    // Search for vocab results
    QS_VocabResultContainer vocabResultContainer = getVocabResults(searchInput);
    request.setAttribute("VocabResultContainer", vocabResultContainer);

    // Search for 'other' results
    QS_OtherSearch otherSearch = new QS_OtherSearch(stConfig);
    QS_OtherResultContainer otherResultContainer = new QS_OtherResultContainer(otherSearch.search(searchInput));
    request.setAttribute("OtherResultContainer", otherResultContainer);

    // prep request and forward to display
    response.setContentType("text/html");
    RequestDispatcher view = request.getRequestDispatcher("result.jsp");
    view.forward(request, response);
  }

  /**
  * MARKER SUMMARY - Gather the data, and forward to the view layer
  */
  private void sendToMarker(HttpServletRequest request,
    HttpServletResponse response, SearchInput searchInput)
    throws Exception
  {
    // Search markers
    QS_MarkerResultContainer markerResultContainer = getMarkerResults(searchInput);
    request.setAttribute("MarkerResultContainer", markerResultContainer);

    // prep request and forward to display
    response.setContentType("text/html");
    RequestDispatcher view = request.getRequestDispatcher("mrk_result.jsp");
    view.forward(request, response);
  }

  /**
  * MARKER DETAIL - Gather the data, and forward to the view layer
  */
  private void sendToMarkerDetails(HttpServletRequest request,
    HttpServletResponse response, SearchInput searchInput)
    throws Exception
  {
    // Search markers
    QS_MarkerResultContainer markerResultContainer = getMarkerResults(searchInput);
    request.setAttribute("MarkerResultContainer", markerResultContainer);

    // prep request and forward to display
    response.setContentType("text/html");
    RequestDispatcher view = request.getRequestDispatcher("mrk_details.jsp");
    view.forward(request, response);
  }

  /**
  * VOCAB SUMMARY - Gather the data, and forward to the view layer
  */
  private void sendToVocab(HttpServletRequest request,
    HttpServletResponse response, SearchInput searchInput)
    throws Exception
  {
    // Search vocab
    QS_VocabResultContainer vocabResultContainer = getVocabResults(searchInput);
    request.setAttribute("VocabResultContainer", vocabResultContainer);

    // prep request and forward to display
    response.setContentType("text/html");
    RequestDispatcher view = request.getRequestDispatcher("voc_result.jsp");
    view.forward(request, response);
  }

  /**
  * VOCAB DETAILS - Gather the data, and forward to the view layer
  */
  private void sendToVocabDetails(HttpServletRequest request,
    HttpServletResponse response, SearchInput searchInput)
    throws Exception
  {
    // Search vocab
    QS_VocabResultContainer vocabResultContainer = getVocabResults(searchInput);
    request.setAttribute("VocabResultContainer", vocabResultContainer);

    // prep request and forward to display
    response.setContentType("text/html");
    RequestDispatcher view = request.getRequestDispatcher("voc_details.jsp");
    view.forward(request, response);
  }

  /**
  * ERROR PAGE
  */
  private void sendToError(HttpServletRequest request,
    HttpServletResponse response, QuickSearchException qse)
  {
    try {
        // prep request and forward to display
        response.setContentType("text/html");
        request.setAttribute("QuickSearchException", qse);
        RequestDispatcher view = request.getRequestDispatcher("error.jsp");
        view.forward(request, response);
    }
    catch (Exception e) {e.printStackTrace();}
  }


  //------------------//
  // Result Retrieval
  //------------------//

  /**
  * encapsulation of marker result container retrieval - if the result
  * container is cached, retrieve it; otherwise generate a new set of
  * results, and cache it.
  *
  * The caller of this method doesn't need to know whether the result set
  * was pulled from cache, or dynamically generated
  */
  private QS_MarkerResultContainer getMarkerResults(SearchInput searchInput)
    throws Exception
  {
    // first, try to retrieve this result set from cache
    QS_MarkerResultContainer markerResultContainer
      = markerResultCache.getMarkerContainer( searchInput.getCacheString() );

    // if a filter has been submitted, bypass cache
    if (searchInput.hasFilter()) {
        markerResultContainer = null;
    }

    // if not found in cache (or we cleared it), generarate the result set
    if (markerResultContainer == null) {
        QS_MarkerSearch markerSearch = new QS_MarkerSearch(stConfig);
        markerResultContainer = new QS_MarkerResultContainer(markerSearch.search(searchInput));
    }

    // and add to servlet-level cache if a filter was not submitted
    if (!searchInput.hasFilter()) {
        markerResultCache.addMarkerContainer(searchInput.getCacheString(), markerResultContainer);
    }

    return markerResultContainer;
  }


  /**
  * encapsulation of vocab result container retrieval - if the result
  * container is cached, retrieve it; otherwise generate a new set of
  * results, and cache it.
  *
  * The caller of this method doesn't need to know whether the result set
  * was pulled from cache, or dynamically generated
  */
  private QS_VocabResultContainer getVocabResults(SearchInput searchInput)
    throws Exception
  {
    // first, try to retrieve this result set from cache
    QS_VocabResultContainer vocabResultContainer
      = vocabResultCache.getVocabContainer( searchInput.getCacheString() );

    // if not found in cache, generarate the result set and add to cache
    if (vocabResultContainer == null) {
        QS_VocabSearch vocabSearch = new QS_VocabSearch(stConfig);
        vocabResultContainer = new QS_VocabResultContainer(vocabSearch.search(searchInput));
        vocabResultCache.addVocabContainer(searchInput.getCacheString(), vocabResultContainer);
    }
    return vocabResultContainer;
  }

  //-----------------//
  // Token Existance
  //-----------------//

  /**
  * Check each token to see if we'll not get a hit for it
  */
  private void parseSearchString(SearchInput searchInput)
    throws Exception
  {
    List tokens = searchInput.getTokenizedInputString();

    // for each token, check existance cache
    for (Iterator iter = tokens.iterator(); iter.hasNext();)
    {
        String token = (String) iter.next();
        if (!tokenExistanceCache.hasToken(token)) {
            logger.debug("#Didn't have this token -> " + token);
            searchInput.addZeroHitToken(token);
        }
    }

    return;
  }

  //-----------------------//
  // User Input Validation
  //-----------------------//

  /**
  * Validation of user's input
  */
  private void validateUserInput(HttpServletRequest request,
    HttpServletResponse response, SearchInput searchInput)
    throws IOException, ServletException
  {
    QuickSearchException qse = new QuickSearchException();
    String inputString = searchInput.getSearchString();
    String largeToken;
    int trimmedLength = inputString.trim().length();
    int tokenCount = inputString.split("\\s").length;
    int quoteCount = 0;
    boolean containsLoneAsterisk = false;

    // count quotes; using speed of primatives
    if ( inputString.contains("\"") ) {

        char[] buffer = inputString.toCharArray();
        for (int count=0; count < buffer.length; count++ ) {
            if (buffer[count] == '\"') {
                quoteCount++;
            }
        }
    }

    // check for a lone asterisk
    for (Iterator iter = searchInput.getLargeTokenList().iterator(); iter.hasNext();)
    {
        largeToken = (String) iter.next();

        if ( largeToken.equals("*") ){
            containsLoneAsterisk = true;
        }
    }

    // if we have any error conditions, forward to an error page
    if (trimmedLength == 0) {
      qse.setErrorDisplay("Your search did not contain anything.  Please enter up to 32 words, IDs, or other text items and try again.");
      sendToError(request, response, qse);
    }
    else if (tokenCount > 32){
      qse.setErrorDisplay("Quick Search is limited to 32 words, IDs, or other search items.  Please edit your search to contain 32 items or fewer.");
      sendToError(request, response, qse);
    }
    else if ((quoteCount % 2) == 1){
      qse.setErrorDisplay("Your search includes an odd number of quotation marks.  Please edit your search to use quotation marks only in pairs.");
      sendToError(request, response, qse);
    }
    else if (containsLoneAsterisk){
      qse.setErrorDisplay("Your search contained a lone asterisk (*).  An asterisk must be appended to other characters for use as a wildcard.  Please modify your text and try again.");
      sendToError(request, response, qse);
    }

    return;
  }

}


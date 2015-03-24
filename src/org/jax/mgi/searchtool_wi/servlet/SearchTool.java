package org.jax.mgi.searchtool_wi.servlet;


import java.util.*;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.jax.mgi.searchtool_wi.lookup.GenomeFeatureDisplayCache;
import org.jax.mgi.searchtool_wi.lookup.VocabInfoCache;
import org.jax.mgi.searchtool_wi.lookup.OtherDisplayLookup;
import org.jax.mgi.searchtool_wi.lookup.GenomeFeatureResultCache;
import org.jax.mgi.searchtool_wi.lookup.VocabResultCache;
import org.jax.mgi.searchtool_wi.lookup.TokenExistanceCache;
import org.jax.mgi.searchtool_wi.lookup.VocabDisplayCache;
import org.jax.mgi.searchtool_wi.results.GenomeFeatureResultContainer;
import org.jax.mgi.searchtool_wi.results.OtherResultContainer;
import org.jax.mgi.searchtool_wi.results.VocabResultContainer;
import org.jax.mgi.searchtool_wi.searches.GenomeFeatureSearch;
import org.jax.mgi.searchtool_wi.searches.OtherSearch;
import org.jax.mgi.searchtool_wi.searches.VocabSearch;
import org.jax.mgi.searchtool_wi.exception.QuickSearchException;
import org.jax.mgi.searchtool_wi.utils.SearchInput;
import org.jax.mgi.searchtool_wi.utils.WebTemplate;

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
	private static GenomeFeatureDisplayCache gfDisplayCache;
	private static VocabDisplayCache vocabDisplayCache;
	private static OtherDisplayLookup otherDisplayLookup;
	private static VocabInfoCache vocabInfoCache;
	private static Logger logger;
	private static ServletContext servletContext;

	private static GenomeFeatureResultCache genomeFeatureResultCache;
	private static VocabResultCache vocabResultCache;
	private static TokenExistanceCache tokenExistanceCache;

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
			stConfig.set("MAX_MATCHES", servletContext.getInitParameter("maxMatchCount"));
		}
		catch (Exception e) { e.printStackTrace(); }

		// log4j logging
		logger = Logger.getLogger(SearchTool.class.getName());

		// Setup display caches and web template for the JSPs to use
		gfDisplayCache = GenomeFeatureDisplayCache.getGenomeFeatureDisplayCache(stConfig);
		vocabDisplayCache = VocabDisplayCache.getVocabDisplayCache(stConfig);
		otherDisplayLookup = OtherDisplayLookup.getOtherDisplayLookup(stConfig);
		webTemplate = new WebTemplate(getServletContext().getInitParameter("templateLoc"));
		webTemplate.addCss("searchTool.css");
		webTemplate.addJs("searchTool.js");

		// Setup any servlet level caches the searches or servlet may need
		vocabInfoCache = VocabInfoCache.getVocabInfoCache(stConfig);
		tokenExistanceCache = TokenExistanceCache.getTokenExistanceCache(stConfig);

		// Caching of result sets returned from Search execution
		genomeFeatureResultCache
		= new GenomeFeatureResultCache(servletContext.getInitParameter("resultCacheSize"));
		vocabResultCache
		= new VocabResultCache(servletContext.getInitParameter("resultCacheSize"));
	}


	/**
	 * handle a submission which came via the GET method over HTTP.
	 * @param req information about the request (parameters, client info, etc.)
	 * @param res information for the response (the writer to be used to reach
	 *    the user's browser, etc.)
	 */
	public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doRequest (req, res);
	}


	/** handle a submission which came via the POST method over HTTP.
	 * @param req information about the request (parameters, client info, etc.)
	 * @param res information for the response (the writer to be used to reach
	 *    the user's browser, etc.)
	 */
	public void doPost (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doRequest (req, res);
	}

	//-------------------------------------------------------- protected methods

	/** handle a submission/request; chained from doPost() or doGet()
	 * @param req information about the request (parameters, client info, etc.)
	 * @param res information for the response (the writer to be used to reach
	 *    the user's browser, etc.)
	 */
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			// Setup search input object for this request, and log the request
			SearchInput searchInput = new SearchInput(request);
			searchInput.setSearchString(request.getParameter("query"));
			logger.info("*User's Input String ->" + request.getParameter("query"));

			// pass the input string downhill in the request object
			request.setAttribute("query", request.getParameter("query"));

			// add references to in-memory objects the display layer may need
			request.setAttribute("GenomeFeatureDisplayCache", gfDisplayCache);
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
				sendToMainSummary(request, response, searchInput);
			}
			else if ( request.getParameter("page").equals("summary") ) {
				sendToMainSummary(request, response, searchInput);
			}
			else if ( request.getParameter("page").equals("featureList") ) {
				sendToGenomeFeatureList(request, response, searchInput);
			}
			else if ( request.getParameter("page").equals("featureDetails") ) {
				sendToGenomeFeatureListDetails(request, response, searchInput);
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
				sendToMainSummary(request, response, searchInput);
			}
		}
		catch (Exception e) {
			QuickSearchException qse = new QuickSearchException(e);
			sendToError(request, response, qse);
		}
	}

	/**
	 * MAIN SUMMARY - Gather the data, and forward to the view layer
	 */
	private void sendToMainSummary(HttpServletRequest request, HttpServletResponse response, SearchInput searchInput) throws Exception {
		// Search for genome feature results
		GenomeFeatureResultContainer gfResultContainer = getGenomeFeatureResults(searchInput);
		request.setAttribute("GenomeFeatureResultContainer", gfResultContainer);

		// Search for vocab results
		VocabResultContainer vocabResultContainer = getVocabResults(searchInput);
		request.setAttribute("VocabResultContainer", vocabResultContainer);

		// Search for 'other' results
		OtherSearch otherSearch = new OtherSearch(stConfig);
		OtherResultContainer otherResultContainer = new OtherResultContainer(otherSearch.search(searchInput));
		request.setAttribute("OtherResultContainer", otherResultContainer);

		// prep request and forward to display
		response.setContentType("text/html");
		RequestDispatcher view = request.getRequestDispatcher("result.jsp");
		view.forward(request, response);
	}

	/**
	 * GENOME FEATURE SUMMARY - Gather the data, and forward to the view layer
	 */
	private void sendToGenomeFeatureList(HttpServletRequest request, HttpServletResponse response, SearchInput searchInput) throws Exception {
		// Execute Search
		GenomeFeatureResultContainer gfResultContainer = getGenomeFeatureResults(searchInput);
		request.setAttribute("GenomeFeatureResultContainer", gfResultContainer);

		// prep request and forward to display
		response.setContentType("text/html");
		RequestDispatcher view = request.getRequestDispatcher("gf_result.jsp");
		view.forward(request, response);
	}

	/**
	 * GENOME FEATURE DETAIL - Gather the data, and forward to the view layer
	 */
	private void sendToGenomeFeatureListDetails(HttpServletRequest request, HttpServletResponse response, SearchInput searchInput) throws Exception {
		// Execute Search
		GenomeFeatureResultContainer gfResultContainer = getGenomeFeatureResults(searchInput);
		request.setAttribute("GenomeFeatureResultContainer", gfResultContainer);

		// prep request and forward to display
		response.setContentType("text/html");
		RequestDispatcher view = request.getRequestDispatcher("gf_details.jsp");
		view.forward(request, response);
	}

	/**
	 * VOCAB SUMMARY - Gather the data, and forward to the view layer
	 */
	private void sendToVocab(HttpServletRequest request, HttpServletResponse response, SearchInput searchInput) throws Exception {
		// Search vocab
		VocabResultContainer vocabResultContainer = getVocabResults(searchInput);
		request.setAttribute("VocabResultContainer", vocabResultContainer);

		// prep request and forward to display
		response.setContentType("text/html");
		RequestDispatcher view = request.getRequestDispatcher("voc_result.jsp");
		view.forward(request, response);
	}

	/**
	 * VOCAB DETAILS - Gather the data, and forward to the view layer
	 */
	private void sendToVocabDetails(HttpServletRequest request, HttpServletResponse response, SearchInput searchInput) throws Exception {
		// Search vocab
		VocabResultContainer vocabResultContainer = getVocabResults(searchInput);
		request.setAttribute("VocabResultContainer", vocabResultContainer);

		// prep request and forward to display
		response.setContentType("text/html");
		RequestDispatcher view = request.getRequestDispatcher("voc_details.jsp");
		view.forward(request, response);
	}

	/**
	 * ERROR PAGE
	 */
	private void sendToError(HttpServletRequest request, HttpServletResponse response, QuickSearchException qse) {
		try {
			// prep request and forward to display
			response.setContentType("text/html");
			request.setAttribute("QuickSearchException", qse);
			RequestDispatcher view = request.getRequestDispatcher("error.jsp");
			view.forward(request, response);
		}
		catch (Exception e) {e.printStackTrace();}
	}

	/**
	 * encapsulation of genome feature result container retrieval - if the result
	 * container is cached, retrieve it; otherwise generate a new set of
	 * results, and cache it.
	 *
	 * The caller of this method doesn't need to know whether the result set
	 * was pulled from cache, or dynamically generated
	 */
	private GenomeFeatureResultContainer getGenomeFeatureResults(SearchInput searchInput) throws Exception {
		// first, try to retrieve this result set from cache
		GenomeFeatureResultContainer gfResultContainer = genomeFeatureResultCache.getMarkerContainer( searchInput.getCacheString() );

		// if a filter has been submitted, bypass cache
		if (searchInput.hasFilter()) {
			gfResultContainer = null;
		}

		// if not found in cache (or we cleared it), generarate the result set
		if (gfResultContainer == null) {
			GenomeFeatureSearch gfSearch = new GenomeFeatureSearch(stConfig);
			gfResultContainer = new GenomeFeatureResultContainer(gfSearch.search(searchInput));
		}

		// and add to servlet-level cache if a filter was not submitted
		if (!searchInput.hasFilter()) {
			genomeFeatureResultCache.addMarkerContainer(searchInput.getCacheString(), gfResultContainer);
		}

		return gfResultContainer;
	}


	/**
	 * encapsulation of vocab result container retrieval - if the result
	 * container is cached, retrieve it; otherwise generate a new set of
	 * results, and cache it.
	 *
	 * The caller of this method doesn't need to know whether the result set
	 * was pulled from cache, or dynamically generated
	 */
	private VocabResultContainer getVocabResults(SearchInput searchInput) throws Exception {
		// first, try to retrieve this result set from cache
		VocabResultContainer vocabResultContainer = vocabResultCache.getVocabContainer( searchInput.getCacheString() );

		// if not found in cache, generarate the result set and add to cache
		if (vocabResultContainer == null) {
			VocabSearch vocabSearch = new VocabSearch(stConfig);
			vocabResultContainer = new VocabResultContainer(vocabSearch.search(searchInput));
			vocabResultCache.addVocabContainer(searchInput.getCacheString(), vocabResultContainer);
		}
		return vocabResultContainer;
	}

	/**
	 * Check each token to see if we'll not get a hit for it
	 */
	private void parseSearchString(SearchInput searchInput) throws Exception {
		List tokens = searchInput.getTokenizedInputString();

		// for each token, check existance cache
		for (Iterator iter = tokens.iterator(); iter.hasNext();) {
			String token = (String) iter.next();
			if (!tokenExistanceCache.hasToken(token)) {
				logger.debug("#Didn't have this token -> " + token);
				searchInput.addZeroHitToken(token);
			}
		}

	}

	/**
	 * Validation of user's input
	 */
	private void validateUserInput(HttpServletRequest request, HttpServletResponse response, SearchInput searchInput) throws IOException, ServletException {
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
		for (Iterator iter = searchInput.getLargeTokenList().iterator(); iter.hasNext();) {
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

	}

}


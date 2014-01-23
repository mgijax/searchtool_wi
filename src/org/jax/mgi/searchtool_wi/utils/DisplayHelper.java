package org.jax.mgi.searchtool_wi.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.jax.mgi.searchtool_wi.dataAccess.IndexReaderContainer;
import org.jax.mgi.searchtool_wi.lookup.GenomeFeatureDisplay;
import org.jax.mgi.searchtool_wi.lookup.GenomeFeatureDisplayCache;
import org.jax.mgi.searchtool_wi.lookup.VocabDisplay;
import org.jax.mgi.searchtool_wi.lookup.VocabDisplayCache;
import org.jax.mgi.searchtool_wi.matches.AbstractMatch;
import org.jax.mgi.searchtool_wi.results.GenomeFeatureResult;
import org.jax.mgi.searchtool_wi.results.VocabResult;
import org.jax.mgi.shr.config.Configuration;
import org.jax.mgi.shr.searchtool.IndexConstants;
import org.jax.mgi.shr.searchtool.MGIAnalyzer;
import org.jax.mgi.shr.searchtool.StemmedMGIAnalyzer;

/**
 * The DisplayHelper object provides static utility methods for display
 * 
 * @module DisplayHelper
 */
public class DisplayHelper
{

	// -------------------------------//
	// Class Fields & Initialization
	// -------------------------------//

	// Set up the logger
	private static Logger						logger					= Logger.getLogger(DisplayHelper.class.getName());

	private Configuration						stConfig;
	private static GenomeFeatureDisplayCache	gfDisplayCache;
	private static VocabDisplayCache			vocabDisplayCache;
	private static IndexReaderContainer			irc;

	// Analyzers
	static Analyzer								standard_analyzer		= new StandardAnalyzer();
	static Analyzer								keyword_analyzer		= new KeywordAnalyzer();
	static Analyzer								snowball_analyzer		= new SnowballAnalyzer("English");
	static Analyzer								mgi_analyzer			= new MGIAnalyzer();
	static Analyzer								stemmed_mgi_analyzer	= new StemmedMGIAnalyzer();

	// --------------//
	// Constructors
	// --------------//

	/**
	 * Hiding the default constructor; must be passed config for usage of
	 * non-static methods. The static method calls in this object are intended
	 * to be used without class instantiation.
	 */
	private DisplayHelper() {
	}

	public DisplayHelper(Configuration c,
			GenomeFeatureDisplayCache mdc,
			VocabDisplayCache vdc)
	{
		stConfig = c;
		irc = IndexReaderContainer.getIndexReaderContainer(stConfig);

		// Setup display caches
		gfDisplayCache = mdc;
		vocabDisplayCache = vdc;
	}

	// ---------------------//
	// Public Class Methods
	// ---------------------//

	// ---------------------------------------------------------- OverLib
	// Pop-ups

	/**
	 * Create the standard "Using the Quick Search Tool" anchor
	 */

	public String getUserDocAnchorString() {
		return "<div class=\\\'detailRowType\\\'>See <a href=\\\'"
				+ stConfig.get("USERHELP_URL")
				+ "QUICK_SEARCH_help.shtml\\\'>Using the Quick Search Tool</a> "
				+ "for more information and examples.</div>";
	}

	/**
	 * Create the tool tip popup for the score column in the marker bucket.
	 * 
	 * @return String containing a formatted overlib call.
	 */

	public String getScoreMouseOverMarker()
	{
		String star = "<img src=" + stConfig.get("QUICKSEARCH_URL")
				+ "darkStarSmall.gif>";

		String scoreMouseOver = "return overlib('Score is based on similarity "
				+ "between your text and IDs, nomenclature, and vocabulary term "
				+ "text in MGI\\\'s database.<br/>"
				+ star + star + star + star
				+ " - exact match between your search and matched text<br/>&nbsp;"
				+ "&nbsp;" + star + star + star
				+ " - all terms in your search appear in the matched text<br/>&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;" + star + star
				+ " - one term in your search matched a genome feature symbol "
				+ "or accession ID exactly<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ star + " - not all terms in your search appear in matched text'"
				+ ", STICKY, CAPTION, 'Score', HAUTO, BELOW, WIDTH, 375, DELAY, "
				+ "600, CLOSECLICK, CLOSETEXT, 'Close X')";

		return scoreMouseOver;
	}

	/**
	 * Create the tool tip popup for the score column in the vocab bucket.
	 * 
	 * @return String containing a formatted overlib call.
	 */

	public String getScoreMouseOverVocab()
	{
		String star = "<img src=" + stConfig.get("QUICKSEARCH_URL")
				+ "darkStarSmall.gif>";

		String scoreMouseOver = "return overlib('Score is based on similarity "
				+ "between your text and IDs, nomenclature, and vocabulary term "
				+ "text in MGI\\\'s database.<br/>"
				+ star + star + star + star
				+ " - exact match between your search and matched text<br/>&nbsp;"
				+ "&nbsp;" + star + star + star
				+ " - all words in your search appear in matched text<br/>"
				+ "&nbsp;&nbsp;&nbsp;" + star + star
				+ " - one term in your search matched a vocabulary term accession ID exactly<br/>&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + star
				+ " - not all words in your search appear in matched text'"
				+ ", STICKY, CAPTION, 'Score', HAUTO, BELOW, WIDTH, 375, DELAY, "
				+ "600, CLOSECLICK, CLOSETEXT, 'Close X')";

		return scoreMouseOver;
	}

	/**
	 * Create the tool tip popup for the marker bucket.
	 * 
	 * @return String containing a formatted overlib call.
	 */

	public String getHelpPopupMarkerBucket()
	{
		String helpPopup = "return overlib('<div class=detailRowType>"
				+ "This list includes genes, QTL, cytogenetic markers, "
				+ "and other genome features whose name, symbol, synonym, or accession "
				+ "ID matched some or all of your search text.<br/><br/>This list also "
				+ "includes genome features associated with vocabulary terms matching "
				+ "your search text. <br/><br/></div>"
				+ getUserDocAnchorString() + "', STICKY, CAPTION, "
				+ "'Genome Features', HAUTO, BELOW, WIDTH, 375, DELAY, "
				+ "600, CLOSECLICK, CLOSETEXT, 'Close X')";

		return helpPopup;
	}

	/**
	 * Create the tool tip popup for the vocab bucket.
	 * 
	 * @return String containing a formatted overlib call.
	 */

	public String getHelpPopupVocabBucket()
	{
		String helpPopup = "return overlib('<div class=detailRowType>"
				+ "Use the vocabulary terms listed here "
				+ "<ul>"
				+ "<li>to learn MGI\\\'s official terms</li>"
				+ "<li>to focus on detailed research topics</li>"
				+ "<li>to explore related research areas</li>"
				+ "<li>to investigate alternative areas</li>"
				+ "</ul></div>"
				+ getUserDocAnchorString() + "', STICKY, CAPTION, "
				+ "'Vocabulary Terms', HAUTO, BELOW, WIDTH, 375, DELAY, "
				+ "600, CLOSECLICK, CLOSETEXT, 'Close X')";

		return helpPopup;
	}

	/**
	 * Create the tool tip popup for the Other Bucket
	 * 
	 * @return String containing a formatted overlib call.
	 */

	public String getHelpPopupOtherBucket()
	{
		String helpPopup = "return overlib('<div class=detailRowType>This "
				+ "section includes links to "
				+ "alleles, sequences, orthology relationships, SNPs and other "
				+ "results whose accession ID matched an item in your search "
				+ "text.</div>" + getUserDocAnchorString() + "', STICKY, CAPTION, "
				+ "'Other Results By ID', HAUTO, BELOW, WIDTH, 375, DELAY, "
				+ "600, CLOSECLICK, CLOSETEXT, 'Close X')";

		return helpPopup;
	}

	/**
	 * Create the tool tip popup for the Google Bucket.
	 * 
	 * @return String containing a formatted overlib call.
	 */

	public String getHelpPopupGoogleBucket()
	{
		String helpPopup = "return overlib('<div class=detailRowType>Use Google to search for your text "
				+ "on MGI\\\'s web pages including:"
				+ "<ul>"
				+ "<li>FAQs</li>"
				+ "<li>Help pages</li>"
				+ "<li>Reference abstracts</li>"
				+ "<li>Phenotypic details for alleles</li>"
				+ "<li>Image captions</li>"
				+ "<li>...and other pages</li>"
				+ "</ul></div>" + getUserDocAnchorString() + "', STICKY, CAPTION, "
				+ "'Search MGI with Google', HAUTO, BELOW, WIDTH, 375, DELAY, "
				+ "600, CLOSECLICK, CLOSETEXT, 'Close X')";

		return helpPopup;
	}

	/**
	 * Creates the tool tip popup when the "Best Match" column is hovered over
	 * in the vocab bucket.
	 * 
	 * @return String containing a formatted overlib call.
	 */

	public String getVocabBestMatchMouseOver() {

		String message = "<div class=\\\'detailRowType\\\'>Displaying matches "
				+ "to vocabulary terms, synonyms, and"
				+ " definitions based on text similarity to your search text.  Not "
				+ "displaying subterms of matched terms.</div>"
				+ getUserDocAnchorString();

		String bestMatchMouseOver = "return overlib('"
				+ message
				+ "', STICKY, CAPTION, 'Best Match', HAUTO, BELOW, OFFSETY, 20,"
				+ " WIDTH, 300, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')";

		return bestMatchMouseOver;
	}

	/**
	 * Creates the tool tip popup when the "Best Match" column is hovered over
	 * on the marker bucket.
	 * 
	 * @return String containing a formatted overlib call.
	 */

	public String getMarkerBestMatchMouseOver() {

		String message = "<div class=\\\'detailRowType\\\'>For each genome"
				+ " feature, displaying the most relevant associated"
				+ " vocabulary term, ID or nomenclature.  Displayed vocabulary terms may"
				+ " be subterms of the best matching term, e.g., query matched"
				+ " &quot;hippocampus&quot; and subterm associated with the"
				+ " genome feature is &quot;dentate gyrus&quot;.</div>"
				+ getUserDocAnchorString();

		String bestMatchMouseOver = "return overlib('"
				+ message
				+ "', STICKY, CAPTION, 'Best Match', HAUTO, BELOW, OFFSETY, 20,"
				+ " WIDTH, 300, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')";

		return bestMatchMouseOver;
	}

	/**
	 * Returns a popup that appears when the help icon is hovered over.
	 * 
	 * @return String containing a formatted overlib call.
	 */

	public String getHelpIconPopup()
	{

		String helpPopup = "return overlib('<div>"
				+ "You can:<ul style=\\\'margin-top:0px; margin-left:10px;"
				+ " padding-left:7px; line-height:1.35;\\\'>"
				+ "<li style=\\\'font-size:1.1em;\\\'>Search for genome"
				+ " features by name, symbol, synonym, ortholog, allele, or accession"
				+ " ID:</li>"
				+ "<ul style=\\\'margin-left:30px; padding-left:0px;\\\'>"
				+ "<li>p53 protein cell cycle DNA damage</li>"
				+ "<li>Notch1</li>"
				+ "<li>NM_053172</li>"
				+ "<li>AT3</li>"
				+ "</ul>"
				+ "<li style=\\\'font-size:1.1em;\\\'>Search for genome"
				+ " features and vocabulary terms by keywords or accession ID. Use"
				+ " several keywords to find the most relevant results:</li>"
				+ "<ul style=\\\'margin-left:30px; padding-left:0px;\\\'>"
				+ "<li>GO:0004713</li>"
				+ "<li>resistance to fatty liver development</li>"
				+ "<li>vitamin D receptor</li>"
				+ "<li>lymphocyte function antigen immunodeficiency</li>"
				+ "</ul>"
				+ "<li style=\\\'font-size:1.1em;\\\'>Use asterisks (*) for wildcards"
				+ " at the end of partial words or genomic symbols:</li>"
				+ "<ul style=\\\'margin-left:30px; padding-left:0px;\\\'>"
				+ "<li>Pax*</li>"
				+ "<li>embr* development</li>"
				+ "<li>hippocamp* pyramid* cell layer</li>"
				+ "</ul>"
				+ "<li style=\\\'font-size:1.1em;\\\'>Use quotation marks"
				+ " (&quot; &quot;) to indicate phrases:</li>"
				+ "<ul style=\\\'margin-left:30px; padding-left:0px;\\\'>"
				+ "<li>&quot;hair follicle&quot; development</li>"
				+ "<li>&quot;amyotrophic lateral sclerosis&quot;</li>"
				+ "</ul>"
				+ "<li style=\\\'font-size:1.1em;\\\'>Use"
				+ " angle brackets to indicate superscript:</li>"
				+ "<ul style=\\\'margin-left:30px; padding-left:0px;\\\'>"
				+ "<li>Pax6<10Neu> for Pax6"
				+ "<sup style=\\\'font-size:0.8em;\\\'>10Neu</sup></li>"
				+ "</ul>"
				+ "<li style=\\\'font-size:1.1em;\\\'>Mix IDs, symbols, and"
				+ " keywords in a list:</li>"
				+ "<ul style=\\\'margin-left:30px; padding-left:0px;\\\'>"
				+ "<li>Nmt2, NM_013627, Acbd7, hair follicle development</li>"
				+ "</ul>"
				+ "</ul></div>"
				+ "Advanced searches are listed under the Search menu.<BR/>"
				+ "See <a href=\\\'"
				+ stConfig.get("USERHELP_URL")
				+ "QUICK_SEARCH_help.shtml\\\'>"
				+ "Using the Quick Search Tool</a> for more information."
				+ "', STICKY, CAPTION, "
				+ "'Quick Search Tips', HAUTO, BELOW, WIDTH, 450, DELAY, "
				+ "600, CLOSECLICK, CLOSETEXT, 'Close X')";

		return helpPopup;
	}

	// ------------------------------------------------------- Search Detail
	// Info

	/**
	 * Create the Why did we match section, this is common across several pages.
	 */

	public String getWhyMatchSearchDetails(SearchInput si) throws IOException
	{
		// The top part of the message text will ALWAYS appear, so simply
		// construct it.
		String topMessageText = "<div class='small'>" + getSearchTermDisplay(si)
				+ " " + getStemmedSearchTermDisplay(si) + "</div>";

		String bottomMessageText = "";

		bottomMessageText += getZeroHitTokensString(si);

		// The bottom part of the message text is only brought back if a missing
		// large
		// token is found. So we want to suppress its div if that's the case.
		if (!bottomMessageText.equals("")) {
			bottomMessageText = "<div class='small'>" + bottomMessageText + "</div>";
		}

		return topMessageText + bottomMessageText;
	}

	/**
	 * Returns the zero hit tokens string.
	 * 
	 * @param Searchinput
	 *            si - The encapsulation of the users search string.
	 * @return String - The displayable zero hit token string.
	 */

	public String getZeroHitTokensString(SearchInput si) {

		String messageText = "";

		String warningIcon = "<img src=" + stConfig.get("QUICKSEARCH_URL")
				+ "redwarning.gif style='vertical-align: text-bottom'>";

		/*
		 * String warningIcon = "<img src=" + stConfig.get("QUICKSEARCH_URL") +
		 * "redwarning.gif>";
		 */

		List<String> zeroHitTokens = si.getZeroHitTokens();

		// First Check to see if we have zero token hits:
		// If we do we need to display something slightly different on the link
		// section.

		if (zeroHitTokens.size() > 0) {
			messageText += "<span>" + warningIcon
					+ "</span><span style='margin-bottom: 100px;'>Could not "
					+ "find the independent term(s): "
					+ "<span class='redText'>";

			// The first word that we print out needs no comma, so set a flag
			// to keep track.

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

	/**
	 * Returns the terms the user searched for.
	 * 
	 * @param si
	 * @return Formatted string containing the users search terms.
	 * @throws IOException
	 */

	public String getSearchTermDisplay(SearchInput si) throws IOException {
		// Display the search terms

		String text = "";

		List<String> tokenizedInputString = si.getTokenizedLittleInputString();

		if (tokenizedInputString.size() > 0) {
			text += "<span>You searched for: <span class=\"italic\">";

			for (Iterator<String> iter = tokenizedInputString.iterator(); iter.hasNext();) {
				String token = (String) iter.next();

				// Overlib cannot handle quotes in its input, so for ones that
				// we want
				// to display we need to use the html encoding.

				text += " " + token.replaceAll("\"", "&quot;");
			}

			text += ".</span></span>";

		}

		return text;
	}

	/**
	 * Creates a stemmed version of the users search string, with any removed
	 * words having been removed.
	 * 
	 * @param si
	 * @return Formatted String containing the users stemmed search.
	 * @throws IOException
	 */

	public String getStemmedSearchTermDisplay(SearchInput si)
			throws IOException {
		// Display the stemmed search terms, if applicable

		String text = "";

		List<String> stemmedTokenizedInputString = si
				.getStemmedTokenizedLittleInputString();

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
	 * 
	 * @param si
	 *            The encapsulation of the users search string.
	 * @return String with the fully formed search details link.
	 * @throws IOException
	 */
	public String getSearchDetailOnClick(SearchInput si) throws IOException {

		String warningIconOverLib = "<img src=" + stConfig.get("QUICKSEARCH_URL")
				+ "redwarning.gif style=\\\'vertical-align: text-bottom\\\'>";
		String messageText = "<div class='small grayText'>";
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
			text += "<div class=\\\'detailRowType\\\'>" +
					"<span class=\\\'detailHeaderType\\\'>" +
					"Search Terms ";

			if (zeroHitTokens.size() > 0) {
				text += warningIconOverLib;
			}

			text += "</span> <br> Searching for matches to"
					+ " <span class=\\\'italic\\\'>";

			for (Iterator<String> iter = tokenizedInputString.iterator(); iter
					.hasNext();) {
				String token = (String) iter.next();

				// Overlib cannot handle quotes in its input, so for ones that
				// we want to display we need to use the html encoding.

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

		List<String> stemmedTokenizedInputString =
				si.getStemmedTokenizedLittleInputString();

		if (stemmedTokenizedInputString.size() > 0 && !si.hasPrefix()) {
			text += "<div class=\\\'detailRowType\\\'>"
					+ "<span class=\\\'detailHeaderType\\\'>"
					+ "Stemming </span><br> When possible words are &quot;"
					+ "stemmed&quot; to a root word by removing common suffixes. "
					+ "Also searching for matches to "
					+ "<span class=\\\'italic\\\'>";

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

		/*
		 * The search excludes numbers between 0 and 99, single letters, and
		 * common words like of, to, with and not when they do not return
		 * relevant results. You can force Quick Search to include them by using
		 * double quotes. The search excludes very common words like the, and,
		 * and or except in current symbols and names for mouse genome features.
		 * You cannot force Quick Search to consider these words.
		 */

		if (si.hasStopWords() || si.hasExcludedWords()) {
			text += "<div class=\\\'detailRowType\\\'>"
					+ "<span class=\\\'detailHeaderType\\\'>"
					+ "Excluded Terms</span><br>The search excludes"
					+ " numbers between 0 and 99, single letters,"
					+ " and common words like"
					+ " <span class=\\\'italic\\\'>of</span>,"
					+ " <span class=\\\'italic\\\'>to</span>,"
					+ " <span class=\\\'italic\\\'>with</span>"
					+ " and <span class=\\\'italic\\\'>not</span> when they"
					+ " do not return relevant results. You can force Quick"
					+ " Search to include them by using double quotes."
					+ "  The search excludes very common words like"
					+ " <span class=\\\'italic\\\'>the</span>,"
					+ " <span class=\\\'italic\\\'>and</span>,"
					+ " and <span class=\\\'italic\\\'>or</span>"
					+ " except in current symbols and names for"
					+ " mouse genome features.  You cannot force Quick Search"
					+ " to consider these words.</div>";
		}

		// Display any boolean detected messages if need be.

		if (si.hasBoolean()) {
			text += "<div class=\\\'detailRowType\\\'>"
					+ "<span class=\\\'detailHeaderType\\\'>"
					+ "And, Not, and Or</span><br>The search handles "
					+ "<span class=\\\'smallCaps\\\'>And</span>,"
					+ " <span class=\\\'smallCaps\\\'>Not</span>"
					+ ", and <span class=\\\'smallCaps\\\'>Or</span> as "
					+ "Stop Words.  "
					+ "They cannot be used for Boolean logic.</div>";
		}

		// Is this a prefix search?

		if (si.hasPrefix()) {
			text += "<div class=\\\'detailRowType\\\'>"
					+ "<span class=\\\'detailHeaderType\\\'>"
					+ "Wildcard *</span><br>Your search contains a wildcard.  "
					+ "The search "
					+ "tries to match terms not ending in a wildcard exactly.  "
					+ "Accession " + "IDs may not use a wildcard.</div>";
		}

		// Are there quotes?

		if (si.hasQuotes()) {
			text += "<div class=\\\'detailRowType\\\'>"
					+ "<span class=\\\'detailHeaderType\\\'>"
					+ "Double Quotes &quot; &quot;</span><br>Your search"
					+ " contains quotation marks."
					+ "  The search handles terms enclosed in quotation marks"
					+ " as a phrase.</div>";
		}

		// The Case insensitive statement is always present, add it to the text.

		text += "<div class=\\\'detailRowType\\\'>"
				+ "<span class=\\\'detailHeaderType\\\'>"
				+ "Case</span><br>Searches are not case sensitive; "
				+ "searching for embryo,"
				+ " Embryo, or eMbRyO will return the same results.</div>";

		text += getUserDocAnchorString();

		text += "</div>";

		return messageText
				+ "return overlib('"
				+ text
				+ "', STICKY, CAPTION, 'Quick Search Details for this search:', "
				+ "HAUTO, BELOW, WIDTH, 375, CLOSECLICK, CLOSETEXT, 'Close X')"
				+ "\">details</a> for this search.</div>";
	}

	// ------------------------------------------------------- Marker
	// "Why Match"

	public String getMrkWhyMatchURL(GenomeFeatureResult gfResult, String query)
	{
		return "'Search.do?query="
				+ getEncodedUrl(query) + "&page=featureDetails&resultKey="
				+ gfResult.getCacheKey() + "'";
	}

	public String getMrkWhyMatchText(GenomeFeatureResult genomeFeatureResult)
	{
		if (genomeFeatureResult.getMatchCount() > 1) {
			return "&nbsp;&nbsp;and " + (genomeFeatureResult.getMatchCount() - 1) + " more...";
		} else {
			return "&nbsp;&nbsp;and more detail...";
		}
	}

	/**
	 * Helper method that creates a tooltip for the markerbucket.
	 * 
	 * @param genomeFeatureResult
	 *            The genomeFeatureResult we want to create the tooltip for.
	 * @return A An anchor tag, containing the overlib javascript to create the
	 *         tooltip.
	 */
	public static String getMarkerScoreMouseOver(GenomeFeatureResult genomeFeatureResult)
	{
		GenomeFeatureDisplay markerDisplay = gfDisplayCache.getGenomeFeature(genomeFeatureResult);

		int nomenMatches = genomeFeatureResult.getAllMarkerNomenMatches().size();
		int adMatches = genomeFeatureResult.getAdMatches().size();
		int mpMatches = genomeFeatureResult.getMpMatches().size();
		int goMatches = genomeFeatureResult.getGoMatches().size();
		int omimMatches = genomeFeatureResult.getOmimMatches().size();
		int omimOrthoMatches = genomeFeatureResult.getOmimOrthoMatches().size();
		int pirsfMatches = genomeFeatureResult.getPirsfMatches().size();
		int ipMatches = genomeFeatureResult.getIpMatches().size();

		String caption = "see matches for "
				+ DisplayHelper.superscript(markerDisplay.getSymbol());
		String contents = "";

		if (nomenMatches != 0) {
			contents = contents + nomenMatches + " Nomenclature Match";
			if (nomenMatches > 1) {
				contents = contents + "es";
			}
			contents = contents + "<br/>";
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
				+ "', RIGHT, BELOW, WIDTH, 200, DELAY, 200);\" onMouseOut=\"nd();\" ";

		return scoreOverlib;
	}

	// ------------------------------------------------------- Vocab Annotations

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

	public String vocabAnnotation(VocabResult vocabResult)
	{
		String vocab = vocabResult.getVocabulary();
		VocabDisplay vocabDisplay = vocabDisplayCache.getVocab(vocabResult);

		String url = "";
		if (vocab.equals(IndexConstants.AD_TYPE_NAME)) {
			// url ="<a href = '" + stConfig.get("WI_URL") +
			// "searches/expression_report.cgi?_Structure_key=" +
			// vocabDisplay.getDbKey() +
			// "&sort=Gene%20Symbol&returnType=assay%20results&substructures=substructures'>"
			// + vocabDisplay.getAnnotDisplay()+ "</a>";
			url = "<a href = '" + stConfig.get("FEWI_URL") + "gxd/summary?structureKey=" + vocabDisplay.getDbKey() + "'>" + vocabDisplay.getAnnotDisplay() + "</a>";
		}
		else if (vocab.equals(IndexConstants.EMAPA_TYPE_NAME)) {
			url = "<a href='" + stConfig.get("FEWI_URL") + "mgi/gxd/structure/" + vocabDisplay.getAcc_id() + "'> " + vocabDisplay.getAnnotDisplay() + "</a>";
		}
		else if (vocab.equals(IndexConstants.EMAPS_TYPE_NAME)) {
			url = "<a href='" + stConfig.get("FEWI_URL") + "mgi/gxd/structure/" + vocabDisplay.getAcc_id() + "'> " + vocabDisplay.getAnnotDisplay() + "</a>";
		}
		else if (vocab.equals(IndexConstants.MP_TYPE_NAME)) {
			url = "<a href='" + stConfig.get("FEWI_URL") + "mp/annotations/" + vocabDisplay.getAcc_id() + "'> " + vocabDisplay.getAnnotDisplay() + "</a>";
		}
		else if (vocab.equals(IndexConstants.GO_TYPE_NAME)) {
			url = "<a href='" + stConfig.get("WI_URL") + "searches/GOannot_report.cgi?id=" + vocabDisplay.getAcc_id() + "'> " + vocabDisplay.getAnnotDisplay() + "</a>";
		}
		else if (vocab.equals(IndexConstants.OMIM_TYPE_NAME)) {
			url = "<a href='" + stConfig.get("FEWI_URL") + "disease/key/" + vocabDisplay.getDbKey() + "'>" + vocabDisplay.getAnnotDisplay() + "</a>";
		}
		else if (vocab.equals(IndexConstants.PIRSF_TYPE_NAME))
		{
			url = "<a href='" + stConfig.get("FEWI_URL") + "vocab/pirsf/" + vocabDisplay.getAcc_id() + "'>" + vocabDisplay.getAnnotDisplay() + "</a>";
		}
		else if (vocab.equals(IndexConstants.INTERPRO_TYPE_NAME))
		{
			url = "<a href='" + stConfig.get("WI_URL") + "searches/marker_report.cgi?op%3Ago_term=contains&go_term=&interpro=" + vocabDisplay.getAcc_id() + "&clone=&sort=Nomenclature&*limit=500'>" + vocabDisplay.getAnnotDisplay() + "</a>";
		}
		else
		{
			url = vocab;
		}

		return url;
	}

	// ---------------------------------------------------------- Utility
	// Methods

	/**
	 * For a given collection, create a comma delimited string
	 * 
	 * @param c
	 *            the source Collection
	 * @return Comma delimited string
	 */
	public static String commaDelimit(Collection c)
	{
		String commaDelimString = new String("");

		for (Iterator i = c.iterator(); i.hasNext();)
		{
			String nextValue = (String) i.next();
			if (nextValue != null) {
				if (!commaDelimString.equals("")) {
					commaDelimString = commaDelimString + ", ";
				}
				commaDelimString = commaDelimString + nextValue;
			}
		}

		return commaDelimString;
	}

	/**
	 * convert all "<" and ">" pairs in 's' to be HTML superscript tags.
	 * 
	 * @param s
	 *            the source String
	 * @return String as 's', but with the noted replacement made. returns null
	 *         if 's' is null.
	 */
	public static String superscript(String s)
	{
		return superscript(s, "<", ">");
	}

	/**
	 * convert all 'start' and 'stop' pair in 's' to be HTML superscript tags.
	 * 
	 * @param s
	 *            the source String
	 * @param start
	 *            the String which indicates the position for the HTML
	 *            superscript start tag "<SUP>"
	 * @param stop
	 *            the String which indicates the position for the HTML
	 *            superscript stop tag "</SUP>"
	 * @return String as 's', but with the noted replacement made. returns null
	 *         if 's' is null. returns 's' if either 'start' or 'stop' is null.
	 */
	public static String superscript(String s, String start, String stop)
	{
		// shortcut logic if there's nothing to do
		if (s == null) {
			return null;
		} // no source string
		if ((start == null) || (stop == null)) {
			return s;
		} // no start/stop string

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

		int startLen = start.length(); // how many chars to cut out for start
		int stopLen = stop.length(); // how many chars to cut out for stop
		int sectionStart = 0; // position of char starting section

		StringBuffer sb = new StringBuffer();

		while ((startPos != -1) && (stopPos != -1))
		{
			sb.append(s.substring(sectionStart, startPos));
			sb.append("<SUP>");
			sb.append(s.substring(startPos + startLen, stopPos));
			sb.append("</SUP>");

			sectionStart = stopPos + stopLen;
			startPos = s.indexOf(start, sectionStart);
			stopPos = s.indexOf(stop, sectionStart);
		}
		sb.append(s.substring(sectionStart));

		return sb.toString();
	}

	/**
	 * @param
	 * @return
	 */
	public static String getEncodedUrl(String s)
	{
		String encodedurl = "";
		try {
			encodedurl = URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			System.err.println(uee);
		}
		return encodedurl;
	}

	/**
	 * Convert string representation of int, adding commas
	 * 
	 * @param string
	 *            value of the integer
	 * @return string value of the interger, with commas added
	 */
	public static String commaFormatIntStr(String numIn)
	{
		String numOut;

		// get default number formatting
		NumberFormat form;
		form = NumberFormat.getInstance();

		// and apply that format to the input string
		numOut = form.format(new Long(numIn));

		return numOut;
	}

	/**
	 * A helper method that returns a truncated version of a string for display.
	 * 
	 * @param strIn
	 *            The string to be transformed.
	 * @return The truncated version of the string.
	 */
	public static String shortenDisplayStr(String strIn)
	{
		String strOut = new String(strIn);
		if (strOut.length() > 60) {
			strOut = strOut.substring(0, 59);
			if (strOut.substring(44, 59).contains(" ")) {
				strOut = strOut.substring(0, strOut.lastIndexOf(" ")); // errors
																		// if no
																		// " "
			}
			strOut = strOut + "...";
		}
		return strOut;
	}

	/**
	 * Shorted the displayed score shorted for readability
	 * 
	 * @param s
	 *            the source of the score
	 * @return String no more than 5 characters long
	 */
	public static String getShortScore(float f)
	{
		Float newF = new Float(f);
		if (newF.toString().length() < 5) {
			return newF.toString();
		}
		return newF.toString().substring(0, 5);
	}

	/**
	 * Visual representation of match's score
	 * 
	 * @param match
	 *            - match to score
	 * @return String - HTML for displaying this matches representative score
	 */
	public String getMatchStarScore(AbstractMatch match) {

		String star = "<img src='" + stConfig.get("QUICKSEARCH_URL")
				+ "darkStarSmall.gif' width='9' height='8'>";

		if (match.isTier1()) {
			return star + star + star + star;
		}
		else if (match.isTier2()) {
			return star + star + star;
		}
		else if (match.isTier3()) {
			return star + star;
		}
		return star;
	}

}

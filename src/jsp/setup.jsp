<%@ page import="java.util.*,
    org.jax.mgi.search.results.*,
    org.jax.mgi.search.lookup.*,
    org.jax.mgi.search.matches.*,
    org.jax.mgi.search.exception.*,
    org.jax.mgi.search.utils.*,
    org.jax.mgi.shr.config.Configuration,
    QS_Commons.IndexConstants" %>

<%
// Pull the objects we'll need from the request.  These have been attached
// to the request'upstream', either by the servlet or the container
SearchInput searchInput = (SearchInput)request.getAttribute("SearchInput");
MarkerDisplayCache markerDisplayCache =
    (MarkerDisplayCache)request.getAttribute("MarkerDisplayCache");
VocabDisplayCache vocabDisplayCache =
    (VocabDisplayCache)request.getAttribute("VocabDisplayCache");
OtherDisplayLookup otherDisplayLookup =
    (OtherDisplayLookup)request.getAttribute("OtherDisplayLookup");
Configuration stConfig = (Configuration)request.getAttribute("Configuration");
WebTemplate webTemplate = (WebTemplate)request.getAttribute("WebTemplate");
List<String> zeroHitTokens = searchInput.getZeroHitTokens();

// setup debug value for display layer
boolean debug = false;
if ( searchInput.hasFormParameter("debug")
  && searchInput.getParameter("debug").equals("true") ) {
    debug = true;
}

// derive needed data from passed request arrributes
DisplayHelper displayHelper
    = new DisplayHelper(stConfig, markerDisplayCache, vocabDisplayCache);

// derive display strings for downstream usage
String query = searchInput.getSearchString().replaceAll("\"", "&quot;");
// The String that we use to generate links can't have the "" replacement in it
String queryForward = searchInput.getSearchString();
String javawi_url = stConfig.get("JAVAWI_URL");
String wi_url = stConfig.get("WI_URL");
String webshare_url = stConfig.get("WEBSHARE_URL");
String userdocs_url = stConfig.get("USERDOCS_URL");
String mgihome_url = stConfig.get("MGIHOME_URL");

%>


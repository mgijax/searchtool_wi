<%@ page import="java.util.*,
    org.jax.mgi.searchtool_wi.results.*,
    org.jax.mgi.searchtool_wi.lookup.*,
    org.jax.mgi.searchtool_wi.matches.*,
    org.jax.mgi.searchtool_wi.exception.*,
    org.jax.mgi.searchtool_wi.utils.*,
    org.jax.mgi.shr.config.Configuration,
    org.jax.mgi.shr.searchtool.IndexConstants" %>

<%
// Pull the objects we'll need from the request.  These have been attached
// to the request'upstream', either by the servlet or the container
SearchInput searchInput = (SearchInput)request.getAttribute("SearchInput");
GenomeFeatureDisplayCache gfDisplayCache =
    (GenomeFeatureDisplayCache)request.getAttribute("GenomeFeatureDisplayCache");
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
    = new DisplayHelper(stConfig, gfDisplayCache, vocabDisplayCache);

// query strings; queryForward to be used in URL generation and forwarding
String query = searchInput.getSearchString().replaceAll("\"", "&quot;");
String queryForward = searchInput.getSearchString();

// display url strings for downstream usage
String javawi_url = stConfig.get("JAVAWI_URL");
String wi_url = stConfig.get("WI_URL");
String fewi_url = stConfig.get("FEWI_URL");
String webshare_url = stConfig.get("WEBSHARE_URL");
String userdocs_url = stConfig.get("USERDOCS_URL");
String mgihome_url = stConfig.get("MGIHOME_URL");

%>


<%@include file="setup.jsp"%>

<%
    GenomeFeatureResultContainer genomeFeatureResultContainer =
        (GenomeFeatureResultContainer)request.getAttribute("MarkerResultContainer");

    // derive needed data from passed request attributes
    Integer markerStart = new Integer(1);   //default
    Integer markerRange = new Integer(100); //default
    if ( searchInput.hasFormParameter("markerStart") ) {
        markerStart = new Integer(searchInput.getParameter("markerStart"));
    }
    if ( searchInput.hasFormParameter("markerRange") ) {
        markerRange = new Integer(searchInput.getParameter("markerRange"));
    }

    Integer markerStop = markerStart + markerRange -1;
    if (markerStop > genomeFeatureResultContainer.size() ) {
        markerStop = genomeFeatureResultContainer.size();
    }

    // colors and color iteration
    StringAlternator bucketRowAlternator
        = new StringAlternator( "qsBucketRow1", "qsBucketRow2" );
    String rowClass;

    // prebuild a few display strings
    String previousLink = new String("");
    String nextLink = new String("");
    if (markerStart.intValue() > 1) {
        String forwardUrl = stConfig.get("QUICKSEARCH_URL") + "Search.do?query="
          + displayHelper.getEncodedUrl(queryForward)
          + "&page=marker&markerStart=" + (markerStart - markerRange);
        previousLink = "<a class='small' href='" + forwardUrl + "'>Previous</a>";
    }
    if (markerStop.intValue() < genomeFeatureResultContainer.size()) {
        String forwardUrl = stConfig.get("QUICKSEARCH_URL") + "Search.do?query="
          + displayHelper.getEncodedUrl(queryForward)
          + "&page=marker&markerStart=" + (markerStart + markerRange);
        nextLink = "<a class='small' href='" + forwardUrl + "'>Next</a>";
    }

    // loop variables
    GenomeFeatureResult  thisGenomeFeatureResult; //search result 
    MarkerDisplay thisMarkerDisplay; //pulled from cache 

    // URLS
    String detailPageUrl = "";

%>

<!--======================================================== Open the Page -->
<%=webTemplate.getTemplateHeadHtml()%>

<meta name="robots" content="NOINDEX">
<title>MGI Quick Search Results</title>

<script>
</script>
<%=webTemplate.getTemplateBodyStartHtml()%>

<%@include file="page_header.jsp"%>


<!--======================================================== Marker Bucket -->

<!-- Header Row -->
<table class='qsBucket' width='100%' border=0>
  <tr>
  <td colspan='8' class='qsBucketHeader'>
    Genome Features
    <span class='small grayText'>
    Sorted by best match, showing <%=markerStart%> -
    <%=markerStop%> of
    <%=displayHelper.commaFormatIntStr(String.valueOf(genomeFeatureResultContainer.size()))%>
    <span class="helpCursor"
      onmouseover="<%=displayHelper.getHelpPopupMarkerBucket()%>"
      onmouseout="nd();">
       <img src="<%=stConfig.get("QUICKSEARCH_URL")%>blue_info_icon.gif" border="0"/>
    </span>&nbsp;&nbsp;&nbsp;
    <%=previousLink%> <%=nextLink%>
    </span>
  </td>
  </tr>

  <tr align=left valign=top >
    <th style='padding-right: 5px;padding-left:5px; text-align:right' width='%4'>
      <span onmouseover="<%=displayHelper.getScoreMouseOverMarker()%>" onmouseout="nd();">
       &nbsp;&nbsp;<a class="helpPopUp helpCursor" href="#">Score</a>
      </span>
    </th>
    <th style='padding-right:5px;padding-left:5px;'>Type</th>
    <th style='padding-right:5px;padding-left:5px;'>Symbol</th>
    <th style='padding-right:5px;padding-left:5px;'>Name</th>
    <th style='text-align:right;padding-right:5px;padding-left:5px;'>Chr</th>
    <th style='padding-right:5px; padding-left:5px;'>Location</th>
    <th style='padding-right:5px; padding-left:5px;'>Str</th>
    <th style='padding-right:5px;padding-left:5px;' width='%40' >
      <span onmouseover="<%=displayHelper.getMarkerBestMatchMouseOver()%>" onmouseout="nd();">
       <a class="helpPopUp helpCursor" href="#">Best Match</a>
      </span>
    </th>
  </tr>

<!-- Iterate through data rows -->
<%

  for (Iterator iter
    = genomeFeatureResultContainer.getHits(markerStart,markerStop).iterator();
    iter.hasNext();)

  {
    thisGenomeFeatureResult    = (GenomeFeatureResult)iter.next();
    thisMarkerDisplay   = gfDisplayCache.getGenomeFeature(thisGenomeFeatureResult);
    rowClass = bucketRowAlternator.getString();

    if ( thisGenomeFeatureResult.isMarker() ) {
      detailPageUrl = javawi_url + "WIFetch?page=markerDetail&key=" + thisGenomeFeatureResult.getDbKey();
    }
    else if ( thisGenomeFeatureResult.isAllele() ) {
      detailPageUrl = javawi_url + "WIFetch?page=alleleDetail&key=" + thisGenomeFeatureResult.getDbKey();
    }

%>
  <tr class='<%=rowClass%>'>
    <td style='text-align:right;'>
        <%=thisGenomeFeatureResult.getStarScore()%>
        <% if(debug){out.print(thisGenomeFeatureResult.getDebugDisplay());} %>
    </td>
    <td class='small' >
        <%=thisMarkerDisplay.getMarkerType()%>
    </td>
    <td>
      <a href='<%=detailPageUrl%>'>
        <%=DisplayHelper.superscript(thisMarkerDisplay.getSymbol())%>
      </a>
    </td>
    <td>
      <%=DisplayHelper.superscript(thisMarkerDisplay.getName())%>
    </td>
    <td class='small' style='text-align:right'>
      <%=thisMarkerDisplay.getChromosome()%>
    </td>
    <td class='small'>
      <%=thisMarkerDisplay.getLocDisplay()%>
    </td>
    <td class='small'>
      <%=thisMarkerDisplay.getStrand()%>
    </td>
    <td class='small'>
      <%=thisGenomeFeatureResult.getBestMatch().display()%>

      <a class="qsWhyMatchLink"
        <%=displayHelper.getMarkerScoreMouseOver(thisGenomeFeatureResult)%>
        href=<%=displayHelper.getMrkWhyMatchURL(thisGenomeFeatureResult, queryForward)%> >
        <%=displayHelper.getMrkWhyMatchText(thisGenomeFeatureResult)%>
      </a>

      <% if (debug) { %>
        <br/>result db key -> <%=thisGenomeFeatureResult.getDbKey()%>
        <br/>match db key -> <%=thisGenomeFeatureResult.getBestMatch().getDbKey()%>
      <% } %>

    </td>
    </tr>
<% } /* for each result */ %>

  <tr style="background-color:#dfefff;">
    <td colspan=5>
      <span class='small grayText'>
      Showing <%=markerStart%> -
      <%=markerStop%> of
      <%=displayHelper.commaFormatIntStr(String.valueOf(genomeFeatureResultContainer.size()))%>
      &nbsp;&nbsp;&nbsp;<%=previousLink%> <%=nextLink%>
      </span>
    </td>
    <td colspan=3>
      <div style='float:right; display:none;' id='bqf'>
               <%@include file="batchQueryForwarding.jsp"%>
      </div>
      <script>document.getElementById('bqf').style.display = 'block';</script>
    </td>
  </tr>

</table>

<br/>
<br/>

<%=webTemplate.getTemplateBodyStopHtml()%>

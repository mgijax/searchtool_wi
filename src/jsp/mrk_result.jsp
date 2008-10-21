<%@include file="setup.jsp"%>

<%
    QS_MarkerResultContainer markerResultContainer =
        (QS_MarkerResultContainer)request.getAttribute("MarkerResultContainer");

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
    if (markerStop > markerResultContainer.size() ) {
        markerStop = markerResultContainer.size();
    }

    // colors and color iteration
    StringAlternator bucketRowAlternator
        = new StringAlternator( "qsBucketRow1", "qsBucketRow2" );
    String rowClass;

    // prebuild a few display strings
    String previousLink = new String("");
    String nextLink = new String("");
    if (markerStart.intValue() > 1) {
        previousLink = "<a class=small href=Search.do?query=" + query + "&page=marker&markerStart="
          + (markerStart - markerRange) + ">Previous</a>" ;
    }
    if (markerStop.intValue() < markerResultContainer.size()) {
        String forwardUrl = stConfig.get("QUICKSEARCH_URL") + "Search.do?query="
          + displayHelper.getEncodedUrl(query)
          + "&page=marker&markerStart=" + (markerStart + markerRange);
        nextLink = "<a class='small' href='" + forwardUrl + "'>Next</a>";
    }

    // loop variables
    QS_MarkerResult thisMarkerResult; //search result built by model
    MarkerDisplay thisMarkerDisplay; //pulled from cache for given marker6
%>

<%=webTemplate.getTemplateHeadHtml()%>
<script>
</script>
<%=webTemplate.getTemplateBodyStartHtml()%>
<%@include file="page_header.jsp"%>


<!--======================================================== Marker Bucket -->

<!-- Header Row -->
<table class='qsBucket' width='100%' border=0>
  <tr>
  <td colspan='6' class='qsBucketHeader'>
    Genome Features
    <span class='small grayText'>
    Sorted by best match, showing <%=markerStart%> -
    <%=markerStop%> of <%=markerResultContainer.size()%>&nbsp;&nbsp;&nbsp;
    <%=previousLink%> <%=nextLink%>
    </span>
  </td>
  </tr>

  <tr align=left valign=top >
    <th style='padding-right: 5px;padding-left:5px; text-align:right' width='%4'>
      <span onmouseover="<%=displayHelper.getScoreMouseOver()%>" onmouseout="nd();">
       &nbsp;&nbsp;<a class="helpPopUp" href="#">Score</a>
      </span>
    </th>
    <th style='padding-right:5px;padding-left:5px;'>Type</th>
    <th style='padding-right:5px;padding-left:5px;'>Symbol</th>
    <th style='padding-right:5px;padding-left:5px;'>Name</th>
    <th style='text-align:right;padding-right:5px;padding-left:5px;'>Chr</th>
    <th style='padding-right:5px;padding-left:5px;' width='%40' >Best Match</th>
  </tr>

<!-- Iterate through data rows -->
<%
  int rowCount = 0;
  for (Iterator iter
    = markerResultContainer.getHits(markerStart,markerStop).iterator();
    iter.hasNext();)

  {
    thisMarkerResult    = (QS_MarkerResult)iter.next();
    thisMarkerDisplay   = markerDisplayCache.getMarker(thisMarkerResult);
    rowClass = bucketRowAlternator.getString();
    rowCount++;
%>
  <tr class='<%=rowClass%>'>
    <td style='text-align:right;'>
        <%=thisMarkerResult.getStarScore()%>
        <% if(debug){out.print(thisMarkerResult.getScore());} %>
        <% if(debug){out.print("<br/>C-> " + rowCount);} %>
    </td>
    <td class='small' >
        <%=thisMarkerDisplay.getMarkerType()%>
    </td>
    <td>
      <a href='<%=javawi_url%>WIFetch?page=markerDetail&key=<%=thisMarkerDisplay.getDbKey()%>'>
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
      <%=thisMarkerResult.getBestMatch().display()%>
      <% if (displayHelper.needsMrkWhyMatchLink(thisMarkerResult) ) { %>
        <a class="qsWhyMatchLink"
          <%=displayHelper.getMarkerScoreMouseOver(thisMarkerResult)%>
          href=<%=displayHelper.getMrkWhyMatchURL(thisMarkerResult, queryForward)%> >
          <%=displayHelper.getMrkWhyMatchText(thisMarkerResult)%>
        </a>
      <% } %>

      <% if (debug) { %>
        <br/>result db key -> <%=thisMarkerResult.getDbKey()%>
        <br/>match db key -> <%=thisMarkerResult.getBestMatch().getDbKey()%>
      <% } %>
    </td>
    </tr>
<% } /* for each result */ %>

  <tr style="background-color:#dfefff;">
    <td colspan=3>
        &nbsp;
    </td>
    <td colspan=3>
      <div style='float:right; display:none;' id='bqf'>
               <%@include file="batchQueryForwarding.jsp"%>
      </div>
      <script>document.getElementById('bqf').style.display = 'block';</script>
    </td>
  </tr>

</table>

<div style='height:2em;position:absolute;width:100%;'>
  <span class='small grayText'>
  Sorted by best match, showing <%=markerStart%> -
  <%=markerStop%> of <%=markerResultContainer.size()%>&nbsp;&nbsp;&nbsp;
  <%=previousLink%> <%=nextLink%>
  </span>
</div>

<br/>
<br/>

<%=webTemplate.getTemplateBodyStopHtml()%>

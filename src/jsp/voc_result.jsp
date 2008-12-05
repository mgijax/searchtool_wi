<%@include file="setup.jsp"%>

<%

    QS_VocabResultContainer vocabResultContainer =
        (QS_VocabResultContainer)request.getAttribute("VocabResultContainer");

    // derive needed data from passed request attributes
    Integer vocabStart = new Integer(1);   //default
    Integer vocabRange = new Integer(100); //default
    if ( searchInput.hasFormParameter("vocabStart") ) {
        vocabStart = new Integer(searchInput.getParameter("vocabStart"));
    }
    if ( searchInput.hasFormParameter("vocabRange") ) {
        vocabRange = new Integer(searchInput.getParameter("vocabRange"));
    }

    Integer vocabStop = vocabStart + vocabRange -1;
    if (vocabStop > vocabResultContainer.size() ) {
        vocabStop = vocabResultContainer.size();
    }

    // colors and color iteration
    StringAlternator bucketRowAlternator
        = new StringAlternator( "qsBucketRow1", "qsBucketRow2" );
%>

<!--======================================================== Open the Page -->
<%=webTemplate.getTemplateHeadHtml()%>

<meta name="robots" content="NOINDEX">
<title>MGI Quick Search Results</title>

<script>
</script>
<%=webTemplate.getTemplateBodyStartHtml()%>

<%@include file="page_header.jsp"%>

<%
  //--------------//
  // Vocab Bucket
  //--------------//

  QS_VocabResult thisVocabResult; //search result built by model
  VocabDisplay thisVocabDisplay; //pulled from cache for given marker

  String rowClass;
  String vocabMatchID;
  int divCounter = 0;

  // table header
  out.print("<table class='qsBucket' width='100%'><tr>");
  out.print("<td colspan='4' class='qsBucketHeader'>");
  out.print("Vocabulary Terms ");
  out.print("<span class='small grayText'> Sorted by best match, showing " + (vocabStart) + "-" + (vocabStop) + " of "
    + (vocabResultContainer.size()) + "</span>");
%>
      <span onmouseover="<%=displayHelper.getHelpPopupVocabBucket()%>" onmouseout="nd();">
           <a class="helpCursor" href="#"><img src="<%=stConfig.get("QUICKSEARCH_URL")%>blue_info_icon.gif" border="0"/></a>
      </span>
      &nbsp;&nbsp;&nbsp;&nbsp;
<%
  if (vocabStart.intValue() > 1) {
      out.print("<a class='small' href='Search.do?query=" + query + "&page=vocab&vocabStart="
        + (vocabStart - vocabRange) +"'>Previous</a> ");
  }
  if (vocabStop.intValue() < vocabResultContainer.size()) {
      out.print("<a class='small' href='Search.do?query=" + query + "&page=vocab&vocabStart="
        + (vocabStart + vocabRange) +"'>Next</a>");
  }
%>

    </td>
  </tr>


  <tr align=left valign=top >
    <th style='padding-right: 5px;padding-left:5px; text-align:right' width='%4'>
      <span onmouseover="<%=displayHelper.getScoreMouseOverVocab()%>" onmouseout="nd();">
       &nbsp;&nbsp;<a class="helpPopUp helpCursor" href="#">Score</a>
      </span>
    </th>
    <th style='padding-right: 5px;padding-left:5px;'>Term</th>
    <th style='padding-right: 5px;padding-left:5px;'>Associated Data</th>
    <th style='padding-right: 5px;padding-left:5px;' width='%40' >
      <span onmouseover="<%=displayHelper.getMarkerBestMatchMouseOver()%>" onmouseout="nd();">
       <a class="helpPopUp helpCursor" href="#">Best Match</a>
      </span>
    </th>
  </tr>
<%

  // iterate through the marker results
  int rowCount = 0;
  for (Iterator iter = vocabResultContainer.getHits(vocabStart,vocabStart + vocabRange).iterator(); iter.hasNext();) {

    rowCount++;

    // derive CSS classes and IDs for this row
    rowClass = bucketRowAlternator.getString();

    thisVocabResult = (QS_VocabResult)iter.next();
    thisVocabDisplay = vocabDisplayCache.getVocab(thisVocabResult);

    out.print("<tr class='" + rowClass +"'>");
%>
    <td style='text-align:right;'>
        <%=thisVocabResult.getStarScore()%>
        <% if(debug){out.print(thisVocabResult.getScore());} %>
        <% if(debug){out.print(rowCount);} %>
    </td>
    <td>
<%
    // column 1 (term)
    out.print("<span class='matchDisplayableType small'>"
      + thisVocabDisplay.getTypeDisplay() + "</span> : ");
    if (thisVocabDisplay.getVocabType().equals(IndexConstants.OMIM_TYPE_NAME))
    {
    out.print("<a href='"
        + stConfig.get("JAVAWI_URL") + "WIFetch?page=humanDisease&key="
        + thisVocabDisplay.getDbKey() + "'>"
        + thisVocabDisplay.getName() + "</a>");
    }
    else if (thisVocabDisplay.getVocabType().equals(IndexConstants.AD_TYPE_NAME))
    {
    out.print("<a href='"
        + stConfig.get("WI_URL") + "searches/anatdict.cgi?id="
        + thisVocabDisplay.getDbKey() + "'>"
        + thisVocabDisplay.getName() + "</a>");
    }
    else if (thisVocabDisplay.getVocabType().equals(IndexConstants.GO_TYPE_NAME))
    {
    out.print("<a href='"
        + stConfig.get("WI_URL") + "searches/GO.cgi?id="
        + thisVocabDisplay.getAcc_id() + "'>"
        + thisVocabDisplay.getName() + "</a>");
    }
    else if (thisVocabDisplay.getVocabType().equals(IndexConstants.PIRSF_TYPE_NAME))
    {
    out.print("<a href='"
        + stConfig.get("JAVAWI_URL") + "WIFetch?page=pirsfDetail&key="
        + thisVocabDisplay.getDbKey() + "'>"
        + thisVocabDisplay.getName() + "</a>");
    }
    else if (thisVocabDisplay.getVocabType().equals(IndexConstants.INTERPRO_TYPE_NAME))
    {
      out.print(thisVocabDisplay.getName() + "");
    }
    else
    {
    out.print("<a href='"
        + stConfig.get("WI_URL") + "searches/Phat.cgi?id="
        + thisVocabDisplay.getAcc_id() + "'>"
        + thisVocabDisplay.getName() + "</a>");
    }
    out.print("</td>");

    // column 3 (associations)
    out.print ("<td class='small'>"+displayHelper.vocabAnnotation(thisVocabResult)+"</td>");

    // column 4 (Why match?)
    out.print("<td class='small'><div style='float:left;'>");
    out.print( thisVocabResult.getBestMatch().display() );

    if (debug) {
    out.print("</div><a style='float:right;' href='Search.do?query=" + query + "&page=vocabDetails&vocabKey="
        + thisVocabResult.getDbKey() + "&vocabType="
        + thisVocabResult.getVocabulary()+"'><i>more..</i></a>");
    }
    out.print("</td>");


    out.print("</tr>");

    }
%>
  <tr style="background-color:#dfefff;">
    <td colspan=4>
<%
out.print("<span class='small grayText'> Showing " + (vocabStart) + "-" + (vocabStop) + " of "
    + (vocabResultContainer.size()) + "</span>&nbsp;&nbsp;&nbsp;&nbsp;");
  if (vocabStart.intValue() > 1) {
      out.print("<a class='small' href='Search.do?query=" + query + "&page=vocab&vocabStart="
        + (vocabStart - vocabRange) +"'>Previous</a> ");
  }
  if (vocabStop.intValue() < vocabResultContainer.size()) {
      out.print("<a class='small' href='Search.do?query=" + query + "&page=vocab&vocabStart="
        + (vocabStart + vocabRange) +"'>Next</a>");
  }
%>
    </td>
  </tr>
</table>

<%=webTemplate.getTemplateBodyStopHtml()%>

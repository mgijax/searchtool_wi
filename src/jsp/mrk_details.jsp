<%@ page language="java" contentType="text/html" %>

<%@include file="setup.jsp"%>

<!--============================================ Setup Scriptlet Variables -->
<%
    // get results for this request
    QS_MarkerResultContainer markerResultContainer
      = (QS_MarkerResultContainer)request.getAttribute("MarkerResultContainer");

    // derive needed data from passed request arrributes
    String markerKey = searchInput.getParameter("markerKey");
    QS_MarkerResult thisMarkerResult
      = markerResultContainer.getMarkerByKey(markerKey);
    MarkerDisplay thisMarkerDisplay
      = markerDisplayCache.getMarker(thisMarkerResult);

    // matches for this marker result
    List nomenMatches = thisMarkerResult.getAllMarkerNomenMatches();
    List vocabMatches = thisMarkerResult.getAllMarkerVocabMatches();

    // colors and color iteration
    String rowClass = "";
    StringAlternator bucketRowAlternator
        = new StringAlternator( "qsBucketRow1", "qsBucketRow2" );

    // misc values used in scriptlets below
    String matchScore = "";
%>

<!--======================================================== Open the Page -->
<%=webTemplate.getTemplateHeadHtml()%>
<script>
</script>
<%=webTemplate.getTemplateBodyStartHtml()%>


<!--========================================================== Page Header -->
<div id="detailTitleBarWrapper">
  <span class="fontSize22 fontBold">
    All Matches for <%=thisMarkerDisplay.getSymbol()%>
  </span>
</div>

<%=displayHelper.getWhyMatchSearchDetails(searchInput)%>

<!--========================================================== Marker Info -->

<table bgcolor="#DFEFFF" cellspacing="2" width="100%">
<tr align="left" valign="top">
  <td class="whyMatchData" align="right" bgcolor="#DFEFFF" width="8%">
    <span class="drkBlueText fontSize18 fontBold">Symbol</span><br>
    <span class="drkBlueText fontBold">Name</span><br>
    <span class="drkBlueText fontBold">ID</span>
  </td>
  <td class="whyMatchData" bgcolor="#ffffff">
    <span class="fontSize18 fontBold">
      <a href="<%=javawi_url%>WIFetch?page=markerDetail&amp;key=<%=thisMarkerDisplay.getDbKey()%>">
      <%=thisMarkerDisplay.getSymbol()%></a>
    </span>
    <br/>
    <span class="fontBold"><%=thisMarkerDisplay.getName()%></span>
    <br/>
    <span class="fontBold"><%=thisMarkerDisplay.getMgiId()%></span>
  </td>
</tr>
</table>


<!--======================================================== Nomen Matches -->
<% if (nomenMatches.size() != 0) {
%>

<!--
<div class="whyMatchSectionHeader2" style="margin-bottom:4px;">
Symbol, Name, Synonym and Accession ID matches to your query
</div>
-->

<br/><table cellspacing="1" class="qsBucket" width="100%" border="0">

<tr>
  <td colspan='2' class='qsBucketHeader'> Nomenclature or ID
    <span class='small grayText'>
      Symbol, Name, Synonym and Accession ID matches to your query for this genome feature.
    </span>
  </td>
</tr>

<tr>
  <th style='padding-right:5px; padding-left:5px; text-align:right' width='%6'>
    <span onmouseover="<%=displayHelper.getScoreMouseOverMarker()%>" onmouseout="nd();">
     &nbsp;&nbsp;<a class="helpPopUp helpCursor" href="#">Score</a>
    </span>
  </th>
  <th style='padding-left:15px;' width="94%">
    Nomenclature or ID

  </th>
</tr>

<% // generate a row for each nomen match
for (Iterator iter = nomenMatches.iterator(); iter.hasNext();) {
  MarkerMatch thisMatch = (MarkerMatch)iter.next();
  rowClass = bucketRowAlternator.getString();
  if (debug) {matchScore="(" + thisMatch.getScore().toString() + ")";}
%>
  <tr class="<%=rowClass%>">
  <td style='text-align:right;'>
    <%=displayHelper.getMatchStarScore(thisMatch)%>
    <%=matchScore%>
  </td>
  <td style='padding-left:15px;'>
    <span class="small matchDisplayableType">
      <%=thisMatch.getDisplayableType()%>
    </span>
    : <%=displayHelper.superscript( thisMatch.getMatchedText() )%>
    <%=thisMatch.getProvider()%>
  </td>
  </tr>
<% }%>
</table>
<%}%>

<!--======================================================== Vocab Matches -->
<% if (vocabMatches.size() != 0) {
%>
<br/><table cellspacing="1" class="qsBucket" width="100%" border="0">

<tr>
  <td colspan='2' class='qsBucketHeader'> Vocabulary Terms
    <span class='small grayText'>
      Term, Synonym, and Definition matches to your query that are associated with this genome feature.
    </span>
  </td>
</tr>


<tr>
  <th style='padding-right:5px; padding-left:5px; text-align:right' width='%6'>
    <span onmouseover="<%=displayHelper.getScoreMouseOverMarker()%>" onmouseout="nd();">
     &nbsp;&nbsp;<a class="helpPopUp helpCursor" href="#">Score</a>
    </span>
  </th>
  <th style='padding-left:15px;' width="94%">
    Annotated Term
  </th>
</tr>

<% // generate a row for each nomen match
for (Iterator iter = vocabMatches.iterator(); iter.hasNext();) {
  MarkerVocabMatch thisMatch = (MarkerVocabMatch)iter.next();
  VocabDisplay vocDisplay = vocabDisplayCache.getVocab(thisMatch);
  VocabDisplay parentVocDisplay = vocabDisplayCache.getParentVocab(thisMatch);
  rowClass = bucketRowAlternator.getString();
  if (debug) {matchScore="(" + thisMatch.getScore().toString() + ")";}
%>
  <tr class="<%=rowClass%>">
  <td style='text-align:right;'>
    <%=displayHelper.getMatchStarScore(thisMatch)%>
    <%=matchScore%>
  </td>
  <td style='padding-left:15px;'>

    <% // we need to indicate child matches as subterm hits
    if (thisMatch.isChildMatch())
    {%>
      <span class="small matchDisplayableType">
        <%=thisMatch.getDisplayableType()%>
      </span>
      : <%=vocDisplay.getName()%>
      <br/>
      <span class="small fontItalic">
        &nbsp;&nbsp;&nbsp;&nbsp;a subterm of
      </span>
      <span class="small">
        : <%=parentVocDisplay.getName()%>
      </span>

      <% // Acc IDs matches get ID appended
      if (thisMatch.isAccID())
      {%>
        <span class="small">
          (<%= parentVocDisplay.getAcc_id()%>)
        </span>
      <%
      }%>

    <%
    }else { // if (thisMatch.isChildMatch())
    %>
      <span class="small matchDisplayableType">
        <%=thisMatch.getDisplayableType()%>
      </span>
      : <%=vocDisplay.getName()%>

        <%
        if (thisMatch.isAccID())
        {%>
            (<%= vocDisplay.getAcc_id()%>)
        <%
        }%>
    <%
    }%>

    <% // for synonyms, we need display it
    if ( thisMatch.getDataType().equals("VY") )
    {%>
      </br>
      <span class="small fontItalic">
         &nbsp;&nbsp;&nbsp;&nbsp;from its synonym
       </span>
       <span class="small">
        : <%=thisMatch.getMatchedText()%>
      </span>
    <%}%>

    <% // for notes, we need display it
    if ( thisMatch.getDataType().equals("VN") )
    {%>
      </br>
      <span class="small fontItalic">
       &nbsp;&nbsp;&nbsp;&nbsp;from its definition
      </span>
      <span class="small">
       : <%=thisMatch.getMatchedText()%>
      </span>
    <%}%>

  </td>
  </tr>
 <%} // for vocabMatches.iterator() close
 %>
</table>
<%}%>


<!--=========================================================== Close Page -->
<%=webTemplate.getTemplateBodyStopHtml()%>

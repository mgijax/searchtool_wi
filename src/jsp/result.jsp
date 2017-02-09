
<%@include file="setup.jsp"%>

<!--============================================ Setup Scriptlet Variables -->
<%
  // pull in the result containers from the request object
  GenomeFeatureResultContainer genomeFeatureResultContainer = (GenomeFeatureResultContainer)request.getAttribute("GenomeFeatureResultContainer");
  VocabResultContainer vocabResultContainer = (VocabResultContainer)request.getAttribute("VocabResultContainer");
  OtherResultContainer otherResultContainer = (OtherResultContainer)request.getAttribute("OtherResultContainer");

  // display lenghts
  int displayLengthMarker = 10;
  int displayLengthVocab = 10;
  int markerResultLength = genomeFeatureResultContainer.size();
  int vocabResultLength = vocabResultContainer.size();
  String markerResultSizeStr = displayHelper.commaFormatIntStr(String.valueOf( markerResultLength ));
  String vocabResultSizeStr = displayHelper.commaFormatIntStr(String.valueOf( vocabResultLength ));

  boolean needMoreMarkerLink = true;
  boolean needMoreVocabLink = true;
  if (markerResultLength < displayLengthMarker) {
      displayLengthMarker = markerResultLength;
      needMoreMarkerLink = false;
  }

  if (vocabResultLength < displayLengthVocab) {
      displayLengthVocab = vocabResultLength;
      needMoreVocabLink = false;
  }

  // Get the number to display in the more link for markers
  int markerMoreLinkNumber;
  if (genomeFeatureResultContainer.size() > 100) {
    markerMoreLinkNumber = 100;
  }
  else {
    markerMoreLinkNumber = genomeFeatureResultContainer.size();
  }

  // Get the number to display in the more link for vocab
  int vocabMoreLinkNumber;
  if (vocabResultContainer.size() > 100) {
    vocabMoreLinkNumber = 100;
  }
  else {
    vocabMoreLinkNumber = vocabResultContainer.size();
  }

  // colors and color iteration
  StringAlternator bucketRowAlternator
    = new StringAlternator( "qsBucketRow1", "qsBucketRow2" );
  String rowClass;

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

<!--================================================ Genome Feature Bucket -->
<%
  GenomeFeatureResult thisGenomeFeatureResult;   //search result
  GenomeFeatureDisplay thisGenomeFeatureDisplay; //pulled from cache

%>
<!-- Header Row -->

<table class='qsBucket' width='98%' border=0>
<tr>
  <td colspan='8' class='qsBucketHeader'>Genome Features
<%
  if (displayLengthMarker != 0) {
    out.print("<span class='small grayText'> sorted by best match, showing 1-");
    out.print(displayLengthMarker + " of " + markerResultSizeStr + "</span>");
  }
  else {
    out.print("<span class='small grayText'> no results</span>");
  }
%>
  <span class="helpCursor" onmouseover="<%=displayHelper.getHelpPopupMarkerBucket()%>" onmouseout="nd();">
       <img src="<%=stConfig.get("QUICKSEARCH_URL")%>blue_info_icon.gif" border="0"/>
  </span>
  </td>
</tr>

<% // display bucket only if we have marker results
  if (displayLengthMarker != 0) { %>

  <tr align=left valign=top >
    <th style='padding-right: 5px;padding-left:5px; text-align:right' width='4%'>
      <span onmouseover="<%=displayHelper.getScoreMouseOverMarker()%>" onmouseout="nd();">
       &nbsp;&nbsp;<a class="helpPopUp helpCursor" href="#">Score</a>
      </span>
    </th>
    <th style='padding-right:5px; padding-left:5px;'>Type</th>
    <th style='padding-right:5px; padding-left:5px;'>Symbol</th>
    <th style='padding-right:5px; padding-left:5px;'>Name</th>
    <th style='padding-right:5px; padding-left:5px; text-align:right;'>Chr</th>

    <th style='padding-right:5px; padding-left:5px;'>Location</th>
    <th style='padding-right:5px; padding-left:5px;'>Str</th>

    <th style='padding-right:5px; padding-left:5px;' width='40%' >
      <span onmouseover="<%=displayHelper.getMarkerBestMatchMouseOver()%>" onmouseout="nd();">
       <a class="helpPopUp helpCursor" href="#">Best Match</a>
      </span>
    </th>
  </tr>

<% // iterate through the marker results
    for (Iterator iter = genomeFeatureResultContainer.getTopHits(displayLengthMarker).iterator(); iter.hasNext();)
  {
    thisGenomeFeatureResult    = (GenomeFeatureResult)iter.next();
    thisGenomeFeatureDisplay   = gfDisplayCache.getGenomeFeature(thisGenomeFeatureResult);
    rowClass = bucketRowAlternator.getString();

    if ( thisGenomeFeatureResult.isMarker() ) {
      detailPageUrl = fewi_url + "marker/" + thisGenomeFeatureDisplay.getMgiId();
    }
    else if ( thisGenomeFeatureResult.isAllele() ) {
      detailPageUrl = fewi_url + "allele/key/" + thisGenomeFeatureResult.getDbKey();
    }

%>
  <tr class='<%=rowClass%>'>
    <td style='text-align:right;'>
        <%=thisGenomeFeatureResult.getStarScore()%>
        <% if(debug){out.print(thisGenomeFeatureResult.getDebugDisplay());} %>
    </td>
    <td class='small' >
        <%=thisGenomeFeatureDisplay.getMarkerType()%>
    </td>
    <td>
      <a href='<%=detailPageUrl%>'>
        <%=DisplayHelper.superscript(thisGenomeFeatureDisplay.getSymbol())%>
      </a>
    </td>
    <td>
      <%=DisplayHelper.superscript(thisGenomeFeatureDisplay.getName())%>
    </td>
    <td class='small' style='text-align:right'>
      <%=thisGenomeFeatureDisplay.getChromosome()%>
    </td>
    <td class='small'>
      <%=thisGenomeFeatureDisplay.getLocDisplay()%>
    </td>
    <td class='small'>
      <%=thisGenomeFeatureDisplay.getStrand()%>
    </td>

    <td class='small'>
      <%=thisGenomeFeatureResult.getBestMatch().display()%>

      <a class="qsWhyMatchLink"
        <%=displayHelper.getMarkerScoreMouseOver(thisGenomeFeatureResult)%>
        href=<%=displayHelper.getMrkWhyMatchURL(thisGenomeFeatureResult, queryForward)%> >
        <%=displayHelper.getMrkWhyMatchText(thisGenomeFeatureResult)%>
      </a>

      <% if (debug) { %>
        <br/>match type -> <%=thisGenomeFeatureResult.getBestMatch().getDataType()%>
        <br/>match db key -> <%=thisGenomeFeatureResult.getBestMatch().getDbKey()%>
        <br/>score -> <%=thisGenomeFeatureResult.getBestMatch().getScore()%>
      <% } %>
    </td>
  </tr>
<% } /* for each result */ %>

<% } /* if we have marker results */ %>

  <tr style="background-color:#dfefff;">
    <td colspan=5>

<%
  if (displayLengthMarker != 0) {
    out.print("<span class='small grayText'> Showing 1-");
    out.print(displayLengthMarker + " of " + markerResultSizeStr + "</span>");
  }
%>

    <% if (needMoreMarkerLink) { %>
      &nbsp;&nbsp;&nbsp;
      <a href="Search.do?query=<%=displayHelper.getEncodedUrl(queryForward)%>&page=featureList">Show
      <% if (markerMoreLinkNumber < 100) {
            %>all<%
         }
         else {
            %>first<%
         }
      %> <%=markerMoreLinkNumber%>...</a>
    <% } else { %>&nbsp;<% }%>
    </td>
    <td colspan=3>
      <div style='float:right; display:none;' id='bqf'>
      <%if (displayLengthMarker != 0) { %>
         <%@include file="batchQueryForwarding.jsp"%>
      <% } %>
      </div>
      <script>document.getElementById('bqf').style.display = 'block';</script>
    </td>
  </tr>

</table>



<br/>

<!--========================================================= Vocab Bucket -->
<%

  bucketRowAlternator.reset();

  // table header
  out.print("<table class='qsBucket' width='98%'><tr>");
  out.print("<td colspan='4' class='qsBucketHeader'>");
  out.print("Vocabulary Terms");
  if (vocabResultLength != 0){
    out.print("<span class='small grayText'> sorted by best match, showing 1-");
    out.print(displayLengthVocab + " of " + vocabResultSizeStr + "</span>");
  }
  else{
    out.print("<span class='small grayText'> no results");
  }
%>
  <span onmouseover="<%=displayHelper.getHelpPopupVocabBucket()%>" onmouseout="nd();">
    <a class="helpCursor" href="#"><img src="<%=stConfig.get("QUICKSEARCH_URL")%>blue_info_icon.gif" border="0"/></a>
  </span>
<%
  out.print("</td></tr>");

  // table column headings
  if (vocabResultLength != 0)
  {
%>
  <tr align=left valign=top >
    <th style='padding-right: 5px;padding-left:5px; text-align:right; width:4%' >
      <span onmouseover="<%=displayHelper.getScoreMouseOverVocab()%>" onmouseout="nd();">
       &nbsp;&nbsp;<a class="helpPopUp helpCursor" href="#">Score</a>
      </span>
    </th>
    <th style='padding-right:5px; padding-left:5px;'>Term</th>
    <th style='padding-right:5px; padding-left:5px;'>Associated Data</th>
    <th style='padding-right:5px; padding-left:5px; width:40%;'>
      <span onmouseover="<%=displayHelper.getVocabBestMatchMouseOver()%>"
      onmouseout="nd();">
      <a class="helpPopUp helpCursor" href="#">Best Match</a>
      </span>
    </th>
  </tr>
<%
    for (Iterator iter = vocabResultContainer.getTopHits(displayLengthVocab).iterator(); iter.hasNext();) {

        // derive CSS classes and IDs for this row
        rowClass = bucketRowAlternator.getString();

        VocabResult thisVocabResult = (VocabResult)iter.next();
        VocabDisplay thisVocabDisplay = vocabDisplayCache.getVocab(thisVocabResult);

        out.print("<tr class='" + rowClass +"'>");
%>
    <td style='text-align:right;'>
        <%=thisVocabResult.getStarScore()%>
        <% if(debug){out.print(thisVocabResult.getDebugDisplay());} %>
    </td>
    <td>
<%
        // column 1 (term)
        out.print("<span class='matchDisplayableType small'>"
          + thisVocabDisplay.getTypeDisplay() + "</span>: ");
        if (thisVocabDisplay.getVocabType().equals(IndexConstants.DO_DATABASE_TYPE))
        {
        out.print("<a href='"
            + fewi_url + "disease/"
            + thisVocabDisplay.getAcc_id() + "'>"
            + thisVocabDisplay.getName() + "</a>");
        }
        else if (thisVocabDisplay.getVocabType().equals(IndexConstants.EMAPA_TYPE_NAME))
        {
        out.print("<a href='"
            + stConfig.get("FEWI_URL") + "vocab/gxd/anatomy/"
            + thisVocabDisplay.getAcc_id() + "'>"
            + thisVocabDisplay.getName() + "</a>");
        }
        else if (thisVocabDisplay.getVocabType().equals(IndexConstants.EMAPS_TYPE_NAME))
        {
        out.print("<a href='"
            + stConfig.get("FEWI_URL") + "vocab/gxd/anatomy/"
            + thisVocabDisplay.getAcc_id() + "'>"
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
            + stConfig.get("FEWI_URL") + "vocab/pirsf/"
            + thisVocabDisplay.getAcc_id() + "'>"
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
        out.print("</div><a style='float:right;' href='Search.do?query="
          + displayHelper.getEncodedUrl(queryForward) + "&page=vocabDetails&vocabKey="
          + thisVocabResult.getDbKey() + "&vocabType="
          + thisVocabResult.getVocabulary()+"'><i>more..</i></a>");
        }
        out.print("</td>");


        out.print("</tr>");

    }
  }
  %>
  <tr style="background-color:#dfefff;">
        <td colspan=4>
        <% if (needMoreVocabLink) { %>
          &nbsp;&nbsp;&nbsp;<a href="Search.do?query=<%=displayHelper.getEncodedUrl(queryForward)%>&page=vocab">Show
          <%
          if (vocabMoreLinkNumber < 100) {
            %>all<%
          }
          else {
            %>first<%
          }

          %> <%=vocabMoreLinkNumber%>...</a>
        <% } else {%>&nbsp;<%} %>
        </td>
  </tr>
  <% out.print("</table>"); %>
<!--========================================================= Other Bucket -->
<%
  // table header
  out.print("<br/>");
  out.print("<table class='qsBucket' width='98%'><tr>");
  out.print("<td colspan='3' class='qsBucketHeader'>");
  out.print("Other Results By ID");
  if (otherResultContainer.size() != 0) {
    out.print("<span class='small grayText'> sorted by best match, showing 1-");
    out.print(otherResultContainer.size() + "</span>");
  }
  else {
    out.print("<span class='small grayText'> no results");
  }

  %>
  <span onmouseover="<%=displayHelper.getHelpPopupOtherBucket()%>" onmouseout="nd();">
       <a class="helpCursor" href="#"><img src="<%=stConfig.get("QUICKSEARCH_URL")%>blue_info_icon.gif" border="0"/></a>
  </span>
  </td>
  </tr>
  <%
  if (otherResultContainer.size() != 0) {
    // table column headings
    out.print("<tr align=left valign=top >");
    out.print("<th>Type</th>");
    out.print("<th>Name/Description</th>");
    out.print("<th>Why did this match?</th>");

    bucketRowAlternator.reset();

    for (Iterator iter = otherResultContainer.getTopHits(otherResultContainer.size()).iterator(); iter.hasNext();) {

        // derive CSS classes and IDs for this row
        rowClass = bucketRowAlternator.getString();

        OtherResult thisOtherResult = (OtherResult)iter.next();
        OtherDisplay thisOtherDisplay = otherDisplayLookup.getOther(thisOtherResult);

        out.print("<tr class='" + rowClass +"'>");

        // column 1 (term)
        String data_type = thisOtherResult.getDataType();

        if (data_type.equals(IndexConstants.OTHER_PROBE)) {
            out.print("<td><a href='"+stConfig.get("FEWI_URL")+"probe/key/" +thisOtherResult.getDbKey()+"'>Probe/Clone</a>");
            if (!thisOtherDisplay.getQualifier1().equals("")){
            	out.print(", "+ thisOtherDisplay.getQualifier1());
            }

            out.print("</td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_REFERENCE)) {
            out.print("<td><a href='"+fewi_url+"reference/summary?id=" +thisOtherResult.getAccId()+"'>Reference</a></td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_SEQUENCE)) {
            out.print("<td><a href='"+fewi_url+"sequence/key/" +thisOtherResult.getDbKey()+"'>Sequence</a>");
            if (!thisOtherDisplay.getQualifier1().equals("")) {
	        	out.print(", "+ thisOtherDisplay.getQualifier1());
	        }
            out.print("</td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_ALLELE)) {
            out.print("<td><a href='"+fewi_url+"allele/key/" +thisOtherResult.getDbKey()+"'>Allele</a>");
            if (!thisOtherDisplay.getQualifier1().equals("")) {
        	   out.print(", "+ thisOtherDisplay.getQualifier1());
            }
            out.print("</td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_ANTIBODY)) {
            out.print("<td><a href='"+fewi_url+"antibody/key/" +thisOtherResult.getDbKey()+"'>Antibody</a></td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_ORTHOLOG)) {
            out.print("<td><a href='"+fewi_url+"homology/key/" +thisOtherResult.getDbKey()+"'>Homolog</a></td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_GENOTYPE)) {
            out.print("<td><a href='"+fewi_url+"accession/" +thisOtherResult.getAccId().toUpperCase()+"'>Genotype</a></td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_SNP)) {
            out.print("<td><a href='"+fewi_url+"snp/" +thisOtherResult.getAccId()+"'>SNP</a></td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_HOMOLOGY)) {
            out.print("<td><a href='"+fewi_url+"homology/" +thisOtherResult.getDbKey()+"'>Homology</a></td>");
        }
        // Map Data is VERy odd, the qualifier is needed when creating the URL, so.. it HAS to be there.

        else if (data_type.equals(IndexConstants.OTHER_EXPERIMENT)) {
            out.print("<td><a href='"+stConfig.get("WI_URL")+"searches/mapdata.cgi?" +thisOtherResult.getDbKey()+"/"+thisOtherDisplay.getQualifier1()+"'>Mapping Experiment</a>");
       	    out.print(", "+ thisOtherDisplay.getQualifier1());
		    out.print("</td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_IMAGE)) {
            out.print("<td><a href='"+fewi_url+"image/" +thisOtherResult.getAccId()+"'>Expression Image</a></td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_ASSAY)) {
            out.print("<td><a href='"+fewi_url+"assay/" + thisOtherResult.getAccId().toUpperCase()  +"'>Expression Assay</a></td>");
        }
        else if (data_type.equals(IndexConstants.OTHER_AMA)) {
            out.print("<td><a href='"+stConfig.get("WI_URL")+"searches/AMA.cgi?id=" +thisOtherResult.getAccId().toUpperCase()+"'>AMA Browser Detail</a></td>");
        }

        else if (data_type.equals(IndexConstants.OTHER_ESCELL)) {
            out.print("<td><a href='"+fewi_url+"allele/key/" +thisOtherResult.getDbKey()+"'>Allele</a>");
            if (!thisOtherDisplay.getQualifier1().equals("")) {
                out.print(", "+ thisOtherDisplay.getQualifier1());
            }
            out.print("</td>");
        }

		out.print("<td>");
        // column 2 (Object dbkey)
        if (!thisOtherDisplay.getName().equals("")) {
            out.print(DisplayHelper.superscript(thisOtherDisplay.getName()));
        }
        else {
            out.print(thisOtherResult.getAccId());
        }

        out.print("</td>");

        // column 3 (Why did this match, accid)
        out.print("<td class='small'>");
        //out.print("ID: ");

        OtherMatch oem = (OtherMatch)thisOtherResult.getBestMatch();


        out.print (oem.display());
/*        out.print( oem.getDisplayableType() +": " + oem.getMatchedText());

        if (!oem.getLogicalDb().equals("") && !oem.getLogicalDb().equals("MGI")) {
            out.print(" ("+oem.getLogicalDb()+")");
        }*/

        out.print("</td>");

        out.print("</tr>");
    }
  }

  %>
  <tr style="background-color:#dfefff;">
    <td colspan=3>
    &nbsp;
    </td>
  </tr>
</table>
<!--======================================================== Search Google -->
</br></br>
<table class='qsBucket' width='98%'>
<tr>
  <td class='qsBucketHeader'>Search MGI with Google
  <span onmouseover="<%=displayHelper.getHelpPopupGoogleBucket()%>" onmouseout="nd();">
       <a class="helpCursor" href="#"><img src="<%=stConfig.get("QUICKSEARCH_URL")%>blue_info_icon.gif" border="0"/></a>
  </span>
  </td>
</tr>
<tr>
  <td>
    <form method="get" action="http://www.google.com/search">
      <input type="text" name="q" size="25" maxlength="255" value="<%=query%>" />
      <input type="submit" value="Search" class="buttonLabelKLF"  />
      <input type="hidden"  name="sitesearch" value="informatics.jax.org" />
    </form>
  </td>
</tr>
</table>
<%=webTemplate.getTemplateBodyStopHtml()%>

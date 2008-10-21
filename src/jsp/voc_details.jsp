<%@include file="setup.jsp"%>

<%
    QS_VocabResultContainer vocabResultContainer
      = (QS_VocabResultContainer)request.getAttribute("VocabResultContainer");

    String vocabKey = searchInput.getParameter("vocabKey");
    String vocabType = searchInput.getParameter("vocabType");

    QS_VocabResult vocabResult;

    if (vocabType.equals(IndexConstants.GO_TYPE_NAME))
    {
    	vocabResult = vocabResultContainer.getGoByKey(vocabKey);
    }
    else if (vocabType.equals(IndexConstants.OMIM_TYPE_NAME))
    {
    	vocabResult = vocabResultContainer.getOmimByKey(vocabKey);
    }
    else if (vocabType.equals(IndexConstants.AD_TYPE_NAME))
    {
    	vocabResult = vocabResultContainer.getAdByKey(vocabKey);
    }
    else if (vocabType.equals(IndexConstants.PIRSF_TYPE_NAME))
    {
        vocabResult = vocabResultContainer.getPsByKey(vocabKey);
    }
    else if (vocabType.equals(IndexConstants.INTERPRO_TYPE_NAME))
    {
        vocabResult = vocabResultContainer.getIpByKey(vocabKey);
    }

    else
    {
    	vocabResult = vocabResultContainer.getMpByKey(vocabKey);
    }

%>

<%=webTemplate.getTemplateHeadHtml()%>
<script>
</script>
<%=webTemplate.getTemplateBodyStartHtml()%>

<div id="titleBarWrapper" userdoc="searchtool_help.shtml">
  <span class="titleBarMainTitle">
    Vocab QuickSearch Results
  </span>
</div>

<B>Vocab Detailed Matching Information</B>

<%
  out.print ("<br><br>" + displayHelper.wholeNormalQuery(searchInput));
  out.print ("<br><br>key: " + vocabResult.getDbKey() + " type: "
    + vocabResult.getVocabulary() + "<br/>Total score: " + vocabResult.getScore());

  List exactMatches = vocabResult.getExactMatches();
  out.print("<br/><br/><b>Exact Matches:</b> <br/>");
  for (Iterator iter = exactMatches.iterator(); iter.hasNext();)
  {
    VocabMatch match = (VocabMatch) iter.next();
    out.print ("<br/>--------------------------<br/>Term Type: " + match.getDataType()
      + "<br/>Matched Text: " + match.getMatchedText()
      + "<br/>Lucene Score: " + match.getSearchScore()
      + "<br/>Additive Score: " + match.getAdditiveScore()
      + "<br/>Derived Score: " + match.getScore());
  }


  List matches = vocabResult.getInexactMatches();
  out.print("<br/><br/><b>Inexact Matches: </b><br/>");
  for (Iterator iter = matches.iterator(); iter.hasNext();)
  {
    VocabMatch match = (VocabMatch) iter.next();
    out.print ("<br/>--------------------------<br/>Term Type: " + match.getDataType()
      + "<br/>Matched Text: " + match.getMatchedText()
      + "<br/>Lucene Score: " + match.getSearchScore()
      + "<br/>Additive Score: " + match.getAdditiveScore()
      + "<br/>Derived Score: " + match.getScore());
  }
%>

<br/>

<%=webTemplate.getTemplateBodyStopHtml()%>

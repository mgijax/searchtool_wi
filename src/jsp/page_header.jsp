<table border=0>
<tr>
<td width="1%">
  <span onmouseover="<%=displayHelper.getHelpIconPopup()%>" onmouseout="nd();">
  <a href="<%=userdocs_url%>searchtool_help.shtml">
    <img src="<%=webshare_url%>/images/help_small_transp.gif" alt="help" border="0">
  </a>
  </span>
</td>

<td width="24%">&nbsp;</td>

<td>
<span id="qsPageHeader">
  <form name="qs_submit" method="GET" action="Search.do"
    onSubmit="return validateQsInput(document.qs_submit)">
    <h3>Quick Search Results</h3> for:
    <input id="qsTextField" type="text" name="query" size="30" value="<%=query%>"></input>&nbsp;
    <input class="qsButton" type="submit" name="submit" value="Search Again">&nbsp;
    <!-- A Styled reset form button, which is NOT the same as a clear button
    <input class="qsButton" type="reset" name="reset" value="Reset">&nbsp; -->
  </form>
  <span class="qsButton" onclick="javascript:clearQuickSearchForm();">
    Reset
  </span>
  <span class="qsButton" style="margin-left:40px;"
    onClick='window.open("<%=mgihome_url%>feedback/feedback_form.cgi?subject=Quick Search")'>
    Your Input Welcome
  </span>


</span>
</td>

<td width="24%">&nbsp;</td>

<td width="10%">
</td>
</tr>
</table>

<div id="qsExamples">Examples:&nbsp;&nbsp;embry*&nbsp;develop*&nbsp;&nbsp;&nbsp;&nbsp;NM_013627&nbsp;&nbsp;&nbsp;&nbsp;Fas&lt;lpr&gt;&nbsp;&nbsp;&nbsp;&nbsp;Pax*&nbsp;&nbsp;&nbsp;&nbsp;axial&nbsp;"skeletal&nbsp;dysplasia"&nbsp;&nbsp;&nbsp;&nbsp;Tg(Igh-6-cre/ESR1)30Afst &nbsp;&nbsp;&nbsp;&nbsp;</div>

<!-- Search Details Link -->
<%=displayHelper.getSearchDetailOnClick(searchInput)%>


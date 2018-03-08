<%@include file="setup.jsp"%>

<%
QuickSearchException qse = (QuickSearchException)request.getAttribute("QuickSearchException");
String searchString = (String) request.getParameter("query");
%>

<%=webTemplate.getTemplateHeadHtml()%>
<script>
</script>
<%=webTemplate.getTemplateBodyStartHtml()%>

<br/>
Sorry, Quick Search cannot return all of the results for this search term.
<br/>
<br/>
You can search for <%= searchString %> using the <a href='<%= stConfig.get("FEWI_URL") %>marker/summary?nomen=<%= searchString %>'>Genes &amp; Markers Query Form</a>.
<br/>
<br/>
Please contact <a href='<%= mgihome_url %>support/mgi_inbox.shtml'>User Support</a> if you have any questions.
<br/>
<br/>
<%=qse.getErrorDisplay()%>

<br/>
<br/>
<%=webTemplate.getTemplateBodyStopHtml()%>

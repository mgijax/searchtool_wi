<%@include file="setup.jsp"%>

<%
QuickSearchException qse = (QuickSearchException)request.getAttribute("QuickSearchException");
%>

<%=webTemplate.getTemplateHeadHtml()%>
<script>
</script>
<%=webTemplate.getTemplateBodyStartHtml()%>

<br/>
Sorry - Quick Search cannot proceed.

<br/>
<br/>
<%=qse.getErrorDisplay()%>

<br/>
<br/>
<%=webTemplate.getTemplateBodyStopHtml()%>

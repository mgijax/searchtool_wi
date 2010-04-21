<script>
var symbolArray = new Array(<%=genomeFeatureResultContainer.getStringOfSymbols()%>);
function updateBatchWebForwardForm()
{
    var batchForwardSize = document.getElementById ("batchForwardSize").value;
    var requestedSymbolStr = symbolArray.slice(0, batchForwardSize).join(", ");
    document.getElementById ("batchSymbolListWeb").value = requestedSymbolStr;
};
function updateBatchTabForwardForm()
{
    var batchForwardSize = document.getElementById ("batchForwardSize").value;
    var requestedSymbolStr = symbolArray.slice(0, batchForwardSize).join(", ");
    document.getElementById ("batchSymbolListTab").value = requestedSymbolStr;
};
</script>

<%  // Determine the max number to forward
  int numToForward;
  if (genomeFeatureResultContainer.size() > 100) {
    numToForward = 100;
  }
  else {
    numToForward = genomeFeatureResultContainer.size();
  }
%>

<form style='display:inline;' name='batchWeb' enctype='multipart/form-data' target='_blank'
  method='post' action='<%=javawi_url%>WIFetch'>
<input name='page' value='batchSummary' type='hidden'>
<input name='params' value='show' type='hidden'>
<input name='IDType' value='Symbol' type='hidden'>
<input name='returnSet' value='Nomenclature' type='hidden'>
<input name='returnSet' value='Location' type='hidden'>
<input name='returnRad' value='None' type='hidden'>
<input name='printFormat' value='toolbar' type='hidden'>
<input name='column' value='1' type='hidden'>
<input name='IDSet' value='' id='batchSymbolListWeb' type='hidden'>
<span onClick='JAVASCRIPT:updateBatchWebForwardForm();batchWeb.submit();'
  class='qsButton'> Get more data </span>
</form>
<span class='small grayText'>
&nbsp; for genome features 1 through &nbsp;
</span>
<input id="batchForwardSize" type="text" size="5" value='  <%=numToForward%>'></input>&nbsp;

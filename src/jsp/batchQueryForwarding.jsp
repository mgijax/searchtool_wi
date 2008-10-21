<script>
var symbolArray = new Array(<%=markerResultContainer.getStringOfSymbols()%>);
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

<span class='small grayText'>
Get more data for genome features 1 through
</span>
<input id="batchForwardSize" type="text" size="5" value='  100'></input>&nbsp;
<span class='small grayText'>
in
</span>
<form style='display:inline;' name='batchWeb' enctype='multipart/form-data' target='_blank'
  method='post' action='<%=javawi_url%>WIFetch'>
<input name='page' value='batchSummary' type='hidden'>
<input name='IDType' value='Symbol' type='hidden'>
<input name='returnSet' value='Nomenclature' type='hidden'>
<input name='returnSet' value='Location' type='hidden'>
<input name='returnSet' value='Ensembl' type='hidden'>
<input name='returnSet' value='EntrezGene' type='hidden'>
<input name='returnSet' value='VEGA' type='hidden'>
<input name='returnRad' value='None' type='hidden'>
<input name='printFormat' value='toolbar' type='hidden'>
<input name='column' value='1' type='hidden'>
<input name='IDSet' value='' id='batchSymbolListWeb' type='hidden'>
<span onClick='JAVASCRIPT:updateBatchWebForwardForm();batchWeb.submit();'
  class='qsButton'>Web Format</span>
</form>
&nbsp;
<form style='display:inline;' name='batchTab' enctype='multipart/form-data' target='_blank'
  method='post' action='<%=javawi_url%>WIFetch'>
<input name='page' value='batchSummary' type='hidden'>
<input name='IDType' value='Symbol' type='hidden'>
<input name='returnSet' value='Nomenclature' type='hidden'>
<input name='returnSet' value='Location' type='hidden'>
<input name='returnSet' value='Ensembl' type='hidden'>
<input name='returnSet' value='EntrezGene' type='hidden'>
<input name='returnSet' value='VEGA' type='hidden'>
<input name='returnRad' value='None' type='hidden'>
<input name='printFormat' value='dataDisplay' type='hidden'>
<input name='column' value='1' type='hidden'>
<input name='IDSet' value='' id='batchSymbolListTab' type='hidden'>
<span onClick='JAVASCRIPT:updateBatchTabForwardForm();batchTab.submit();'
  class='qsButton'>Tab Format</span>
</form>

// searchTool.js

// making something visible via it's ID
function showID(thisId){
  obj = document.getElementById(thisId);
  obj.style.visibility = "visible";
};

// Validate the user's imput string; client-side checking
function validateQsInput(formObj) {

  var queryText = formObj.query.value;
  queryText = queryText.replace(/^\s+|\s+$/g, '') ;

  var tokenCount = queryText.split(' ').length;
  var quoteCount = 0;
  
  // count the number of double-quotes
  for (var i=0; i < queryText.length; i++)
  {
    thisChar = "" + queryText.substring(i, i+1);
    if (thisChar == "\"")
    {
      quoteCount++;
    }
  }

  // generate popup notifying user
  if (formObj.query.value.length == 0)
    alert("Your search did not contain anything.  Please enter up to 32 words, IDs, or other text items and try again.");
  else if (tokenCount > 32)
    alert("Quick Search is limited to 32 words, IDs, or other search items.  Please edit your search to contain 32 items or fewer.");
  else if ((quoteCount % 2) == 1)
    alert("Your search includes an odd number of quotation marks.  Please edit your search to use quotation marks only in pairs.");
  else return true;

  return false;
};


function clearQuickSearchForm() { 
    var qsTextField = document.getElementById("qsTextField");
    qsTextField.value = "";
}


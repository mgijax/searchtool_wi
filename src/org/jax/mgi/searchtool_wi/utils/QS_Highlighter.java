package org.jax.mgi.searchtool_wi.utils;

import org.jax.mgi.searchtool_wi.matches.AbstractMatch;

/**
 * This class is responsible for highlighting matching text.
 * @author mhall
 *
 */

public class QS_Highlighter {

    private SearchInput si;

    public QS_Highlighter(SearchInput si) {
        this.si = si;
    }

    public String highlight (AbstractMatch match) {

        // Work string for the matched text.

        String workString = match.getMatchedText();

        // Encode the Superscript stuff

        workString = encodeSuperscript(workString);

        // Highlight the matching string
        // Is this a prefix search?
        // If not, highlight everything
        // If so, and its a symbol search, do something special
        // If not, highlight everything

        // Decode the Superscript stuff

        workString = decodeSuperscript(workString);

        // Return the string

        return workString;
    }



    private String highlight (String pattern, String matchingString) {
        return matchingString.replaceAll(pattern, "<b>"+pattern+"</b>");
    }

    private String encodeSuperscript (String text) {
        return text;
    }

    private String decodeSuperscript (String text) {
        return text;
    }

}

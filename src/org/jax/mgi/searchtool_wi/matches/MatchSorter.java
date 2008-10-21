package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;

/**
* A MatchSorter sorts a collection of Matches; See official Comparator
* interface javadocs for usage; written to AbstractMatch abstraction
*/
public class MatchSorter implements Comparator {

  /**
  * Implementation of the compare method.
  */
  public int compare(Object obj1, Object obj2) {

    int iReturn = 0; // return value; see Comparator javadoc for return
                     // values

    AbstractMatch match_1 = (AbstractMatch) obj1;
    AbstractMatch match_2 = (AbstractMatch) obj2;

    if (match_1.getScore().floatValue() > match_2.getScore().floatValue()) {
        iReturn = -1;
    } else if (match_1.getScore().floatValue() < match_2.getScore().floatValue()) {
        iReturn = 1;
    } else { // resort to secondary sort
        if (match_1.getAlphaSortBy().compareTo(match_2.getAlphaSortBy()) < 0) {
            iReturn = -1;
        } else {
            iReturn = 1;
        }
    }

    return iReturn;
  }

}

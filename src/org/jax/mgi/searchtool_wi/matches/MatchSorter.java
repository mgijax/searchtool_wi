package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;

/**
* A MatchSorter assists in sorting a collection of Matches; given two matches
* this class determines which should come first in the sort order.  Written to
* the AbstractMatch abstraction.
* Also see official Comparator interface javadocs for usage & return values
*/
public class MatchSorter implements Comparator {

  /**
  * Implementation of the compare method (for Comparator interface);
  * determines which object has a "higher" sort value
  */
  public int compare(Object obj1, Object obj2) {

    int iReturn = 0; // d

    AbstractMatch match_1 = (AbstractMatch) obj1;
    AbstractMatch match_2 = (AbstractMatch) obj2;

    // determine which object should come first in the sort order
    if (match_1.getScore().floatValue() > match_2.getScore().floatValue()) {
        iReturn = -1;
    } else if (match_1.getScore().floatValue() < match_2.getScore().floatValue()) {
        iReturn = 1;
    } else { // resort to secondary sort
        if (match_1.getAlphaSortBy().compareTo(match_2.getAlphaSortBy()) < 0) {
            iReturn = -1;
        } else if (match_1.getAlphaSortBy().compareTo(match_2.getAlphaSortBy()) > 0) {
            iReturn = 1;
        }
    }

    return iReturn;
  }

} //public class MatchSorter

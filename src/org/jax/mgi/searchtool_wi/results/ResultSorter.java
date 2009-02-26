package org.jax.mgi.searchtool_wi.results;

// Standard Java Classes
import java.util.*;

/**
* A ResultSorter assists in sorting a collection of Results; given two results
* this class determines which should come first in the sort order.  Written to
* the AbstractResult abstraction.
* Also see official Comparator interface javadocs for usage & return values
*/
public class ResultSorter implements Comparator {

  /**
  * Implementation of the compare method (for Comparator interface);
  * determines which object has a "higher" sort value
  */
  public int compare(Object obj1, Object obj2) {

    int iReturn = 0; // return value; see Comparator javadocs

    AbstractResult result_1 = (AbstractResult) obj1;
    AbstractResult result_2 = (AbstractResult) obj2;

    // determine which object should come first in the sort order
    if (result_1.getScore().floatValue() > result_2.getScore().floatValue()) {
        iReturn = -1;
    } else if (result_1.getScore().floatValue() < result_2.getScore().floatValue()) {
        iReturn = 1;
    } else { // resort to secondary sort
        if (result_1.getAlphaSortBy().compareTo(result_2.getAlphaSortBy()) < 0) {
            iReturn = -1;
        } else {
            iReturn = 1;
        }
    }

    return iReturn;
  }

} //public class ResultSorter

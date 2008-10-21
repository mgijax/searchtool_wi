package org.jax.mgi.searchtool_wi.results;

/*
 * ResultSorter.java
 *
 * See official Comparator interface javadocs for usage
 */

import java.util.*;

/**
 *
 */
public class ResultSorter implements Comparator {

  /**
  * Implementation of the compare method.
  */
  public int compare(Object obj1, Object obj2) {

      int iReturn = 0; // return value; see Comparator javadocs

      AbstractResult result_1 = (AbstractResult) obj1;
      AbstractResult result_2 = (AbstractResult) obj2;

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
}

package org.jax.mgi.searchtool_wi.utils;


/*
* @author pf (shamelessly plagiarized from javawi2's ColorAlternator)
*/

import java.util.List;

/**
* provides an easy mechanism to alternate between Strings
*/
public class StringAlternator
{
  //--------------//
  // class fields
  //--------------//

  // internal list of the two strings to cycle
  private String[] myStrings = new String[2];

  // index into 'myStrings' of the next string to return
  private int myStringIndex = 0;


  //---------------------//
  // public constructors
  //---------------------//

  /** instantiates a new 'StringAlternator' to cycle through
  * @param str1
  * @param str2
  */
  public StringAlternator(String str1, String str2)
  {

      myStrings[0] = str1;
      myStrings[1] = str2;
  }

  //------------------------//
  // public instance methods
  //------------------------//

  /**
  * get the next string
  * @return String
  */
  public String getString()
  {
      String str = myStrings[myStringIndex];

      myStringIndex++;
      if (myStringIndex == myStrings.length) {
          myStringIndex = 0;
      }

      return str;
  }

  /**
  * resets this 'StringAlternator' so that the next string to be returned
  *    by 'getString()' is the first string
  */
  public void reset()
  {
      myStringIndex = 0;
      return;
  }

}

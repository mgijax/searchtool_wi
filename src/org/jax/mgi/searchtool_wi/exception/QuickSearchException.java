package org.jax.mgi.searchtool_wi.exception;

import org.apache.log4j.Logger;

public class QuickSearchException extends RuntimeException {

  // --------------//
  // Class Fields
  // --------------//

  private String errorDisplay = new String("");
  private Exception caughtException;
  private static Logger logger = Logger.getLogger(QuickSearchException.class.getName());

  // --------------//
  // Constructors
  // --------------//

  public QuickSearchException() {
      super();
  }

  public QuickSearchException(String message) {
      super(message);
      errorDisplay = message;
      logger.error(message);
  }

  public QuickSearchException(Exception e) {
      super();
      caughtException = e;
      logger.error("EXCEPTION ENCOUNTERED",e);
  }

  // ------------------//
  // Public Accessors
  // ------------------//

  // errorDisplay
  public String getErrorDisplay() {
      return errorDisplay;
  }

  public void setErrorDisplay(String s) {
      errorDisplay = s;
  }

}

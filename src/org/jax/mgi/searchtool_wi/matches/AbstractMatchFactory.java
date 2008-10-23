package org.jax.mgi.searchtool_wi.matches;

// Standard Java Classes
import java.util.*;
import java.io.IOException;

// Lucene Classes
import org.apache.lucene.search.Hit;

// Quick Search Classes
import org.jax.mgi.shr.searchtool.IndexConstants;

// MGI Shared Classes
import org.jax.mgi.shr.config.Configuration;
import org.jax.mgi.shr.timing.TimeStamper;

/**
* An AbstractMatchFactory is an uninstantiatable parent of concrete
* MatchFactory classes, and is responsible for loading of the general shared
* knowledge residing in an AbstractMatch; extending concrete MatchFactory
* classes are responsible for specific object creation/loading, and need to
* override the abstract <i>getMatch()</i> method.  Also see AbstractMatch.java
*/
public abstract class AbstractMatchFactory
{
  //--------//
  // Fields
  //--------//

  /**
  * Configuration knowledge
  */
  protected Configuration config;

  /**
  * Timer for timing information
  */
  protected TimeStamper timer;

  /**
  * Hit from which to generate the match
  */
  protected Hit hit;


  //-------------//
  // Constructor //
  //-------------//

  /**
  * Hiding the default constructor; we should always be passed a config
  */
  private AbstractMatchFactory(){}

  /**
  * Parent constructor to be called by all extending concrete classes. All
  * class-level objects composing the extending classes will be exposed here
  */
  public AbstractMatchFactory(Configuration c)
  {
    config = c;
    timer = new TimeStamper();
  }


  //-----------------//
  // Abstract Methods
  //-----------------//

  /**
  * This is the abstract method all extending concrete classes must implement,
  * and should return the concrete match type the specific factory generates.
  * @param Lucene Hit object
  */
  public abstract AbstractMatch getMatch(Hit h) throws IOException;


  //-------------------------------------------//
  // Shared Implementation for Match Factories
  //-------------------------------------------//

  /**
  * Sets the Lucene Hit upon which the match will be generated
  * @param - Lucene Hit
  */
  public void setHit(Hit h) {
      hit = h;
  }

  /**
  * Loads a given AbstractMatch (a "Match"'s abstraction); all data elevated
  *  to the AbstractMatch should be loaded here
  * @param AbstractMatch
  * @return AbstractMatch (loaded with basic data)
  */
  public AbstractMatch loadAbstractMatchInfo (AbstractMatch thisMatch)
    throws IOException
  {
    thisMatch.setSearchScore( hit.getScore() );
    thisMatch.setLuceneDocID( hit.getId() );
    thisMatch.setDbKey( hit.get(IndexConstants.COL_DB_KEY) );
    thisMatch.setMatchedText( hit.get(IndexConstants.COL_RAW_DATA) );
    thisMatch.setDataType( hit.get(IndexConstants.COL_DATA_TYPE) );
    thisMatch.setDisplayableType( hit.get(IndexConstants.COL_TYPE_DISPLAY) );
    thisMatch.setProvider( hit.get(IndexConstants.COL_PROVIDER) );
    thisMatch.setUniqueKey( hit.get(IndexConstants.COL_UNIQUE_KEY) );

    return thisMatch;
  }
}


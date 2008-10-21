package org.jax.mgi.searchtool_wi.matches;

// standard java classes
import java.util.*;
import java.io.IOException;

// lucene
import org.apache.lucene.search.Hit;

// QS shared classes
import QS_Commons.IndexConstants;

// MGI Shared Classes
import org.jax.mgi.shr.config.Configuration;
import org.jax.mgi.shr.timing.TimeStamper;


/**
* A MarkerMatchFactory is responsible for MarkerMatch object creation and
*  data initialization
*/
public class MarkerMatchFactory extends AbstractMatchFactory
{

  //-------------//
  // Constructor //
  //-------------//

  /**
  * Constructs the factory, calling the parent class constructor with config
  */
  public MarkerMatchFactory(Configuration c)
  {
    super(c);
  }

  //------------------------------------//
  // Over-ridden Parent Abstract Method
  //------------------------------------//

  /**
  * Generates and initializes a MarkerMatch
  */
  public MarkerMatch getMatch(Hit h)
    throws IOException
  {
    // match type generated by this factory
    MarkerMatch markerMatch = new MarkerMatch();

    // use parent class's implementation to fill basic information about match
    this.setHit(h);
    this.loadAbstractMatchInfo(markerMatch);

    // fill data specific to this match type
    markerMatch.setOrganismKey( hit.get(IndexConstants.COL_ORGANISM) );
    markerMatch.setIsCurrent( hit.get(IndexConstants.COL_IS_CURRENT) );

    return markerMatch;

  }

}


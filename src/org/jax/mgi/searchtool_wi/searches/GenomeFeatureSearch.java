package org.jax.mgi.searchtool_wi.searches;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


import org.apache.log4j.Logger;

import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;

import org.jax.mgi.searchtool_wi.lookup.VocabInfoCache;
import org.jax.mgi.searchtool_wi.matches.AlleleNomenMatch;
import org.jax.mgi.searchtool_wi.matches.AlleleNomenMatchFactory;
import org.jax.mgi.searchtool_wi.matches.MarkerNomenMatch;
import org.jax.mgi.searchtool_wi.matches.MarkerNomenMatchFactory;
import org.jax.mgi.searchtool_wi.matches.MarkerVocabMatch;
import org.jax.mgi.searchtool_wi.matches.MarkerVocabMatchFactory;
import org.jax.mgi.searchtool_wi.matches.MatchTypeScorer;
import org.jax.mgi.searchtool_wi.results.GenomeFeatureResult;
import org.jax.mgi.searchtool_wi.utils.ScoreConstants;
import org.jax.mgi.searchtool_wi.utils.SearchHelper;
import org.jax.mgi.searchtool_wi.utils.SearchInput;

import org.jax.mgi.shr.config.Configuration;
import org.jax.mgi.shr.searchtool.IndexConstants;

/**
 * This concrete Search is responsible for gathering all data required for
 * the Quick Search to display the "Marker Bucket"
 */
public class GenomeFeatureSearch extends AbstractSearch {

	// results to be returned
	private HashMap searchResults = new HashMap();

	// logger
	private static Logger logger = Logger.getLogger(GenomeFeatureSearch.class.getName());

	// search cache; will get singleton reference in constructor
	private VocabInfoCache vocabInfoCache;

	// match holders
	private List markerNomenMatches		= new ArrayList();
	private List alleleNomenMatches		= new ArrayList();
	private List adMatches				= new ArrayList();
	private List goMatches				= new ArrayList();
	private List emapaMatches			= new ArrayList();
	private List emapsMatches			= new ArrayList();
	private List omimMatches			= new ArrayList();
	private List omimOrthoMatches		= new ArrayList();
	private List mpMatches				= new ArrayList();
	private List pirsfMatches			= new ArrayList();
	private List ipMatches				= new ArrayList();

	// holds vocab terms already handled; If we hit a term multiple ways,
	// we need only handle the best (first)
	private HashSet handledAdTerms			= new HashSet();
	private HashSet handledMpTerms			= new HashSet();
	private HashSet handledGoTerms			= new HashSet();
	private HashSet handledEmapaTerms		= new HashSet();
	private HashSet handledEmapsTerms		= new HashSet();
	private HashSet handledOmimTerms		= new HashSet();
	private HashSet handledOmimOrthoTerms	= new HashSet();
	private HashSet handledPirsfTerms		= new HashSet();
	private HashSet handledIpTerms			= new HashSet();

	// both the "and" and the "or" search use the same index; if we have
	// an "and" hit, do not keep the "or" hit for the same document
	private HashSet handledDocIDs = new HashSet();

	// filters
	private boolean incNomen			= true;
	private boolean incAd				= true;
	private boolean incMp				= true;
	private boolean incGo				= true;
	private boolean incEmapa			= true;
	private boolean incEmaps			= true;
	private boolean incOmim				= true;
	private boolean incPirsf			= true;
	private boolean incIp				= true;

	// Match factories we'll need generate the matches
	MarkerNomenMatchFactory markerNomenMatchFactory = new MarkerNomenMatchFactory(config);
	AlleleNomenMatchFactory alleleNomenMatchFactory = new AlleleNomenMatchFactory(config);
	MarkerVocabMatchFactory markerVocabMatchFactory = new MarkerVocabMatchFactory(config);

	// Match Type Scorers we'll need to score the matches
	MatchTypeScorer gfExactTypeScorer = new MatchTypeScorer( ScoreConstants.getMarkerExactScoreMap() );
	MatchTypeScorer gfAndTypeScorer = new MatchTypeScorer( ScoreConstants.getMrkAndScoreMap() );
	MatchTypeScorer gfOrTypeScorer = new MatchTypeScorer( ScoreConstants.getMrkOrScoreMap() );
	MatchTypeScorer gfOrWeightTypeScorer = new MatchTypeScorer( ScoreConstants.getMrkOrWeightMap() );

	// threshold
	private int matchCounter = 0;
	private int maxMatches;

	public GenomeFeatureSearch(Configuration c) {
		super(c);
		vocabInfoCache =
				VocabInfoCache.getVocabInfoCache();

		maxMatches = new Integer(config.get("MAX_MATCHES")).intValue();
	}

	/**
	 * gatherData() is an over-ridden method from the AbstractSearch class, and
	 *	is responsible for gathering all results objects
	 * @param a SearchInput object
	 * @return List of result objects; these are not yet 'finalized', scored,
	 *	or sorted, as the framework will take care of that
	 */
	public List gatherData(SearchInput searchInput) throws Exception {
		timer.record("---Marker gatherData Started---");

		parseParms(searchInput);

		// exact nomenclature matches - tier1
		searchGenomeFeature_NomenExact(searchInput);
		timer.record("GenomeFeatre Search - Done searching Exact");

		// exact vocabulary matches - tier1
		searchExactVocabMatches(searchInput);
		timer.record("GenomeFeatre Search - Done searching Vocab Exact");

		// 'and' matches - tier2
		searchGenomeFeature_AllTokens(searchInput);
		timer.record("GenomeFeatre Search - Done searching 'and' matches");

		// large token matches tier3 (only if we have more than 1 large token)
		if (searchInput.getLargeTokenCount() > 1) {
			// exact nomenclature matches - tier3
			searchGenomeFeature_LargeToken(searchInput);
			timer.record("GenomeFeatre Search - large tokens nomen");

			// exact vocabulary matches - tier3
			searchVocab_LargeToken(searchInput);
			timer.record("GenomeFeatre Search - Done searching large tokens for vocab");
		}

		// 'or' matches - tier4 (only if we have more than 1 small token)
		if (searchInput.getFilteredSmallTokenCount() > 0 && searchInput.getSmallTokenCount() > 1) {
			searchOrMatches(searchInput);
			timer.record("Marker - Done searching 'or' matches");
		}

		// assign matches
		assignMatches();
		timer.record("GenomeFeatre Search - Done Assigning Matches");

		return new ArrayList( searchResults.values() );
	}

	private void searchGenomeFeature_NomenExact (SearchInput searchInput) throws Exception {
		Hit hit;
		Hits hits;

		// search for ID matches of entire user input string
		hits =	indexAccessor.searchMarkerAccIDByWholeTerm(searchInput);
		logger.debug("GenomeFeatureSearch.searchMarkerAccIDByWholeTerm number of hits ->" + hits.length());
		for (Iterator hitIter = hits.iterator(); hitIter.hasNext();) {
			// get the hit, and build a match
			hit = (Hit) hitIter.next();
			handleTier1NomenHit(hit);
		}

		// search for symbol matches of entire user input string
		hits =	indexAccessor.searchMarkerSymbolExactByWholeTerm(searchInput);
		logger.debug("GenomeFeatureSearch.searchMarkerSymbolExactByWholeTerm number of hits ->" + hits.length());
		for (Iterator hitIter = hits.iterator(); hitIter.hasNext();) {
			// get the hit, and build a match
			hit = (Hit) hitIter.next();
			handleTier1NomenHit(hit);
		}

		// search for name/synonym matches of entire user input string
		hits =	indexAccessor.searchMarkerExactByWholeTerm(searchInput);
		logger.debug("GenomeFeatureSearch.searchMarkerExactByWholeTerm number of hits ->" + hits.length());
		for (Iterator hitIter = hits.iterator(); hitIter.hasNext();) {
			// get the hit, and build a match
			hit = (Hit) hitIter.next();
			handleTier1NomenHit(hit);
		}

	}

	//handler for Tier1 nomen
	private void handleTier1NomenHit (Hit hit) throws IOException {
		// marker hit
		if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.MARKER_TYPE_NAME)) {


			MarkerNomenMatch markerNomenMatch = markerNomenMatchFactory.getMatch(hit);
			gfExactTypeScorer.addScore(markerNomenMatch);
			markerNomenMatch.flagAsTier1();
			markerNomenMatches.add(markerNomenMatch);
		}

		// allele hit
		else if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.ALLELE_TYPE_NAME)) {

			AlleleNomenMatch alleleNomenMatch = alleleNomenMatchFactory.getMatch(hit);
			gfExactTypeScorer.addScore(alleleNomenMatch);
			alleleNomenMatch.flagAsTier1();
			alleleNomenMatches.add(alleleNomenMatch);
		}

	}

	private void searchExactVocabMatches (SearchInput searchInput) throws Exception {

		Hit hit;
		Hits hits;
		String vocabID;

		hits =	indexAccessor.searchMarkerVocabExactByWholeTerm(searchInput);
		logger.debug("GenomeFeatureSearch.searchMarkerVocabExactByWholeTerm ->" + hits.length());
		for (Iterator iter = hits.iterator(); iter.hasNext();) {
			hit = (Hit) iter.next();
			handleTier1VocabHit(hit);
		}

		hits =	indexAccessor.searchMarkerVocabAccIDByWholeTerm(searchInput);
		logger.debug("GenomeFeatureSearch.searchMarkerVocabAccIDByWholeTerm ->" + hits.length());
		for (Iterator iter = hits.iterator(); iter.hasNext();) {
			hit = (Hit) iter.next();
			handleTier1VocabHit(hit);
		}

	}

	private void handleTier1VocabHit (Hit hit) throws Exception {
		MarkerVocabMatch markerVocabMatch;
		List childIDs;
		String childTermKey;


		// anatomical dictionary hit
		if ( SearchHelper.isAD(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledAdTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {

				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier1();
				gfExactTypeScorer.addScore(markerVocabMatch);
				adMatches.add(markerVocabMatch);
				handledAdTerms.add( markerVocabMatch.getDbKey() );

				//children of this term;
				childIDs = vocabInfoCache.getAdChildTerms(hit.get(IndexConstants.COL_DB_KEY));
				if (childIDs != null) {
					for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
						childTermKey = (String)childIter.next();
						if ( !handledAdTerms.contains(childTermKey) ) {
							markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
							markerVocabMatch.flagAsTier1();
							gfExactTypeScorer.addScore(markerVocabMatch);

							// change db key to reflect the 'down-dag' term we're dealing
							// with, and penalize the scores of these, slightly
							markerVocabMatch.setDbKey(childTermKey);
							markerVocabMatch.addScore(-0.0001);

							adMatches.add(markerVocabMatch);
							handledAdTerms.add( markerVocabMatch.getDbKey() );
						}
					}
				}
			}
		}


		// gene emapa hit
		else if ( SearchHelper.isEMAPA(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledEmapaTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier1();
				gfExactTypeScorer.addScore(markerVocabMatch);
				emapaMatches.add(markerVocabMatch);
				handledEmapaTerms.add( markerVocabMatch.getDbKey() );

				//children of this term;
				childIDs = vocabInfoCache.getEmapaChildTerms(hit.get(IndexConstants.COL_DB_KEY));
				if (childIDs != null) {
					for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
						childTermKey = (String)childIter.next();
						if ( !handledEmapaTerms.contains(childTermKey) ) {
							markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
							markerVocabMatch.flagAsTier1();
							gfExactTypeScorer.addScore(markerVocabMatch);

							// change db key to reflect the 'down-dag' term we're dealing
							// with, and penalize the scores of these, slightly
							markerVocabMatch.setDbKey(childTermKey);
							markerVocabMatch.addScore(-0.0001);

							emapaMatches.add(markerVocabMatch);
							handledEmapaTerms.add( markerVocabMatch.getDbKey() );
						}
					}
				}
			}
		}

		// gene emaps hit
		else if ( SearchHelper.isEMAPS(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledEmapsTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier1();
				gfExactTypeScorer.addScore(markerVocabMatch);
				emapsMatches.add(markerVocabMatch);
				handledEmapsTerms.add( markerVocabMatch.getDbKey() );

				//children of this term;
				childIDs = vocabInfoCache.getEmapsChildTerms(hit.get(IndexConstants.COL_DB_KEY));
				if (childIDs != null) {
					for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
						childTermKey = (String)childIter.next();
						if ( !handledEmapsTerms.contains(childTermKey) ) {
							markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
							markerVocabMatch.flagAsTier1();
							gfExactTypeScorer.addScore(markerVocabMatch);

							// change db key to reflect the 'down-dag' term we're dealing
							// with, and penalize the scores of these, slightly
							markerVocabMatch.setDbKey(childTermKey);
							markerVocabMatch.addScore(-0.0001);

							emapsMatches.add(markerVocabMatch);
							handledEmapsTerms.add( markerVocabMatch.getDbKey() );
						}
					}
				}
			}
		}

		// gene ontology hit
		else if ( SearchHelper.isGO(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledGoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier1();
				gfExactTypeScorer.addScore(markerVocabMatch);
				goMatches.add(markerVocabMatch);
				handledGoTerms.add( markerVocabMatch.getDbKey() );

				//children of this term;
				childIDs = vocabInfoCache.getGoChildTerms(hit.get(IndexConstants.COL_DB_KEY));
				if (childIDs != null) {
					for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
						childTermKey = (String)childIter.next();
						if ( !handledGoTerms.contains(childTermKey) ) {
							markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
							markerVocabMatch.flagAsTier1();
							gfExactTypeScorer.addScore(markerVocabMatch);

							// change db key to reflect the 'down-dag' term we're dealing
							// with, and penalize the scores of these, slightly
							markerVocabMatch.setDbKey(childTermKey);
							markerVocabMatch.addScore(-0.0001);

							goMatches.add(markerVocabMatch);
							handledGoTerms.add( markerVocabMatch.getDbKey() );
						}
					}
				}
			}
		}

		// mammalian phenotype hit
		else if ( SearchHelper.isMP(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledMpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier1();
				gfExactTypeScorer.addScore(markerVocabMatch);
				mpMatches.add(markerVocabMatch);
				handledMpTerms.add(markerVocabMatch.getDbKey());

				//children of this term;
				childIDs = vocabInfoCache.getMpChildTerms(hit.get(IndexConstants.COL_DB_KEY));
				if (childIDs != null) {
					for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
						childTermKey = (String)childIter.next();
						if ( !handledMpTerms.contains(childTermKey) ) {
							markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
							markerVocabMatch.flagAsTier1();
							gfExactTypeScorer.addScore(markerVocabMatch);

							// change db key to reflect the 'down-dag' term we're dealing
							// with, and penalize the scores of these, slightly
							markerVocabMatch.setDbKey(childTermKey);
							markerVocabMatch.addScore(-0.0001);

							mpMatches.add(markerVocabMatch);
							handledMpTerms.add(markerVocabMatch.getDbKey());
						}
					}
				}
			}
		}

		// OMIM hit (no chasing down the dag)
		else if ( SearchHelper.isOMIM(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledOmimTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier1();
				gfExactTypeScorer.addScore(markerVocabMatch);
				omimMatches.add(markerVocabMatch);
				handledOmimTerms.add(markerVocabMatch.getDbKey());

			}
		}

		// OMIM ORTHO hit (no chasing down the dag)
		else if ( SearchHelper.isOMIMORTHO(hit.get(IndexConstants.COL_VOCABULARY)) ) {
			//ensure we haven't already done this term
			if (!handledOmimOrthoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier1();
				gfExactTypeScorer.addScore(markerVocabMatch);
				omimOrthoMatches.add(markerVocabMatch);
				handledOmimOrthoTerms.add(markerVocabMatch.getDbKey());

			}
		}

		// PIRSF hit (no chasing down the dag)
		else if ( SearchHelper.isPIRSF(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledPirsfTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier1();
				gfExactTypeScorer.addScore(markerVocabMatch);
				pirsfMatches.add(markerVocabMatch);
				handledPirsfTerms.add(markerVocabMatch.getDbKey());

			}
		}

		// Interprot hit (no chasing down the dag)
		else if ( SearchHelper.isIP(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledIpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier1();
				gfExactTypeScorer.addScore(markerVocabMatch);
				ipMatches.add(markerVocabMatch);
				handledIpTerms.add(markerVocabMatch.getDbKey());

			}
		}

		else { // shouldn't be hitting this part of the code, so log value
			logger.error("***Bad Object Type in Exact Vocab GenomeFeatureSearch ->" + hit.get(IndexConstants.COL_VOCABULARY));
		}
	}

	private void searchGenomeFeature_AllTokens (SearchInput searchInput) throws Exception {

		// defined outside loops to avoid repeated instantiation of references
		Hit hit;
		String termKey;
		MarkerNomenMatch markerNomenMatch;
		AlleleNomenMatch alleleMatch;
		MarkerVocabMatch markerVocabMatch;
		List childIDs;
		String childTermKey;

		// execute the search, and handle each textual match
		Hits hits =	indexAccessor.searchMarkerAnd(searchInput);
		logger.debug("GenomeFeatureSearch.searchGenomeFeature_AllTokens number of hits ->" + hits.length());

		// examine each hit
		for (Iterator iter = hits.iterator(); iter.hasNext();) {

			// ensure we're not over the max allowable number of and/or matches
			if (matchCounter > maxMatches) {
				break;
			}
			matchCounter++;

			hit = (Hit) iter.next();

			// mark this doc ID as having been handled; will skip in 'or' search
			handledDocIDs.add( hit.getId() );

			// marker hit
			if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.MARKER_TYPE_NAME)) {
				markerNomenMatch = markerNomenMatchFactory.getMatch(hit);
				markerNomenMatch.flagAsTier2();
				gfAndTypeScorer.addScore(markerNomenMatch);
				markerNomenMatches.add(markerNomenMatch);
			}

			// allele hit
			else if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.ALLELE_TYPE_NAME)) {

				alleleMatch = alleleNomenMatchFactory.getMatch(hit);
				alleleMatch.flagAsTier2();
				gfAndTypeScorer.addScore(alleleMatch);
				alleleNomenMatches.add(alleleMatch);
			}

			// anatomical dictionary hit
			else if ( SearchHelper.isAD(hit) ) {

				//ensure we haven't already done this term
				if (!handledAdTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
					markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
					markerVocabMatch.flagAsTier2();
					gfAndTypeScorer.addScore(markerVocabMatch);
					adMatches.add(markerVocabMatch);
					handledAdTerms.add( markerVocabMatch.getDbKey() );

					//children of this term;
					childIDs = vocabInfoCache.getAdChildTerms(hit.get(IndexConstants.COL_DB_KEY));
					if (childIDs != null) {
						for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
							childTermKey = (String)childIter.next();
							if ( !handledAdTerms.contains(childTermKey) ) {
								markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
								markerVocabMatch.flagAsTier2();
								gfAndTypeScorer.addScore(markerVocabMatch);

								// change db key to reflect the 'down-dag' term we're dealing
								// with, and penalize the scores of these, slightly
								markerVocabMatch.setDbKey(childTermKey);
								markerVocabMatch.addScore(-0.0001);

								adMatches.add(markerVocabMatch);
								handledAdTerms.add( markerVocabMatch.getDbKey() );
							}
						}
					}
				}
			}

			// gene emapa hit
			else if ( SearchHelper.isEMAPA(hit) ) {

				//ensure we haven't already done this term
				if (!handledEmapaTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
					markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
					markerVocabMatch.flagAsTier2();
					gfAndTypeScorer.addScore(markerVocabMatch);
					emapaMatches.add(markerVocabMatch);
					handledEmapaTerms.add( markerVocabMatch.getDbKey() );

					//children of this term;
					childIDs = vocabInfoCache.getEmapaChildTerms(hit.get(IndexConstants.COL_DB_KEY));
					if (childIDs != null) {
						for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
							childTermKey = (String)childIter.next();
							if ( !handledEmapaTerms.contains(childTermKey) ) {
								markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
								markerVocabMatch.flagAsTier2();
								gfAndTypeScorer.addScore(markerVocabMatch);

								// change db key to reflect the 'down-dag' term we're dealing
								// with, and penalize the scores of these, slightly
								markerVocabMatch.setDbKey(childTermKey);
								markerVocabMatch.addScore(-0.0001);

								emapaMatches.add(markerVocabMatch);
								handledEmapaTerms.add( markerVocabMatch.getDbKey() );
							}
						}
					}
				}
			}

			// gene emaps hit
			else if ( SearchHelper.isEMAPS(hit) ) {

				//ensure we haven't already done this term
				if (!handledEmapsTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
					markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
					markerVocabMatch.flagAsTier2();
					gfAndTypeScorer.addScore(markerVocabMatch);
					emapsMatches.add(markerVocabMatch);
					handledEmapsTerms.add( markerVocabMatch.getDbKey() );

					//children of this term;
					childIDs = vocabInfoCache.getEmapsChildTerms(hit.get(IndexConstants.COL_DB_KEY));
					if (childIDs != null) {
						for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
							childTermKey = (String)childIter.next();
							if ( !handledEmapsTerms.contains(childTermKey) ) {
								markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
								markerVocabMatch.flagAsTier2();
								gfAndTypeScorer.addScore(markerVocabMatch);

								// change db key to reflect the 'down-dag' term we're dealing
								// with, and penalize the scores of these, slightly
								markerVocabMatch.setDbKey(childTermKey);
								markerVocabMatch.addScore(-0.0001);

								emapsMatches.add(markerVocabMatch);
								handledEmapsTerms.add( markerVocabMatch.getDbKey() );
							}
						}
					}
				}
			}

			// gene ontology hit
			else if ( SearchHelper.isGO(hit) ) {

				//ensure we haven't already done this term
				if (!handledGoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
					markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
					markerVocabMatch.flagAsTier2();
					gfAndTypeScorer.addScore(markerVocabMatch);
					goMatches.add(markerVocabMatch);
					handledGoTerms.add( markerVocabMatch.getDbKey() );

					//children of this term;
					childIDs = vocabInfoCache.getGoChildTerms(hit.get(IndexConstants.COL_DB_KEY));
					if (childIDs != null) {
						for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
							childTermKey = (String)childIter.next();
							if ( !handledGoTerms.contains(childTermKey) ) {
								markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
								markerVocabMatch.flagAsTier2();
								gfAndTypeScorer.addScore(markerVocabMatch);

								// change db key to reflect the 'down-dag' term we're dealing
								// with, and penalize the scores of these, slightly
								markerVocabMatch.setDbKey(childTermKey);
								markerVocabMatch.addScore(-0.0001);

								goMatches.add(markerVocabMatch);
								handledGoTerms.add( markerVocabMatch.getDbKey() );
							}
						}
					}
				}
			}

			// mammalian phenotype hit
			else if ( SearchHelper.isMP(hit) ) {

				//ensure we haven't already done this term
				if (!handledMpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
					markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
					markerVocabMatch.flagAsTier2();
					gfAndTypeScorer.addScore(markerVocabMatch);
					mpMatches.add(markerVocabMatch);
					handledMpTerms.add( markerVocabMatch.getDbKey() );

					//children of this term;
					childIDs = vocabInfoCache.getMpChildTerms(hit.get(IndexConstants.COL_DB_KEY));
					if (childIDs != null) {
						for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
							childTermKey = (String)childIter.next();
							if ( !handledMpTerms.contains(childTermKey) ) {
								markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
								markerVocabMatch.flagAsTier2();
								gfAndTypeScorer.addScore(markerVocabMatch);

								// change db key to reflect the 'down-dag' term we're dealing
								// with, and penalize the scores of these, slightly
								markerVocabMatch.setDbKey(childTermKey);
								markerVocabMatch.addScore(-0.0001);

								mpMatches.add(markerVocabMatch);
								handledMpTerms.add( markerVocabMatch.getDbKey() );
							}
						}
					}
				}
			}

			// OMIM hit (no chasing down the dag)
			else if ( SearchHelper.isOMIM(hit) ) {

				//ensure we haven't already done this term
				if (!handledOmimTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
					markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
					markerVocabMatch.flagAsTier2();
					gfAndTypeScorer.addScore(markerVocabMatch);
					omimMatches.add(markerVocabMatch);
					handledOmimTerms.add( markerVocabMatch.getDbKey() );

				}
			}

			// OMIM Ortho hit (no chasing down the dag)
			else if ( SearchHelper.isOMIMORTHO(hit) ) {

				//ensure we haven't already done this term
				if (!handledOmimOrthoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
					markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
					markerVocabMatch.flagAsTier2();
					gfAndTypeScorer.addScore(markerVocabMatch);
					omimOrthoMatches.add(markerVocabMatch);
					handledOmimOrthoTerms.add( markerVocabMatch.getDbKey() );

				}
			}

			// PIRSF hit (no chasing down the dag)
			else if ( SearchHelper.isPIRSF(hit) ) {

				//ensure we haven't already done this term
				if (!handledPirsfTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
					markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
					markerVocabMatch.flagAsTier2();
					gfAndTypeScorer.addScore(markerVocabMatch);
					pirsfMatches.add(markerVocabMatch);
					handledPirsfTerms.add( markerVocabMatch.getDbKey() );

				}
			}

			// Interprot hit (no chasing down the dag)
			else if ( SearchHelper.isIP(hit) ) {

				//ensure we haven't already done this term
				if (!handledIpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
					markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
					markerVocabMatch.flagAsTier2();
					gfAndTypeScorer.addScore(markerVocabMatch);
					ipMatches.add(markerVocabMatch);
					handledIpTerms.add( markerVocabMatch.getDbKey() );

				}
			}
		}

	}

	private void searchGenomeFeature_LargeToken (SearchInput searchInput) throws Exception {
		Hit hit;
		Hits hits;

		// search each token against IDs
		List idHits =	indexAccessor.searchMarkerAccIDByLargeToken(searchInput);
		logger.debug("GenomeFeatureSearch.searchMarkerAccID ->" + idHits.size());
		for (Iterator hitIter = idHits.iterator(); hitIter.hasNext();) {
			// get the hit, and build a match
			hit = (Hit) hitIter.next();
			handleLargeTokenNomenHit(hit);
		}

		// search each token against symbols
		List symbolHits =	indexAccessor.searchMarkerSymbolExactByLargeToken(searchInput);
		logger.debug("GenomeFeatureSearch.searchMarkerSymbolExact ->" + symbolHits.size());
		for (Iterator hitIter = symbolHits.iterator(); hitIter.hasNext();) {
			// get the hit, and build a match
			hit = (Hit) hitIter.next();
			handleLargeTokenNomenHit(hit);
		}

		// search each token against marker names/synonyms
		List nameSynonymHits =	indexAccessor.searchMarkerExactByLargeToken(searchInput);
		logger.debug("GenomeFeatureSearch.searchMarkerExactByBigToken ->" + nameSynonymHits.size());
		for (Iterator hitIter = nameSynonymHits.iterator(); hitIter.hasNext();) {
			// get the hit, and build a match
			hit = (Hit) hitIter.next();
			handleLargeTokenNomenHit(hit);
		}
	}

	//handler for Tier3 nomen
	private void handleLargeTokenNomenHit (Hit hit) throws IOException {
		if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.MARKER_TYPE_NAME)) {

			MarkerNomenMatch markerNomenMatch = markerNomenMatchFactory.getMatch(hit);
			gfExactTypeScorer.addScore(markerNomenMatch);
			markerNomenMatch.flagAsTier3();
			markerNomenMatches.add(markerNomenMatch);
		} else if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.ALLELE_TYPE_NAME)) {

			AlleleNomenMatch alleleNomenMatch = alleleNomenMatchFactory.getMatch(hit);
			gfExactTypeScorer.addScore(alleleNomenMatch);
			alleleNomenMatch.flagAsTier3();
			alleleNomenMatches.add(alleleNomenMatch);

		}

	}

	private void searchVocab_LargeToken (SearchInput searchInput) throws Exception {
		Hit hit;
		List hits;

		// search each token against vocab IDs
		hits =	indexAccessor.searchMarkerVocabAccIDByLargeToken(searchInput);
		logger.debug("GenomeFeatureSearch.searchMarkerVocabAccIDByLargeToken ->"
				+ hits.size());
		for (Iterator hitIter = hits.iterator(); hitIter.hasNext();) {
			// get the hit, and build a match
			hit = (Hit) hitIter.next();
			handleLargeTokenVocabHit(hit);
		}
	}

	private void handleLargeTokenVocabHit (Hit hit) throws Exception {
		MarkerVocabMatch markerVocabMatch;
		List childIDs;
		String childTermKey;


		// anatomical dictionary hit
		if ( SearchHelper.isAD(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledAdTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier3();
				gfExactTypeScorer.addScore(markerVocabMatch);
				adMatches.add(markerVocabMatch);
				handledAdTerms.add( markerVocabMatch.getDbKey() );

				//children of this term;
				childIDs = vocabInfoCache.getAdChildTerms(hit.get(IndexConstants.COL_DB_KEY));
				if (childIDs != null) {
					for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
						childTermKey = (String)childIter.next();
						if ( !handledAdTerms.contains(childTermKey) ) {
							markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
							markerVocabMatch.flagAsTier3();
							gfExactTypeScorer.addScore(markerVocabMatch);

							// change db key to reflect the 'down-dag' term we're dealing
							// with, and penalize the scores of these, slightly
							markerVocabMatch.setDbKey(childTermKey);
							markerVocabMatch.addScore(-0.0001);

							adMatches.add(markerVocabMatch);
							handledAdTerms.add( markerVocabMatch.getDbKey() );
						}
					}
				}
			}
		}

		// gene emapa hit
		else if ( SearchHelper.isEMAPA(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledEmapaTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier3();
				gfExactTypeScorer.addScore(markerVocabMatch);
				emapaMatches.add(markerVocabMatch);
				handledEmapaTerms.add( markerVocabMatch.getDbKey() );

				//children of this term;
				childIDs = vocabInfoCache.getEmapaChildTerms(hit.get(IndexConstants.COL_DB_KEY));
				if (childIDs != null) {
					for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
						childTermKey = (String)childIter.next();
						if ( !handledEmapaTerms.contains(childTermKey) ) {
							markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
							markerVocabMatch.flagAsTier3();
							gfExactTypeScorer.addScore(markerVocabMatch);

							// change db key to reflect the 'down-dag' term we're dealing
							// with, and penalize the scores of these, slightly
							markerVocabMatch.setDbKey(childTermKey);
							markerVocabMatch.addScore(-0.0001);

							emapaMatches.add(markerVocabMatch);
							handledEmapaTerms.add( markerVocabMatch.getDbKey() );
						}
					}
				}
			}
		}

		// gene emaps hit
		else if ( SearchHelper.isEMAPS(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledEmapsTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier3();
				gfExactTypeScorer.addScore(markerVocabMatch);
				emapsMatches.add(markerVocabMatch);
				handledEmapsTerms.add( markerVocabMatch.getDbKey() );

				//children of this term;
				childIDs = vocabInfoCache.getEmapsChildTerms(hit.get(IndexConstants.COL_DB_KEY));
				if (childIDs != null) {
					for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
						childTermKey = (String)childIter.next();
						if ( !handledEmapsTerms.contains(childTermKey) ) {
							markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
							markerVocabMatch.flagAsTier3();
							gfExactTypeScorer.addScore(markerVocabMatch);

							// change db key to reflect the 'down-dag' term we're dealing
							// with, and penalize the scores of these, slightly
							markerVocabMatch.setDbKey(childTermKey);
							markerVocabMatch.addScore(-0.0001);

							emapsMatches.add(markerVocabMatch);
							handledEmapsTerms.add( markerVocabMatch.getDbKey() );
						}
					}
				}
			}
		}

		// gene ontology hit
		else if ( SearchHelper.isGO(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledGoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier3();
				gfExactTypeScorer.addScore(markerVocabMatch);
				goMatches.add(markerVocabMatch);
				handledGoTerms.add( markerVocabMatch.getDbKey() );

				//children of this term;
				childIDs = vocabInfoCache.getGoChildTerms(hit.get(IndexConstants.COL_DB_KEY));
				if (childIDs != null) {
					for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
						childTermKey = (String)childIter.next();
						if ( !handledGoTerms.contains(childTermKey) ) {
							markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
							markerVocabMatch.flagAsTier3();
							gfExactTypeScorer.addScore(markerVocabMatch);

							// change db key to reflect the 'down-dag' term we're dealing
							// with, and penalize the scores of these, slightly
							markerVocabMatch.setDbKey(childTermKey);
							markerVocabMatch.addScore(-0.0001);

							goMatches.add(markerVocabMatch);
							handledGoTerms.add( markerVocabMatch.getDbKey() );
						}
					}
				}
			}
		}

		// mammalian phenotype hit
		else if ( SearchHelper.isMP(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledMpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier3();
				gfExactTypeScorer.addScore(markerVocabMatch);
				mpMatches.add(markerVocabMatch);
				handledMpTerms.add( markerVocabMatch.getDbKey() );

				//children of this term;
				childIDs = vocabInfoCache.getMpChildTerms(hit.get(IndexConstants.COL_DB_KEY));
				if (childIDs != null) {
					for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
						childTermKey = (String)childIter.next();
						if ( !handledMpTerms.contains(childTermKey) ) {
							markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
							markerVocabMatch.flagAsTier3();
							gfExactTypeScorer.addScore(markerVocabMatch);

							// change db key to reflect the 'down-dag' term we're dealing
							// with, and penalize the scores of these, slightly
							markerVocabMatch.setDbKey(childTermKey);
							markerVocabMatch.addScore(-0.0001);

							mpMatches.add(markerVocabMatch);
							handledMpTerms.add( markerVocabMatch.getDbKey() );
						}
					}
				}
			}
		}

		// OMIM hit (no chasing down the dag)
		else if ( SearchHelper.isOMIM(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledOmimTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier3();
				gfExactTypeScorer.addScore(markerVocabMatch);
				omimMatches.add(markerVocabMatch);
				handledOmimTerms.add( markerVocabMatch.getDbKey() );

			}
		}

		// OMIM ORTHO hit (no chasing down the dag)
		else if ( SearchHelper.isOMIMORTHO(hit.get(IndexConstants.COL_VOCABULARY)) ) {
			//ensure we haven't already done this term
			if (!handledOmimOrthoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier3();
				gfExactTypeScorer.addScore(markerVocabMatch);
				omimOrthoMatches.add(markerVocabMatch);
				handledOmimOrthoTerms.add( markerVocabMatch.getDbKey() );

			}
		}

		// PIRSF hit (no chasing down the dag)
		else if ( SearchHelper.isPIRSF(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledPirsfTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier3();
				gfExactTypeScorer.addScore(markerVocabMatch);
				pirsfMatches.add(markerVocabMatch);
				handledPirsfTerms.add( markerVocabMatch.getDbKey() );

			}
		}

		// Interprot hit (no chasing down the dag)
		else if ( SearchHelper.isIP(hit.get(IndexConstants.COL_VOCABULARY)) ) {

			//ensure we haven't already done this term
			if (!handledIpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
				markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
				markerVocabMatch.flagAsTier3();
				gfExactTypeScorer.addScore(markerVocabMatch);
				ipMatches.add(markerVocabMatch);
				handledIpTerms.add( markerVocabMatch.getDbKey() );

			}
		}

		else { // shouldn't be hitting this part of the code, so log value
			logger.error("***Bad Object Type in Large Token Vocab GenomeFeatureSearch ->"
					+ hit.get(IndexConstants.COL_VOCABULARY) );
		}
			}

	//-------------------------//
	// Search for 'OR' matches
	//-------------------------//
	private void searchOrMatches (SearchInput searchInput) throws Exception {
		// defined outside loops to avoid repeated instantiation of references
		Hit hit;
		String termKey;
		MarkerNomenMatch markerNomenMatch;
		AlleleNomenMatch alleleNomenMatch;
		MarkerVocabMatch markerVocabMatch;
		List childIDs;
		String childTermKey;

		// execute the search, and handle each textual match
		Hits hits =	indexAccessor.searchMarkerOr(searchInput);
		logger.debug("GenomeFeatureSearch.searchOrMatches number of hits ->" + hits.length());
		for (Iterator iter = hits.iterator(); iter.hasNext();) {

			// ensure we're not over the max allowable number of and/or matches
			if (matchCounter > maxMatches) {
				break;
			}
			matchCounter++;

			hit = (Hit) iter.next();

			// skip this match if we already handled it in the 'AND' search
			if ( !handledDocIDs.contains( hit.getId() ) ) {
				// marker nomen
				if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.MARKER_TYPE_NAME)) {
					markerNomenMatch = markerNomenMatchFactory.getMatch(hit);
					gfOrWeightTypeScorer.addLuceneWeight(markerNomenMatch);
					gfOrTypeScorer.addScore(markerNomenMatch);
					markerNomenMatches.add(markerNomenMatch);
				}

				// allele hit
				else if (hit.get(IndexConstants.COL_OBJ_TYPE).equals(IndexConstants.ALLELE_TYPE_NAME)) {
					alleleNomenMatch = alleleNomenMatchFactory.getMatch(hit);
					gfOrWeightTypeScorer.addLuceneWeight(alleleNomenMatch);
					gfOrTypeScorer.addScore(alleleNomenMatch);
					alleleNomenMatches.add(alleleNomenMatch);
				}

				// anatomical dictionary hit
				else if ( SearchHelper.isAD(hit) ) {

					//ensure we haven't already done this term
					if (!handledAdTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
						markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
						adMatches.add(markerVocabMatch);
						handledAdTerms.add( markerVocabMatch.getDbKey() );

						//children of this term;
						childIDs = vocabInfoCache.getAdChildTerms(hit.get(IndexConstants.COL_DB_KEY));
						if (childIDs != null) {
							for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
								childTermKey = (String)childIter.next();
								if ( !handledAdTerms.contains(childTermKey) ) {
									markerVocabMatch = markerVocabMatchFactory.getMatch(hit);

									// change db key to reflect the 'down-dag' term we're dealing
									// with, and penalize the scores of these, slightly
									markerVocabMatch.setDbKey(childTermKey);
									markerVocabMatch.addScore(-0.0001);

									adMatches.add(markerVocabMatch);
									handledAdTerms.add( markerVocabMatch.getDbKey() );
								}
							}
						}
					}
				}

				// gene emapa hit
				else if ( SearchHelper.isEMAPA(hit) ) {

					//ensure we haven't already done this term
					if (!handledEmapaTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
						markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
						emapaMatches.add(markerVocabMatch);
						handledEmapaTerms.add( markerVocabMatch.getDbKey() );

						//children of this term;
						childIDs = vocabInfoCache.getEmapaChildTerms(hit.get(IndexConstants.COL_DB_KEY));
						if (childIDs != null) {
							for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
								childTermKey = (String)childIter.next();
								if ( !handledEmapaTerms.contains(childTermKey) ) {
									markerVocabMatch = markerVocabMatchFactory.getMatch(hit);

									// change db key to reflect the 'down-dag' term we're dealing
									// with, and penalize the scores of these, slightly
									markerVocabMatch.setDbKey(childTermKey);
									markerVocabMatch.addScore(-0.0001);

									emapaMatches.add(markerVocabMatch);
									handledEmapaTerms.add( markerVocabMatch.getDbKey() );
								}
							}
						}
					}
				}

				// gene emaps hit
				else if ( SearchHelper.isEMAPS(hit) ) {

					//ensure we haven't already done this term
					if (!handledEmapsTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
						markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
						emapsMatches.add(markerVocabMatch);
						handledEmapsTerms.add( markerVocabMatch.getDbKey() );

						//children of this term;
						childIDs = vocabInfoCache.getEmapsChildTerms(hit.get(IndexConstants.COL_DB_KEY));
						if (childIDs != null) {
							for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
								childTermKey = (String)childIter.next();
								if ( !handledEmapsTerms.contains(childTermKey) ) {
									markerVocabMatch = markerVocabMatchFactory.getMatch(hit);

									// change db key to reflect the 'down-dag' term we're dealing
									// with, and penalize the scores of these, slightly
									markerVocabMatch.setDbKey(childTermKey);
									markerVocabMatch.addScore(-0.0001);

									emapsMatches.add(markerVocabMatch);
									handledEmapsTerms.add( markerVocabMatch.getDbKey() );
								}
							}
						}
					}
				}

				// gene ontology hit
				else if ( SearchHelper.isGO(hit) ) {

					//ensure we haven't already done this term
					if (!handledGoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
						markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
						goMatches.add(markerVocabMatch);
						handledGoTerms.add( markerVocabMatch.getDbKey() );

						//children of this term;
						childIDs = vocabInfoCache.getGoChildTerms(hit.get(IndexConstants.COL_DB_KEY));
						if (childIDs != null) {
							for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
								childTermKey = (String)childIter.next();
								if ( !handledGoTerms.contains(childTermKey) ) {
									markerVocabMatch = markerVocabMatchFactory.getMatch(hit);

									// change db key to reflect the 'down-dag' term we're dealing
									// with, and penalize the scores of these, slightly
									markerVocabMatch.setDbKey(childTermKey);
									markerVocabMatch.addScore(-0.0001);

									goMatches.add(markerVocabMatch);
									handledGoTerms.add( markerVocabMatch.getDbKey() );
								}
							}
						}
					}
				}

				// mammalian phenotype hit
				else if ( SearchHelper.isMP(hit) ) {

					//ensure we haven't already done this term
					if (!handledMpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
						markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
						mpMatches.add(markerVocabMatch);
						handledMpTerms.add( markerVocabMatch.getDbKey() );

						//children of this term;
						childIDs = vocabInfoCache.getMpChildTerms(hit.get(IndexConstants.COL_DB_KEY));
						if (childIDs != null) {
							for (Iterator childIter = childIDs.iterator(); childIter.hasNext();) {
								childTermKey = (String)childIter.next();
								if ( !handledMpTerms.contains(childTermKey) ) {
									markerVocabMatch = markerVocabMatchFactory.getMatch(hit);

									// change db key to reflect the 'down-dag' term we're dealing
									// with, and penalize the scores of these, slightly
									markerVocabMatch.setDbKey(childTermKey);
									markerVocabMatch.addScore(-0.0001);

									mpMatches.add(markerVocabMatch);
									handledMpTerms.add( markerVocabMatch.getDbKey() );
								}
							}
						}
					}
				}

				// OMIM hit (no chasing down the dag)
				else if ( SearchHelper.isOMIM(hit) ) {

					//ensure we haven't already done this term
					if (!handledOmimTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
						markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
						omimMatches.add(markerVocabMatch);
						handledOmimTerms.add( markerVocabMatch.getDbKey() );

					}
				}

				// OMIM Ortho hit (no chasing down the dag)
				else if ( SearchHelper.isOMIMORTHO(hit) ) {

					//ensure we haven't already done this term
					if (!handledOmimOrthoTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
						markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
						omimOrthoMatches.add(markerVocabMatch);
						handledOmimOrthoTerms.add( markerVocabMatch.getDbKey() );

					}
				}

				// PIRSF hit (no chasing down the dag)
				else if ( SearchHelper.isPIRSF(hit) ) {

					//ensure we haven't already done this term
					if (!handledPirsfTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
						markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
						pirsfMatches.add(markerVocabMatch);
						handledPirsfTerms.add( markerVocabMatch.getDbKey() );

					}
				}

				// Interprot hit (no chasing down the dag)
				else if ( SearchHelper.isIP(hit) ) {

					//ensure we haven't already done this term
					if (!handledIpTerms.contains(hit.get(IndexConstants.COL_DB_KEY))) {
						markerVocabMatch = markerVocabMatchFactory.getMatch(hit);
						ipMatches.add(markerVocabMatch);
						handledIpTerms.add( markerVocabMatch.getDbKey() );

					}
				}

				else { // shouldn't be hitting this part of the code, so log value
					logger.error("***Bad Object Type in 'OR' GenomeFeatureSearch ->"
							+ hit.get(IndexConstants.COL_OBJ_TYPE) );
				}
			}
		}

	}

	/**
	 * Convenience method to interface with the result set; If a given entry has
	 * already been created, it will be returned.	If not, a new one is
	 * generated (and db_key set), placed in the result set, and then returned.
	 */
	private GenomeFeatureResult getGfResult(String objectType, String objectKey) {

		String cacheKey = objectType + "_" + objectKey;

		if (searchResults.containsKey(cacheKey)) {
			return (GenomeFeatureResult)searchResults.get(cacheKey);
		} else {
			GenomeFeatureResult gfResult = new GenomeFeatureResult(config);
			gfResult.setDbKey(objectKey);
			gfResult.setGenomeFeatureType(objectType);
			searchResults.put(cacheKey, gfResult);
			return gfResult;
		}
	}

	private void assignMatches() {

		String MARKER_TYPE = "MARKER";
		String ALLELE_TYPE = "ALLELE";

		List markerKeys;
		List alleleKeys;
		MarkerNomenMatch markerNomenMatch;
		AlleleNomenMatch alleleMatch;
		MarkerVocabMatch markerVocabMatch;
		MarkerNomenMatch nm;
		GenomeFeatureResult thisGenomeFeature;

		// assign exact input string matches to their markers
		if (incNomen) {

			for (Iterator iter = markerNomenMatches.iterator(); iter.hasNext();) {
				markerNomenMatch = (MarkerNomenMatch)iter.next();
				thisGenomeFeature = getGfResult(MARKER_TYPE, markerNomenMatch.getDbKey() );
				thisGenomeFeature.addMarkerNomenMatch(markerNomenMatch);
			}

			for (Iterator iter = alleleNomenMatches.iterator(); iter.hasNext();) {
				alleleMatch = (AlleleNomenMatch)iter.next();
				thisGenomeFeature = getGfResult(ALLELE_TYPE, alleleMatch.getDbKey() );
				thisGenomeFeature.addAlleleNomenMatch(alleleMatch);
			}
		}

		// assign AD matches to their markers
		if (incAd) {
			for (Iterator iter = adMatches.iterator(); iter.hasNext();) {
				markerVocabMatch = (MarkerVocabMatch)iter.next();
				markerKeys = vocabInfoCache.getAdAnnotMarkers(markerVocabMatch.getDbKey());
				if (markerKeys != null) {
					for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
						thisGenomeFeature = getGfResult(MARKER_TYPE, (String)mrkKeyIter.next() );
						thisGenomeFeature.addAdMatch(markerVocabMatch);
					}
				}
			}
		}

		// assign GO matches to their markers
		if (incGo) {
			for (Iterator iter = goMatches.iterator(); iter.hasNext();) {
				markerVocabMatch = (MarkerVocabMatch)iter.next();
				markerKeys = vocabInfoCache.getGoAnnotMarkers(markerVocabMatch.getDbKey());
				if (markerKeys != null) {
					for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
						thisGenomeFeature = getGfResult(MARKER_TYPE, (String)mrkKeyIter.next() );
						thisGenomeFeature.addGoMatch(markerVocabMatch);
					}
				}
			}
		}

		// assign EMAPA matches to their markers
		if (incEmapa) {
			for (Iterator iter = emapaMatches.iterator(); iter.hasNext();) {
				markerVocabMatch = (MarkerVocabMatch)iter.next();
				markerKeys = vocabInfoCache.getEmapaAnnotMarkers(markerVocabMatch.getDbKey());
				if (markerKeys != null) {
					for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
						thisGenomeFeature = getGfResult(MARKER_TYPE, (String)mrkKeyIter.next() );
						thisGenomeFeature.addEmapaMatch(markerVocabMatch);
					}
				}
			}
		}

		// assign EMAPS matches to their markers
		if (incEmaps) {
			for (Iterator iter = emapsMatches.iterator(); iter.hasNext();) {
				markerVocabMatch = (MarkerVocabMatch)iter.next();
				markerKeys = vocabInfoCache.getEmapsAnnotMarkers(markerVocabMatch.getDbKey());
				if (markerKeys != null) {
					for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
						thisGenomeFeature = getGfResult(MARKER_TYPE, (String)mrkKeyIter.next() );
						thisGenomeFeature.addEmapsMatch(markerVocabMatch);
					}
				}
			}
		}

		// assign MP matches to their markers
		if (incMp) {
			for (Iterator iter = mpMatches.iterator(); iter.hasNext();) {
				markerVocabMatch = (MarkerVocabMatch)iter.next();
				alleleKeys = vocabInfoCache.getMpAnnotAlleles(markerVocabMatch.getDbKey());
				if (alleleKeys != null) {
					for (Iterator mrkKeyIter = alleleKeys.iterator(); mrkKeyIter.hasNext();) {
						thisGenomeFeature = getGfResult(ALLELE_TYPE, (String)mrkKeyIter.next() );
						thisGenomeFeature.addMpMatch(markerVocabMatch);
					}
				}
			}
		}

		// assign Omim matches to their markers
		if (incOmim) {
			for (Iterator iter = omimMatches.iterator(); iter.hasNext();) {
				markerVocabMatch = (MarkerVocabMatch)iter.next();
				alleleKeys = vocabInfoCache.getOmimAnnotAlleles(markerVocabMatch.getDbKey());
				if (alleleKeys != null) {
					for (Iterator mrkKeyIter = alleleKeys.iterator(); mrkKeyIter.hasNext();) {
						thisGenomeFeature = getGfResult(ALLELE_TYPE, (String)mrkKeyIter.next() );
						thisGenomeFeature.addOmimMatch(markerVocabMatch);
					}
				}
			}
		}

		// assign Omim ortho matches to their markers
		if (incOmim) {
			for (Iterator iter = omimOrthoMatches.iterator(); iter.hasNext();) {
				markerVocabMatch = (MarkerVocabMatch)iter.next();
				markerKeys = vocabInfoCache.getOmimOrthoAnnotMarkers(markerVocabMatch.getDbKey());
				if (markerKeys != null) {
					for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
						thisGenomeFeature = getGfResult(MARKER_TYPE, (String)mrkKeyIter.next() );
						thisGenomeFeature.addOmimOrthoMatch(markerVocabMatch);
					}
				}
			}
		}

		// assign PIRSF matches to their markers
		if (incPirsf) {
			for (Iterator iter = pirsfMatches.iterator(); iter.hasNext();) {
				markerVocabMatch = (MarkerVocabMatch)iter.next();
				markerKeys = vocabInfoCache.getPsAnnotMarkers(markerVocabMatch.getDbKey());
				if (markerKeys != null) {
					for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
						thisGenomeFeature = getGfResult(MARKER_TYPE, (String)mrkKeyIter.next() );
						thisGenomeFeature.addPirsfMatch(markerVocabMatch);
					}
				}
			}
		}

		// assign ip matches to their markers
		if (incOmim) {
			for (Iterator iter = ipMatches.iterator(); iter.hasNext();) {
				markerVocabMatch = (MarkerVocabMatch)iter.next();
				markerKeys = vocabInfoCache.getIpAnnotMarkers(markerVocabMatch.getDbKey());
				if (markerKeys != null) {
					for (Iterator mrkKeyIter = markerKeys.iterator(); mrkKeyIter.hasNext();) {
						thisGenomeFeature = getGfResult(MARKER_TYPE, (String)mrkKeyIter.next() );
						thisGenomeFeature.addIpMatch(markerVocabMatch);
					}
				}
			}
		}

	}

	private void parseParms(SearchInput searchInput) throws Exception {
		if (searchInput.hasFormParameter("exclude")) {
			for (String excludeItem : searchInput.getParameterValues("exclude")) {
				if (excludeItem.equals("nomen")) {
					incNomen=false;
				} else if (excludeItem.equals("ad")) {
					incAd=false;
				} else if (excludeItem.equals("mp")) {
					incMp=false;
				} else if (excludeItem.equals("go")) {
					incGo=false;
				} else if (excludeItem.equals("emapa")) {
					incEmapa=false;
				} else if (excludeItem.equals("emaps")) {
					incEmaps=false;
				} else if (excludeItem.equals("omim")) {
					incOmim=false;
				} else if (excludeItem.equals("pirsf")) {
					incPirsf=false;
				} else if (excludeItem.equals("Ip")) {
					incIp=false;
				}
			}
		}
	}
}

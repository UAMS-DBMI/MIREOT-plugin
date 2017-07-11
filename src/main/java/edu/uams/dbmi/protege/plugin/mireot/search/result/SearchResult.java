package edu.uams.dbmi.protege.plugin.mireot.search.result;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Represents a matching OWL Entity.  This class is used to display results to the user.
 * 
 * @author Josh Hanna
 */
public interface SearchResult {
	
	/**
	 * @author Josh Hanna
	 * @return String representing the type of SearchResult (e.g. Class vs Object Property)
	 */
	public String getType();
	
	/*
	 * Accessors
	 */
	
	/**
	 * 
	 * @return IRI of the matching Entity
	 */
	public IRI getIRI();

	/**
	 * 
	 * @return Label of the matching Entity
	 */
	public String getName();

	/**
	 * 
	 * @return annotation type where the match occurred
	 */
	public String getMatchType();
	
	/**
	 * 
	 * @return context of match
	 */
	public String getMatchContext();

	/**
	 * 
	 * @return Ontology from which the Entity was found to have matching annotations
	 */
	public OWLOntology getOntology();

	/**
	 * 
	 * @return Entity which matched
	 */
	public OWLEntity getOWLEntity();

}

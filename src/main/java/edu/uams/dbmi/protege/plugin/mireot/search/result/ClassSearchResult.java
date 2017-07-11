package edu.uams.dbmi.protege.plugin.mireot.search.result;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Represents a matching OWL Class
 * 
 * @author Josh Hanna, Cheng Chen
 */
public class ClassSearchResult implements SearchResult {
	private String nameString;
	private String matchType;
	private String matchContext;
	private IRI iri;
	
	private OWLClass ontClass;
	private OWLOntology ontology;

	@Override
	public OWLEntity getOWLEntity() {
		return ontClass;
	}

	public ClassSearchResult(IRI iri, String name, String matchType, String matchContext, OWLEntity ontClass, OWLOntology ontology) {
		this.iri = iri;
		this.nameString = name;
		this.matchType = matchType;
		this.matchContext = matchContext;
		this.ontClass = (OWLClass) ontClass;
		this.ontology = ontology;
	}

	@Override
	public IRI getIRI() {
		return iri;
	}

	@Override
	public String getName() {
		return nameString;
	}

	@Override
	public String getMatchType() {
		return matchType;
	}

	@Override
	public OWLOntology getOntology() {
		return ontology;
	}


	@Override
	public String getType() {
		return "Class";
	}

	@Override
	public String getMatchContext() {
		return matchContext;
	}
}
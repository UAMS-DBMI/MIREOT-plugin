package edu.uams.dbmi.protege.plugin.mireot.search.result;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLDataProperty;

/**
 * Represents a matching Datatype Property
 * 
 * @author Zakariae Aloulen
 */
public class DatatypePropertySearchResult implements SearchResult {
	private String nameString;
	private String matchType;
	private String matchContext;
	private IRI iri;
	
	private OWLOntology ontology;
	private OWLDataProperty datatypeProperty;

	public OWLEntity getOWLEntity() {
		return datatypeProperty;
	}

	@Override
	public String getType() {
		return "Datatype Property";
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
	
	public DatatypePropertySearchResult(IRI iri, String name, String matchType, String matchContext, OWLEntity datatypeProperty, OWLOntology ontology) {
		this.iri = iri;
		this.nameString = name;
		this.matchType = matchType;
		this.matchContext = matchContext;
		this.datatypeProperty = (OWLDataProperty) datatypeProperty;
		this.ontology = ontology;
	}

	@Override
	public String getMatchContext() {
		return matchContext;
	}

}

package edu.uams.dbmi.protege.plugin.mireot.search.result;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Represents a matching Object Property
 * 
 * @author Josh Hanna
 */
public class ObjectPropertySearchResult implements SearchResult {
	private String nameString;
	private String matchType;
	private String matchContext;
	private IRI iri;
	
	private OWLOntology ontology;
	private OWLObjectProperty objectProperty;

	public OWLEntity getOWLEntity() {
		return objectProperty;
	}

	@Override
	public String getType() {
		return "Object Property";
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
	
	public ObjectPropertySearchResult(IRI iri, String name, String matchType, String matchContext, OWLEntity objProperty, OWLOntology ontology) {
		this.iri = iri;
		this.nameString = name;
		this.matchType = matchType;
		this.matchContext = matchContext;
		this.objectProperty = (OWLObjectProperty) objProperty;
		this.ontology = ontology;
	}

	@Override
	public String getMatchContext() {
		return matchContext;
	}

}

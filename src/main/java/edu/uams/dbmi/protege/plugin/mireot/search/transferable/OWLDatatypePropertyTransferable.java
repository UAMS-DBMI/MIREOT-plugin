package edu.uams.dbmi.protege.plugin.mireot.search.transferable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Transferable that transfers a message containing both an OWL Datatype Property and an Ontology used for drag and drop
 * 
 * @author Zakariae Aloulen
 */
public class OWLDatatypePropertyTransferable implements Transferable, Serializable {
	
	OWLDatatypePropertyMessage message;

	private DataFlavor ontDataFlavor = new DataFlavor(OWLDataProperty.class, OWLDataProperty.class.getSimpleName());

	public OWLDatatypePropertyTransferable(OWLDataProperty datatypeProperty,
			OWLOntology ontology, String URL) {
		this.message = new OWLDatatypePropertyMessage();
		message.datatypeProperty = datatypeProperty;
		message.ontology = ontology;
		message.URL = URL;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if(flavor.equals(ontDataFlavor)){
			return this.message;
		} else {
			return null;
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return null;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if(flavor.equals(ontDataFlavor)){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Message representing data used for drag and drop.
	 * Contains references to both ontology and datatype property.
	 * 
	 * @author Josh Hanna
	 */
	public class OWLDatatypePropertyMessage implements Serializable{
		private OWLDataProperty datatypeProperty;
		private OWLOntology ontology;
		private String URL;
		
		public OWLDataProperty getDatatypeProperty() {
			return datatypeProperty;
		}

		public OWLOntology getOntology() {
			return ontology;
		}
		
		public String getURL(){
			return URL;
		}
		
	}
}

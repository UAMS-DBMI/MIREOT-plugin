package edu.uams.dbmi.protege.plugin.mireot.search.transferable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Transferable that transfers a message containing both an OWL Object Property and an Ontology used for drag and drop
 * 
 * @author Josh Hanna
 */
public class OWLObjectPropertyTransferable implements Transferable, Serializable {
	
	OWLObjectPropertyMessage message;

	private DataFlavor ontDataFlavor = new DataFlavor(OWLObjectProperty.class, OWLObjectProperty.class.getSimpleName());

	public OWLObjectPropertyTransferable(OWLObjectProperty objectProperty,
			OWLOntology ontology, String URL) {
		this.message = new OWLObjectPropertyMessage();
		message.objectProperty = objectProperty;
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
	 * Contains references to both ontology and object property.
	 * 
	 * @author Josh Hanna
	 */
	public class OWLObjectPropertyMessage implements Serializable{
		private OWLObjectProperty objectProperty;
		private OWLOntology ontology;
		private String URL;
		
		public OWLObjectProperty getObjectProperty() {
			return objectProperty;
		}

		public OWLOntology getOntology() {
			return ontology;
		}
		
		public String getURL(){
			return URL;
		}
		
	}
}

package edu.uams.dbmi.protege.plugin.mireot.search.transferable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Transferable that transfers a message containing both an OWL Class and an Ontology used for drag and drop
 * 
 * @author Josh Hanna
 */
public class OWLClassTransferable implements Transferable, Serializable {
	
	OWLClassMessage message;
	
	private DataFlavor ontDataFlavor = new DataFlavor(OWLClass.class, OWLClass.class.getSimpleName());

	public OWLClassTransferable(OWLClass ontClass, OWLOntology ontology, String URL) {
		this.message = new OWLClassMessage();
		message.ontClass = ontClass;
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
	 * Contains references to both ontology and class.
	 * 
	 * @author Josh Hanna
	 */
	public class OWLClassMessage implements Serializable {
		private OWLClass ontClass;
		private OWLOntology ontology;
		private String URL;
		
		public OWLClass getOntClass() {
			return ontClass;
		}

		public OWLOntology getOntology() {
			return ontology;
		}
		
		public String getURL(){
			return URL;
		}
		
	}

}

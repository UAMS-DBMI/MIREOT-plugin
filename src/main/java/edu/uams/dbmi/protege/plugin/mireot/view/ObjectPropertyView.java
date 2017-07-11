package edu.uams.dbmi.protege.plugin.mireot.view;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.tree.TreePath;

import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.tree.OWLObjectTreeNode;
import org.protege.editor.owl.ui.view.AbstractOWLPropertyHierarchyViewComponent;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;

import edu.uams.dbmi.protege.plugin.mireot.search.transferable.OWLObjectPropertyTransferable.OWLObjectPropertyMessage;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 * View that contains a tree representing the Object Properties of the active ontology
 * 
 * @author Josh Hanna
 */
public class ObjectPropertyView extends AbstractOWLPropertyHierarchyViewComponent<OWLObjectProperty> 
implements DropTargetListener{

	DataFlavor dataFlavor = new DataFlavor(OWLObjectProperty.class, OWLObjectProperty.class.getSimpleName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 5118205875325880751L;

	public void performExtraInitialisation() throws Exception {
		new DropTarget(getTree(), this);

		getTree().setDropMode(DropMode.ON_OR_INSERT);

	}

	protected boolean isOWLObjectPropertyView() {
		return true;
	}

	protected OWLObjectHierarchyProvider<OWLObjectProperty> getHierarchyProvider() {
		return getOWLModelManager().getOWLHierarchyManager().getOWLObjectPropertyHierarchyProvider();
	}

	protected OWLSubPropertyAxiom<?> getSubPropertyAxiom(OWLObjectProperty child, OWLObjectProperty parent) {
		return getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(child, parent);
	}

	protected boolean canAcceptDrop(Object child, Object parent) {
		return child instanceof OWLObjectProperty;
	}

	protected OWLEntityCreationSet<OWLObjectProperty> createProperty() {
		return getOWLWorkspace().createOWLObjectProperty();
	}

	protected Icon getSubIcon() {
		return OWLIcons.getIcon("property.object.addsub.png");
	}

	protected Icon getSibIcon() {
		return OWLIcons.getIcon("property.object.addsib.png");
	}

	protected Icon getDeleteIcon() {
		return OWLIcons.getIcon("property.object.delete.png");
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// do nothing

	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// do nothing

	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// do nothing

	}

	@Override
	public void drop(DropTargetDropEvent dtde) {

		// gets the cursor point
		Point pt = dtde.getLocation();

		OWLObjectTree<OWLObjectProperty> tr = getTree();

		// finds the current path to the cursor in the tree
		TreePath parentPath = tr.getClosestPathForLocation(pt.x, pt.y);

		// gets the node we are at
		OWLObjectTreeNode<?> n = (OWLObjectTreeNode<?>) parentPath
				.getLastPathComponent();

		// gets the object for that node
		OWLObject ob = (OWLObject) n.getOWLObject();

		// get all the object properties for the object
		Set<OWLObjectProperty> objPropSet = ob.getObjectPropertiesInSignature();

		// create an iterator for the set. the first element is the parent property
		Iterator<OWLObjectProperty> it = objPropSet.iterator();
		OWLObjectProperty origin = it.next();

		// get the data being transferred by the drop
		Transferable trans = dtde.getTransferable();

		if(trans.isDataFlavorSupported(dataFlavor)){

			OWLObjectPropertyMessage msg = null;

			try {
				msg = (OWLObjectPropertyMessage) trans.getTransferData(dataFlavor);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(msg.getOntology() != getOWLModelManager().getActiveOntology()){

				handleRemoteDrop(msg, origin);

			} else {

				dtde.rejectDrop();
				return;

			}

			//expand the tree node we just dropped on
			tr.expandPath(parentPath);

			//drop is complete
			dtde.dropComplete(true);
		} else if(trans.isDataFlavorSupported(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR)){
			handleLocalDrop(trans, origin);

			//expand the tree node we just dropped on
			tr.expandPath(parentPath);

			//drop is complete
			dtde.dropComplete(true);
		} else {
			dtde.rejectDrop();
		}

	}

	@SuppressWarnings("unchecked")
	private void handleLocalDrop(Transferable trans, OWLObjectProperty origin) {

		ArrayList<OWLObject> objects = null;
		try {
			objects = (ArrayList<OWLObject>) trans.getTransferData(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR);
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		OWLOntology activeOnt = this.getOWLModelManager().getActiveOntology();

		OWLObjectProperty objPropInTransit = null;

		if(objects.get(0) instanceof OWLObjectProperty){
			// load in the additional class from the iri
			objPropInTransit = (OWLObjectProperty) objects.get(0);
		} else {
			return;
		}

		// get the parent of the additional class. this is pointless for a
		// non local drag but is needed to make sure the tree looks right
		// otherwise
		Set<OWLObjectPropertyExpression> sup = new HashSet<>(EntitySearcher.getSuperProperties(objPropInTransit, activeOnt));
		Iterator<OWLObjectPropertyExpression> s = sup.iterator();

		// the first element in the superclass set is the parent (if this
		// changes it will break the drag and drop for local)

		OWLObjectPropertyExpression oc = null;

		if (s.hasNext()) {
			oc = s.next();
		}

		OWLObjectProperty fromParent = null;

		if(oc != null){
			fromParent = oc.asOWLObjectProperty();
		} 

		// get the set of annotations for the additional class
		Set<OWLAnnotation> anots = new HashSet<>(EntitySearcher.getAnnotations(objPropInTransit.getIRI(), activeOnt));

		addObjectProperty(objPropInTransit, origin, anots);

		removeParent(objPropInTransit, fromParent);

	}



	private void handleRemoteDrop(OWLObjectPropertyMessage msg, OWLObjectProperty active) {
		OWLAnnotationProperty importedFromProperty = getOWLDataFactory().getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000412"));
		OWLAnnotationProperty label = getOWLDataFactory().getRDFSLabel();

		OWLObjectProperty objProp = msg.getObjectProperty();
		OWLOntology ontology = msg.getOntology();

		Set<OWLAnnotation> annotations = new HashSet<>(EntitySearcher.getAnnotations(objProp.getIRI(), ontology));

		//getting annotations from the imports, too
		for(OWLOntology ont : ontology.getImports()){
			annotations.addAll(EntitySearcher.getAnnotations(objProp.getIRI(), ont));
		}

		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		HashSet<OWLAnnotation> annotationsCopy = new HashSet<OWLAnnotation>(annotations);

		//processing annotations
		for(OWLAnnotation annotation : annotationsCopy){
			//checking for any existing 'imported from' annotations
			if(annotation.getProperty().getIRI().toString().equals(importedFromProperty.getIRI().toString())){
				annotations.remove(annotation);
				continue;
			}

			//checking for annotation property labels
			Set<OWLAnnotation> propertyAnnotations = new HashSet<>(EntitySearcher.getAnnotations(annotation.getProperty().getIRI(), ontology, label));

			//getting annotations from the imports, too
			for(OWLOntology ont : ontology.getImports()){
				propertyAnnotations.addAll(EntitySearcher.getAnnotations(annotation.getProperty().getIRI(), ont, label));
			}
			
			
			for (OWLAnnotation propertyAnnotation : propertyAnnotations) {
				OWLAxiom ax = getOWLModelManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(annotation.getProperty().getIRI(), propertyAnnotation);
				changes.add(new AddAxiom((getOWLModelManager().getActiveOntology()), ax));
			}
			


		}

		OWLAnnotation importedFromAnnotation = null;

		if(ontology.getOntologyID().getOntologyIRI() != null){
			importedFromAnnotation = getOWLDataFactory().getOWLAnnotation(importedFromProperty, getOWLDataFactory().getOWLLiteral(ontology.getOntologyID().getOntologyIRI().toString()));
		} else {
			importedFromAnnotation = getOWLDataFactory().getOWLAnnotation(importedFromProperty, getOWLDataFactory().getOWLLiteral(msg.getURL()));
		}
		
		OWLAxiom ax = getOWLDataFactory().getOWLAnnotationAssertionAxiom(label, importedFromProperty.getIRI(), getOWLDataFactory().getOWLLiteral("imported from", "en"));
		changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
		
		annotations.add(importedFromAnnotation);

		getOWLModelManager().applyChanges(changes);
		
		addObjectProperty(objProp, active, annotations);
	}

	private void addObjectProperty(OWLObjectProperty child, OWLObjectProperty parent,
			Set<OWLAnnotation> annotations) {

		OWLDataFactory df = getOWLModelManager().getOWLDataFactory();


		//can't move topObjectProperty
		if (child.equals(getOWLModelManager().getOWLDataFactory().getOWLTopObjectProperty())) {
			return;
		}

		//data structure to store changes for batching
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

		// add all the annotations into the child
		for (OWLAnnotation an : annotations) {
			OWLAxiom ax = getOWLModelManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(child.getIRI(), an);
			changes.add(new AddAxiom((getOWLModelManager().getActiveOntology()), ax));
		}

		// add new parent
		changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), df.getOWLDeclarationAxiom(child)));

		if (!df.getOWLThing().equals(parent)) {
			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), df.getOWLSubObjectPropertyOfAxiom(child, parent)));
		}

		getOWLModelManager().applyChanges(changes);


	}

	private void removeParent(OWLObjectProperty child, OWLObjectProperty parent) {

		//pointless if either is null
		if(parent == null || child == null){
			return;
		}

		OWLDataFactory df = getOWLModelManager().getOWLDataFactory();

		getOWLModelManager().applyChange(
				new RemoveAxiom(getOWLModelManager().getActiveOntology(), 
						df.getOWLSubObjectPropertyOfAxiom(child, parent)));
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		//do nothing
	}

}

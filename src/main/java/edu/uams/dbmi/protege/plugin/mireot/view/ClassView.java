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
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.DropMode;
import javax.swing.tree.TreePath;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.protege.editor.owl.ui.action.DeleteClassAction;
import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.tree.OWLObjectTreeNode;
import org.protege.editor.owl.ui.view.CreateNewChildTarget;
import org.protege.editor.owl.ui.view.CreateNewSiblingTarget;
import org.protege.editor.owl.ui.view.CreateNewTarget;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassHierarchyViewComponent;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;

import edu.uams.dbmi.protege.plugin.mireot.search.transferable.OWLClassTransferable.OWLClassMessage;

/**
 * View that contains a tree representing the active ontology
 * 
 * @author Chen Cheng, Josh Hanna
 */
public class ClassView extends AbstractOWLClassHierarchyViewComponent implements
CreateNewTarget, CreateNewChildTarget, CreateNewSiblingTarget,
DropTargetListener {

	DataFlavor dataFlavor = new DataFlavor(OWLClass.class, OWLClass.class.getSimpleName());
	private static final long serialVersionUID = 1L;

	public void performExtraInitialisation() throws Exception {

		// Add in the manipulation actions - we won't need to keep track
		// of these, as this will be done by the view - i.e. we won't
		// need to dispose of these actions.
		getOWLWorkspace().getOWLSelectionModel().addListener(
				new OWLSelectionModelListener() {

					public void selectionChanged() throws Exception {
						final OWLObject owlObject = getOWLWorkspace().getOWLSelectionModel().getSelectedObject();

						Set<OWLClass> oc = owlObject.getClassesInSignature();

						Iterator<OWLClass> classes = oc.iterator();

						if (classes.hasNext())
							getTree().setSelectedOWLObject(classes.next());
					}

				});

		addAction(
				new AbstractOWLTreeAction<OWLClass>("Add subclass",
						OWLIcons.getIcon("class.add.sub.png"), getTree()
						.getSelectionModel()) {
					private static final long serialVersionUID = -4067967212391062364L;

					public void actionPerformed(ActionEvent event) {
						createNewChild();
					}

					protected boolean canPerform(OWLClass cls) {
						return canCreateNewChild();
					}
				}, "A", "A");

		addAction(
				new AbstractOWLTreeAction<OWLClass>("Add sibling class",
						OWLIcons.getIcon("class.add.sib.png"), getTree().getSelectionModel()) {

					private static final long serialVersionUID = 9163133195546665441L;

					public void actionPerformed(ActionEvent event) {
						createNewSibling();
					}

					protected boolean canPerform(OWLClass cls) {
						return canCreateNewSibling();
					}
				}, "A", "B");

		addAction(new DeleteClassAction(getOWLEditorKit(),
				new OWLEntitySetProvider<OWLClass>() {
			public Set<OWLClass> getEntities() {
				return new HashSet<OWLClass>(getTree()
						.getSelectedOWLObjects());
			}
		}), "B", "A");


		new DropTarget(getTree(), this);


		getTree().setDropMode(DropMode.ON_OR_INSERT);

	}

	protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
		return getOWLModelManager().getOWLHierarchyManager()
				.getOWLClassHierarchyProvider();
	}

	public boolean canCreateNew() {
		return true;
	}

	public boolean canCreateNewChild() {
		return !getSelectedEntities().isEmpty();
	}

	public boolean canCreateNewSibling() {
		return !getSelectedEntities().isEmpty()
				&& !getSelectedEntity().equals(
						getOWLModelManager().getOWLDataFactory().getOWLThing());
	}

	public void createNewChild() {
		OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
		if (set != null) {
			OWLClass newClass = set.getOWLEntity();
			OWLClass selectedClass = getSelectedEntity();
			List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
			changes.addAll(set.getOntologyChanges());
			final OWLModelManager mngr = getOWLEditorKit().getModelManager();
			final OWLDataFactory df = mngr.getOWLDataFactory();
			if (!df.getOWLThing().equals(selectedClass)) {
				OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(
						set.getOWLEntity(), selectedClass);
				changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
			}
			mngr.applyChanges(changes);
			getTree().setSelectedOWLObject(newClass);
		}
	}

	public void createNewObject() {
		OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
		if (set != null) {
			OWLClass newClass = set.getOWLEntity();
			getOWLModelManager().applyChanges(set.getOntologyChanges());
			getTree().setSelectedOWLObject(newClass);
		}
	}

	public void createNewSibling() {
		OWLClass cls = getTree().getSelectedOWLObject();
		if (cls == null) {
			// Shouldn't really get here, because the
			// action should be disabled
			return;
		}
		// We need to apply the changes in the active ontology
		OWLEntityCreationSet<OWLClass> creationSet = getOWLWorkspace().createOWLClass();

		if (creationSet != null) {
			@SuppressWarnings("unchecked")
			OWLObjectTreeNode<OWLClass> parentNode = (OWLObjectTreeNode<OWLClass>) getTree()
			.getSelectionPath().getParentPath().getLastPathComponent();

			if (parentNode == null || parentNode.getOWLObject() == null) {
				return;
			}

			OWLClass parentCls = parentNode.getOWLObject();

			// Combine the changes that are required to create the OWLClass,
			// with the changes that are required to make it a sibling class.
			List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
			changes.addAll(creationSet.getOntologyChanges());
			OWLModelManager mngr = getOWLModelManager();
			OWLDataFactory df = mngr.getOWLDataFactory();
			if (!df.getOWLThing().equals(parentCls)) {
				changes.add(new AddAxiom(mngr.getActiveOntology(), df
						.getOWLSubClassOfAxiom(creationSet.getOWLEntity(),
								parentCls)));
			}
			mngr.applyChanges(changes);
			// Select the new class
			getTree().setSelectedOWLObject(creationSet.getOWLEntity());
		}
	}

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {

	}

	@Override
	public void dragExit(DropTargetEvent arg0) {


	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {

	}

	@Override
	public void drop(DropTargetDropEvent dtde) {

		// gets the cursor point
		Point pt = dtde.getLocation();

		OWLObjectTree<OWLClass> tr = getTree();

		// finds the current path to the cursor in the tree
		TreePath parentPath = tr.getClosestPathForLocation(pt.x, pt.y);

		// gets the node we are at
		OWLObjectTreeNode<?> n = (OWLObjectTreeNode<?>) parentPath
				.getLastPathComponent();

		// gets the object for that node
		OWLObject ob = (OWLObject) n.getOWLObject();

		// get all the classes for the object
		Set<OWLClass> classSet = ob.getClassesInSignature();

		// create an iterator for the set. the first element is the class we
		// need
		Iterator<OWLClass> it = classSet.iterator();
		OWLClass active = it.next();

		// get the data being transferred by the drop
		Transferable trans = dtde.getTransferable();

		if(trans.isDataFlavorSupported(dataFlavor)){

			OWLClassMessage msg = null;

			try {
				msg = (OWLClassMessage) trans.getTransferData(dataFlavor);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(msg.getOntology() != getOWLModelManager().getActiveOntology()){

				handleRemoteDrop(msg, active);

			} else {

				dtde.rejectDrop();
				return;

			}

			//expand the tree node we just dropped on
			tr.expandPath(parentPath);

			//drop is complete
			dtde.dropComplete(true);
		} else if(trans.isDataFlavorSupported(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR)) {
			handleLocalDrop(trans, active);

			//expand the tree node we just dropped on
			tr.expandPath(parentPath);

			//drop is complete
			dtde.dropComplete(true);

		} else {
			dtde.rejectDrop();
		}

	}

	private void handleRemoteDrop(OWLClassMessage msg, OWLClass active) {
		OWLAnnotationProperty importedFromProperty = getOWLDataFactory().getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000412"));
		OWLAnnotationProperty label = getOWLDataFactory().getRDFSLabel();
		
		OWLClass cls = msg.getOntClass();
		OWLOntology ontology = msg.getOntology();

		Set<OWLAnnotation> annotations = new HashSet<>(EntitySearcher.getAnnotations(cls.getIRI(), ontology));

		//getting annotations from the imports, too
		for(OWLOntology ont : ontology.getImports()){
			annotations.addAll(EntitySearcher.getAnnotations(cls.getIRI(), ont));
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
		addClass(cls, active, annotations, null);		
		
	}

	@SuppressWarnings("unchecked")
	private void handleLocalDrop(Transferable trans, OWLClass active) {
		
		
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
		
		OWLOntology ontology = this.getOWLModelManager().getActiveOntology();
			
		// get the proper manager, factory, ontology
		OWLModelManager mgr = getOWLModelManager();
		
		ontology = mgr.getActiveOntology();
		
		OWLClass additional = null;
		if(objects.get(0) instanceof OWLClass) {
			additional = (OWLClass) objects.get(0);
		} else {
			return;
		}
		
		// get the parent of the additional class.
		Set<OWLClassExpression> sup = new HashSet<>(EntitySearcher.getSuperClasses(additional, ontology));
		Iterator<OWLClassExpression> s = sup.iterator();
	
		// the first element in the superclass set is the parent 
		OWLClassExpression oc = null;
	
		if (s.hasNext()) {
			oc = s.next();
		}
	
		OWLClass fromParent = null;
		
		if(oc != null){
			fromParent = oc.asOWLClass();
		} 
		
		// get the set of annotations for the additional class
		Set<OWLAnnotation> anots = new HashSet<>(EntitySearcher.getAnnotations(additional.getIRI(), ontology));

		addClass(additional, active, anots, fromParent);		
	}


	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {

	}

	/**
	 * This method removes the current parent for a child and assigns it a new
	 * one while preserving the annotations
	 * 
	 * @param child
	 *            the class that is being dropped
	 * @param parent
	 *            the class being dropped onto
	 * @param annots
	 *            the annotations for the child
	 * @param fromParent
	 *            the parent of the child originally
	 */
	public void addClass(OWLClass child, OWLClass parent,
			Set<OWLAnnotation> annots, OWLClass fromParent) {

		if (child.equals(getOWLModelManager().getOWLDataFactory().getOWLThing())) {
			return;
		}
		
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		
		// add all the annotations into the child
		for (OWLAnnotation an : annots) {
			OWLAxiom ax = getOWLModelManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(child.getIRI(), an);
			changes.add(new AddAxiom((getOWLModelManager().getActiveOntology()), ax));
		}
		
		OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
		
		// only used for local moves
		if(fromParent != null){
			// remove original parent
			changes.add(new RemoveAxiom(getOWLModelManager().getActiveOntology(), df.getOWLSubClassOfAxiom(child, fromParent)));
		}
		
		// add new parent
		changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), df.getOWLDeclarationAxiom(child)));
		
		if (!df.getOWLThing().equals(parent)) {
			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), df.getOWLSubClassOfAxiom(child, parent)));
		}
		
		getOWLModelManager().applyChanges(changes);
		
	}
	
}

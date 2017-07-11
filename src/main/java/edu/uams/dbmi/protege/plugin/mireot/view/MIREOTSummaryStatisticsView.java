package edu.uams.dbmi.protege.plugin.mireot.view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

public class MIREOTSummaryStatisticsView extends AbstractActiveOntologyViewComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 12323L;

	private MIREOTMetricsPanel metricsPanel;

	@Override
	protected void initialiseOntologyView() throws Exception {
		metricsPanel = new MIREOTMetricsPanel(getOWLEditorKit());
		setLayout(new BorderLayout());
		add(metricsPanel);

	}

	@Override
	protected void disposeOntologyView() {
		// do nothing

	}

	@Override
	protected void updateView(OWLOntology activeOntology){
		metricsPanel.updateView(activeOntology);

		repaint();
	}

	private class MIREOTMetricsPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1423L;

		private int numClassesCopied = 0;
		private int numObjPropsCopied = 0;
		private int totalCopied = 0;

		private Map<String, Integer> numClassesPerOntologyCopied;
		private Map<String, Integer> numObjPropsPerOntologyCopied;

		private OWLEditorKit owlEditorKit;

		private DefaultTableModel totalStatsModel;
		private DefaultTableModel classStatsModel;
		private DefaultTableModel objPropsStatsModel;

		public MIREOTMetricsPanel(OWLEditorKit editorKit){
			this.owlEditorKit = editorKit;
			this.initialiseOWLView();
		}

		private void initialiseOWLView() {
			numClassesPerOntologyCopied = this.countClassesCopied();
			numObjPropsPerOntologyCopied = this.countObjPropsCopied();

			numClassesCopied = sum(numClassesPerOntologyCopied.values());
			numObjPropsCopied = sum(numObjPropsPerOntologyCopied.values());

			totalCopied = numClassesCopied + numObjPropsCopied;

			createUI();
			this.updateView(getActiveOntology());

		}

		public void updateView(OWLOntology activeOntology) {
			numClassesPerOntologyCopied = this.countClassesCopied();
			numObjPropsPerOntologyCopied = this.countObjPropsCopied();

			numClassesCopied = sum(numClassesPerOntologyCopied.values());
			numObjPropsCopied = sum(numObjPropsPerOntologyCopied.values());

			totalCopied = numClassesCopied + numObjPropsCopied;

			updateTableModels();

			repaint();
		}

		private void updateTableModels() {
			clearTableModel(totalStatsModel);
			clearTableModel(classStatsModel);
			clearTableModel(objPropsStatsModel);

			totalStatsModel.setColumnIdentifiers(new Object[]{"", "Number Imported"});
			totalStatsModel.addRow(new Object[]{"Total terms MIREOTed", totalCopied});
			totalStatsModel.addRow(new Object[]{"Total classes MIREOTed", numClassesCopied});
			totalStatsModel.addRow(new Object[]{"Total object properties MIREOTed", numObjPropsCopied});

			classStatsModel.setColumnIdentifiers(new Object[]{"URL", "Number Imported"});

			Set<String> keys = numClassesPerOntologyCopied.keySet();

			for(String key : keys){
				classStatsModel.addRow(new Object[]{key, numClassesPerOntologyCopied.get(key)});
			}

			objPropsStatsModel.setColumnIdentifiers(new Object[]{"URL", "Number Imported"});

			keys = numObjPropsPerOntologyCopied.keySet();

			for(String key : keys){
				objPropsStatsModel.addRow(new Object[]{key, numObjPropsPerOntologyCopied.get(key)});
			}
			
		}

		private void clearTableModel(final DefaultTableModel tableModel) {

			while (tableModel.getRowCount() > 0) {
				tableModel.removeRow(tableModel.getRowCount() - 1);
			}

		}

		private void createUI() {
			setLayout(new BorderLayout());
			Box box = new Box(BoxLayout.Y_AXIS);

			totalStatsModel = new DefaultTableModel();
			classStatsModel = new DefaultTableModel();
			objPropsStatsModel = new DefaultTableModel();

			updateTableModels();

			final JTable totalStatsTable = new JTable(totalStatsModel);
			totalStatsTable.setGridColor(Color.LIGHT_GRAY);
			totalStatsTable.setShowGrid(true);
			totalStatsTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

			final JTable classStatsTable = new JTable(classStatsModel);
			classStatsTable.setGridColor(Color.LIGHT_GRAY);
			classStatsTable.setShowGrid(true);
			classStatsTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

			final JTable objPropsStatsTable = new JTable(objPropsStatsModel);
			objPropsStatsTable.setGridColor(Color.LIGHT_GRAY);
			objPropsStatsTable.setShowGrid(true);
			objPropsStatsTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));  
			
			final JPanel totalStatsTablePanel = new JPanel(new BorderLayout());
			totalStatsTablePanel.add(totalStatsTable);
			totalStatsTablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 14, 2),
					ComponentFactory.createTitledBorder("Totals MIREOTed")));

			final JPanel classStatsTablePanel = new JPanel(new BorderLayout());
			classStatsTablePanel.add(classStatsTable);
			classStatsTablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 14, 2),
					ComponentFactory.createTitledBorder("Classes by URL")));

			final JPanel objPropsStatsTablePanel = new JPanel(new BorderLayout());
			objPropsStatsTablePanel.add(objPropsStatsTable);
			objPropsStatsTablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 14, 2),
					ComponentFactory.createTitledBorder("Object Properties by URL")));
			
			box.add(totalStatsTablePanel);
			box.add(classStatsTablePanel);
			box.add(objPropsStatsTablePanel);

			JScrollPane sp = new JScrollPane(box);
			sp.setOpaque(false);
			add(sp);
		}

		private Integer sum (Collection<Integer> list) {
			Integer sum = 0;

			for(Integer i : list){
				sum += i;
			}

			return sum;
		}

		private Map<String, Integer> countClassesCopied() {
			Map<String, Integer> counts = new HashMap<String, Integer>();

			OWLOntology onto = getActiveOntology();
			Set<OWLClass> classes = onto.getClassesInSignature();

			for(OWLClass cls : classes){
				Set<OWLAnnotation> annos = new HashSet<>(EntitySearcher.getAnnotations(cls.getIRI(), onto));

				for(OWLAnnotation anno : annos){
					if(anno.getProperty().getIRI().toString().equals("http://purl.obolibrary.org/obo/IAO_0000412")){
						if(anno.getValue() instanceof OWLLiteral){

							String location = ((OWLLiteral) anno.getValue()).getLiteral().toString();
							if(counts.containsKey(location)){
								counts.put(location, counts.get(location) + 1);
							} else {
								counts.put(location, 1);
							}
						}
					}
				}

			}


			return counts;

		}

		private Map<String, Integer> countObjPropsCopied() {
			Map<String, Integer> counts = new HashMap<String, Integer>();

			OWLOntology onto = getActiveOntology();
			Set<OWLObjectProperty> objProps = onto.getObjectPropertiesInSignature();

			for(OWLObjectProperty objProp : objProps){
				Set<OWLAnnotation> annos = new HashSet<>(EntitySearcher.getAnnotations(objProp.getIRI(), onto));

				for(OWLAnnotation anno : annos){
					if(anno.getProperty().getIRI().toString().equals("http://purl.obolibrary.org/obo/IAO_0000412")){
						if(anno.getValue() instanceof OWLLiteral){

							String location = ((OWLLiteral) anno.getValue()).getLiteral().toString();
							if(counts.containsKey(location)){
								counts.put(location, counts.get(location) + 1);
							} else {
								counts.put(location, 1);
							}
						}
					}
				}

			}


			return counts;

		}

		private OWLOntology getActiveOntology(){
			return owlEditorKit.getOWLModelManager().getActiveOntology();
		}

	}


}

package edu.uams.dbmi.protege.plugin.mireot.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import edu.uams.dbmi.protege.plugin.mireot.search.AdditionalOntologyList;
import edu.uams.dbmi.protege.plugin.mireot.search.AdditionalOntologySearcher;
import edu.uams.dbmi.protege.plugin.mireot.search.result.ClassSearchResult;
import edu.uams.dbmi.protege.plugin.mireot.search.result.ObjectPropertySearchResult;
import edu.uams.dbmi.protege.plugin.mireot.search.result.SearchResult;
import edu.uams.dbmi.protege.plugin.mireot.search.transferable.OWLClassTransferable;
import edu.uams.dbmi.protege.plugin.mireot.search.transferable.OWLObjectPropertyTransferable;


/**
 * View which allows users to search and copy from an additional ontology
 *
 * @author Chen Cheng, Josh Hanna
 */
public class AdditionalOntologyView extends AbstractOWLViewComponent {
    private static final long serialVersionUID = -4515710047558710080L;

    private JTextField searchBox;
    private JCheckBox showSearchCommentCheckBox;
    private JCheckBox showSearchLabelCheckBox;
    private JCheckBox showSearchDefinitionCheckBox;
    private JCheckBox clsCheckBox;
    private JCheckBox objPropCheckBox;

    private JButton executeButton;
    private OWLModelManagerListener listener;
    private static AdditionalOntologyList ddla = new AdditionalOntologyList();
    private String query;
    private ArrayList<SearchResult> resultList;
    private JTable resultTable;
    private JScrollPane scrollPane;
    private String[] columnNames = { "Entity Type", "Label", "URI", "Match Type" };
    public AdditionalOntologySearcher saoi;
    private DefaultTableModel tableModel = new DefaultTableModel();
    private String ontologyURL = null;
    private File ontoFile = null;

    private JTextField currentOntologyBox;

    private JComboBox ontologySelectBox;





    public AdditionalOntologySearcher getSearcher() {
        return saoi;
    }


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(10, 10));

        JComponent queryPanel = this.createQueryPanel();
        JComponent resultsPanel = this.createResultsPanel();

        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                queryPanel, resultsPanel);
        splitter.setDividerLocation(0.3);
        add(splitter, BorderLayout.CENTER);

        //setting up the searcher
        this.saoi = new AdditionalOntologySearcher();

        //setting it so that tooltips last longer
        //this will probably affect other tabs
        ToolTipManager.sharedInstance().setDismissDelay(100000);

    }

    private JComponent createQueryPanel(){
        JComponent queryPanel = new JPanel(new BorderLayout());

        JComponent ontologySelectPanel = createOntologySelectBox();
        JComponent searchPanel = createSearchPanel();

        queryPanel.add(ontologySelectPanel, BorderLayout.NORTH);
        queryPanel.add(searchPanel, BorderLayout.SOUTH);

        return queryPanel;
    }

    private JComponent createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel searchBoxHolder = new JPanel(new BorderLayout());

        searchBox = new JTextField();

        searchBoxHolder.add(searchBox, BorderLayout.NORTH);

        executeButton = new JButton("Search");

        AdditionalExecuteListener ael = new AdditionalExecuteListener();
        executeButton.addActionListener(ael);
        searchBox.addActionListener(ael);

        JPanel executeButtonHolder = new JPanel(new BorderLayout());;
        executeButtonHolder.add(executeButton, BorderLayout.WEST);

        JPanel searchHolder = new JPanel(new BorderLayout());

        searchHolder.add(searchBoxHolder, BorderLayout.NORTH);
        searchHolder.add(executeButtonHolder, BorderLayout.WEST);

        searchPanel.add(searchHolder, BorderLayout.NORTH);

        searchPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JComponent optionsBox = createOptionsBox();
        searchPanel.add(optionsBox, BorderLayout.SOUTH);

        searchPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createTitledBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        "Query"), BorderFactory.createEmptyBorder(3, 3,
                3, 3)));

        return searchPanel;
    }

    private JComponent createResultsPanel() {
        JComponent resultsPanel = new JPanel(new BorderLayout(10, 10));
        resultsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createTitledBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        "Query Results"), BorderFactory.createEmptyBorder(3, 3,
                3, 3)));
        Object[][] InitResultData = {};
        resultTable = new JTable(InitResultData, columnNames);
        resultTable.setFillsViewportHeight(true);
        resultTable.setModel(tableModel);
        resultTable.setRowSelectionAllowed(true);
        resultTable.setColumnSelectionAllowed(false);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.setTransferHandler(new FromTransferHandler());

        resultTable.setDragEnabled(true);
        scrollPane = new JScrollPane(resultTable);
        resultsPanel.add(scrollPane);

        return resultsPanel;
    }



    private JComponent createOntologySelectBox() {
        JComponent ontSelectPanel = new JPanel(new BorderLayout());

        
        this.ontologySelectBox = new JComboBox(ddla.getNames());
        this.ontologySelectBox.setEditable(true);

        /* - Joseph 2017 - removing glazed lists because of maven problems
        AutoCompleteSupport<String> support = AutoCompleteSupport.install(
                this.ontologySelectBox, GlazedLists.eventListOf(ddla.getNames()));
        support.setStrict(true);
        */
        
        JButton loadFromFileButton = new JButton("Load From File");
        JButton loadFromURLButton = new JButton("Load From URL");
        JButton selectFromListButton = new JButton("Select from list");
        
        LoadFromFileListener lffl = new LoadFromFileListener();
        loadFromFileButton.addActionListener(lffl);
        
        LoadFromURLListener lful = new LoadFromURLListener();
        loadFromURLButton.addActionListener(lful);

        SelectFromListListener sfll = new SelectFromListListener();
        selectFromListButton.addActionListener(sfll);

        
        JComponent ontSelectBoxHolder = new JPanel(new BorderLayout());
        
        ontSelectBoxHolder.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createTitledBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        "Please choose ontology"), BorderFactory
                .createEmptyBorder(3, 3, 3, 3)));

        JPanel buttonHolder = new JPanel();
        JPanel thirdButtonHolder = new JPanel();
        buttonHolder.add(loadFromURLButton, BorderLayout.WEST);
        buttonHolder.add(loadFromFileButton, BorderLayout.EAST);
        thirdButtonHolder.add(buttonHolder, BorderLayout.WEST);
        thirdButtonHolder.add(selectFromListButton, BorderLayout.EAST);
        
        ontSelectBoxHolder.add(thirdButtonHolder, BorderLayout.WEST);
        ontSelectPanel.add(ontSelectBoxHolder, BorderLayout.NORTH);

        this.currentOntologyBox = new JTextField();
        this.currentOntologyBox.setEditable(false);

        currentOntologyBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createTitledBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        "Ontology Location"), BorderFactory
                .createEmptyBorder(3, 3, 3, 3)));


        ontSelectPanel.add(this.currentOntologyBox, BorderLayout.SOUTH);

        return ontSelectPanel;
    }

    private JComponent createOptionsBox() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));

        Box searchByBox = new Box(BoxLayout.X_AXIS);
        searchByBox.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Search by"));

        Box searchBox = new Box(BoxLayout.X_AXIS);
        searchBox.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Entity Type"));

        clsCheckBox = new JCheckBox(
                new AbstractAction("Class") {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        if(clsCheckBox.isSelected()){
                            saoi.setSearchClassesFlag(true);
                        } else {
                            saoi.setSearchClassesFlag(false);
                        }
                    }
                });

        objPropCheckBox = new JCheckBox(
                new AbstractAction("Object Property") {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        if(objPropCheckBox.isSelected()){
                            saoi.setSearchObjectPropertiesFlag(true);
                        } else {
                            saoi.setSearchObjectPropertiesFlag(false);
                        }
                    }
                });

        //setting the class entity type as the default type to search for
        clsCheckBox.setSelected(true);

        searchBox.add(clsCheckBox);
        searchBox.add(Box.createHorizontalStrut(1));

        searchBox.add(objPropCheckBox);
        searchBox.add(Box.createHorizontalStrut(1));

        showSearchLabelCheckBox = new JCheckBox(
                new AbstractAction("Label") {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        if(showSearchLabelCheckBox.isSelected()){
                            saoi.setSearchByLabelFlag(true);
                        } else {
                            saoi.setSearchByLabelFlag(false);
                        }
                    }
                });

        //setting label checkbox as selected by default
        showSearchLabelCheckBox.setSelected(true);

        searchByBox.add(showSearchLabelCheckBox);
        searchByBox.add(Box.createHorizontalStrut(1));

        showSearchDefinitionCheckBox = new JCheckBox(
                new AbstractAction("Definition") {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        if(showSearchDefinitionCheckBox.isSelected()){
                            saoi.setSearchByDefinitionFlag(false);
                        } else {
                            saoi.setSearchByDefinitionFlag(false);
                        }
                    }
                });

        searchByBox.add(showSearchDefinitionCheckBox);
        searchByBox.add(Box.createHorizontalStrut(1));

        showSearchCommentCheckBox = new JCheckBox(
                new AbstractAction("Comment") {

                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        if(showSearchCommentCheckBox.isSelected()){
                            saoi.setSearchByCommentFlag(true);
                        } else {
                            saoi.setSearchByCommentFlag(false);
                        }

                    }
                });

        //setting comment checkbox as selected by default
        showSearchCommentCheckBox.setSelected(true);

        searchByBox.add(showSearchCommentCheckBox);
        searchByBox.add(Box.createHorizontalStrut(1));

        optionsPanel.add(searchBox);
        optionsPanel.add(searchByBox);

        optionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createTitledBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        "Query Options"), BorderFactory.createEmptyBorder(3, 3,
                3, 3)));

        return optionsPanel;
    }

    protected void disposeOWLView() {
        getOWLModelManager().removeListener(listener);
    }




    private class AdditionalExecuteListener implements ActionListener {


        public void actionPerformed(ActionEvent ee) {

            if(clsCheckBox.isSelected() || objPropCheckBox.isSelected()){

                query = searchBox.getText();
                saoi.setQuery(query);
                
                //URL takes precidence over file.  If both file and URL is null, an error is thrown during search
                saoi.setUrl(ontologyURL);
                saoi.setFile(ontoFile);
                

                try {
                    saoi.buildResultTable(columnNames, tableModel, resultTable);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
    
    private class LoadFromFileListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			final JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(currentOntologyBox);
			
			if(returnVal == JFileChooser.APPROVE_OPTION){
				ontoFile = fc.getSelectedFile();
				ontologyURL = null;
				
				currentOntologyBox.setText(ontoFile.getAbsolutePath());
			} 
		}
    	
    }
    
    private class LoadFromURLListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String url = (String) JOptionPane.showInputDialog(currentOntologyBox,
					"Ontology URL: ",
					"Open Ontology by URL",
					JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					null);
				
			if(url != null && !url.equals("")){
				ontologyURL = url;
				ontoFile = null;
				
		        currentOntologyBox.setText(ontologyURL);

			}
			
		}
    	
    }
    
    private class SelectFromListListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int returnVal = JOptionPane.showOptionDialog(currentOntologyBox, ontologySelectBox, "Choose from the list below.", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
			
			if(returnVal == JOptionPane.OK_OPTION){
				String selected = (String) ontologySelectBox.getSelectedItem();
				
		        ontologyURL = ddla.getLink(selected);
		        ontoFile = null;
		        
		        currentOntologyBox.setText(ontologyURL);

			}
			
		}
    	
    }




    class FromTransferHandler extends TransferHandler {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int getSourceActions(JComponent comp) {
            return COPY;
        }

        private int index = 0;

        public Transferable createTransferable(JComponent comp) {
            index = resultTable.getSelectedRow();
            resultList = getSearcher().getResults();
            SearchResult transferData = resultList.get(index);

            String ontologyLabel = (String) ontologySelectBox.getSelectedItem();
            String ontologyURL = ddla.getLink(ontologyLabel);

            if(transferData.getType().equals("Class")){
                ClassSearchResult classTransferData = (ClassSearchResult) transferData;

                return new OWLClassTransferable((OWLClass) classTransferData.getOWLEntity(), classTransferData.getOntology(), ontologyURL);

            } else if(transferData.getType().equals("Object Property")){
                ObjectPropertySearchResult objectPropertyTransferData = (ObjectPropertySearchResult) transferData;

                return new OWLObjectPropertyTransferable((OWLObjectProperty) objectPropertyTransferData.getOWLEntity(), objectPropertyTransferData.getOntology(), ontologyURL);
            }

            return null;
        }

        public void exportDone(JComponent comp, Transferable trans, int action) {

            if (action != MOVE) {
                return;
            }
        }


    }


}

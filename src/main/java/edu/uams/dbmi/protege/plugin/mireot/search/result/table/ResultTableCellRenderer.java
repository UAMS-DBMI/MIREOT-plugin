package edu.uams.dbmi.protege.plugin.mireot.search.result.table;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ResultTableCellRenderer extends JLabel implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3868063988973472570L;
	
	Map<Integer, String> tooltipStrings = new HashMap<Integer, String>();
	
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
	
		if (isSelected) {
			// do nothing
		}

		if (hasFocus) {
			// do nothing
		}

		if(value != null){
			// Configure the component with the specified value
			setText(value.toString());


			setToolTipText(this.wrapTooltip(this.tooltipStrings.get(rowIndex)));
		} else {
			setText("");
			setToolTipText(null);
		}

		// Since the renderer is a component, return itself
		return this;
	}
	
	private String wrapTooltip(String tooltip) {
		StringBuilder wrappedTooltip = new StringBuilder();
		String[] tooltipTokens = tooltip.split(" ");
		
		wrappedTooltip.append("<html>");
		
		int i = 0;
		for(String token : tooltipTokens ){
			wrappedTooltip.append(token);
			wrappedTooltip.append(" ");
			
			i++;
			if(i > 10){
				i = 0;
				wrappedTooltip.append("<br/>");
			}
			
		}
		
		wrappedTooltip.append("</html>");
		
		return wrappedTooltip.toString();
	}

	public void setTooltip(int rowIndex, String ttString){
		this.tooltipStrings.put(rowIndex, ttString);
	}

}

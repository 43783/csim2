/**
 * 
 */
package ch.hesge.csim2.ui.utils;

import java.awt.Component;
import java.io.File;

import javax.swing.DefaultCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

/**
 * Custom file editor in JTable.
 * 
 * @author Eric
 *
 */
@SuppressWarnings("serial")
public class FileTableCellEditor extends DefaultCellEditor implements TableCellEditor {
	
	// Private attributes
	private JTextField editorField;
	private String filepath = "";
	private static JFileChooser fileChooser = new JFileChooser();

	/**
	 * Default constructor.
	 */
	public FileTableCellEditor() {
		
		super(new JTextField());
		editorField = ((JTextField)getComponent());
		editorField.setBorder(null);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	}
	/**
	 * Return the path associated to folder
	 */
	@Override
	public Object getCellEditorValue() {
		return filepath;
	}

	/**
	 * Return the component used to select a folder 
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, final Object value, boolean isSelected, int row, int col) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				if (value != null) {			
					filepath = value.toString();
					fileChooser.setSelectedFile(new File(filepath));
				}
				
				if (fileChooser.showOpenDialog(editorField) == JFileChooser.APPROVE_OPTION) {
					filepath = fileChooser.getSelectedFile().getAbsolutePath();
				}
				
				fireEditingStopped();
			}
		});
		
		return editorField;
	}
}

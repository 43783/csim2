package ch.hesge.csim2.ui.combo;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import ch.hesge.csim2.core.model.Project;

@SuppressWarnings("serial")
public class ProjectComboBox extends JComboBox<Project> {

	// Private attributes
	List<Project> projects;

	/**
	 * Default constructor
	 */
	public ProjectComboBox(List<Project> projects) {
		this.projects = projects;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {
		initModel();
		initRenderer();
	}

	/**
	 * Initialize the component's model
	 */
	private void initModel() {

		// Initialize model		
		setModel(new DefaultComboBoxModel<Project>() {
			@Override
			public int getSize() {
				if (projects == null)
					return 0;
				return projects.size();
			}

			@Override
			public Project getElementAt(int row) {
				return projects.get(row);
			}
		});
	}

	/**
	 * Initialize the component's renderer
	 */
	private void initRenderer() {

		setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean hasFocus) {

				JLabel cellRenderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);

				if (value != null) {
					cellRenderer.setText(((Project) value).getName());
				}

				return cellRenderer;
			}
		});
	}
}

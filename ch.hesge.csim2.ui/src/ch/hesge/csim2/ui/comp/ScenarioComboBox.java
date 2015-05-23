package ch.hesge.csim2.ui.comp;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import ch.hesge.csim2.core.model.Scenario;

@SuppressWarnings("serial")
public class ScenarioComboBox extends JComboBox<Scenario> {

	// Private attributes
	List<Scenario> scenarios;

	/**
	 * Default constructor
	 */
	public ScenarioComboBox(List<Scenario> scenarios) {
		this.scenarios = scenarios;
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
		setModel(new DefaultComboBoxModel<Scenario>() {
			@Override
			public int getSize() {
				if (scenarios == null)
					return 0;
				return scenarios.size();
			}

			@Override
			public Scenario getElementAt(int row) {
				return scenarios.get(row);
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
					cellRenderer.setText(((Scenario) value).getName());
				}

				return cellRenderer;
			}
		});
	}
}

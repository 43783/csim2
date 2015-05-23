package ch.hesge.csim2.ui.comp;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import ch.hesge.csim2.core.model.Ontology;

@SuppressWarnings("serial")
public class OntologyComboBox extends JComboBox<Ontology> {

	// Private attributes
	List<Ontology> ontologies;

	/**
	 * Default constructor
	 */
	public OntologyComboBox(List<Ontology> ontologies) {
		this.ontologies = ontologies;
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
		setModel(new DefaultComboBoxModel<Ontology>() {
			@Override
			public int getSize() {
				if (ontologies == null)
					return 0;
				return ontologies.size();
			}

			@Override
			public Ontology getElementAt(int row) {
				return ontologies.get(row);
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
					cellRenderer.setText(((Ontology) value).getName());
				}

				return cellRenderer;
			}
		});
	}
}

package ch.hesge.csim2.ui.comp;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import ch.hesge.csim2.core.model.IMethodConceptMatcher;

@SuppressWarnings("serial")
public class MatcherComboBox extends JComboBox<IMethodConceptMatcher> {

	// Private attributes
	List<IMethodConceptMatcher> matcher;

	/**
	 * Default constructor
	 */
	public MatcherComboBox(List<IMethodConceptMatcher> matcher) {
		this.matcher = matcher;
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
		setModel(new DefaultComboBoxModel<IMethodConceptMatcher>() {
			@Override
			public int getSize() {
				if (matcher == null)
					return 0;
				return matcher.size();
			}

			@Override
			public IMethodConceptMatcher getElementAt(int row) {
				return matcher.get(row);
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
					cellRenderer.setText(((IMethodConceptMatcher) value).getName());
				}

				return cellRenderer;
			}
		});
	}
}

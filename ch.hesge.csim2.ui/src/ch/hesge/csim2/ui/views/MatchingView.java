package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.ui.comp.MatchingTable;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class MatchingView extends JPanel {

	// Private attribute
	private List<MethodConceptMatch> matchings;
	private MatchingTable matchingTable;

	/**
	 * Default constructor.
	 */
	public MatchingView(List<MethodConceptMatch> matchings) {
		this.matchings = matchings;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setLayout(new BorderLayout(0, 0));

		// Initialize matching table
		matchingTable = new MatchingTable(matchings);
		matchingTable.setFocusable(true);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(matchingTable);
		add(scrollPane);

		// Focus on chart panel
		SwingUtils.invokeWhenVisible(this, new Runnable() {
			@Override
			public void run() {
				matchingTable.requestFocus();
			}
		});
	}
}

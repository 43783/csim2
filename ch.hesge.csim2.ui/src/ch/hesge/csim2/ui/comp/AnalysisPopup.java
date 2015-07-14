package ch.hesge.csim2.ui.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ch.hesge.csim2.ui.views.ActionHandler;

@SuppressWarnings("serial")
public class AnalysisPopup extends JPopupMenu implements ActionListener {

	// Private attribute
	private ActionHandler actionHandler;
	private JMenuItem mnuSourceStem;
	private JMenuItem mnuConceptStem;
	private JMenuItem mnuMatching;
	private JMenuItem mnuTrace;
	private JMenuItem mnuTimeSeries;

	/**
	 * Default constructor
	 */
	public AnalysisPopup(ActionHandler actionHandler) {

		this.actionHandler = actionHandler;
		initComponent();
	}
	
	/**
	 * Initialize the component
	 */
	private void initComponent() {
		
		mnuSourceStem = new JMenuItem("Show stem sources");
		mnuSourceStem.addActionListener(this);
		add(mnuSourceStem);

		mnuConceptStem = new JMenuItem("Show stem concepts");
		mnuConceptStem.addActionListener(this);
		add(mnuConceptStem);

		mnuMatching = new JMenuItem("Show matching");
		mnuMatching.addActionListener(this);
		add(mnuMatching);

		mnuTrace = new JMenuItem("Show traces");
		mnuTrace.addActionListener(this);
		add(mnuTrace);
		
		mnuTimeSeries = new JMenuItem("Show timeseries");
		mnuTimeSeries.addActionListener(this);
		add(mnuTimeSeries);
	}
	
	/**
	 * Clear menu state
	 */
	public void clearMenuState() {	
		
		mnuSourceStem.setEnabled(false);
		mnuConceptStem.setEnabled(false);
		mnuMatching.setEnabled(false);
		mnuTrace.setEnabled(false);
		mnuTimeSeries.setEnabled(false);
	}
	

	/**
	 * Enable menus for source methods
	 */
	public void setSourceStemMenuState(boolean state) {
		mnuSourceStem.setEnabled(state);
	}

	/**
	 * Enable menus for step concepts
	 */
	public void setConceptStemMenuState(boolean state) {
		mnuConceptStem.setEnabled(state);
	}

	/**
	 * Enable menus for matching view
	 */
	public void setMatchingMenuState(boolean state) {
		mnuMatching.setEnabled(state);
	}
	
	/**
	 * Enable menus for trace analysis
	 */
	public void setTraceMenuState(boolean state) {
		mnuTrace.setEnabled(state);
	}

	/**
	 * Enable menus for chart analysis
	 */
	public void setTimeSeriesMenuState(boolean state) {
		mnuTimeSeries.setEnabled(state);
	}

	/**
	 * Handle action generated by menu.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == mnuSourceStem) {
			actionHandler.showSourceStems();
		}
		else if (e.getSource() == mnuConceptStem) {
			actionHandler.showConceptStems();
		}
		else if (e.getSource() == mnuSourceStem) {
			actionHandler.showSourceStems();
		}
		else if (e.getSource() == mnuTrace) {
			actionHandler.showTraceView();
		}
		else if (e.getSource() == mnuTimeSeries) {
			actionHandler.showTimeSeriesView();
		}
	}
}

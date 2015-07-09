package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.ScenarioStep;
import ch.hesge.csim2.ui.comp.ScenarioTable;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class ScenarioView extends JPanel implements ActionListener {

	// Private attributes
	private Scenario scenario;
	private ScenarioTable scenarioTable;
	private int currentStepIndex;
	private JButton btnStart;
	private JButton btnStop;

	/**
	 * Default constructor.
	 */
	public ScenarioView(Scenario scenario) {
		this.scenario = scenario;		
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setLayout(new BorderLayout(0, 0));

		scenarioTable = new ScenarioTable(scenario);
		scenarioTable.setFocusable(true);
		scenarioTable.setEnabled(scenario.getSteps().size() > 0);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(scenarioTable);
		add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(panel, BorderLayout.SOUTH);

		btnStart = new JButton("Start");
		btnStart.setEnabled(false);
		btnStart.setEnabled(scenario != null && scenario.getSteps().size() > 0);
		btnStart.addActionListener(this);
		panel.add(btnStart);

		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnStop.addActionListener(this);
		panel.add(btnStop);
		
		currentStepIndex = 0;
		selectScenarioStep(currentStepIndex);
		
		// Focus on scenario table
		SwingUtils.invokeWhenVisible(this, new Runnable() {
			@Override
			public void run() {
				scenarioTable.requestFocus();
			}
		});		
	}

	/**
	 * Start executing current scenario.
	 * 
	 * @param stepIndex
	 *        the index of the step to select
	 */
	private void selectScenarioStep(int stepIndex) {
		
		if (stepIndex < scenario.getSteps().size()) {
			scenarioTable.setRowSelectionInterval(stepIndex, stepIndex);
			scenarioTable.scrollRectToVisible(scenarioTable.getCellRect(stepIndex, 0, true));
			scenarioTable.invalidate();
		}
	}
	
	/**
	 * Start executing current scenario
	 */
	public void startScenario() {

		// Clear all scenario steps
		ApplicationLogic.resetExecutionTimes(scenario);

		// Update current step execution time
		ScenarioStep currentStep = scenario.getSteps().get(currentStepIndex);
		ApplicationLogic.initExecutionTime(currentStep);

		// Select next scenario step
		currentStepIndex++;
		selectScenarioStep(currentStepIndex);
		scenarioTable.setStopContextMenu();

		btnStart.setText("Next");
		btnStop.setText("Stop");
		btnStop.setEnabled(true);

		scenarioTable.repaint();
	}

	/**
	 * Stop executing current scenario
	 */
	public void nextStep() {

		// Update current step execution time
		ScenarioStep currentStep = scenario.getSteps().get(currentStepIndex);
		ApplicationLogic.initExecutionTime(currentStep);

		// Force current step to refresh
		selectScenarioStep(currentStepIndex);

		currentStepIndex++;

		if (currentStepIndex < scenario.getSteps().size()) {
			selectScenarioStep(currentStepIndex);
		}
		else {

			// Execution is completed
			int dialogResult = JOptionPane.showConfirmDialog(null, "Would you like to save execution times ?", "Action", JOptionPane.YES_NO_OPTION);

			if (dialogResult == JOptionPane.YES_OPTION) {

				try {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					ApplicationLogic.saveScenario(scenario);
				}
				finally {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
			else {
				ApplicationLogic.resetExecutionTimes(scenario);
			}

			currentStepIndex = 0;
			selectScenarioStep(currentStepIndex);

			btnStart.setText("Start");
			btnStop.setText("Stop");
			btnStop.setEnabled(false);

			scenarioTable.repaint();
		}
	}

	/**
	 * Stop executing current scenario
	 */
	public void stopScenario() {

		currentStepIndex = 0;
		selectScenarioStep(currentStepIndex);
		scenarioTable.setStartContextMenu();

		btnStart.setText("Start");
		btnStop.setText("Stop");
		btnStop.setEnabled(false);

		scenarioTable.repaint();
	}

	// Handle button event
	public void actionPerformed(ActionEvent e) {

		String actionSource = ((JButton) e.getSource()).getText();

		switch (actionSource) {
			case "Start": {
				startScenario();
				break;
			}
			case "Stop": {
				stopScenario();
				break;
			}
			case "Next": {
				nextStep();
				break;
			}
		}
	}
}

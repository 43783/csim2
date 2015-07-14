package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
	private ActionHandler actionHandler;
	private ScenarioTable scenarioTable;
	private int currentStepIndex;
	private JButton btnAdd;
	private JButton btnDel;
	private JButton btnSave;
	private JButton btnStart;
	private JButton btnStop;

	/**
	 * Default constructor.
	 */
	public ScenarioView(Scenario scenario, ActionHandler actionHandler) {
		this.scenario = scenario;
		this.actionHandler = actionHandler;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setLayout(new BorderLayout(0, 0));

		scenarioTable = new ScenarioTable(scenario, actionHandler);
		scenarioTable.setFocusable(true);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(scenarioTable);
		add(scrollPane, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BorderLayout(0, 0));
		add(btnPanel, BorderLayout.SOUTH);

		JPanel rightPanel = new JPanel();
		FlowLayout fl_rightPanel = (FlowLayout) rightPanel.getLayout();
		fl_rightPanel.setAlignment(FlowLayout.RIGHT);
		btnPanel.add(rightPanel, BorderLayout.EAST);

		JPanel leftPanel = new JPanel();
		FlowLayout fl_leftPanel = (FlowLayout) leftPanel.getLayout();
		fl_leftPanel.setAlignment(FlowLayout.LEFT);
		btnPanel.add(leftPanel, BorderLayout.WEST);

		btnAdd = new JButton("Add");
		btnAdd.setPreferredSize(new Dimension(80, 25));
		btnAdd.addActionListener(this);
		leftPanel.add(btnAdd);

		btnDel = new JButton("Delete");
		btnDel.setPreferredSize(new Dimension(80, 25));
		btnDel.addActionListener(this);
		leftPanel.add(btnDel);

		btnSave = new JButton("Save Times");
		btnSave.setPreferredSize(new Dimension(100, 25));
		btnSave.addActionListener(this);
		leftPanel.add(btnSave);

		btnStart = new JButton("Start");
		btnStart.setPreferredSize(new Dimension(80, 25));
		btnStart.addActionListener(this);
		rightPanel.add(btnStart);

		btnStop = new JButton("Stop");
		btnStop.setPreferredSize(new Dimension(80, 25));
		btnStop.setEnabled(false);
		btnStop.addActionListener(this);
		rightPanel.add(btnStop);

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
	 * Select current step within table
	 */
	private void selectScenarioStep(int newStepIndex) {

		if (newStepIndex < scenario.getSteps().size()) {
			scenarioTable.refresh();
			scenarioTable.setRowSelectionInterval(newStepIndex, newStepIndex);
			scenarioTable.scrollRectToVisible(scenarioTable.getCellRect(newStepIndex, 0, true));
			currentStepIndex = newStepIndex;
		}
	}

	/**
	 * Start executing current scenario
	 */
	public void startScenario() {

		btnStart.setText("Next");
		btnStop.setEnabled(true);

		// Clear all scenario steps times
		ApplicationLogic.resetExecutionTimes(scenario);

		// Select first step
		selectScenarioStep(0);
	}

	/**
	 * Stop executing current scenario
	 */
	private void nextScenarioStep() {

		// Update current step execution time
		ScenarioStep currentStep = scenario.getSteps().get(currentStepIndex);
		ApplicationLogic.initExecutionTime(currentStep);

		// Go to next step
		currentStepIndex++;

		if (currentStepIndex < scenario.getSteps().size()) {
			selectScenarioStep(currentStepIndex);
		}
		else {
			stopScenario();
		}
	}

	/**
	 * Stop executing current scenario
	 */
	public void stopScenario() {

		btnStart.setText("Start");
		btnStop.setEnabled(false);

		// Select first step
		selectScenarioStep(0);
	}

	// Handle button event
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnAdd) {
			actionHandler.createScenarioStep(scenario);
			scenarioTable.refresh();
		}
		else if (e.getSource() == btnDel) {
			ScenarioStep step = scenarioTable.getSelectedStep();
			actionHandler.deleteScenarioStep(scenario, step);
			scenarioTable.refresh();
		}
		else if (e.getSource() == btnSave) {
			actionHandler.saveScenario(scenario);
		}
		else if (e.getSource() == btnStart) {

			String btnText = ((JButton) e.getSource()).getText();

			if (btnText.equals("Start")) {
				startScenario();
			}
			else if (btnText.equals("Next")) {
				nextScenarioStep();
			}
		}
		else if (e.getSource() == btnStop) {
			stopScenario();
		}
	}
}

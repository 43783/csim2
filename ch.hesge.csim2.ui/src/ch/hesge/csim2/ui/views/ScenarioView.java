package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;

import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.ScenarioStep;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.popup.ScenarioStepPopup;
import ch.hesge.csim2.ui.table.ScenarioTable;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class ScenarioView extends JPanel implements ActionListener {

	// Private attributes
	private Scenario scenario;
	private ApplicationManager appManager;
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
	public ScenarioView(Scenario scenario) {
		this.scenario = scenario;
		this.appManager = ApplicationManager.UNIQUE_INSTANCE;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setLayout(new BorderLayout(0, 0));

		scenarioTable = new ScenarioTable(scenario);
		scenarioTable.setFillsViewportHeight(true);
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
		btnStop.addActionListener(this);
		rightPanel.add(btnStop);

		currentStepIndex = 0;
		selectScenarioStep(currentStepIndex);
		
		refreshBtnState();
		initListeners();
	}

	/**
	 * Initialize component listeners
	 */
	private void initListeners() {

		// Set focus when visible
		SwingUtils.onComponentVisible(this, new SimpleAction<Object>() {
			@Override
			public void run(Object o) {
				scenarioTable.requestFocus();
			}
		});
		
		// Listen to selection
		SwingUtils.onTableSelection(scenarioTable, new SimpleAction<ListSelectionEvent>() {
			@Override
			public void run(ListSelectionEvent e) {
				refreshBtnState();
			}
		});

		// Listen to selection
		SwingUtils.onTableDoubleClick(scenarioTable, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				refreshBtnState();
				appManager.editScenarioStep(scenarioTable.getSelectedObject());
			}
		});
		
		// Listen to right-click
		SwingUtils.onTableRightClick(scenarioTable, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				
				// Show context menu
				ScenarioStepPopup popup = new ScenarioStepPopup();
				popup.setScenarioStep(scenarioTable.getSelectedObject());
				popup.show(e.getComponent(), e.getX(), e.getY());
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
	 * Refresh button state
	 */
	public void refreshBtnState() {
		
		if (btnStart.getText().equals("Start")) {
			btnAdd.setEnabled(true);
			btnDel.setEnabled(scenarioTable.getSelectedObject() != null);
			btnSave.setEnabled(scenario.getSteps().size() > 0);
			btnStart.setEnabled(scenario.getSteps().size() > 0);
			btnStop.setEnabled(false);		
		}
		else {
			btnAdd.setEnabled(false);
			btnDel.setEnabled(false);
			btnSave.setEnabled(false);
			btnStart.setEnabled(true);
			btnStop.setEnabled(true);		
		}
	}
	
	/**
	 * Start executing current scenario
	 */
	public void startScenario() {

		btnStart.setText("Next");
		btnStop.setEnabled(true);

		// Clear all scenario steps times
		appManager.resetExecutionTimes(scenario);

		// Select first step
		selectScenarioStep(0);
	}

	/**
	 * Stop executing current scenario
	 */
	private void nextScenarioStep() {

		// Update current step execution time
		ScenarioStep currentStep = scenario.getSteps().get(currentStepIndex);
		appManager.initExecutionTime(currentStep);

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
			appManager.createScenarioStep(scenario);
			scenarioTable.refresh();
			refreshBtnState();
		}
		else if (e.getSource() == btnDel) {
			ScenarioStep step = scenarioTable.getSelectedObject();
			appManager.deleteScenarioStep(scenario, step);
			scenarioTable.refresh();
			refreshBtnState();
		}
		else if (e.getSource() == btnSave) {
			appManager.saveScenario(scenario);
		}
		else if (e.getSource() == btnStart) {

			String btnText = ((JButton) e.getSource()).getText();

			if (btnText.equals("Start")) {
				startScenario();
			}
			else if (btnText.equals("Next")) {
				nextScenarioStep();
			}

			refreshBtnState();
		}
		else if (e.getSource() == btnStop) {
			stopScenario();
			refreshBtnState();
		}

		scenarioTable.requestFocus();
	}
}

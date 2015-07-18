package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.TimeSeries;
import ch.hesge.csim2.core.utils.SimpleVector;
import ch.hesge.csim2.ui.combo.MatcherComboBox;
import ch.hesge.csim2.ui.combo.ScenarioComboBox;
import ch.hesge.csim2.ui.dialogs.TimeSeriesDialog;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class TimeSeriesView extends JPanel implements ActionListener {

	// Private attribute
	private Project project;
	private ApplicationManager appManager;
	private List<Scenario> scenarios;
	private List<Concept> selectedConcepts;
	private TimeSeries timeSeries;
	private TimeSeries filteredSeries;
	private int segmentCount;
	private double threshold;
	private boolean isLegendVisible;

	private ScenarioComboBox scenarioComboBox;
	private MatcherComboBox matcherComboBox;
	private JButton loadBtn;
	private JButton settingsBtn;

	public static int DEFAULT_SEGMENT_COUNT = 200;
	public static double DEFAULT_THRESHOLD  = 0.3d;

	/**
	 * Default constructor.
	 */
	public TimeSeriesView(Project project) {

		this.project = project;
		this.appManager = ApplicationManager.UNIQUE_INSTANCE;
		this.scenarios = appManager.getScenarios(project);
		this.segmentCount = DEFAULT_SEGMENT_COUNT;
		this.threshold = DEFAULT_THRESHOLD;
		this.isLegendVisible = false;
		
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setLayout(new BorderLayout(0, 0));

		// Create the parameters panel
		JPanel paramsPanel = new JPanel();
		paramsPanel.setLayout(new BorderLayout(0, 0));
		this.add(paramsPanel, BorderLayout.NORTH);

		// Create the scenario selection panel
		JPanel scenarioPanel = new JPanel();
		FlowLayout flowLayout1 = (FlowLayout) scenarioPanel.getLayout();
		flowLayout1.setAlignment(FlowLayout.LEFT);
		JLabel scenarioLabel = new JLabel("Scenario:");
		scenarioPanel.add(scenarioLabel);
		scenarioComboBox = new ScenarioComboBox(scenarios);
		scenarioComboBox.setPreferredSize(new Dimension(150, scenarioComboBox.getPreferredSize().height));
		scenarioPanel.add(scenarioComboBox);
		
		// Create the matcher selection panel
		JLabel matchingLabel = new JLabel("Matching:");
		scenarioPanel.add(matchingLabel);		
		List<IMethodConceptMatcher> matchers = appManager.getMatchers();
		matcherComboBox = new MatcherComboBox(matchers);
		matcherComboBox.setPreferredSize(new Dimension(150, 20));
		scenarioPanel.add(matcherComboBox);
		
		// Create the load button
		loadBtn = new JButton("Load");
		loadBtn.setPreferredSize(new Dimension(80, 25));
		loadBtn.addActionListener(this);
		scenarioPanel.add(loadBtn);
		paramsPanel.add(scenarioPanel, BorderLayout.CENTER);

		// Create the settings panel
		JPanel settingsPanel = new JPanel();
		FlowLayout flowLayout2 = (FlowLayout) settingsPanel.getLayout();
		flowLayout2.setAlignment(FlowLayout.RIGHT);

		settingsBtn = new JButton("Settings");
		settingsBtn.setPreferredSize(new Dimension(80, 25));
		settingsBtn.setEnabled(false);
		settingsBtn.addActionListener(this);
		settingsPanel.add(settingsBtn);
		paramsPanel.add(settingsPanel, BorderLayout.EAST);

		initChartPanel();
		initListeners();
	}

	/**
	 * Initialize the chart displaying graph.
	 */
	private void initChartPanel() {

		// Remove previous chart panel
		BorderLayout layout = (BorderLayout) this.getLayout();
		Component oldChartPanel = layout.getLayoutComponent(BorderLayout.CENTER);
		if (oldChartPanel != null) {
			this.remove(oldChartPanel);
		}
		
		XYSeriesCollection dataSet = new XYSeriesCollection();
		
		// Populate dataset with timeseries values
		if (filteredSeries != null && filteredSeries.getTraceMatrix() != null) {

			for (int i = 0; i < filteredSeries.getTraceMatrix().getRowDimension(); i++) {

				Concept concept = filteredSeries.getTraceConcepts().get(i);
				XYSeries series = new XYSeries(concept.getName());

				// Populate series with trace matrix row
				SimpleVector rowVector = filteredSeries.getTraceMatrix().getRowVector(i);
				for (int j = 0; j < rowVector.getDimension(); j++) {
					series.add(j + 1, rowVector.getValue(j));
				}

				dataSet.addSeries(series);
			}
		}
		
		// Create a line renderer
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setBaseShapesVisible(false);

		// Create a tooltip renderer for timeseries values
		String tooltipFormat = "<html>concept: {0}<br>segment n°: {1}<br>occurrences: {2}";		
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(tooltipFormat, NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance()));
		
		// Create the vertical axis		
		NumberAxis yAxis = new NumberAxis("concept occurrences");
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// Create the horizontal axis		
		NumberAxis xAxis = new NumberAxis("method segments");
		xAxis.setAutoRangeIncludesZero(false);
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// Create the graph plot		
		XYPlot plot = new XYPlot(dataSet, xAxis, yAxis, renderer);
		plot.setOutlinePaint(Color.WHITE);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

		// Create the graph to draw		
		JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		chart.setBackgroundPaint(Color.WHITE);

		ChartPanel newChartPanel = new ChartPanel(chart);
		newChartPanel.setMouseWheelEnabled(true);
		newChartPanel.setInitialDelay(0); // tooltip delay
		newChartPanel.restoreAutoBounds();

		if (!isLegendVisible) {
			newChartPanel.getChart().removeLegend();
		}
		
		this.add(newChartPanel, BorderLayout.CENTER);
		this.revalidate();
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Set focus when visible
		SwingUtils.onComponentVisible(this, new SimpleAction<Object>() {
			@Override
			public void run(Object o) {
				scenarioComboBox.requestFocus();
			}
		});
	}

	/**
	 * Handle action generated by button.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == loadBtn) {
			
			Scenario scenario = (Scenario) scenarioComboBox.getSelectedItem();
			IMethodConceptMatcher matcher = (IMethodConceptMatcher) matcherComboBox.getSelectedItem();

			if (scenario == null || matcher == null) {
				filteredSeries = null;
				initChartPanel();
			}
			else {

				SwingUtils.invokeLongOperation(TimeSeriesView.this, new Runnable() {
					@Override
					public void run() {

						// Retrieve timeseries associated to the current scenario
						timeSeries = appManager.getTimeSeries(project, scenario, matcher);

						// Extract segmented information
						filteredSeries = appManager.getFilteredTimeSeries(timeSeries, DEFAULT_SEGMENT_COUNT, DEFAULT_THRESHOLD, null);

						// Keep concepts found, for future use in dialog
						selectedConcepts = filteredSeries.getTraceConcepts();
						
						// Initialize chart
						initChartPanel();
						settingsBtn.setEnabled(true);
						loadBtn.requestFocus();
					}
				});
			}			
		}
		else if (e.getSource() == settingsBtn) {

			// Display dialog
			MainView mainView = (MainView) SwingUtilities.getAncestorOfClass(MainView.class, this);
			TimeSeriesDialog dialog = new TimeSeriesDialog(mainView);

			dialog.setTimeSeries(timeSeries);
			dialog.setThreshold(threshold);
			dialog.setSegmentCount(segmentCount);
			dialog.setSelectedConcepts(selectedConcepts);
			dialog.setShowLegend(isLegendVisible);
			dialog.setVisible(true);

			// Refresh view with new parameters
			if (dialog.getDialogResult()) {

				segmentCount = dialog.getSegmentCount();
				isLegendVisible = dialog.isShowLegend();

				// If threshold has changed, we need all concepts
				if (threshold != dialog.getThreshold()) {
					threshold = dialog.getThreshold();
					selectedConcepts.clear();
				}
				else {
					selectedConcepts = dialog.getSelectedConcepts();
				}

				SwingUtils.invokeLongOperation(TimeSeriesView.this, new Runnable() {
					@Override
					public void run() {

						// Retrieve filtered timeseries 
						filteredSeries = appManager.getFilteredTimeSeries(timeSeries, segmentCount, threshold, selectedConcepts);

						// Keep concepts found, for future use in dialog
						selectedConcepts = filteredSeries.getTraceConcepts();

						// Initialize chart
						initChartPanel();
						settingsBtn.requestFocus();
					}
				});
			}
		}
	}
}

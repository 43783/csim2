package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.linear.RealVector;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.TimeSeries;
import ch.hesge.csim2.ui.comp.ScenarioComboBox;
import ch.hesge.csim2.ui.dialogs.TimeSeriesDialog;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class TimeSeriesView extends JPanel implements ActionListener {

	// Private attribute
	private Project project;
	private List<Scenario> scenarios;
	private List<Concept> selectedConcepts;
	private TimeSeries timeSeries;
	private TimeSeries filteredSeries;
	private int segmentCount;
	private double threshold;
	private boolean showLegend;

	private ScenarioComboBox scenarioComboBox;
	private JButton loadBtn;
	private JButton settingsBtn;

	public static int DEFAULT_SEGMENT_COUNT = 20;
	public static double DEFAULT_THRESHOLD = 0.5d;
	public static int MAX_SEGMENT_COUNT = 1000;

	/**
	 * Default constructor.
	 */
	public TimeSeriesView(Project project, List<Scenario> scenarios) {

		this.segmentCount = DEFAULT_SEGMENT_COUNT;
		this.threshold = DEFAULT_THRESHOLD;
		this.project = project;
		this.scenarios = scenarios;
		this.showLegend = false;
		
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Time series", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		setLayout(new BorderLayout(0, 0));

		// Create the parameters panel
		JPanel parameterPanel = new JPanel();
		parameterPanel.setLayout(new BorderLayout(0, 0));
		this.add(parameterPanel, BorderLayout.NORTH);

		// Create the scenario panel
		JPanel scenarioPanel = new JPanel();
		FlowLayout flowLayout1 = (FlowLayout) scenarioPanel.getLayout();
		flowLayout1.setAlignment(FlowLayout.LEFT);
		JLabel scenarioLabel = new JLabel("Scenario:");
		scenarioPanel.add(scenarioLabel);
		scenarioComboBox = new ScenarioComboBox(scenarios);
		scenarioComboBox.setPreferredSize(new Dimension(150, scenarioComboBox.getPreferredSize().height));
		scenarioPanel.add(scenarioComboBox);
		loadBtn = new JButton("Load scenario");
		scenarioPanel.add(loadBtn);
		parameterPanel.add(scenarioPanel, BorderLayout.CENTER);

		// Create the settings panel
		JPanel settingsPanel = new JPanel();
		FlowLayout flowLayout2 = (FlowLayout) settingsPanel.getLayout();
		flowLayout2.setAlignment(FlowLayout.RIGHT);
		settingsBtn = new JButton("Settings");
		settingsBtn.setEnabled(false);
		settingsBtn.addActionListener(this);
		settingsPanel.add(settingsBtn);
		parameterPanel.add(settingsPanel, BorderLayout.EAST);

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
				RealVector rowVector = filteredSeries.getTraceMatrix().getRowVector(i);
				for (int j = 0; j < rowVector.getDimension(); j++) {
					series.add(j + 1, rowVector.getEntry(j));
				}

				dataSet.addSeries(series);
			}
		}
		
		// Create a smooth line renderer
		XYSplineRenderer renderer = new XYSplineRenderer();
		renderer.setBaseShapesVisible(false);
		renderer.setSeriesPaint(0, Color.GREEN);
		renderer.setPrecision(5);
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		
		// Create the vertical axis		
		NumberAxis yAxis = new NumberAxis("concepts");
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// Create the horizontal axis		
		NumberAxis xAxis = new NumberAxis("methods");
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
		newChartPanel.restoreAutoBounds();

		if (!showLegend) {
			newChartPanel.getChart().removeLegend();
		}
		
		this.add(newChartPanel, BorderLayout.CENTER);
		this.revalidate();
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Add listener to load button
		loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Scenario scenario = (Scenario) scenarioComboBox.getSelectedItem();

				if (scenario == null) {
					filteredSeries = null;
					initChartPanel();
				}
				else {

					SwingUtils.invokeLongOperation(TimeSeriesView.this, new Runnable() {
						@Override
						public void run() {

							// Retrieve timeseries associated to current scenario
							timeSeries = ApplicationLogic.getTimeSeries(project, scenario);

							// Reset current settings
							segmentCount = DEFAULT_SEGMENT_COUNT;
							threshold = DEFAULT_THRESHOLD;

							// Extract segmented information
							filteredSeries = ApplicationLogic.getFilteredTimeSeries(timeSeries, segmentCount, threshold, null);

							// Update view attributes
							selectedConcepts = new ArrayList<>(filteredSeries.getTraceConcepts());
							settingsBtn.setEnabled(true);
							initChartPanel();
						}
					});
				}
			}
		});
	}

	/**
	 * Handle action generated by menu.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == settingsBtn) {

			MainView mainView = (MainView) SwingUtilities.getAncestorOfClass(MainView.class, this);

			// Display dialog
			TimeSeriesDialog dialog = new TimeSeriesDialog(mainView);

			dialog.setTimeSeries(timeSeries);
			dialog.setThreshold(threshold);
			dialog.setSegmentCount(segmentCount);
			dialog.setSelectedConcepts(selectedConcepts);
			dialog.setShowLegend(showLegend);
			dialog.setVisible(true);

			// Refresh view with new parameters
			if (dialog.getDialogResult()) {

				segmentCount = dialog.getSegmentCount();
				showLegend = dialog.isShowLegend();

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
						filteredSeries = ApplicationLogic.getFilteredTimeSeries(timeSeries, segmentCount, threshold, selectedConcepts);

						// Update view attributes
						selectedConcepts = new ArrayList<>(filteredSeries.getTraceConcepts());
						initChartPanel();
					}
				});
			}
		}
	}
}

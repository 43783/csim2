package ch.hesge.csim2.ui.comp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.linear.RealVector;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.TimeSeries;

@SuppressWarnings("serial")
public class TimeSeriesGraph extends JPanel {

	// Private attributes
	private XYSeriesCollection dataSet;
	private boolean showLegend;

	/**
	 * Default constructor
	 */
	public TimeSeriesGraph() {
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {		
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Chart", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		setOpaque(true);
	}

	/**
	 * Initialize the timeseries to display.
	 * 
	 * @param timeSeries
	 */
	public void setTimeSeries(TimeSeries timeSeries) {

		// Update series to display
		if (timeSeries != null) {

			dataSet = new XYSeriesCollection();

			// Create the series to display
			for (int i = 0; i < timeSeries.getTraceMatrix().getRowDimension(); i++) {

				Concept concept = timeSeries.getTraceConcepts().get(i);
				XYSeries series = new XYSeries(concept.getName());

				// Populate series with trace matrix row
				RealVector rowVector = timeSeries.getTraceMatrix().getRowVector(i);
				for (int j = 0; j < rowVector.getDimension(); j++) {
					series.add(j+1, rowVector.getEntry(j));				}

				dataSet.addSeries(series);
			}
		}
		else {
			dataSet = null;
		}

		// Refresh graph
		repaint();
	}

	/**
	 * Defined if legend should be displayed or not
	 * 
	 * @param showLegend
	 */
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}
	
	/**
	 * Repaint the surface area.
	 */
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (dataSet != null) {

			// Create the vertical axis		
			NumberAxis yAxis = new NumberAxis("occurences");
			yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

			// Create the horizontal axis		
			NumberAxis xAxis = new NumberAxis("segments");
			xAxis.setAutoRangeIncludesZero(false);
			xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

			// Create the graph plot		
			XYPlot plot = new XYPlot(dataSet, xAxis, yAxis, new XYLineAndShapeRenderer(true, false));
			plot.setOutlinePaint(Color.WHITE);
			plot.setBackgroundPaint(Color.WHITE);
			plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
			plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
			plot.setOrientation(PlotOrientation.VERTICAL);
			
			// Create the graph to draw		
			JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			chart.setBackgroundPaint(Color.WHITE);

			if (!showLegend) {
				chart.removeLegend();
			}
			
			// Draw the chart
	        int x = getInsets().left;
	        int y = getInsets().top;
	        int w = getSize().width - getInsets().left - getInsets().right - 1;
	        int h = getSize().height - getInsets().top - getInsets().bottom - 1;
			Rectangle bounds = new Rectangle(x, y, w, h);
			chart.draw((Graphics2D) g, bounds);
		}
	}
}

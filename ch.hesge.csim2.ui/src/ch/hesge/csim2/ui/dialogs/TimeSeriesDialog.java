package ch.hesge.csim2.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.TimeSeries;
import ch.hesge.csim2.ui.comp.ConceptTable;
import ch.hesge.csim2.ui.utils.SwingUtils;
import ch.hesge.csim2.ui.views.TimeSeriesView;

@SuppressWarnings("serial")
public class TimeSeriesDialog extends JDialog implements ActionListener, ChangeListener {

	// Private attributes
	private TimeSeries timeSeries;
	private int traceSize;
	private double threshold;
	private int segmentCount;
	private int segmentSize;
	private boolean isShowLegend;
	private boolean isClearSelection;

	private JTextField thresholdField;
	private JSlider thresholdSlider;
	
	private JTextField segmentCountField;
	private JSlider segmentCountSlider;
	
	private JTextField segmentSizeField;
	private JSlider segmentSizeSlider;

	private JButton btnReset;
	private JButton btnSelection;
	private JButton btnOK;
	private JButton btnCancel;
	private boolean dialogResult;
	
	private ConceptTable conceptTable;
	private JCheckBox showLegendCheckbox;
	private JTextField traceSizeField;
	private JLabel traceSizeLabel;
	private JPanel btnLeftPane;
	private JPanel btnRightPane;

	/**
	 * Create the dialog with owner.
	 */
	public TimeSeriesDialog(Window parent) {
		super(parent);
		
		this.isClearSelection = false;
		this.isShowLegend = false;
		this.segmentCount = TimeSeriesView.DEFAULT_SEGMENT_COUNT;
		this.threshold = TimeSeriesView.DEFAULT_THRESHOLD;

		initComponents();
	}

	/**
	 * Initialize the view components
	 */
	private void initComponents() {

		// Dialog configuration
		setTitle("TimeSeries Settings");
		setBounds(0, 0, 476, 568);
		setLocationRelativeTo(getParent());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		setResizable(false);

		// Create layout structure
		JPanel mainPane = new JPanel();
		getContentPane().add(mainPane, BorderLayout.CENTER);
		JPanel btnPane = new JPanel();
		getContentPane().add(btnPane, BorderLayout.SOUTH);
		btnPane.setLayout(new BorderLayout(0, 0));
		
		// Initialize button pane
		btnLeftPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) btnLeftPane.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		btnPane.add(btnLeftPane, BorderLayout.WEST);
		btnRightPane = new JPanel();
		btnPane.add(btnRightPane, BorderLayout.CENTER);
		btnRightPane.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
				
		btnReset = new JButton("Reset");
		btnLeftPane.add(btnReset);
		btnReset.setPreferredSize(new Dimension(100, 25));
		btnReset.addActionListener(this);
		
		btnSelection = new JButton("Selection");
		btnLeftPane.add(btnSelection);
		btnSelection.setPreferredSize(new Dimension(100, 25));
		btnSelection.addActionListener(this);
		
		btnOK = new JButton("OK");
		btnRightPane.add(btnOK);
		btnOK.setPreferredSize(new Dimension(100, 25));
		btnOK.addActionListener(this);
		
		btnCancel = new JButton("Cancel");
		btnRightPane.add(btnCancel);
		btnCancel.setPreferredSize(new Dimension(80, 25));
		btnCancel.addActionListener(this);
		
		mainPane.setLayout(null);

		// Create threshold fields
		JLabel thresholdLabel = new JLabel("Weight threshold:");
		thresholdLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		thresholdLabel.setBounds(10, 58, 115, 23);
		mainPane.add(thresholdLabel);
		thresholdField = new JTextField();
		thresholdField.setBounds(130, 58, 61, 23);
		thresholdField.setColumns(10);
		mainPane.add(thresholdField);
		thresholdSlider = new JSlider(1, 1000);
		thresholdSlider.setBounds(201, 58, 244, 23);
		thresholdSlider.setMajorTickSpacing(200);
		thresholdSlider.setMinorTickSpacing(100);
		thresholdSlider.setPaintTicks(true);
		thresholdSlider.addChangeListener(this);
		mainPane.add(thresholdSlider);

		// Create segment count fields
		JLabel segmentCountLabel = new JLabel("Segment count:");
		segmentCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		segmentCountLabel.setBounds(10, 92, 115, 23);
		mainPane.add(segmentCountLabel);
		segmentCountField = new JTextField();
		segmentCountField.setColumns(10);
		segmentCountField.setBounds(130, 92, 61, 23);
		mainPane.add(segmentCountField);
		segmentCountSlider = new JSlider(1, 1000);
		segmentCountSlider.setBounds(201, 92, 244, 23);
		segmentCountSlider.addChangeListener(this);
		mainPane.add(segmentCountSlider);

		// Create segment size field
		JLabel segmentSizeLabel = new JLabel("Segment size:");
		segmentSizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		segmentSizeLabel.setBounds(10, 126, 115, 23);
		mainPane.add(segmentSizeLabel);
		segmentSizeField = new JTextField();
		segmentSizeField.setColumns(10);
		segmentSizeField.setBounds(130, 126, 61, 23);
		mainPane.add(segmentSizeField);
		segmentSizeSlider = new JSlider(1, 1000);
		segmentSizeSlider.setBounds(201, 126, 244, 23);
		segmentSizeSlider.addChangeListener(this);
		mainPane.add(segmentSizeSlider);				
		
		// Create concept panel
		JPanel conceptPanel = new JPanel();
		conceptPanel.setBorder(new TitledBorder(null, "Trace concepts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		conceptPanel.setBounds(10, 160, 450, 304);
		conceptPanel.setLayout(new BorderLayout(0, 0));
		conceptTable = new ConceptTable();
		JScrollPane scrollbar = new JScrollPane();
		scrollbar.setViewportView(conceptTable);
		conceptPanel.add(scrollbar, BorderLayout.CENTER);
		mainPane.add(conceptPanel);
		
		// Create show legend checkbox
		showLegendCheckbox = new JCheckBox("Show legend");
		showLegendCheckbox.setBounds(352, 469, 108, 23);
		showLegendCheckbox.addActionListener(this);
		mainPane.add(showLegendCheckbox);
		
		traceSizeField = new JTextField();
		traceSizeField.setEditable(false);
		traceSizeField.setColumns(10);
		traceSizeField.setBounds(130, 24, 61, 23);
		mainPane.add(traceSizeField);
		
		traceSizeLabel = new JLabel("Trace size:");
		traceSizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		traceSizeLabel.setBounds(10, 24, 115, 23);
		mainPane.add(traceSizeLabel);
		
		initListeners();
	}

	/**
	 * Initialize component listeners
	 */
	private void initListeners() {

		thresholdField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {

				try {
					double newValue = NumberFormat.getInstance().parse(thresholdField.getText()).doubleValue();
					setThreshold(newValue);
				}
				catch (ParseException e1) {
					// Ignore
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});

		segmentCountField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {

				try {
					int newValue = NumberFormat.getInstance().parse(segmentCountField.getText()).intValue();
					setSegmentCount(newValue);
				}
				catch (ParseException e1) {
					// Ignore
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});

		segmentSizeField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {

				try {
					int newValue = NumberFormat.getInstance().parse(segmentSizeField.getText()).intValue();
					setSegmentSize(newValue);
				}
				catch (ParseException e1) {
					// Ignore
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});

		// Replace default ENTER action
		SwingUtils.setInputKeyAction(this.getRootPane(), KeyEvent.VK_ESCAPE, "ENTER", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TimeSeriesDialog.this.actionPerformed(new ActionEvent(btnOK, e.getID(), null));
			}
		});

		// Replace default ESCAPE action
		SwingUtils.setInputKeyAction(this.getRootPane(), KeyEvent.VK_ESCAPE, "ESCAPE", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TimeSeriesDialog.this.actionPerformed(new ActionEvent(btnOK, e.getID(), null));
			}
		});
	}

	/**
	 * Handle sliders change event.
	 */
	public void stateChanged(ChangeEvent e) {

		if (e.getSource() == thresholdSlider) {
			setThreshold(thresholdSlider.getValue() / 1000d);
		}
		else if (e.getSource() == segmentCountSlider) {
			setSegmentCount(segmentCountSlider.getValue());
		}
		else if (e.getSource() == segmentSizeSlider) {
			setSegmentSize(segmentSizeSlider.getValue());
		}
	}

	/**
	 * Handle action generated by the view.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnOK) {
			dialogResult = true;
			this.setVisible(false);
		}
		else if (e.getSource() == btnCancel) {
			dialogResult = false;
			this.setVisible(false);
		}
		else if (e.getSource() == showLegendCheckbox) {
			isShowLegend = showLegendCheckbox.isSelected();
		}
		else if (e.getSource() == btnSelection) {
			
			if (isClearSelection) {
				conceptTable.setSelectedConcepts(new ArrayList<>());
			}
			else {
				conceptTable.setSelectedConcepts(timeSeries.getTraceConcepts());
			}
			
			isClearSelection = !isClearSelection;
			conceptTable.repaint();
		}
		else if (e.getSource() == btnReset) {
			
			setShowLegend(false);
			setSegmentCount(TimeSeriesView.DEFAULT_SEGMENT_COUNT);
			setThreshold(TimeSeriesView.DEFAULT_THRESHOLD);
			setSelectedConcepts(new ArrayList<>());
		}
	}

	/**
	 * Return dialog result.
	 * true = user clicked on OK
	 * false = use clicked on Cancel or ESC
	 * 
	 * @return the dialogResult
	 */
	public boolean getDialogResult() {
		return dialogResult;
	}

	/**
	 * Return current threshold.
	 * 
	 * @return an double between 0 and 1
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * Return selected segment count.
	 * 
	 * @return an int between 1 and time series size
	 */
	public int getSegmentCount() {
		return Math.round(segmentCount);
	}

	/**
	 * Return segment size.
	 * 
	 * @return an int between 1 and time series size
	 */
	public int getSegmentSize() {
		return Math.round(segmentSize);
	}

	/**
	 * Set current list of selected concept.
	 * 
	 * @return a list of concepts
	 */
	public List<Concept> getSelectedConcepts() {
		return conceptTable.getSelectedConcepts();
	}

	/**
	 * Return true if legend should be displayed.
	 * 
	 * @return a boolean
	 */
	public boolean isShowLegend() {
		return isShowLegend;
	}

	/**
	 * Set current threshold.
	 * 
	 * @param threshold
	 */
	public void setThreshold(double threshold) {

		this.threshold = threshold;
		thresholdSlider.setValue((int) (threshold * 1000));
		thresholdField.setText(String.format("%4.3f", threshold));
	}

	/**
	 * Set current segment count.
	 * 
	 * @param segmentCount
	 */
	public void setSegmentCount(int segmentCount) {
		
		this.segmentCount = segmentCount;
		this.segmentSize = (int) ((double)traceSize / segmentCount);
		
		segmentCountSlider.setValue(segmentCount);
		segmentCountField.setText(String.format("%d", segmentCount));

		segmentSizeSlider.setValue(segmentSize);
		segmentSizeField.setText(String.format("%d", segmentSize));
	}

	/**
	 * Set current segment size.
	 * 
	 * @param segmentCount
	 */
	public void setSegmentSize(int segmentSize) {

		this.segmentSize = segmentSize;
		this.segmentCount = (int) ((double) traceSize / segmentSize);

		segmentCountSlider.setValue(segmentCount);
		segmentCountField.setText(String.format("%d", segmentCount));

		segmentSizeSlider.setValue(segmentSize);
		segmentSizeField.setText(String.format("%d", segmentSize));		
	}

	/**
	 * Set timeseries.
	 * 
	 * @param timeSeries
	 */
	public void setTimeSeries(TimeSeries timeSeries) {
		
		this.timeSeries = timeSeries;
		this.traceSize  = timeSeries.getTraceMatrix().getColumnDimension();
		
		conceptTable.setConcepts(timeSeries.getTraceConcepts());
		traceSizeField.setText(String.format("%d", traceSize));
		
		segmentCountSlider.setMinimum(1);
		segmentCountSlider.setMaximum(this.traceSize);
				
		setThreshold(TimeSeriesView.DEFAULT_THRESHOLD);
		setSegmentCount(TimeSeriesView.DEFAULT_SEGMENT_COUNT);
	}

	/**
	 * Set current selected concepts.
	 * 
	 * @param concepts
	 */
	public void setSelectedConcepts(List<Concept> concepts) {
		conceptTable.setSelectedConcepts(concepts);
		conceptTable.repaint();
	}

	/**
	 * Defined if legend should be displayed or not
	 * 
	 * @param showLegend
	 */
	public void setShowLegend(boolean showLegend) {
		this.isShowLegend = showLegend;
		showLegendCheckbox.setSelected(showLegend);
	}
}

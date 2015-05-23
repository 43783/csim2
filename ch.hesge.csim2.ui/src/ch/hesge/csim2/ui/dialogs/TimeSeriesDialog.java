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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.TimeSeries;
import ch.hesge.csim2.ui.comp.ConceptTable;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class TimeSeriesDialog extends JDialog implements ActionListener, ChangeListener {

	// Private attributes
	private int segmentCount;
	private double threshold;

	private JButton btnOK;
	private JButton btnCancel;
	private boolean dialogResult;
	private JTextField thresholdField;
	private JSlider thresholdSlider;
	private JTextField segmentCountField;
	private JSlider segmentCountSlider;
	private ConceptTable conceptTable;

	/**
	 * Create the dialog with owner.
	 */
	public TimeSeriesDialog(Window parent) {
		super(parent);
		initComponents();
	}

	/**
	 * Initialize the view components
	 */
	private void initComponents() {

		// Dialog configuration
		setTitle("TimeSeries Settings");
		setBounds(0, 0, 476, 457);
		setLocationRelativeTo(getParent());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		setResizable(false);

		// Create layout structure
		JPanel mainPane = new JPanel();
		getContentPane().add(mainPane, BorderLayout.CENTER);
		JPanel btnPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) btnPane.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(btnPane, BorderLayout.SOUTH);
		mainPane.setLayout(null);

		// Create matching fields
		JLabel thresholdLabel = new JLabel("Weight threshold:");
		thresholdLabel.setBounds(34, 29, 108, 23);
		mainPane.add(thresholdLabel);
		thresholdField = new JTextField();
		thresholdField.setBounds(152, 26, 61, 23);
		thresholdField.setColumns(10);
		mainPane.add(thresholdField);
		thresholdSlider = new JSlider(1, 1000);
		thresholdSlider.setBounds(227, 26, 218, 23);
		thresholdSlider.setMajorTickSpacing(200);
		thresholdSlider.setMinorTickSpacing(100);
		thresholdSlider.setPaintTicks(true);
		thresholdSlider.addChangeListener(this);
		mainPane.add(thresholdSlider);

		// Grouping fields
		JLabel segmentCountLabel = new JLabel("Segment count:");
		segmentCountLabel.setBounds(34, 63, 108, 23);
		mainPane.add(segmentCountLabel);
		segmentCountField = new JTextField();
		segmentCountField.setColumns(10);
		segmentCountField.setBounds(152, 60, 61, 23);
		mainPane.add(segmentCountField);
		segmentCountSlider = new JSlider(1, 1000);
		segmentCountSlider.setBounds(227, 60, 218, 23);
		segmentCountSlider.addChangeListener(this);
		mainPane.add(segmentCountSlider);

		// Create concept panel
		JPanel conceptPanel = new JPanel();
		conceptPanel.setBorder(new TitledBorder(null, "Trace concepts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		conceptPanel.setBounds(10, 105, 450, 268);
		conceptPanel.setLayout(new BorderLayout(0, 0));
		conceptTable = new ConceptTable();
		JScrollPane scrollbar = new JScrollPane();
		scrollbar.setViewportView(conceptTable);
		conceptPanel.add(scrollbar, BorderLayout.CENTER);
		mainPane.add(conceptPanel);

		// Initialize button pane
		btnOK = new JButton("OK");
		btnOK.setPreferredSize(new Dimension(100, 25));
		btnOK.addActionListener(this);
		btnPane.add(btnOK);

		btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(80, 25));
		btnCancel.addActionListener(this);
		btnPane.add(btnCancel);

		initListeners();
	}

	/**
	 * Initialize component listeners
	 */
	private void initListeners() {

		thresholdField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {

				//threshold = Integer.parseInt(thresholdField.getText());
				
				try {
					threshold = NumberFormat.getInstance().parse(thresholdField.getText()).doubleValue();
					setThreshold(threshold);
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
				
				//segmentCount segmentCount = Integer.parseInt(segmentCountField.getText());
				
				try {
					segmentCount = NumberFormat.getInstance().parse(segmentCountField.getText()).intValue();
					setSegmentCount(segmentCount);
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
			threshold = thresholdSlider.getValue() / 1000d;
			thresholdField.setText(String.format("%4.3f", threshold));
		}
		else if (e.getSource() == segmentCountSlider) {
			segmentCount = segmentCountSlider.getValue();
			segmentCountField.setText(String.format("%d", segmentCount));
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
	 * Return selected segment count.
	 * 
	 * @return an int between 1 and time series size
	 */
	public int getSegmentCount() {
		return segmentCount;
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
	 * Set current list of selected concept.
	 * 
	 * @return a list of concepts
	 */
	public List<Concept> getSelectedConcepts() {
		return conceptTable.getSelectedConcepts();
	}

	/**
	 * Set current segment count.
	 * 
	 * @param segmentCount
	 */
	public void setSegmentCount(int segmentCount) {

		this.segmentCount = segmentCount;
		this.segmentCountSlider.setValue(segmentCount);
		this.segmentCountField.setText(String.format("%d", segmentCount));
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
	 * Set timeseries.
	 * 
	 * @param timeSeries
	 */
	public void setTimeSeries(TimeSeries timeSeries) {
		this.segmentCountSlider.setMinimum(1);
		this.segmentCountSlider.setMaximum(timeSeries.getTraceVectors().size());
		this.conceptTable.setConcepts(timeSeries.getConcepts());
		setThreshold(0.1d);
		setSegmentCount(100);
	}

	/**
	 * Set current selected concepts.
	 * 
	 * @param concepts
	 */
	public void setSelectedConcepts(List<Concept> concepts) {
		conceptTable.setSelectedConcepts(concepts);
	}
}

package Utilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
/**
 * 
 *  @version 2.0 Class responsible for creating the Tabular View Visualization which is displayed withing
 *  the app's Home page.
 *  Most of its functionality is done within the constructor.
 *
 */
public class TabularView extends JFrame implements Window{
	private static final long serialVersionUID = 1L;
//	private static TabularView instance;
	private JPanel mainPanel;
	private JPanel currentPanel;
	MySQLAccess dao = new MySQLAccess();
	// private String query;

//	public static TabularView getInstance(String s) {  //String geo, String geo2, String sendStart, String sendEnd
//		if (instance == null) {
//			instance = new TabularView(s);
//		}
//
//		return instance;
//	}

	public TabularView(String query, String country) {
		super("NHIP");

		// pre window we send query to db and get the results back.
		ArrayList<DataBaseResults> dbResults = new ArrayList<DataBaseResults>();
		try {
			dbResults = getDBResults(query); // getDBScriptResults(geo, geo2, sendStart, sendEnd);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// main window
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // add padding

		currentPanel = new JPanel(new BorderLayout()); // to switch views

		// Panel Containing Raw Data Table -- DEFAULT VIEW
		JPanel rawDataTable = rawData(dbResults);
		currentPanel = rawDataTable;

		mainPanel.add(currentPanel, BorderLayout.NORTH);
//		
		// Pannel w/ summary table
		JPanel summaryTablePanel = summaryTable(dbResults);

		// PANNEL IN BOTTOM FOR TOGGLE BUTTON -- WORKSSS
		JPanel toggleButtonPanel = new JPanel();
		toggleButtonPanel.setPreferredSize(new Dimension(400, 120));
		mainPanel.add(toggleButtonPanel, BorderLayout.SOUTH);

		// Create toggle button & panel
		JToggleButton toggleButton = createToggleButton();
		JPanel toggle = new JPanel();

		// action listener to switch views
		toggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (toggleButton.isSelected()) {
					// show summary table panel
					mainPanel.remove(currentPanel);
					currentPanel = summaryTablePanel;
					mainPanel.add(currentPanel, BorderLayout.NORTH);
				} else {
					// show raw data panel
					mainPanel.remove(currentPanel);
					currentPanel = rawDataTable;
					mainPanel.add(currentPanel, BorderLayout.NORTH);
				}
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});

		toggle.add(toggleButton);
		toggleButtonPanel.add(toggle);
		
		mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tabular View For "+country,
				TitledBorder.LEFT, TitledBorder.TOP));

		// Add main panel to JFrame
		this.add(mainPanel);

		// mainPanel.setSize(300, 600);
	}

	/**
	 * Creates a raw data table with the given array of data
	 * @param dbResults - Array of data 
	 * @return The panel containing the table
	 */
	private JPanel rawData(ArrayList<DataBaseResults> dbResults) {
		JTable table;
		DefaultTableModel model_table;
		JScrollPane scroll_table;

		JPanel containerPanel = new JPanel(new BorderLayout());
		// containerPanel.setPreferredSize(new Dimension(700, 400));

		// table creation
		table = new JTable();
		model_table = new DefaultTableModel();
		model_table.addColumn("REF_DATE");
		model_table.addColumn("GEO");
		model_table.addColumn("VALUE");
		table.setModel(model_table);

		table.setRowHeight(17); // makes the data less congested

		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(0).setMinWidth(100);
		table.getColumnModel().getColumn(0).setMaxWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setMinWidth(100);
		table.getColumnModel().getColumn(1).setMaxWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setMinWidth(60);
		table.getColumnModel().getColumn(2).setMaxWidth(60);

		for (int i = 0; i < dbResults.size(); i++) { // add values to table
			Vector<String> r = new Vector<String>();
			r.addElement(dbResults.get(i).getRefDates());
			r.addElement(dbResults.get(i).getGeos());
			r.addElement(String.valueOf(dbResults.get(i).getValues()));
			model_table.addRow(r);
		}

		scroll_table = new JScrollPane(table); // add table to scroll panel

		JPanel centeredPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		centeredPanel.add(scroll_table, gridBagConstraints);
		scroll_table.setPreferredSize(new Dimension(278, 348));

		// scroll_table.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		// Add centered panel to the container panel
		containerPanel.add(centeredPanel, BorderLayout.CENTER);
		containerPanel.setSize(100, 100);

		return containerPanel;

	}
	
	/**
	 * Creates a toggle button that changes views between the raw data table and descriptive statistics. 
	 * @return The button created
	 */
	private JToggleButton createToggleButton() {
		JToggleButton toggleButton;
		toggleButton = new JToggleButton("Switch View");
		toggleButton.setFont(new Font("San Francisco", Font.PLAIN, 16));
		toggleButton.setForeground(Color.WHITE);
		toggleButton.setBackground(new Color(0x4CD964));
		toggleButton.setOpaque(true);
		toggleButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		toggleButton.setPreferredSize(new Dimension(130, 40));

		JPanel togglePanel = new JPanel();
		togglePanel.add(toggleButton);

		return toggleButton;

	}

	/**
	 * Creates a descriptive statistics table of the given array of data.
	 * Includes stats such as average, min, max and standard deviation.
	 * @param dbResults - Array of data 
	 * @return The panel containing the table
	 */
	private JPanel summaryTable(ArrayList<DataBaseResults> dbResults) {
		JTable table;
		DefaultTableModel model_table;
		JScrollPane scroll_table;

		JPanel containerPanel = new JPanel(new BorderLayout());
		containerPanel.setPreferredSize(new Dimension(50, 184));

		// table creation
		table = new JTable();
		model_table = new DefaultTableModel();
		model_table.addColumn("Descriptive Statistics");
		model_table.addColumn("Values");
		table.setModel(model_table);

		table.setRowHeight(40); // makes the data less congested

		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(0).setMinWidth(150);
		table.getColumnModel().getColumn(0).setMaxWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setMinWidth(60);
		table.getColumnModel().getColumn(1).setMaxWidth(60);

		// center align the text in the table
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, centerRenderer);

		// FIND DESCRIPTIVE STATISTICS
		ArrayList<Double> listOfValues = new ArrayList<Double>();
		double average = 0.0;

		for (int i = 0; i < dbResults.size(); i++) { // get an array of all values only
			listOfValues.add(dbResults.get(i).getValues());
			average += dbResults.get(i).getValues();
		}
		average = average / dbResults.size();
		double minValue = Collections.min(listOfValues);
		double maxValue = Collections.max(listOfValues);

		double standardDev = getDBResults(
				"select 'REF_DATE','GEO',stddev(value) as value from  nhipcopy where geo = 'Calgary, Alberta'").get(0)
						.getValues();

		// ADDING VALUES TO TABLE
		Vector<String> r = new Vector<String>();
		r.addElement("Average :");
		r.addElement("" + String.format("%.2f", average));
		model_table.addRow(r);

		r = new Vector<String>();
		r.addElement("Standard Deviation :");
		r.addElement("" + String.format("%.2f", standardDev));
		model_table.addRow(r);

		r = new Vector<String>();
		r.addElement("Min :");
		r.addElement("" + minValue);
		model_table.addRow(r);

		r = new Vector<String>();
		r.addElement("Max :");
		r.addElement("" + maxValue);
		model_table.addRow(r);

		scroll_table = new JScrollPane(table); // add table to scroll panel otherwise titles dont show

//		containerPanel.add(scroll_table, BorderLayout.CENTER);
//
//		// Add container panel to another panel with a layout manager so it doesnt take
//		// up whole screen
//		JPanel funcMainPanel = new JPanel(new BorderLayout());
//		funcMainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // add padding
//		funcMainPanel.add(containerPanel, BorderLayout.CENTER);

		JPanel centeredPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		centeredPanel.add(scroll_table, gridBagConstraints);
		scroll_table.setPreferredSize(new Dimension(210, 179));

		scroll_table.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		// Add centered panel to the container panel
		containerPanel.add(centeredPanel, BorderLayout.CENTER);
		containerPanel.setSize(100, 100);

		return containerPanel;

	}

	/**
	 * Retrieves the results from the database with the specified parameters that the user selected. 
	 * @param query - String query containing all of the parameters
	 * @return An arrayList containing all the data retrieved
	 */
	private ArrayList<DataBaseResults> getDBResults(String query) {
		// MySQLAccess dao = new MySQLAccess();
		try {
			dao.connectToDataBase();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		ArrayList<DataBaseResults> results = new ArrayList<DataBaseResults>();

		try {
			results = dao.sendQuery(query);
			// return results;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;
	}

//	public ArrayList<DataBaseResults> getDBScriptResults(String geo, String geo2, String sendStart, String sendEnd) throws Exception {
//
//		dao.connectToDataBase();
//
//		String query = "SELECT * FROM nhip "
//				+ "WHERE STR_TO_DATE(CONCAT('01-', REF_DATE), '%d-%b-%y') "
//				+ "BETWEEN ? AND ?;";
//
//		ArrayList<DataBaseResults> dbResult = dao.sendQuery(query, sendStart, sendEnd);
//		
//		ArrayList<DataBaseResults> results = new ArrayList<DataBaseResults>();
//
//		for (int i = 0; i < dbResult.size(); i++) {
//			DataBaseResults row = dbResult.get(i);
//			if (row.getGeos().equals(geo) || row.getGeos().equals(geo2)) {
//				results.add(dbResult.get(i));
//			}
//		}
//		return results;
//	}

	/**
	 * Retrieves the panel containing the tabular visualization.
	 * @return the panel
	 */
	public JPanel getTable() {
		return this.mainPanel;
	}

	public static void main(String[] args) {
		String script = "SELECT * FROM nhipcopy " + "WHERE STR_TO_DATE(CONCAT('01-', REF_DATE), '%d-%b-%y') "
				+ "BETWEEN DATE('1998-01-01') AND DATE('1999-01-01') " + "AND GEO = 'Ontario'";
		JFrame frame = new TabularView(script, "Ontario");

		frame.setAlwaysOnTop(true);
		frame.pack();
//		frame.setSize(350, 500);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
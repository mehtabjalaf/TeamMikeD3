package Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TTest;

/**
 * 
 * @version 2.0 Class responsible for creating the NHIP Comparison window and
 *          containing the functionalities related to statistical tests. Most of
 *          its functionality is done within the constructor.
 *
 */
public class NHIPComparison extends JFrame implements Window {

	private static final long serialVersionUID = 1L;
	private JComboBox<String> location1;
	private JComboBox<String> location2;
	private JComboBox<String> startMonth;
	private JComboBox<String> endMonth;
	private JComboBox<String> startYear;
	private JComboBox<String> endYear;

	JLabel testShow = new JLabel();

	private JComboBox<String> comparisonType;
	private JButton updateButton;

	private JPanel mainPanel;

	// JFrame mainPanel = new JFrame(new BorderLayout());

	public NHIPComparison() throws Exception {
		super("NHIP Comparison");

		// Create the combo boxes for selecting the locations and comparison type
		MySQLAccess dao = new MySQLAccess();
		dao.connectToDataBase();
		Vector<String> countriesNames = new Vector<String>();
		
		DatasetYearsMonths d = new DatasetYearsMonths();

		String[] months = d.returnMonths();
		String[] years = d.returnYears();

		ArrayList<DataBaseResults> result2 = dao
				.sendQuery("SELECT \"REF_DATE\", GEO, 23 as value\r\n" + "FROM nhipcopy\r\n" + "GROUP BY GEO;");

		for (int i = 0; i < 40; i++) {
			countriesNames.add(result2.get(i).getGeos());
		}

		location1 = new JComboBox<>(countriesNames);
		location2 = new JComboBox<>(countriesNames);
		startMonth = new JComboBox<>(months);
		startYear = new JComboBox<>(years);
		endMonth = new JComboBox<>(months);
		endYear = new JComboBox<>(years);

		comparisonType = new JComboBox<>(new String[] { "T-Test", "Mann-Whitney U-test" });
		this.updateButton = new JButton("Load Data");

		updateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String l1 = (String) location1.getSelectedItem();
				String l2 = (String) location2.getSelectedItem();
				String testType = (String) comparisonType.getSelectedItem();

				try {
					SelectedMonth s1 = new SelectedMonth();
					String startNum = s1.getMonthNumber((String) startMonth.getSelectedItem());
					String endNum = s1.getMonthNumber((String) endMonth.getSelectedItem());
					String sendStart = (String) startYear.getSelectedItem() + "-" + startNum + "-" + "01";
					String sendEnd = (String) endYear.getSelectedItem() + "-" + endNum + "-" + "01";
					TimeSeriesStartEnd t = new TimeSeriesStartEnd(l1, sendStart, sendEnd);
					ArrayList<DataBaseResults> set1 = updateDataset(t);
					t.updateCountry(l2);
					ArrayList<DataBaseResults> set2 = updateDataset(t);
					if (testType.equals("T-Test")) {
						StatisticalCalculations s = new StatisticalCalculations();
						String p = s.calculateTtest(set1, set2);
						testShow.setText(p);
					}

					if (testType.equals("Mann-Whitney U-test")) {
						StatisticalCalculations s = new StatisticalCalculations();
						String manuTest = s.calculateMannWhitneyU(set1, set2);
						testShow.setText(manuTest);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});

		JLabel startDateLabel = new JLabel("Start Date:");
		JLabel endDateLabel = new JLabel("End Date:");

		JPanel topMenu = new JPanel();
		topMenu.add(new JLabel("Location 1:"));
		topMenu.add(location1);
		topMenu.add(new JLabel("Location 2:"));
		topMenu.add(location2);
		topMenu.add(startDateLabel);
		topMenu.add(startMonth);
		topMenu.add(startYear);
		topMenu.add(endDateLabel);
		topMenu.add(endMonth);
		topMenu.add(endYear);
		topMenu.add(new JLabel("Comparison Type:"));
		topMenu.add(comparisonType);
		topMenu.add(updateButton);

		testShow.setFont(new Font("Serif", Font.BOLD, 40));
		testShow.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		testShow.setHorizontalAlignment(SwingConstants.CENTER);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(topMenu, BorderLayout.NORTH);
		mainPanel.add(testShow, BorderLayout.CENTER);
		setContentPane(mainPanel);
	}

	// TODO MODIFIED VERSION OF THIS FUNC CAN BE MADE TO ADD WHERE GEO = STATEMENT
	// AND WE GET RID OF FORLOOP
	public ArrayList<DataBaseResults> updateDataset(TimeSeriesStartEnd t) throws Exception {
		MySQLAccess dao = new MySQLAccess();
		dao.connectToDataBase();
		String query = "SELECT * FROM nhipcopy " + "WHERE STR_TO_DATE(CONCAT('01-', REF_DATE), '%d-%b-%y') "
				+ "BETWEEN ? AND ?;";

		ArrayList<DataBaseResults> result3 = dao.sendQuery(query, t.sendStart, t.sendEnd);
		ArrayList<DataBaseResults> result4 = new ArrayList<DataBaseResults>();

		for (int i = 0; i < result3.size(); i++) {
			DataBaseResults row = result3.get(i);
			if (row.getGeos().equals(t.selectedCountry)) {
				result4.add(result3.get(i)); // query to get nhpi values from date range and put in array list then
												// passed to calcttest
			}
		}

		return result4;
	}

	/**
	 * Calculates TTest results given 2 sets of data.
	 * @param set1 - First set of data
	 * @param set2 - Second set of data
	 * @return String containing the results from the test
	 */
	

	public static void main(String[] args) throws Exception {
		JFrame frame = new NHIPComparison();
		frame.setAlwaysOnTop(true);
		frame.pack();
		frame.setSize(1550, 400);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
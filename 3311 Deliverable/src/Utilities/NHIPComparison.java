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

		String[] months = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
				"Dec" };
		String[] years = new String[] { "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990",
				"1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003",
				"2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016",
				"2017", "2018", "2019", "2020", "2021" };

		ArrayList<DataBaseResults> result2 = dao
				.sendQuery("SELECT \"REF_DATE\", GEO, 23 as value\r\n" + "FROM nhip\r\n" + "GROUP BY GEO;");

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
					String startNum = MainWindow.getMonthNumber((String) startMonth.getSelectedItem());
					String endNum = MainWindow.getMonthNumber((String) endMonth.getSelectedItem());
					String sendStart = (String) startYear.getSelectedItem() + "-" + startNum + "-" + "01";
					String sendEnd = (String) endYear.getSelectedItem() + "-" + endNum + "-" + "01";
					ArrayList<DataBaseResults> set1 = updateDataset(l1, sendStart, sendEnd);
					ArrayList<DataBaseResults> set2 = updateDataset(l2, sendStart, sendEnd);
					if (testType.equals("T-Test")) {
						String p = calculateTtest(set1, set2);
						testShow.setText(p);
					}

					if (testType.equals("Mann-Whitney U-test")) {
						String manuTest = calculateMannWhitneyU(set1, set2);
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
	public ArrayList<DataBaseResults> updateDataset(String geo, String sendStart, String sendEnd) throws Exception {
		MySQLAccess dao = new MySQLAccess();
		dao.connectToDataBase();
		String query = "SELECT * FROM nhip " + "WHERE STR_TO_DATE(CONCAT('01-', REF_DATE), '%d-%b-%y') "
				+ "BETWEEN ? AND ?;";

		ArrayList<DataBaseResults> result3 = dao.sendQuery(query, sendStart, sendEnd);
		ArrayList<DataBaseResults> result4 = new ArrayList<DataBaseResults>();

		for (int i = 0; i < result3.size(); i++) {
			DataBaseResults row = result3.get(i);
			if (row.getGeos().equals(geo)) {
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
	public String calculateTtest(ArrayList<DataBaseResults> set1, ArrayList<DataBaseResults> set2) {

		TTest ttest = new TTest();
		ArrayList<Double> vals1 = new ArrayList<Double>();
		ArrayList<Double> vals2 = new ArrayList<Double>();

		for (int i = 0; i < set1.size(); i++) {
			vals1.add(set1.get(i).getValues());
		}

		for (int i = 0; i < set2.size(); i++) {
			vals2.add(set2.get(i).getValues());
		}

		double[] array1 = new double[vals1.size()];
		double[] array2 = new double[vals2.size()];

		for (int i = 0; i < set1.size(); i++) {
			array1[i] = vals1.get(i);
		}
		for (int i = 0; i < set2.size(); i++) {
			array2[i] = vals2.get(i); // TODO REFACTOOOOR

		}

		double p = ttest.tTest(array1, array2);
		String ret = "";

		if (p < 0.05) {
			ret = "We reject the null hypothesis,  p = " + p;
		} else {
			ret = "We cannot reject the null hypothesis,   p = " + p;
		}

		return ret;
	}

	/**
	 * Calculates MannWhitneyU score given 2 sets of data.
	 * @param set1 - First set of data
	 * @param set2 - Second set of data
	 * @return String containing the results from the test
	 */
	private String calculateMannWhitneyU(ArrayList<DataBaseResults> set1, ArrayList<DataBaseResults> set2) {
		double[] values1 = new double[set1.size()];
		for (int i = 0; i < set1.size(); i++) {
			values1[i] = set1.get(i).getValues();
		}

		double[] values2 = new double[set2.size()];
		for (int i = 0; i < set2.size(); i++) {
			values2[i] = set2.get(i).getValues();
		}

		MannWhitneyUTest test = new MannWhitneyUTest();
		double pValue = test.mannWhitneyUTest(values1, values2);

		return "Mann-Whitney U Test p-value: " + pValue;
	}

	public static void main(String[] args) throws Exception {
		JFrame frame = new NHIPComparison();
		frame.setAlwaysOnTop(true);
		frame.pack();
		frame.setSize(1550, 400);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
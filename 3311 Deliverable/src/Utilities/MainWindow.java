package Utilities;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;  
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.time.Month;
import weka.core.Instances;

/**
 * 
 * @version 2.0
 * Class responsible for creating the Home page of our application. 
 * Calls onto other classes to create the required visualizations and pop-up windows.
 * Most of its functionality is done within the constructor.
 *
 */
public class MainWindow extends JFrame implements Window{  
	private DefaultCategoryDataset dataset;
	
	private JPanel mainPanel;  //AdDED 
	
	private  DefaultCategoryDataset bar;
	private DefaultCategoryDataset stacked;
	
	private JButton updateButton;
	private JButton statisticalTestButton;
	private JButton forecastingButton;
	protected String vis1 = "";
	protected String vis2 = "";
	private static final long serialVersionUID = 1L;  
	
	
	MySQLAccess dao = new MySQLAccess();

	/**
	 * Static method that converts the selected month from text to a numeric value.
	 * @param monthName
	 * @return A string containing the numeric value of the month
	 */
	public static String getMonthNumber(String monthName) {  //has to be public bc nhip Comparison uses it
		if(monthName.equals("Jan")) {
			return "01";
		}
		if(monthName.equals("Feb")) {
			return "02";
		}
		if(monthName.equals("Mar")) {
			return "03";
		}
		if(monthName.equals("Apr")) {
			return "04";
		}
		if(monthName.equals("May")) {
			return "05";
		}
		if(monthName.equals("Jun")) {
			return "06";
		}

		if(monthName.equals("Jul")) {
			return "07";
		}
		if(monthName.equals("Aug")) {
			return "08";
		}
		if(monthName.equals("Sep")) {
			return "09";
		}
		if(monthName.equals("Oct")) {
			return "10";
		}
		if(monthName.equals("Nov")) {
			return "11";
		}
		if(monthName.equals("Dec")) {
			return "12";
		}


		return "";
	}
	//TODO ADD W/ DB 

	
	public MainWindow(String city) throws Exception {  //constructor 

		super("NHIP - Homescreen");  
		
		mainPanel = new JPanel(new BorderLayout());
		
		// Create dataset  
		this.dataset = createDataset(city); 
		this.bar = createDataset(city);
		this.stacked = createDataset(city);

		dao.connectToDataBase();  //TODO only for connecting purposes
		
		ChartPanel chartpanel1 = new ChartPanel(null);
		ChartPanel chartpanel2 = new ChartPanel(null);
		JPanel table1Panel = new JPanel();
		JPanel table2Panel = new JPanel();

		
		//Date drop downs : 
		String[] months = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		String[] years = new String[] {"1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021"};
		JComboBox<String> startMonth = new JComboBox<String>(months);
		JComboBox<String> startYear = new JComboBox<String>(years);
		JComboBox<String> endMonth = new JComboBox<String>(months);
		JComboBox<String> endYear = new JComboBox<String>(years);
		JLabel startDateLabel = new JLabel("Start Date:");
		JLabel endDateLabel = new JLabel("End Date:");
		
		//country drop down : 
		JLabel chooseCountryLabel = new JLabel("Choose a country: ");
		
		Vector<String> countriesNames = new Vector<String>();

		//get all the countries from db
		ArrayList<DataBaseResults> countriesResult = dao.sendQuery("SELECT \"REF_DATE\", GEO, 23 as value "
				+ "FROM nhip " + "GROUP BY GEO;");

		for (int i = 0; i< countriesResult.size(); i++) {
			countriesNames.add(countriesResult.get(i).getGeos());
		}
		
		JComboBox<String> countriesList = new JComboBox<String>(countriesNames);
		JComboBox<String> countriesList2 = new JComboBox<String>(countriesNames);

		//Visualization drop downs:
		JComboBox<String> vis1drop = new JComboBox<>(new String[]{"Line Chart", "Bar Chart", "Stacked Area Chart"});
		JComboBox<String> vis2drop = new JComboBox<>(new String[]{"Line Chart", "Bar Chart", "Stacked Area Chart"});
		
		//set the view buttons
		this.updateButton = new JButton("Load Data");
		this.statisticalTestButton = new JButton ("Statistical Test");
		
		forecastingButton = new JButton("Forecasting");
		
		statisticalTestButton.addActionListener(new ActionListener() {

			//Will open a new window to perform stat. test
			public void actionPerformed(ActionEvent e) {
				try {
					NHIPComparison n = new NHIPComparison();
					n.pack();  
					n.setSize(1550, 400);  
					n.setVisible(true);
					n.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
				} 
				catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		forecastingButton.addActionListener(new ActionListener() {

			//Will open a new window to perform stat. test
			public void actionPerformed(ActionEvent e) {
				try {
					Forecasting frame = new Forecasting();
					frame.pack();  
					frame.setSize(1500, 900);  
					frame.setVisible(true);
					frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
				} 
				catch (Exception e1) {

					e1.printStackTrace();
				}
			}
		});
		
		//Updates view based on chosen criteria
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Get the selected parameters from user
				String selectedCountry = (String) countriesList.getSelectedItem(); 
				String selectedCountry2 = (String) countriesList2.getSelectedItem();
				//convert month to a numeric value
				String startNum = getMonthNumber ((String)startMonth.getSelectedItem());
				String endNum = getMonthNumber ((String)endMonth.getSelectedItem());

				String sendStart = (String) startYear.getSelectedItem() + "-" +startNum + "-"+"01";
				String sendEnd = (String) endYear.getSelectedItem() + "-" +endNum + "-"+"01";
				
				//ALL OF THIS ^^^ IS FOR QUERY

				String sendvis1 = (String)vis1drop.getSelectedItem();
				String sendvis2= (String)vis2drop.getSelectedItem();
				
				//get tabular views for both countries
				TabularView tableDataForCountry1 = setTabularViews(selectedCountry, sendStart, sendEnd);
				table1Panel.removeAll();; 
				table1Panel.add(tableDataForCountry1.getTable());
				table1Panel.validate();
				table1Panel.repaint();
				
				TabularView tableDataForCountry2 = setTabularViews(selectedCountry2, sendStart, sendEnd);
				table2Panel.removeAll();; 
				table2Panel.add(tableDataForCountry2.getTable());
				table2Panel.validate();
				table2Panel.repaint();
				
				
				// Update the dataset with the selected country
				try {
					updateDataset(selectedCountry, selectedCountry2, sendStart, sendEnd);
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
			
				
				if((sendvis1.equals("Line Chart") && sendvis2.equals("Stacked Area Chart")) || (sendvis1.equals("Stacked Area Chart") && sendvis2.equals("Line Chart"))) {
					JFreeChart line = ChartFactory.createLineChart(  

							"NHIP " + selectedCountry + " VS " + selectedCountry2, // Chart title  
							"Date", // X-Axis Label  
							"NHIP Value", // Y-Axis Label  
							dataset  
							);  
					
					JFreeChart stacked1 = ChartFactory.createStackedAreaChart(  

							"NHIP " + selectedCountry + " VS " + selectedCountry2, // Chart title  
							"Date", // X-Axis Label  
							"NHIP Value", // Y-Axis Label  
							dataset  
							);  
					
				chartpanel1.setChart(line);
				chartpanel2.setChart(stacked1);
				
				}
				
				if((sendvis1.equals("Line Chart") && sendvis2.equals("Bar Chart")) || (sendvis1.equals("Bar Chart") && sendvis2.equals("Line Chart"))) {
					JFreeChart line = ChartFactory.createLineChart(  

							"NHIP " + selectedCountry + " VS " + selectedCountry2, // Chart title  
							"Date", // X-Axis Label  
							"NHIP Value", // Y-Axis Label  
							dataset  
							);  
					
					JFreeChart bar1 = ChartFactory.createBarChart(  

							"NHIP " + selectedCountry + " VS " + selectedCountry2, // Chart title  
							"Date", // X-Axis Label  
							"NHIP Value", // Y-Axis Label  
							dataset  
							);  
					
				chartpanel1.setChart(line);
				chartpanel2.setChart(bar1);
				
				}
				
				if((sendvis1.equals("Bar Chart") && sendvis2.equals("Stacked Area Chart")) || (sendvis1.equals("Stacked Area Chart") && sendvis2.equals("Bar Chart"))) {
					JFreeChart bar1 = ChartFactory.createBarChart(  

							"NHIP " + selectedCountry + " VS " + selectedCountry2, // Chart title  
							"Date", // X-Axis Label  
							"NHIP Value", // Y-Axis Label  
							dataset  
							);  
					
					JFreeChart stacked1 = ChartFactory.createStackedAreaChart(  

							"NHIP " + selectedCountry + " VS " + selectedCountry2, // Chart title  
							"Date", // X-Axis Label  
							"NHIP Value", // Y-Axis Label  
							dataset  
							);  
					
				chartpanel1.setChart(bar1);
				chartpanel2.setChart(stacked1);
				}

			}

		});

		//Bottom bar with all selection opens
		JPanel controlsPane = new JPanel();
		controlsPane.add(vis1drop);
		controlsPane.add(vis2drop);
		controlsPane.add(countriesList);
		controlsPane.add(countriesList2);
		controlsPane.add(startDateLabel);
		controlsPane.add(startMonth);
		controlsPane.add(startYear);
		controlsPane.add(endDateLabel);
		controlsPane.add(endMonth);
		controlsPane.add(endYear);
		controlsPane.add(updateButton);
		
		//Top menu with stats test and forecasting option
		JPanel topMenu = new JPanel();
		topMenu.add(forecastingButton);
		topMenu.add(statisticalTestButton);
		
		JPanel containerPanel = new JPanel(new GridLayout(1,3));
		containerPanel.add(chartpanel1); 
		containerPanel.add(chartpanel2); 
		
		JPanel tablesPane = new JPanel();
		tablesPane.setLayout(new BoxLayout(tablesPane, BoxLayout.Y_AXIS));
		
		// set my table sizes and add to tables panel
//		table1Panel.setPreferredSize(new Dimension(350,350));
//		table1Panel.setMaximumSize(new Dimension(320,370));
//		table1Panel.setMinimumSize(new Dimension(350,350));
		
		// add them both to one pannel and add that pannel to main panel 		
		tablesPane.add(table1Panel);
		tablesPane.add(Box.createVerticalStrut(2)); // add some spacing between the two panels
		tablesPane.add(table2Panel);
		
		containerPanel.add(tablesPane);
		
		//had this for testing purposes TODO
//		table1Panel.setBackground(Color.GREEN);
//		table2Panel.setBackground(Color.BLUE);
	
		mainPanel.add(controlsPane, BorderLayout.SOUTH);
		mainPanel.add(topMenu, BorderLayout.NORTH);
		mainPanel.add(containerPanel, BorderLayout.CENTER);
		
		this.add(mainPanel);

	}  
	
	/**
	 * Calls onto TabularView class to create the Tabular Visualization.
	 * @param geo - Selected location 
	 * @param sendStart - Selected start date 
	 * @param sendEnd - Selected end date 
	 * @return The panel containing the tabular visualization
	 */
	public TabularView setTabularViews(String geo, String sendStart, String sendEnd) {
		String script = "SELECT * FROM nhip "
				+ "WHERE STR_TO_DATE(CONCAT('01-', REF_DATE), '%d-%b-%y') "
				+ "BETWEEN DATE('" +sendStart + "') AND DATE('" + sendEnd + "') "
				+ "AND GEO = '"+ geo +"';";
		
		TabularView tableDataForCountry = new TabularView(script, geo); 
		
		return tableDataForCountry;
	}
	
	/**
	 * Updates the dataset used by the visualizations with the new selected parameters chosen by user.
	 * @param geo - Selected first location
	 * @param geo2 - Selected second location
	 * @param sendStart - Selected start date
	 * @param sendEnd - Selected end date
	 * @throws Exception - In case database cannot be accessed
	 */
	public void updateDataset(String geo, String geo2, String sendStart, String sendEnd) throws Exception {
		dataset.clear();

		String query = "SELECT * FROM nhip "
				+ "WHERE STR_TO_DATE(CONCAT('01-', REF_DATE), '%d-%b-%y') "
				+ "BETWEEN ? AND ?;";

		ArrayList<DataBaseResults> result3 = dao.sendQuery(query, sendStart, sendEnd);

		for (int i = 0; i < result3.size(); i++) {
			DataBaseResults row = result3.get(i);
			if (row.getGeos().equals(geo) || row.getGeos().equals(geo2)) {
				double value = row.getValues();
				String date = row.getRefDates();
				String geoName = row.getGeos();
				dataset.addValue(value, geoName, date);
			}
		}
	}

	//i think this is useless but later issue TODO
	private DefaultCategoryDataset createDataset(String city) throws Exception{  
		DefaultCategoryDataset dataset = new DefaultCategoryDataset(); //collection of data items so chart can use
		//MySQLAccess dao = new MySQLAccess();
		ArrayList<DataBaseResults> results = dao.readEntireDataBase();

		for (int i = 0; i<49654; i++) {
			if (results.get(i).getGeos().equals(city)) {
				double value = results.get(i).getValues();
				String date = results.get(i).getRefDates();
				String geo = results.get(i).getGeos();
				dataset.addValue(value, geo, date);
			}
		}

		return dataset;  
	} 

	public static void main(String[] args) throws Exception {  

		MySQLAccess dao = new MySQLAccess();
		dao.connectToDataBase();

		SwingUtilities.invokeLater(() -> {  
			try {
				MainWindow example = new MainWindow("Calgary, Alberta");   //old default 
				example.setAlwaysOnTop(true);  
				example.pack();  
				example.setSize(1550, 930);  
				example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
				example.setVisible(true); }
			catch(Exception e) {
				e.printStackTrace();
			}
		});  
	}  

}
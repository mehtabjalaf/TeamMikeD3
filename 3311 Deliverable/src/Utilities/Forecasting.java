package Utilities;

import javax.swing.*;
import weka.core.Instance;
import weka.core.Instances;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SimpleLinearRegression;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.DenseInstance;
import java.time.LocalDate;
import java.time.ZoneId;
import java.text.SimpleDateFormat;

/**
 * 
 * @version 2.0 
 * Class responsible for creating the Forecasting window and its
 * related functionalities.
 * Most of its functionality is done within the constructor.
 *
 */
public class Forecasting extends JFrame implements Window {
	private JComboBox<String> geoComboBox;
	private JTextField monthsComboBox;
	private JComboBox<String> forecastingMethods;
	private DefaultCategoryDataset dataset;

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	ChartPanel chartpanel1 = new ChartPanel(null);

	private JTextField epochss;
	private JButton predictButton;
	private JLabel resultLabel = new JLabel();

	public Forecasting() throws Exception {
		super("Forecasting");

		MySQLAccess dao = new MySQLAccess();
		Vector<String> countriesNames = new Vector<String>();

		ArrayList<DataBaseResults> result2;
		try {
			dao.connectToDataBase(); // just so it connects
			result2 = dao.sendQuery("SELECT \"REF_DATE\", GEO, 23 as value\r\n" + "FROM nhipcopy\r\n" + "GROUP BY GEO;");

			for (int i = 0; i < 40; i++) {
				countriesNames.add(result2.get(i).getGeos());
			}

		} catch (Exception e2) {
			e2.printStackTrace();
		}

		this.dataset = createDataset("Calgary, Alberta");
		geoComboBox = new JComboBox<>(countriesNames);
		forecastingMethods = new JComboBox<>(new String[] { "Linear Regression", "Multilayer Perceptron" });
		this.predictButton = new JButton("Predict");
		monthsComboBox = new JTextField();
		epochss = new JTextField();

		monthsComboBox.setPreferredSize(new Dimension(100, 30));
		epochss.setPreferredSize(new Dimension(100, 30));

		predictButton.addActionListener(new ActionListener() {

			//
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedGeo = (String) geoComboBox.getSelectedItem();
				String months = (String) monthsComboBox.getText();
				String forecast = (String) forecastingMethods.getSelectedItem();
				String epochs = (String) epochss.getText();
				System.out.print(months);
				try {
					updateBasedOnGeo(selectedGeo);
				} catch (Exception e2) {
					e2.printStackTrace();
				}

				try {
					if (forecast.equals("Linear Regression")) {
						if (months == null || months.isEmpty() || epochs == null || epochs.isEmpty()) {
							// invalid entry label
							resultLabel.setText("Invalid Entry");
							resultLabel.setForeground(Color.RED);
							resultLabel.setFont(new Font("Serif", Font.BOLD, 70));
							// clears previous view in case of invalid entry
							chartpanel1.removeAll();
							chartpanel1.validate();
							chartpanel1.repaint();
						}

						else {
							int monthsInt = Integer.parseInt(months);
							int epochsInt = Integer.parseInt(epochs);
							ArrayList<DataBaseResults> geoData = updateDataset(selectedGeo);
							Instances geoDataNew = prepareData(geoData);

							SimpleLinearRegression model = new SimpleLinearRegression(); // ml method
							model.buildClassifier(geoDataNew);// training model
							// System.out.println(geoData.get(geoData.size() - 1).getRefDates());

							Instances future = new Instances(geoDataNew, monthsInt);// to hold forecasted values
							LocalDate ld = LocalDate.parse("2022-12-01"); // predicting after this date

							for (int i = 1; i <= monthsInt; i++) {
								LocalDate next = ld.plusMonths(i);
								double[] instanceValues = new double[3];
								instanceValues[0] = (double) Date
										.from(next.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime();
								instanceValues[1] = geoComboBox.getSelectedIndex();
								instanceValues[2] = model.classifyInstance(new DenseInstance(1.0, instanceValues));
								Instance newInstance = new DenseInstance(1.0, instanceValues); // to predict nhip value
								future.add(newInstance); //
							}

							for (int i = 0; i < epochsInt; i++) { // epoch = amnt of times u wanna train data
								model.buildClassifier(geoDataNew);// for each epopch train model
								// go thru data
								for (int j = 0; j < future.numInstances(); j++) {
									Instance ins = future.get(j);
									double predictedValue = model.classifyInstance(ins);
									ins.setValue(2, predictedValue);
									geoDataNew.add(ins);
								}
							}

							for (int i = 0; i < future.size(); i++) { // add to dataset
								Instance ins = future.get(i);
								String date = new SimpleDateFormat("MMM-yy").format(new Date((long) ins.value(0)));
								double value = ins.value(2);
								dataset.addValue(value, selectedGeo, date);
							}

							Evaluation eval = new Evaluation(geoDataNew); // get ML stats
							eval.crossValidateModel(model, geoDataNew, 10, new Random(1));

							// stats abt model TODO CAN CREATE HELPER FOR THIS
							double mae = eval.meanAbsoluteError();
							double rmse = Math.sqrt(eval.rootMeanSquaredError());
							double rsq = eval.correlationCoefficient() * eval.correlationCoefficient();

							String resultText = "Mean Absolute Error: " + String.format("%.3f", mae)
									+ "   |   Root Mean Squared Error: " + String.format("%.3f", rmse)
									+ "   |   Correlation Coefficient: " + String.format("%.3f", rsq);

							resultLabel.setText(resultText);
							resultLabel.setFont(new Font("Serif", Font.BOLD, 18));
							resultLabel.setForeground(Color.BLACK);

							JFreeChart line = ChartFactory.createLineChart(

									"NHIP " + selectedGeo, // Chart title
									"Date", // X-Axis Label
									"NHIP", // Y-Axis Label
									dataset);

							chartpanel1.setChart(line);
						}
					}

					if (forecast.equals("Multilayer Perceptron")) {
						if (months == null || months.isEmpty() || epochs == null || epochs.isEmpty()) {
							resultLabel.setText("Invalid Entry");
							resultLabel.setForeground(Color.RED);
							resultLabel.setFont(new Font("Serif", Font.BOLD, 70));

							// clears previous view in case of invalid entry
							chartpanel1.removeAll();
							chartpanel1.validate();
							chartpanel1.repaint();

						} else {
							int monthsInt = Integer.parseInt(months);
							int epochsInt = Integer.parseInt(epochs);
							ArrayList<DataBaseResults> geoData = updateDataset(selectedGeo);
							Instances geoDataNew = prepareData(geoData);

							MultilayerPerceptron model = new MultilayerPerceptron();
							model.buildClassifier(geoDataNew); // Training model
							System.out.println(geoData.get(geoData.size() - 1).getRefDates());

							Instances future = new Instances(geoDataNew, monthsInt);
							LocalDate ld = LocalDate.parse("2022-12-01");

							for (int i = 1; i <= monthsInt; i++) {
								LocalDate next = ld.plusMonths(i);
								double[] instanceValues = new double[3];
								instanceValues[0] = (double) Date
										.from(next.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime();
								instanceValues[1] = geoComboBox.getSelectedIndex();
								instanceValues[2] = model.classifyInstance(new DenseInstance(1.0, instanceValues));
								Instance newInstance = new DenseInstance(1.0, instanceValues);
								future.add(newInstance);
							}

							for (int i = 0; i < epochsInt; i++) {
								model.buildClassifier(geoDataNew); // For each epoch, train model
								for (int j = 0; j < future.numInstances(); j++) {
									Instance ins = future.get(j);
									double predictedValue = model.classifyInstance(ins);
									ins.setValue(2, predictedValue);
									geoDataNew.add(ins);
								}
							}

							for (int i = 0; i < future.size(); i++) {
								Instance ins = future.get(i);
								String date = new SimpleDateFormat("MMM-yy").format(new Date((long) ins.value(0)));
								double value = ins.value(2);
								dataset.addValue(value, selectedGeo, date);
							}

							Evaluation eval = new Evaluation(geoDataNew);
							eval.crossValidateModel(model, geoDataNew, 10, new Random(1));
							double mae = eval.meanAbsoluteError();
							double rmse = Math.sqrt(eval.rootMeanSquaredError());
							double rsq = eval.correlationCoefficient() * eval.correlationCoefficient();

							//
							String resultText = "Mean Absolute Error: " + String.format("%.3f", mae)
									+ "   |   Root Mean Squared Error: " + String.format("%.3f", rmse)
									+ "   |   Correlation Coefficient: " + String.format("%.3f", rsq);

							resultLabel.setText(resultText);
							resultLabel.setFont(new Font("Serif", Font.BOLD, 18));
							resultLabel.setForeground(Color.BLACK);

							JFreeChart line = ChartFactory.createLineChart("NHIP " + selectedGeo, // Chart title
									"Date", // X-Axis Label
									"NHIP", // Y-Axis Label
									dataset);

							chartpanel1.setChart(line);
						}

					}

					// i dont think either is being used but DOUBLE CHECK
					ArrayList<DataBaseResults> geoData = updateDataset(selectedGeo);
					Instances geoDataNew = prepareData(geoData);

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		});
		// top menu
		JPanel topMenu = new JPanel();
		topMenu.add(geoComboBox);
		topMenu.add(new JLabel("Months To Forcast:"));
		topMenu.add(monthsComboBox);
		topMenu.add(new JLabel("Epochs:"));
		topMenu.add(epochss);
		topMenu.add(new JLabel("Forecasting Method:"));
		topMenu.add(forecastingMethods);
		topMenu.add(predictButton);

		JPanel topContainer = new JPanel(new BorderLayout());
		topContainer.add(topMenu, BorderLayout.NORTH);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(topMenu, BorderLayout.NORTH);
		mainPanel.add(chartpanel1, BorderLayout.CENTER);

		resultLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(resultLabel, BorderLayout.SOUTH);

		setContentPane(mainPanel);

	}

	/**
	 * Creates the starting dataset using the results from the entire database. Will
	 * be refactored, was mainly used for testing purposes.
	 * 
	 * @param location - City selected by user for which predictions will be made
	 * @return - The dataset created
	 * @throws Exception - In case the database cannot be read
	 */
	private DefaultCategoryDataset createDataset(String location) throws Exception {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		MySQLAccess dao = new MySQLAccess();
		dao.connectToDataBase();
		ArrayList<DataBaseResults> results = dao.readEntireDataBase();

		for (int i = 0; i < 49654; i++) {
			if (results.get(i).getGeos().equals(location)) {
				double value = results.get(i).getValues();
				String date = results.get(i).getRefDates();
				String geo = results.get(i).getGeos();
				dataset.addValue(value, geo, date);
			}
		}

		return dataset;
	}

	/**
	 * Updates the dataset with the new parameters that the user has selected. 
	 * @param geo - The chosen location chosen by user
	 * @return An array of DataBaseResults with the new data retrieved
	 * @throws Exception - In case the database cannot be read
	 */
	public ArrayList<DataBaseResults> updateDataset(String geo) throws Exception {
		MySQLAccess msa = new MySQLAccess();
		ArrayList<DataBaseResults> results = msa.readEntireDataBase();
		ArrayList<DataBaseResults> resultRet = new ArrayList<DataBaseResults>();

		for (int i = 0; i < results.size(); i++) {
			DataBaseResults row = results.get(i);
			if (row.getGeos().equals(geo)) {
				resultRet.add(results.get(i));
			}
		}

		return resultRet;
	}

	/**
	 *  Covert our data retrieved from the database to an Instance type so that 
	 *  forecasting methods can later use this data. 
	 * @param data - An array of DataBaseResults
	 * @return the data once converted to type Instances
	 */
	public Instances prepareData(ArrayList<DataBaseResults> data) {

		ArrayList<Attribute> attributes = new ArrayList<>();

		// Create attribute for refDates
		Attribute refDates = new Attribute("refDates");
		attributes.add(refDates);

		// Create attribute for geos
		Attribute geos = new Attribute("geos");
		attributes.add(geos);
		// Create attribute for values
		Attribute values = new Attribute("values");
		attributes.add(values);

		// Create Weka Instances object with the attributes
		Instances instances = new Instances("forecasting", attributes, data.size());

		// Add instances to Weka dataset
		for (DataBaseResults row : data) {
			double[] instanceValues = new double[3];
			instanceValues[0] = refDates.addStringValue(row.getRefDates());
			instanceValues[1] = geos.addStringValue(row.getGeos());
			instanceValues[2] = row.getValues();
			instances.add(new DenseInstance(1.0, instanceValues));
		}

		// Set the class index
		instances.setClassIndex(instances.numAttributes() - 1);

		return instances;

	}

	/**
	 * Updates the dataset based on the location selected by the user
	 * @param geo - Location selected by the user
	 * @throws Exception - In case database cannot be accessed
	 */
	public void updateBasedOnGeo(String geo) throws Exception {
		this.dataset = createDataset(geo);
	}

	// TODO DONT THINK WE USING BUT LEFT JUST IN CASE
//	public void updateDatasett(String geo, String start, String end) throws Exception {
//		dataset.clear();
//		MySQLAccess dao = new MySQLAccess();
//		dao.connectToDataBase();
//
//		String query = "SELECT * FROM nhip " + "WHERE STR_TO_DATE(CONCAT('01-', REF_DATE), '%d-%b-%y') "
//				+ "BETWEEN ? AND ?;";
//
//		ArrayList<DataBaseResults> result3 = dao.sendQuery(query, start, end);
//
//		for (int i = 0; i < result3.size(); i++) {
//			DataBaseResults row = result3.get(i);
//			if (row.getGeos().equals(geo)) {
//				double value = row.getValues();
//				String date = row.getRefDates();
//				String geoName = row.getGeos();
//				dataset.addValue(value, geoName, date);
//			}
//		}
//	}

	public static void main(String[] args) throws Exception {

		Forecasting frame = new Forecasting();
		frame.pack();
		frame.setSize(1500, 900);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

}
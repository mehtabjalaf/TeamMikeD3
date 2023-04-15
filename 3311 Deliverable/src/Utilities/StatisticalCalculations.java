package Utilities;

import java.util.ArrayList;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TTest;

public class StatisticalCalculations {
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

		ret = nullHypothesisCheck(ret,p);

		return ret;
	}
	
	public String nullHypothesisCheck (String ret, double p) {
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
	public String calculateMannWhitneyU(ArrayList<DataBaseResults> set1, ArrayList<DataBaseResults> set2) {
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
}

package Utilities;

/**
 * 
 * @version 2.0
 * Object class that stores every result retrieved from the database.
 * Its attributes match each row from our NHIP table. 
 *
 */
public class DataBaseResults {

	private String refDates;
	private String geos; 
	private double values;  
	
	/**
	 * Constructor that initializes each attribute to empty values.
	 * Used for testing. 
	 */
	public DataBaseResults() {
		refDates = ""; 
		geos = "";
		values = 0.0;
	}
	
	/**
	 * Overloaded constructor for the real creation of a database data enty. 
	 * @param refDates - Matches the REF_DATES column in the database
	 * @param geos - Matches the GEO column in the database
	 * @param values - Matches the VALUES column in the database
	 */
	public DataBaseResults(String refDates, String geos, double values) {
		this.refDates = refDates; 
		this.geos = geos;
		this.values = values;
	}

	public String getRefDates() {
		return refDates;
	}

	public void setRefDates(String refDates) {
		this.refDates = refDates;
	}

	public String getGeos() {
		return geos;
	}

	public void setGeos(String geos) {
		this.geos = geos;
	}

	public double getValues() {
		return values;
	}

	public void setValues(double values) {
		this.values = values;
	}
	
}

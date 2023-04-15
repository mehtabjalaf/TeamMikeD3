package Utilities;

public class TimeSeriesStartEnd {
	public String selectedCountry;
	public String selectedCountry2;
	public String sendStart;
	public String sendEnd;

	
	public TimeSeriesStartEnd(String selectedCountry, String selectedCountry2, String sendStart, String sendEnd) {
		this.selectedCountry = selectedCountry;
		this.selectedCountry2 = selectedCountry2;
		this.sendStart = sendStart;
		this.sendEnd = sendEnd;
	}
	
	public TimeSeriesStartEnd(String selectedCountry,  String sendStart, String sendEnd) {
		this.selectedCountry = selectedCountry;
		this.sendStart = sendStart;
		this.sendEnd = sendEnd;
	}
	
	public void updateCountry (String selectedCountry2) {
		this.selectedCountry = selectedCountry2;
	}
	
	

}

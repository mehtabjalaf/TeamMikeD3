package Utilities;

public class SelectedMonth {
	public String getMonthNumber(String monthName) {  //has to be public bc nhip Comparison uses it
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
}

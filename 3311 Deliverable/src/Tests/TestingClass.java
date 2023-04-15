package Tests;
import java.util.ArrayList;

import javax.swing.JFrame;

import Utilities.DataBaseResults;
import Utilities.MySQLAccess;

/**
 * Class used for testing how data was being retrieved from the database via MySQLAccess.
 * @author fio
 *
 */
public class TestingClass{
	public static void main(String[] args) throws Exception {
        MySQLAccess dao = new MySQLAccess();
        dao.connectToDataBase(); //connects to database
        ArrayList<DataBaseResults> results = dao.readEntireDataBase();
        
       System.out.println(results.get(0).getGeos());
       System.out.println(results.get(0).getRefDates());
       System.out.println(results.get(0).getValues());
	
       
       
       //TODO CODE IN CASE I CHANGE WRITERESULTS FUNCT
       ArrayList<DataBaseResults> resultsForQuery = dao.sendQuery("select * from nhip where geo = 'Calgary, Alberta'");
       System.out.println(resultsForQuery.get(0).getGeos());
       System.out.println(resultsForQuery.get(0).getRefDates());
       System.out.println(resultsForQuery.get(0).getValues());
       
//       ArrayList<DataBaseResults> resultsForQuery2 = dao.sendQuery("select 'REF_DATE','GEO',stddev(value) as value from  nhip where geo = 'Calgary, Alberta'");
//
//       System.out.println(resultsForQuery2.get(0).getValues());
	}
	
}



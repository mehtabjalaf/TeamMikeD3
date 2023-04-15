package Utilities;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @version 2.0
 * Class implementing the DataBaseConnection interface. 
 * Used to access our MySQL database, retrieve data and send queries. 
 */
public class MySQLAccess implements DataBaseConnection{
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	/**
	 * Connects to the database
	 * @throws Exception
	 */
	public void connectToDataBase() throws Exception{
		
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/3311deliverable", "root", "Sunpreet02"); 

			//statement = connect.createStatement();

		} catch (Exception e) {
			throw e;
		} finally {
			//close();
		}
		
	}

	/**
	 * Read the entire database and stores the results in an arrayList
	 * @return The arrayList containing every row from the database
	 * @throws Exception
	 */
	public ArrayList<DataBaseResults> readEntireDataBase() throws Exception {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/3311deliverable", "root", "Sunpreet02"); // TODO

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			
			//ADDED CONNECT METHOD SO NOW WE JUST CALL THAT 
			//connectToDataBase();
			
			
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("select * from nhipcopy"); // changed

			ArrayList<DataBaseResults> result = writeResultSet(resultSet);

//	            // PreparedStatements can use variables and are more efficient
//	            preparedStatement = connect
//	                    .prepareStatement("insert into  feedback.comments values (default, ?, ?, ?, ? , ?, ?)");
//	            // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
//	            // Parameters start with 1
//	            preparedStatement.setString(1, "Test");
//	            preparedStatement.setString(2, "TestEmail");
//	            preparedStatement.setString(3, "TestWebpage");
//	            preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
//	            preparedStatement.setString(5, "TestSummary");
//	            preparedStatement.setString(6, "TestComment");
//	            preparedStatement.executeUpdate();
//
//	            preparedStatement = connect
//	                    .prepareStatement("SELECT myuser, webpage, datum, summary, COMMENTS from feedback.comments");
//	            resultSet = preparedStatement.executeQuery();
//	            writeResultSet(resultSet);
//
//	            // Remove again the insert comment
//	            preparedStatement = connect
//	            .prepareStatement("delete from feedback.comments where myuser= ? ; ");
//	            preparedStatement.setString(1, "Test");
//	            preparedStatement.executeUpdate();
//
//	            resultSet = statement
//	            .executeQuery("select * from feedback.comments");
//	            writeMetaData(resultSet);

			return result;

		} catch (Exception e) {
			throw e;
		} finally {
			//close();
		}

	}

	/**
	 * Sends the necessary query to the database and retrieves the results. 
	 * @param query - Query in a String type
	 * @return An array containing the results from the query
	 * @throws Exception
	 */
	public ArrayList<DataBaseResults> sendQuery(String query) throws Exception {
		// private ResultSet resultSet = null;
		
		try {
			statement = connect.createStatement();
			resultSet = statement.executeQuery(query);
			ArrayList<DataBaseResults> result = writeResultSet(resultSet);
			return result;
		}

		catch (Exception e) {
			throw e;
		} finally {
			//close();
		}
	}
	
	/**
	 * Sends the necessary more complex query to the database and retrieves the results. 
	 * @param query - Query in a String type
	 * @param sendStart - Start Date selected by the user
	 * @param sendEnd - End Date selected by the user
	 * @return An array containing the results from the query
	 * @throws Exception
	 */
	public ArrayList<DataBaseResults> sendQuery(String query, String sendStart, String sendEnd) throws Exception {
        try {
            // Set up the prepared statement with the parameter placeholders
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, sendStart);
            statement.setString(2, sendEnd);

            // Execute the query and process the results
            ResultSet resultSet = statement.executeQuery();
            ArrayList<DataBaseResults> result = writeResultSet(resultSet);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            //close();
        }
    }

//    private void writeMetaData(ResultSet resultSet) throws SQLException {
//        //  Now get some metadata from the database
//        // Result set get the result of the SQL query
//
//        System.out.println("The columns in the table are: ");
//
//        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
//        for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
//            System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
//        }
//    }

	/**
	 * Writes the results from the database into an array of DataBaseResults Objects
	 * @param resultSet - results retrieved from database
	 * @return - Array of DataBaseResults Objects containing each result
	 * @throws SQLException
	 */
	private ArrayList<DataBaseResults> writeResultSet(ResultSet resultSet) throws SQLException {
		// ResultSet is initially before the first data set

		ArrayList<DataBaseResults> dbResults = new ArrayList<DataBaseResults>();
		DataBaseResults element;
		
//		ResultSetMetaData rsmd = resultSet.getMetaData(); 
//		int numberOfColumns = rsmd.getColumnCount();
//		ArrayList<String> columns = new ArrayList<String>(); 
		//get the list of columns retrieved
		
//		for(int i=0; i < numberOfColumns; i++) {
//			columns.add(rsmd.getColumnName(i));
//		}

//    	ArrayList refDates = new ArrayList(); 
//    	ArrayList geos = new  ArrayList();
//    	ArrayList values = new  ArrayList();

		while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g. resultSet.getSTring(2);
			
			//if(resultSet.)

			String REF_DATE = resultSet.getString("REF_DATE");
			String GEO = resultSet.getString("GEO");
			double value = Double.valueOf(resultSet.getString("value"));

			element = new DataBaseResults(REF_DATE, GEO, value);
			
			

//            element.setRefDates(REF_DATE); 
//            element.setGeos(GEO);
//            values.add(value);
//            System.out.println("REF_DATE: " + REF_DATE);
//            System.out.println("GEO: " + GEO);
//            System.out.println("value: " + value);

			dbResults.add(element);
		}

		return dbResults;
	}


	/**
	 * Closes the database resultSet and the database overall
	 */
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

}

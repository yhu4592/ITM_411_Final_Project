package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
		
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE yhu2_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), closed VARCHAR(30), start_date DATETIME DEFAULT CURRENT_TIMESTAMP, end_date DATETIME DEFAULT NULL)";
		final String createUsersTable = "CREATE TABLE yhu2_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();
			statement.executeUpdate(createUsersTable);
			statement.executeUpdate(createTicketsTable);
			
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into yhu2_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc) {
		int id = 0;
		// Check if either field is empty and cancel ticket creation
		if (ticketName==null || ticketDesc==null) {
			return id;
		} 
		else {
			try {
				statement = getConnection().createStatement();
				statement.executeUpdate("INSERT INTO yhu2_tickets" + "(ticket_issuer, ticket_description, closed) values(" + " '"
						+ ticketName + "','" + ticketDesc + "', 'No' )", Statement.RETURN_GENERATED_KEYS);
	
				// retrieve ticket id number newly auto generated upon record insertion
				ResultSet resultSet = null;
				resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) {
					// retrieve first field in table
					id = resultSet.getInt(1);
				}
	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return id;
		}
	}

	// Read records only for records created by user
	public ResultSet readRecords(String userName) {
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM yhu2_tickets WHERE ticket_issuer='" + userName + "' ");
			System.out.println("Records retrieved!");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
	// Admin can read ALL tickets regardless who created them
		public ResultSet readRecords() {
			ResultSet results = null;
			try {
				statement = connect.createStatement();
				results = statement.executeQuery("SELECT * FROM yhu2_tickets");
				//connect.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return results;
		}
	
		public Boolean deleteRecords(String ticketId) {
			Boolean delete = false;
			// Check if either field is empty and cancel ticket creation
			if (ticketId==null) {
				return delete;
			} 
			else {
				try {
					statement = getConnection().createStatement();
					ResultSet rs = statement.executeQuery("SELECT * FROM yhu2_tickets WHERE ticket_id=" + ticketId);
					if (rs.next()) {
						statement.executeUpdate("DELETE FROM yhu2_tickets WHERE ticket_id=" + ticketId);
						delete = true;
					}
		
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return delete;
			}
		}
		
		public Boolean updateRecords(String ticketId, String ticketDesc) {
			Boolean update = false;
			// Check if either field is empty and cancel ticket creation
			if (ticketId==null || ticketDesc==null) {
				return update;
			} 
			else {
				try {
					statement = getConnection().createStatement();
					ResultSet rs = statement.executeQuery("SELECT * FROM yhu2_tickets WHERE ticket_id=" + ticketId);
					if (rs.next()) {
						statement.executeUpdate("UPDATE yhu2_tickets SET ticket_description='" + ticketDesc + "' WHERE ticket_id=" + ticketId);
						update = true;
					}
		
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return update;
			}
		}
}

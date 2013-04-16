package org.smslib.smsserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class revolver {

	/**
	 * @param args
	 */
	public  Connection con = null;
	static String url = "jdbc:mysql://localhost:3306/smslib";
	static String username = "smslib";
	static String password = "smslib";
	
	
	public revolver(){
		getMessages();
	}
	
	//select messages from db
	
	private void getMessages() {
		String sqlQuery = "select * from smsserver_in where process = 0";
		
		try {
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlQuery);
			
				while(rs.next()) {
					System.out.println("New message found " + rs.getString("Text"));
					updateProcessed("UPDATE smsserver_in SET process=1 WHERE id= "+rs.getInt(1)+" ;");
					processMessage(rs);
					}	 
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	
	//Check the type of message
	private void processMessage(ResultSet rs){
		//if message is x send to user y
		
		String message = "";

		try {
			message = rs.getString("text");
			String PhoneNumber = rs.getString("originator");
			
			if (message.equals("O+")){
				getBloodCount("Select hospitalname, opositive from hospitals", PhoneNumber);
			}
			else if (message.equals("O-")){
				getBloodCount("Select hospitalname, onegative from hospitals", PhoneNumber);
			}
			else if (message.equals("A+")){
				getBloodCount("Select hospitalname, apositive from hospitals", PhoneNumber);
			}
			else if (message.equals("A-")){
				getBloodCount("Select hospitalname, anegative from hospitals", PhoneNumber);
			}
			else if (message.equals("B+")){
				getBloodCount("Select hospitalname, bpositive from hospitals", PhoneNumber);
			}
			else if (message.equals("B-")){
				getBloodCount("Select hospitalname, bnegative from hospitals", PhoneNumber);
			}
			else if (message.equals("AB+")){
				getBloodCount("Select hospitalname, abpositive from hospitals", PhoneNumber);
			}
			else if (message.equals("AB-")){
				getBloodCount("Select hospitalname, abnegative from hospitals", PhoneNumber);
			}
			else {
				insertNewMessage("That is not a valid blood type", PhoneNumber);
			}
			
			
		} catch (SQLException e) {	
			e.printStackTrace();
		}
	
	}
	
	//message
	private void getBloodCount(String sqlStmt, String PhoneNumber){
		
		String Usermessage = "";
		
		try {
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStmt);
			
				while(rs.next()) {
					Usermessage += rs.getString(1) +  " " + rs.getString(2) + " "; 
				}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		insertNewMessage(Usermessage, PhoneNumber);
	}
	
	//insert into db new message
	private void insertNewMessage(String message, String phonenumber){
		
		String insertString = "insert into smsserver_out (recipient, text, create_date) values (" + phonenumber + ",'" + message + "', now() );" ;
		try {
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			stmt.executeUpdate(insertString);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void updateProcessed(String Sqlstmt){
		
		try {
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			stmt.executeUpdate(Sqlstmt);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

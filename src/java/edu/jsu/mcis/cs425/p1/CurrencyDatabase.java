package edu.jsu.mcis.cs425.p1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CurrencyDatabase {
    
    Context envContext = null, initContext = null;      
    DataSource ds = null;                               
    Connection conn = null;

    public CurrencyDatabase() throws NamingException {
        
        try {
            
            envContext = new InitialContext();
            initContext  = (Context)envContext.lookup("java:/comp/env");
            ds = (DataSource)initContext.lookup("jdbc/db_pool");
            conn = ds.getConnection();
            
        }
        
        catch (SQLException e) {}

    }
    
    //METHOD TO GENERATE THE RESULTSET FROM THE DATABASE "RATES" TABLE
    public ResultSet getResultSetFromRatesTable(String date) throws SQLException{
        
        PreparedStatement pstatement = null;
        ResultSet resultset = null;
        
        String selectQuery;
        
        boolean hasresults;
        
        try{
            selectQuery = "SELECT * FROM cs425_p1_fa21.rate WHERE date = ?";
            pstatement = conn.prepareStatement(selectQuery);
            pstatement.setString(1, date); 
            
            hasresults = pstatement.execute();
            
            while ( hasresults || pstatement.getUpdateCount() != -1 ) {
                    if ( hasresults ) {
                        resultset = pstatement.getResultSet();
                        return resultset;  
                    }
                    else {
                        if ( pstatement.getUpdateCount() == -1 ) {
                            break;
                        } 
                    }

                    hasresults = pstatement.getMoreResults();
            }       
        }
        
        catch(SQLException e){}
        
        return resultset;
        
    } // End getResultSetFromRatesTable()

    
    
    //METHOD TO GENERATE THE RESULTSET FROM THE DATABASE "CURRENCY" TABLE
    public ResultSet getResultSetFromCurrencyTable() throws SQLException{
        
        PreparedStatement pstatement = null;
        ResultSet resultset = null;
        
        String selectQuery;
        
        boolean hasresults;
        
        // PREPARE SELECT QUERY
        selectQuery = "SELECT * FROM cs425_p1_fa21.currency";
        pstatement = conn.prepareStatement(selectQuery);
            
        hasresults = pstatement.execute();

         while ( hasresults || pstatement.getUpdateCount() != -1 ) {
                if ( hasresults ) {
                    resultset = pstatement.getResultSet();
                    return resultset;
                }
                else {
                    if ( pstatement.getUpdateCount() == -1 ) {
                        break;
                    } 
                }

                hasresults = pstatement.getMoreResults();
         }       
        return resultset;
    } // End getResultSetFromCurrencyTable()
    
    
    
    public void addNewExchangeDataToDatabase(JSONObject newExchangeData, String date) throws SQLException {
         
        PreparedStatement pstatement = null;
        String insertQuery; 
        
        insertQuery = "INSERT INTO cs425_p1_fa21.rate (currencyid, date, rate) VALUES (?, ?, ?)"; 
        pstatement = conn.prepareStatement(insertQuery); 
        
        // gets the value mapped to the key "rates"
        JSONObject exchangeData = (JSONObject)newExchangeData.get("rates");
        
        try {
            // iterate over JSONObject and batch process the prepared statement
           
                // iterate over JSONObject and batch process the prepared statement
                for(Iterator iterator = exchangeData.keySet().iterator(); iterator.hasNext();){
                    String key = (String)iterator.next(); // key is the currencyid
                    double value = Double.parseDouble(String.valueOf(exchangeData.get(key))); // value is the rate
                    
                    System.out.println("Date: " + date + ", " + key + ": " + value);
                    
                    //prepared statement: "INSERT INTO cs425_p1_fa21.rate (currencyid, date, rate) VALUES (?, ?, ?)";
                    pstatement.setString(1, key);
                    pstatement.setString(2, date);
                    pstatement.setDouble(3, value);
                    
                    pstatement.addBatch();
                    
                }// end for loop
                
                int[] r = pstatement.executeBatch();
                conn.commit();
                pstatement.close();
        } // End try block 
        
        catch(NumberFormatException | SQLException e){}
        
    }// End addNewExchangeDataToDatabase()
    
    
    
    
    
    // METHOD TO CREATE A JSON OBJECT FROM THE DATABASE "CURRENCY" TABLE
    public LinkedHashMap<String, String> createJSONObjectFromResultSet(ResultSet resultset) throws ServletException, IOException, SQLException {
        
        LinkedHashMap<String, String> record = new LinkedHashMap<>(); 
        
        try {
            
         
            while(resultset.next()) {
                String id = resultset.getString("id"); 
                String description = resultset.getString("description");  
                record.put(id, description);
            } 

            
        } // End try block
       
        catch (SQLException e) {}
        
        return record;
        // change to JSONobject later
        
        
    } // End createJSONObject()
    
    
    // METHOD TO CREATE A JSON OBJECT FROM THE DATABASE "RATES" TABLE
    public LinkedHashMap<String, Object> createJSONObjectFromResultSet(ResultSet resultset, String date) throws ServletException, IOException, SQLException {
        
        LinkedHashMap<String, BigDecimal> record = new LinkedHashMap<>(); // currencyid:rate inner map
        LinkedHashMap<String, Object> object = new LinkedHashMap<>(); // outer map

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        try {
            
            System.out.println("*** Getting Query Results ... ");
         
            // create inner hashmap
            while(resultset.next()) {
                String currencyid = resultset.getString("currencyid"); 
                BigDecimal rate = BigDecimal.valueOf(resultset.getDouble("rate"));
                record.put(currencyid, rate); 
            } 
            
            // create outer hashmap
            object.put("success", true);
            object.put("timestamp", timestamp.getTime()); 
            object.put("base", "EUR");
            object.put("date", date);
            object.put("rates", record);
            
        } // End try block
       
        catch (SQLException e) {}
        
        // change to JSONobject later
        return object;
        
    } // End createJSONObject()
    
    
    public String getJSONString(ResultSet resultset, String date) throws SQLException, ServletException, IOException{
        if (!resultset.isBeforeFirst()){
            JSONObject newExchangeData = getRequest(date);
            addNewExchangeDataToDatabase(newExchangeData, date);
            resultset = getResultSetFromRatesTable(date);
        }
        return JSONValue.toJSONString(createJSONObjectFromResultSet(resultset, date));
    }
    
    

    public JSONObject getRequest(String date) {
        
        String access_key = ""; //Go to URL below to obtain an access key
        String uri = "http://api.exchangeratesapi.io/v1/" + date + "?access_key=" + access_key;
        JSONObject jsonResponse= null;
        try {
            URL url= new URL(uri);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json; charset=UTF-8");
            connection.setRequestMethod("GET");   
            int responseCode= connection.getResponseCode();
            if (responseCode== HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(in);
                String response = reader.readLine();
                JSONParser parser = new JSONParser();
                jsonResponse= (JSONObject)parser.parse(response);
            }
            connection.disconnect();
        }
        catch (IOException | ParseException e) {}
        
        return jsonResponse;
    } // End getRequest()
    

    
    public void closeConnection() {
        
        if (conn != null) {
            
            try {
                conn.close();
            }
            
            catch (SQLException e) {}
            
        }
    
    } // End closeConnection()
    
    public Connection getConnection() { return conn; }
}

package edu.jsu.mcis.cs425.p1;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.sql.SQLException;
import javax.naming.NamingException;


public class ExchangeServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        response.setContentType("application/json");
        PrintWriter out = response.getWriter(); 

        String jsonString; 
        String date;

        CurrencyDatabase db; 

        
        try {
            
            db = new CurrencyDatabase();  
            
            date = request.getParameter("date");
            
            if (date == null || "".equals(date)) {
                Date d = new Date(System.currentTimeMillis());  
                date = d.toString();
            }
            
            jsonString = db.getJSONString(db.getResultSetFromRatesTable(date), date);
            out.write(jsonString);
        } 
        catch(IOException | SQLException | NamingException | ServletException e){}   
    } 
    
    @Override
    public String getServletInfo() {
        return "Exchange Servlet accepts GET requests.";
    }
}

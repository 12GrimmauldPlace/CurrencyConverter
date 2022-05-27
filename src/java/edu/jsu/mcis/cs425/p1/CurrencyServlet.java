package edu.jsu.mcis.cs425.p1;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONValue;

public class CurrencyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
            
        CurrencyDatabase db; 
        
        LinkedHashMap<String, String> jobj;
        
        try{

            db = new CurrencyDatabase();
            
            jobj = db.createJSONObjectFromResultSet(db.getResultSetFromCurrencyTable());

            out.write(JSONValue.toJSONString(jobj));   
        }
       
        catch (Exception e) { 
            e.printStackTrace(); 
        }
   
    }

    @Override
    public String getServletInfo() {
        return "Currency Servlet accepts GET requests.";
    }

}
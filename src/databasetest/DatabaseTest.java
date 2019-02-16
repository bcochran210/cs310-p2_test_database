package databasetest;

import java.sql.*;
import java.util.LinkedHashMap;
import org.json.simple.*;

public class DatabaseTest {

    public static JSONArray getJSONData(){
        
        JSONArray results = null;
        
        Connection conn = null;
        PreparedStatement pstSelect = null, pstUpdate = null;
        ResultSet resultSet = null;
        ResultSetMetaData metaData = null;
        
        String query, value;
        String[] headers;
        
        JSONArray records = new JSONArray();
        
        boolean hasResults;
        int resultCount, columnCount;
        
        try {
            
            /* Indentify the Server */
            
            String server = ("jdbc:mysql://localhost/p2_test");
            String username = "root";
            String password = "CS310";
            
            /* Load the MySQL JDBC Driver */
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            /* Open Connection */

            conn = DriverManager.getConnection(server, username, password);
            
            if (conn.isValid(0)){
                
                /* Connection Open! */
                
                System.out.println("Connected Successfully!");
                
                // Parepare Select Query
                
                query = "SELECT * FROM people";
                pstSelect = conn.prepareStatement(query);
                
                // Execute Select Query
                
                hasResults = pstSelect.execute();
                
                /* Get Results */
                
                while (hasResults || pstSelect.getUpdateCount() != -1){
                
                    if (hasResults){

                        /* Get Meta data */

                        resultSet = pstSelect.getResultSet();
                        metaData = resultSet.getMetaData();
                        columnCount = metaData.getColumnCount();

                        headers = new String[columnCount - 1];

                        for(int i = 0; i < headers.length; i++){
                            headers[i] = metaData.getColumnLabel(i + 2);
                        }

                        /* Get data, print it */

                        LinkedHashMap data = new LinkedHashMap();

                        while(resultSet.next()){

                            /* Loop through columns, print */

                            data = new LinkedHashMap();

                            for(int i = 0; i < headers.length; i++){
                                value = resultSet.getString(i + 2);
                                if(resultSet.wasNull()){
                                    data.put(headers[i], "");
                                } else {
                                    data.put(headers[i], value);
                                }
                            }

                            records.add(data);

                        }

                    } else {

                        resultCount = pstSelect.getUpdateCount();

                        if (resultCount == -1){
                            break;
                        }

                    }
                    
                    /* Check for more data */
                    
                    hasResults = pstSelect.getMoreResults();

                }
                
                results = records;
            
            }
            
            /* Close connection */
            
            System.out.println();
            
            conn.close();
            
        } catch (Exception e){
            System.err.println(e.toString());
        }
        
        /* close other databases */
        
        finally {
            
            if (resultSet != null){
                try {
                    resultSet.close();
                    resultSet = null;
                } catch (Exception e){}
            }
            if (pstSelect != null){
                try {
                    pstSelect.close();
                    pstSelect = null;
                } catch (Exception e){}
            }
            if (pstUpdate != null){
                try{
                    pstUpdate.close();
                    pstUpdate = null;
                } catch (Exception e){}
            }
            
        }
        
        return results;
    } 
    
}


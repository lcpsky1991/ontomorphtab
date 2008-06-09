/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.brainCatalog.ccdb.database;



import  java.io.*;
import  java.net.*;
import  java.sql.*;
import  java.util.*;
import  oracle.jdbc.pool.*;
import  javax.sql.*;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
/**
 * Re-usable database connection class
 */
public class DBService {




 static String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
  static String HOST = null;

  static String DATABASENAME = null;
  static  int PORT = 0;

  static final String connectString="jdbc:oracle:thin:@";
  static String URL = "jdbc:oracle:thin:@"+HOST+":"+PORT+":"+DATABASENAME;
  static String USERNAME =null;
  static String PASSWORD =null;



    public DBService(String path) throws Exception
    {
        this.readConfig(path);
    }

  //  public DBService()
 //   {
  //
  //  }


    // Load the driver when this class is first loaded
    static {
        try {
            Class.forName(DB_DRIVER).newInstance();
        } catch (ClassNotFoundException cnfx) {
            cnfx.printStackTrace();
        } catch (IllegalAccessException iaex) {
            iaex.printStackTrace();
        } catch (InstantiationException iex) {
            iex.printStackTrace();
        }
    }

    public static String getRealPath(String path) throws Exception
    {
        try
        {
            //Now, go get the connection info from resource files
            ResourceBundle rb = new PropertyResourceBundle(new FileInputStream(
                    path));
            String value = rb.getString("resource"); //Get the path and filename of the real connection resource file.
            return value;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public boolean readConfig(String path) throws Exception
    {
      try
      {
        //Now, go get the connection info from resource files
        ResourceBundle rb = null;//new PropertyResourceBundle(new FileInputStream(path));
       // String value = rb.getString("resource"); //Get the path and filename of the real connection resource file.
        //if ( (value == null) || (value.trim().length() < 1))
       //   return false;

        //Then get the real connection info from the real resource
        rb = new PropertyResourceBundle(new FileInputStream(path));

        //Then, to get a property, just use the getString() method such as
        HOST = rb.getString("host");
        String iport = rb.getString("port");
        PORT = Integer.valueOf(iport).intValue();
        DATABASENAME = rb.getString("cid");
        USERNAME = rb.getString("user");
        PASSWORD = rb.getString("password");
       // URL=connectString+HOST+":"+PORT+":"+DATABASENAME;
       URL=rb.getString("private_jdbc_url");//connectString+HOST+":"+PORT+":"+DATABASENAME;
       System.out.println(URL);
        return true;
      }
      catch(Exception e)
      {
        e.printStackTrace();
        throw new Exception("Problem reading database parameters: "+e.getMessage());

      }



    }



    public String getHost()
    {
      return HOST;
    }
    public int getPort()
    {
      return PORT;
    }
    public String getDBName()
    {
      return DATABASENAME;
    }
    public String getUerName()
    {
      return USERNAME;
    }
    public String getPwd()
    {
      return PASSWORD;
    }

    /**
     * Returns a normal connection to the database
     */
    public static Connection getConnection () {
         try {
            return  DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

      /**
     * Returns a pool connection to the database
     */
    public static PooledConnection getPooledConnection () {
        try {
        // create a connection pool data source object
        OracleConnectionPoolDataSource ods = new OracleConnectionPoolDataSource();
        // connection usering the connection pool data source object
        ods.setServerName(HOST);
        ods.setDatabaseName(DATABASENAME);
        ods.setPortNumber(PORT);
        ods.setDriverType("thin");
        ods.setUser(USERNAME);
        ods.setPassword(PASSWORD);
        PooledConnection myPooledConnection = ods.getPooledConnection();
         return  myPooledConnection;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }

    }


    /**
     * Static method that releases a connection
     * @param con the connection
     */
    public static void closeConnection (Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection (PooledConnection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Static method that return a result set
     * @param query
     * @return
     */
    public static ResultSet getResultSet (String query) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        // grab a connection to the database
        try {

            con = getConnection();
            if (con == null)
                return  null;
            stmt = con.createStatement();
            // run the sql query to obtain a result set

            rs = stmt.executeQuery(query);

        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (RuntimeException rex) {
            rex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

         return rs;

    }

      /**
     * Static method that return a result set
     * @param query
     * @return
     */
    public static java.util.Map getTypeMap () {
        Connection con = null;
        Statement stmt = null;
        java.util.Map map = null;
        // grab a connection to the database
        try {
            con = getConnection();
            if (con == null)
                return  null;
           map = con.getTypeMap();

        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (RuntimeException rex) {
            rex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  map;
    }

    public String debug()
    {
      return this.URL+"   "+this.USERNAME+"  "+this.PASSWORD;

    }


   /* public static void main(String[] args)
    {


      DBService d = new DBService();
      System.out.println("before"+d.debug());
      d.readConfig("C:/tomcat55/webapps/CCDBProject/WEB-INF/config/ResourceMetadata.properties");
       System.out.println("after"+d.debug());

      DBService c = new DBService();
      System.out.println("other object"+c.debug());

    }*/

}

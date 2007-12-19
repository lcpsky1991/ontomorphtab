
package neurolucida;
import java.util.*;
//oracle jdbc connection
import java.sql.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
//import oracle.jdbc.driver.*;

//import oracle.jdbc.driver.OracleResultSet;
//import oracle.sql.ARRAY;
//import org.postgresql.jdbc3.Jdbc3Array;
public class RelationalDB {
	
	    /* Database properties */
	    Connection conn = null;
	    Properties dbProperties = new Properties();
	    Statement stmt = null;
	    ResultSet rs = null;
	    ResultSetMetaData md = null;
	    boolean print = false;
	    final float PI = 3.1415f;
	    final int INCREMENT = 10000;
	    int status = 0;
	    public RelationalDB(String file){
	    	try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = null;
				while((line = br.readLine()) != null ){
					String key = line.substring(0,line.indexOf("=")).trim();
					String value = line.substring(line.indexOf("=")+1).trim();
					//NeurolucidaInterface.s_.append(key + ":" + value + "<hr>");
					dbProperties.put(key, value);
				}
				if (dbProperties.size() < 4){
					System.out.println("Exception: Set all db properties! (url, username, password, databaseDriver)");
					NeurolucidaInterface.s_.append("db properties less than 4");
					//System.exit(1);
				}
				conn = (Connection)initDBConn();
			} catch (FileNotFoundException e) {
				NeurolucidaInterface.s_.append("file not found: " + file + "<hr>");
				status = -1;
				e.printStackTrace();
			} catch (IOException e) {				
				NeurolucidaInterface.s_.append("io exception: " + file + "<hr>");
				status = -2;
				e.printStackTrace();
			}
	    }

	    public RelationalDB(String url, String user, String passwd, String driver){        
	    	dbProperties.put("url", url);
		    dbProperties.put("username", user);
		    dbProperties.put("password", passwd);
		    dbProperties.put("databaseDriver", driver);
	   	    try {
	       	    conn = (Connection)initDBConn();
	        }
	   	    catch (Exception e) {
	       	    System.out.println("in Constructor: Exception at connection");
	           	e.printStackTrace();
	        }
	    }

	    /* oracle/postgresql db connection related methods*/
	    public Connection initDBConn() {
	    	NeurolucidaInterface.s_.append("Initiating db conn: <br>");
	        Connection dbConn = null;
	        try {
	            System.out.println("database.RelationalDB Initiating DB Connections for ");
	            String driverName = dbProperties.getProperty("databaseDriver");
	            if (driverName == null) {
	                driverName = "oracle.jdbc.driver.OracleDriver";
	            	//driverName = "org.postgresql.Driver";
	                System.out.println("Driver name is " + driverName);
	            }
	            Class driverClass = Class.forName(driverName);
	            Constructor driverConstructor = driverClass.getConstructor(null);
	            Driver driverObject = (Driver)(driverConstructor.newInstance(null));
	            DriverManager.registerDriver(driverObject);
	            //System.setProperty("jdbc.drivers", driverName);
	            dbConn = DriverManager.getConnection(dbProperties.getProperty("url"), dbProperties.getProperty("username"),
	                dbProperties.getProperty("password"));
	            System.out.println(":-) CONNECTION TO THE DATABASE ESTABLISHED!");
	            NeurolucidaInterface.s_.append("CONNECTION ESTABLISHED!");
	        }
	        catch (Exception e) {
	            System.out.println(":-( CANNOT CONNECT TO THE DATABASE!");
	            NeurolucidaInterface.s_.append("CONNECTION NOT ESTABLISHED! " + e);
	            e.printStackTrace();
	            return null;
	        }
	        return dbConn;
	    }

	    public void deleteAll(String n){
	    	if (n==null){
	    		executeUpdate("delete from nl_nodes");
	    		executeUpdate("delete from nl_segments");
	    		executeUpdate("delete from nl_tree");
	    		executeUpdate("commit");	    		
	    	}
	    	else{
	    		n = n.trim().toUpperCase();
	    		String cond = " where upper(neuron)='" + n + "'";
	    		executeUpdate("delete from nl_nodes " + cond);
	    		executeUpdate("delete from nl_segments " + cond);
	    		executeUpdate("delete from nl_tree " + cond);
	    		executeUpdate("commit");	    		
	    	}	    		
	    	System.out.println(n + " info DELETED!");	
	    }
	     
	    public void insert(String table, Vector columns, Vector values){
	    	if (print) System.out.println("\n * in insert");
	    	
	    	try{	    		
	    		String insert = "INSERT INTO " + table + "(";
	    		for (int i=0; i<columns.size(); i++){
	    			insert += (String) columns.get(i) + ",";
	    		}
	    		insert = insert.substring(0, insert.length()-1) + ") ";

				insert += "VALUES (";
				for (int i=0; i<values.size(); i++){
	    			insert += (String) values.get(i) + ",";	    			
	    		}
				insert = insert.substring(0, insert.length()-1) + ") ";
				//System.out.println(insert);
				executeUpdate(insert);
	    		if (print) System.out.println("Insert successful: " + insert);
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationalDB.insert!");
	    		e.printStackTrace();
	    		System.exit(1);
	    	}
	    }	    

	    public void insert(String table, String columns, Vector values){
	    	if (print) System.out.println("\n * in insert");
	    	
	    	try{	    		
	    		String insert = "INSERT INTO " + table + columns + " ";

				insert += "VALUES (";
				for (int i=0; i<values.size(); i++){
	    			insert += (String) values.get(i) + ",";	    			
	    		}
				insert = insert.substring(0, insert.length()-1) + ") ";
				//System.out.println(insert);
				executeUpdate(insert);
	    		if (print) System.out.println("Insert successful: " + insert);
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationalDB.insert!");
	    		e.printStackTrace();
	    		System.exit(1);
	    	}
	    }	    
	    
	    public boolean executeUpdate(String sql){
	    	boolean toReturn = true;
	    	//System.out.println(" START: in execute update for sql: " + sql);
	    	try{
		    	if (conn == null) initDBConn();		    	
		    	stmt = conn.createStatement();		    	
		    	stmt.executeUpdate(sql);
		    	finalizeStatement();
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationalDB.executeUpdate!");
	    		System.out.println("\n[SQL] " + sql);
	    		finalizeStatement();
	    		toReturn = false;
	    		if (sql.indexOf("drop table") == -1){	    			
	    			e.printStackTrace();
	    		}
	    		else{
	    			System.out.println("Table does not exist!");
	    		}
	    	}
	    	//System.out.println(" END: in execute update for sql: " + sql);
	    	return toReturn;
	    }

	    public void setNoChildren(String neuron){
	    	System.out.println("Setting no children, please wait...");
	    	// this is sql to get nochildren
	    	// select t1.fromsegment, count(t1.tosegment) 
	        // from nl_tree t1 where upper(neuron)='ALXP.TXT' 
	    	// group by t1.fromsegment order by t1.fromsegment;
	    	boolean toReturn = true;
	    	String sql = new String("update nl_nodes set nochildren  =  ");
	    	sql += "(select count(t1.tosegment) from nl_tree t1 ";
	    	sql +=	"where upper(neuron)='"+neuron.toUpperCase()+"' and t1.fromsegment = segmentid)";
	    	System.out.println("SQL << " + sql + " >>");
	    	executeUpdate(sql);	    	
	    }

	    
	    public void finalizeStatement(){
	    	try {
	    		if (stmt != null)
	    			stmt.close();
	    		if (rs != null)
	    			rs.close();	    					
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	    
	    
	    public ResultSet execute (String sql){
	    	if (print) System.out.println("\n * execute");
	    	try{	    			    		
	    		if (print) System.out.println("[Executing] " + sql);	    	
	    		if (conn == null)initDBConn();
	    		if (conn == null)
	    			NeurolucidaInterface.s_.append("DB CONN IS NULL!<hr>");
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);
	    	}
	    	catch(Exception e){
	    		NeurolucidaInterface.s_.append("Exception in execute()!<hr>");
	    		System.out.println("Exception at RelationalDB.execute!");
	    		e.printStackTrace();	    		
	    	}
	    	return rs;
	    }
	    	    
	    public HashMap getNodesAtLevel(String neuron, int l, String returnCol, String levelCond){
	    	
	    	if ((l < -1) || (neuron == null) || (levelCond == null)){
	    		System.out.println("Exception at getNodesAtLevel: neuron or level is invalid!");
	    		return null;
	    	}
	    	neuron = neuron.trim().toUpperCase();
	    	int neuronCnt = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"'");
	    	if (neuronCnt <=0 ){
	    		System.out.println("Exception at getNodesAtLevel: No neurons with name " + neuron + " found in the database!");
	    		return null;
	    	}
	    	if (!(levelCond.equals("=") || levelCond.equals(">") || levelCond.equals("<") || 
	    		  levelCond.equals(">=") || levelCond.equals("<=")))
	    		return null;
	    	String cond = " upper(neuron)='" + neuron + "' AND ( (level_" + levelCond + l + ") AND (level_>=0) ) ";
	    	return getNodes(cond, returnCol);
	    }
	    
	    public HashMap getNodes(String cond, String returnCol){
	    	HashMap nodes = new HashMap();
	    	String sql = "SELECT segmentid";
	    	boolean newCol = false;
	    	if (returnCol != null){
	    		newCol = true;
	    		sql += ", " + returnCol;
	    	}
	    	sql += " FROM nl_nodes ";
	    	if ((cond!=null) && (cond.length()>0))
	    		sql += "WHERE " + cond;
	    	System.out.println("SQL: " + sql);
	    	execute(sql);
	    	try{
	    		if (rs != null){
	    			Integer segmentid = null;
	    			while (rs.next()){
	    				segmentid = new Integer(rs.getInt(1));						
	    				if (newCol)
	    					nodes.put(segmentid, rs.getObject(2));
	    				else
	    					nodes.put(segmentid, null);
	    			}
	    		}
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    		System.out.println("Exception at getNodes");
	    		return null;
	    	}
	    	finally{
	    		finalizeStatement();
	    	}
	    	return nodes;
	    }

	    public Vector getXthOrderDiameterCompute(String neuron, int level, String levelCond ){
	    	Vector toReturn = new Vector();
	    	if (neuron == null) 
	    		return null;
	    	neuron = neuron.trim().toUpperCase();
	    	HashMap nodes = getNodesAtLevel(neuron, level, null, levelCond);
	    	if ((nodes == null) || (nodes.size() == 0)){
	    		System.out.println("Null nodes!");
	    		return null;	    	
	    	}
	    	String sql = "SELECT avg(radius) FROM nl_segments WHERE upper(neuron) = '" + neuron + "' AND segmentid in (";
	    	Set s = nodes.keySet();
	    	Iterator it = s.iterator();	    	
	    	while(it.hasNext()){
	    		sql += it.next() + ",";	    		
	    	}
	    	sql = sql.substring(0, sql.length()-1) + ") group by segmentid order by avg(radius)";
	    	execute(sql);
	    	try{
	    		if (rs != null){
	    			while (rs.next()){
	    				toReturn.add(new Float(2*rs.getFloat(1)));
	    			}
	    		}
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderDiameter");
	    		e.printStackTrace();
	    		return null;
	    	}
	    	finally{
	    		finalizeStatement();
	    	}
	    	return toReturn;
	    }
	    public Vector getXthOrderDiameter(String neuron, int level, String levelCond ){
	    	Vector toReturn = new Vector();
	    	if (neuron == null) 
	    		return null;
	    	neuron = neuron.trim().toUpperCase();
	    	int neuronCnt = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"'");
	    	if (neuronCnt <=0 ){
	    		System.out.println("Exception at getXthOrderDiameter: No neurons with name " + neuron + " found in the database!");
	    		return null;
	    	}
	    	
	    	String sql = "SELECT radius FROM nl_nodes WHERE upper(neuron) = '" + neuron + "' AND level_"+levelCond+""+level;
	    	sql += " order by radius";
	    	execute(sql);
	    	try{
	    		if (rs != null){
	    			while (rs.next()){
	    				toReturn.add(new Float(2*rs.getFloat(1)));
	    			}
	    		}
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderDiameter");
	    		e.printStackTrace();
	    		return null;
	    	}
	    	finally{
	    		finalizeStatement();
	    	}
	    	return toReturn;
	    }

	    public Vector getXthOrderLength(String neuron, int level, String levelCond ){
	    	Vector toReturn = new Vector();
	    	if (neuron == null) 
	    		return null;
	    	neuron = neuron.trim().toUpperCase();
	    	int neuronCnt = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"'");
	    	if (neuronCnt <=0 ){
	    		System.out.println("Exception at getXthOrderLength: No neurons with name " + neuron + " found in the database!");
	    		return null;
	    	}
	    	
	    	String sql = "SELECT length FROM nl_nodes WHERE upper(neuron) = '" + neuron + "' AND level_"+levelCond+""+level;
	    	sql += " order by length";
	    	execute(sql);
	    	try{
	    		if (rs != null){
	    			while (rs.next()){
	    				toReturn.add(new Float(rs.getFloat(1)));
	    			}
	    		}
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderLength");
	    		e.printStackTrace();
	    		return null;
	    	}
	    	finally{
	    		finalizeStatement();
	    	}
	    	return toReturn;
	    }
	    public Vector getXthOrderSpines(String neuron, int level, String levelCond ){
	    	Vector toReturn = new Vector();
	    	if (neuron == null) 
	    		return null;
	    	neuron = neuron.trim().toUpperCase();
	    	int neuronCnt = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"'");
	    	if (neuronCnt <=0 ){
	    		System.out.println("Exception at getXthOrderSpines: No neurons with name " + neuron + " found in the database!");
	    		return null;
	    	}
	    	
	    	String sql = "SELECT spineCount FROM nl_nodes WHERE upper(neuron) = '" + neuron + "' AND level_"+levelCond+""+level;
	    	sql += " order by spineCount";
	    	execute(sql);
	    	try{
	    		if (rs != null){
	    			while (rs.next()){
	    				toReturn.add(new Float(rs.getFloat(1)));
	    			}
	    		}
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderSpines");
	    		e.printStackTrace();
	    		return null;
	    	}
	    	finally{
	    		finalizeStatement();
	    	}
	    	return toReturn;
	    }
	    public Vector getXthOrderSpineDensity(String neuron, int level, String levelCond ){
	    	Vector toReturn = new Vector();
	    	if (neuron == null) 
	    		return null;
	    	neuron = neuron.trim().toUpperCase();
	    	int neuronCnt = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"'");
	    	if (neuronCnt <=0 ){
	    		System.out.println("Exception at getXthOrderSpineDensity: No neurons with name " + neuron + " found in the database!");
	    		return null;
	    	}
	    	
	    	String sql = "SELECT spineCount/length FROM nl_nodes WHERE upper(neuron) = '" + neuron + "' AND level_"+levelCond+""+level;
	    	sql += " order by spineCount/length";
	    	execute(sql);
	    	try{
	    		if (rs != null){
	    			while (rs.next()){
	    				toReturn.add(new Float(rs.getFloat(1)));
	    			}
	    		}
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderSpineDensity");
	    		e.printStackTrace();
	    		return null;
	    	}
	    	finally{
	    		finalizeStatement();
	    	}
	    	return toReturn;
	    }
	    
	    public float getXthOrderAverageDiameterCompute(String neuron, int level, String levelCond){
	    	Vector v = getXthOrderDiameterCompute(neuron, level, levelCond);
	    	float average = (float) 0.0;
	    	if (v != null){
	    		int size = v.size();
	    		float total = (float) 0.0;	
	    		for (int i=0; i<size; i++){
	    			total += ((Float) v.get(i)).floatValue();
	    		}
	    		//System.out.println("\nAverage " + levelCond + "" + level + ".th order diameter is: " + total/size);
	    		average = total/size;
	    	}
	    	else{
	    		System.out.println(" It is NULL");
	    	}
	    	return average;
	    }
	    public float getXthOrderAverageDiameter(String neuron, int level, String levelCond){
	    	Vector v = getXthOrderDiameter(neuron, level, levelCond);
	    	float average = (float) 0.0;
	    	if (v != null){
	    		int size = v.size();
	    		float total = (float) 0.0;	
	    		for (int i=0; i<size; i++){
	    			total += ((Float) v.get(i)).floatValue();
	    		}
	    		//System.out.println("\nAverage " + levelCond + "" + level + ".th order diameter is: " + total/size);
	    		average = total/size;
	    	}
	    	else{
	    		System.out.println(" It is NULL");
	    	}
	    	return average;
	    }
	    
	    public float getXthOrderAreaCompute(String neuron, int level, String levelCond ){
	    	double area = -1.;
	    	if (neuron == null) 
	    		return -1;
	    	neuron = neuron.trim().toUpperCase();
	    	HashMap nodes = getNodesAtLevel(neuron, level, null, levelCond);
	    	if ((nodes == null) || (nodes.size() == 0)){
	    		System.out.println("Null nodes!");
	    		return -1;	    	
	    	}
	    	
	    	String sqlBase = "SELECT x, y, z, radius from nl_segments where upper(neuron)='" + neuron + "' and segmentid = ? and sequenceid =";
	    	sqlBase += "(select min(sequenceid) from nl_segments where upper(neuron)='" + neuron + "' and segmentid = ?)";
	    	String sql = null;
	    	Set s = nodes.keySet();
	    	Iterator it = s.iterator();
	    	Vector v = new Vector();
	    	while(it.hasNext())
	    		v.add((Integer) it.next());
	    	Collections.sort(v);
	    	
	    	try{
	    		area = 0;
	    		float xstart=0, ystart=0, zstart=0, xend=0, yend=0, zend=0, radius=0;
	    		int i=0;
	    		Integer itNext = null;
	    		int vSize = v.size();
	    		for (int j=0; j<vSize; j++){
	    			sql = new String(sqlBase);
	    			itNext = (Integer) v.get(i);
	    			//System.out.println("A.SQL IS <" + sql + ">");
	    			sql = sql.replaceAll(" [?]", ""+itNext);	   
	    			//System.out.println("B.SQL IS <" + sql + ">");
	    			execute(sql);
	    			if (rs != null && rs.next()){
	    				xstart = rs.getFloat(1);
	    				ystart = rs.getFloat(2);
	    				zstart = rs.getFloat(3);
	    				radius = rs.getFloat(4);
	    			}
	    			finalizeStatement();	  
	    			
	    			//sql = sql.replace("min", "max");
	    			int ind = sql.indexOf("min");
	    			String temp = sql.substring(0, ind);
	    			sql = temp + "max" + sql.substring(ind+3);
	    			execute(sql);
	    			if (rs != null && rs.next()){
	    				xend = rs.getFloat(1);
	    				yend = rs.getFloat(2);
	    				zend = rs.getFloat(3);	    				
	    			}
	    			finalizeStatement();
	    			//System.out.println(">" + xstart + " " + xend);
	    			//System.out.println(">" + ystart + " " + yend);
	    			//System.out.println(">" + zstart + " " + zend);
	    			float length = (float) Math.sqrt( Math.pow((xend-xstart),2) + Math.pow((yend-ystart),2) + Math.pow((zend-zstart),2));	    			
	    			//System.out.println("Length " + circum);	    			
	    			area += length*2*PI*radius;
	    			System.out.println((++i) + "\t: " + itNext + "\t   AREA: " + area);
	    			if (i%2==0) System.out.print(" ");
	    			
	    		}
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderDiameter");
	    		System.out.println("SQL: " + sql);
	    		e.printStackTrace();
	    		finalizeStatement();	    		
	    	}	    	
	    	return (float) area;
	    }
	    public float getXthOrderArea(String neuron, int level, String levelCond ){
	    	double area = -1.;
	    	if (neuron == null) 
	    		return -1;
	    	neuron = neuron.trim().toUpperCase();
	    	int neuronCnt = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"'");
	    	if (neuronCnt <=0 ){
	    		System.out.println("Exception at getXthOrderArea: No neurons with name " + neuron + " found in the database!");
	    		return -1.0f;
	    	}
	    	
	    	String sql = "SELECT length, radius, segmentid FROM nl_nodes where upper(neuron)='" + neuron + "' ";
	    	sql += " AND level_" + levelCond + "" + level + " order by segmentid"; 
	    	try{
	    		area = 0;
	    		float length, radius=0;
    			execute(sql);
	    		if (rs != null){
	    			int i=0;
	    			while(rs.next()){
	    				length = rs.getFloat(1);
	    				radius = rs.getFloat(2);
	    				area += length*2*PI*radius;
	    				System.out.println((++i) + "\t: " + rs.getInt(3) + "\t   area: " + area);
		    			if (i%2==0) System.out.print(" ");
	    			}
	    		}
	    		else
	    			System.out.println("rs is null!");
	    		finalizeStatement();
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderDiameter");
	    		System.out.println("SQL: " + sql);
	    		e.printStackTrace();
	    		finalizeStatement();	    		
	    	}	    	
	    	return (float) area;
	    }

	    public float getXthOrderVolume(String neuron, int level, String levelCond ){
	    	double volume = -1.;
	    	if (neuron == null) 
	    		return -1.0f;
	    	neuron = neuron.trim().toUpperCase();
	    	int neuronCnt = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"'");
	    	if (neuronCnt <=0 ){
	    		System.out.println("Exception at getXthOrderVolume: No neurons with name " + neuron + " found in the database!");
	    		return -1.0f;
	    	}
	    		    	
	    	String sql = "SELECT length, radius, segmentid FROM nl_nodes where upper(neuron)='" + neuron + "' ";
	    	sql += " AND level_" + levelCond + "" + level + " order by segmentid"; 
	    	
	    	try{
	    		volume = 0;
	    		float length, radius=0;
    			execute(sql);
	    		if (rs != null){
	    			int i=0;
	    			while(rs.next()){
	    				length = rs.getFloat(1);
	    				radius = rs.getFloat(2);
	    				volume += length*PI*radius*radius;
	    				System.out.println((++i) + "\t: " + rs.getInt(3) + "\t   area: " + volume);
		    			if (i%2==0) System.out.print(" ");
	    			}
	    		}
	    		finalizeStatement();
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderVolume");
	    		System.out.println("SQL: " + sql);
	    		e.printStackTrace();
	    		finalizeStatement();	    		
	    	}	    	
	    	return (float) volume;
	    }

	    public Vector getXthOrderFanoutCompute(String neuron, int level, String levelCond){
	    	Vector toReturn = new Vector();
	    	if (neuron == null) 
	    		return null;
	    	neuron = neuron.trim().toUpperCase();
	    	HashMap nodes = getNodesAtLevel(neuron, level, null, levelCond);
	    	if ((nodes == null) || (nodes.size() == 0)){
	    		System.out.println("Null nodes!");
	    		return null;	    	
	    	}	    	
	    	String sql = "SELECT count(*) FROM nl_tree WHERE upper(neuron) = '" + neuron + "' AND fromsegment in (";	    	
	    	Set s = nodes.keySet();
	    	Iterator it = s.iterator();	    	
	    	while(it.hasNext()){
	    		sql += it.next() + ",";	    		
	    	}
	    	sql = sql.substring(0, sql.length()-1) + ") group by fromsegment order by count(*)";
	    	System.out.println("SQL::: " + sql);
	    	execute(sql);
	    	int counter = 0;
	    	try{
	    		if (rs != null){
	    			while (rs.next()){
	    				counter++;
	    				toReturn.add(new Integer(rs.getInt(1)));
	    			}
	    		}
	    		int addnew = nodes.size()-counter;
	    		for (int i=0; i<addnew; i++){
	    			toReturn.add(0, new Integer(0));
	    		}
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderDiameter");
	    		e.printStackTrace();
	    		return null;
	    	}
	    	finally{
	    		finalizeStatement();
	    	}
	    	return toReturn;
	    }
	    public Vector getXthOrderFanout(String neuron, int level, String levelCond ){
	    	Vector toReturn = new Vector();
	    	if (neuron == null) 
	    		return null;
	    	neuron = neuron.trim().toUpperCase();
	    	int neuronCnt = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"'");
	    	if (neuronCnt <=0 ){
	    		System.out.println("Exception at getXthOrderFanout: No neurons with name " + neuron + " found in the database!");
	    		return null;
	    	}
	    	
	    	String sql = "SELECT noChildren FROM nl_nodes WHERE upper(neuron) = '" + neuron + "' AND level_"+levelCond+""+level;
	    	sql += " order by noChildren";
	    	//System.out.println("SQL:: " + sql);
	    	execute(sql);
	    	try{
	    		if (rs != null){
	    			while (rs.next()){
	    				toReturn.add(new Integer(rs.getInt(1)));
	    			}
	    		}
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderFanout");
	    		e.printStackTrace();
	    		return null;
	    	}
	    	finally{
	    		finalizeStatement();
	    	}
	    	return toReturn;
	    }

	    public String getInfo(String neuron){
	    	StringBuffer res = new StringBuffer();
	    	res.append("<br><p><b>- METADATA FOR NEURON: ");
	    	if (neuron == null){
	    		res.append("NULL \n");
	    		return res.toString();
	    	}
	    	neuron = neuron.trim().toUpperCase();
	    	res.append("<font color=\"#ff4444\">" + neuron + "</font></b>\n");
	    	int neuronCnt = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"'");
	    	if (neuronCnt <=0 ){
	    		System.out.println("Exception at getXthOrderFanout: No neurons with name " + neuron + " found in the database!");
	    		res.append("Exception: No neurons with name " + neuron + " found in the database!\n");
	    		return res.toString();
	    	}
	    	int dendrites = getNoOfDendrites(neuron);
	    	res.append("<li>  It has " + dendrites + " dendrite(s) and ");
	    	res.append("total of " + neuronCnt + " neuronal arms.<br>\n");
	    	int md = getMax("level_", "nl_nodes", "upper(neuron)='"+neuron+"'");
			res.append("<li>  Max depth is " + md + " for the deepest dendrite.\n<br>");
	    	int cellbody = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"' AND segmentid=-2");
	    	int axon = getCount("*", "nl_nodes", "upper(neuron)='"+neuron+"' AND segmentid=-1");
	    	res.append("<li>  It has " + cellbody + " cellbody and " + axon + " axon in the database.\n<br>");
	    	
	    	return res.toString();
	    }
	    
	    public int getNoOfDendrites(String n){
	    	String sql = "select max(segmentid/10000) from nl_nodes where " + "upper(neuron)='" + n + "'";
	    	int dendrites = 0;	    		    	
	    	try{
	    		execute(sql);
	    		if (rs != null && rs.next())
	    			dendrites = (int) rs.getFloat(1);
	    		finalizeStatement();	    		
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getNoOfDendrites");
	    		e.printStackTrace();
	    	}
	    	return dendrites;
	    
	    }
	    public boolean validateNeuron(String n){
	    	if (n == null){
	    		System.out.println("Exception at validateNeuron: Neuron name is null!");
	    		return false;
	    	}
	    	n = n.toUpperCase();
	    	int neuronCnt = getCount("*", "nl_nodes", "upper(neuron)='"+n+"'");
	    	if (neuronCnt <=0 ){
	    		System.out.println("Exception at getXthOrderFanout: No neurons with name " + n + " found in the database!");
	    		return false;
	    	}
	    	return true;
	    }
	    public Vector getXthOrderWhat(String neuron, String what, int level, String levelCond ){
	    	Vector toReturn = new Vector();
	    	if (!validateNeuron(neuron))
	    		return null;
	    	neuron = neuron.toUpperCase();
	    	if (what == null){
	    		System.out.println("Exception at getXthOrderWhat: Null operation!");
	    		NeurolucidaInterface.s_.append("Exception at getXthOrderWhat: Null operation!");
	    		return null;
	    	}
	    	what = what.trim().toUpperCase();
	    	if (what.equals("NOCHILDREN") || what.equals("2*RADIUS") || what.equals("LENGTH") || 
	    		what.equals("SPINECOUNT") || what.equals("SPINECOUNT/LENGTH")){ // do nothing
	    	}
	    	else if (what.equals("FANOUT"))
	    		what = "noChildren";	    			    	
	    	else if (what.equals("DIAMETER"))
	    		what = "2*radius";
	    	else if (what.equals("LENGTH"))
	    		what = "length";
	    	else if (what.equals("SPINES"))
	    		what = "spineCount";	    		    	
	    	else if (what.equals("SPINEDENSITY"))
	    		what = "spineCount/length";
	    	else{
	    		System.out.println("\nOperation \"" + what + "\" could not be identified!\n");
	    		NeurolucidaInterface.s_.append("\nOperation \"" + what + "\" could not be identified!\n");
	    		return null;
	    	}
	    	
	    	String cond = "upper(neuron)='" + neuron + "'";
	    	String sql = new String();
	    	int dendrites = getNoOfDendrites(neuron);
	    	System.out.println("# of dendrites for " + neuron + " = " + dendrites);
	    	int min = 0, max=0;;
	    	Vector aDendrite = null;
	    	for (;dendrites>0;dendrites--){
	    		aDendrite = new Vector();
	    		min = INCREMENT*dendrites;
	    		max = INCREMENT*(dendrites+1);
	    		sql = "SELECT " + what + " FROM nl_nodes WHERE " + cond + " AND level_"+levelCond+""+level;
	    		sql += " AND ( (segmentid >= " + min + ") AND (segmentid < " + max + ") ) ";
	    		sql += " order by " + what;
	    		
	    		System.out.println("SQL:: " + sql);
	    		execute(sql);
	    		try{
	    			if (rs != null){
	    				while (rs.next()){
	    					aDendrite.add(new Float(rs.getFloat(1)));
	    				}
	    				toReturn.add(aDendrite);
	    			}
	    		}
	    		catch(Exception e){
	    			System.out.println("Exception at RelationDB get getXthOrderFanout");
	    			e.printStackTrace();
	    			NeurolucidaInterface.s_.append("Exception at RelationDB get getXthOrderFanout: " + e);
	    			return null;
	    		}
	    		finally{
	    			finalizeStatement();
	    		}
	    	}
	    	return toReturn;
	    }	    
	    public float getXthOrderAverageFanoutCompute(String n, int level, String levelCond){
	    	Vector v = getXthOrderFanoutCompute(n, level, levelCond);
	    	if ((v!=null) || (v.size()>0)){
	    		int size = v.size();
	    		float total = 0;
	    		for (int i=0; i<size; i++){
	    			total += ((Integer) v.get(i)).intValue();
	    		}
	    		total /= size;
	    		return total;
	    	}
	    	return -1;
	    }
	    public float getXthOrderAverageFanout(String n, int level, String levelCond){
	    	Vector v = getXthOrderFanout(n, level, levelCond);
	    	if ((v!=null) || (v.size()>0)){
	    		int size = v.size();
	    		float total = 0;
	    		for (int i=0; i<size; i++){
	    			total += ((Integer) v.get(i)).intValue();
	    		}
	    		total /= size;
	    		return total;
	    	}
	    	return -1;
	    }	    
	    
	    public int getMax(String field, String table, String cond){
	    	if (conn == null) { System.out.println("Connection is null"); System.exit(1); }
			String sql = new String();
			int toReturn = -1;
	    	try{
	    		sql = "SELECT max(" + field + ") FROM " + table + " WHERE " + cond;
	    		//if (print) 
	    			System.out.println("[Executing] " + sql);	    		
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);

	    		if (rs.next()){
	    			//System.out.println(rs.getFloat(1));
	    			toReturn = rs.getInt(1);
	    		}
	    	    stmt.close();
	    	    rs.close();    	
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception: SQL <<<" + sql + ">>> cannot be run: ");
	    		e.printStackTrace();
	    	}
	    	return toReturn;
	    }
	    
	    public int getMax(String field, String table){
	    	if (conn == null) { System.out.println("Connection is null"); System.exit(1); }
			String sql = new String();
			int toReturn = 0;
	    	try{
	    		sql = "SELECT max(" + field + ") FROM " + table;
	    		if (print) System.out.println("[Executing] " + sql);	    		
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);

	    		if (rs.next()){
	    			//System.out.println(rs.getFloat(1));
	    			toReturn = rs.getInt(1);
	    		}
	    	    stmt.close();
	    	    rs.close();    	
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception: SQL <<<" + sql + ">>> cannot be run: ");
	    		e.printStackTrace();
	    	}
	    	return toReturn;
	    }
	    
	    public int getCount(String field, String table, String cond){
	    	if (conn == null) { System.out.println("Connection is null"); System.exit(1); }
			String sql = new String();
			int toReturn = 0;
	    	try{
	    		sql = "SELECT count(" + field + ") FROM " + table;
	    		if (cond!=null && cond.length()>0) 
	    			sql+= " WHERE " + cond;
	    		if (print) System.out.println("[Executing] " + sql);	    		
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);

	    		if (rs.next()){
	    			//System.out.println(rs.getFloat(1));
	    			toReturn = rs.getInt(1);
	    		}
	    	    stmt.close();
	    	    rs.close();    	
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception: SQL <<<" + sql + ">>> cannot be run: ");
	    		e.printStackTrace();
	    	}
	    	return toReturn;
	    }
	    
	    
	    public Vector getChildren(String n, int f){
	    	Vector c = new Vector();
	    	String sql = "SELECT toSegment FROM nl_tree WHERE fromsegment= "+ f + " AND upper(neuron)='"+n+"'";	    	
	    	try{
	    		execute(sql);
	    		if (rs != null){
	    			while (rs.next()){
	    				c.add(new Float(rs.getInt(1)));
	    			}
	    		}
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationDB get getXthOrderDiameter");
	    		e.printStackTrace();
	    		return null;
	    	}
	    	finally{
	    		finalizeStatement();
	    	}
	    	return c;	    	
	    }
	    
        public double[] getTtest(int i){
        	// stores the t-table for 0:df=0.05 and 1:df=0.10
        	double[][] t = {{0.0,  6.31, 2.9200, 2.3534, 2.1318, 2.0150, 1.9432, 1.8946, 1.8595, 1.8331, 1.8125,
        	1.7959, 1.7823, 1.7709, 1.7613, 1.7531, 1.7459, 1.7396, 1.7341, 1.7291, 1.7247, 1.7207, 1.7171,
			1.7139, 1.7109, 1.7081, 1.7056, 1.7033, 1.7011, 1.6991, 1.6973, 1.6955, 1.6939, 1.6924, 1.6909,
			1.6896, 1.6883, 1.6871, 1.6860, 1.6849, 1.6839, 1.6829, 1.6820, 1.6811, 1.6802, 1.6794, 1.6787,
			1.6779, 1.6772, 1.6766, 1.6759, 1.6753, 1.6747, 1.6741, 1.6736, 1.6730, 1.6725, 1.6720, 1.6716,
			1.6711, 1.6706},
						    {0.0, 12.71, 4.3027, 3.1824, 2.7765, 2.5706, 2.4469, 2.3646, 2.3060, 2.2622, 2.2281, 
			2.2010, 2.1788, 2.1604, 2.1448, 2.1315, 2.1199, 2.1098, 2.1009, 2.0930, 2.0860, 2.0796, 2.0739, 
			2.0687, 2.0639, 2.0595, 2.0555, 2.0518, 2.0484, 2.0452, 2.0423, 2.0395, 2.0369, 2.0345, 2.0322,
			2.0301, 2.0281, 2.0262, 2.0244, 2.0227, 2.0211, 2.0195, 2.0181, 2.0167, 2.0154, 2.0141, 2.0129,
			2.0117, 2.0106, 2.0096, 2.0086, 2.0076, 2.0066, 2.0057, 2.0049, 2.0040, 2.0032, 2.0025, 2.0017, 
			2.0010,	2.0003},
							{0.0, 63.60, 9.9250, 5.8408, 4.6041, 4.0321, 3.7074, 3.4995, 3.3554, 3.2498, 3.1693,
			3.1058, 3.0545, 3.0123, 2.9768, 2.9467, 2.9208, 2.8982, 2.8784, 2.8609, 2.8453, 2.8314, 2.8188, 
			2.8073, 2.7970, 2.7874, 2.7787, 2.7707, 2.7633, 2.7564, 2.7500, 2.7440, 2.7385, 2.7333, 2.7284, 
			2.7238, 2.7195,	2.7154, 2.7116, 2.7079, 2.7045, 2.7012,	2.6981, 2.6951, 2.6923, 2.6896, 2.6870,
			2.6846, 2.6822, 2.6800, 2.6778, 2.6757, 2.6737, 2.6718, 2.6700, 2.6682, 2.6665, 2.6649, 2.6633,
			2.6618, 2.6603}	
			};        	
        	double[] tr = new double[3];
        	if (i<=60 && i>=1){
        		tr[0] = t[0][i]; // 90% confidence
        		tr[1] = t[1][i]; // 95% confidence
        		tr[2] = t[2][i]; // 99% confidence
        	}
        	return tr;
        }	    
	    /*
	    public int getAggregate(String aggregate, String field, String table, String where){
	    	if (conn == null) { System.out.println("Connection is null"); System.exit(1); }
	    	String aLow = aggregate.toLowerCase();
	    	if (!aLow.equals("count") && !aLow.equals("max") && !aLow.equals("min")){
	    		System.out.println("Error in relationalDB.getAggregate");
	    		System.out.println("AGGREGATE " + aggregate + " is not valid!");
	    		System.exit(1);
	    	}
			String sql = new String();
			int toReturn = 0;
	    	try{
	    		sql = "SELECT " + aggregate + "(" + field + ") FROM " + table;	    		
	    		if (where != null && where.trim().length()>0)
	    			sql += " where " + where;
	    		if (print) System.out.println("[Executing] " + sql);	    		
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);

	    		if (rs.next()){
	    			//System.out.println(rs.getFloat(1));
	    			toReturn = rs.getInt(1);
	    		}
	    	    stmt.close();
	    	    rs.close();    	
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception: SQL <<<" + sql + ">>> cannot be run: ");
	    		e.printStackTrace();
	    	}
	    	return toReturn;
	    }	    
	    public int[] getIntArray(String field, String table, String where){
	    	if (conn == null) { System.out.println("Connection is null"); System.exit(1); }
			String sql = new String();
			int toReturn[] = null;
			int size = 0;
	    	try{
	    		sql = "SELECT " + "count(" + field + ") FROM " + table + " where " + where;
	    		if (print) System.out.println("[Executing] " + sql);	    		
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);
	    		if (rs.next()){	    			
	    			size = rs.getInt(1);
	    		}
	    		stmt.close();
	    		rs.close();
	    		if (size == 0) return null;
	    		toReturn = new int[size];
	    		sql = "SELECT " + field + " FROM " + table + " where " + where;
	    		if (print) System.out.println("[Executing] " + sql);	    		
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);
	    		int counter = 0;
	    		while (rs.next()){	    			
	    			toReturn[counter++] = rs.getInt(1);
	    		}
	    			    		
	    	    stmt.close();
	    	    rs.close();    	
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception: SQL <<<" + sql + ">>> cannot be run: ");
	    		e.printStackTrace();
	    	}
	    	return toReturn;
	    }
	    
	    public boolean tupleExists(String table, Vector columns, Vector values){
	    	boolean toReturn = false;
	    	if (print) System.out.println("\n * in tuple exits");
	    	try{	    		
	    		if (columns.size() != values.size()) return toReturn;
	    		String sql = "SELECT count(*) from " + table + " where ";
	    		int columnSize = columns.size();
	    		for (int i=0; i<columnSize; i++){
	    			sql += " (" + (String) columns.get(i) + " = " + (String) values.get(i) + ") ";
	    			if ((i+1)<columnSize) 
	    				sql += " AND ";
	    		}
	    		if (print) System.out.println("[Executing] " + sql);	    		
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);

	    		if (rs.next()){
	    			//System.out.println(rs.getFloat(1));
	    			if (rs.getInt(1)>0) 
	    				toReturn = true;
	    		}
	    	    stmt.close();
	    	    rs.close();    	
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationalDB.insert!");
	    		e.printStackTrace();
	    	}
	    	return toReturn;
	    }	    
	    
	    public boolean tupleExists(String table, String where){
	    	boolean toReturn = false;
	    	if (print) System.out.println("\n * in tuple exits");
	    	try{	    		
	    		String sql = "SELECT count(*) from " + table + " where " + where;
	    		if (print) System.out.println("[Executing] " + sql);	    		
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);
	    		if (rs.next()){
	    			if (rs.getInt(1)>0) 
	    				toReturn = true;
	    		}
	    	    stmt.close();
	    	    rs.close();    	
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationalDB.insert!");
	    		e.printStackTrace();	    		
	    	}
	    	return toReturn;
	    }	    
	    
	    public int getIntField(String table, String column, String where){
	    	int toReturn = -1;
	    	if (print) System.out.println("\n * in getIntField");
	    	try{	    		
	    		String sql = "SELECT " + column + " from " + table + " where " + where;
	    		if (print) System.out.println("[Executing] " + sql);	    		
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);
	    		if (rs.next()){
	    			toReturn = rs.getInt(1);
	    		}
	    	    stmt.close();
	    	    rs.close();    	
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationalDB.getIntField!");
	    		e.printStackTrace();
	    	}
	    	return toReturn;
	    }	    
	    
	    public void addToEdge(int ds, int from, int rel, int to){
	    	int id = getMax("id", "Edges");	    	
	    	Vector fields = new Vector();
	    	fields.add("id"); fields.add("ds"); fields.add("idsrc"); fields.add("iddst"); 
	    	Vector values = new Vector();	    	
	    	values.add(""+(++id)); values.add(""+ds); values.add(""+from); values.add(""+rel);
	    	insert("Edges", fields, values);
	    	values = new Vector();	    	
	    	values.add(""+(++id)); values.add(""+ds); values.add(""+rel); values.add(""+to);
	    	insert("Edges", fields, values);	    	
	    }
	    

	    	    
	    public void update(String table, String column, String value, String where){
	    	if (print) System.out.println("\n * in update");
	    	try{	    		
	    		String update = "UPDATE " + table + " SET " + column + "=" + value + " WHERE " + where;
				executeUpdate(update);
	    		if (print) System.out.println(update);
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationalDB.update!");
	    		e.printStackTrace();
	    	}
	    }	    
	    
	    public void delete(String table, String where){
	    	if (print) System.out.println("\n * in delete");
	    	try{	    		
	    		String update = "DELETE FROM " + table + " WHERE " + where;
				executeUpdate(update);
				executeUpdate("commit");
	    		if (print) System.out.println(update);
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationalDB.delete!");
	    		e.printStackTrace();
	    	}
	    }	    
	    


	    public ResultSet execute (String sql){
	    	if (print) System.out.println("\n * execute");
	    	try{	    			    		
	    		if (print) System.out.println("[Executing] " + sql);	    		
	    		stmt = conn.createStatement();
	    		rs = stmt.executeQuery(sql);
	    	}
	    	catch(Exception e){
	    		System.out.println("Exception at RelationalDB.insert!");
	    		e.printStackTrace();	    		
	    	}
	    	return rs;
	    }	

	    public int[] getRoots(int ds){
			Vector v = new Vector();
			int toReturn[] = null;
			String rootS = new String();
			if (ds != -1){
				rootS = "select distinct idsrc from edges where ds=" + ds;
				rootS += " and idsrc not in (select iddst from edges where ds=" + ds + ")";
			}
			else{
				return null;
			}
			rootS += " order by idsrc";
			try{
				stmt = conn.createStatement();
				rs = stmt.executeQuery(rootS);
				while(rs.next()){
					v.add(new Integer(rs.getInt(1)));
				}
				finalizeStatement();
			}
			catch(Exception e){
	    		System.out.println("Exception at RelationalDB.insert!");
	    		e.printStackTrace();	    		
			}
			int vSize = v.size();
			if (vSize == 0) return null;
			toReturn = new int[vSize];
			for (int i=0; i<vSize; i++){
				toReturn[i] = ((Integer) v.get(i)).intValue();
			}
			return toReturn;
	    }	    
	    
	    public int[] getLeaves(int ds){
			Vector v = new Vector();
			int toReturn[] = null;
			String rootS = new String();
			if (ds != -1){
				rootS = "select distinct iddst from edges where ds=" + ds;
				rootS += " and iddst not in (select idsrc from edges where ds=" + ds + ")";
			}
			else
				return null;
			rootS += "order by iddst";
			try{
				stmt = conn.createStatement();
				rs = stmt.executeQuery(rootS);
				while(rs.next()){
					v.add(new Integer(rs.getInt(1)));
				}
				finalizeStatement();
			}
			catch(Exception e){
	    		System.out.println("Exception at RelationalDB.insert!");
	    		e.printStackTrace();	    		
			}
			int vSize = v.size();
			if (vSize == 0) return null;
			toReturn = new int[vSize];
			for (int i=0; i<vSize; i++){
				toReturn[i] = ((Integer) v.get(i)).intValue();
			}
			return toReturn;
	    }
	    
		public int[][] getEdges(int ds){		
			int nodeSize = getAggregate("count", "*", "Nodes", "ds=" + ds) + 1;
			int[][] toReturn = new int[nodeSize][1];
			try{
				rs = execute("Select idsrc, iddst from edges where ds=" + ds + " order by idsrc");
				int idSrc = 0;
				while(rs.next()){
					idSrc = rs.getInt(1);					
					toReturn[idSrc] = addToArray(toReturn[idSrc], rs.getInt(2));
				}			
				finalizeStatement();
			}
			catch(Exception e){
				System.out.println("Exception at getEdges!");
				e.printStackTrace();
				System.exit(1);
			}
			return toReturn;
		} 
	    
		public int[] addToArray(int[] a, int dst){
			int[] newA = null;;
			 if (a==null || (a.length==1 && a[0]==0)){
			 	newA = new int[1];
			 	newA[0] = dst;
			 	return newA;
			 }
			 int aLength = a.length;
			 newA = new int[aLength+1];
			 for (int i=0; i<aLength; i++){
			 	newA[i] = a[i];
			 }
			 newA[aLength] = dst;
			 return newA;			 
		}
	    */
	    public void setPrint(boolean b){ print = b; }
}
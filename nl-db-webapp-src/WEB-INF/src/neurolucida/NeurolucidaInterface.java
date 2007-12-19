
package neurolucida;

//import java.util.*;
import env.ReadEnv;
import java.util.Vector;



public class NeurolucidaInterface {
    
	private static String path = null;
    public NeurolucidaInterface(){}
    public static RelationalDB rdb = null;
    public static Operations opers = null;
    public static StringBuffer s_ = new StringBuffer();
   	
    public static void main(String[] args) {}

    public static String exec(Vector inputs, boolean isXML){
    	StringBuffer xml = new StringBuffer();
    	try{
    		String req = null;
    		String value = null;    	
    		xml.append("<html><title>RESULT PAGE</title><body>");
    		if (inputs!=null){
    			String anInput = null;
    			for (int i=0; i<inputs.size(); i++){
    				anInput = (String) inputs.get(i);
    				//xml.append(anInput + "\n");
    			}
    		}
    		xml.append("<br>");
    	
    		String path = "C:/DEV/Neurolucida/WEB-INF/";
    		path = ReadEnv.getHOME("NEUROLUCIDA_HOME", true);
    		if ((path == null)||(path.length()==0)){
    			xml.append("NEUROLUCIDA_HOME environment variable cannot be read!");
    			xml.append("Therefore configuration path could not be found!");
    			xml.append("</body></html>");
    			return xml.toString();
    		}
    		if ((inputs == null)||(inputs.size()==0)){
    			xml.append("There is no input!<br>Please validate your request!");
    			xml.append("</body></html>");
    			return xml.toString();
    		}
    		req = (String) inputs.get(0);
    		if ((req==null) || (req.length()==0)){
    			xml.append("Request is null!<br>Please validate your request!");
    			xml.append("</body></html>");
    			return xml.toString();
    		}
    		int index = req.indexOf(":");
    		if (index!=1)
    			value = req.substring(index+1).trim();
    		if ((value==null) || (value.length()==0)){
    			xml.append("Request input is null!<br>Please validate your request!");
    			xml.append("</body></html>");
    			return xml.toString();
    		}    	
    		if (rdb == null){						
    			//xml.append("PATH:" + path);
    			rdb = new RelationalDB(path + "/connection.txt");
    			if (rdb.status!=0){
    				xml.append(s_);
    				xml.append("</body></html>");
    				return xml.toString();
    			}
    			rdb.initDBConn();		
    			opers = new Operations(rdb);
    		} 
    		boolean cont = true;
    		//GET NEUROLUCIDA INFO 
    		if (req.startsWith("neuron:")){
    			xml.append("<hr> <font color=\"#aa2222\"> <b><center>GET NEUROLUCIDA INFO </center></b> </font><hr>");
    			String neuron = value;    		
    			if (inputs.size()<4){
    				xml.append("Not enough inputs! (There should be 4 inputs for neuron, level cond, level and operation)\n");
    				cont = false;
    			}
    			if (cont && !rdb.validateNeuron(value)){
    				xml.append("Neuron: " + value + " not found in the database!\n");
    				cont = false;
    			}	
    		
    			req =  (String) inputs.get(1); // lcond:
    			String lcond = null;
    			if (cont){
    				if ((lcond = getValue("lcond:", req)) == null){
    					xml.append("Level condition is null!\n");
    					cont = false;
    				}    		
    				else if (!lcond.equals("=") && !lcond.equals("<=") && !lcond.equals(">=") && 
    						!lcond.equals("<") && !lcond.equals(">")){
    					xml.append("Level condition: " + lcond + " is not valid!\n");
    					cont = false;
    				}
    			}
    		
    			req = (String) inputs.get(2); //level
    			int level_=0;
    			if (cont){
    				String level = null;    		
    				try{ 
    					if ((level=getValue("level:", req)) == null){
    						xml.append("Level is null!\n");
    						cont = false;
    					}
    					else 
    						level_ = Integer.parseInt(level);
    				}
    				catch(Exception e){
    					xml.append("Level " + level + " is not valid!\n");
    					cont = false;  	
    				}
    			}

    			req =  (String) inputs.get(3); // op:
    			String op = null;
    			if (cont){
    				if ((op = getValue("op:", req)) == null){
    					xml.append("Operation condition is null!\n");
    					cont = false;
    				}    		
    				else{    				
    					op = op.toUpperCase();
    					xml.append("<table border=1>\n");
    					xml.append("<tr bgcolor=\"#BBBBCC\"><th>Input Name</th><th>Value</th></tr>\n");
    					xml.append("<tr><td>Neuron</td><td>" + neuron + "</td></tr>\n");				
    					xml.append("<tr><td>Level Condition</td><td>"+ lcond + " " + level_ + "</td></tr>\n");					
    					xml.append("<tr><td>Operation</td><td>"+ op + "</td></tr>\n");
    					xml.append("</table><br>");
    					xml.append(opers.getInfo(neuron));  
    					if (op.startsWith("METADATA")){
    					}
    					else if (op.startsWith("ALL")){
    						xml.append(opers.getXthOrderWhat(neuron, "FANOUT", level_, lcond));
    						xml.append(opers.getXthOrderWhat(neuron, "DIAMETER", level_, lcond));
    						xml.append(opers.getXthOrderWhat(neuron, "LENGTH", level_, lcond));
    						xml.append(opers.getXthOrderWhat(neuron, "SPINES", level_, lcond));
    						xml.append(opers.getXthOrderWhat(neuron, "SPINEDENSITY", level_, lcond));
    						xml.append(opers.getXthOrderArea(neuron, level_, lcond));
    						xml.append(opers.getXthOrderVolume(neuron, level_, lcond));
    						xml.append(opers.getTotalArea(neuron));
    						xml.append(opers.getTotalVolume(neuron));
    					}
    					else if (op.startsWith("GETXTHORDERFANOUT") || op.startsWith("GETXTHORDERDIAMETER") || op.startsWith("GETXTHORDERLENGTH") || 
    							op.startsWith("GETXTHORDERSPINES") || op.startsWith("GETXTHORDERSPINEDENSITY")){
    						String what = op.substring(11);   
    						xml.append(opers.getXthOrderWhat(neuron, what, level_, lcond));
    					}
    					else if (op.startsWith("GETXTHORDERAREA")){
    						xml.append(opers.getXthOrderArea(neuron, level_, lcond));	
    					}
    					else if (op.startsWith("GETXTHORDERVOLUME")){
    						xml.append(opers.getXthOrderVolume(neuron, level_, lcond));    					
    					}
    					else if (op.startsWith("GETTOTALAREA")){
    						xml.append(opers.getTotalArea(neuron));
    					}
    					else if (op.startsWith("GETTOTALVOLUME")){
    						xml.append(opers.getTotalVolume(neuron));
    					}
    					else{
    						xml.append("<br><b>Operation " + op + " is not valid!</b>");
    					}
    				}
    			}
    		}
    		//COMPARE TWO NEURONS
    		else if(req.startsWith("neuron1:")){
    			xml.append("<hr> <font color=\"#aa2222\"> <b><center>COMPARE TWO NEURONS </center></b> </font><hr>");    		
    			String neuron1 = value;    		
    			if (inputs.size()<4){
    				xml.append("Not enough inputs! (There should be 5 inputs for neuron1, neuron2, level and criteria)\n");
    				cont = false;
    			}
    			if (cont && !rdb.validateNeuron(neuron1)){
    				xml.append("Neuron 1: " + neuron1 + " not found in the database!\n");
    				cont = false;
    			}    		
    			
    			req =  (String) inputs.get(1); // neuron2:
    			String neuron2 = null;
    			if (cont){
    				if ((neuron2 = getValue("neuron2:", req)) == null){
    					xml.append("Neuron 2 is null!\n");
    					cont = false;
    				}    		
    				else if (!rdb.validateNeuron(neuron2)){
    					xml.append("Neuron 2: " + neuron2 + " not found in the database!\n");
    					cont = false;
    				}    			
    			}
    					
    			req = (String) inputs.get(2); //level
    			int level_=0;
    			if (cont){
    				String level = null;    		
    				try{ 
    					if ((level=getValue("level:", req)) == null){
    						xml.append("Level is null!\n");
    						cont = false;
    					}
    					else 
    						level_ = Integer.parseInt(level);
    				}	
    				catch(Exception e){
    					xml.append("Level " + level + " is not valid!\n");
    					cont = false;  	
    				}
    			}
    			
    			req =  (String) inputs.get(3); // criteria:
    			String criteria = null;
    			if (cont){
    				if ((criteria = getValue("criteria:", req)) == null){
    					xml.append("Criteria is null!\n");
    					cont = false;
    				}    		
    				else if (!criteria.equals("FANOUT") && !criteria.equals("LENGTH") && !criteria.equals("DIAMETER") &&
    						!criteria.equals("SPINES") && !criteria.equals("SPINEDENSITY")){
    					xml.append("Criteria " + criteria + " is not valid!\n");
    					cont = false;
    				}    			
    			}
    			
    			if (cont){
    				xml.append("<table border=1>\n");
    				xml.append("<tr bgcolor=\"#BBBBCC\"><th>Input Name</th><th>Value</th></tr>\n");
    				xml.append("<tr><td>Neuron 1</td><td>" + neuron1 + "</td></tr>\n");				
    				xml.append("<tr><td>Neuron 2</td><td>" + neuron2 + "</td></tr>\n");
    				xml.append("<tr><td>Level Condition</td><td> = " + level_ + "</td></tr>\n");					
    				xml.append("<tr><td>Criteria</td><td>"+ criteria + "</td></tr>\n");
    				xml.append("</table><br>");
    				xml.append(opers.t_test(neuron1, neuron2, level_, criteria));
    			}
    		}
    		//LOAD/DELETE NEURON 
    		else if(req.startsWith("load:")){
    			xml.append("<hr> <font color=\"#aa2222\"> <b><center>LOAD/DELETE NEURON </center></b> </font><hr>");
    			String load = value;    		
    			if (inputs.size()<5){
    				xml.append("Not enough inputs for this operation! \n");
    				cont = false;
    			}
    			//LOAD
    			if ((load != null) && load.toLowerCase().startsWith("http://") && (load.length()>20)){
    				xml.append("\n<br><li><h3>LOAD NEURON </h3>");    				
        			req =  (String) inputs.get(1); // neuronl:
        			String neuronl = null;
        			if (cont && (neuronl = getValue("neuronl:", req)) == null){
        				xml.append("Neuron to load is null!\n");
        				cont = false;
        			}
        			else if(neuronl.length()<=2){
        				xml.append("Neuron name " + neuronl + " is too short to load!\n");
        				cont = false;
        			}
        			
        			req =  (String) inputs.get(2); // pwl:
        			String pwl = null;
        			if (cont && (pwl = getValue("pwl:", req)) == null){ 
        				xml.append("Pasword to load is null!\n");
        				cont = false;
        			}    		
    				if (!pwl.equals("LoadNeuron")){
        				xml.append("Invalid Pasword!\n");
        				cont = false;
    				}
    				if (cont){
        				xml.append("<table border=1>\n");
        				xml.append("<tr bgcolor=\"#BBBBCC\"><th>Input Name</th><th>Value</th></tr>\n");
        				xml.append("<tr><td>Load</td><td>" + load + "</td></tr>\n");				
        				xml.append("<tr><td>Neuron</td><td>" + neuronl + "</td></tr>\n");
        				xml.append("</table><br>");
    					Parser p = new Parser(rdb);    					
    					p.parse(load, neuronl);
    					xml.append(p.getResult());
    				}
    				
    			}
    			//DELETE
    			else{
    				xml.append("\n<br><li><h3>DELETE NEURON </h3>");
        			req =  (String) inputs.get(3); // delete:
        			String delete = null;
        			if (cont && (delete = getValue("delete:", req)) == null){ 
        				xml.append("Pasword to delete is null!\n");
        				cont = false;
        			}
    				else if (!rdb.validateNeuron(delete)){
    					xml.append("Neuron " + delete + " is not found in the database!\n");
    					cont = false;
    				}    			
        			if (cont){    				
        				req =  (String) inputs.get(4); // pwd:
        				String pwd = null;
        				if ((pwd = getValue("pwd:", req)) == null){ 
        					xml.append("Pasword to load is null!\n");
        					cont = false;
        				}    		
    					if (!pwd.equals("DeleteNeuron")){
        					xml.append("Invalid Pasword!\n");
        					cont = false;
    					}
    					if (cont){
            				xml.append("<table border=1>\n");
            				xml.append("<tr bgcolor=\"#BBBBCC\"><th>Input Name</th><th>Value</th></tr>\n");
            				xml.append("<tr><td>Neuron</td><td>" + delete+ "</td></tr>\n");				
            				xml.append("</table><br>");
    						opers.deleteNeuron(delete);
    						xml.append("Successfully deleted!");
    					}    		
        			}
    			}
    		}
    		else{
    			xml.append("Invalid/Missing request: " + req + "<br>Please validate your request!\n");
    		}
    		//xml.append(s_.toString());
    		xml.append("</body></html>");
    	}
    	catch(Exception exp){
    		xml.append("Exception occured: " + exp);
    		return xml.toString();
    	}
    	return xml.toString();
    }
    
    public static String getValue(String req, String input){
    	if ((req==null) || (input==null) || !input.startsWith(req))
    		return null;
    	input = input.trim();
    	int index = input.indexOf(":");
    	if (index==-1)
    		return null;
    	return input.substring(index+1);
    }
}


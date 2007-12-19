/*
 * Created on Jan 9, 2006
 * M. Erdem Kurul
 */

package neurolucida;
import java.util.*;

public class Operations {
    RelationalDB rdb = null;
    String result = new String();
    public Operations(RelationalDB r){
    	rdb = r;
    }

    public String getXthOrderArea(String n, int level, String levelCond){
    	//System.out.println("GET XTH ORDER AREA");
    	//System.out.println("\n " + levelCond + "" + level + ".th order area is: " + rdb.getXthOrderArea(n, level, levelCond));
    	StringBuffer res = new StringBuffer();
    	res.append("\n<br><li><h3>GET XTH ORDER AREA ");
    	res.append(" for neuron " + n + " where level " + levelCond + " " + level + "</h3>\n");
    	res.append("<b> Volume: " + rdb.getXthOrderArea(n, level, levelCond)+" </b><br>\n");
    	return res.toString();
    }
    public String getTotalArea(String n){
    	//System.out.println("GET TOTAL AREA");
    	//System.out.println("\n Total order area is: " + rdb.getXthOrderArea(n, 0, ">="));
    	StringBuffer res = new StringBuffer();
    	res.append("\n<br><li><h3>GET TOTAL AREA ");
    	res.append(" for neuron " + n + " (default condition level>=0)</h3>\n");
    	res.append("<b> Volume: " + rdb.getXthOrderArea(n, 0, ">=")+" </b><br>\n");    	
    	return res.toString();    	
    }
    public String getXthOrderVolume(String n, int level, String levelCond){
    	//System.out.println("GET XTH ORDER VOLUME");
    	//System.out.println("\n " + levelCond + "" + level + ".th order volume is: " + rdb.getXthOrderVolume(n, level, levelCond));
    	StringBuffer res = new StringBuffer();
    	res.append("\n<br><li><h3>GET XTH ORDER VOLUME ");
    	res.append(" for neuron " + n + " where level " + levelCond + " " + level + "</h3>\n");
    	res.append("<b> Volume: " + rdb.getXthOrderVolume(n, level, levelCond)+" </b><br>\n");
    	return res.toString();
    }
    public String getTotalVolume(String n){
    	//System.out.println("GET TOTAL VOLUME");
    	//System.out.println("\n Total order area is: " + rdb.getXthOrderVolume(n, 0, ">="));
    	StringBuffer res = new StringBuffer();
    	res.append("\n<br><li><h3>GET TOTAL VOLUME ");
    	res.append(" for neuron " + n + " (default condition level>=0)</h3>\n");
    	res.append("<b> Volume: " + rdb.getXthOrderVolume(n, 0, ">=")+" </b><br>\n");    	
    	return res.toString();    	
    }
    public int getMaxDepth(String n){
    	int md = rdb.getMax("level_", "nl_nodes", "upper(neuron)='"+n.toUpperCase()+"'");
    	System.out.println("\n Max depth is: " + md);
    	return md;
    }	

    public void getXthOrderAverageFanout(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER AVERAGE FANOUT:");    	
    	System.out.println("\n " + levelCond + "" + level + ".th order Average Fanout is: " + 
    	rdb.getXthOrderAverageFanout(n, level, levelCond));    	
    }
    public void getXthOrderAverageDiameter(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER AVERAGE DIAMETER :");
    	System.out.println("\nAverage " + levelCond + "" + level + ".th order diameter is: " + rdb.getXthOrderAverageDiameter(n, level, levelCond));
    }

    public String getInfo(String n){
    	String toReturn = rdb.getInfo(n);
    	System.out.println(toReturn);
    	return toReturn;
    }
    
    public String getXthOrderWhat(String n, String what, int level, String levelCond){    	
    	System.out.println("GET XTH ORDER " + what + ":");
    	StringBuffer res = new StringBuffer();
    	res.append("\n<br><li><h3>GET XTH ORDER ");
    	if (what==null){
    		res.append( "\n Null Operation!"+ ":\n");
    		return res.toString();
    	}
    	else{
    		res.append(what + " for neuron " + n + " where level " + levelCond + " " + level + "</h3>\n");
    	}    	
    	
    	boolean isInteger = false;
    	what = what.trim().toUpperCase();
    	if (what.equals("FANOUT")){
    		what = "noChildren";
    		isInteger = true;
    	}
    	else if (what.equals("DIAMETER"))
    		what = "2*radius";
    	else if (what.equals("LENGTH"))
    		what = "length";
    	else if (what.equals("SPINES")){
    		what = "spineCount";
    		isInteger = true;
    	}
    	else if (what.equals("SPINEDENSITY"))
    		what = "spineCount/length";
    	else{
    		res.append("\nOperation \"" + what + "\" could not be identified!\n");
    		return res.toString();
    	}
    	Vector v = rdb.getXthOrderWhat(n, what, level, levelCond);
    	if (v != null){
    		int size = v.size();
    		//System.out.println("\n " + levelCond + "" + level + ".th order " + what + " is: ");
    		res.append("\n");
    		
    		float totalTotal = 0.0f;    		
    		int totalCount = 0;
			res.append("<table border=1>\n");
							
			String color1="\"55bb55\"";
			String color2="\"bb55bb\"";
    		for (int i=size-1; i>=0; i--){
    			//System.out.println("      For dendrite " + (size-i) + " : ");    			
    			Vector v2 = (Vector) v.get(i);    			
    			int v2size = v2.size();
    			totalCount+=v2size;
    			//res.append("dendrite " + (size-i) + " : " + v2size + " results! \n");
    			res.append("<tr><td><b><font color=");
    			if (i%2==0) res.append(color1);
    			else res.append(color2);
    			res.append(">Dendrite #</font></b></td><td>" + (size-i) + "</td></tr>\n");
    			res.append("<tr><td>Value Size</td><td>" + v2size + "</td></tr>\n");
    			float total = 0.0f;
    			float current = 0.0f;
    			res.append("<tr><td>Values</td><td>");
    			for (int j=0; j<v2size; j++){
    				//System.out.println(" " + (j+1) + ": " + v2.get(j));
    				current = ((Float) v2.get(j)).floatValue();
    				total += current;
    				res.append(" " + (j+1)+":");
    				if (isInteger)
    					res.append((int) current);
    				else
    					res.append(current);
    			}    			
    			res.append("</td></tr>\n");
    			if (v2size>0){
    				res.append("<tr><td>Total</td><td>" + total + "</td></tr>\n");
    				res.append("<tr><td>Average</td><td>" + (total/v2size) + "</td></tr>\n");
    				totalTotal += total;
    				res.append("<tr><td></td><td></td></tr>\n");
    			}
    		}
    		res.append("</table>");
    		res.append("<b> Grand Total: " + totalTotal + ", Count: " + totalCount + ", Average: " + (totalTotal/totalCount) + " </b><br>\n");
    	}
    	else{
    		//System.out.println(" It is NULL");
    		res.append("No results found!");
    	}
    	return res.toString();
    }
    
    public void getXthOrderFanout(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER FANOUT:");
    	Vector v = rdb.getXthOrderFanout(n, level, levelCond);
    	if (v != null){
    		int size = v.size();
    		System.out.println("\n " + levelCond + "" + level + ".th order fanout is: ");
    		for (int i=0; i<size; i++){
    			System.out.println(i + " : " + v.get(i));
    		}
    	}
    	else{
    		System.out.println(" It is NULL");
    	}
    }
    public void getXthOrderDiameter(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER DIAMETER:");
    	Vector v = rdb.getXthOrderDiameter(n, level, levelCond);
    	if (v != null){
    		int size = v.size();
    		System.out.println("\n " + levelCond + "" + level + ".th order diameter is: ");
    		for (int i=0; i<size; i++){
    			System.out.println(i + " : " + v.get(i));
    		}
    	}
    	else{
    		System.out.println(" It is NULL");
    	}
    }
    public void getXthOrderLength(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER LENGTH:");
    	Vector v = rdb.getXthOrderLength(n, level, levelCond);
    	if (v != null){
    		int size = v.size();
    		System.out.println("\n " + levelCond + "" + level + ".th order length is: ");
    		for (int i=0; i<size; i++){
    			System.out.println(i + " : " + v.get(i));
    		}
    	}
    	else{
    		System.out.println(" It is NULL");
    	}
    }
    public void getXthOrderSpines(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER SPINES:");
    	Vector v = rdb.getXthOrderSpines(n, level, levelCond);
    	if (v != null){
    		int size = v.size();
    		System.out.println("\n " + levelCond + "" + level + ".th order spines are: ");
    		for (int i=0; i<size; i++){
    			System.out.println(i + " : " + v.get(i));
    		}
    	}
    	else{
    		System.out.println(" It is NULL");
    	}
    }
    public void getXthOrderSpineDensity(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER SPINEDENSITY:");
    	Vector v = rdb.getXthOrderSpineDensity(n, level, levelCond);
    	if (v != null){
    		int size = v.size();
    		System.out.println("\n " + levelCond + "" + level + ".th order spine density is: ");
    		for (int i=0; i<size; i++){
    			System.out.println(i + " : " + v.get(i));
    		}
    	}
    	else{
    		System.out.println(" It is NULL");
    	}
    }
    
    // C O M P U T E start
    public void getXthOrderDiameterCompute(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER DIAMETER COMPUTE:");    	
    	Vector v = rdb.getXthOrderDiameterCompute(n, level, levelCond);
    	if (v != null){
    		int size = v.size();
    		System.out.println("\n " + levelCond + "" + level + ".th order diameter is: ");
    		for (int i=0; i<size; i++){
    			System.out.println(i + " : " + v.get(i));
    		}
    	}
    	else{
    		System.out.println(" It is NULL");
    	}
    }
    public void getXthOrderFanoutCompute(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER FANOUT COMPUTE:");
    	Vector v = rdb.getXthOrderFanoutCompute(n, level, levelCond);
    	if (v!= null){
    		int size = v.size();
    		System.out.println("\n " + levelCond + "" + level + ".th order Fanout is: ");
    		for (int i=0; i<size; i++){
    			System.out.println(i + " " + v.get(i));
    		}
    	}
    	else{
    		System.out.println("it is NULL");
    	}
    }
    public void getXthOrderAverageFanoutCompute(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER AVERAGE FANOUT COMPUTE:");
    	System.out.println("\n " + levelCond + "" + level + ".th order Average Fanout is: " + 
    	rdb.getXthOrderAverageFanoutCompute(n, level, levelCond));    	
    }
    public void getXthOrderAreaCompute(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER AREA COMPUTE");
    	System.out.println("\n " + levelCond + "" + level + ".th order area is: " + rdb.getXthOrderAreaCompute(n, level, levelCond));
    }
    public void getXthOrderAverageDiameterCompute(String n, int level, String levelCond){
    	System.out.println("GET XTH ORDER AVERAGE DIAMETER COMPUTE :");
    	System.out.println("\nAverage " + levelCond + "" + level + ".th order diameter is: " + rdb.getXthOrderAverageDiameterCompute(n, level, levelCond));
    }
    public void getTotalAreaCompute(String n){
    	System.out.println("GET TOTAL AREA COMPUTE");
    	System.out.println("\n Total order area is: " + rdb.getXthOrderAreaCompute(n, 0, ">="));
    }
    // C O M P U T E end

    public void deleteNeuron(String n){
    	rdb.deleteAll(n);
    }
    

    public String t_test(String n1, String n2, int level, String parameter){   
    	Vector v1N = null, v2N = null;
    	StringBuffer res = new StringBuffer();
    	System.out.println("T_TEST:");
    	res.append("\n<br><li><h3>T-TEST COMPARISON");
    	res.append(" for neuron 1: <font color=\"#ff4444\">'" + n1 + "'</font> and neuron 2: <font color=\"#ff4444\">'" + n2 + "'</font> ");
    	res.append("where level = " + level + " and criteria=" + parameter +"</h3>\n");
    	
    	if (level<2 || level>5){
    		System.out.println("Level : '" + level + "' is not valid to compute");
    		res.append("Level : '" + level + "' is not valid to compute!<br>");
    		res.append("Select a level between [2,5]!<br>");
    		return res.toString();
    	}
    	if (parameter == null)
    		parameter = "FANOUT";
    	parameter = parameter.toUpperCase();
    	v1N = rdb.getXthOrderWhat(n1, parameter, level, "=");
    	v2N = rdb.getXthOrderWhat(n2, parameter, level, "=");
    	
    	
    	/* TEST: for the below values t-test should be: 13.0, result not similar! 
    	v1N = new Vector();
    	Vector av = new Vector();
    	av.add(new Float(520));
    	av.add(new Float(460));
    	av.add(new Float(500));
    	av.add(new Float(470));
    	v1N.add(av);
    	v2N = new Vector();
    	av = new Vector();
    	av.add(new Float(230));
    	av.add(new Float(270));
    	av.add(new Float(250));
    	av.add(new Float(280));
    	v2N.add(av);
    	*/
    	
    	
    	
    	if (v1N==null || v2N==null){
    		System.out.println("T-TEST cannot be performed! Please check the followings:");
    		System.out.println("* Valid neuron names!");
    		System.out.println("* Set a valid parameter for comparison: [FANOUT, DIAMETER, LENGTH, SPINES, SPINEDENSITY]");
    		System.out.println("* Relax the condition on level!");
    		res.append("T-TEST cannot be performed! Please check the followings:<br>");
    		res.append("* Valid neuron names!<br>");
    		res.append("* Set a valid parameter for comparison: [FANOUT, DIAMETER, LENGTH, SPINES, SPINEDENSITY]<br>");
    		res.append("* Relax the condition on level!<br>");
    		res.append("<br>LOG:<br>" + NeurolucidaInterface.s_.toString());
    		return res.toString();
    	}    	
    	int v1Ds = v1N.size(); // # of dendrites
    	int v2Ds = v2N.size(); // # of dendrites
    	int v1Size = 0, v2Size = 0;    	
    	System.out.println(n1 + " has  " + v1Ds + " dendrites,			 " + n2 + " has " + v2Ds + " dendrites!");
    	res.append("<p><li>" + n1 + " has  " + v1Ds + " dendrites,			 " + n2 + " has " + v2Ds + " dendrites!<br>");
    	res.append("<table border=1>\n");
    	res.append("<tr bgcolor=\"#CCCC22\"><th>" + n1 + "'s dendrite #</th><th>Size</th><th>" + n2 + "'s dendrite #</th>");
    	res.append("<th>Size</th><th>T-TEST Value</th><th>RESULT</th></tr>\n");
    	for (int abc = 0; abc<v1Ds; abc++){
    		Vector v1 = (Vector) v1N.get(abc);
    		v1Size = v1.size();
    		for (int def=0; def<v2Ds; def++){
    			System.out.println("\n\n *** Checking " + n1 + "'s " + (abc+1) + " dendrite against " + n2 + "'s " + (def+1) + " dendrite: ");
    			//res.append("\n\n *** Checking " + n1 + "'s " + (abc+1) + " dendrite against " + n2 + "'s " + (def+1) + " dendrite: <br>");    			
    			Vector v2 = (Vector) v2N.get(def);    			
    			v2Size = v2.size();
    			if (v1Size > 30 || v2Size > 30 || v1Size==0 || v2Size==0){
    				System.out.println("T_TEST is not applicable because of set size considerations!");
    				//res.append("T_TEST is not applicable because of set size considerations!<br>");
    				System.out.println("Dendrite A's size " + v1Size + ", Dendrite B's size " + v2Size);
    				//res.append("Dendrite A's size " + v1Size + ", Dendrite B's size " + v2Size + "<br>");
    				res.append("<tr><td>" + (abc+1) + "</td><td>"+v1Size+"</td><td>" + (def+1) + "</td><td>"+v2Size+"</td>");
    				res.append("<td>N/A due to sample size</td><td>N/A due to sample size</td>");
    				if (v1Size>30 || v1Size==0){    					
    					break;
    				}
    				continue;
    			}
				res.append("<tr><td>" + (abc+1) + "</td><td>"+v1Size+"</td><td>" + (def+1) + "</td><td>"+v2Size+"</td>");
    			float[] v1f = new float[v1.size()];
    			for (int i=0; i<v1Size; i++){
    				Object ob = v1.get(i);
    				if (ob instanceof Float)
    					v1f[i] = ((Float) ob).floatValue();
    				else if (ob instanceof Integer)
    					v1f[i] = (float) ((Integer) ob).intValue();
    				//	System.out.println("v1f " + i + " : " + v1f[i] + ", v1 " + v1.get(i));
    			}
    			System.out.println();
    			float[] v2f = new float[v2.size()];
    			for (int i=0; i<v2Size; i++){
    				Object ob = v2.get(i);
    				if (ob instanceof Float)
    					v2f[i] = ((Float) ob).floatValue();
    				else if (ob instanceof Integer)
    					v2f[i] = (float) ((Integer) ob).intValue();
    				//System.out.println("v2f " + i + " : " + v2f[i] + ", v2 " + v2.get(i));
    			}    	
    			float delta1x_2 = computeSumsSquare(v1f);
    			float delta2x_2 = computeSumsSquare(v2f);
    			float delta1_x2 = computeSquaresSum(v1f);
    			float delta2_x2 = computeSquaresSum(v2f);
    			float var1_2 = (delta1_x2 - (delta1x_2/v1Size)) / (v1Size-1);
    			float var2_2 = (delta2_x2 - (delta2x_2/v2Size)) / (v2Size-1);
    			System.out.println("var1_2 " + var1_2 + ", var2_2 " + var2_2);
    			//res.append("var1_2 " + var1_2 + ", var2_2 " + var2_2 + "<br>");
    			float vd = (float) Math.sqrt((var1_2/v1Size) + (var2_2/v2Size));
    			System.out.println("VD: " + vd);
    			float t = (float) 0;
    			if (vd != 0)
    				t = Math.abs( (computeMean(v1f) - computeMean(v2f)) ) /vd;
    	
    			double ttest[] = rdb.getTtest(v1Size+v2Size);
    			System.out.println("neuron1: " + n1 + ", neuron2: " + n2);
    			//res.append("neuron1: " + n1 + ", neuron2: " + n2 + "<br>");
    			res.append("<td>"+t+"</td>");
    			if (t>ttest[2]){
    				if (t>ttest[1]){
    					if (t>ttest[0]){
    						System.out.println(" Two dendrites are NOT similar under 90% confidence!");
    						res.append("<td>NOT Similar!</td>");    						
    					}
    					else{
    						System.out.println(" Two dendrites are similar with 90% confidence!");
    						res.append("<td>Similar with 90% confidence!</td>");
    					}
    				}
    				else{
    					System.out.println(" Two dendrites are similar with 95% confidence!");
    					res.append("<td>Similar with 95% confidence!</td>");
    				}
    			}
    			else{
    				System.out.println(" Two dendrites are similar with 99% confidence!");
    				res.append("<td>Similar with 99% confidence!</td>");
    			}
    			res.append("</tr>");
    			System.out.println("T-TEST value is: " + t);
    			//res.append("T-TEST value is: " + t + "<br>");
    			//res.append("<li>99% value is :" + ttest[2] + "br>");
    			//res.append("<li>95% value is :" + ttest[1] + "<br>");
    			//res.append("<li>90% value is :" + ttest[0] + "<br>");
    		}
    	}
		res.append("</table><br>");
    	return res.toString();
    }
    
    public float computeMean(float v[]){
    	int vLength = v.length;
    	float total = 0;
    	for (int i=0; i<vLength; i++){
    		total += v[i];
    	}
    	return total/(float)vLength;
    }
    public float computeVarianceSquare(float v[]){
    	float mean = computeMean(v);
    	int vLength = v.length; 
    	float total = 0;
    	for (int i=0; i<vLength; i++){
    		total += Math.pow(v[i]-mean,2);
    	}
    	return total;
    }
    public float computeSum(float v[]){
    	float mean = computeMean(v);
    	int vLength = v.length; 
    	float total = 0;
    	for (int i=0; i<vLength; i++){
    		total += v[i];
    	}
    	return total;
    }
    public float computeSumsSquare(float v[]){
    	float mean = computeMean(v);
    	int vLength = v.length; 
    	float total = 0;
    	for (int i=0; i<vLength; i++){
    		total += v[i];
    	}
    	return total*total;
    }
    public float computeSquaresSum(float v[]){
    	float mean = computeMean(v);
    	int vLength = v.length; 
    	float total = 0;
    	for (int i=0; i<vLength; i++){
    		total += (v[i]*v[i]);
    	}
    	return total;
    }

}

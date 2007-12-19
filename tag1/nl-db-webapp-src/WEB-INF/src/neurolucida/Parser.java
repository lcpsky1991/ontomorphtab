/*
 * Created on Jan 3, 2006 @author erdem
 */

package neurolucida;

import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class Parser {

	BufferedReader br = null;

	String neuron = new String();

	Hashtable segments = new Hashtable();

	int currentSid = -1;

	RelationalDB rdb = null;

	int currentDid = 0;

	int INCREMENT = 10000;

	int spineCount = 0;

	int position = -1; // 0[dendrite:n], 1[axon:-1], 2[cell body:-2]
	
	StringBuffer res = new StringBuffer();
	
	public Parser(RelationalDB r) {
		rdb = r;
		neuron = new String();
		segments = new Hashtable();
		currentSid = -1;
		res = new StringBuffer();
	}

	public boolean init(String filename) {
		try {
			//original: br = new BufferedReader(new FileReader(filename));	
			if (filename.startsWith("http://"))			
				br = new BufferedReader(new InputStreamReader(new URL(filename).openStream()));
			else
				br = new BufferedReader(new FileReader(filename));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception at Parser.init " + e);
			res.append("Exception at Parser.init " + e);
			return false;
		}
		return true;
	}

	public void setNeuron(String n) {
		neuron = n;
	}

	public void parse(String filename, String neuronName) {		
		segments = new Hashtable();
		currentSid = -1;
		neuron = neuronName;
		if (neuronName!=null){
			int dot = neuronName.indexOf("."); 
			if (dot != -1)
				neuron = neuronName.substring(0,dot);
		}
		
		parse(filename);
		rdb.setNoChildren(neuron);		
	}

	public void parse(String filename) {		
		segments = new Hashtable();
		currentSid = -1;
		if (!init(filename))
			return;
		if ((filename!=null) && (neuron.length()==0)){
			int dot = filename.indexOf("."); 
			if (dot != -1)
				neuron = filename.substring(0,dot);
		}		
		res.append("<h3> LOADING FILE " + filename + " as neuron <font color=\"#aa2222\"> <b> '"+neuron+"'</b></font><br></h3>" );
		res.append("<li>Attempting to delete old information about " + neuron + "<br>");
		rdb.deleteAll(neuron);		
		String line = null;
		try {
			while ((line = getLine()) != null) {
				//System.out.println("Calling parseEntry");
				if (position>= 0)
					parseEntry(line, spineCount);
				else
					System.out.println("Position is less than 0");
				spineCount = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		rdb.setNoChildren(neuron);
		System.out.println("DONE!");
		System.out.println("DONE!");
		System.out.println("PARSING DONE!");
		res.append("<br><b>DONE!</b>");		
	}

	public String getLine() throws Exception {
		StringBuffer toReturn = new StringBuffer();
		String line = new String();
		boolean newLine = false;
		while ((line = br.readLine()) != null) {
			newLine = false;
			line = line.trim();
			if (line.indexOf("(CellBody)") != -1) {
				position = 2;
				currentSid = -2;
				res.append("<br><br><b>CELLBODY</b><br>");
				return "CELLBODY";
			} else if (line.indexOf("(Axon)") != -1) {
				position = 1;
				currentSid = -1;
				res.append("<br><br><b>XON</b><br>");
				return "AXON";
			} else if (line.indexOf("(Dendrite)") != -1) {
				currentSid = 0;
				position = 0;
				currentDid += INCREMENT;
				//System.out.println("currentDid is " + currentDid);
				res.append("<br><br><b>DENDRITE</b><br>");
				return "DENDRITE";
			} else {
				int status = 0;
				while (true) {
					//System.out.println("<<<" + line + ">>>");
					status = checkSegment(line);
					//System.out.println("Status is : " + status + " for line <" + line + ">");
					if (status == 1) {
						newLine = true;
						toReturn.append(line + "\n");
					} else if (status == -1) {
						//System.out.println("I am here at spine");
						spineCount++;
					} // do nothing
					else if (status == -2) {
						currentDid += INCREMENT;
						break;					
					} else {
						break;
					}
					line = br.readLine();
					if (line == null)
						break;
					else
						line = line.trim();
				}
			}
			if (newLine)
				break;
		}
		if (line == null)
			return null;
		return toReturn.toString();
	}

	public int checkSegment(String l) {
		int status = 1;
		if ((l == null) || (l.length() <= 1) || (l.charAt(0) != '(')) {
			status = 0;
			if (l.startsWith("<("))
				status = -1;
		} else {
			if (l.startsWith("<(") && l.endsWith("Spine")){
				res.append("<br>SPINE<br>");
				status = -1;
			}
			else if (l.startsWith("(Dendrite)")) {
				res.append("<br><br><b>DENDRITE</b><br>");
				status = -2;
				position = 0;
				currentSid = 0;
			} else if (l.startsWith("(Axon)")) {
				res.append("<br><br><b>Axon</b><br>");
				status = -3;
				position = 1;
				currentSid = -1;
			} else if (l.startsWith("(CellBody)")) {
				res.append("<br><br><b>CellBody</b><br>");
				status = -3;
				position = 2;
				currentSid = -2;
			}
		}
		return status;
	}

	public void parseEntry(String l, int sc) {
		StringTokenizer st = new StringTokenizer(l, "\n");
		String line = null;
		float x, y, z, radius, totalRadius = 0;
		String segmentId = null;
		int seq = 0;
		int sid = 0;
		int level = 0;
		//System.out.println();
		boolean first = true;
		Vector values = new Vector();
		
		while (st.hasMoreTokens()) {
			line = st.nextToken();
			StringTokenizer st2 = new StringTokenizer(line, " \t();,");
			String token = null;
			int tc = st2.countTokens();
			if (tc != 5 && tc != 6) {
				System.out.println("token count:" + tc + ". Invalid line: " + line);
				res.append("<li> token count:" + tc + ". Invalid line: " + line + "<br>");
				continue;
			}
			seq = 0;
			x = Float.parseFloat(st2.nextToken());
			y = Float.parseFloat(st2.nextToken());
			z = Float.parseFloat(st2.nextToken());
			radius = Float.parseFloat(st2.nextToken());
			totalRadius += radius;
			
			segmentId = st2.nextToken();
			if (st2.hasMoreTokens())
				seq = Integer.parseInt(st2.nextToken());

			if (first) {
				first = false;
				if (segmentId.equals("Root"))
					segmentId = "R";
				//position 0[dendrite:n], 1[axon:-1], 2[cell body:-2]
				String key = position+""+currentDid+""+segmentId;
				if (segments.containsKey(key)) {
					sid = ((Integer) segments.get(key)).intValue();					
				} else {
					sid = currentSid;					
					segments.put(key, new Integer(currentSid));
					currentSid++;
				}
				StringTokenizer st3 = new StringTokenizer(segmentId, " -");
				level = st3.countTokens() - 1;
				res.append("<br>Processing the arm starting with [" + x + "\t" + y + "\t" + z + "]\t rad:" + radius + "\t id:" + segmentId + " \t" + seq + " <br>");
				System.out.println("Processing the arm starting with [" + x + "\t" + y + "\t" + z + "]\t rad:" + radius + "\t id:" + segmentId + " \t" + seq);
			}
			Vector aValue = new Vector();
			aValue.add("" + seq);
			aValue.add("" + x);
			aValue.add("" + y);
			aValue.add("" + z);
			aValue.add("" + radius);
			values.add(aValue);
		}
		// there is at least one entry
		if (!first) {
			//System.out.println(" sid: " + sid + ", currentDid: "+ currentDid);
			radius = (float) (totalRadius/values.size());
			putIntoTables(sid + currentDid, values, level, segmentId, radius, sc);
		}
	}

	public void putIntoTables(int sid, Vector values, int level, String segmentId, float radius, int sc) {
		System.out.println("Put into tables SID: " + sid + ", LEVEL: " + level + ", SEGMENTID: " + segmentId + ", radius: " + radius + ", sc: " + sc);
		res.append("SID: " + sid + ", LEVEL: " + level + ", SEGMENTID: " + segmentId + ", radius: " + radius + ", sc: " + sc + "<br>");
		// NL_SEGMENTS(neuron, sid, seq, x, y, z, radius)		
		String cols = "(neuron, segmentId, sequenceId, x, y, z, radius)";
		Vector aValue = new Vector();
		float xstart = 0, ystart = 0, zstart = 0, xend = 0, yend = 0, zend = 0, length = 0;
		for (int i = 0; i < values.size(); i++) {
			aValue = new Vector();
			aValue.add("'" + neuron + "'");
			aValue.add(sid + "");
			Vector v = (Vector) values.get(i);
			for (int j = 0; j < v.size(); j++)
				aValue.add(v.get(j));
			rdb.insert("NL_SEGMENTS", cols, aValue);
		}
		if (values != null && values.size() > 1) {
			Vector v = (Vector) values.get(0);
			xstart = Float.parseFloat((String) v.get(1));
			ystart = Float.parseFloat((String) v.get(2));
			zstart = Float.parseFloat((String) v.get(3));
			v = (Vector) values.lastElement();
			xend = Float.parseFloat((String) v.get(1));
			yend = Float.parseFloat((String) v.get(2));
			zend = Float.parseFloat((String) v.get(3));
			length = (float) Math.sqrt(Math.pow((xend - xstart), 2)
					+ Math.pow((yend - ystart), 2)
					+ Math.pow((zend - zstart), 2));

		}
		// NL_NODES(neuron, sid, start_, end_, level_)
		cols = "(neuron, segmentid, start_, end_, level_, path, nochildren, length, radius, spineCount)";
		aValue = new Vector();
		aValue.add("'" + neuron + "'");
		aValue.add(sid + "");
		aValue.add("-1");
		aValue.add("-1");
		aValue.add(level + "");
		aValue.add("'" + segmentId + "'");
		aValue.add("0");
		aValue.add("" + length);
		aValue.add("" + radius);
		aValue.add("" + sc);
		rdb.insert("NL_NODES", cols, aValue);

		// NL_TREE(neuron, fromSegment, fromSequence, toSegment)
		cols = "(neuron, fromSegment, fromSequence, toSegment)";
		aValue = new Vector();
		//System.out.println("LEVEL IS " + level);
		if (level > 0) {
			int last_ = segmentId.lastIndexOf("-");
			if (last_ != -1) {
				String parent = segmentId.substring(0, last_);
				parent = position+""+currentDid+""+parent;
				if (segments.containsKey(parent)) {
					int fromSegment = ((Integer) segments.get(parent)).intValue()+ currentDid;
					int fromSequence = Integer.parseInt(segmentId.substring(last_ + 1));
					aValue.add("'" + neuron + "'");
					aValue.add("" + fromSegment);
					aValue.add("" + fromSequence);
					aValue.add("" + sid);
					rdb.insert("NL_TREE", cols, aValue);
				}
			}
		}
	}
	public String getResult(){ return res.toString(); }
	public void clearResult(){ res = new StringBuffer(); }
}


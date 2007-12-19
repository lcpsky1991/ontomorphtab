package env;
import java.io.*;
import java.util.*;
//import neurolucida.NeurolucidaInterface;;

public class ReadEnv {

	public static Properties envVars = null;

	public static Properties getEnvVars() throws Throwable {
		Process p = null;
		envVars = new Properties();
		Runtime r = Runtime.getRuntime();
		String OS = System.getProperty("os.name").toLowerCase();
		//	 System.out.println(OS);
		if (OS.indexOf("windows 9") > -1) {
			p = r.exec( "command.com /c set" );
		}
		else if ( (OS.indexOf("nt") > -1) || (OS.indexOf("windows 20") > -1 ) || (OS.indexOf("windows xp") > -1) ) {
	        p = r.exec( "cmd.exe /c set" );
		}
		else { // unix
			p = r.exec( "env" );
		}
		BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
		String line;
		while( (line = br.readLine()) != null ) {
			int idx = line.indexOf('=');
			if (idx == -1) continue;
			String key = line.substring( 0, idx );
			String value = line.substring( idx+1 );
			envVars.setProperty( key, value );
			//NeurolucidaInterface.s_.append("<br>" + key + " = " + value);
			//System.out.println( key + " = " + value );
		}
		return envVars;
	}

	public static void main(String args[]) {
		try {
			Properties p = envVars;
			if (p == null)
				p = ReadEnv.getEnvVars();
			System.out.println("the current value of NEUROLCIDAHOME is : " + p.getProperty("NEUROLCIDAHOME"));
		}
		catch (Throwable e) { e.printStackTrace(); }
	}

	public static String getHOME(String h, boolean init){
		String home = "NEUROLUCIDAHOME";
		if (h != null) home = h;
		try {
			Properties p = envVars;
			if (p==null) p = ReadEnv.getEnvVars();
			if (init) p = ReadEnv.getEnvVars();
			String toReturn = p.getProperty(h);
			if (toReturn == null){
				System.out.println("Could not find " + home + " in the environment variables");
				System.out.println("Set " + home + " to the path where config file is located");
				/*UPDATE THIS LINE*/

                                if(toReturn==null)
                                  return "C:\\tomcat5\\webapps\\Neurolucida\\WEB-INF";

                                  //return "C:\\DEV\\Neurolucida\\WEB-INF";
			}
			return (String) p.getProperty(home);
		}
		catch (Throwable e) { e.printStackTrace(); }
		return null;
	}
}


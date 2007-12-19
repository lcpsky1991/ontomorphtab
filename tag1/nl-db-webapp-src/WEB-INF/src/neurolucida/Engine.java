package neurolucida;

import env.ReadEnv;


/*
 * Created on Jan 3, 2006
 * @author erdem
 */

public class Engine {
	public static void main(String[] args){		
		
		/**/		
		String n  = "alxp.txt";
		String n2 = "e1cb4a5.txt";
		String n3 = "e4cb2a2.txt";
		String n4 = "ACC1.txt";
		
		RelationalDB rdb = null;
		String path =  ReadEnv.getHOME("NEUROLUCIDA_HOME", true);
		if (path != null)
			rdb = new RelationalDB(path + "\\connection.txt");
		Operations op = new Operations(rdb);
		//op.getInfo(n);
		//System.out.println(op.getXthOrderWhat(n4,"SpineDensity", 2, "="));
		
		/*
		op.getTotalArea(n);
		op.getXthOrderSpines(n4, 3, "=");
		op.getXthOrderLength(n4, 3, "=");
		op.getXthOrderSpineDensity(n4, 3, "=");
		
		System.out.println("-" + path + "-");
		op.getXthOrderDiameter(n2, 3, "=");
		op.getXthOrderWhat(n2,"2*radius", 3, "=");		
		/**
		op.getXthOrderDiameter(n, 3, "=");
		op.getXthOrderAverageDiameter(n, 3, "=");
		op.getXthOrderArea(n, 3, "=");
		op.getTotalArea(n);
		op.getMaxDepth(n);
		
		/**/
		//op.getXthOrderFanoutCompute(n, 2, "=");
		//op.getXthOrderFanout(n, 3, "=");
		
		//op.t_test("alxp", "alxp", 3, "FANOUT");
		/*
		op.getXthOrderAverageFanoutCompute(n, 3, "=");
		op.getXthOrderAverageFanout(n, 3, "=");		
		/**/
		Parser p = new Parser(rdb);
		//p.parse("http://users.sdsc.edu/~erdem/newlife/alxp.txt");
		//p.parse(n);
		//p.parse(n2);		
		//p.parse(n3);
		p.parse(n4);	
		//rdb.deleteAll(null);
		/**/
	}
}

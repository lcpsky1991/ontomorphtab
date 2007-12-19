<%@ page import="neurolucida.NeurolucidaInterface" %>
<%@ page import="java.util.Vector" %>
<% 
response.setContentType( "text/xml" );
out.clear();
String req = null;
Vector inputs = new Vector();
req = request.getParameter("neuron");
if (req!=null){
	inputs.add("neuron:"+req);
	req = request.getParameter("lcond");
	inputs.add("lcond:"+req);
	req = request.getParameter("level");
	inputs.add("level:"+req);
	req = request.getParameter("op");
	inputs.add("op:"+req);
}
req = request.getParameter("neuron1");
if (req!=null){
	inputs.add("neuron1:"+req);
	req = request.getParameter("neuron2");
	inputs.add("neuron2:"+req);
	req = request.getParameter("level");
	inputs.add("level:"+req);
	req = request.getParameter("criteria");
	inputs.add("criteria:"+req);
}
req = request.getParameter("load");
if (req!=null){
	inputs.add("load:"+req);
	req = request.getParameter("neuronl");
	inputs.add("neuronl:"+req);
	req = request.getParameter("pwl");
	inputs.add("pwl:"+req);
	req = request.getParameter("delete");
	inputs.add("delete:"+req);
	req = request.getParameter("pwd");
	inputs.add("pwd:"+req);
}

NeurolucidaInterface ni = new NeurolucidaInterface();
response.setContentType( "text/html" );
out.println(ni.exec(inputs, false));
%>


<%@ page import="java.util.Vector" %>

<h1> hi! </h1>


<%
out.println( "<h2> hello from java code</h2>" ) ;

Vector v = new Vector() ;

v.add( new String( "hi" ) ) ;

out.println( "<p>v.size() = " + v.size() ) ;

%>

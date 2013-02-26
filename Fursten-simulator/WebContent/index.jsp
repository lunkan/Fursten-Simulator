<%@ page language="java" contentType="text/html" import="fursten.simulator.*, fursten.simulator.resource.*, java.util.List, fursten.simulator.node.*, java.awt.Rectangle"%>
<!DOCTYPE html>
<html>
	<head>
		<title>Fursten Simulator index page</title>
	</head>
	<body>
		<%
			Status status = Facade.getStatus();
			List<Resource> resources = Facade.getResources(new ResourceSelection());
			Rectangle rect = new Rectangle(Integer.MIN_VALUE/2, Integer.MIN_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE);
			List<Node> nodes = Facade.getNodes(rect, null);
		%>
		<h1>Fursten Simulator is running</h1>
		<dl>
			<dt>World name:</dt>
		    <dd><%= status.getName() %></dd>
		    <dt>Currrent tick:</dt>
		    <dd><%= status.getTick() %></dd>
		    <dt>Dimensions:</dt>
		    <dd>width(<%= status.getWidth() %>) height(<%= status.getHeight() %>)</dd>
		    <dt>Resources:</dt>
		    <dd><%= resources.size() %></dd>
		    <dt>Nodes:</dt>
		    <dd><%= nodes.size() %></dd>
		</dl>
	
	</body>
</html> 
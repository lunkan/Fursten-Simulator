<%@ page language="java" contentType="text/html" import="fursten.simulator.*, fursten.simulator.world.World, fursten.simulator.resource.*, java.util.List, fursten.simulator.node.*, java.awt.Rectangle"%>
<!DOCTYPE html>
<html>
	<head>
		<title>Instance Simulator index page</title>
	</head>
	<body>
		<%
			World world = Facade.getWorld();
			System.out.println(world);
			List<Resource> resources = Facade.getResources(new ResourceSelection());
			Rectangle rect = new Rectangle(Integer.MIN_VALUE/2, Integer.MIN_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE);
			List<Node> nodes = Facade.getNodes(rect, null);
		%>
		<h1>Fursten Simulator is running</h1>
		<dl>
			<dt>World name:</dt>
		    <dd><%= world.getName() %></dd>
		    <dt>Currrent tick:</dt>
		    <dd><%= world.getTick() %></dd>
		    <dt>Dimensions:</dt>
		    <dd>width(<%= world.getWidth() %>) height(<%= world.getHeight() %>)</dd>
		    <dt>Resources:</dt>
		    <dd><%= resources.size() %></dd>
		    <dt>Nodes:</dt>
		    <dd><%= nodes.size() %></dd>
		</dl>
	
	</body>
</html> 
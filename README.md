Fursten-Simulator
=================

Java server.

Install
-------

1. Download or fork the project
2. Make sure MySql and Java server is working (I'm using tomcat7)
3. Configure your database settings for "default" and "test" in src/fursten/simulator/Settings.xml
4. Run Unit test test/fursten/simulator to make sure settup is correct
5. Start server
6. Navigate to index page to validate if Simulator is ready.
7. Done.

API
------------

### Status

GET		/rest/status

### Resources

GET		/rest/resources
POST	/rest/resources
DELETE	/rest/resources

GET		/rest/resources/{id}
POST	/rest/resources/{id}
PUT		/rest/resources/{id}
DELETE	/rest/resources/{id}

### Nodes

GET 	/rest/nodes
PUT 	/rest/nodes
POST	/rest/nodes
DELETE	/rest/nodes
	
POST	/rest/nodes/inject
POST	/rest/nodes/remove
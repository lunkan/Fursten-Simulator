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

GET: "/rest/resources"  
Returnerar alla resurser som en lista. I nuläget består listan bara av id och namn, men jag tänker att en parameter "details=true" resulterar i att en fulständig lista skickas tillbaka  

GET: "/rest/resources/{id}"  
Hämtar data för en enskild resurs  

POST: "/rest/resources"  
Lägger till en ny root-resurs och returnerar den nya resursen med ett id genererat av servern  

POST: "rest/resources/{id}  
lägger till en ny resurs som barn till resursen med id = {id}  

PUT: "/rest/resources"  
Ersätter den befintliga listan av resurser med en helt ny lista  

PUT: "/rest/resources/{id}"  
Ersätter resursen med id = {id} med ny data  

DELETE: "/rest/resources"  
Tar bort samtliga resurser  

DELETE: "/rest/resources/{id}"  
Tar bort resursen med id = {id}  

### Nodes

GET 	/rest/nodes
PUT 	/rest/nodes
POST	/rest/nodes
DELETE	/rest/nodes
	
POST	/rest/nodes/inject
POST	/rest/nodes/remove

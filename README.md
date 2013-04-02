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

**GET:** "/rest/resources"  
**Params:** !details (true/false), !mask (32-bit resource-key-mask)  
**Produce:** "application/json", "application/x-protobuf"  
Returnerar alla resurser som en lista. I nuläget består listan bara av id och namn, men jag tänker att en parameter "details=true" resulterar i att en fulständig lista skickas tillbaka  

**GET:** "/rest/resources/{id}"  
**Produce:** "application/json", !"application/x-protobuf"    
Hämtar all data för en enskild resurs  

**!GET:** "/rest/resources/{id}/children"  
**Produce:** "application/json", "application/x-protobuf"    
Hämtar alla barn-resurser för en enskild resurs  

**!GET:** "/rest/resources/{id}/parents"  
**Produce:** "application/json", "application/x-protobuf"    
Hämtar alla föräldrar till en enskild resurs

**POST:** "/rest/resources"  
**Consume:** "application/json", !"application/x-protobuf"  
**!Produce:** "application/json", "application/x-protobuf"  
Lägger till en ny root-resurs och returnerar den nya resursen med ett id genererat av servern  

**POST:** "rest/resources/{id}  
**Consume:** "application/json" , !"application/x-protobuf"  
**!Produce:** "application/json", "application/x-protobuf"   
lägger till en ny resurs som barn till resursen med id = {id}  

**!PUT:** "/rest/resources"  
**Consume:** "application/json", "application/x-protobuf"  
Ersätter den befintliga listan av resurser med en helt ny lista  

**PUT:** "/rest/resources/{id}"  
**Consume:** "application/json", !"application/x-protobuf"  
Ersätter resursen med id = {id} med ny data  

**DELETE:** "/rest/resources"  
Tar bort samtliga resurser  

**DELETE:** "/rest/resources/{id}"  
Tar bort resursen med id = {id}, samt alla resursens barn.   

### Nodes  

**GET:** "/rest/nodes"  
**Params:** x, y, w (width), h (height), !resources (id, id, id...)  
**Produce:** "application/json", !"application/x-protobuf"  
Returnerar en lista med noder  

**!POST**	/rest/nodes (?)    
**Consume:** "application/json", "application/x-protobuf"  
Adderar en lista med noder  

**!PUT:** "/rest/nodes"  
**Consume:** "application/json", "application/x-protobuf"  
Ersätter samtliga noder med en ny lista av noder.  

**DELETE:**	"/rest/nodes"  
**!Params:** x, y, w (width), h (height), resources (id, id, id...)    
Tar bort samtliga noder.

### Node transactions

**!POST:** "/rest/node-transaction"  
**Consume:** "application/json", "application/x-protobuf"  
Adderar och tar bort noder i en transaktion. Om något går fel rullas hela transaktionen tillbaka.  

**POST:** "/rest/nodes/inject" (-)  
**Consume:** "application/json"    
Adderar en lista med noder  

**POST:** "/rest/nodes/remove" (-)  
**Consume:** "application/json"    
Tar bort en lista med noder

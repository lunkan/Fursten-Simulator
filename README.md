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

!: Funktionaliteten är inte implementerad ännu  
(-): Funktionaliteten kan komma att tas bort  

### Resources  

**GET:** "/rest/resources"  
**Params:** details (true|false) *Set true to return all data for resources, or set fales or leave blank for trimed list*  
**Params:** r (resourcekey) *Filter query by repeateateble number of resourcekeys params (r=reskey1&r=reskey2&r=...)*  
**Params:** method ("children"|"parents") *Filter query to provided resources "children"|"parents" or leave blank for normal match. Ignored if "r" params is not provided*  
**Produce:** ResourceCollection (application/json | application/xml | application/x-protobuf)  
Returns a list of all resources, or a resource list filtered by arguments. Returned resources may be of detailed or simple type.  

**GET:** "/rest/resources/{key}"  
**Produce:** Resource (application/json | application/xml | application/x-protobuf)  
Returns a single resource matching the provided resource-key.  

**POST:** "/rest/resources"  
**Consume:** Resource (application/json | application/xml | application/x-protobuf)  
Adds a new resource to the root of the resource tree  

**POST:** "rest/resources/{key}  
**Consume:** Resource (application/json | application/xml | application/x-protobuf)  
Adds a new resource as a child to the resource with key = {key}  

**PUT:** "/rest/resources"  
**Consume:** ResourceCollection (application/json | application/xml | application/x-protobuf)  
Replace the current resource list (all resources) with a new list. This will also delete all nodes.  

**PUT:** "/rest/resources/{key}"  
**Consume:** Resource (application/json | application/xml | application/x-protobuf)  
Replace the resource with key = {key}  

**DELETE:** "/rest/resources"  
**Params:** r (resourcekey) *Filter query by repeateateble number of resourcekeys params (r=reskey1&r=reskey2&r=...)*  
**Params:** method ("children"|"parents") *Filter query to provided resources "children"|"parents" or leave blank for normal match. Ignored if "r" params is not provided*  
Removes all resources, or removes resources filtered by arguments. Related nodes to removed resources will be deleted as well.  

**DELETE:** "/rest/resources/{key}"  
Removes resource with key = {key}. Nodes related to the resource will be removed as well.  

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

### Node samples

**POST:** "/rest/samples"  
**Consume:** "application/json", !"application/x-protobuf"  
**Produce:** "application/json", !"application/x-protobuf"  
Tar emot en lista med samples (motsvarar en nodelista med stability som en extra variabel utöver x,y,r). En lista med alla samples returneras, där varje sample-koordinats stabilitetsvärde har räknas ut efter samma principer som för motsvarande node. Observera att ett "sample" inte behöver motsvaras av en verklig node utan kan var fiktiv.

### Simulation instance

**GET:** "/rest/instance"  
**Produce:** "application/json", !"application/x-protobuf"  
Returnerar värdena för den activa världen - name, width, height etc

**!GET:** "/rest/instance/{id}"  
**Produce:** "application/json", !"application/x-protobuf"  
Returnerar värdena för den världen med id = {id} - name, width, height etc. Om detta anrop implementeras bör anropet ovan returnera en lista med tillgängliga "instances".

**POST:**  "/rest/instance"  
**Consume:** "application/json", !"application/x-protobuf"  
Initierar en ny värld baserad på inkommande värden. En ny värld nollställer alla resurser och noder mm. En ny värld/instance blir automatiskt aktiv.

**!PUT:**  "/rest/instance/{id}"  
**Consume:** "application/json", !"application/x-protobuf"  
Updaterar en värld där id = {id} baserat på inkommande värden.

**!DELETE:**  "/rest/instance/"  
Tar bort samtliga instancer.

**!DELETE:**  "/rest/instance/{id}"  
Tar bort instansen med id = {id}.

### Server process

**POST:** "/rest/process"  
**!Consume:** "application/json", "application/x-protobuf"  
**!Produce:** "application/json", "application/x-protobuf"  
Kör simulatorn 1 tick. Anropet kan i framtiden ta emot argument som styr processen (vilka resurser som ska köras, antal tick etc). Som respons kan ett status object skickas tillbaka.

**GET:** "/rest/status"  
**Produce:** "application/json", !"application/x-protobuf"  
Returnerar simulatorns status - name, tick, num-resources, num-nodes etc.

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
**Params:** x, y, w (width), h (height) *Bounds of query.*  
**Params:** r (resourcekey) *Filter query by repeateateble number of resourcekeys params (r=reskey1&r=reskey2&r=...)*  
**Params:** method ("children"|"parents") *Filter query to provided resources "children"|"parents" or leave blank for normal match. Ignored if "r" params is not provided*  
**Produce:** NodeCollection (application/json | application/xml | application/x-protobuf)  
Returns a collection of nodes.  

**POST**	/rest/nodes  
**Consume:** NodeCollection (application/json | application/xml | application/x-protobuf)  
Adds a collection of nodes  

**PUT:** "/rest/nodes"  
**Params:** x, y, w (width), h (height) *Bounds of query*  
**Params:** r (resourcekey) *Filter query by repeateateble number of resourcekeys params (r=reskey1&r=reskey2&r=...)*  
**Params:** method ("children"|"parents") *Filter query to provided resources "children"|"parents" or leave blank for normal match. Ignored if "r" params is not provided*  
**Consume:** NodeCollection (application/json | application/xml | application/x-protobuf)  
Replaces all nodes with provided NodeCollection if no parameters is provided, or delete all nodes matching the provided parameters and add the NodeCollection.  

**DELETE:**	"/rest/nodes"  
**Params:** x, y, w (width), h (height) *Bounds of query*  
**Params:** r (resourcekey) *Filter query by repeateateble number of resourcekeys params (r=reskey1&r=reskey2&r=...)*  
**Params:** method ("children"|"parents") *Filter query to provided resources "children"|"parents" or leave blank for normal match. Ignored if "r" params is not provided*   
Deletes node matched by the provided parameters, or all if no parameters where provided.

**POST:** "/rest/nodes/transaction"  
**Consume:** NodeTransaction (application/json | application/xml | application/x-protobuf)  
Removes and injects nodes in a single transaction. You may leave "injectedNodes" or "deletedNodes" blank.

**POST:** "/rest/nodes/inject" (Obsolite!)  
**POST:** "/rest/nodes/remove" (Obsolite!)  

### Node samples  

**POST:** "/rest/samples"  
**Params:** prospecting (true|false) *Defines whether (false) we want to calculate real node values, or (false) if we would like to calculate fiktive values. If (false) samples without corresponding node will be ignored.  
**Consume:** SampleCollection (application/json | application/xml)  
**Produce:** SampleCollection (application/json | application/xml)  
Returns a sample collection where each samples stability-value has been calculated by the server. A sample shares the same variables as a node with an additional variable "stability". High stability-value indicate a benificial location for the node.  

### World

**GET:** "/rest/world"  
**Produce:** World (application/json | application/xml | application/x-protobuf)  
Returns the current world - name, size, tick etc.  

**PUT:**  "/rest/world"  
**Consume:** World (application/json | application/xml | application/x-protobuf)   
Init a new world based on put data.  

### Server process

**POST:** "/rest/process" 
Runs the simulator 1 tick.

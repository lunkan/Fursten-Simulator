package fursten.rest.providers;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.*;

import com.google.protobuf.AbstractMessage.Builder;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;


@Provider
@Consumes("application/x-protobuf")
@Produces("application/x-protobuf")
public class FurstenProtobufProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>  {

	/*
	 * Mapping protobuff classes to pojo classes
	 * Todo: Use Google reflection lib to automap classes by name and "@XMLRootElement" annotation   
	 */
	public static HashMap<Class, Class> pojoToProtoMapper = new HashMap<Class, Class>();
	public static HashMap<Class, Class> protoToPojoMapper = new HashMap<Class, Class>();
	
	/*
	 * Reflection is slower than standard creation
	 * Todo: Add factory classes to store and reuse classes and methods
	 */
	public FurstenProtobufProvider() {
		
		//Nodes
		pojoToProtoMapper.put(fursten.simulator.node.NodeCollection.class, org.fursten.message.proto.NodeProto.NodeCollection.class);
		pojoToProtoMapper.put(fursten.simulator.node.Node.class, org.fursten.message.proto.NodeProto.Node.class);
		pojoToProtoMapper.put(fursten.simulator.node.NodeTransaction.class, org.fursten.message.proto.NodeProto.NodeTransaction.class);
		
		//Resources
		pojoToProtoMapper.put(fursten.simulator.resource.ResourceCollection.class, org.fursten.message.proto.ResourceProto.ResourceCollection.class);
		pojoToProtoMapper.put(fursten.simulator.resource.Resource.class, org.fursten.message.proto.ResourceProto.Resource.class);
		pojoToProtoMapper.put(fursten.simulator.resource.Resource.Offspring.class, org.fursten.message.proto.ResourceProto.Resource.Offspring.class);
		pojoToProtoMapper.put(fursten.simulator.resource.Resource.Weight.class, org.fursten.message.proto.ResourceProto.Resource.Weight.class);
		pojoToProtoMapper.put(fursten.simulator.resource.Resource.WeightGroup.class, org.fursten.message.proto.ResourceProto.Resource.WeightGroup.class);
		
		//Reverse mapping
		Iterator<Class> it = pojoToProtoMapper.keySet().iterator();
		while(it.hasNext()) {
			Class pojoClass = it.next();
			Class protoClass = pojoToProtoMapper.get(pojoClass);
			protoToPojoMapper.put(protoClass, pojoClass);
		}
		
	}
    
	private String protoVarNameToJava(String protoVarName) {
		
		String javaVarName = "";
		String[] nameSections = protoVarName.split("_");
		for(String nameSection : nameSections) {
			javaVarName += Character.toUpperCase(nameSection.charAt(0)) + nameSection.substring(1);
		}
		
		return javaVarName;
	}
	
    private Object parseMessage(Message m) {
    	
    	Class c = protoToPojoMapper.get(m.getClass());
    	
    	try {
    		Object o = c.newInstance();
    		
    		//SET FIELDS
    		Map<FieldDescriptor, Object> fieldMap = m.getAllFields();
    		Iterator<FieldDescriptor> it = fieldMap.keySet().iterator();
    		while(it.hasNext()) {
    			
    			FieldDescriptor field = it.next();
    			String setMethodName = "set" + protoVarNameToJava(field.getName());
    			Method setMethod = null;
    			Object param = null;
    			
				switch(field.getJavaType()) {
					case MESSAGE: 
						setMethod = o.getClass().getDeclaredMethod(setMethodName, new Class[] { ArrayList.class });
						break;
					case INT:
						setMethod = o.getClass().getDeclaredMethod(setMethodName, new Class[] { int.class });
						break;
					case FLOAT:
						setMethod = o.getClass().getDeclaredMethod(setMethodName, new Class[] { float.class });
						break;
					case LONG:
						setMethod = o.getClass().getDeclaredMethod(setMethodName, new Class[] { long.class });
						break;
					case BOOLEAN:
						setMethod = o.getClass().getDeclaredMethod(setMethodName, new Class[] { boolean.class });
						break;
					case STRING:
						setMethod = o.getClass().getDeclaredMethod(setMethodName, new Class[] { String.class });
						break;
					case DOUBLE:
						setMethod = o.getClass().getDeclaredMethod(setMethodName, new Class[] { double.class });
						break;
				}
				
    			if(field.isRepeated()) {
    				
    				ArrayList<Object> parsedChildren = new ArrayList<Object>(); 
    				List<Message> children = (List<Message>)fieldMap.get(field);
    				
    				for(Message child : children) {
    					parsedChildren.add(parseMessage(child));
    				}
    				
    				param = parsedChildren;
    			}
    			else {
    				param = fieldMap.get(field);
    			}
    			
    			setMethod.invoke(o, param);
    		}
    		
    		return o;
    	}
    	catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	return null;
    }
    
	@Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
		return (pojoToProtoMapper.get(type) != null);
    }
    
    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String,String> httpHeaders, InputStream entityStream) throws IOException
    {
    	Class c = pojoToProtoMapper.get(type);
    	
    	try {
    		Method newBuilder = c.getMethod("newBuilder", null);
        	GeneratedMessage.Builder builder = (GeneratedMessage.Builder) newBuilder.invoke(null, null);
        	Message message = builder.mergeFrom(entityStream).build();
        	return parseMessage(message);
    	}
    	catch(Exception e) {
    		return null;
    	}
    }

    /*
     * WRITER
     */
    
    private Map<Object, byte[]> buffer = new WeakHashMap<Object, byte[]>();
    private byte[] bufferMsg = null;
    
	private Builder getBuilder(Class type) {
    	
		try {
	    	Class buildClass = pojoToProtoMapper.get(type);
			Method  method = buildClass.getDeclaredMethod("newBuilder", null);
			Builder builder	= (Builder)method.invoke(null, null);
			return builder;
		}
		catch(Exception e) {
			System.out.println("could not create build object from: " + type.getName());
			return null;
		}
    }
	
	private Message mapObject(Object o) {
		
		//Init new builder
		Builder b = getBuilder(o.getClass());
		
		//Map Fields
		for (Field f : o.getClass().getDeclaredFields()) {
    		
			try {
				f.setAccessible(true);
				
				if(f.getType().isAssignableFrom(ArrayList.class)) {
    				
    				String methodGetName = "get" + protoVarNameToJava(f.getName());
    				String methodAddName = "add" + protoVarNameToJava(f.getName());
    				
    				Method getMethod = o.getClass().getDeclaredMethod(methodGetName, null);
    				for(Object childObj : (List<Object>)getMethod.invoke(o, null)) {
    				
    					Message childMsg = mapObject(childObj);
    					Method addMethod = b.getClass().getDeclaredMethod(methodAddName, new Class[] { childMsg.getClass() });
        		    	addMethod.invoke(b, childMsg);
    				}
    			}
				else {
					String methodName = "set" + protoVarNameToJava(f.getName());
					Method addMethod = null;
					
					if(f.getType().isAssignableFrom(Integer.TYPE)) 
		    			addMethod = b.getClass().getDeclaredMethod(methodName, new Class[] { int.class });
		    		else if(f.getType().isAssignableFrom(Float.TYPE))
		    			addMethod = b.getClass().getDeclaredMethod(methodName, new Class[] { float.class });
				    else if(f.getType().isAssignableFrom(Long.TYPE))
				    	addMethod = b.getClass().getDeclaredMethod(methodName, new Class[] { long.class });
				    else if(f.getType().isAssignableFrom(Boolean.TYPE))
		    			addMethod = b.getClass().getDeclaredMethod(methodName, new Class[] { boolean.class });
				    else if(f.getType().isAssignableFrom(Double.TYPE))
				    	addMethod = b.getClass().getDeclaredMethod(methodName, new Class[] { double.class });
				    else if(f.getType().isAssignableFrom(String.class))
				    	addMethod = b.getClass().getDeclaredMethod(methodName, new Class[] { String.class });
				    
					addMethod.invoke(b, f.get(o));
				}
	    			
	    		f.setAccessible(false);
    		}
    		catch(Exception e) {
    			if(e.getMessage() != null) {
	    			if(e.getMessage().equals("fursten.simulator.resource.Resource$WeightGroup.getWeights()")) {
	    				System.out.println("!"+e.getMessage());
	    				e.printStackTrace();
	    			}
    			}
    			//It's okey - some fields is not writable
    		}
		}
		
		//Build object
		try {
			Method finalBuildMethod = b.getClass().getDeclaredMethod("build", null);
        	Message message	= (Message)finalBuildMethod.invoke(b, null);
        	return message;
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
    
    public long getSize(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
    	Message message = mapObject(value);
    	
    	try {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.writeTo(baos);
            byte[] bytes = baos.toByteArray();
            bufferMsg = bytes;
            return bytes.length;
    	}
    	catch(Exception e) {
    		System.out.println(e.getMessage());
			e.printStackTrace();
    		return -1;
    	}
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
    	return (pojoToProtoMapper.get(type) != null);
    }

    @Override
    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String,Object> httpHeaders, OutputStream entityStream)  throws IOException
    {
    	entityStream.write(bufferMsg);
    }
}

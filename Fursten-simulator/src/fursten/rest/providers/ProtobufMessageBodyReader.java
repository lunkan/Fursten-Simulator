package fursten.rest.providers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;

@Provider
@Consumes("application/x-protobuff")
public class ProtobufMessageBodyReader implements MessageBodyReader<Message> {

	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		System.out.println("@Reader isReadable 2");
        return Message.class.isAssignableFrom(type);
    }

    public Message readFrom(Class<Message> type, Type genericType, Annotation[] annotations,
                MediaType mediaType, MultivaluedMap<String, String> httpHeaders, 
                InputStream entityStream) throws IOException, WebApplicationException {
    	
    	System.out.println("@Reader readFrom 2");
    	
        try {
        	Method newBuilder = type.getMethod("newBuilder");
            //Method newBuilder = type.getMethod("newBuilder");
            GeneratedMessage.Builder builder = (GeneratedMessage.Builder) newBuilder.invoke(type);
            return builder.mergeFrom(entityStream).build();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

}

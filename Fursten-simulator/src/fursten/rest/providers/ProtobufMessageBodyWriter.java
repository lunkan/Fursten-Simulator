package fursten.rest.providers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.WeakHashMap;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.protobuf.Message;

@Provider
@Produces("application/x-protobuff")
public class ProtobufMessageBodyWriter implements MessageBodyWriter<Message> {

	private Map<Object, byte[]> buffer = new WeakHashMap<Object, byte[]>();
	
	public long getSize(Message m, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    	System.out.println("@Provider getSize");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            m.writeTo(baos);
        } catch (IOException e) {
            return -1;
        }
        byte[] bytes = baos.toByteArray();
        buffer.put(m, bytes);
        return bytes.length;
    }
	
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    	System.out.println("@Provider isWriteable");
        return Message.class.isAssignableFrom(type);
    }

	public void writeTo(Message m, Class type, Type genericType, Annotation[] annotations, 
            MediaType mediaType, MultivaluedMap httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
		System.out.println("@Provider writeTo");
	    entityStream.write(buffer.remove(m));
	}

}
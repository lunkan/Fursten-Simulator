package fursten.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BinaryTranslator {

	public static byte[] objectToBinary(Serializable value) {
		
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			GZIPOutputStream gz = new GZIPOutputStream(buffer);
			ObjectOutputStream oos = new ObjectOutputStream(gz);
			oos.writeObject(value);
			oos.flush();
			oos.close();
			buffer.close();
			
			return buffer.toByteArray();
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public static Serializable binaryToObject(InputStream byteData) {
		
		try {
			GZIPInputStream gs = new GZIPInputStream(byteData);
			ObjectInputStream ois = new ObjectInputStream(gs);
			Serializable value = (Serializable) ois.readObject();
		    ois.close();
		    return value;
		}
		catch(Exception e) {
			return null;
		}
	}
}
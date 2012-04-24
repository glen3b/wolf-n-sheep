package org.glen_h.games.wolfnsheep;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SerializerClass {
	/**
	 * Make an MD5 hash
	 * @param in The input string
	 * @return The MD5 hash of in
	 * @author Glen Husman
	 */
	public static String md5(String in) {
	    MessageDigest digest;
	    try {
	        digest = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
	        digest.reset();
	        digest.update(in.getBytes());
	        byte[] a = digest.digest();
	        int len = a.length;
	        StringBuilder sb = new StringBuilder(len << 1);
	        for (int i = 0; i < len; i++) {
	            sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
	            sb.append(Character.forDigit(a[i] & 0x0f, 16));
	        }
	        return sb.toString();
	    } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
	    return null;
	}
	
	/**
	 * Serialize an {@link java.lang.Object Object} into a byte[].
	 * @param o Object to serialize
	 * @return The serialized form of o
	 * @author Glen Husman
	 */
	public static byte[] serializeObject(Object o) { 
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	 
	    try { 
	      ObjectOutput out = new ObjectOutputStream(bos); 
	      out.writeObject(o); 
	      out.close(); 
	 
	      // Get the bytes of the serialized object 
	      byte[] buf = bos.toByteArray(); 
	 
	      return buf; 
	    } catch(IOException ioe) { 
	      return null; 
	    } 
	  }
	
	/**
	 * De-serialize a byte[] into an {@link java.lang.Object Object}.
	 * @param b Byte array to deserialize
	 * @return The deserialized form of b
	 * @author Glen Husman
	 */
	public static Object deserializeObject(byte[] b) { 
	    try { 
	      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b)); 
	      Object object = in.readObject(); 
	      in.close(); 
	 
	      return object; 
	    } catch(ClassNotFoundException cnfe) { 
	      return null; 
	    } catch(IOException ioe) { 	 
	      return null; 
	    } 
	}
}

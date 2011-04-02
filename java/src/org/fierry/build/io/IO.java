package org.fierry.build.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class IO {

	static public void serialize(Serializable object, File file) {
		try {
			if(!file.exists() && !file.createNewFile()) { throw new IllegalStateException("Couldn't create new File: " + file.getAbsolutePath()); }
			
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			try { out.writeObject(object); }
			finally { out.close(); }
			
		} catch(IOException e) { throw new RuntimeException(e); } 
	}
	
	static public<T> T deserialize(Class<T> cls, File file) {
		try {
			T object = null;
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			
			try { object = cls.cast(in.readObject()); }
			catch(ClassNotFoundException e) { throw new RuntimeException(e); }
			
			finally { in.close(); }
			return object;
		}
		catch(FileNotFoundException e ) { return null; }
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
}

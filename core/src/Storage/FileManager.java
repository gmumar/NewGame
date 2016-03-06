package Storage;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import JSONifier.JSONCar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;

public class FileManager {

	public final static String FILE_NAME = "myfile.txt";
	public final static String CAR_FILE_NAME = "mycarfile.txt";

	public static void writeToFile(FileObject objectIn) {

		FileObject objectCur = null;
		Json json = new Json();
		String strToWrite;

		FileHandle handle = Gdx.files.external(FILE_NAME);

		if (handle != null && handle.exists()) {
			objectCur = json.fromJson(FileObject.class, handle.readString());
			objectCur.append(objectIn);
			strToWrite = json.toJson(objectCur);
		} else {
			strToWrite = json.toJson(objectIn);
		}

		// System.out.println( strToWrite);

		handle.writeString(strToWrite, false);
	}
	
	public static void writeToFileGson(ArrayList<JSONCar> objectIn) {

		ArrayList<JSONCar> objectCur = null;
		Gson json = new Gson();
		String strToWrite;

		FileHandle handle = Gdx.files.external(CAR_FILE_NAME);

	/*	if (handle != null && handle.exists()) {
			objectCur = json.fromJson(getFileStream(),FileObject.class);
			objectCur.append(objectIn);
			strToWrite = json.toJson(objectCur);
		} else {
			*/
			//for (JSONCar car : objectIn) {
				strToWrite = json.toJson(objectIn);
				handle.writeString(strToWrite ,true);
			//}
			
		//}

		// System.out.println( strToWrite);

		//handle.writeString(strToWrite, false);
	}
	
	public static void writeToFileGson(FileObject objectIn) {

		FileObject objectCur = null;
		Gson json = new Gson();
		String strToWrite;

		FileHandle handle = Gdx.files.external(FILE_NAME);

		if (handle != null && handle.exists()) {
			objectCur = json.fromJson(getFileStream(FILE_NAME),FileObject.class);
			objectCur.append(objectIn);
			strToWrite = json.toJson(objectCur);
		} else {
			strToWrite = json.toJson(objectIn);
		}

		// System.out.println( strToWrite);

		handle.writeString(strToWrite, false);
	}

	public static FileObject readFromFile() {

		FileObject objectCur = new FileObject();
		Json json = new Json();

		FileHandle handle = Gdx.files.external(FILE_NAME);

		if (handle != null && handle.exists()) {
			objectCur = json.fromJson(FileObject.class, handle.readString());
			return objectCur;

		}

		return objectCur;
	}

	public static Reader getFileStream(String fileName) {

		FileHandle handle = Gdx.files.external(fileName);
		
		if(!handle.exists()) return null;
		
		InputStream input = handle.read();
		Reader reader = null;
		try {
			reader = new InputStreamReader(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return reader;
	}

}

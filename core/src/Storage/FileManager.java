package Storage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

import JSONifier.JSONCar;
import RESTWrapper.Gzip;

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

	public static void writeToFileBoon(ArrayList<JSONCar> objectIn) {

		JSONCarList carList = new JSONCarList();
		carList.carList = objectIn;
		ObjectMapper mapper = JsonFactory.create();

		String strToWrite;

		FileHandle handle = Gdx.files.external(CAR_FILE_NAME);

		strToWrite = mapper.toJson(carList);
		try {
			handle.writeBytes(Gzip.compress(strToWrite), false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void writeToFileGson(ArrayList<JSONCar> objectIn) {

		Gson json = new Gson();
		String strToWrite;

		FileHandle handle = Gdx.files.external(CAR_FILE_NAME);

		strToWrite = json.toJson(objectIn);
		// handle.writeString(strToWrite, false);

		try {
			handle.writeBytes(Gzip.compress(strToWrite), false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void writeToFileGson(FileObject objectIn) {

		FileObject objectCur = null;
		Gson json = new Gson();
		String strToWrite;

		FileHandle handle = Gdx.files.external(FILE_NAME);

		if (handle != null && handle.exists()) {
			objectCur = json.fromJson(getFileStream(FILE_NAME),
					FileObject.class);
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

		if (!handle.exists())
			return null;

		InputStream input = handle.read();
		Reader reader = null;
		try {

			reader = new InputStreamReader(new BufferedInputStream(
					new GZIPInputStream(new FileInputStream(handle.file()))),
					"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reader;
	}

}

package Storage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import wrapper.Globals;
import DataMutators.Gzip;
import JSONifier.JSONCar;
import JSONifier.JSONTrack;
import UserPackage.User;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;

public class FileManager {

	public final static String CAR_FILE_NAME = "mycarfile_v" + Globals.FILE_VERSION + ".dat";
	public final static String FORREST_TRACK_FILE_NAME = "mytrackfile2_v" + Globals.FILE_VERSION + ".dat";
	public final static String ARTIC_TRACK_FILE_NAME = "mytrackfile3_v" + Globals.FILE_VERSION + ".dat";
	public final static String INFINITE_TRACK_FILE_NAME = "mytrackfile1_v" + Globals.FILE_VERSION + ".dat";
	public static final String COMMUNITY_FILE_NAME = "mycommcarfile_v" + Globals.FILE_VERSION + ".dat";

	/*
	 * private static void writeToFile(FileObject objectIn) {
	 * 
	 * FileObject objectCur = null; Gson json = new Gson(); String strToWrite;
	 * 
	 * FileHandle handle = Gdx.files.external("");
	 * 
	 * if (handle != null && handle.exists()) { objectCur =
	 * json.fromJson(handle.readString(), FileObject.class);
	 * objectCur.append(objectIn); strToWrite = json.toJson(objectCur); } else {
	 * strToWrite = json.toJson(objectIn); }
	 * 
	 * handle.writeString(strToWrite, false); }
	 */

	public static void writeCarsToFileGson(ArrayList<JSONCar> objectIn,
			String fileName, Long lastCreationTimeStamp) {

		Gson json = new Gson();
		writeToFileGson(fileName, json.toJson(objectIn), lastCreationTimeStamp);

	}

	public static void writeTracksToFileGson(ArrayList<JSONTrack> objectIn,
			String fileName, Long lastCreationTimeStamp) {

		Gson json = new Gson();
		writeToFileGson(fileName, json.toJson(objectIn), lastCreationTimeStamp);

	}

	private static void writeToFileGson(String fileName, String strToWrite,
			Long lastCreationTimeStamp) {

		FileHandle handle = Gdx.files.external(fileName);

		try {
			handle.writeBytes(Gzip.compressToBytes(strToWrite), false);
			User.getInstance().saveFileTimeStamp(fileName,
					lastCreationTimeStamp.toString());
			User.getInstance().saveFileMD5(fileName, checkSum(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/*
	 * public static void writeToFileGson(FileObject objectIn) {
	 * 
	 * FileObject objectCur = null; Gson json = new Gson(); String strToWrite;
	 * 
	 * FileHandle handle = Gdx.files.external(FILE_NAME);
	 * 
	 * if (handle != null && handle.exists()) { objectCur =
	 * json.fromJson(getFileStream(FILE_NAME), FileObject.class);
	 * objectCur.append(objectIn); strToWrite = json.toJson(objectCur); } else {
	 * strToWrite = json.toJson(objectIn); }
	 * 
	 * handle.writeString(strToWrite, false); }
	 * 
	 * public static FileObject readFromFile() {
	 * 
	 * FileObject objectCur = new FileObject(); Gson json = new Gson();
	 * 
	 * FileHandle handle = Gdx.files.external(FILE_NAME);
	 * 
	 * if (handle != null && handle.exists()) { objectCur =
	 * json.fromJson(handle.readString(), FileObject.class); return objectCur;
	 * 
	 * }
	 * 
	 * return objectCur; }
	 */

	public static Reader getFileStream(String fileName) {

		FileHandle handle = Gdx.files.external(fileName);

		if (!handle.exists()){
			return null;
		}

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
	
	public static boolean validateFileState(String fileName){
		User user = User.getInstance();
		
		String md5 = FileManager.checkSum(fileName);

		if (md5.isEmpty()) {
			// File missing
			user.saveFileTimeStamp(fileName, "0");
			System.out.println("FileManger: file missing");
			return false;
		} else {
			if (md5.compareTo(user.getFileMD5(fileName)) != 0) {
				// File corrupted
				user.saveFileTimeStamp(fileName, "0");
				System.out.println("FileManger: file corrupted");
				return false;
			}

		}
		System.out.println("FileManger: file Ok");
		return true;
	}

	public static String checkSum(String path) {
		String checksum = "";
		try {
			FileHandle handle = Gdx.files.external(path);
			FileInputStream fis = new FileInputStream(handle.file());
			MessageDigest md = MessageDigest.getInstance("MD5");

			// Using MessageDigest update() method to provide input
			byte[] buffer = new byte[8192];
			int numOfBytesRead;
			while ((numOfBytesRead = fis.read(buffer)) > 0) {
				md.update(buffer, 0, numOfBytesRead);
			}
			byte[] hash = md.digest();
			checksum = new BigInteger(1, hash).toString(16); // don't use this,
																// truncates
																// leading zero
		} catch (Exception ex) {
			
		}
		return checksum;
	}
}

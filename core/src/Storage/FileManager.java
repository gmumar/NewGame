package Storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class FileManager {
	
	private final static String FILE_NAME = "myfile.txt";
	
	public static void writeToFile(FileObject objectIn){
		
		FileObject objectCur = null;
		Json json = new Json();
		String strToWrite;
		
		FileHandle handle = Gdx.files.external(FILE_NAME);
		
		if(handle!=null && handle.exists()){
			objectCur = json.fromJson(FileObject.class, handle.readString());
			objectCur.append(objectIn);
			strToWrite = json.toJson(objectCur);
		}else{
			strToWrite = json.toJson(objectIn);
		}

		System.out.println( strToWrite);
		
		handle.writeString(strToWrite, false);
	}

	public static FileObject readFromFile() {
		
		FileObject objectCur = new FileObject();
		Json json = new Json();
		
		FileHandle handle = Gdx.files.external(FILE_NAME);
		
		if(handle!=null && handle.exists()){
			objectCur = json.fromJson(FileObject.class, handle.readString());
			return objectCur;

		}
		
		return objectCur;
	}
	
}

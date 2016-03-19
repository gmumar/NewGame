package JSONifier;

public class Properties{
	//	ROTATION, POSITION, MOTOR, TYPE
	static public final String ROTATION = "RT";
	static public final String POSITION = "PS";
	static public final String MOTOR = "MT";
	static public final String TYPE = "TP";
	
	public static String makePostionString(float x, float y){
		return Float.toString(x) + "," + Float.toString(y);
	}
	
}


	

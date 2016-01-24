package PraseWrapper;

import org.parse4j.Parse;
import org.parse4j.ParseException;
import org.parse4j.ParseObject;

public class ParseManager {

	private static final String APPLICATION_ID = "EZtg50LS9I3uQa9Vk4pAMwrh6X62qXfhCHR9LUav";
	private static final String REST_API_KEY = "dTSLfLnhVVDShbF6QcYHl1olkY2mVNbdvEkEAH2k";

	public ParseManager() {
		super();
		Parse.initialize(APPLICATION_ID, REST_API_KEY);

		ParseObject gameScore = new ParseObject("GameScore");
		gameScore.put("score", 1337);
		gameScore.put("playerName", "Sean Plott");
		gameScore.put("cheatMode", false);

		gameScore.saveInBackground();

	}
	
	public void postInBackground(ParseObject obj){
		obj.saveInBackground();
	}
	
	
	public void post(ParseObject obj){
		try {
			obj.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}

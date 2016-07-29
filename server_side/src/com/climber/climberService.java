package com.climber;

import java.sql.Date;
import java.util.List;

import org.json.simple.JSONObject;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.servercode.IBackendlessService;
import com.climber.models.challenges;
import com.climber.models.game_users;
import com.climber.models.submitedChallenge;

public class climberService implements IBackendlessService {

	public void printHello() {
		System.out.println("Hello thr");
	}

	public void printText(String text) {
		System.out.println("--> " + text);
	}
	
	public String addsubmitChallenge(submitedChallenge challenge) {
		
		JSONObject ret = new JSONObject();
		
		String targetUser = challenge.targetUser;
		
		System.out.println("Submitting challenge " + challenge.targetUser);
		// Check if target user exists
		String whereClause = "userName = '" + targetUser + "'";
		BackendlessDataQuery dataQuery = new BackendlessDataQuery();
		dataQuery.setWhereClause(whereClause);
		BackendlessCollection<game_users> result = Backendless.Persistence.of(
				game_users.class).find(dataQuery);
		
		if(result.getData().isEmpty()){
			// targetUser not found entry failed
			ret.put("status", "1");
			//System.out.println("Failed");
		} else {
			ret.put("status", "0");
			//System.out.println("passed");
			challenges newChallenge = new challenges();
			newChallenge.setBestTime(Double.parseDouble(challenge.bestTime));
			newChallenge.setChallenge(challenge.challenge);
			newChallenge.setChallengeReward(Integer.parseInt(challenge.challengeReward));
			newChallenge.setSourceUser(challenge.sourceUser);
			newChallenge.setTargetUser(challenge.targetUser);
			
			Backendless.Persistence.save(newChallenge);
			
		}
		
		System.out.println("returning: " + ret.toJSONString());
		
		return ret.toJSONString();
	}

	public String getMoneyDelta(String objectId) {
		
		//System.out.println("called with " + objectId);
	
		JSONObject obj = new JSONObject();
		
		// STATUS
		// 0 no errors read money
		// 1 user not found
		
		
		String whereClause = "objectId = '" + objectId + "'";
		BackendlessDataQuery dataQuery = new BackendlessDataQuery();
		dataQuery.setWhereClause(whereClause);
		BackendlessCollection<game_users> result = Backendless.Persistence.of(
				game_users.class).find(dataQuery);

		List<game_users> listToRemove = result.getData();
		for (game_users user : listToRemove) {
			Integer money = user.getMoneyDelta() ;
			user.setMoneyDelta(0);
			user.setLastActivity(new Date(System.currentTimeMillis()));
			
			Backendless.Persistence.save(user);
			
			
			obj.put("status", "0");
			obj.put("money", money.toString());
			break;
		}
		
		if(listToRemove.isEmpty()){
			obj.put("status", "1");
			obj.put("money", "0");
		}
		
		
		return obj.toJSONString();
		
	}

}

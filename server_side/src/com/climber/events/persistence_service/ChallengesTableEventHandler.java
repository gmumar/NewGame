package com.climber.events.persistence_service;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.servercode.ExecutionResult;
import com.backendless.servercode.RunnerContext;
import com.backendless.servercode.annotation.Asset;
import com.climber.models.game_users;

/**
 * ChallengesTableEventHandler handles events for all entities. This is
 * accomplished with the @Asset( "challenges" ) annotation. The methods in the
 * class correspond to the events selected in Backendless Console.
 */

@Asset("*")
public class ChallengesTableEventHandler extends
		com.backendless.servercode.extension.PersistenceExtender<HashMap> {

	@Override
	public void afterUpdate(RunnerContext context, HashMap challenges,
			ExecutionResult<HashMap> result) throws Exception {

		String winner = (String) result.getResult().get("winner");
		String sourceUser = (String) result.getResult().get("sourceUser");
		Integer winnings = (Integer) result.getResult().get("challengeReward");

		//System.out.println("sourceUser: " + sourceUser + " Winner: " + winner + " prize " + winnings);

		// target user has been rewarded
		if (!winner.isEmpty()) {
			if (winner.compareTo("TARGET") == 0) {
				// target won so source will loose money
				updateUser(sourceUser, -winnings);
			} else if (winner.compareTo("SOURCE") == 0) {
				// source won so source will gain money
				updateUser(sourceUser, winnings);
			} else if (winner.compareTo("NONE") == 0) {
				updateUser(sourceUser, winnings / 2);
			}
		}

	}

	private void updateUser(String userName, Integer money) {
		String whereClause = "userName = '" + userName + "'";
		BackendlessDataQuery dataQuery = new BackendlessDataQuery();
		dataQuery.setWhereClause(whereClause);
		BackendlessCollection<game_users> result = Backendless.Persistence.of(
				game_users.class).find(dataQuery);

		List<game_users> listToRemove = result.getData();
		for (game_users user : listToRemove) {
			user.setMoneyDelta(user.getMoneyDelta() + money);
			user.setLastActivity(new Date(System.currentTimeMillis()));
			
			Backendless.Persistence.save(user);
			break;
		}
	}

}

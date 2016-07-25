package com.climber.timers;

import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.servercode.annotation.BackendlessTimer;
import com.climber.models.Challenges;
import com.climber.models.game_users;

/**
 * Delete_usersTimer is a timer. It is executed according to the schedule
 * defined in Backendless Console. The class becomes a timer by extending the
 * TimerExtender class. The information about the timer, its name, schedule,
 * expiration date/time is configured in the special annotation -
 * BackendlessTimer. The annotation contains a JSON object which describes all
 * properties of the timer.
 */
@BackendlessTimer("{'startDate':1469329140000,'frequency':{'schedule':'custom','repeat':{'every':3600}},'timername':'delete_users'}")
public class Delete_usersTimer extends
		com.backendless.servercode.extension.TimerExtender {

	@Override
	public void execute(String appVersionId) throws Exception {
		long threeDaysAgo = System.currentTimeMillis() - (3600 * 72 * 1000);

		String whereClause = "lastActivity < " + Long.toString(threeDaysAgo);
		BackendlessDataQuery dataQuery = new BackendlessDataQuery();
		dataQuery.setWhereClause(whereClause);
		BackendlessCollection<game_users> result = Backendless.Persistence.of(
				game_users.class).find(dataQuery);

		List<game_users> listToRemove = result.getData();

		for (game_users user : listToRemove) {
			Backendless.Persistence.of(game_users.class).remove(user);
		}
		
		
		long oneDaysAgo = System.currentTimeMillis() - (3600 * 24 * 1000);

		whereClause = "created < " + Long.toString(oneDaysAgo);
		dataQuery = new BackendlessDataQuery();
		dataQuery.setWhereClause(whereClause);
		BackendlessCollection<Challenges> result2 = Backendless.Persistence.of(
				Challenges.class).find(dataQuery);

		List<Challenges> listToRemove2 = result2.getData();

		for (Challenges challenge : listToRemove2) {
			Backendless.Persistence.of(Challenges.class).remove(challenge);
		}
		
		
	}

}

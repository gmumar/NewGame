package com.climber;

import java.sql.Date;
import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.servercode.IBackendlessService;
import com.climber.models.game_users;

public class climberService implements IBackendlessService {

	public void printHello() {
		System.out.println("Hello thr");
	}

	public void printText(String text) {
		System.out.println("--> " + text);
	}

	public Integer getMoneyDelta(String objectId) {
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
			
			return money;
		}
		
		return 0;
		
	}

}

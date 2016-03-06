package wrapper;

import User.User;

import com.gudesigns.climber.GameLoader;

public class GameState {

	private GameLoader gameLoader;
	private User user;
	
	public GameState(GameLoader gameLoader, User user) {
		super();
		this.gameLoader = gameLoader;
		this.user = user;
	}
	
	public GameLoader getGameLoader() {
		return gameLoader;
	}
	public void setGameLoader(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
	}
	public void setUser(User user){
		this.user = user;
	}
	public User getUser(){
		return user;
	}
	
}

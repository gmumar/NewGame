package wrapper;

import com.badlogic.gdx.physics.box2d.World;
import com.gudesigns.climber.GameLoader;

public class GamePhysicalState {

	private World world;
	private GameLoader gameLoader;
	public GamePhysicalState(World world, GameLoader gameLoader) {
		super();
		this.world = world;
		this.gameLoader = gameLoader;
	}
	

	public World getWorld() {
		return world;
	}
	public void setWorld(World world) {
		this.world = world;
	}
	public GameLoader getGameLoader() {
		return gameLoader;
	}
	public void setGameLoader(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
	}
	
}

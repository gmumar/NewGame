package wrapper;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameViewport extends ExtendViewport {

	public GameViewport(float worldWidth, float worldHeight, Camera camera) {
		super(worldWidth, worldHeight, camera);
		this.apply();
		// TODO Auto-generated constructor stub
	}

}

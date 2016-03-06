package ParallexBackground;

import wrapper.Globals;
import Assembly.AssembledObject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.gudesigns.climber.GameLoader;

public class ScrollingBackground {

	Texture background;
	AssembledObject object;
	
	ParallaxBackground rbg;
	
	public ScrollingBackground(GameLoader gameLoader, AssembledObject obj) {
		//this.background = gameState.getGameLoader().Assets.get("temp_background1.png",Texture.class);
		//this.camera = camera;
		
		rbg = new ParallaxBackground(new ParallaxLayer[]{
	            new ParallaxLayer(
	            		new TextureRegion(gameLoader.Assets.get("temp_background1.png",Texture.class))
	            		,new Vector2(1,0)
	            		,new Vector2(-0, 50)
	            		,new Vector2(0,0)),
        		new ParallaxLayer(
	            		new TextureRegion(gameLoader.Assets.get("temp_background2.png",Texture.class))
	            		,new Vector2(7,0)
	            		,new Vector2(-0, -10)
	            		,new Vector2(-6,0)),
	      }, Globals.ScreenWidth, Globals.ScreenHeight*3/4);
		
		this.object = obj;
	}

	public void draw() {
		//batch.draw(background, camera.position.x-20, camera.position.y-5,100,20);
		rbg.render(object.getSpeed());
	}

}

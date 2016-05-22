package ParallexBackground;

import wrapper.Globals;
import Assembly.AssembledObject;
import JSONifier.JSONTrack.TrackType;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.gudesigns.climber.GameLoader;

public class ScrollingBackground {
	
	public enum BackgroundType {NORMAL, STATIONARY, SCROLLING, SELECTOR};

	Texture background;
	AssembledObject object;
	
	ParallaxBackground rbg;
	
	public ScrollingBackground(GameLoader gameLoader, AssembledObject obj, TrackType trackType, BackgroundType bgType) {
		//this.background = gameState.getGameLoader().Assets.get("temp_background1.png",Texture.class);
		//this.camera = camera;
		
		String mountains = null, hills = null;
		
		if(trackType == TrackType.FORREST){
			mountains = "worlds/forrest/mountains.png";
			hills = "worlds/forrest/hills.png";
		} else if (trackType == TrackType.ARTIC) {
			mountains = "worlds/artic/mountains.png";
			hills = "worlds/artic/hills.png";
		}
		
		if(bgType == BackgroundType.SELECTOR){
			rbg = new ParallaxBackground(new ParallaxLayer[]{
		            new ParallaxLayer(
		            		new TextureRegion(gameLoader.Assets.getFilteredTexture("worlds/gradient.png"))
		            		,new Vector2(0,0)
		            		,new Vector2(-50, 330)
		            		,new Vector2(0,400)),
	        		new ParallaxLayer(
		            		new TextureRegion(gameLoader.Assets.get(hills, Texture.class))
		            		,new Vector2(7,0)
		            		,new Vector2(0, -330)
		            		,new Vector2(-5,400)),
		      }, Globals.ScreenWidth*1.3f, Globals.ScreenHeight*1.3f);
			
		} else {//
			rbg = new ParallaxBackground(new ParallaxLayer[]{
					
		            new ParallaxLayer(
		            		new TextureRegion(gameLoader.Assets.getFilteredTexture("worlds/gradient.png"))
		            		,new Vector2(0,0)
		            		,new Vector2(-50, 330)
		            		,new Vector2(0,400)),
		            		
		            new ParallaxLayer(
		            		new TextureRegion(gameLoader.Assets.getFilteredTexture(mountains))
		            		,new Vector2(1,0)
		            		,new Vector2(0, 130)
		            		,new Vector2(-5,400)),
	
	        		new ParallaxLayer(
		            		new TextureRegion(gameLoader.Assets.getFilteredTexture(hills))
		            		,new Vector2(7,0)
		            		,new Vector2(0, -170)
		            		,new Vector2(-5,400)),
		      }, Globals.ScreenWidth*1.3f, Globals.ScreenHeight*1.3f);
			
		}

		this.object = obj;
	}

	public void draw(BackgroundType type) {
		//batch.draw(background, camera.position.x-20, camera.position.y-5,100,20);
		if(type == BackgroundType.STATIONARY){
			rbg.render(new Vector2(0,0));
		} else if(type == BackgroundType.NORMAL){
			rbg.render(object.getSpeed());	
		} else if(type == BackgroundType.SCROLLING){
			rbg.render(new Vector2(0,0));	
		}
		
	}

}

package com.gudesigns.climber;

import wrapper.CameraManager;
import wrapper.Globals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SplashScreen implements Screen {

	GameLoader gameLoader;
	CameraManager camera, secondCamera;
	SpriteBatch batch;
	Stage stage;
	FitViewport vp;
	
	
	float time = 0;
	
	public class SplashActor extends Actor {
        Texture texture = gameLoader.Assets.get("colooorsssxcf.png", Texture.class);
        public boolean started = false;

        public SplashActor(){
            setBounds(getX(),getY(),texture.getWidth(),texture.getHeight());
        }
        
        @Override
        public void draw(Batch batch, float alpha){
            batch.draw(texture,this.getX(),getY(),getWidth(),getHeight());
        }
    }

	public SplashScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;

		initStage();
		
		SplashActor splashActor = new SplashActor();
		splashActor.setSize(Globals.ScreenWidth*1/6, Globals.ScreenWidth*1/6);
		splashActor.setX(Globals.ScreenWidth*5/12);
		splashActor.setY(Globals.ScreenHeight*2/5 - 20);

		
	    MoveToAction moveAction = new MoveToAction();
        moveAction.setPosition(Globals.ScreenWidth*5/12, Globals.ScreenHeight*2/5);
        moveAction.setDuration(0.5f);
        
        
        splashActor.addAction(Actions.sequence(Actions.fadeOut(0.1f),moveAction , Actions.fadeIn(1.5f)));
		
		stage.addActor(splashActor);

	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new FitViewport(Globals.ScreenWidth, Globals.ScreenHeight, camera);
		batch = new SpriteBatch();
		stage = new Stage(vp);

	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = Globals.PixelToMeters(width);
		camera.viewportHeight = Globals.PixelToMeters(height);
		camera.update();
		vp.update(width, height);

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		Globals.updateScreenInfo();

	}

	@Override
	public void render(float delta) {
		renderWorld();
		
		time += delta;
		if(time > 0.5f){
			gameLoader.setScreen(new MainMenuScreen(gameLoader));
		}
		
	}

	private void renderWorld() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		  Gdx.gl20.glEnable(GL20.GL_BLEND);
	        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		batch.setProjectionMatrix(camera.combined);
		stage.draw();
		stage.act(Gdx.graphics.getDeltaTime());

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}

package com.gudesigns.climber;

import java.util.ArrayList;

import wrapper.CameraManager;
import wrapper.Globals;
import Menu.Button;
import Menu.TableW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class TrackSelectorScreen implements Screen {

	GameLoader gameLoader;
	CameraManager camera,secondCamera;
	SpriteBatch batch;
	Stage stage;
	FitViewport vp;
	
	int buttonCount = 100;
	ArrayList<Button> buttons = new ArrayList<Button>();
	TableW tracksTable;
	ScrollPane scrollPane;

	public TrackSelectorScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		initStage();
		
		initTrackSelector();
		initButtons();
		
		tracksTable.invalidate();
		scrollPane.invalidate();
		
	}
	
	private void initTrackSelector(){
		tracksTable = new TableW();
		tracksTable.setRotation(-20);
		//tracksTable.setFillParent(true);
		//tracksTable.align(Align.center);
		
		
		scrollPane = new ScrollPane(tracksTable);
		//scrollPane.setHeight(Globals.ScreenHeight);
		//scrollPane.setWidth(Globals.ScreenWidth);
		//scrollPane.setOrigin(0, 0);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setFillParent(true);
		scrollPane.setLayoutEnabled(true);
		
		stage.addActor(scrollPane);
	}

	private void initButtons() {
		
		Button b;

		for(int i=0;i<buttonCount;i++){
			b = new Button("button " + i);
			b.setRotation(-20);
			b.setWidth(1000);
			b.invalidate();
			

			buttons.add(b);

			
			tracksTable.add(b);
			tracksTable.row();
			
		}
		
	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth,
				Globals.ScreenHeight);
		camera.update();

		vp = new FitViewport(Globals.ScreenWidth,
				Globals.ScreenHeight, camera);
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
		
	}

	private void renderWorld() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

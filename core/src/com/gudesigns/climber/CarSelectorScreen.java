package com.gudesigns.climber;

import java.util.ArrayList;

import wrapper.CameraManager;
import wrapper.Globals;
import Menu.Button;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.TableW;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CarSelectorScreen implements Screen {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private FitViewport vp;
	
	private int buttonCount = 100;
	private ArrayList<Button> buttons = new ArrayList<Button>();
	private TableW tracksTable;
	private ScrollPane scrollPane;
	private PopQueManager popQueManager;

	public CarSelectorScreen(GameLoader gameLoader) {
		initStage();
		
		//initTrackSelector();
		//initButtons();
		
		//tracksTable.invalidate();
		//scrollPane.invalidate();
		
		popQueManager = new PopQueManager(stage);
		popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));
		
		downloadCars();
		
		popQueManager.push(new PopQueObject(PopQueObjectType.DELETE));
		
	}
	
	private void downloadCars() {
		REST.getData(RESTPaths.CARS + 
				RESTProperties.URL_ARG_SPLITTER + RESTProperties.PROPS + 
				RESTProperties.CREATED + RESTProperties.ARG_ARG_SPLITTER +
				RESTProperties.CAR_JSON
				, new HttpResponseListener() {
			
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				Backendless_JSONParser.processDownloadedCars(httpResponse.getResultAsString());
				System.out.println(httpResponse.getResultAsString());
				
			}
			
			@Override
			public void failed(Throwable t) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void cancelled() {
				// TODO Auto-generated method stub
				
			}
		});
		
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
		popQueManager.update(delta);
		
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

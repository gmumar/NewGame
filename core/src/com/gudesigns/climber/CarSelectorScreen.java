package com.gudesigns.climber;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.CameraManager;
import wrapper.GamePreferences;
import wrapper.Globals;
import Menu.Button;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.TableW;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.Backendless_Object;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CarSelectorScreen implements Screen {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private FitViewport vp;
	
	private ArrayList<Button> buttons = new ArrayList<Button>();
	private TableW tracksTable;
	private ScrollPane scrollPane;
	private PopQueManager popQueManager;
	private GameLoader gameLoader;
	
	private Button exit;
	private Preferences prefs = Gdx.app
			.getPreferences(GamePreferences.CAR_PREF_STR);

	public CarSelectorScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		initStage();
		
		initNavigationButtons();
		
		popQueManager = new PopQueManager(stage);
		popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));
		
		downloadCars();
		
	}
	
	private void initNavigationButtons() {
		exit = new Button("exit") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new MainMenuScreen(gameLoader));
			}
		};

		exit.setPosition(0, 0);
		stage.addActor(exit);
		
	}

	private void downloadCars() {

		REST.getData(RESTPaths.CARS + 
				RESTProperties.URL_ARG_SPLITTER + RESTProperties.PROPS + 
				RESTProperties.CREATED + RESTProperties.ARG_ARG_SPLITTER +
				RESTProperties.CAR_JSON
				, new HttpResponseListener() {
			
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				String response = httpResponse.getResultAsString();
				
				System.out.println("---results " + response);
				Backendless_Object obj = Backendless_JSONParser.processDownloadedCars(response);
				
				Iterator<String> iter = obj.getData().iterator();
				
				while(iter.hasNext()){
					final String car = iter.next();
					System.out.println(car);
					Globals.runOnUIThread(new Runnable() {
						
						@Override
						public void run() {
							addButton(car);
							
						}
					});
					
				}
				
				popQueManager.push(new PopQueObject(PopQueObjectType.DELETE));
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

	private void initCarSelector(){
		tracksTable = new TableW();
		//tracksTable.setRotation(-20);
		//tracksTable.setFillParent(true);
		//tracksTable.align(Align.center);
		
		
		scrollPane = new ScrollPane(tracksTable);
		//scrollPane.setHeight(Globals.ScreenHeight);
		//scrollPane.setWidth(Globals.ScreenWidth);
		//scrollPane.setOrigin(0, 0);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setFillParent(true);
		scrollPane.setLayoutEnabled(true);
		scrollPane.setTouchable(Touchable.enabled);
		
		stage.addActor(scrollPane);
	}

	private void initButtons() {
		
		Button b;
		
		Iterator<Button> iter = buttons.iterator();
		
		int count = 0;

		while(iter.hasNext()){
			b = iter.next();
			//b.setRotation(-20);
			//b.setWidth(1000);
			b.invalidate();

			tracksTable.add(b);
			
			count++;
			if(count >2){
				tracksTable.row();
				count = 0;
			}
			
		}
		
	}
	
	private void refreshAllButtons(){
		initCarSelector();
		initButtons();
		
		tracksTable.invalidate();
		scrollPane.invalidate();
		
		initNavigationButtons();
	}
	
	
	private void addButton(final String text){
		Button b = new Button("bla"){

			@Override
			public void Clicked() {
				prefs.putString(GamePreferences.CAR_MAP_STR, text);
				prefs.flush();

				super.Clicked();
			}
			
		};

		buttons.add(b);
		
		refreshAllButtons();
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
		
		/*if(!buttonQue.isEmpty()){
			while(!buttonQue.isEmpty()){
				System.out.println("here");
				tempButton = buttonQue.get(0);
				buttonQue.remove(0);
				buttons.add(tempButton);
			}
			
			reinitScroll();
		}*/
		
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

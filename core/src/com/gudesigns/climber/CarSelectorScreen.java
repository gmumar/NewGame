package com.gudesigns.climber;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import wrapper.CameraManager;
import wrapper.GamePreferences;
import wrapper.GameState;
import wrapper.Globals;
import Assembly.Assembler;
import JSONifier.JSONCar;
import Menu.Button;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.TableW;
import RESTWrapper.Backendless_Car;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import Storage.FileManager;
import Storage.FileObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class CarSelectorScreen implements Screen {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private StretchViewport vp;

	// private ArrayList<Button> buttons = new ArrayList<Button>();
	private ArrayList<ImageButton> buttons = new ArrayList<ImageButton>();
	private ArrayList<String> uniquenessList = new ArrayList<String>();
	private ArrayList<String> uniquenessButtonList = new ArrayList<String>();
	private ArrayList<JSONCar> cars = new ArrayList<JSONCar>();
	private TableW tracksTable;
	private ScrollPane scrollPane;
	private PopQueManager popQueManager;
	private GameLoader gameLoader;

	private Lock carLock = new ReentrantLock();
	private boolean first = false;

	private Button exit;
	private Preferences prefs = Gdx.app
			.getPreferences(GamePreferences.CAR_PREF_STR);

	boolean resultsRemaining = true;
	int currentOffset = 0;
	volatile boolean stall = true;
	AsyncExecutor ae = new AsyncExecutor(4);

	//private Integer previousSize = 0;

	private GameState gameState;
	
	public CarSelectorScreen(GameState gameState) {
		// carLock.lock();
		this.gameState = gameState;
		this.gameLoader = this.gameState.getGameLoader();
		initStage();

		initNavigationButtons();

		popQueManager = new PopQueManager(stage);
		popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));

		loadLocalCars();
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

	/*
	 * private void loadLocalCars() {
	 * 
	 * ae.submit(new AsyncTask<String>() {
	 * 
	 * @Override public String call() throws Exception { FileObject object =
	 * FileManager.readFromFile(); ArrayList<JSONCar> carList =
	 * object.getCars(); Iterator<JSONCar> iter = carList.iterator(); int count
	 * = 0;
	 * 
	 * System.out.println("number of cars: " + carList.size());
	 * 
	 * while (iter.hasNext()) {
	 * 
	 * //carLock.lock(); if (carLock.tryLock(1,TimeUnit.MILLISECONDS)) {
	 * System.out.println("reading " + count++); JSONCar car = iter.next(); if
	 * (!uniquenessList.contains((String) car.getId())) { //
	 * addButton(car.jsonify()); cars.add(car); uniquenessList.add(car.getId());
	 * // System.out.println(car.jsonify()); } carLock.unlock(); }
	 * 
	 * }
	 * 
	 * System.out.println("loaded from file: " + uniquenessList.size()); return
	 * null; } });
	 * 
	 * }
	 */

	private void loadLocalCars() {

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				Reader stream = FileManager
						.getFileStream(FileManager.CAR_FILE_NAME);
				ArrayList<JSONCar> carList = new ArrayList<JSONCar>();
				

				if (stream == null)
					return null;

				JsonReader reader = new JsonReader(stream);
				try {
					reader.beginArray();
					while (reader.hasNext()) {
						while (reader.hasNext()) {
							if (carLock.tryLock(10, TimeUnit.MILLISECONDS)) {
								final JSONCar car = new Gson()
										.fromJson(reader, JSONCar.class);

								cars.add(car);

								System.out.println(car.getId());
								
								Globals.runOnUIThread(new Runnable() {

									@Override
									public void run() {
										if (!uniquenessButtonList.contains(car.getId())) {
											addButton(car.jsonify());
											System.out.println("adding car");
											uniquenessButtonList.add(car.getId());
										}
									}
								});
								
								carLock.unlock();
								break;
							}
						}

						

					}

					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Iterator<JSONCar> iter = carList.iterator();
				// int count = 0;

				System.out.println("number of cars: " + carList.size());

				/*
				 * while (iter.hasNext()) {
				 * 
				 * 
				 * //if (carLock.tryLock(1,TimeUnit.MILLISECONDS)) {
				 * System.out.println("reading " + count++); JSONCar car =
				 * iter.next(); if (!uniquenessList.contains((String)
				 * car.getId())) { // addButton(car.jsonify()); cars.add(car);
				 * uniquenessList.add(car.getId()); //
				 * System.out.println(car.jsonify()); } carLock.unlock(); //}
				 * 
				 * }
				 */

				System.out.println("loaded from file: " + uniquenessList.size());
				return null;
			}
		});

	}

	private void downloadCars() {

		resultsRemaining = true;
		currentOffset = 0;

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {

				while (resultsRemaining) {
					stall = true;

					REST.getData(RESTPaths.CARS
							+ RESTProperties.URL_ARG_SPLITTER
							+ RESTProperties.PAGE_SIZE + REST.PAGE_SIZE
							+ RESTProperties.PROP_ARG_SPLITTER
							+ RESTProperties.OFFSET + currentOffset
							+ RESTProperties.PROP_ARG_SPLITTER
							+ RESTProperties.PROPS + RESTProperties.CREATED
							+ RESTProperties.PROP_PROP_SPLITTER
							+ RESTProperties.CAR_JSON

					, new HttpResponseListener() {

						@Override
						public void handleHttpResponse(HttpResponse httpResponse) {
							Backendless_Car obj = Backendless_JSONParser
									.processDownloadedCars(httpResponse
											.getResultAsString());

							Iterator<String> iter = obj.getData().iterator();

							while (iter.hasNext()) {

								carLock.lock();
								// if (carLock.tryLock()) {

								final String car = iter.next();
								final JSONCar carJson = JSONCar.objectify(car);
								final String carId = carJson.getId();
								// System.out.println(car);
								// Globals.runOnUIThread(new Runnable() {

								// @Override
								// public void run() {
								if (!uniquenessList.contains(carId)) {
									// addButton(car);

									cars.add(carJson);
									uniquenessList.add(carId);
								}

								// }
								// });
								carLock.unlock();
								// }
							}

							if (obj.getTotalObjects() - obj.getOffset() > 0) {
								resultsRemaining = true;
							} else {
								resultsRemaining = false;
							}
							stall = false;

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
					while (stall)
						;

					currentOffset += REST.PAGE_SIZE;
				}

				popQueManager.push(new PopQueObject(PopQueObjectType.DELETE));

				// Globals.runOnUIThread(new Runnable() {

				// @Override
				// public void run() {
				writeCarsToFile();
				// }
				// });

				return null;
			}

		});

	}

	private void writeCarsToFile() {

		// System.out.println("starting write");

		// Iterator<String> iter = cars.iterator();
		ArrayList<JSONCar> list = new ArrayList<JSONCar>();
		for (JSONCar car : cars) {
			// String car = iter.next();
			list.add(car);
		}

		FileObject fileObject = new FileObject();
		fileObject.setCars(list);

		FileManager.writeToFileGson(list);

	}

	private void initCarSelector() {
		tracksTable = new TableW();
		tracksTable.setWidth(300);
		// tracksTable.setRotation(-20);
		// tracksTable.setFillParent(true);
		// tracksTable.align(Align.center);

		scrollPane = new ScrollPane(tracksTable);
		// scrollPane.setHeight(Globals.ScreenHeight);
		// scrollPane.setWidth(Globals.ScreenWidth);
		// scrollPane.setOrigin(0, 0);
		scrollPane.setWidth(100);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setFillParent(true);
		scrollPane.setLayoutEnabled(true);
		scrollPane.setTouchable(Touchable.enabled);

		stage.addActor(scrollPane);
	}

	private void initButtons() {

		ImageButton b;

		Iterator<ImageButton> iter = buttons.iterator();

		int count = 0;

		while (iter.hasNext()) {
			b = iter.next();
			// b.setRotation(-20);
			// b.setWidth(1000);
			b.invalidate();

			tracksTable.add(b);

			count++;
			if (count > 2) {
				tracksTable.row();
				count = 0;
			}

		}

	}

	private void refreshAllButtons() {
		initCarSelector();
		initButtons();

		tracksTable.invalidate();
		scrollPane.invalidate();

		initNavigationButtons();
	}

	private void addButton(final String text) {

		/*
		 * Button b = new Button("bla"){
		 * 
		 * @Override public void Clicked() {
		 * prefs.putString(GamePreferences.CAR_MAP_STR, text); prefs.flush();
		 * 
		 * super.Clicked(); }
		 * 
		 * };
		 */

		String prevString = prefs
				.getString(
						GamePreferences.CAR_MAP_STR,
						"{jointList:[{mount1:springJoint=upper_1:0,mount2:tire_1:0},{mount1:bar3_0:0,mount2:springJoint=lower_1:0},{mount1:springJoint=upper_0:0,mount2:tire_0:0},{mount1:bar3_0:2,mount2:springJoint=lower_0:0}],componentList:[{componentName:bar3_0,properties:{ROTATION:0.0,POSITION:\"0.0,0.0\"}},{componentName:springJoint_0,properties:{ROTATION:1.4883224,POSITION:\"1.313098,-1.0663831\"}},{componentName:tire_0,properties:{MOTOR:1,ROTATION:0.0,POSITION:\"1.25,-1.1499996\"}},{componentName:springJoint_1,properties:{ROTATION:-0.33204922,POSITION:\"-1.3914706,-1.3713517\"}},{componentName:tire_1,properties:{MOTOR:1,ROTATION:0.0,POSITION:\"-1.3499994,-1.3000002\"}}]}");//

		prefs.putString(GamePreferences.CAR_MAP_STR, text);
		prefs.flush();

		TextureRegion tr = Assembler.assembleObjectImage(gameLoader);
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		trd.setMinWidth(200);
		trd.setMinHeight(140);
		ImageButton b = new ImageButton(trd);
		// b.setPosition(100, 100);
		b.setSize(100, 100);
		// stage.addActor(b);
		prefs.putString(GamePreferences.CAR_MAP_STR, prevString);
		prefs.flush();

		b.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				prefs.putString(GamePreferences.CAR_MAP_STR, text);
				prefs.flush();
				gameLoader.setScreen(new GamePlayScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		// b.setBackground(trd);

		buttons.add(b);

		refreshAllButtons();
	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new StretchViewport(Globals.ScreenWidth, Globals.ScreenHeight, camera);
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

		if (!first) {
			// carLock.unlock();
			first = true;
		}

		/*
		 * carLock.lock(); //if (carLock.tryLock()) { if (previousSize !=
		 * cars.size()) { previousSize = cars.size();
		 * 
		 * System.out.println("render" + cars.size());
		 * 
		 * for (JSONCar car : cars) { if
		 * (!uniquenessButtonList.contains(car.getId())) {
		 * addButton(car.jsonify()); uniquenessButtonList.add(car.getId()); } }
		 * 
		 * } carLock.unlock();
		 */
		// }

		/*
		 * if(!buttonQue.isEmpty()){ while(!buttonQue.isEmpty()){
		 * System.out.println("here"); tempButton = buttonQue.get(0);
		 * buttonQue.remove(0); buttons.add(tempButton); }
		 * 
		 * reinitScroll(); }
		 */

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

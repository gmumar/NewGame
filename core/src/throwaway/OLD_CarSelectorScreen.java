package throwaway;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import wrapper.CameraManager;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class OLD_CarSelectorScreen implements Screen {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private StretchViewport vp;

	// private ArrayList<Button> buttons = new ArrayList<Button>();
	static private ArrayList<ImageButton> buttons = new ArrayList<ImageButton>();
	private ArrayList<JsonElement> uniquenessButtonList = new ArrayList<JsonElement>();
	private ArrayList<JSONCar> cars = new ArrayList<JSONCar>();
	private TableW tracksTable;
	private ScrollPane scrollPane;
	private PopQueManager popQueManager;
	private GameLoader gameLoader;

	private Button exit, nextPage, prevPage;

	boolean resultsRemaining = true;
	int currentOffset = 0;
	volatile boolean stall = true;
	AsyncExecutor ae = new AsyncExecutor(6);

	private Semaphore loaderSemaphore = new Semaphore(2);
	private Lock uniqueListLock = new ReentrantLock();
	private Semaphore loadingLock = new Semaphore(1);

	private int pageNumber = 0;
	private static final int MAX_CARS_PER_PAGE = 6;
	private int currentPageStart = 0;
	private int currentPageEnd = MAX_CARS_PER_PAGE;
	private int loadedCount = 0;
	private JsonParser parser = new JsonParser();
	// private Integer previousSize = 0;

	private GameState gameState;

	public OLD_CarSelectorScreen(GameState gameState) {
		// carLock.lock();
		this.gameState = gameState;
		this.gameLoader = this.gameState.getGameLoader();
		initStage();

		initNavigationButtons();

		//popQueManager = new PopQueManager(stage);

		if (loadingLock.tryAcquire()) {
			popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));
		}

		try {
			loaderSemaphore.acquire(2);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//loadLocalCars();
		//downloadCars();

		//writeCarsToFile();

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

		nextPage = new Button(">") {
			@Override
			public void Clicked() {
				pageNumber++;
				if (loadingLock.tryAcquire()) {
					popQueManager.push(new PopQueObject(
							PopQueObjectType.LOADING));
				}

				pageDisplayed = false;
				// buttons.clear();
				currentPageStart += MAX_CARS_PER_PAGE;
				currentPageEnd += MAX_CARS_PER_PAGE;

				// if(currentPageEnd >= totalCars) currentPageEnd = totalCars;
			}
		};

		nextPage.setPosition(Globals.ScreenWidth - 50,
				Globals.ScreenHeight / 2 - 100);
		nextPage.setHeight(200);
		nextPage.setWidth(50);

		stage.addActor(nextPage);

		prevPage = new Button("<") {
			@Override
			public void Clicked() {
				pageNumber--;

				if (pageNumber < 0) {
					pageNumber = 0;
				} else {
					pageDisplayed = false;
					if (loadingLock.tryAcquire()) {
						popQueManager.push(new PopQueObject(
								PopQueObjectType.LOADING));
					}
					// buttons.clear();

					currentPageStart -= MAX_CARS_PER_PAGE;
					if (currentPageStart <= 0)
						currentPageStart = 0;
					currentPageEnd = currentPageStart + MAX_CARS_PER_PAGE;
				}

			}
		};

		prevPage.setPosition(0, Globals.ScreenHeight / 2 - 100);
		prevPage.setHeight(200);
		prevPage.setWidth(50);

		stage.addActor(prevPage);

	}

	private void loadLocalCars() {

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {

				for (JSONCar car : gameLoader.cars) {
					addCarToList(car);
				}

				loaderSemaphore.release();

				return null;
			}
		});

	}

	private void downloadCars() {

		resultsRemaining = true;
		currentOffset = 0;

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() {

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

								// carLock.lock();

								final String car = iter.next();
								final JSONCar carJson = JSONCar.objectify(car);
								// System.out.println("adding from cloud");

								addCarToList(carJson);

								uniqueListLock.lock();
								uniqueListLock.unlock();

								// carLock.unlock();

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

				System.out.println("release download");
				loaderSemaphore.release();
				return null;
			}

		});

	}

	private void addCarToList(final JSONCar car) {

		uniqueListLock.lock();

		JsonElement catElem = parser.parse(car.jsonify());
		
		if (!uniquenessButtonList.contains(catElem)) {
			cars.add(car);
			uniquenessButtonList.add(catElem);
		}

		uniqueListLock.unlock();

	}

	private void addCarToDisplay(final int from, final int to) {

		final int actualTo = to >= cars.size() ? cars.size() : to;

		Globals.runOnUIThread(new Runnable() {

			@Override
			public void run() {

				for (int i = from; i < actualTo; i++) {
					addButton(cars.get(i).jsonify());
				}

				popQueManager.push(new PopQueObject(PopQueObjectType.DELETE));
				loadingLock.release();
			}
		});

	}

	private void writeCarsToFile() {

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {

				try {
					while (isLoading())
						Thread.sleep(5);
					// loaderSemaphore.acquire(2);

					// Iterator<String> iter = cars.iterator();
					ArrayList<JSONCar> list = new ArrayList<JSONCar>();
					for (JSONCar car : cars) {
						// String car = iter.next();
						list.add(car);
					}

					System.out.println("writing " + list.size());

					FileObject fileObject = new FileObject();
					fileObject.setCars(list);

					FileManager.writeCarsToFileGson(list);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// loaderSemaphore.release(2);
				}

				return null;

			}
		});

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

		/*
		 * String prevString = prefs .getString( GamePreferences.CAR_MAP_STR,
		 * "{jointList:[{mount1:springJoint=upper_1:0,mount2:tire_1:0},{mount1:bar3_0:0,mount2:springJoint=lower_1:0},{mount1:springJoint=upper_0:0,mount2:tire_0:0},{mount1:bar3_0:2,mount2:springJoint=lower_0:0}],componentList:[{componentName:bar3_0,properties:{ROTATION:0.0,POSITION:\"0.0,0.0\"}},{componentName:springJoint_0,properties:{ROTATION:1.4883224,POSITION:\"1.313098,-1.0663831\"}},{componentName:tire_0,properties:{MOTOR:1,ROTATION:0.0,POSITION:\"1.25,-1.1499996\"}},{componentName:springJoint_1,properties:{ROTATION:-0.33204922,POSITION:\"-1.3914706,-1.3713517\"}},{componentName:tire_1,properties:{MOTOR:1,ROTATION:0.0,POSITION:\"-1.3499994,-1.3000002\"}}]}"
		 * );//
		 * 
		 * prefs.putString(GamePreferences.CAR_MAP_STR, text); prefs.flush();
		 */

		TextureRegion tr = Assembler.assembleObjectImage(gameLoader, text);
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		trd.setMinWidth(200);
		trd.setMinHeight(140);
		ImageButton b = new ImageButton(trd);
		b.setZIndex(100);
		// b.setPosition(100, 100);
		b.setSize(100, 100);
		// stage.addActor(b);
		/*
		 * prefs.putString(GamePreferences.CAR_MAP_STR, prevString);
		 * prefs.flush();
		 */

		b.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				/*
				 * prefs.putString(GamePreferences.CAR_MAP_STR, text);
				 * prefs.flush();
				 */
				gameState.getUser().setCurrentCar(text);
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

		vp = new StretchViewport(Globals.ScreenWidth, Globals.ScreenHeight,
				camera);
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

	boolean pageDisplayed = false;

	private void showCars() {
		if (pageDisplayed == false) {
			buttons.clear();
			if (tracksTable != null) {
				tracksTable.clear();
			}
			addCarToDisplay(currentPageStart, currentPageEnd);
			pageDisplayed = true;
		}

	}

	private boolean isLoading() {
		return loaderSemaphore.availablePermits() != 2;
	}

	@Override
	public void render(float delta) {
		renderWorld();
		popQueManager.update(delta);

		if (uniqueListLock.tryLock()) {
			loadedCount = uniquenessButtonList.size();

			uniqueListLock.unlock();
		}

		if (loadedCount <= currentPageEnd) {
			if (isLoading()) {
				;// loading
			} else {
				// done loading
				showCars();
			}
		} else {
			showCars();
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

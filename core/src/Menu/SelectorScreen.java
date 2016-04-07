package Menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.Globals;
import JSONifier.JSONParentClass;
import Menu.PopQueObject.PopQueObjectType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.MainMenuScreen;

public abstract class SelectorScreen implements Screen {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private StretchViewport vp;

	// private ArrayList<Button> buttons = new ArrayList<Button>();
	protected static ArrayList<Table> buttons = new ArrayList<Table>();
	protected ArrayList<JsonElement> uniquenessButtonList = new ArrayList<JsonElement>();
	protected ArrayList<JSONParentClass> items = new ArrayList<JSONParentClass>();
	private TableW tracksTable;
	private ScrollPane scrollPane;
	private PopQueManager popQueManager;
	public GameLoader gameLoader;

	private Button exit, nextPage, prevPage;

	public volatile boolean resultsRemaining = true;
	private boolean loadingComplete = false;
	//public volatile boolean stall = true;
	protected int currentOffset = 0;
	protected AsyncExecutor ae = new AsyncExecutor(2);
	protected HttpRequest downloadRequest;
	protected Semaphore stallSemaphore = new Semaphore(1);
	protected volatile boolean killThreads = false;

	public Semaphore loaderSemaphore = new Semaphore(2);
	public Lock uniqueListLock = new ReentrantLock();
	private Semaphore loadingLock = new Semaphore(1);
	public Semaphore localLoading = new Semaphore(1);

	private int pageNumber = 0;
	private static final int MAX_CARS_PER_PAGE = 6;
	private int currentPageStart = 0;
	private int currentPageEnd = MAX_CARS_PER_PAGE;
	private int loadedCount = 0;
	private JsonParser parser = new JsonParser();
	// private Integer previousSize = 0;

	public GameState gameState;

	abstract protected void addButton(final String text);

	abstract protected void downloadItems();

	abstract protected void writeObjectsToFile();

	abstract protected void addSpecificItemToList();

	public SelectorScreen(GameState gameState) {
		// carLock.lock();
		
		//ae = Globals.getTaskRunner();
		this.gameState = gameState;
		this.gameLoader = this.gameState.getGameLoader();
		initStage();

		initNavigationButtons();

		popQueManager = new PopQueManager(gameLoader, stage);

		if (loadingLock.tryAcquire()) {
			popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));
		}

		try {
			loaderSemaphore.acquire(2);
			localLoading.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		loadLocalItems();
		downloadItems();

		writeItemsToFile();

	}

	protected void loadLocalItems() {
		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {

				/*
				 * for (JSONCar car : gameLoader.cars) { addItemToList(car); }
				 */

				addSpecificItemToList();

				return null;
			}
		});

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

	protected void addItemToList(final JSONParentClass item) {

		uniqueListLock.lock();

		JsonElement catElem = parser.parse(item.jsonify());

		if (!uniquenessButtonList.contains(catElem)) {
			items.add(item);
			uniquenessButtonList.add(catElem);
		}

		uniqueListLock.unlock();

	}

	private void addItemToDisplay(final int from, final int to) {

		final int actualTo = to >= items.size() ? items.size() : to;

		Globals.runOnUIThread(new Runnable() {

			@Override
			public void run() {

				for (int i = from; i < actualTo; i++) {
					addButton(items.get(i).jsonify());
				}

				popQueManager.push(new PopQueObject(PopQueObjectType.DELETE));
				loadingLock.release();
			}
		});

	}

	protected void writeItemsToFile() {
		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {

				try {
					while (isLoading())
						;
					// Thread.sleep(5);
					// loaderSemaphore.acquire(2);

					// Iterator<String> iter = cars.iterator();

					writeObjectsToFile();

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

		scrollPane = new ScrollPane(tracksTable);
		scrollPane.setWidth(100);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setFillParent(true);
		scrollPane.setLayoutEnabled(true);
		scrollPane.setTouchable(Touchable.enabled);

		stage.addActor(scrollPane);
	}

	private void initButtons() {

		Table b;

		Iterator<Table> iter = buttons.iterator();

		int count = 0;

		while (iter.hasNext()) {
			b = iter.next();
			b.invalidate();

			tracksTable.add(b);

			count++;
			if (count > 2) {
				tracksTable.row();
				count = 0;
			}

		}

	}

	protected void refreshAllButtons() {
		initCarSelector();
		initButtons();

		tracksTable.invalidate();
		scrollPane.invalidate();

		initNavigationButtons();
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
			addItemToDisplay(currentPageStart, currentPageEnd);
			pageDisplayed = true;
		}

	}

	public boolean isLoading() {
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

		if (!isLoading() && !loadingComplete) {
			// refresh the page when loading complete
			loadingComplete = true;
			pageDisplayed = false;
		}

		if (localLoading.tryAcquire() || loadedCount >= currentPageEnd) {
			showCars();
			localLoading.release();
		}

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
		if(downloadRequest!=null) Gdx.net.cancelHttpRequest(downloadRequest);	
		
		killThreads = true;

		ae.dispose();

		batch.dispose();
		stage.dispose();

	}
}

package Menu;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.Globals;
import Dialog.Skins;
import JSONifier.JSONParentClass;
import Menu.PopQueObject.PopQueObjectType;
import Menu.Bars.BottomBar;
import Menu.Bars.TitleBar;

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

public abstract class SelectorScreen implements Screen {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private StretchViewport vp;

	// private ArrayList<Button> buttons = new ArrayList<Button>();
	protected static ArrayList<Table> buttons = new ArrayList<Table>();
	protected ArrayList<JsonElement> uniquenessButtonList = new ArrayList<JsonElement>();
	protected ArrayList<JSONParentClass> items = new ArrayList<JSONParentClass>();
	protected Table itemsTable;
	protected Table baseTable;
	protected ScrollPane scrollPane;
	protected PopQueManager popQueManager;
	public GameLoader gameLoader;

	protected Button  nextPage;
	protected Button prevPage;

	public volatile boolean resultsRemaining = true;
	private boolean loadingComplete = false;
	// public volatile boolean stall = true;
	protected int currentOffset = 0;
	protected AsyncExecutor ae = new AsyncExecutor(2);
	protected HttpRequest downloadRequest;
	protected Semaphore stallSemaphore = new Semaphore(1);
	protected volatile boolean killThreads = false;

	public Semaphore loaderSemaphore = new Semaphore(2);
	public Lock uniqueListLock = new ReentrantLock();
	protected Semaphore loadingLock = new Semaphore(1);
	public Semaphore localLoading = new Semaphore(1);
	protected Semaphore localLoadedCounter = new Semaphore(0);
	protected Semaphore totalLoadedCounter = new Semaphore(0);
	public volatile boolean downloadCancelled = false;

	protected int pageNumber = 0;
	protected static final int MAX_CARS_PER_PAGE = 6;
	protected int currentPageStart = 0;
	protected int currentPageEnd = MAX_CARS_PER_PAGE;
	private int loadedCount = 0;
	private JsonParser parser = new JsonParser();
	// private Integer previousSize = 0;
	private boolean initButtons = false;
	
	public GameState gameState;

	abstract protected void addButton(final JSONParentClass jsonParentClass);

	abstract protected void downloadItems();

	abstract protected void writeObjectsToFile();

	abstract protected void addSpecificItemToList();
	
	abstract protected void goNext();
	
	abstract protected void initButtons();
	
	abstract protected void populateContentTable(Table contentTable);
	
	protected abstract void createItemsTable(Table container);
	
	protected abstract ScreenType getScreenType();
	
	public SelectorScreen(GameState gameState) {
		// carLock.lock();

		// ae = Globals.globalRunner;
		this.gameState = gameState;
		this.gameLoader = gameState.getGameLoader();
		initStage();

		//initNavigationButtons();

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

	protected void addItemToList(final JSONParentClass item) {

		uniqueListLock.lock();

		JsonElement catElem = parser.parse(item.jsonify());

		if (!uniquenessButtonList.contains(catElem)) {
			items.add(item);
			totalLoadedCounter.release();
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
					//System.out.println("SelectorScreen: " + items.get(i).jsonify());
					addButton(items.get(i));
				}
				if(items.size()>0 && !isLoading() || loadedCount >= currentPageEnd){
					popQueManager.push(new PopQueObject(PopQueObjectType.DELETE));
					loadingLock.release();
				}
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

					if(downloadCancelled){
						return null;
					}
					
					System.out.println("SelectorScreen: " + localLoadedCounter.availablePermits() +  " " + totalLoadedCounter.availablePermits());
					
					if(localLoadedCounter.availablePermits()>=totalLoadedCounter.availablePermits()){
						return null;
					}
					
					
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



	protected void refreshAllButtons() {
		

		if(!initButtons){
			initButtons = !initButtons;
			baseTable = new Table(Skins.loadDefault(gameLoader, 1));
			baseTable.setBackground("blackOut");
			baseTable.setFillParent(true);
			
			
		} else {
			baseTable.clear();
		}
		
		TitleBar.create(baseTable, getScreenType(), popQueManager, gameState, false);
		
		Table contentTable = new Table();
		contentTable.setTouchable(Touchable.childrenOnly);
		
		populateContentTable(contentTable);

		baseTable.add(contentTable).expand().pad(20);
		
		BottomBar.create(baseTable, getScreenType(), gameState, false);
		
		//initNavigationButtons();
		
		
		
		stage.addActor(baseTable);
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

	public boolean pageDisplayed = false;

	private void showCars() {
		if (pageDisplayed == false) {
			buttons.clear();
			if (itemsTable != null) {
				itemsTable.clear();
			}
			addItemToDisplay(currentPageStart, currentPageEnd);
			pageDisplayed = true;
		}

	}

	public boolean isLoading() {
		return loaderSemaphore.availablePermits() < 2;
	}

	@Override
	public void render(float delta) {
		renderWorld();
		popQueManager.update();

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

		Gdx.gl.glClearColor(0, 0, 0, 1);
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
		exit();

	}
	
	private void exit(){
		killThreads = true;
		if(downloadRequest!=null) Gdx.net.cancelHttpRequest(downloadRequest);
	}

	@Override
	public void dispose() {
		exit();

		killThreads = true;
		
		Globals.globalRunner.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				ae.dispose();
				return null;
			}
			
		});
		
		batch.dispose();
		stage.dispose();

	}
}

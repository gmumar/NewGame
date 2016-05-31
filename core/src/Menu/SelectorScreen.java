package Menu;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.GameViewport;
import wrapper.Globals;
import Dialog.Skins;
import JSONifier.JSONCar;
import JSONifier.JSONParentClass;
import JSONifier.JSONParentClass.JSONParentType;
import JSONifier.JSONTrack;
import Menu.PopQueObject.PopQueObjectType;
import Menu.Bars.BottomBar;
import Menu.Bars.TitleBar;
import User.TwoButtonDialogFlow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gudesigns.climber.GameLoader;

public abstract class SelectorScreen implements Screen, TwoButtonDialogFlow {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private GameViewport vp;

	// private ArrayList<Button> buttons = new ArrayList<Button>();
	protected ArrayList<Table> buttons = new ArrayList<Table>();
	// protected ArrayList<JsonElement> uniquenessButtonList = new
	// ArrayList<JsonElement>();
	protected ArrayList<JSONParentClass> uniquenessButtonList = new ArrayList<JSONParentClass>();

	protected ArrayList<JSONParentClass> items = new ArrayList<JSONParentClass>();
	protected Table itemsTable;
	protected Table baseTable;
	protected ScrollPane scrollPane;
	protected PopQueManager popQueManager;
	protected SelectorScreen context;
	public GameLoader gameLoader;

	protected ImageButton prevPage, nextPage;

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
	// protected static final int MAX_ITEMS_PER_PAGE = 6;
	protected int currentPageStart = 0;
	protected int currentPageEnd;
	private int loadedCount = 0;
	// private JsonParser parser = new JsonParser();
	// private Integer previousSize = 0;
	private boolean initButtons = false;

	public GameState gameState;

	abstract protected void addButton(final JSONParentClass jsonParentClass);

	abstract protected void downloadItems();

	abstract protected void writeObjectsToFile();

	abstract protected void addSpecificItemToList();

	abstract protected void initButtons();

	abstract protected void populateContentTable(Table contentTable);

	protected abstract void createItemsTable(Table container);

	protected abstract ScreenType getScreenType();

	protected abstract void selectorRender(float delta);

	protected abstract void clearScreen();

	protected abstract int getItemsPerPage();

	public SelectorScreen(GameState gameState) {
		// carLock.lock();
		context = this;

		currentPageEnd = getItemsPerPage();

		// ae = Globals.globalRunner;
		this.gameState = gameState;
		this.gameLoader = gameState.getGameLoader();
		initStage();

		popQueManager = new PopQueManager(gameLoader, stage);
		// initNavigationButtons();
		refreshAllButtons();
		
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

		if (item.getParentType() == JSONParentType.TRACK) {

			JSONTrack trackToBeAdded = (JSONTrack) item;

			if (uniquenessButtonList.contains(trackToBeAdded)) {

				System.out.println("SelectorScreen: found");

				Integer indexInList = uniquenessButtonList
						.indexOf(trackToBeAdded);
				JSONParentClass otherItem = uniquenessButtonList
						.get(indexInList);
				JSONTrack trackInList = (JSONTrack) otherItem;

				Long time1 = Long.parseLong(trackToBeAdded.getCreationTime());
				Long time2 = Long.parseLong(trackInList.getCreationTime());

				if (time1 > time2) {
					System.out.println("SelectorScreen: newer " + time1 + " > "
							+ time2);
					items.add(item);
					items.remove(otherItem);
					totalLoadedCounter.release();
					uniquenessButtonList.remove(trackInList);
					uniquenessButtonList.add(trackToBeAdded);

				} else {
					System.out.println("SelectorScreen: skipping");
					;
				}

			} else {
				items.add(item);
				totalLoadedCounter.release();
				uniquenessButtonList.add(trackToBeAdded);
			}

		} else if (item.getParentType() == JSONParentType.CAR) {
			JSONCar car = (JSONCar) item;

			if (uniquenessButtonList.contains(car)) {
			} else {
				items.add(item);
				totalLoadedCounter.release();
				uniquenessButtonList.add(car);
			}
		}

		/*
		 * JsonElement catElem = parser.parse(item.jsonify());
		 * 
		 * if (uniquenessButtonList.contains(catElem)) {
		 * System.out.println("SelectorScreen: Found"); } else{
		 * System.out.println("SelectorScreen: Not Found"); items.add(item);
		 * totalLoadedCounter.release(); uniquenessButtonList.add(catElem); }
		 */

		uniqueListLock.unlock();

	}

	private void addItemToDisplay(final int from, final int to) {

		final int actualTo = to >= items.size() ? items.size() : to;

		Globals.runOnUIThread(new Runnable() {

			@Override
			public void run() {

				for (int i = from; i < actualTo; i++) {
					// System.out.println("SelectorScreen: " +
					// items.get(i).jsonify());
					addButton(items.get(i));
				}
				if (items.size() > 0 && !isLoading()
						|| loadedCount >= currentPageEnd) {
					popQueManager
							.push(new PopQueObject(PopQueObjectType.DELETE));
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

					if (downloadCancelled) {
						return null;
					}

					System.out.println("SelectorScreen: "
							+ localLoadedCounter.availablePermits() + " "
							+ totalLoadedCounter.availablePermits());

					if (localLoadedCounter.availablePermits() - 2 >= totalLoadedCounter
							.availablePermits()) {
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

	Table contentTable;

	protected void refreshAllButtons() {

		if (!initButtons) {
			initButtons = !initButtons;
			baseTable = new Table(Skins.loadDefault(gameLoader, 1));
			baseTable.setBackground("transparent");
			baseTable.setFillParent(true);
			
			TitleBar.create(baseTable, getScreenType(), popQueManager,
					gameState, null, true);

			contentTable = new Table();
			
			contentTable.clear();
			contentTable.setTouchable(Touchable.childrenOnly);

			populateContentTable(contentTable);

			baseTable.add(contentTable).fill().expand().pad(20);

			BottomBar.create(baseTable, getScreenType(), gameState, false);

			stage.addActor(baseTable);
		} else {

			// baseTable.clear();
			contentTable.clear();
			contentTable.setTouchable(Touchable.childrenOnly);

			populateContentTable(contentTable);
		}

		/*
		 * Table contentTable = new Table(); contentTable.clear();
		 * contentTable.setTouchable(Touchable.childrenOnly);
		 * 
		 * populateContentTable(contentTable);
		 * 
		 * baseTable.add(contentTable).expand().pad(20);
		 */

		// BottomBar.create(baseTable, getScreenType(), gameState, false);

		// initNavigationButtons();

	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new GameViewport(Globals.ScreenWidth, Globals.ScreenHeight, camera);
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

		selectorRender(delta);

		stage.draw();
		stage.act(Gdx.graphics.getDeltaTime());

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

		clearScreen();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

	}

	@Override
	public boolean successful() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean failed() {
		// TODO Auto-generated method stub
		return false;
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

	private void exit() {
		killThreads = true;
		if (downloadRequest != null)
			Gdx.net.cancelHttpRequest(downloadRequest);
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

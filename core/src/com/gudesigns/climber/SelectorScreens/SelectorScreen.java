package com.gudesigns.climber.SelectorScreens;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.GameViewport;
import wrapper.Globals;
import Dialog.Skins;
import Dialog.StoreBuyDialog;
import JSONifier.JSONCar;
import JSONifier.JSONParentClass;
import JSONifier.JSONParentClass.JSONParentType;
import JSONifier.JSONTrack;
import JSONifier.JSONTrack.TrackType;
import Menu.Animations;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.ScreenType;
import Menu.PopQueObject.PopQueObjectType;
import Menu.Bars.BottomBar;
import Menu.Bars.TitleBar;
import Menu.Bars.TitleBarObject;
import UserPackage.ItemsLookupPrefix;
import UserPackage.TwoButtonDialogFlow;
import UserPackage.User;

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
import com.gudesigns.climber.CarBuilderScreen;
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
	protected SelectorScreen instance;
	public GameLoader gameLoader;

	protected ImageButton prevPage, nextPage;

	public volatile boolean resultsRemaining = true;
	volatile public boolean pageDisplayed = false;
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
	protected Long lastObjectCreationTime = -Long.MAX_VALUE;

	protected int pageNumber = 0;
	// protected static final int MAX_ITEMS_PER_PAGE = 6;
	protected int currentPageStart = 0;
	protected int currentPageEnd;
	private int loadedCount = 0;
	// private JsonParser parser = new JsonParser();
	// private Integer previousSize = 0;
	private boolean initButtons = false;

	public GameState gameState;

	private User user;

	private TitleBarObject titleBar;
	private Integer currentMoney;

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

	protected abstract boolean isCorrectTrackType(TrackType type);

	protected abstract void updateGameLoaderObjects();

	public SelectorScreen(GameState gameState) {
		// carLock.lock();
		instance = this;
		user = gameState.getUser();

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
				updateGameLoaderObjects();

				return null;
			}
		});

	}

	protected void addItemToList(final JSONParentClass item) {

		uniqueListLock.lock();

		Long creationTime = Long.parseLong(item.getCreationTime());
		if (creationTime > lastObjectCreationTime) {
			lastObjectCreationTime = creationTime;
		}
		
		System.out.println(creationTime);

		if (item.getParentType() == JSONParentType.TRACK) {

			JSONTrack trackToBeAdded = (JSONTrack) item;

			if (isCorrectTrackType(trackToBeAdded.getType())) {

				if (uniquenessButtonList.contains(trackToBeAdded)) {

					Integer indexInList = uniquenessButtonList
							.indexOf(trackToBeAdded);
					JSONParentClass otherItem = uniquenessButtonList
							.get(indexInList);
					JSONTrack trackInList = (JSONTrack) otherItem;

					Long time1 = Long.parseLong(trackToBeAdded
							.getCreationTime());
					Long time2 = Long.parseLong(trackInList.getCreationTime());

					if (time1 > time2) {
						items.add(item);
						items.remove(otherItem);
						totalLoadedCounter.release();
						uniquenessButtonList.remove(trackInList);
						uniquenessButtonList.add(trackToBeAdded);

					} else {
						;
					}

				} else {
					items.add(item);
					totalLoadedCounter.release();
					uniquenessButtonList.add(trackToBeAdded);
				}
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
		 * if (uniquenessButtonList.contains(catElem)) { items.add(item);
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

					if (localLoadedCounter.availablePermits() - 2 >= totalLoadedCounter
							.availablePermits()) {
						return null;
					}

					writeObjectsToFile();

					updateGameLoaderObjects();
					
					System.out.println("last " + lastObjectCreationTime.toString());

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
			initButtons = true;
			baseTable = new Table(Skins.loadDefault(gameLoader, 1));
			baseTable.setBackground("transparent");
			baseTable.setFillParent(true);

			titleBar = TitleBar.create(baseTable, getScreenType(),
					popQueManager, gameState, null, true);

			currentMoney = user.getMoney();

			contentTable = new Table();

			contentTable.clear();
			contentTable.setTouchable(Touchable.childrenOnly);

			populateContentTable(contentTable);

			baseTable.add(contentTable).fill().expand().pad(20);

			BottomBar.create(baseTable, getScreenType(), gameState, false);

			stage.addActor(baseTable);
		} else {
			float scrollPos = 0;
			if (scrollPane != null) {
				scrollPos = scrollPane.getScrollX();
			}
			// baseTable.clear();
			// contentTable.clear();
			// contentTable.setTouchable(Touchable.childrenOnly);

			// populateContentTable(contentTable);

			itemsTable.clear();
			initButtons();

			if (scrollPane != null) {
				scrollPane.setScrollX(scrollPos);
			}
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

		currentMoney = Animations.money(titleBar.getAnimationMoney(),
				titleBar.getBaseMoney(), user.getMoney(), currentMoney);

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
			refershPage();
		}

		if (localLoading.tryAcquire() || loadedCount >= currentPageEnd) {
			showCars();
			localLoading.release();
		}

	}

	private void refershPage() {
		pageDisplayed = false;
	}

	@Override
	public boolean successfulTwoButtonFlow(String itemName) {
		if (itemName.compareTo(ItemsLookupPrefix.ERROR_NOT_ENOUGH_MONEY) == 0) {
			// popQueManager.push(new PopQueObject(PopQueObjectType.STORE_BUY));
			StoreBuyDialog.launchDialogFlow(gameLoader, popQueManager);
		} else if (itemName
				.compareTo(ItemsLookupPrefix.ERROR_PARTS_NOT_UNLOCKED) == 0) {
			gameLoader.setScreen(new CarBuilderScreen(gameState));
		} else {
			refershPage();
		}
		// showCars();
		// refreshAllButtons();
		return false;
	}

	@Override
	public boolean failedTwoButtonFlow(Integer moneyRequired) {
		popQueManager.push(new PopQueObject(
				PopQueObjectType.ERROR_NOT_ENOUGH_MONEY, "Not Enough Coins",
				"You need " + moneyRequired.toString() + " more coins", this));
		return false;
	}

	private void renderWorld() {

		clearScreen();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

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

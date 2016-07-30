package com.gudesigns.climber.SelectorScreens.CarSelectorScreen;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.GameState;
import wrapper.Globals;
import Assembly.Assembler;
import Dialog.Skins;
import JSONifier.JSONCar;
import JSONifier.JSONParentClass;
import JSONifier.JSONTrack.TrackType;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.ScreenType;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;
import RESTWrapper.Backendless_Car;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import RESTWrapper.ServerDataUnit;
import Storage.FileManager;
import Storage.FileObject;
import UserPackage.GameErrors;
import UserPackage.ItemsLookupPrefix;
import UserPackage.User;
import UserPackage.User.CarSetErrors;
import UserPackage.User.GameMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gudesigns.climber.CarBuilderScreen;
import com.gudesigns.climber.ChallengeCreationScreen;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.SelectorScreens.SelectorScreen;

public class CarSelectorScreen extends SelectorScreen {

	@Override
	protected String getFileName() {
		// TODO Auto-generated method stub
		return FileManager.CAR_FILE_NAME;
	}

	@Override
	protected int getItemsPerPage() {
		return 3;
	}

	public CarSelectorScreen(GameState gameState) {
		super(gameState);

		if (gameState.getUser().isNew(ItemsLookupPrefix.CAR_MY_PICKS)) {
			gameState.getUser()
					.setNonNew(ItemsLookupPrefix.CAR_MY_PICKS, false);
		}
	}

	@Override
	protected String getDownloadRequestString(int offset, Long lastCreatedTime) {
		// TODO Auto-generated method stub
		return RESTPaths.CARS + RESTProperties.URL_ARG_SPLITTER
				+ RESTProperties.PAGE_SIZE + REST.PAGE_SIZE
				+ RESTProperties.PROP_ARG_SPLITTER + RESTProperties.OFFSET
				+ offset + RESTProperties.PROP_ARG_SPLITTER
				+ RESTProperties.PROPS + RESTProperties.CREATED
				+ RESTProperties.PROP_PROP_SPLITTER + RESTProperties.CAR_JSON
				+ RESTProperties.PROP_PROP_SPLITTER + RESTProperties.OBJECT_ID
				+ RESTProperties.PROP_PROP_SPLITTER + RESTProperties.CAR_INDEX
				+ RESTProperties.PROP_ARG_SPLITTER
				+ RESTProperties.WhereCreatedGreaterThan(lastCreatedTime);
	}

	// @Override
	protected void downloadItems_old() {
		resultsRemaining = true;
		currentOffset = 0;

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() {

				while (resultsRemaining && !killThreads) {
					// stall = true;

					if (killThreads) {
						loaderSemaphore.release();
						resultsRemaining = false;
						return null;
					}

					while (stallSemaphore.tryAcquire()) {
						downloadRequest = REST.getData(RESTPaths.CARS
								+ RESTProperties.URL_ARG_SPLITTER
								+ RESTProperties.PAGE_SIZE + REST.PAGE_SIZE
								+ RESTProperties.PROP_ARG_SPLITTER
								+ RESTProperties.OFFSET + currentOffset
								+ RESTProperties.PROP_ARG_SPLITTER
								+ RESTProperties.PROPS + RESTProperties.CREATED
								+ RESTProperties.PROP_PROP_SPLITTER
								+ RESTProperties.CAR_JSON
								+ RESTProperties.PROP_PROP_SPLITTER
								+ RESTProperties.OBJECT_ID

						, new HttpResponseListener() {

							@Override
							public void handleHttpResponse(
									HttpResponse httpResponse) {
								Backendless_Car obj = Backendless_JSONParser
										.processDownloadedCars(httpResponse
												.getResultAsString());

								for (ServerDataUnit fromServer : obj.getData()) {

									// carLock.lock();

									final JSONCar carJson = JSONCar
											.objectify(fromServer.getData());
									carJson.setObjectId(fromServer
											.getObjectId());
									carJson.setCreationTime(fromServer
											.getCreationTime());

									addItemToList(carJson);

									uniqueListLock.lock();
									uniqueListLock.unlock();

									// carLock.unlock();

								}

								if (obj.getTotalObjects() - obj.getOffset() > 0) {
									resultsRemaining = true;
								} else {
									resultsRemaining = false;
								}
								stallSemaphore.release();
								// stall = false;

							}

							@Override
							public void failed(Throwable t) {
								System.out
										.println("CarSelectorScreen: download failed Stack printing");
								t.printStackTrace();
								loaderSemaphore.release();
								// stallSemaphore.release();
								// stall = false;
								resultsRemaining = false;
								downloadCancelled = true;
								return;
							}

							@Override
							public void cancelled() {
								loaderSemaphore.release();
								// stallSemaphore.release();
								// stall = false;
								resultsRemaining = false;
								downloadCancelled = true;
								return;
							}

						});
						currentOffset += REST.PAGE_SIZE;
					}

					// while (stall)
					// ;

				}

				loaderSemaphore.release();
				return null;
			}

		});
	}

	/*
	 * @Override protected void writeItemsToFile() { ae.submit(new
	 * AsyncTask<String>() {
	 * 
	 * @Override public String call() throws Exception {
	 * 
	 * try { while (isLoading()); //Thread.sleep(5); //
	 * loaderSemaphore.acquire(2);
	 * 
	 * // Iterator<String> iter = cars.iterator(); ArrayList<JSONCar> list = new
	 * ArrayList<JSONCar>(); for (JSONParentClass car : items) { // String car =
	 * iter.next(); list.add((JSONCar) car); }
	 * 
	 * 
	 * FileObject fileObject = new FileObject(); fileObject.setCars(list);
	 * 
	 * FileManager.writeCarsToFileGson(list);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } finally { //
	 * loaderSemaphore.release(2); }
	 * 
	 * return null;
	 * 
	 * } });
	 * 
	 * }
	 */

	@Override
	protected void addButton(final JSONParentClass item) {

		Table wrapper = new Table();

		final String itemJson = item.jsonify();

		TextureRegion tr = Assembler.assembleCarImage(gameLoader, itemJson,
				false, false);
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		trd.setMinWidth(Globals.CAR_DISPLAY_BUTTON_WIDTH);
		trd.setMinHeight(Globals.CAR_DISPLAY_BUTTON_HEIGHT);

		ImageButton image = new ImageButton(trd);
		// image.setZIndex(100);
		// b.setPosition(100, 100);
		// image.setSize(100, 100);
		image.setTouchable(Touchable.enabled);
		image.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				CarSetErrors carSetStatus = gameState.getUser().setCurrentCar(
						itemJson, false);

				if (carSetStatus == CarSetErrors.NONE) {
					if (User.getInstance().getCurrentGameMode() == GameMode.SET_CHALLENGE) {
						gameLoader.setScreen(new ChallengeCreationScreen(
								gameState));
					} else {
						gameLoader.setScreen(new GamePlayScreen(gameState));
					}
				} else if (carSetStatus == CarSetErrors.PARTS_NOT_UNLOCKED) {
					popQueManager.push(new PopQueObject(
							PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED, "Error",
							GameErrors.PARTS_NOT_UNLOCKED, instance));
				} else if (carSetStatus == CarSetErrors.CAR_NOT_SUTIBLE_FOR_CHALLENGE) {
					popQueManager.push(new PopQueObject(
							PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED, "Error",
							GameErrors.CAR_TOO_HIGH_TO_USE, instance));
				}
				// gameState.getUser().setCurrentCar(itemJson);
				// gameLoader.setScreen(new GamePlayScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		// image.row();

		Skin skin = Skins.loadDefault(gameLoader, 0);

		wrapper.add(image);

		wrapper.row();

		Table buttonsWrapper = new Table();

		final Table playButton = new Table(skin);
		playButton.setBackground("gameGreen");

		Label chooseCarText = new Label("Choose Car", skin);
		playButton.add(chooseCarText).pad(5);

		Image playImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/play_black.png"));
		playButton.add(playImage).width(Globals.baseSize)
				.height(Globals.baseSize).pad(5);
		playButton.setTouchable(Touchable.enabled);
		// TextButton play = new TextButton("play",skin, "noButton");
		playButton.addListener(new ActorGestureListener() {

			@Override
			public void touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				CarSetErrors carSetStatus = gameState.getUser().setCurrentCar(itemJson, false);
				playButton.setBackground("grey");
				if (carSetStatus == CarSetErrors.NONE) {
					if (User.getInstance().getCurrentGameMode() == GameMode.SET_CHALLENGE) {
						gameLoader.setScreen(new ChallengeCreationScreen(
								gameState));
					} else {
						gameLoader.setScreen(new GamePlayScreen(gameState));
					}
				} else if(carSetStatus == CarSetErrors.PARTS_NOT_UNLOCKED) {
					popQueManager.push(new PopQueObject(
							PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED, "Error",
							GameErrors.PARTS_NOT_UNLOCKED, instance));
				} else if (carSetStatus == CarSetErrors.CAR_NOT_SUTIBLE_FOR_CHALLENGE) {
					popQueManager.push(new PopQueObject(
							PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED, "Error",
							GameErrors.CAR_TOO_HIGH_TO_USE, instance));
				}
				// gameLoader.setScreen(new GamePlayScreen(gameState));
				super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				playButton.setBackground("gameGreen");
				super.touchUp(event, x, y, pointer, button);
			}

		});
		buttonsWrapper.add(playButton).colspan(20).expand().fill();

		ImageButton editImage = SimpleImageButton.create(
				SimpleImageButtonTypes.EDIT, gameLoader);

		final Table edit = new Table(skin);
		edit.setBackground("gameYellow");

		edit.add(editImage);
		edit.setTouchable(Touchable.enabled);

		edit.addListener(new ActorGestureListener() {

			@Override
			public void touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				CarSetErrors carSetStatus = gameState.getUser().setCurrentCar(itemJson, false);
				
				edit.setBackground("grey");
				if (carSetStatus == CarSetErrors.NONE) {
					gameLoader.setScreen(new CarBuilderScreen(gameState));
				} else if (carSetStatus == CarSetErrors.PARTS_NOT_UNLOCKED) {
					popQueManager.push(new PopQueObject(
							PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED, "Error",
							GameErrors.PARTS_NOT_UNLOCKED, instance));
				} else if (carSetStatus == CarSetErrors.CAR_NOT_SUTIBLE_FOR_CHALLENGE) {
					popQueManager.push(new PopQueObject(
							PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED, "Error",
							GameErrors.CAR_TOO_HIGH_TO_USE, instance));
				}
				// gameLoader.setScreen(new CarBuilderScreen(gameState));
				super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				edit.setBackground("gameYellow");
				super.touchUp(event, x, y, pointer, button);
			}

		});

		buttonsWrapper.add(edit).colspan(1).expand().fill();

		wrapper.add(buttonsWrapper).expand().fill();

		wrapper.pad(5);

		wrapper.setTouchable(Touchable.childrenOnly);
		// b.setBackground(trd);

		buttons.add(wrapper);

		refreshAllButtons();

	}

	@Override
	protected void updateGameLoaderObjects() {
		gameLoader.autherCars.clear();

		for (JSONParentClass car : items) {
			gameLoader.autherCars.add((JSONCar) car);
		}

	}

	@Override
	protected void writeObjectsToFile(Long lastCreationTime) {
		ArrayList<JSONCar> list = new ArrayList<JSONCar>();

		for (JSONParentClass car : items) {
			// String car = iter.next();
			list.add((JSONCar) car);
		}

		if (list.isEmpty())
			return;

		FileObject fileObject = new FileObject();
		fileObject.setCars(list);

		FileManager.writeCarsToFileGson(list, getFileName(), lastCreationTime);

	}

	@Override
	protected void readFileForItems() {
		for (JSONCar car : gameLoader.autherCars) {
			addItemToList(car);
			localLoadedCounter.release();
		}
		loaderSemaphore.release();
		localLoading.release();

	}

	@Override
	protected void initButtons() {

		Table b;

		Iterator<Table> iter = buttons.iterator();

		int count = 0;

		while (iter.hasNext()) {
			b = iter.next();
			b.invalidate();

			itemsTable.add(b);

			count++;
			if (count > 2) {
				itemsTable.row();
				count = 0;
			}

		}

	}

	@Override
	protected void populateContentTable(Table contentTable) {

		Table prevHolder = new Table(Skins.loadDefault(gameLoader, 1));
		prevHolder.setTouchable(Touchable.enabled);
		prevPage = SimpleImageButton.create(SimpleImageButtonTypes.RIGHT,
				gameLoader);
		prevHolder.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
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

					currentPageStart -= getItemsPerPage();
					if (currentPageStart <= 0)
						currentPageStart = 0;
					currentPageEnd = currentPageStart + getItemsPerPage();
				}

			}

		});

		prevHolder.add(prevPage).width(Globals.baseSize);
		contentTable.add(prevHolder).left().expand().fill()
				.height(Globals.baseSize * 10).width(Globals.baseSize * 1.5f);
		// stage.addActor(prevPage);

		createItemsTable(contentTable);
		initButtons();
		itemsTable.invalidate();

		Table nextHolder = new Table(Skins.loadDefault(gameLoader, 1));
		nextHolder.setTouchable(Touchable.enabled);

		nextPage = SimpleImageButton.create(SimpleImageButtonTypes.LEFT,
				gameLoader);

		nextHolder.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				pageNumber++;
				if (loadingLock.tryAcquire()) {
					popQueManager.push(new PopQueObject(
							PopQueObjectType.LOADING));
				}

				pageDisplayed = false;
				// buttons.clear();
				currentPageStart += getItemsPerPage();
				currentPageEnd += getItemsPerPage();

				// if(currentPageEnd >= totalCars) currentPageEnd = totalCars;
				super.clicked(event, x, y);
			}

		});

		nextHolder.add(nextPage).width(Globals.baseSize);
		contentTable.add(nextHolder).right().expand().fill()
				.height(Globals.baseSize * 10).width(Globals.baseSize * 1.5f);

	}

	@Override
	protected void createItemsTable(Table container) {
		itemsTable = new Table();

		container.add(itemsTable).width(600f).pad(20);
		// container.row();
	}

	@Override
	protected ScreenType getScreenType() {
		return ScreenType.CAR_SELECTOR;
	}

	@Override
	protected void selectorRender(float delta) {

	}

	@Override
	protected void clearScreen() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
	}

	@Override
	protected boolean isCorrectTrackType(TrackType type) {
		// TODO Auto-generated method stub
		return false;
	}

}

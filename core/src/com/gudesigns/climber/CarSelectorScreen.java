package com.gudesigns.climber;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.GameState;
import wrapper.Globals;
import Assembly.Assembler;
import Dialog.Skins;
import JSONifier.JSONCar;
import JSONifier.JSONParentClass;
import Menu.Button;
import Menu.PopQueObject;
import Menu.ScreenType;
import Menu.PopQueObject.PopQueObjectType;
import Menu.SelectorScreen;
import RESTWrapper.Backendless_Car;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import RESTWrapper.ServerDataUnit;
import Storage.FileManager;
import Storage.FileObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.async.AsyncTask;

public class CarSelectorScreen extends SelectorScreen {

	@Override
	protected int getItemsPerPage() {
		return 6;
	}
	
	public CarSelectorScreen(GameState gameState) {
		super(gameState);
	}

	@Override
	protected void downloadItems() {
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


								for (ServerDataUnit fromServer :  obj.getData()) {

									// carLock.lock();

									final JSONCar carJson = JSONCar
											.objectify(fromServer.getData());
									carJson.setObjectId(fromServer.getObjectId());
									carJson.setCreationTime(fromServer.getCreationTime());

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
								System.out.println("CarSelectorScreen: download failed Stack printing");
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
	 * System.out.println("writing " + list.size());
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
		
		TextureRegion tr = Assembler.assembleObjectImage(gameLoader, itemJson,
				false);
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		trd.setMinWidth(200);
		trd.setMinHeight(140);

		ImageButton image = new ImageButton(trd);
		image.setZIndex(100);
		// b.setPosition(100, 100);
		image.setSize(100, 100);

		image.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				gameState.getUser().setCurrentCar(itemJson);
				gameLoader.setScreen(new GamePlayScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		// image.row();

		wrapper.add(image);

		wrapper.row();

		Table buttonsWrapper = new Table();

		TextButton play = new TextButton("play", Skins.loadDefault(gameLoader,
				0), "noButton");
		play.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				gameState.getUser().setCurrentCar(itemJson);
				gameLoader.setScreen(new GamePlayScreen(gameState));
				super.clicked(event, x, y);
			}

		});
		buttonsWrapper.add(play).height(10).colspan(20).expand().fill();

		TextButton edit = new TextButton("edit", Skins.loadDefault(gameLoader,
				0), "noButton");
		edit.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				gameState.getUser().setCurrentCar(itemJson);
				gameLoader.setScreen(new CarBuilderScreen(gameState));
				super.clicked(event, x, y);
			}

		});
		buttonsWrapper.add(edit).height(10).colspan(1).expand().fill();

		wrapper.add(buttonsWrapper).expand().fill();

		wrapper.pad(5);

		// b.setBackground(trd);

		buttons.add(wrapper);

		refreshAllButtons();

	}

	@Override
	protected void writeObjectsToFile() {
		ArrayList<JSONCar> list = new ArrayList<JSONCar>();
		gameLoader.cars.clear();
		
		for (JSONParentClass car : items) {
			// String car = iter.next();
			list.add((JSONCar) car);
			gameLoader.cars.add((JSONCar) car);
		}
		
		System.out.println("CarSelectorScreen: written " + list.size());

		if (list.isEmpty())
			return;

		FileObject fileObject = new FileObject();
		fileObject.setCars(list);

		FileManager.writeCarsToFileGson(list);

	}

	@Override
	protected void addSpecificItemToList() {
		for (JSONCar car : gameLoader.cars) {
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

					currentPageStart -= getItemsPerPage();
					if (currentPageStart <= 0)
						currentPageStart = 0;
					currentPageEnd = currentPageStart + getItemsPerPage();
				}

			}
		};
		

		contentTable.add(prevPage).colspan(1).left().width(Globals.baseSize);
		//stage.addActor(prevPage);
		
		
		createItemsTable(contentTable);
		initButtons();
		itemsTable.invalidate();
		
		
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
				currentPageStart += getItemsPerPage();
				currentPageEnd += getItemsPerPage();

				// if(currentPageEnd >= totalCars) currentPageEnd = totalCars;
			}
		};
		
		contentTable.add(nextPage).colspan(1).right().width(Globals.baseSize);
		
	}
	
	@Override
	protected void createItemsTable(Table container) {
		itemsTable = new Table();

		container.add(itemsTable).width(600f).pad(20);
		//container.row();
	}
	
	@Override
	protected ScreenType getScreenType(){
		return ScreenType.CAR_SELECTOR;
	}
	
	@Override
	protected void selectorRender(float delta) {
		
	}
	
	@Override
	protected  void clearScreen(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
	}
}

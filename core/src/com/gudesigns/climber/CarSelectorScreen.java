package com.gudesigns.climber;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.GameState;
import Assembly.Assembler;
import Dialog.Skins;
import JSONifier.JSONCar;
import JSONifier.JSONParentClass;
import Menu.SelectorScreen;
import RESTWrapper.Backendless_Car;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import Storage.FileManager;
import Storage.FileObject;

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

	public CarSelectorScreen(GameState gameState) {
		super(gameState);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void downloadItems() {
		resultsRemaining = true;
		currentOffset = 0;

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {

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

						, new HttpResponseListener() {

							@Override
							public void handleHttpResponse(
									HttpResponse httpResponse) {
								Backendless_Car obj = Backendless_JSONParser
										.processDownloadedCars(httpResponse
												.getResultAsString());

								Iterator<String> iter = obj.getData()
										.iterator();

								while (iter.hasNext()) {

									// carLock.lock();

									final String car = iter.next();
									final JSONCar carJson = JSONCar
											.objectify(car);
									// System.out.println("adding from cloud");

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
								System.out.println("failed");
								t.printStackTrace();
								loaderSemaphore.release();
								// stallSemaphore.release();
								// stall = false;
								resultsRemaining = false;
								return;
							}

							@Override
							public void cancelled() {
								System.out.println("cancelled");
								loaderSemaphore.release();
								// stallSemaphore.release();
								// stall = false;
								resultsRemaining = false;
								return;
							}

						});
						currentOffset += REST.PAGE_SIZE;
					}

					// while (stall)
					// ;

				}

				System.out.println("release download");
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
	protected void addButton(final String text) {

		Table wrapper = new Table();

		TextureRegion tr = Assembler.assembleObjectImage(gameLoader, text,
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

				gameState.getUser().setCurrentCar(text);
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

				gameState.getUser().setCurrentCar(text);
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

				gameState.getUser().setCurrentCar(text);
				gameLoader.setScreen(new BuilderScreen(gameState));
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
		for (JSONParentClass car : items) {
			// String car = iter.next();
			list.add((JSONCar) car);
		}

		System.out.println("writing " + list.size());

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
		}
		loaderSemaphore.release();
		localLoading.release();
	}

}

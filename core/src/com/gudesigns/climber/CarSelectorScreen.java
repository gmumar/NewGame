package com.gudesigns.climber;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.GameState;
import Assembly.Assembler;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.async.AsyncTask;

public class CarSelectorScreen extends SelectorScreen{

	public CarSelectorScreen(GameState gameState) {
		super(gameState);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void loadLocalItems() {
		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {

				for (JSONCar car : gameLoader.cars) {
					addItemToList(car);
				}

				loaderSemaphore.release();

				return null;
			}
		});

	}

	@Override
	protected void downloadItems() {
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
							stall = false;

						}
						@Override
						public void failed(Throwable t) {
							loaderSemaphore.release();
						}

						@Override
						public void cancelled() {
							loaderSemaphore.release();
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

	@Override
	protected void writeItemsToFile() {
		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {

				try {
					while (isLoading())
						Thread.sleep(5);
					// loaderSemaphore.acquire(2);

					// Iterator<String> iter = cars.iterator();
					ArrayList<JSONCar> list = new ArrayList<JSONCar>();
					for (JSONParentClass car : items) {
						// String car = iter.next();
						list.add((JSONCar) car);
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

	@Override
	protected void addButton(final String text) {

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


		b.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				gameState.getUser().setCurrentCar(text);
				gameLoader.setScreen(new GamePlayScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		// b.setBackground(trd);

		buttons.add(b);

		refreshAllButtons();
		
	}

}

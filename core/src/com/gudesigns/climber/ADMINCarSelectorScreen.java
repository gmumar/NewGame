package com.gudesigns.climber;

import java.util.Iterator;

import wrapper.GameState;
import wrapper.Globals;
import Assembly.Assembler;
import JSONifier.JSONCar;
import JSONifier.JSONParentClass;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import RESTWrapper.Backendless_Car;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import RESTWrapper.ServerDataUnit;

import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.async.AsyncTask;

public class ADMINCarSelectorScreen extends CarSelectorScreen {

	@Override
	protected int getItemsPerPage() {
		return 10;
	}

	public ADMINCarSelectorScreen(GameState gameState) {
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
						downloadRequest = REST.getData(RESTPaths.COMMUNITY_CARS_DUMP
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

	@Override
	protected void addButton(final JSONParentClass item) {

		Table wrapper = new Table();

		final String itemJson = item.jsonify();

		TextureRegion tr = Assembler.assembleObjectImage(gameLoader, itemJson,
				false);
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		trd.setMinWidth(Globals.CAR_DISPLAY_BUTTON_WIDTH*3/5);
		trd.setMinHeight(Globals.CAR_DISPLAY_BUTTON_HEIGHT*3/5);

		ImageButton image = new ImageButton(trd);
		// image.setZIndex(100);
		// b.setPosition(100, 100);
		// image.setSize(100, 100);

		image.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				popQueManager.push(new PopQueObject(PopQueObjectType.CAR_DISPLAY,itemJson,Globals.ADMIN_MODE));
				//gameState.getUser().setCurrentCar(itemJson);
				//gameLoader.setScreen(new GamePlayScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		// image.row();

		wrapper.add(image);

		wrapper.row();

		wrapper.pad(5);

		// b.setBackground(trd);

		buttons.add(wrapper);

		refreshAllButtons();

	}

	@Override
	protected void writeObjectsToFile() {
		
	}

	@Override
	protected void addSpecificItemToList() {
		//for (JSONCar car : gameLoader.communityCars) {
		//	addItemToList(car);
		//	localLoadedCounter.release();
		//}
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
			if (count > 4) {
				itemsTable.row();
				count = 0;
			}

		}

	}

}

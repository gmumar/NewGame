package com.gudesigns.climber.SelectorScreens.TrackSelectorScreen;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import wrapper.GameState;
import wrapper.Globals;
import Dialog.Skins;
import JSONifier.JSONParentClass;
import JSONifier.JSONTrack;
import JSONifier.JSONTrack.TrackType;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.ScreenType;
import Menu.Buttons.AdventureTrackButton;
import Menu.Buttons.ButtonLockWrapper;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.Backendless_Track;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import RESTWrapper.ServerDataUnit;
import Storage.FileManager;
import Storage.FileObject;
import UserPackage.Costs;
import UserPackage.ItemsLookupPrefix;
import UserPackage.TrackMode;
import UserPackage.User;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.gudesigns.climber.CarModeScreen;
import com.gudesigns.climber.SelectorScreens.SelectorScreen;

public class InfiniteTrackSelectorScreen extends SelectorScreen {

	private final static int itemsPerRow = 5;

	@Override
	protected String getFileName() {
		// TODO Auto-generated method stub
		return FileManager.INFINITE_TRACK_FILE_NAME;
	}

	@Override
	protected void createItemsTable(Table container) {
		itemsTable = new Table();

		scrollPane = new ScrollPane(itemsTable);
		scrollPane.layout();
		// scrollPane.setWidth(100);
		scrollPane.setScrollingDisabled(false, true);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setLayoutEnabled(true);
		scrollPane.setTouchable(Touchable.enabled);
		scrollPane.setOverscroll(true, false);

		// stage.addActor(scrollPane);
		container.add(scrollPane).width(600f);
		// container.row();
	}

	@Override
	protected int getItemsPerPage() {
		return 20;
	}

	public InfiniteTrackSelectorScreen(GameState gameState) {
		super(gameState);
		
		if (gameState.getUser().isNew(ItemsLookupPrefix.INFINITY_TRACK_MODE)) {
			gameState.getUser().setNonNew(
					ItemsLookupPrefix.INFINITY_TRACK_MODE, false);
		}
	}
	
	

	@Override
	protected String getDownloadRequestString(int offset, Long lastCreatedTime) {
		// TODO Auto-generated method stub
		return RESTPaths.INFINITE_MAPS
				+ RESTProperties.URL_ARG_SPLITTER
				+ RESTProperties.PAGE_SIZE + REST.PAGE_SIZE
				+ RESTProperties.PROP_ARG_SPLITTER
				+ RESTProperties.OFFSET + offset
				+ RESTProperties.PROP_ARG_SPLITTER
				+ RESTProperties.PROPS + RESTProperties.CREATED
				+ RESTProperties.PROP_PROP_SPLITTER
				+ RESTProperties.TRACK_POINTS_JSON
				+ RESTProperties.PROP_PROP_SPLITTER
				+ RESTProperties.OBJECT_ID
				+ RESTProperties.PROP_PROP_SPLITTER
				+ RESTProperties.TRACK_BEST_TIME
				+ RESTProperties.PROP_PROP_SPLITTER
				+ RESTProperties.TRACK_DIFFICULTY
				+ RESTProperties.PROP_PROP_SPLITTER
				+ RESTProperties.TRACK_INDEX
				+ RESTProperties.PROP_ARG_SPLITTER
				+ RESTProperties.WhereCreatedGreaterThan(lastCreatedTime);
	}

	//@Override
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
						downloadRequest = REST.getData(RESTPaths.INFINITE_MAPS
								+ RESTProperties.URL_ARG_SPLITTER
								+ RESTProperties.PAGE_SIZE + REST.PAGE_SIZE
								+ RESTProperties.PROP_ARG_SPLITTER
								+ RESTProperties.OFFSET + currentOffset
								+ RESTProperties.PROP_ARG_SPLITTER
								+ RESTProperties.PROPS + RESTProperties.CREATED
								+ RESTProperties.PROP_PROP_SPLITTER
								+ RESTProperties.TRACK_POINTS_JSON
								+ RESTProperties.PROP_PROP_SPLITTER
								+ RESTProperties.OBJECT_ID
								+ RESTProperties.PROP_PROP_SPLITTER
								+ RESTProperties.TRACK_BEST_TIME
								+ RESTProperties.PROP_PROP_SPLITTER
								+ RESTProperties.TRACK_DIFFICULTY
								+ RESTProperties.PROP_PROP_SPLITTER
								+ RESTProperties.TRACK_INDEX

						, new HttpResponseListener() {

							@Override
							public void handleHttpResponse(
									HttpResponse httpResponse) {
								Backendless_Track obj = Backendless_JSONParser
										.processDownloadedTrack(httpResponse
												.getResultAsString());

								// Iterator<String> iter =
								// obj.getData().iterator();

								for (ServerDataUnit fromServer : obj.getData()) {

									final JSONTrack trackJson = JSONTrack
											.objectify(fromServer.getData());
									trackJson.setObjectId(fromServer
											.getObjectId());
									trackJson.setBestTime(fromServer
											.getTrackBestTime());
									trackJson.setDifficulty(fromServer
											.getTrackDifficulty());
									trackJson.setItemIndex(fromServer
											.getItemIndex());
									trackJson.setCreationTime(fromServer
											.getCreationTime());

									addItemToList(trackJson);

									uniqueListLock.lock();
									uniqueListLock.unlock();

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
										.println("TrackSelectorScreen: download failed Stack printing");
								t.printStackTrace();
								loaderSemaphore.release();
								// stallSemaphore.release();
								// stall = false;
								resultsRemaining = false;
								downloadCancelled = true;
								// return;
							}

							@Override
							public void cancelled() {
								loaderSemaphore.release();
								// stallSemaphore.release();
								// stall = false;
								resultsRemaining = false;
								downloadCancelled = true;
								// return;
							}

						});
						currentOffset += REST.PAGE_SIZE;
					}
					// while (stall);

				}
				loaderSemaphore.release();

				return null;
			}
		});

	}

	@Override
	protected void addButton(final JSONParentClass item) {
		final JSONTrack track = JSONTrack.objectify(item.jsonify());
		final ButtonLockWrapper b = AdventureTrackButton.create(gameLoader,
				track, true, ScreenType.NONE);

		b.button.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				User user = gameState.getUser();

				if (!b.locked) {
					user.setCurrentTrack(item.jsonify(), TrackMode.INFINTE,
							true);

					if (user.isNew(ItemsLookupPrefix
							.getInfiniteTrackPrefix(Integer.toString(track
									.getItemIndex())))) {
						user.setNonNew(ItemsLookupPrefix
								.getInfiniteTrackPrefix(Integer.toString(track
										.getItemIndex())), false);
					}

					gameLoader.setScreen(new CarModeScreen(gameState));
					super.clicked(event, x, y);
				} else {

					popQueManager.push(new PopQueObject(
							PopQueObjectType.UNLOCK_TRACK, ItemsLookupPrefix
									.getInfiniteTrackPrefix(Integer
											.toString(track.getItemIndex())),
							"Unlock Track", "\t\tUnlock track "
									+ Integer.toString(track.getItemIndex())
									+ "\t\t", Costs.INFINITY_TRACK, instance));

				}
			}

		});

		buttons.add(b.button);

		refreshAllButtons();
	}

	@Override
	protected void writeObjectsToFile(Long lastCreationTime) {
		ArrayList<JSONTrack> list = new ArrayList<JSONTrack>();
		gameLoader.infiniteTracks.clear();

		for (JSONParentClass track : items) {
			// String car = iter.next();
			list.add((JSONTrack) track);
			gameLoader.infiniteTracks.add((JSONTrack) track);

		}

		if (list.isEmpty())
			return;

		FileObject fileObject = new FileObject();
		fileObject.setTracks(list);

		FileManager.writeTracksToFileGson(list, getFileName(), lastCreationTime);
	}

	@Override
	protected void updateGameLoaderObjects() {
		gameLoader.infiniteTracks.clear();

		for (JSONParentClass track : items) {
			gameLoader.infiniteTracks.add((JSONTrack) track);
		}

	}

	@Override
	protected void readFileForItems() {
		Gson gson = new Gson();
		Reader stream = FileManager.getFileStream(getFileName());

		if (stream == null) {
			loaderSemaphore.release();
			localLoading.release();
			return;
		}

		JsonReader reader = new JsonReader(stream);
		try {
			reader.beginArray();
			while (reader.hasNext()) {
				while (reader.hasNext()) {
					final JSONTrack track = gson.fromJson(reader,
							JSONTrack.class);

					localLoadedCounter.release();
					addItemToList(track);
					break;
				}

			}

			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			loaderSemaphore.release();
			localLoading.release();
		}
		return;
	}

	@Override
	protected void initButtons() {

		Table b;
		int count = 0;

		Collections.sort(buttons, new Comparator<Table>() {

			@Override
			public int compare(Table o1, Table o2) {

				Integer table1 = Integer.parseInt((String) o1.getUserObject());
				Integer table2 = Integer.parseInt((String) o2.getUserObject());

				return table2.compareTo(table1);
			}

		});

		Iterator<Table> iter = buttons.iterator();

		while (iter.hasNext()) {
			b = iter.next();
			b.invalidate();

			itemsTable.add(b).pad(5).height(Globals.baseSize * 4)
					.width(Globals.baseSize * 4);
			count++;
			if (count >= itemsPerRow) {
				itemsTable.row();
				count = 0;
			}
			itemsTable.invalidate();
			scrollPane.invalidate();
		}

	}

	@Override
	protected void populateContentTable(Table contentTable) {

		// contentTable.add(prevPage).colspan(1).left();
		// stage.addActor(prevPage);

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

		createItemsTable(contentTable);
		initButtons();
		itemsTable.invalidate();
		scrollPane.invalidate();

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

		// contentTable.add(nextPage).colspan(1).right();

	}

	@Override
	protected boolean isCorrectTrackType(TrackType type) {
		if (type == TrackType.FORREST || type == TrackType.ARTIC) {
			return true;
		}
		return false;
	}

	@Override
	protected ScreenType getScreenType() {
		return ScreenType.INFINITE_TRACK_SELECTOR;
	}

	@Override
	protected void selectorRender(float delta) {
		// scrollingBackground.draw(BackgroundType.SCROLLING);

	}

	@Override
	protected void clearScreen() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
	}

}

package com.gudesigns.climber;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import wrapper.GameState;
import wrapper.Globals;
import JSONifier.JSONParentClass;
import JSONifier.JSONTrack;
import JSONifier.JSONTrack.TrackType;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.ScreenType;
import Menu.SelectorScreen;
import Menu.Buttons.AdventureTrackButton;
import Menu.Buttons.ButtonLockWrapper;
import ParallexBackground.ScrollingBackground;
import ParallexBackground.ScrollingBackground.BackgroundType;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.Backendless_Track;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import RESTWrapper.ServerDataUnit;
import Storage.FileManager;
import Storage.FileObject;

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

public class TrackSelectorScreen extends SelectorScreen {

	private final static int itemsPerRow = 5;

	@Override
	protected int getItemsPerPage() {
		return 20;
	}

	private ScrollingBackground scrollingBackground;

	public TrackSelectorScreen(GameState gameState) {
		super(gameState);

		scrollingBackground = new ScrollingBackground(this.gameLoader, null,
				TrackType.FORREST, BackgroundType.SELECTOR);
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
						downloadRequest = REST.getData(RESTPaths.MAPS
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
								System.out
										.println("TrackSelectorScreen: got a reply ");
								Backendless_Track obj = Backendless_JSONParser
										.processDownloadedTrack(httpResponse
												.getResultAsString());

								// Iterator<String> iter =
								// obj.getData().iterator();

								for (ServerDataUnit fromServer : obj.getData()) {

									System.out.println("TrackSelectorScreen: "
											+ fromServer.getTrackIndex());

									final JSONTrack trackJson = JSONTrack
											.objectify(fromServer.getData());
									trackJson.setObjectId(fromServer
											.getObjectId());
									trackJson.setBestTime(fromServer
											.getTrackBestTime());
									trackJson.setDifficulty(fromServer
											.getTrackDifficulty());
									trackJson.setIndex(fromServer
											.getTrackIndex());
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

	/*
	 * @Override protected void writeItemsToFile() { ae.submit(new
	 * AsyncTask<String>() {
	 * 
	 * @Override public String call() throws Exception {
	 * 
	 * try { while (isLoading()); //Thread.sleep(5); //
	 * loaderSemaphore.acquire(2);
	 * 
	 * // Iterator<String> iter = cars.iterator(); ArrayList<JSONTrack> list =
	 * new ArrayList<JSONTrack>(); for (JSONParentClass track : items) { //
	 * String car = iter.next(); list.add((JSONTrack) track); }
	 * 
	 * System.out.println("writing " + list.size());
	 * 
	 * FileObject fileObject = new FileObject(); fileObject.setTracks(list);
	 * 
	 * FileManager.writeTracksToFileGson(list);
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
		final JSONTrack track = JSONTrack.objectify(item.jsonify());
		final ButtonLockWrapper b = AdventureTrackButton.create(gameLoader,
				track, false);

		b.button.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!b.locked) {
					gameState.getUser().setCurrentTrack(item.jsonify());
					gameLoader.setScreen(new CarModeScreen(gameState));
					super.clicked(event, x, y);
				} else {

					popQueManager.push(new PopQueObject(
							PopQueObjectType.UNLOCK_TRACK, "Unlock Track",
							"\t\tUnlock track "
									+ Integer.toString(track.getIndex())
									+ "\t\t", 2000, context));

				}
			}

		});

		buttons.add(b.button);

		refreshAllButtons();
	}

	@Override
	protected void writeObjectsToFile() {
		ArrayList<JSONTrack> list = new ArrayList<JSONTrack>();
		for (JSONParentClass track : items) {
			// String car = iter.next();
			list.add((JSONTrack) track);

		}

		System.out.println("TrackSelectorScreen: writting " + list.size());

		if (list.isEmpty())
			return;

		FileObject fileObject = new FileObject();
		fileObject.setTracks(list);

		FileManager.writeTracksToFileGson(list);
	}

	@Override
	protected void addSpecificItemToList() {
		Gson gson = new Gson();
		Reader stream = FileManager.getFileStream(FileManager.TRACK_FILE_NAME);

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

				return table1.compareTo(table2);
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

		createItemsTable(contentTable);
		initButtons();
		itemsTable.invalidate();
		scrollPane.invalidate();

		// contentTable.add(nextPage).colspan(1).right();

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
	protected ScreenType getScreenType() {
		return ScreenType.TRACK_SELECTOR;
	}

	@Override
	protected void selectorRender(float delta) {
		scrollingBackground.draw(BackgroundType.SCROLLING);

	}

	@Override
	protected void clearScreen() {
		Gdx.gl.glClearColor(Globals.FORREST_GREEN_BG.r,
				Globals.FORREST_GREEN_BG.g, Globals.FORREST_GREEN_BG.b, 1);
	}

}

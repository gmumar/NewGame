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
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.ScreenType;
import Menu.Buttons.AdventureTrackButton;
import Menu.Buttons.ButtonLockWrapper;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.Backendless_Track;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import RESTWrapper.ServerDataUnit;
import Storage.FileManager;
import Storage.FileObject;
import User.Costs;
import User.ItemsLookupPrefix;
import User.TrackMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class InfiniteTrackSelectorScreen extends TrackSelectorScreen {

	private final static int itemsPerRow = 5;

	@Override
	protected int getItemsPerPage() {
		return 20;
	}

	public InfiniteTrackSelectorScreen(GameState gameState) {
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

	@Override
	protected void addButton(final JSONParentClass item) {
		final JSONTrack track = JSONTrack.objectify(item.jsonify());
		final ButtonLockWrapper b = AdventureTrackButton.create(gameLoader,
				track, true);

		b.button.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!b.locked) {
					gameState.getUser().setCurrentTrack(item.jsonify(),
							TrackMode.INFINTE);
					gameLoader.setScreen(new CarModeScreen(gameState));
					super.clicked(event, x, y);
				} else {

					popQueManager.push(new PopQueObject(
							PopQueObjectType.UNLOCK_TRACK, ItemsLookupPrefix
									.getInfiniteTrackPrefix(Integer
											.toString(track.getIndex())),
							"Unlock Track", "\t\tUnlock track "
									+ Integer.toString(track.getIndex())
									+ "\t\t", Costs.INFINITY_TRACK, instance));

				}
			}

		});

		buttons.add(b.button);

		refreshAllButtons();
	}

	@Override
	protected void writeObjectsToFile() {
		ArrayList<JSONTrack> list = new ArrayList<JSONTrack>();
		gameLoader.infiniteTracks.clear();

		for (JSONParentClass track : items) {
			// String car = iter.next();
			list.add((JSONTrack) track);
			gameLoader.infiniteTracks.add((JSONTrack) track);

		}

		System.out.println("TrackSelectorScreen: writting " + list.size());

		if (list.isEmpty())
			return;

		FileObject fileObject = new FileObject();
		fileObject.setTracks(list);

		FileManager.writeTracksToFileGson(list,
				FileManager.INFINITE_TRACK_FILE_NAME);
	}

	@Override
	protected void addSpecificItemToList() {
		Gson gson = new Gson();
		Reader stream = FileManager
				.getFileStream(FileManager.INFINITE_TRACK_FILE_NAME);

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
	protected ScreenType getScreenType() {
		return ScreenType.TRACK_SELECTOR;
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

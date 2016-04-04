package com.gudesigns.climber;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

import wrapper.GameState;
import JSONifier.JSONParentClass;
import JSONifier.JSONTrack;
import Menu.Button;
import Menu.SelectorScreen;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.Backendless_Track;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import Storage.FileManager;
import Storage.FileObject;

import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class TrackSelectorScreen extends SelectorScreen {

	public TrackSelectorScreen(GameState gameState) {
		super(gameState);
	}

	@Override
	protected void downloadItems() {
		resultsRemaining = true;
		currentOffset = 0;

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {

				while (resultsRemaining) {
					stall = true;

					REST.getData(RESTPaths.MAPS
							+ RESTProperties.URL_ARG_SPLITTER
							+ RESTProperties.PAGE_SIZE + REST.PAGE_SIZE
							+ RESTProperties.PROP_ARG_SPLITTER
							+ RESTProperties.OFFSET + currentOffset
							+ RESTProperties.PROP_ARG_SPLITTER
							+ RESTProperties.PROPS + RESTProperties.CREATED
							+ RESTProperties.PROP_PROP_SPLITTER
							+ RESTProperties.TRACK_POINTS_JSON

					, new HttpResponseListener() {

						@Override
						public void handleHttpResponse(HttpResponse httpResponse) {
							System.out.println("got a reply ");
							Backendless_Track obj = Backendless_JSONParser
									.processDownloadedTrack(httpResponse
											.getResultAsString());

							

							Iterator<String> iter = obj.getData().iterator();

							while (iter.hasNext()) {
								final String track = iter.next();
								final JSONTrack trackJson = JSONTrack
										.objectify(track);

								addItemToList(trackJson);

								uniqueListLock.lock();
								uniqueListLock.unlock();

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
							t.printStackTrace();
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

				System.out.println("released download");
				loaderSemaphore.release();

				return null;
			}
		});

	}

	/*@Override
	protected void writeItemsToFile() {
		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {

				try {
					while (isLoading());
						//Thread.sleep(5);
					// loaderSemaphore.acquire(2);

					// Iterator<String> iter = cars.iterator();
					ArrayList<JSONTrack> list = new ArrayList<JSONTrack>();
					for (JSONParentClass track : items) {
						// String car = iter.next();
						list.add((JSONTrack) track);
					}

					System.out.println("writing " + list.size());

					FileObject fileObject = new FileObject();
					fileObject.setTracks(list);

					FileManager.writeTracksToFileGson(list);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// loaderSemaphore.release(2);
				}

				return null;

			}
		});

	}*/

	@Override
	protected void addButton(final String text) {
		Button b = new Button("bla");

		b.setZIndex(100);
		b.setSize(100, 100);
		
		b.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameState.getUser().setCurrentTrack(text);
				gameLoader.setScreen(new CarSelectorScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		buttons.add(b);

		refreshAllButtons();
	}

	@Override
	protected void writeObjectsToFile() {
		ArrayList<JSONTrack> list = new ArrayList<JSONTrack>();
		for (JSONParentClass track : items) {
			// String car = iter.next();
			list.add((JSONTrack) track);
		}

		System.out.println("writing " + list.size());

		FileObject fileObject = new FileObject();
		fileObject.setTracks(list);

		FileManager.writeTracksToFileGson(list);
	}

	@Override
	protected void addSpecificItemToList() {
		Gson gson = new Gson();
		Reader stream = FileManager
				.getFileStream(FileManager.TRACK_FILE_NAME);

		if (stream == null) {
			System.out.println("first release local");
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

					addItemToList(track);
					break;
				}

			}

			reader.close();

			System.out.println("loaded " + items.size());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			loaderSemaphore.release();
			localLoading.release();
			System.out.println("second release local");
		}
		return;
	}

}

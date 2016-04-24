package com.gudesigns.climber;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

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
import RESTWrapper.ServerDataUnit;
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
			public String call() {

				while (resultsRemaining && !killThreads) {
					//stall = true;
					
					if(killThreads) {
						loaderSemaphore.release();
						resultsRemaining = false;
						
						return null;
					}
					
					while(stallSemaphore.tryAcquire()){
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
								
						, new HttpResponseListener() {
	
							@Override
							public void handleHttpResponse(HttpResponse httpResponse) {
								System.out.println("TrackSelectorScreen: got a reply ");
								Backendless_Track obj = Backendless_JSONParser
										.processDownloadedTrack(httpResponse
												.getResultAsString());
	
								//Iterator<String> iter = obj.getData().iterator();
	
								for (ServerDataUnit fromServer :  obj.getData()) {
									
									final JSONTrack trackJson = JSONTrack
											.objectify(fromServer.getData());
									trackJson.setObjectId(fromServer.getObjectId());
									trackJson.setBestTime(fromServer.getTrackBestTime());
									
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
								//stall = false;
	
							}
	
							@Override
							public void failed(Throwable t) {
								System.out.println("TrackSelectorScreen: download failed Stack printing");
								t.printStackTrace();
								loaderSemaphore.release();
								//stallSemaphore.release();
								//stall = false;
								resultsRemaining = false;
								downloadCancelled = true;
								//return;
							}
	
							@Override
							public void cancelled() {
								loaderSemaphore.release();
								//stallSemaphore.release();
								//stall = false;
								resultsRemaining = false;
								downloadCancelled = true;
								//return;
							}
	
						});
						currentOffset += REST.PAGE_SIZE;
					}
					//while (stall);

					
				}
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
	protected void addButton(final JSONParentClass item) {
		Button b = new Button(item.getObjectId().substring(0, 5));

		b.setZIndex(100);
		b.setSize(100, 100);

		b.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameState.getUser().setCurrentTrack(item.jsonify());
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
		
		System.out.println("TrackSelectorScreen: writting " + list.size());
		
		if(list.isEmpty()) return;

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
	protected void goNext() {
		gameLoader.setScreen(new CarSelectorScreen(gameState));
	}

}

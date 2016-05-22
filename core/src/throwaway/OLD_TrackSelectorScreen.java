package throwaway;


public class OLD_TrackSelectorScreen {//Oimplements Screen {

	/*private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private StretchViewport vp;

	private ArrayList<Button> buttons = new ArrayList<Button>();
	private ArrayList<String> uniquenessList = new ArrayList<String>();
	private ArrayList<String> tracks = new ArrayList<String>();
	//private ArrayList<ImageButton> buttons = new ArrayList<ImageButton>();
	private TableW tracksTable;
	private ScrollPane scrollPane;
	private PopQueManager popQueManager;
	private GameLoader gameLoader;

	private Button exit;
	private Preferences prefs = Gdx.app
			.getPreferences(GamePreferences.CAR_PREF_STR);

	boolean resultsRemaining = true;
	int currentOffset = 0;
	volatile boolean stall = true;
	private AsyncExecutor ae = new AsyncExecutor(1);

	public OLD_TrackSelectorScreen(GameState gameState) {
		this.gameLoader = gameState.getGameLoader();
		initStage();

		initNavigationButtons();

		//popQueManager = new PopQueManager(stage);
		popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));

		//loadLocalTracks();
		//downloadCars();

	}

	private void loadLocalTracks() {
		
		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				FileObject object = FileManager.readFromFile();
				ArrayList<JSONTrack> trackList = object.getTracks();
				Iterator<JSONTrack> iter = trackList.iterator();
				
				while(iter.hasNext()){
					JSONTrack track = iter.next();
					if(!uniquenessList.contains((String)track.getId())){
						addButton(track.jsonify());
						tracks.add(track.jsonify());
						uniquenessList.add(track.getId());
						//System.out.println(car.jsonify());
					}
				}
				
				System.out.println("loaded from file: " + uniquenessList.size() );
				return null;
			}
			
		});

		refreshAllButtons();
	}

	private void initNavigationButtons() {
		exit = new Button("exit") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new MainMenuScreen(gameLoader));
			}
		};

		exit.setPosition(0, 0);
		stage.addActor(exit);

	}

	private void downloadCars() {
		
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
							Backendless_Track obj = Backendless_JSONParser
									.processDownloadedTrack(httpResponse
											.getResultAsString());

							//Iterator<String> iter = obj.getData().iterator();

							while (iter.hasNext()) {
								final String track = iter.next();
								final String trackId = JSONTrack.objectify(track).getId();
								//System.out.println(track);
								Globals.runOnUIThread(new Runnable() {

									@Override
									public void run() {
										if(!uniquenessList.contains(trackId)){
											addButton(track);
											uniquenessList.add(trackId);
											tracks.add(track);
										}

									}
								});

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
							// TODO Auto-generated method stub

						}

						@Override
						public void cancelled() {
							// TODO Auto-generated method stub

						}

					});
					while (stall);

					currentOffset += REST.PAGE_SIZE;
				}

				popQueManager.push(new PopQueObject(PopQueObjectType.DELETE));
				
				Globals.runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						writeTracksToFile();						
					}
				});
				

				return null;
			}
		});

	}
	
	private void writeTracksToFile() {
		
		//System.out.println("starting write");
		
		Iterator<String> iter = tracks.iterator();
		ArrayList<JSONTrack> list = new ArrayList<JSONTrack>();
		while(iter.hasNext()){
			String track = iter.next();
			list.add(JSONTrack.objectify(track));
		}
		
		FileObject fileObject = new FileObject();
		fileObject.setTracks(list);
		
		FileManager.writeToFile(fileObject);
		
	}

	private void initTrackSelector() {
		tracksTable = new TableW();
		tracksTable.setWidth(300);
		// tracksTable.setRotation(-20);
		// tracksTable.setFillParent(true);
		// tracksTable.align(Align.center);

		scrollPane = new ScrollPane(tracksTable);
		// scrollPane.setHeight(Globals.ScreenHeight);
		// scrollPane.setWidth(Globals.ScreenWidth);
		// scrollPane.setOrigin(0, 0);
		scrollPane.setWidth(100);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setFillParent(true);
		scrollPane.setLayoutEnabled(true);
		scrollPane.setTouchable(Touchable.enabled);

		stage.addActor(scrollPane);
	}

	private void initButtons() {


		int count = 0;

		for (Button b : buttons) {
			// b.setRotation(-20);
			// b.setWidth(1000);
			b.invalidate();

			tracksTable.add(b);

			count++;
			if (count > 2) {
				tracksTable.row();
				count = 0;
			}

		}

	}

	private void refreshAllButtons() {
		initTrackSelector();
		initButtons();

		tracksTable.invalidate();
		scrollPane.invalidate();

		initNavigationButtons();
	}

	private void addButton(final String text) {
		
		  Button b = new Button("bla"){
		 
		  @Override public void Clicked() {
			  prefs.putString(GamePreferences.TRACK_MAP_STR, text); 
			  prefs.flush();
		  
			  super.Clicked(); 
		  }
		  
		  };

		buttons.add(b);

		refreshAllButtons();
	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new StretchViewport(Globals.ScreenWidth, Globals.ScreenHeight, camera);
		batch = new SpriteBatch();
		stage = new Stage(vp);

	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = Globals.PixelToMeters(width);
		camera.viewportHeight = Globals.PixelToMeters(height);
		camera.update();
		vp.update(width, height);

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		Globals.updateScreenInfo();

	}

	@Override
	public void render(float delta) {
		renderWorld();
		popQueManager.update();

		//
		 // if(!buttonQue.isEmpty()){ while(!buttonQue.isEmpty()){
		//  System.out.println("here"); tempButton = buttonQue.get(0);
		 // buttonQue.remove(0); buttons.add(tempButton); }
		  
		 // reinitScroll(); }
		 

	}

	private void renderWorld() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		stage.draw();
		stage.act(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}*/
}

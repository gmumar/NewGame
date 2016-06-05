package com.gudesigns.climber;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.GameViewport;
import wrapper.Globals;
import Dialog.DialogBase;
import Dialog.Skins;
import Menu.Button;
import Menu.CarAnimationRunner;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Purchases.GamePurchaseObserver;
import Shader.GameMesh;
import User.User;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class MainMenuScreen implements Screen {

	private GameLoader gameLoader;
	private GameState gameState;
	private CameraManager camera;
	private Stage stage;
	private GameViewport vp;
	private PopQueManager popQueManager;
	private User user;

	private Button builder, playGame, buildTrack, selectTrack, selectCar,
			quickNext, buyCoins, soundControl, tapToPlay,communityCars;

	private static CarAnimationRunner carAnimation;
	
	public MainMenuScreen(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		
		initStage();
		initButtons();
		initUser();

		this.gameState = new GameState(gameLoader, user);

		carAnimation = new CarAnimationRunner(gameState);
	
	}

	private void initUser() {
		user = User.getInstance();

	}

	private void initButtons() {
		
		tapToPlay = new Button("Tap To Play") {
			@Override
			public void Clicked() {
				/*Action completeAction = new Action() {
					public boolean act(float delta) {
						gameLoader.setScreen(new GameModeScreen(gameState));
						return true;
					}
				};*/
				
				gameLoader.setScreen(new GameModeScreen(gameState));
				
				/*DialogBase d = new DialogBase("black",Skins.loadDefault(gameLoader, 1), "default");
				d.setBackground("blackOut");
				d.setFillParent(true);
				d.setColor(1, 1, 1, 0);
				d.addAction(new SequenceAction(Actions.fadeIn(0.25f),completeAction));
				stage.addActor(d);*/
				
			}
		};
		tapToPlay.setPosition(Globals.ScreenWidth/2 - 50, Globals.ScreenHeight/2);
		tapToPlay.setWidth(100);
		tapToPlay.setHeight(50);
		stage.addActor(tapToPlay);
		
		soundControl = new Button("Sound Control") {
			@Override
			public void Clicked() {
				popQueManager.push(new PopQueObject(PopQueObjectType.SOUND));
			}
		};
		soundControl.setPosition(Globals.ScreenWidth-50, Globals.ScreenHeight-50);
		soundControl.setWidth(50);
		soundControl.setHeight(50);
		stage.addActor(soundControl);

		builder = new Button("builder") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new CarBuilderScreen(gameState));
			}
		};

		builder.setPosition(0, 0);
		stage.addActor(builder);

		quickNext = new Button("Next") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new ForrestTrackSelectorScreen(gameState));
			}
		};

		quickNext.setPosition(Globals.ScreenWidth - 150, 25);
		quickNext.setWidth(75);
		quickNext.setHeight(75);
		stage.addActor(quickNext);

		playGame = new Button("playGame") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new GamePlayScreen(gameState));
			}
		};

		playGame.setPosition(100, 0);
		stage.addActor(playGame);

		if (Globals.ADMIN_MODE) {
			buildTrack = new Button("build track") {
				@Override
				public void Clicked() {
					gameLoader.setScreen(new TrackBuilderScreen(gameState));
				}
			};

			buildTrack.setPosition(200, 0);
			stage.addActor(buildTrack);
			
			
			communityCars = new Button("community backend") {
				@Override
				public void Clicked() {
					gameLoader.setScreen(new ADMINCarSelectorScreen(gameState));
				}
			};

			communityCars.setPosition(200, 100);
			stage.addActor(communityCars);
		}

		selectTrack = new Button("select track") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new ForrestTrackSelectorScreen(gameState));
			}
		};

		selectTrack.setPosition(0, 100);
		stage.addActor(selectTrack);

		selectCar = new Button("select car") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new CarSelectorScreen(gameState));
			}
		};

		selectCar.setPosition(100, 100);
		stage.addActor(selectCar);

		buyCoins = new Button("buy coins") {
			@Override
			public void Clicked() {

				popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));

				Globals.runOnUIThread(new Runnable() {

					@Override
					public void run() {
						gameLoader.getPlatformResolver().requestInformation(
								new GamePurchaseObserver() {
									@Override
									public void handleRecievedInformation(
											Purchases.GamePurchaseResult gamePurchaseResult) {
										popQueManager.push(new PopQueObject(
												PopQueObjectType.DELETE));
										
										popQueManager.push(new PopQueObject(
												PopQueObjectType.STORE_BUY));
									}
								});
					}
				});

				// gameLoader.getPlatformResolver().requestPurchase("pack_one");
			}
		};

		buyCoins.setPosition(25, Globals.ScreenHeight - 75);
		buyCoins.setHeight(50);
		stage.addActor(buyCoins);

	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new GameViewport(Globals.ScreenWidth, Globals.ScreenHeight,
				camera);
		vp.apply();
		// batch = new SpriteBatch();
		stage = new Stage(vp);

		popQueManager = new PopQueManager(gameLoader, stage);

	}

	@Override
	public void resize(int width, int height) {

		camera.viewportWidth = Globals.PixelToMeters(width);
		camera.viewportHeight = Globals.PixelToMeters(height);
		camera.update();

		vp.update(width, height);
		carAnimation.resize(width, height);
	
		camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		GameMesh.create();
		Globals.updateScreenInfo();

	}

	@Override
	public void render(float delta) {
		camera.update();

		Gdx.gl.glClearColor(Globals.FORREST_GREEN_BG.r, Globals.FORREST_GREEN_BG.g,
				Globals.FORREST_GREEN_BG.b, Globals.FORREST_GREEN_BG.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		carAnimation.draw(delta);
		stepStage();

		popQueManager.update();

	}

	private void stepStage() {

		// batch.setProjectionMatrix(camera.combined);
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
		stage.dispose();
		GameMesh.destroy();

	}

	@Override
	public void dispose() {
		carAnimation.dispose();
		camera = null;
	}
}

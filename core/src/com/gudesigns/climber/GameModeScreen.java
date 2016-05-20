package com.gudesigns.climber;

import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.GameViewport;
import wrapper.Globals;
import Dialog.Skins;
import Menu.PopQueManager;
import Menu.ScreenType;
import Menu.Bars.BottomBar;
import Menu.Bars.TitleBar;
import Menu.Buttons.ModeButton;
import Menu.Buttons.ModeButton.ModeButtonTypes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameModeScreen implements Screen {

	private CameraManager camera;
	private SpriteBatch batch;
	private Stage stage;
	private GameViewport vp;
	private PopQueManager popQueManager;

	private Button adventrueMode, infinityMode;

	private Table buttonHolder, base;

	private Skin skin;
	private GameLoader gameLoader;

	public GameModeScreen(final GameState gameState) {
		gameLoader = gameState.getGameLoader();
		skin = Skins.loadDefault(gameLoader, 1);

		initStage();

		popQueManager = new PopQueManager(gameLoader, stage);

		base = new Table(skin);
		base.setFillParent(true);
		// base.pad(25);

		TitleBar.create(base, ScreenType.MODE_SCREEN, popQueManager, gameState, null,
				true);

		// Main Buttons
		buttonHolder = new Table(skin);

		adventrueMode = ModeButton.create(skin, gameLoader,
				ModeButtonTypes.ADVENTURE, true);
		adventrueMode.setChecked(true);
		adventrueMode.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameLoader.setScreen(new TrackSelectorScreen(gameState));
			}
		});

		infinityMode = ModeButton.create(skin, gameLoader,
				ModeButtonTypes.INFINITY, true);
		infinityMode.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameLoader.setScreen(new TrackSelectorScreen(gameState));
			}
		});

		ButtonGroup<Button> group = new ButtonGroup<Button>();
		group.add(infinityMode);
		group.add(adventrueMode);
		group.setMaxCheckCount(1);
		group.setMinCheckCount(1);

		buttonHolder.add(adventrueMode).pad(5).height(Globals.baseSize * 10)
				.width(Globals.baseSize * 8).center();
		buttonHolder.add(infinityMode).pad(5).height(Globals.baseSize * 10)
				.width(Globals.baseSize * 8).center();

		base.add(buttonHolder).expand().fill();

		BottomBar.create(base, ScreenType.MODE_SCREEN, gameState, true);

		// Animations.fadeInAndSlideSide(base);

		stage.addActor(base);

	}

	private void initStage() {

		camera = new CameraManager(Globals.ScreenWidth, Globals.ScreenHeight);
		camera.zoom = 1f;
		camera.setToOrtho(false, Globals.ScreenWidth, Globals.ScreenHeight);
		camera.update();

		vp = new GameViewport(Globals.ScreenWidth, Globals.ScreenHeight,
				camera);
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

	}

	private void renderWorld() {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.enableBlending();

		batch.setProjectionMatrix(camera.combined);
		stage.draw();
		stage.act(Gdx.graphics.getDeltaTime());

		batch.disableBlending();
		Gdx.gl20.glDisable(GL20.GL_BLEND);

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

	}
}

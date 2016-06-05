package Dialog;

import wrapper.Globals;
import Menu.Animations;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.gudesigns.climber.GameLoader;

public class TutorialDialog {

	final public static int TEXT_PADDING = 10;
	final public static int LEFT_FRAME = 150;

	private DialogBase base;
	private Skin skin;
	private GameLoader gameLoader;

	private float stageWidth, stageHeight;

	public TutorialDialog(GameLoader gameLoader,
			final PopQueObject popQueObject, Stage stage) {
		super();
		skin = Skins.loadDefault(gameLoader, 0);
		this.gameLoader = gameLoader;

		stageWidth = stage.getWidth();
		stageHeight = stage.getHeight();

		CreateDialog(popQueObject);
	}

	public DialogBase CreateDialog(final PopQueObject popQueObject) {

		skin = Skins.loadDefault(gameLoader, 0);

		base = new DialogBase("", skin, "tutorialDialog");
		// base.clear();
		// base.setMovable(false);
		// base.setModal(true);
		base.setFillParent(true);
		base.setBackground("transparent");
		base.setTouchable(Touchable.enabled);

		return base;
	}

	public void setStep(PopQueObject popQueObject) {

		if (popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_INTRO) {
			intro(gameLoader, skin);
		} else if (popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP1) {
			step1(gameLoader, skin);
		} else if (popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP2) {
			step2(gameLoader, skin);
		} else if (popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP3) {
			step3(gameLoader, skin);
		} else if (popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP4) {
			step4(gameLoader, skin);
		} else if (popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP5) {
			step5(gameLoader, skin);
		}

		addButtons(popQueObject);

	}

	private void intro(GameLoader gameLoader, Skin skin) {
		base.clear();

		Table bg = new Table(skin);

		bg.setFillParent(true);
		bg.setBackground("dialogDimer");
		base.addActor(bg);

		Table t1 = createBallon(skin, gameLoader, "Welcome to the Car Builder",
				4);
		t1.setPosition(stageWidth / 2 - 143, 370);
		t1.align(Align.center);
		base.addActor(t1);

		Table t2 = createBallon(skin, gameLoader,
				"This tutorial will show you\nhow to build your first car", 4);
		t2.setPosition(stageWidth / 2 - 140, 270);
		t2.align(Align.center);
		base.addActor(t2);

	}

	// parts
	private void step1(GameLoader gameLoader, Skin skin) {
		base.clear();

		Table bg = new Table(skin);

		bg.setFillParent(true);
		bg.setBackground("dialogDimer");
		bg.setPosition(LEFT_FRAME - 40, 0);
		base.addActor(bg);

		Table bg1 = new Table(skin);

		bg1.setBackground("dialogDimer");
		bg1.setPosition(0, 420);
		bg1.setSize(LEFT_FRAME - 40, 60);
		base.addActor(bg1);

		Table t = createBallon(skin, gameLoader, "", 0);
		t.setPosition(LEFT_FRAME, 370);
		base.addActor(t);

		Table t1 = createBallon(
				skin,
				gameLoader,
				"Use these buttons to add and\n" + "remove parts from your car",
				0);
		t1.setPosition(LEFT_FRAME, 270);
		base.addActor(t1);

		Table t2 = createBallon(skin, gameLoader, "", 0);
		t2.setPosition(LEFT_FRAME, 170);
		base.addActor(t2);

		Table t3 = createBallon(skin, gameLoader, "", 0);
		t3.setPosition(LEFT_FRAME, 70);
		base.addActor(t3);

	}

	// car draging
	private void step2(GameLoader gameLoader, Skin skin) {
		base.clear();

		Table bg = new Table(skin);

		bg.setSize(stageWidth, 100);
		bg.setBackground("dialogDimer");
		bg.setPosition(0, stageHeight - 100);
		base.addActor(bg);

		Table bg1 = new Table(skin);

		bg1.setBackground("dialogDimer");
		bg1.setPosition(0, 0);
		bg1.setSize(LEFT_FRAME, stageHeight - 100);
		base.addActor(bg1);

		Table bg2 = new Table(skin);

		bg2.setBackground("dialogDimer");
		bg2.setPosition(stageWidth - LEFT_FRAME, 0);
		bg2.setSize(LEFT_FRAME, stageHeight - 100);
		base.addActor(bg2);

		Table t = createBallon(skin, gameLoader, "", 3);
		t.setPosition(stageWidth / 2 - Globals.baseSize / 2, stageHeight - 120);
		base.addActor(t);

		Table t1 = createBallon(skin, gameLoader,
				"Click on a part below to move it", 4);
		t1.setPosition(stageWidth / 2 - 160, stageHeight - 100);
		base.addActor(t1);

	}

	// part modifier menu
	private void step3(GameLoader gameLoader, Skin skin) {
		base.clear();

		Table bg = new Table(skin);

		bg.setSize(stageWidth - LEFT_FRAME, stageHeight);
		bg.setBackground("dialogDimer");
		bg.setPosition(0, 0);
		base.addActor(bg);

		Table bg1 = new Table(skin);

		bg1.setBackground("dialogDimer");
		bg1.setPosition(stageWidth - LEFT_FRAME, stageHeight - 60);
		bg1.setSize(LEFT_FRAME, 60);
		base.addActor(bg1);

		Table t = createBallon(skin, gameLoader, "", 2);
		t.setPosition(stageWidth - LEFT_FRAME, 370);
		base.addActor(t);

		Table t0 = createBallon(skin, gameLoader, "Use this menu to rotate\n"
				+ "and upgrade parts", 4);
		t0.setPosition(stageWidth - LEFT_FRAME - 270, 270);
		base.addActor(t0);

		Table t1 = createBallon(skin, gameLoader, "", 2);
		t1.setPosition(stageWidth - LEFT_FRAME, 270);
		base.addActor(t1);

		Table t2 = createBallon(skin, gameLoader, "", 2);
		t2.setPosition(stageWidth - LEFT_FRAME, 170);
		base.addActor(t2);

		Table t3 = createBallon(skin, gameLoader, "", 2);
		t3.setPosition(stageWidth - LEFT_FRAME, 70);
		base.addActor(t3);

	}

	// buy coins
	private void step4(GameLoader gameLoader, Skin skin) {
		base.clear();

		Table bg = new Table(skin);

		bg.setSize(stageWidth, stageHeight);
		bg.setBackground("dialogDimer");
		bg.setPosition(0, -60);
		base.addActor(bg);

		Table t = createBallon(skin, gameLoader, "", 1);
		t.setPosition(LEFT_FRAME - 30, 370);
		base.addActor(t);

		Table t0 = createBallon(skin, gameLoader,
				"Click here to buy more coins", 4);
		t0.setPosition(LEFT_FRAME - 120, 350);
		base.addActor(t0);

	}

	// upload car
	private void step5(GameLoader gameLoader, Skin skin) {
		base.clear();

		Table bg = new Table(skin);

		bg.setSize(stageWidth, stageHeight);
		bg.setBackground("dialogDimer");
		bg.setPosition(0, -60);
		base.addActor(bg);

		Table t = createBallon(skin, gameLoader, "", 1);
		t.setPosition(stageWidth - LEFT_FRAME + 35, 370);
		base.addActor(t);

		Table t0 = createBallon(skin, gameLoader,
				"Click here to share your creation", 4);
		t0.setPosition(stageWidth - LEFT_FRAME - 220, 350);
		base.addActor(t0);

	}

	// 0 = left, 1 = top, 2 = right , 3 = down
	private static Table createBallon(Skin skin, GameLoader gameLoader,
			String txt, int direction) {
		// base.clear();

		Table ballon = new Table(skin);
		Label text = new Label(txt, skin, "title");
		text.setWrap(true);

		Image arrow = null;

		if (direction == 0) {
			arrow = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/right.png"));
			Animations.slideAndBlink(arrow, direction);

			ballon.add(arrow).padLeft(20).width(Globals.baseSize)
					.height(Globals.baseSize);

			ballon.add(text).pad(TEXT_PADDING).center();

		} else if (direction == 1) {
			arrow = new Image(
					gameLoader.Assets.getFilteredTexture("menu/icons/up.png"));
			Animations.slideAndBlink(arrow, direction);

			ballon.add(arrow).padLeft(20).width(Globals.baseSize)
					.height(Globals.baseSize);
			ballon.row();
			ballon.add(text).pad(TEXT_PADDING).center();
		} else if (direction == 2) {
			arrow = new Image(
					gameLoader.Assets.getFilteredTexture("menu/icons/left.png"));
			Animations.slideAndBlink(arrow, direction);

			ballon.add(text).pad(TEXT_PADDING).center();

			ballon.add(arrow).padLeft(20).width(Globals.baseSize)
					.height(Globals.baseSize);
		} else if (direction == 3) {
			arrow = new Image(
					gameLoader.Assets.getFilteredTexture("menu/icons/down.png"));
			Animations.slideAndBlink(arrow, direction);

			ballon.add(text).pad(TEXT_PADDING).center();
			ballon.row();

			ballon.add(arrow).padLeft(20).width(Globals.baseSize)
					.height(Globals.baseSize);

		} else if (direction == 4) {

			ballon.add(text).pad(TEXT_PADDING).center();

		}

		ballon.defaults().center();
		return ballon;
	}

	private void addButtons(final PopQueObject popQueObject) {
		TextButton skipTutorial = new TextButton("Skip Tutorial",
				Skins.loadDefault(gameLoader, 1), "noButton");
		skipTutorial.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getTwoButtonFlowContext().failedTwoButtonFlow(0);
				base.hide();
				super.clicked(event, x, y);
			}

		});

		int width = (int) (stageWidth * (0.35f));
		int unit = (int) (stageWidth * (0.1));

		skipTutorial.setPosition(unit, 90);
		skipTutorial.setSize(width, Globals.baseSize * 2);
		base.addActor(skipTutorial);

		TextButton nextStep = new TextButton("Next", Skins.loadDefault(
				gameLoader, 1), "yesButton");

		nextStep.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Transition to the next step
				if (popQueObject
						.getType()
						.toString()
						.compareTo(
								PopQueObjectType.TUTORIAL_BUILDER_SCREEN_INTRO
										.toString()) == 0) {
					popQueObject
							.getTwoButtonFlowContext()
							.successfulTwoButtonFlow(
									PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP1
											.toString());
				} else if (popQueObject
						.getType()
						.toString()
						.compareTo(
								PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP1
										.toString()) == 0) {
					popQueObject
							.getTwoButtonFlowContext()
							.successfulTwoButtonFlow(
									PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP2
											.toString());
				} else if (popQueObject
						.getType()
						.toString()
						.compareTo(
								PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP2
										.toString()) == 0) {
					popQueObject
							.getTwoButtonFlowContext()
							.successfulTwoButtonFlow(
									PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP3
											.toString());
				} else if (popQueObject
						.getType()
						.toString()
						.compareTo(
								PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP3
										.toString()) == 0) {
					popQueObject
							.getTwoButtonFlowContext()
							.successfulTwoButtonFlow(
									PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP4
											.toString());
				} else if (popQueObject
						.getType()
						.toString()
						.compareTo(
								PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP4
										.toString()) == 0) {
					popQueObject
							.getTwoButtonFlowContext()
							.successfulTwoButtonFlow(
									PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP5
											.toString());
				} else if (popQueObject
						.getType()
						.toString()
						.compareTo(
								PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP5
										.toString()) == 0) {
					base.remove();
				}
				super.clicked(event, x, y);
			}

		});

		nextStep.setPosition(width + unit * 2, 90);
		nextStep.setSize(width, Globals.baseSize * 2);
		// nextStep.setFillParent(true);
		base.addActor(nextStep);

	}

	public Actor getBase() {
		// TODO Auto-generated method stub
		return base;
	}

}

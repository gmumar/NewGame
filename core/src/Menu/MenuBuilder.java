package Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.CameraManager;
import wrapper.GamePhysicalState;
import wrapper.GameState;
import wrapper.Globals;
import Assembly.AssemblyRules;
import Component.Component;
import Component.Component.PropertyTypes;
import Component.ComponentBuilder;
import Component.ComponentNames;
import Dialog.Skins;
import JSONifier.JSONCar;
import JSONifier.JSONCompiler;
import JSONifier.JSONComponent;
import JSONifier.JSONComponentName;
import Menu.PopQueObject.PopQueObjectType;
import Menu.Bars.BarObjects;
import Menu.Bars.TitleBar;
import Menu.Buttons.ButtonLockWrapper;
import Menu.Buttons.CarBuilderButton;
import Menu.Buttons.CarBuilderButton.CarBuilderButtonType;
import RESTWrapper.BackendFunctions;
import User.User;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;

public class MenuBuilder implements  InputProcessor{

	public final static String BUILDER_SELECTED = "builder_selected";
	public final static String BUILDER = "builder";

	private final static float BOX_SIZE = 0.3f;
	public final static float ROTATION_SIZE = 30;
	private final static int LIMIT_BOX_X = -5, LIMIT_BOX_Y = -5,
			LIMIT_BOX_X_LEN = 20, LIMIT_BOX_Y_LEN = 20;

	private Stage stage;
	private World world;

	private ButtonLockWrapper spring_but, but, tire_but, delete, rotateLeft,
			rotateRight, build, upload, levelUp, levelDown;

	private Label partLevelText, partNameLabel;

	private Vector3 mousePoint = new Vector3();
	private Body hitBody, lastSelected = null, baseObject;

	private CameraManager camera;

	private boolean mouseJoined = false;

	private ArrayList<Component> parts = new ArrayList<Component>();
	private JSONCompiler compiler = new JSONCompiler();
	private AssemblyRules assemblyRules = new AssemblyRules();
	private HashMap<String, Integer> jointTypes = new HashMap<String, Integer>();

	private HashMap<String, Integer> componentCounts = new HashMap<String, Integer>();;

	private GameLoader gameLoader;

	private Vector2 relativeVector = new Vector2();

	private static ShapeRenderer fixtureRenderer;

	private User user;
	volatile private int partLevel = 1;

	private MenuBuilder instance;

	private Label titleBar;
	private Integer currentMoney;
	private final float buttonWidth = 110;

	// private final float buttonHeight = 100;

	// private FixtureDef mouseFixture;
	// BaseActor mouseActor;

	public MenuBuilder(final GamePhysicalState gamePhysicalState, Stage stage,
			CameraManager secondCamera, ShapeRenderer shapeRenderer,
			final User user, final PopQueManager popQueManager) {

		this.world = gamePhysicalState.getWorld();
		this.gameLoader = gamePhysicalState.getGameLoader();
		this.stage = stage;
		this.camera = secondCamera;
		this.mouseJoined = false;
		this.user = User.getInstance();
		this.instance = this;

		Skin skin = Skins.loadDefault(gameLoader, 1);
		// this.user = user;
		MenuBuilder.fixtureRenderer = shapeRenderer;

		BodyDef bodyDef = new BodyDef();
		world.createBody(bodyDef);

		drawBox(LIMIT_BOX_X, LIMIT_BOX_Y, LIMIT_BOX_X_LEN, LIMIT_BOX_Y_LEN);

		setUpPreBuiltCar();

		// Add life regardless
		// addLife();
		/**/

		//

		Table base = new Table();
		base.setFillParent(true);

		titleBar = TitleBar.create(base, ScreenType.CAR_BUILDER, popQueManager,
				new GameState(gameLoader, user), new BarObjects(this), false);

		currentMoney = user.getMoney();

		Table menuHolders = new Table();

		Table leftMenu = new Table();

		Table rightMenu = new Table();

		/*
		 * but = new Button("Small bar") {
		 * 
		 * @Override public void Clicked() { addSmallBar(-1); }
		 * 
		 * };
		 */

		but = CarBuilderButton.create(gameLoader, CarBuilderButtonType.BAR,
				false, false);
		but.button.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				addSmallBar(-1);
			}

		});

		// but.setPosition(0, 200);
		leftMenu.add(but.button).width(buttonWidth).expandY().fillY();
		leftMenu.row();

		tire_but = CarBuilderButton.create(gameLoader,
				CarBuilderButtonType.WHEEL, false, false);
		tire_but.button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				addTire(-1);

			}

		});

		// tire_but.setPosition(0, 0);
		leftMenu.add(tire_but.button).width(buttonWidth).expandY().fillY();
		leftMenu.row();

		/*
		 * spring_but = new Button("spring") {
		 * 
		 * @Override public void Clicked() { addSpring(-1);
		 * 
		 * } };
		 */

		spring_but = CarBuilderButton.create(gameLoader,
				CarBuilderButtonType.SPRING, false, false);
		spring_but.button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				addSpring(-1);

			}

		});

		// spring_but.setPosition(0, 100);
		leftMenu.add(spring_but.button).width(buttonWidth).expandY().fillY();
		leftMenu.row();

		delete = CarBuilderButton.create(gameLoader,
				CarBuilderButtonType.DELETE, false, false);
		delete.button.addListener(new ActorGestureListener() {

			@Override
			public boolean longPress(Actor actor, float x, float y) {

				ArrayList<Component> partsCopy = new ArrayList<Component>(parts);
				Body body = null;
				for (Component part : partsCopy) {
					body = part.getObject().getPhysicsBody();

					if (body == null || body.getUserData() == null) {
						continue;
					}

					if (((JSONComponentName) body.getUserData()).getBaseName()
							.compareTo(ComponentNames.LIFE) == 0) {
						continue;
					}
					lastSelected = body;
					deleteLastSelected();
				}

				return super.longPress(actor, x, y);
			}

			@Override
			public void tap(InputEvent event, float x, float y, int count,
					int button) {
				deleteLastSelected();
				super.tap(event, x, y, count, button);
			}

		});

		// delete.setPosition(100, 0);
		// delete.setHeight(50);
		// delete.setWidth(50);
		leftMenu.add(delete.button).width(buttonWidth).expandY().fillY();

		menuHolders.add(leftMenu).left().expand().fillY().top();

		/*
		 * zoomIn = new Button("+") {
		 * 
		 * @Override public void Clicked() { camera.zoom -= 0.01;
		 * camera.update(); } };
		 * 
		 * zoomIn.setPosition(Globals.ScreenWidth - 100, 50);
		 * zoomIn.setHeight(50); stage.addActor(zoomIn);
		 * 
		 * zoomOut = new Button("-") {
		 * 
		 * @Override public void Clicked() { camera.zoom += 0.01;
		 * camera.update(); } };
		 * 
		 * zoomOut.setPosition(Globals.ScreenWidth - 100, 0);
		 * zoomOut.setHeight(50); stage.addActor(zoomOut);
		 */

		build = CarBuilderButton.create(gameLoader, CarBuilderButtonType.PLAY,
				false, false);
		build.button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (buildCar()) {
					gameLoader.setScreen(new GamePlayScreen(new GameState(
							gameLoader, user)));
					Destroy();
				}
			}

		});

		build.button.setPosition(0, Globals.ScreenHeight - 100);
		build.button.setHeight(50);
		// stage.addActor(build);

		upload = new ButtonLockWrapper(new Button("^") {
			@Override
			public void Clicked() {
				if (buildCar()) {
					BackendFunctions.uploadCar(user.getCurrentCar());
				}
			}

		}, false);

		upload.button.setPosition(0, Globals.ScreenHeight - 50);
		upload.button.setHeight(50);
		// stage.addActor(upload);

		rotateLeft = CarBuilderButton.create(gameLoader,
				CarBuilderButtonType.ROTATE_LEFT, false, false);
		rotateLeft.button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (lastSelected != null
						&& ((JSONComponentName) lastSelected.getUserData())
								.getBaseName().compareTo(
										ComponentNames.SPRINGJOINT) != 0
						&& ((JSONComponentName) lastSelected.getUserData())
								.getBaseName().compareTo(ComponentNames.AXLE) != 0) {
					lastSelected.setTransform(lastSelected.getPosition(),
							lastSelected.getAngle() + ROTATION_SIZE
									* MathUtils.degreesToRadians);
				}
			}

		});

		rotateLeft.button.setPosition(Globals.ScreenWidth - 50, 150);
		rotateLeft.button.setHeight(50);
		rotateLeft.button.setWidth(50);
		// stage.addActor(rotateLeft);
		rightMenu.add(rotateLeft.button).width(buttonWidth).expandY().fillY();
		rightMenu.row();

		rotateRight = CarBuilderButton.create(gameLoader,
				CarBuilderButtonType.ROTATE_RIGHT, false, false);
		rotateRight.button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (lastSelected != null
						&& ((JSONComponentName) lastSelected.getUserData())
								.getBaseName().compareTo(
										ComponentNames.SPRINGJOINT) != 0
						&& ((JSONComponentName) lastSelected.getUserData())
								.getBaseName().compareTo(ComponentNames.AXLE) != 0) {
					lastSelected.setTransform(lastSelected.getPosition(),
							lastSelected.getAngle() - ROTATION_SIZE
									* MathUtils.degreesToRadians);
				}
			}
		});

		rightMenu.add(rotateRight.button).width(buttonWidth).expandY().fillY();
		rightMenu.row();
		// stage.addActor(rotateRight);

		Table partInfo = new Table(skin);
		partInfo.setBackground("white");

		Label partLevelLabel = new Label("Level", skin);
		partInfo.add(partLevelLabel).pad(5);
		partInfo.row();

		partLevelText = new Label("1", skin);
		partInfo.add(partLevelText).expandY().fillY();
		partInfo.row();

		partNameLabel = new Label("name", skin);
		partInfo.add(partNameLabel).pad(5);

		rightMenu.add(partInfo).fill().height(Globals.baseSize * 5);
		rightMenu.row();

		levelUp = CarBuilderButton.create(gameLoader,
				CarBuilderButtonType.LEVEL_UP, false, false);
		levelUp.button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (lastSelected != null) {
					JSONComponentName lastSelectName = (JSONComponentName) lastSelected
							.getUserData();
					int level = user.getLevel(lastSelectName.getBaseName());

					if (partLevel + 1 > level
							&& !(partLevel >= User.getMaxLevel(lastSelectName
									.getBaseName()))) {

						((JSONComponentName) lastSelected.getUserData())
								.setLevel(partLevel);
						popQueManager.push(new PopQueObject(
								PopQueObjectType.BUY, lastSelectName
										.getBaseName(), partLevel + 1, instance));
						return;
					}

					partLevel += 1;

					if (partLevel >= User.getMaxLevel(lastSelectName
							.getBaseName())) {
						partLevel = User.getMaxLevel(lastSelectName
								.getBaseName());
					}

					setLastSelectedLevel(partLevel);

				}
			}
		});

		rightMenu.add(levelUp.button).expandY().fillY().width(buttonWidth);
		rightMenu.row();
		// stage.addActor(levelUp);

		levelDown = CarBuilderButton.create(gameLoader,
				CarBuilderButtonType.LEVEL_DOWN, false, false);
		levelDown.button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (lastSelected != null) {
					partLevel -= 1;
					if (partLevel <= 1) {
						partLevel = 1;
					}

					setLastSelectedLevel(partLevel);
				}
			}
		});

		rightMenu.add(levelDown.button).expandY().fillY().width(buttonWidth);
		rightMenu.row();

		rightMenu.add(build.button).width(buttonWidth).expandY().fillY();
		rightMenu.row();
		// stage.addActor(levelDown);

		menuHolders.add(rightMenu).right().expand().fillY().top();

		base.add(menuHolders).fill().expand();

		// JSONComponentName mouseName = new JSONComponentName();
		// mouseName.setBaseName("mouseActor");
		// mouseActor = new BaseActor(mouseName, gameState);
		// mouseActor.setSensor();
		// mouseFixture = ComponentBuilder.buildMount(new
		// Vector2(mousePoint.x,mousePoint.y), 1, true);
		// mouseActor.getPhysicsBody().createFixture(mouseFixture);

		stage.addActor(base);

	}

	private void deleteLastSelected() {
		if (lastSelected == null) {
			System.out.println("MenuBuilder: delete is null");
			return;
		}

		System.out.println("MenuBuilder: deleting "
				+ ((JSONComponentName) lastSelected.getUserData())
						.getBaseName());

		// ----------------------- HACK ----------------------
		if (((JSONComponentName) lastSelected.getUserData()).getBaseName()
				.compareTo(ComponentNames.AXLE) == 0) {
			Array<JointEdge> joints = lastSelected.getJointList();
			JointEdge joint = joints.first();
			lastSelected = joint.other;
		}
		// ---------------------------------------------------

		Array<Fixture> fixtures = lastSelected.getFixtureList();
		Fixture fixture = null;

		for (Fixture fixtureIter : fixtures) {
			if (fixtureIter.getUserData() == null)
				continue;
			fixture = fixtureIter;
			break;
		}

		if (fixture == null)
			return;
		if (fixture.getUserData() == null) {
			world.destroyBody(fixture.getBody());
			return;
		}

		Component toRemove = lookupLastPart(lastSelected);

		if (toRemove != null) {
			if (toRemove.getJointBodies() != null) {
				for (BaseActor joinedPart : toRemove.getJointBodies()) {
					parts.remove(lookupLastPart(joinedPart.getPhysicsBody()));
				}
			}

			parts.remove(toRemove);
		} else {
			System.out.println("MenuBuilder: to remove null");
		}
		Array<JointEdge> joints = lastSelected.getJointList();

		for (JointEdge joint : joints) {
			world.destroyBody(joint.other);
		}

		world.destroyBody(lastSelected);
		lastSelected = null;

	}

	private ArrayList<Component> addLife() {
		incrementCount(ComponentNames.LIFE);
		Component c = ComponentBuilder.buildLife(new GamePhysicalState(world,
				gameLoader), 1, true);
		// c.setUpForBuilder(ComponentNames.LIFE
		// + Assembler.NAME_ID_SPLIT
		// + componentCounts.get(ComponentNames.LIFE));
		JSONComponentName name = new JSONComponentName();
		name.setBaseName(ComponentNames.LIFE);
		name.setComponentId(Integer.toString(componentCounts
				.get(ComponentNames.LIFE)));
		name.setMountId("*");
		c.setUpForBuilder(name, partLevel);
		System.out.println("MenuBuilder: " + name);

		baseObject = c.getObject().getPhysicsBody();// lastSelected =
		parts.add(c);

		ArrayList<Component> tmp = new ArrayList<Component>();
		tmp.add(c);
		return tmp;
	}

	private ArrayList<Component> addSmallBar(Integer inLevel) {
		if (inLevel == -1) {
			partLevel = user.getSmallBarLevel();
		} else {
			partLevel = inLevel;
		}
		System.out.println(partLevel);
		incrementCount(ComponentNames.BAR3);
		Component c = ComponentBuilder.buildBar3(new GamePhysicalState(world,
				gameLoader), partLevel, true);
		// c.setUpForBuilder(ComponentNames.BAR3
		// + Assembler.NAME_ID_SPLIT
		// + componentCounts.get(ComponentNames.BAR3));
		JSONComponentName name = new JSONComponentName();
		name.setBaseName(ComponentNames.BAR3);
		name.setComponentId(Integer.toString(componentCounts
				.get(ComponentNames.BAR3)));
		name.setMountId("*");
		name.setLevel(partLevel);
		c.setUpForBuilder(name, partLevel);

		System.out.println("MenuBuilder: " + name);
		lastSelected = c.getObject().getPhysicsBody();
		parts.add(c);
		// lastSelected =
		// parts.get(parts.size()-1).getObject().getPhysicsBody();
		ArrayList<Component> tmp = new ArrayList<Component>();
		tmp.add(c);
		return tmp;
	}

	private ArrayList<Component> addTire(Integer inLevel) {
		if (inLevel == -1) {
			partLevel = user.getTireLevel();
		} else {
			partLevel = inLevel;
		}

		System.out.println(partLevel);
		incrementCount(ComponentNames.TIRE);
		Component c = ComponentBuilder.buildTire(new GamePhysicalState(world,
				gameLoader), partLevel, true);
		// c.setUpForBuilder(ComponentNames.TIRE
		// + Assembler.NAME_ID_SPLIT
		// + componentCounts.get(ComponentNames.TIRE));
		JSONComponentName name = new JSONComponentName();
		name.setBaseName(ComponentNames.TIRE);
		name.setLevel(partLevel);
		name.setMountId("*");
		name.setComponentId(Integer.toString(componentCounts
				.get(ComponentNames.TIRE)));
		c.setUpForBuilder(name, partLevel);
		System.out.println("MenuBuilder: " + name);
		lastSelected = c.getObject().getPhysicsBody();
		parts.add(c);

		// --------------- HACK ------------------
		// if (((JSONComponentName) lastSelected.getUserData()).getBaseName()
		// .compareTo(ComponentNames.TIRE) == 0) {
		// ((JSONComponentName) lastSelected.getUserData())
		// .setBaseName(ComponentNames.AXLE);
		// }
		// ---------------------------------------

		// lastSelected =
		// parts.get(parts.size()-1).getObject().getPhysicsBody();
		ArrayList<Component> tmp = new ArrayList<Component>();
		tmp.add(c);
		return tmp;
	}

	private ArrayList<Component> addSpring(Integer inLevel) {
		boolean hack = false;

		if (inLevel == -1) {
			partLevel = user.getSpringLevel();
		} else {
			hack = true;
			partLevel = inLevel;
		}
		incrementCount(ComponentNames.SPRINGJOINT);

		ArrayList<Component> list = ComponentBuilder
				.buildSpringJoint(new GamePhysicalState(world, gameLoader),
						partLevel, true, hack);

		Component c = list.get(0);
		// c.setUpForBuilder(Assembler.NAME_ID_SPLIT
		// + componentCounts.get(ComponentNames.SPRINGJOINT));
		JSONComponentName name = new JSONComponentName();
		name.setBaseName(ComponentNames.SPRINGJOINT);
		name.setLevel(partLevel);
		name.setMountId("*");
		name.setComponentId(Integer.toString(componentCounts
				.get(ComponentNames.SPRINGJOINT)));
		c.setUpForBuilder(name, partLevel);
		System.out.println("MenuBuilder: " + name);
		lastSelected = c.getObject().getPhysicsBody();
		parts.add(c);

		Component otherComponent = list.get(1);
		otherComponent.setComponentId(Integer.toString(componentCounts
				.get(ComponentNames.SPRINGJOINT)));
		otherComponent.setLevel(Globals.DISABLE_LEVEL);

		parts.add(list.get(1));
		// lastSelected =
		// parts.get(parts.size()-1).getObject().getPhysicsBody();
		return list;
	}

	private void setUpPreBuiltCar() {
		// World tmpWorld = new World(new Vector2(), false);
		JSONCar currentCarJson = new JSONCar();
		currentCarJson = JSONCar.objectify(user.getCurrentCar());
		// HashMap<String, Component> carParts =
		// Assembler.extractComponents(currentCarJson, new
		// GamePhysicalState(tmpWorld,gameLoader),true);

		ArrayList<JSONComponent> jcomponents = currentCarJson
				.getComponentList();

		// Set<Entry<String, Component>> carPartsSet = carParts.entrySet();

		// tmpWorld.dispose();

		ArrayList<String> seenComponents = new ArrayList<String>();
		ArrayList<Component> addedComponents = null;
		ArrayList<JSONComponent> additionalComps = currentCarJson
				.getAddComponents();

		// for(Entry<String, Component> part : carPartsSet){
		for (JSONComponent jcomponent : jcomponents) {
			// Component comp = part.getValue();
			JSONComponentName name = jcomponent.getjComponentName();// comp.getjComponentName();
			String key = name.getBaseId();

			if (seenComponents.contains(key)) {

			} else {

				seenComponents.add(key);

				if (name.getBaseName().compareTo(ComponentNames.AXLE) == 0) {
					addedComponents = addTire(name.getLevel());
				} else if (name.getBaseName().compareTo(
						ComponentNames.SPRINGJOINT) == 0) {
					addedComponents = addSpring(name.getLevel());
				} else if (name.getBaseName().compareTo(ComponentNames.LIFE) == 0) {
					addedComponents = addLife();
					baseObject = addedComponents.get(0).getObject()
							.getPhysicsBody();
				} else if (name.getBaseName().compareTo(ComponentNames.BAR3) == 0) {
					addedComponents = addSmallBar(name.getLevel());
				} else {
					System.exit(1);
				}

				for (Component addedComponent : addedComponents) {
					if (addedComponent.getLevel() == Globals.DISABLE_LEVEL) {
						if (additionalComps != null) {
							for (JSONComponent addcomp : additionalComps) {
								if (name.getBaseId()
										.compareTo(
												addcomp.getjComponentName()
														.getBaseId()) == 0) {

									addedComponent.getObject().setPosition(
											Float.parseFloat(addcomp
													.getProperties()
													.getPositionX()),
											Float.parseFloat(addcomp
													.getProperties()
													.getPositionY()));
									addedComponent.getObject().setRotation(
											Float.parseFloat(addcomp
													.getProperties()
													.getRotation()));
								}
							}
						}
					} else {

						// addedComponent.setPosition(comp.getObject().getPosition().x,
						// comp.getObject().getPosition().y);
						// addedComponent.getObject().setRotation(comp.getObject().getRotation()*MathUtils.radiansToDegrees);
						if (jcomponent.getProperties() != null) {
							addedComponent.applyProperties(
									jcomponent.getProperties(),
									PropertyTypes.BOTH);
						}
						// componentList.get(1)
						// .applyProperties(sourceComponent.getProperties(),
						// PropertyTypes.ABSOLUTE);
					}
				}

			}

			// tmpWorld.dispose();

			/*
			 * Component comp = part.getValue();
			 * 
			 * if(comp.getBaseName().compareTo(ComponentNames.LIFE)==0){
			 * baseObject = comp.getObject().getPhysicsBody(); }
			 * 
			 * incrementCount(comp.getBaseName());
			 * comp.setComponentId(Integer.toString(componentCounts
			 * .get(comp.getBaseName()))); comp.setUpForBuilder(
			 * comp.getjComponentName(), comp.getPartLevel() == null ? 1 :
			 * comp.getPartLevel() ); lastSelected =
			 * comp.getObject().getPhysicsBody(); parts.add(comp);
			 */
		}

		// tmpWorld.dispose();

	}

	private Component lookupLastPart(Body body) {

		/*
		 * Array<Fixture> fixtures = lastSelected.getFixtureList(); Fixture
		 * fixture = null;
		 * 
		 * System.out.println("here " + (JSONComponentName)
		 * lastSelected.getUserData() + " connected size " + fixtures.size);
		 * 
		 * for (Fixture fixtureIter : fixtures) {
		 * 
		 * 
		 * if (fixtureIter.getUserData() == null) continue; fixture =
		 * fixtureIter; break; }
		 * 
		 * if (fixture == null) { //return null; }
		 * 
		 * if (fixture.getUserData() == null) { //return null; }
		 */

		for (Component part : parts) {

			JSONComponentName lookupName = new JSONComponentName(
					(JSONComponentName) body.getUserData());

			// ------------------- HACK -----------------
			if (lookupName.getBaseName().compareTo(ComponentNames.TIRE) == 0) {
				lookupName.setBaseName(ComponentNames.AXLE);
			}
			// ------------------------------------------

			if (part.getjComponentName().getBaseId()
					.compareTo(lookupName.getBaseId()) == 0) {

				return part;

			}

		}

		return null;
	}

	public void drawForBuilder(SpriteBatch batch, float delta) {

		for (Component part : parts) {
			part.draw(batch, BUILDER);
		}

		currentMoney = Animations.money(titleBar, user.getMoney(),
				currentMoney);


		if (lastSelected == null)
			return;

		String name = ((JSONComponentName) lastSelected.getUserData())
				.getBaseId();

		for (Component part : parts) {

			if (part.getjComponentName().getBaseId().compareTo(name) == 0) {
				part.draw(batch, BUILDER_SELECTED);
			}
		}

	}

	private void setLastSelectedLevel(Integer level) {
		partLevel = level;
		Component c = lookupLastPart(lastSelected);

		c.setPartLevel(level);

		((JSONComponentName) lastSelected.getUserData()).setLevel(level);

		Array<JointEdge> joints = c.getObject().getPhysicsBody().getJointList();

		for (JointEdge joint : joints) {
			((JSONComponentName) joint.other.getUserData()).setLevel(level);
		}

	}

	public void successfulBuy() {
		setLastSelectedLevel(user.getLevel(((JSONComponentName) lastSelected
				.getUserData()).getBaseName()));
	}

	public void failedBuy() {

	}

	public void drawShapes(SpriteBatch batch) {

		final Color unConnectedColor = Globals.GREEN;
		final Color ConnectedColorLocked = Globals.RED;
		final Color ConnectedColorUnLocked = Globals.YELLOW;

		partLevelText.setText(Integer.toString(partLevel));

		if (lastSelected != null) {
			partNameLabel.setText(((JSONComponentName) (lastSelected
					.getUserData())).getPrettyName());
		}

		// if(clickedLevelUpBuy){
		//
		// clickedLevelUpBuy = false;
		// }

		Integer jointType;

		ArrayList<String> drawIds = new ArrayList<String>();

		// world.QueryAABB(fixtureCallback, -20,-20, 20, 20);
		// mouseActor.setPosition(mousePoint.x, mousePoint.y);

		Array<Contact> contacts = world.getContactList();
		Iterator<Contact> contactIter = contacts.iterator();

		Iterator<Component> iter = parts.iterator();

		while (contactIter.hasNext()) {
			Contact contact = contactIter.next();
			Fixture fixtureA = contact.getFixtureA();
			Fixture fixtureB = contact.getFixtureB();
			if (contact.isTouching()
					&& isFixtureName((JSONComponentName) fixtureA.getUserData())
					&& isFixtureName((JSONComponentName) fixtureB.getUserData())) {

				if (fixtureA.getUserData() != null
						&& fixtureB.getUserData() != null) {
					syncJointType((JSONComponentName) fixtureA.getUserData(),
							(JSONComponentName) fixtureB.getUserData());
				}

				jointType = lookupJointType(fixtureA);
				if (jointType == 0) {
					drawFixtureSquare(fixtureA, ConnectedColorLocked);
				} else if (jointType == 1) {
					drawFixtureDiamond(fixtureA, ConnectedColorUnLocked);
				}

				drawIds.add(((JSONComponentName) fixtureA.getUserData())
						.getMountedId());

				jointType = lookupJointType(fixtureB);
				if (jointType == 0) {
					drawFixtureSquare(fixtureB, ConnectedColorLocked);
				} else if (jointType == 1) {
					drawFixtureDiamond(fixtureB, ConnectedColorUnLocked);
				}

				drawIds.add(((JSONComponentName) fixtureB.getUserData())
						.getMountedId());

				/*
				 * DistanceJointDef dJoint = new DistanceJointDef();
				 * dJoint.initialize(fixtureA.getBody(), fixtureB.getBody(),
				 * ((CircleShape)fixtureA.getShape()).getPosition(),
				 * ((CircleShape)fixtureB.getShape()).getPosition());
				 * dJoint.length = 0; dJoint.collideConnected = false;
				 * 
				 * // dJoint.frequencyHz = 10; //dJoint.dampingRatio = 0.5f;
				 * 
				 * Joint joint = world.createJoint(dJoint); world.step(1, 100,
				 * 100); world.destroyJoint(joint);
				 */

			}
		}

		while (iter.hasNext()) {
			Component part = iter.next();
			ArrayList<BaseActor> bodies = part.getJointBodies();

			if (bodies == null) {
				Body body = part.getObject().getPhysicsBody();

				for (Fixture fixture : body.getFixtureList()) {
					// jointType = lookupJointType(fixture);
					// if(jointType == -1){
					if (fixture.getUserData() == null) {
						drawFixture(fixture, unConnectedColor);
					} else if (!drawIds.contains(((JSONComponentName) fixture
							.getUserData()).getMountedId())) {
						drawFixture(fixture, unConnectedColor);
					}
					// } else if(jointType == 1){
					// ;
					// }

				}
			} else {

				Iterator<BaseActor> bodiesIter = bodies.iterator();

				while (bodiesIter.hasNext()) {
					BaseActor base = bodiesIter.next();
					Body body = base.getPhysicsBody();

					for (Fixture fixture : body.getFixtureList()) {
						// jointType = lookupJointType(fixture);
						// if(jointType == -1){
						if (fixture.getUserData() == null) {
							drawFixture(fixture, unConnectedColor);
						} else if (!drawIds
								.contains(((JSONComponentName) fixture
										.getUserData()).getMountedId())) {
							drawFixture(fixture, unConnectedColor);
						}
						// } else if(jointType == 1){
						// ;
						// }

					}
				}
			}
			//

		}

	}

	public boolean buildCar() {
		if (assemblyRules.checkBuild(world, baseObject, parts)) {
			user.setCurrentCar(compiler.compile(world, parts, jointTypes));
			return true;
		} else {
			return false;
		}
	}

	private boolean isFixtureName(JSONComponentName jsonComponentName) {

		if (jsonComponentName == null) {
			return false;
		}

		if (jsonComponentName.getMountId() == null
				|| jsonComponentName.getMountId().contains("*")) {
			return false;
		}
		/*
		 * if (jsonComponentName.contains(Assembler.NAME_ID_SPLIT)) { return
		 * true; }
		 */

		if (jsonComponentName.getComponentId() != null) {
			return true;
		}

		return false;
	}

	private void drawFixture(Fixture fix, Color c) {
		final float cirlceSize = 0.1f;

		if (!isFixtureName((JSONComponentName) fix.getUserData())) {
			return;
		}

		Transform transform = fix.getBody().getTransform();
		if (fix.getShape().getType() == Type.Circle) {

			CircleShape shape = (CircleShape) fix.getShape();
			Vector2 vec = new Vector2();
			vec.set(shape.getPosition());
			transform.mul(vec);
			// Gdx.gl.glLineWidth(20 / camera.zoom);
			fixtureRenderer.setColor(c);
			fixtureRenderer.circle(vec.x, vec.y, cirlceSize, 45);

		}
	}

	private void drawFixtureSquare(Fixture fix, Color c) {

		final float squareSize = 0.17f;

		if (!isFixtureName((JSONComponentName) fix.getUserData())) {
			return;
		}

		Transform transform = fix.getBody().getTransform();
		if (fix.getShape().getType() == Type.Circle) {

			CircleShape shape = (CircleShape) fix.getShape();
			Vector2 vec = new Vector2();
			vec.set(shape.getPosition());
			transform.mul(vec);
			// Gdx.gl.glLineWidth(20 / camera.zoom);
			fixtureRenderer.setColor(c);
			fixtureRenderer.box(vec.x - squareSize / 2, vec.y - squareSize / 2,
					0, squareSize, squareSize, 1);
			// fixtureRenderer.circle(vec.x, vec.y, shape.getRadius() + 0.2f,
			// 45);

		}
	}

	private void drawFixtureDiamond(Fixture fix, Color c) {

		final float squareSize = 0.17f;

		if (!isFixtureName((JSONComponentName) fix.getUserData())) {
			return;
		}

		Transform transform = fix.getBody().getTransform();
		if (fix.getShape().getType() == Type.Circle) {

			CircleShape shape = (CircleShape) fix.getShape();
			Vector2 vec = new Vector2();
			vec.set(shape.getPosition());
			transform.mul(vec);
			// Gdx.gl.glLineWidth(20 / camera.zoom);
			fixtureRenderer.setColor(c);
			fixtureRenderer.rect(vec.x - squareSize / 2,
					vec.y - squareSize / 2, squareSize / 2, squareSize / 2,
					squareSize, squareSize, 1, 1, 45, c, c, c, c);
			// fixtureRenderer.box(vec.x - squareSize/2, vec.y - squareSize/2,
			// 0, squareSize, squareSize, 1);
			// fixtureRenderer.circle(vec.x, vec.y, shape.getRadius() + 0.2f,
			// 45);

		}
	}

	private void Destroy() {
		// this.world.dispose();
		Iterator<Component> iter = parts.iterator();
		while (iter.hasNext()) {
			Component comp = iter.next();
			comp.destroyObject();
		}
		stage.dispose();
	}

	private void incrementCount(String name) {
		if (componentCounts.containsKey(name)) {
			componentCounts.put(name, componentCounts.get(name) + 1);
		} else {
			componentCounts.put(name, 0);
		}
	}

	public boolean isJoined() {
		return mouseJoined;
	}

	public void drawBox(float x, float y, float sizex, float sizey) {
		BodyDef box = new BodyDef();

		box.type = BodyDef.BodyType.KinematicBody;
		box.position.set(x, y);
		Body boxBody = world.createBody(box);

		drawEdge(new Vector2(x, y), new Vector2(x, sizey + y), boxBody);
		drawEdge(new Vector2(x, sizey + y), new Vector2(x + sizex, y + sizey),
				boxBody);
		drawEdge(new Vector2(x + sizex, y + sizey), new Vector2(x + sizex, y),
				boxBody);
		drawEdge(new Vector2(x + sizex, y), new Vector2(x, y), boxBody);

	}

	public Fixture drawEdge(Vector2 v1, Vector2 v2, Body body) {
		FixtureDef fixtureDef = new FixtureDef();

		EdgeShape edgeShape = new EdgeShape();
		edgeShape.set(v1, v2);
		fixtureDef.shape = edgeShape;
		// fixtureDef.density = 1;
		fixtureDef.friction = 5;
		fixtureDef.restitution = 0;

		Fixture f = body.createFixture(fixtureDef);
		edgeShape.dispose();

		return f;
	}

	public void handleClick(float f, float g) {
		camera.unproject(mousePoint.set(f, g, 0));
		// mousePoint.x += (mousePoint.x * Globals.AspectRatio)/6;
		world.QueryAABB(mouseClickCallback, mousePoint.x - BOX_SIZE,
				mousePoint.y - BOX_SIZE, mousePoint.x + BOX_SIZE, mousePoint.y
						+ BOX_SIZE);

	}

	public void handleDrag(float f, float g) {
		camera.unproject(mousePoint.set(f, g, 0));
		// mousePoint.x += (mousePoint.x * Globals.AspectRatio)/6;
		if (!isJoined()) {
			return;
		}
		hitBody.setTransform(new Vector2(relativeX(mousePoint.x),
				relativeY(mousePoint.y)), hitBody.getAngle());
	}

	public void handlePan(float f, float g, float deltaX, float deltaY) {
		camera.unproject(mousePoint.set(f, g, 0));
		// mousePoint.x += (mousePoint.x * Globals.AspectRatio)/6 + deltaX/6;

	}

	public void handleRelease(float f, float g) {
		camera.unproject(mousePoint.set(f, g, 0));
		// mousePoint.x += (mousePoint.x * Globals.AspectRatio)/6;
		if (!isJoined()) {
			return;
		}
		// lastSelected = hitBody;
		hitBody = null;
		mouseJoined = false;
	}

	QueryCallback mouseClickCallback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {

			processJointClick(fixture);

			if (fixture.testPoint(mousePoint.x, mousePoint.y)) {
				Body tmpBody = fixture.getBody();

				if (((JSONComponentName) tmpBody.getUserData()).getBaseName()
						.contains(ComponentNames.LIFE)
						|| (((JSONComponentName) tmpBody.getUserData())
								.getBaseName().contains(ComponentNames.TIRE) && fixture
								.getUserData() == null)) {

					return true;
				}

				hitBody = fixture.getBody();

				// ----------------------- HACK ----------------------
				if (((JSONComponentName) hitBody.getUserData()).getBaseName()
						.compareTo(ComponentNames.TIRE) == 0) {
					Array<JointEdge> joints = hitBody.getJointList();
					JointEdge joint = joints.first();
					hitBody = joint.other;
				} else {
					// ---------------------------------------------------
					lastSelected = hitBody;
				}

				// --------------- HACK ------------------
				// if (((JSONComponentName) hitBody.getUserData()).getBaseName()
				// .compareTo(ComponentNames.AXLE) == 0) {
				// ((JSONComponentName) hitBody.getUserData())
				// .setBaseName(ComponentNames.TIRE);
				// }
				// ---------------------------------------

				System.out.println("mouse: "
						+ ((JSONComponentName) hitBody.getUserData())
								.getBaseName());

				// JSONComponentName hitBodyName = ((JSONComponentName) hitBody
				// .getUserData());
				partLevel = lookupLastPart(lastSelected).getPartLevel();// hitBodyName.getLevel();

				// if (hitBodyName.getBaseName().compareTo(ComponentNames.LIFE)
				// == 0) {
				// return true;
				// }

				mouseJoined = true;

				relativeVector.set(mousePoint.x - hitBody.getPosition().x,
						mousePoint.y - hitBody.getPosition().y);
				hitBody.setTransform(new Vector2(relativeX(mousePoint.x),
						relativeY(mousePoint.y)), hitBody.getAngle());

				if (fixture.getUserData() == null) {
					return true;
				}
				return false;
			} else {

				return true;
			}
		}
	};

	private void processJointClick(Fixture fixture) {

		JSONComponentName componentName = (JSONComponentName) fixture
				.getUserData();

		//
		if (componentName == null) {
			return;
		}

		incrementJointType(componentName);

	}

	private void syncJointType(JSONComponentName componentName,
			JSONComponentName otherComponentName) {

		if (otherComponentName == null || componentName == null)
			return;

		if (!jointTypes.containsKey(componentName.getMountedId())) {
			jointTypes.put(componentName.getMountedId(), 0);
		}

		jointTypes.put(otherComponentName.getMountedId(),
				jointTypes.get(componentName.getMountedId()));
	}

	private Integer lookupJointType(Fixture fixture) {

		if (fixture == null)
			return -1;
		if (fixture.getUserData() == null)
			return -1;

		JSONComponentName componentName = (JSONComponentName) fixture
				.getUserData();

		if (jointTypes.containsKey(componentName.getMountedId())) {
			return jointTypes.get(componentName.getMountedId());
		}

		return -1;
	}

	private void incrementJointType(JSONComponentName componentName) {
		Integer count;
		String key = componentName.getMountedId();

		if (jointTypes.containsKey(key)) {
			count = jointTypes.get(key);

			if (count >= 1) {
				count = 0;
			} else {
				count++;
			}

			jointTypes.put(key, count);
		} else {
			jointTypes.put(key, Globals.LOCKED_JOINT);
		}

	}

	private float relativeX(float p) {
		return p - relativeVector.x;
	}

	private float relativeY(float p) {
		return p - relativeVector.y;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		handleClick(screenX, screenY);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		handleRelease(screenX, screenY);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		handleDrag(screenX, screenY);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}

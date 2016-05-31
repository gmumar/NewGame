package Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.CameraManager;
import wrapper.GamePhysicalState;
import wrapper.GameState;
import wrapper.Globals;
import Component.Component;
import Component.ComponentBuilder;
import Component.ComponentNames;
import GroundWorks.TrackBuilder;
import JSONifier.JSONCompiler;
import JSONifier.JSONComponentName;
import JSONifier.JSONTrack.TrackType;
import RESTWrapper.BackendFunctions;
import RESTWrapper.RESTPaths;
import User.User;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class TrackMenuBuilder {

	// private final float BOX_SIZE = 0.0001f;
	// private final float ROTATION_SIZE = 30;

	private Button zoomIn, zoomOut, panLeft, panRight, build, exit, upload,
			worldType, buildPost, buildBar, rotateLeft, rotateRight, delete,
			panUp, panDown, switchMode, moveMultiple, buildCoin;
	private TextBox currentMode, currentTrackType, distance;

	private CameraManager camera;
	private JSONCompiler compiler;
	private GameLoader gameLoader;

	private World world;
	private ArrayList<Component> parts = new ArrayList<Component>();
	private Body hitBody, lastSelected = null;
	private HashMap<String, Integer> componentCounts = new HashMap<String, Integer>();
	private Vector3 mousePoint = new Vector3();
	private final static float ITEM_SELECT_BOX_SIZE = 0.3f;
	private Vector2 relativeVector = new Vector2();
	private boolean mouseJoined = false;
	private HashMap<String, Integer> jointTypes = new HashMap<String, Integer>();
	private ShapeRenderer fixtureRenderer;
	private boolean drawTrack = true;
	private TrackType trackType = TrackType.FORREST;
	private TrackBuilder trackBuilder;

	private int panSpeedMultiplier = 1;
	private final static int PAN_SPEED = 1;

	public boolean isTrackDrawMode() {
		return drawTrack;
	}

	public TrackMenuBuilder(final GamePhysicalState gamePhysicalState,
			Stage stage, CameraManager secondCamera,
			final TrackBuilder trackBuilder, final User user,
			ShapeRenderer fixtureRenderer) {

		// this.stage = stage;
		this.camera = secondCamera;
		this.gameLoader = gamePhysicalState.getGameLoader();
		this.world = gamePhysicalState.getWorld();
		this.fixtureRenderer = fixtureRenderer;
		this.trackBuilder = trackBuilder;

		compiler = new JSONCompiler();

		worldType = new Button("Type");
		worldType.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (trackType == TrackType.FORREST) {
					trackType = TrackType.ARTIC;
				} else if (trackType == TrackType.ARTIC) {
					trackType = TrackType.FORREST;
				}
				super.clicked(event, x, y);
			}

		});
		worldType.setPosition(Globals.ScreenWidth - 150,
				Globals.ScreenHeight - 50);
		stage.addActor(worldType);

		upload = new Button("upload");
		upload.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				BackendFunctions.uploadTrack(User.getInstance()
						.getCurrentTrack(), RESTPaths.MAPS, 0.0f, 0,0);
				super.clicked(event, x, y);
			}

		});
		upload.setPosition(Globals.ScreenWidth - 50, Globals.ScreenHeight - 50);
		// stage.addActor(upload);

		exit = new Button("exit") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new MainMenuScreen(gameLoader));
			}
		};

		exit.setPosition(Globals.ScreenWidth - 50, Globals.ScreenHeight - 50);
		stage.addActor(exit);

		zoomIn = new Button("+") {
			@Override
			public void Clicked() {
				camera.zoom -= 0.01;
				camera.update();

			}
		};

		zoomIn.setPosition(0, 0);
		stage.addActor(zoomIn);

		zoomOut = new Button("-") {
			@Override
			public void Clicked() {
				camera.zoom += 0.01;
				camera.update();

			}
		};

		zoomOut.setPosition(100, 0);
		stage.addActor(zoomOut);

		build = new Button("build") {
			@Override
			public void Clicked() {
				compiler.compile(world, trackBuilder.getMapList(), parts,
						jointTypes, trackType);
				gameLoader.setScreen(new GamePlayScreen(new GameState(
						gameLoader, user)));
				Destroy();
			}

		};

		build.setPosition(200, 0);
		stage.addActor(build);

		panLeft = new Button(">") {
			@Override
			public void Clicked() {
				camera.position.x += PAN_SPEED * panSpeedMultiplier;
				camera.update();

			}

		};

		panLeft.setPosition(100, 150);
		panLeft.setHeight(50);
		panLeft.setWidth(50);
		stage.addActor(panLeft);

		panRight = new Button("<") {
			@Override
			public void Clicked() {
				camera.position.x -= PAN_SPEED * panSpeedMultiplier;
				camera.update();

			}
		};

		panRight.setPosition(0, 150);
		panRight.setHeight(50);
		panRight.setWidth(50);
		stage.addActor(panRight);

		panUp = new Button("^") {
			@Override
			public void Clicked() {
				camera.position.y += PAN_SPEED * panSpeedMultiplier;
				camera.update();

			}

		};

		panUp.setPosition(50, 200);
		panUp.setHeight(50);
		panUp.setWidth(50);
		stage.addActor(panUp);

		panDown = new Button("\\/") {
			@Override
			public void Clicked() {
				camera.position.y -= PAN_SPEED * panSpeedMultiplier;
				camera.update();

			}
		};

		panDown.setPosition(50, 100);
		panDown.setHeight(50);
		panDown.setWidth(50);
		stage.addActor(panDown);

		switchMode = new Button("switch") {
			@Override
			public void Clicked() {
				drawTrack = !drawTrack;
			}
		};

		switchMode.setPosition(600, 50);
		switchMode.setHeight(50);
		stage.addActor(switchMode);

		moveMultiple = new Button("multiply") {
			@Override
			public void Clicked() {

				if (panSpeedMultiplier == 5)
					panSpeedMultiplier = 1;
				else
					panSpeedMultiplier = 5;
			}

		};

		moveMultiple.setPosition(600, 0);
		moveMultiple.setHeight(50);
		stage.addActor(moveMultiple);

		currentTrackType = new TextBox("type");
		currentTrackType.setPosition(0, Globals.ScreenHeight - 50);
		stage.addActor(currentTrackType);

		currentMode = new TextBox("Mode");
		currentMode.setPosition(0, Globals.ScreenHeight - 25);
		stage.addActor(currentMode);

		distance = new TextBox("Distance");
		distance.setPosition(Globals.ScreenWidth / 2, Globals.ScreenHeight - 25);
		stage.addActor(distance);

		rotateLeft = new Button(">") {
			@Override
			public void Clicked() {
				if (lastSelected != null) {
					lastSelected.setTransform(lastSelected.getPosition(),
							lastSelected.getAngle() - MenuBuilder.ROTATION_SIZE
									* MathUtils.degreesToRadians);
				}

			}

		};

		rotateLeft.setPosition(Globals.ScreenWidth - 50, 150);
		rotateLeft.setHeight(50);
		rotateLeft.setWidth(50);
		stage.addActor(rotateLeft);

		rotateRight = new Button("<") {
			@Override
			public void Clicked() {
				if (lastSelected != null) {
					lastSelected.setTransform(lastSelected.getPosition(),
							lastSelected.getAngle() + MenuBuilder.ROTATION_SIZE
									* MathUtils.degreesToRadians);
				}

			}
		};

		rotateRight.setPosition(Globals.ScreenWidth - 100, 150);
		rotateRight.setHeight(50);
		rotateRight.setWidth(50);
		stage.addActor(rotateRight);

		delete = new Button("delete") {
			@Override
			public void Clicked() {

				if (lastSelected == null)
					return;

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

				for (Component part : parts) {

					if (part.getjComponentName()
							.getId()
							.compareTo(
									((JSONComponentName) fixture.getUserData())
											.getId()) == 0) {
						parts.remove(part);
						break;
					}

				}

				Array<JointEdge> joints = lastSelected.getJointList();

				for (JointEdge joint : joints) {
					world.destroyBody(joint.other);
				}

				world.destroyBody(lastSelected);
				lastSelected = null;
			}
		};

		delete.setPosition(Globals.ScreenWidth - 50, 200);
		delete.setHeight(50);
		delete.setWidth(50);
		stage.addActor(delete);

		buildPost = new Button("buildPost") {
			@Override
			public void Clicked() {
				drawTrack = false;
				int partLevel = 1;
				incrementCount(ComponentNames.POST);
				Component c = ComponentBuilder.buildTrackPost(
						new GamePhysicalState(world, gameLoader), partLevel,
						true);
				// c.setUpForBuilder(ComponentNames.BAR3
				// + Assembler.NAME_ID_SPLIT
				// + componentCounts.get(ComponentNames.BAR3));
				JSONComponentName name = new JSONComponentName();
				name.setBaseName(ComponentNames.POST);
				name.setComponentId(Integer.toString(componentCounts
						.get(ComponentNames.POST)));
				name.setMountId("*");
				name.setLevel(partLevel);
				c.setUpForBuilder(name, partLevel);
				System.out.println("TrackBuilder: " + name);
				lastSelected = c.getObject().getPhysicsBody();
				c.setPosition(camera.position.x, camera.position.y);
				parts.add(c);

			}
		};

		buildPost.setPosition(400, 0);
		stage.addActor(buildPost);

		buildBar = new Button("buildBar") {
			@Override
			public void Clicked() {
				drawTrack = false;
				int partLevel = 1;
				incrementCount(ComponentNames.TRACKBAR);
				Component c = ComponentBuilder.buildTrackBar(
						new GamePhysicalState(world, gameLoader), partLevel,
						true);
				// c.setUpForBuilder(ComponentNames.BAR3
				// + Assembler.NAME_ID_SPLIT
				// + componentCounts.get(ComponentNames.BAR3));
				JSONComponentName name = new JSONComponentName();
				name.setBaseName(ComponentNames.TRACKBAR);
				name.setComponentId(Integer.toString(componentCounts
						.get(ComponentNames.TRACKBAR)));
				name.setMountId("*");
				name.setLevel(partLevel);
				c.setUpForBuilder(name, partLevel);
				System.out.println("TrackBuilder: " + name);
				lastSelected = c.getObject().getPhysicsBody();
				c.setPosition(camera.position.x, camera.position.y);
				parts.add(c);

			}
		};

		buildBar.setPosition(500, 0);
		buildBar.setHeight(50);
		stage.addActor(buildBar);// buildCoin

		buildCoin = new Button("buildCoin") {
			@Override
			public void Clicked() {
				drawTrack = false;
				int partLevel = 1;
				incrementCount(ComponentNames.TRACKCOIN);
				Component c = ComponentBuilder.buildTrackCoin(
						new GamePhysicalState(world, gameLoader), partLevel,
						true);
				// c.setUpForBuilder(ComponentNames.BAR3
				// + Assembler.NAME_ID_SPLIT
				// + componentCounts.get(ComponentNames.BAR3));
				JSONComponentName name = new JSONComponentName();
				name.setBaseName(ComponentNames.TRACKCOIN);
				name.setComponentId(Integer.toString(componentCounts
						.get(ComponentNames.TRACKCOIN)));
				name.setMountId("*");
				name.setLevel(partLevel);
				c.setUpForBuilder(name, partLevel);
				System.out.println("TrackBuilder: " + name);
				lastSelected = c.getObject().getPhysicsBody();
				c.setPosition(camera.position.x, camera.position.y);
				parts.add(c);

			}
		};

		buildCoin.setPosition(500, 50);
		buildCoin.setHeight(50);
		stage.addActor(buildCoin);

		/*
		 * buildBall = new Button("buildBall") {
		 * 
		 * @Override public void Clicked() { drawTrack = false; int partLevel =
		 * 1; incrementCount(ComponentNames.TRACKBALL); Component c =
		 * ComponentBuilder.buildTrackBall( new GamePhysicalState(world,
		 * gameLoader), partLevel, true); //
		 * c.setUpForBuilder(ComponentNames.BAR3 // + Assembler.NAME_ID_SPLIT //
		 * + componentCounts.get(ComponentNames.BAR3)); JSONComponentName name =
		 * new JSONComponentName(); name.setBaseName(ComponentNames.TRACKBALL);
		 * name.setComponentId(Integer.toString(componentCounts
		 * .get(ComponentNames.TRACKBALL))); name.setMountId("*");
		 * name.setLevel(partLevel); c.setUpForBuilder(name, partLevel);
		 * System.out.println("TrackBuilder: " + name); lastSelected =
		 * c.getObject().getPhysicsBody(); c.setPosition(camera.position.x,
		 * camera.position.y); parts.add(c);
		 * 
		 * } };
		 * 
		 * buildBall.setPosition(500, 50); buildBall.setHeight(50);
		 * stage.addActor(buildBall);
		 */

	}

	private void incrementCount(String name) {
		if (componentCounts.containsKey(name)) {
			componentCounts.put(name, componentCounts.get(name) + 1);
		} else {
			componentCounts.put(name, 0);
		}
	}

	public void handleClick(float f, float g) {
		camera.unproject(mousePoint.set(f, g, 0));
		// mousePoint.x += (mousePoint.x * Globals.AspectRatio)/6;
		world.QueryAABB(mouseClickCallback,
				mousePoint.x - ITEM_SELECT_BOX_SIZE, mousePoint.y
						- ITEM_SELECT_BOX_SIZE, mousePoint.x
						+ ITEM_SELECT_BOX_SIZE, mousePoint.y
						+ ITEM_SELECT_BOX_SIZE);

	}

	public void handleRelease(float f, float g) {
		camera.unproject(mousePoint.set(f, g, 0));
		// mousePoint.x += (mousePoint.x * Globals.AspectRatio)/6;
		if (!isJoined()) {
			return;
		}
		lastSelected = hitBody;
		hitBody = null;
		mouseJoined = false;
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

	public boolean isJoined() {
		return mouseJoined;
	}

	private float relativeX(float p) {
		return p - relativeVector.x;
	}

	private float relativeY(float p) {
		return p - relativeVector.y;
	}

	private void processJointClick(Fixture fixture) {

		JSONComponentName componentName = (JSONComponentName) fixture
				.getUserData();

		//
		if (componentName == null) {
			return;
		}

		incrementJointType(componentName);

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
			jointTypes.put(key, 0);
		}

	}

	QueryCallback mouseClickCallback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {

			processJointClick(fixture);

			if (fixture.testPoint(mousePoint.x, mousePoint.y)) {

				/*
				 * if(((String)fixture.getBody().getUserData()).contains(
				 * ComponentNames.LIFE) ||
				 * ((String)fixture.getBody().getUserData
				 * ()).contains(ComponentNames.AXLE) |
				 * ((String)fixture.getBody()
				 * .getUserData()).contains(ComponentNames.TIRE)){ return true;
				 * }
				 */

				hitBody = fixture.getBody();

				JSONComponentName hitBodyName = ((JSONComponentName) hitBody
						.getUserData());

				if (hitBodyName.getBaseName().compareTo(ComponentNames.LIFE) == 0) {
					return true;
				}

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

	public boolean getDrawTrack() {
		return drawTrack;
	}

	public void drawShapes(SpriteBatch batch) {

		if (drawTrack) {
			currentMode.setTextBoxString("Track " + panSpeedMultiplier);
		} else {
			currentMode.setTextBoxString("Parts " + panSpeedMultiplier);
		}

		distance.setTextBoxString(trackBuilder.getMapList().size());

		currentTrackType.setTextBoxString(trackType.name());

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
					drawFixtureSquare(fixtureA, Globals.ORANGE);
				} else if (jointType == 1) {
					drawFixture(fixtureA, Globals.FORREST_GREEN);
				}

				drawIds.add(((JSONComponentName) fixtureA.getUserData())
						.getMountedId());

				jointType = lookupJointType(fixtureB);
				if (jointType == 0) {
					drawFixtureSquare(fixtureB, Globals.ORANGE);
				} else if (jointType == 1) {
					drawFixture(fixtureB, Globals.FORREST_GREEN);
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
						drawFixture(fixture, Globals.RED);
					} else if (!drawIds.contains(((JSONComponentName) fixture
							.getUserData()).getMountedId())) {
						drawFixture(fixture, Globals.RED);
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
							drawFixture(fixture, Globals.RED);
						} else if (!drawIds
								.contains(((JSONComponentName) fixture
										.getUserData()).getMountedId())) {
							drawFixture(fixture, Globals.RED);
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

	private boolean isFixtureName(JSONComponentName jsonComponentName) {

		if (jsonComponentName == null) {
			return false;
		}

		if ((jsonComponentName.getMountId().contains("*"))) {
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
			fixtureRenderer.circle(vec.x, vec.y, shape.getRadius() + 0.2f, 45);

		}
	}

	private void drawFixtureSquare(Fixture fix, Color c) {
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
			fixtureRenderer.box(vec.x - 0.25f, vec.y - 0.25f, 0, 0.5f, 0.5f, 1);
			// fixtureRenderer.circle(vec.x, vec.y, shape.getRadius() + 0.2f,
			// 45);

		}
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

	private void Destroy() {
		// this.world.dispose();
		// stage.dispose();
	}
}

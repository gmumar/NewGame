package Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.CameraManager;
import wrapper.GameState;
import wrapper.Globals;
import Assembly.Assembler;
import Assembly.AssemblyRules;
import Component.Component;
import Component.ComponentBuilder;
import Component.ComponentNames;
import JSONifier.JSONCompiler;
import RESTWrapper.BackendFunctions;

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
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class MenuBuilder {

	private final static float BOX_SIZE = 0.0001f;
	private final static float ROTATION_SIZE = 30;
	private final static int LIMIT_BOX_X = -5, LIMIT_BOX_Y = -5, LIMIT_BOX_X_LEN = 20,
			LIMIT_BOX_Y_LEN = 20;

	private final static Color GREEN = new Color(0, 1, 0, 1);
	private final static Color RED = new Color(1, 0, 0, 1);

	private Stage stage;
	private World world;

	private Button but, tire_but, spring_but, zoomIn, zoomOut, rotateLeft, rotateRight,
			build, exit, upload;

	private Vector3 mousePoint = new Vector3();
	private Body hitBody, lastSelected = null, baseObject;

	private CameraManager camera;

	private boolean mouseJoined = false;

	private ArrayList<Component> parts = new ArrayList<Component>();
	private JSONCompiler compiler = new JSONCompiler();
	private AssemblyRules assemblyRules = new AssemblyRules();

	private HashMap<String, Integer> componentCounts;

	private GameLoader gameLoader;

	private Vector2 relativeVector = new Vector2();

	private static ShapeRenderer fixtureRenderer;
	
	private BackendFunctions backend;

	public MenuBuilder(final GameState gameState, Stage stage,
			CameraManager secondCamera, ShapeRenderer shapeRenderer) {

		this.backend = new BackendFunctions();
		this.world = gameState.getWorld();
		this.stage = stage;
		this.camera = secondCamera;
		this.mouseJoined = false;
		this.gameLoader = gameState.getGameLoader();
		MenuBuilder.fixtureRenderer = shapeRenderer;

		BodyDef bodyDef = new BodyDef();
		world.createBody(bodyDef);

		componentCounts = new HashMap<String, Integer>();

		drawBox(LIMIT_BOX_X, LIMIT_BOX_Y, LIMIT_BOX_X_LEN, LIMIT_BOX_Y_LEN);

		// Add life regardless

		incrementCount(ComponentNames.LIFE);
		Component c = ComponentBuilder.buildLife(new GameState(world,
				gameLoader), true);
		c.setUpForBuilder(ComponentNames.LIFE
				+ Assembler.NAME_ID_SPLIT
				+ componentCounts.get(ComponentNames.LIFE));

		baseObject = c.getObject().getPhysicsBody();//lastSelected = 
		parts.add(c);

		//

		but = new Button("Small bar") {
			@Override
			public void Clicked() {
				incrementCount(ComponentNames.BAR3);
				Component c = ComponentBuilder.buildBar3(new GameState(world,
						gameLoader), true);
				c.setUpForBuilder(ComponentNames.BAR3
						+ Assembler.NAME_ID_SPLIT
						+ componentCounts.get(ComponentNames.BAR3));
				lastSelected = c.getObject().getPhysicsBody();
				parts.add(c);
			}
		};

		but.setPosition(0, 0);
		stage.addActor(but);

		tire_but = new Button("tire") {
			@Override
			public void Clicked() {
				incrementCount(ComponentNames.TIRE);
				Component c = ComponentBuilder.buildTire(new GameState(world,
						gameLoader), true);
				c.setUpForBuilder(ComponentNames.TIRE
						+ Assembler.NAME_ID_SPLIT
						+ componentCounts.get(ComponentNames.TIRE));
				lastSelected = c.getObject().getPhysicsBody();
				parts.add(c);
			}
		};

		tire_but.setPosition(100, 100);
		stage.addActor(tire_but);

		spring_but = new Button("spring") {
			@Override
			public void Clicked() {
				incrementCount(ComponentNames.SPRINGJOINT);
				Component c = ComponentBuilder.buildSpringJoint(
						new GameState(world, gameLoader), true).get(0);
				c.setUpForBuilder(Assembler.NAME_ID_SPLIT
						+ componentCounts.get(ComponentNames.SPRINGJOINT));
				lastSelected = c.getObject().getPhysicsBody();
				parts.add(c);
			}
		};

		spring_but.setPosition(200, 100);
		stage.addActor(spring_but);

		zoomIn = new Button("+") {
			@Override
			public void Clicked() {
				camera.zoom -= 0.01;
				camera.update();
			}
		};

		zoomIn.setPosition(100, 0);
		stage.addActor(zoomIn);

		zoomOut = new Button("-") {
			@Override
			public void Clicked() {
				camera.zoom += 0.01;
				camera.update();
			}
		};

		zoomOut.setPosition(200, 0);
		stage.addActor(zoomOut);

		build = new Button("build") {
			@Override
			public void Clicked() {
				if(buildCar()){
					gameLoader.setScreen(new GamePlayScreen(gameLoader));
					Destroy();
				}
			}

		};

		build.setPosition(300, 0);
		stage.addActor(build);
		
		upload = new Button("^") {
			@Override
			public void Clicked() {
				if(buildCar()){
					backend.uploadCar();
				}
			}

		};

		upload.setPosition(Globals.ScreenWidth - 100, Globals.ScreenHeight - 100);
		stage.addActor(upload);

		rotateLeft = new Button(">") {
			@Override
			public void Clicked() {
				if (lastSelected != null) {
					lastSelected.setTransform(lastSelected.getPosition(),
							lastSelected.getAngle() - ROTATION_SIZE
									* MathUtils.degreesToRadians);
				}
			}

		};

		rotateLeft.setPosition(0, 100);
		stage.addActor(rotateLeft);

		rotateRight = new Button("<") {
			@Override
			public void Clicked() {
				if (lastSelected != null) {
					lastSelected.setTransform(lastSelected.getPosition(),
							lastSelected.getAngle() + ROTATION_SIZE
									* MathUtils.degreesToRadians);
				}
			}
		};

		rotateRight.setPosition(0, 200);
		stage.addActor(rotateRight);

		exit = new Button("exit") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new MainMenuScreen(gameLoader));
			}
		};

		exit.setPosition(400, 0);
		stage.addActor(exit);

	}

	public void draw(SpriteBatch batch) {
		Iterator<Component> iter = parts.iterator();

		while (iter.hasNext()) {
			Component part = iter.next();
			part.draw(batch);
		}

	}

	public void drawShapes(SpriteBatch batch) {

		// world.QueryAABB(fixtureCallback, -20,-20, 20, 20);

		Array<Contact> contacts = world.getContactList();
		Iterator<Contact> contactIter = contacts.iterator();

		Iterator<Component> iter = parts.iterator();

		while (iter.hasNext()) {
			Component part = iter.next();
			ArrayList<BaseActor> bodies = part.getJointBodies();

			if (bodies == null) {
				Body body = part.getObject().getPhysicsBody();
				Array<Fixture> fixtures = body.getFixtureList();

				Iterator<Fixture> fixtureIter = fixtures.iterator();

				while (fixtureIter.hasNext()) {
					drawFixture(fixtureIter.next(), RED);
				}
			} else {

				Iterator<BaseActor> bodiesIter = bodies.iterator();

				while (bodiesIter.hasNext()) {
					BaseActor base = bodiesIter.next();
					Body body = base.getPhysicsBody();
					Array<Fixture> fixtures = body.getFixtureList();

					Iterator<Fixture> fixtureIter = fixtures.iterator();

					while (fixtureIter.hasNext()) {
						drawFixture(fixtureIter.next(), RED);
					}
				}
			}
			//

		}
		while (contactIter.hasNext()) {
			Contact contact = contactIter.next();
			Fixture fixtureA = contact.getFixtureA();
			Fixture fixtureB = contact.getFixtureB();
			if (contact.isTouching()
					&& isFixtureName((String) fixtureA.getUserData())
					&& isFixtureName((String) fixtureB.getUserData())) {
				drawFixture(fixtureA, GREEN);
				drawFixture(fixtureB, GREEN);
			}
		}
	}
	
	private boolean buildCar(){
		if (assemblyRules.checkBuild(world, baseObject, parts)) {
			compiler.compile(world, parts);
			return true;
		} else {
			return false;
		}	
	}

	private boolean isFixtureName(String name) {

		if (name == null) {
			return false;
		}

		if (name.contains(Assembler.NAME_ID_SPLIT)) {
			return true;
		}

		return false;
	}

	private void drawFixture(Fixture fix, Color c) {
		if (!isFixtureName((String) fix.getUserData())) {
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

	QueryCallback fixtureCallback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {

			if (fixture.getShape().getType() == Type.Circle) {
				//System.out.println(fixture.getUserData());
			}

			return true;
		}
	};

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

	public void handleClick(float f, float g) {
		camera.unproject(mousePoint.set(f, g, 0));

		world.QueryAABB(mouseClickCallback, mousePoint.x - BOX_SIZE,
				mousePoint.y - BOX_SIZE, mousePoint.x + BOX_SIZE, mousePoint.y
						+ BOX_SIZE);

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
		fixtureDef.density = 1;
		fixtureDef.friction = 5;
		fixtureDef.restitution = 0;

		Fixture f = body.createFixture(fixtureDef);
		edgeShape.dispose();

		return f;
	}

	public void handleDrag(float f, float g) {
		camera.unproject(mousePoint.set(f, g, 0));
		if (!isJoined()) {
			return;
		}
		hitBody.setTransform(new Vector2(relativeX(mousePoint.x),
				relativeY(mousePoint.y)), hitBody.getAngle());
	}

	public void handleRelease(float f, float g) {
		if (!isJoined()) {
			return;
		}
		lastSelected = hitBody;
		hitBody = null;
		mouseJoined = false;
	}

	QueryCallback mouseClickCallback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {

			if (fixture.testPoint(mousePoint.x, mousePoint.y)) {
				
				if(((String)fixture.getBody().getUserData()).contains(ComponentNames.LIFE) ||
						((String)fixture.getBody().getUserData()).contains(ComponentNames.AXLE) |  
						((String)fixture.getBody().getUserData()).contains(ComponentNames.TIRE)){
					return true;
				}
				
				hitBody = fixture.getBody();
				mouseJoined = true;

				relativeVector.set(mousePoint.x - hitBody.getPosition().x,
						mousePoint.y - hitBody.getPosition().y);
				hitBody.setTransform(new Vector2(relativeX(mousePoint.x),
						relativeY(mousePoint.y)), hitBody.getAngle());

				return false;
			} else
				return true;
		}
	};

	private float relativeX(float p) {
		return p - relativeVector.x;
	}

	private float relativeY(float p) {
		return p - relativeVector.y;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}

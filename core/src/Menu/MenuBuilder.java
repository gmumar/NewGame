package Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wrapper.CameraManager;
import Assembly.Assembler;
import Component.Component;
import Component.ComponentBuilder;
import Component.ComponentBuilder.ComponentNames;
import JSONifier.JSONCompiler;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class MenuBuilder {

	final float BOX_SIZE = 0.0001f;
	final float ROTATION_SIZE = 30;

	Stage stage;
	World world;

	Button but, tire_but, spring_but, zoomIn, zoomOut, rotateLeft, rotateRight,
			build, exit;

	Vector3 mousePoint = new Vector3();
	Body groundBody, hitBody, lastSelected = null;

	CameraManager camera;

	boolean mouseJoined = false;

	ArrayList<Component> parts = new ArrayList<Component>();
	JSONCompiler compiler = new JSONCompiler();

	HashMap<String, Integer> componentCounts;

	GameLoader gameLoader;

	Vector2 relativeVector = new Vector2();

	public MenuBuilder(final World world, Stage stage,
			CameraManager secondCamera, final GameLoader gameLoader) {

		this.world = world;
		this.stage = stage;
		this.camera = secondCamera;
		this.mouseJoined = false;
		this.gameLoader = gameLoader;

		BodyDef bodyDef = new BodyDef();
		groundBody = world.createBody(bodyDef);

		componentCounts = new HashMap<String, Integer>();
		
		drawBox(-5, -5, 20, 20);
		
		// Add life regardless 
		
		incrementCount(ComponentNames._LIFE_.name());
		Component c = ComponentBuilder.buildLife(world, true);
		c.setUpForBuilder(ComponentNames._LIFE_.name()
				+ Assembler.NAME_ID_SPLIT
				+ componentCounts.get(ComponentNames._LIFE_.name()));
		lastSelected = c.getObject().getPhysicsBody();
		parts.add(c);
		
		//

		but = new Button("Small bar") {
			@Override
			public void Clicked() {
				incrementCount(ComponentNames._BAR3_.name());
				Component c = ComponentBuilder.buildBar3(world, true);
				c.setUpForBuilder(ComponentNames._BAR3_.name()
						+ Assembler.NAME_ID_SPLIT
						+ componentCounts.get(ComponentNames._BAR3_.name()));
				lastSelected = c.getObject().getPhysicsBody();
				parts.add(c);
			}
		};

		but.setPosition(0, 0);
		stage.addActor(but);

		tire_but = new Button("tire") {
			@Override
			public void Clicked() {
				incrementCount(ComponentNames._TIRE_.name());
				Component c = ComponentBuilder.buildTire(world, true);
				c.setUpForBuilder(ComponentNames._TIRE_.name()
						+ Assembler.NAME_ID_SPLIT
						+ componentCounts.get(ComponentNames._TIRE_.name()));
				lastSelected = c.getObject().getPhysicsBody();
				parts.add(c);
			}
		};

		tire_but.setPosition(100, 100);
		stage.addActor(tire_but);

		spring_but = new Button("spring") {
			@Override
			public void Clicked() {
				incrementCount(ComponentNames._SPRINGJOINT_.name());
				Component c = ComponentBuilder.buildSpringJoint(world, true).get(0);
				c.setUpForBuilder( Assembler.NAME_ID_SPLIT
						+ componentCounts.get(ComponentNames._SPRINGJOINT_.name()));
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
				compiler.compile(world, parts);
				gameLoader.gameSetScreen(new GamePlayScreen(gameLoader));
				Destroy();
			}

		};

		build.setPosition(300, 0);
		stage.addActor(build);

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

		world.QueryAABB(callback, mousePoint.x - BOX_SIZE, mousePoint.y
				- BOX_SIZE, mousePoint.x + BOX_SIZE, mousePoint.y + BOX_SIZE);

	}
	
	public void drawBox(float x, float y, float sizex,float sizey){
		BodyDef box = new BodyDef();
		
		box.type = BodyDef.BodyType.KinematicBody;
		box.position.set(x, y);
		Body boxBody = world.createBody(box);
		
		drawEdge(new Vector2(x,y), new Vector2(x,sizey+y), boxBody);
		drawEdge(new Vector2(x,sizey+y),new Vector2(x+sizex,y+sizey), boxBody);
		drawEdge(new Vector2(x+sizex,y+sizey),new Vector2(x+sizex,y), boxBody);
		drawEdge(new Vector2(x+sizex,y),new Vector2(x,y), boxBody);
		
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

	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {

			if (fixture.testPoint(mousePoint.x, mousePoint.y)) {
				mouseJoined = true;

				hitBody = fixture.getBody();
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
}

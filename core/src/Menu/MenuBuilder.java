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
import Component.ComponentBuilder;
import Component.ComponentNames;
import JSONifier.JSONCompiler;
import JSONifier.JSONComponentName;
import RESTWrapper.BackendFunctions;
import User.User;

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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.MainMenuScreen;

public class MenuBuilder {

	private final static float BOX_SIZE = 0.3f;
	private final static float ROTATION_SIZE = 30;
	private final static int LIMIT_BOX_X = -5, LIMIT_BOX_Y = -5, LIMIT_BOX_X_LEN = 20,
			LIMIT_BOX_Y_LEN = 20;

	private final static Color GREEN = new Color(0, 1, 0, 1);
	private final static Color RED = new Color(1, 0, 0, 1);
	private final static Color ORANGE = new Color(0.4f, 0.8f, 0.4f, 1);

	private Stage stage;
	private World world;

	private Button but, tire_but, spring_but, zoomIn, zoomOut, rotateLeft, rotateRight,
			build, exit, upload, levelUp, levelDown, delete;
	
	private TextBox partLevelText;

	private Vector3 mousePoint = new Vector3();
	private Body hitBody, lastSelected = null, baseObject;

	private CameraManager camera;

	private boolean mouseJoined = false;

	private ArrayList<Component> parts = new ArrayList<Component>();
	private JSONCompiler compiler = new JSONCompiler();
	private AssemblyRules assemblyRules = new AssemblyRules();
	private HashMap<String, Integer> jointTypes = new HashMap<String, Integer>();

	private HashMap<String, Integer> componentCounts;

	private GameLoader gameLoader;

	private Vector2 relativeVector = new Vector2();

	private static ShapeRenderer fixtureRenderer;
	
	private BackendFunctions backend;

	volatile private int partLevel = 1;
	
	private GameState gameState;
	
	//private FixtureDef mouseFixture;
	//BaseActor mouseActor;
	
	public MenuBuilder(final GamePhysicalState gamePhysicalState, Stage stage,
			CameraManager secondCamera, ShapeRenderer shapeRenderer, User user) {

		this.backend = new BackendFunctions();
		this.world = gamePhysicalState.getWorld();
		this.stage = stage;
		this.camera = secondCamera;
		this.mouseJoined = false;
		this.gameLoader = gamePhysicalState.getGameLoader();
		this.gameState = new GameState(gameLoader, user);
		MenuBuilder.fixtureRenderer = shapeRenderer;

		BodyDef bodyDef = new BodyDef();
		world.createBody(bodyDef);

		componentCounts = new HashMap<String, Integer>();

		drawBox(LIMIT_BOX_X, LIMIT_BOX_Y, LIMIT_BOX_X_LEN, LIMIT_BOX_Y_LEN);
		
		

		// Add life regardless

		incrementCount(ComponentNames.LIFE);
		Component c = ComponentBuilder.buildLife(new GamePhysicalState(world,
				gameLoader), 1, true);
		//c.setUpForBuilder(ComponentNames.LIFE
		//		+ Assembler.NAME_ID_SPLIT
		//		+ componentCounts.get(ComponentNames.LIFE));
		JSONComponentName name = new JSONComponentName();
		name.setBaseName(ComponentNames.LIFE);
		name.setComponentId(Integer.toString(componentCounts.get(ComponentNames.LIFE)));
		name.setMountId("*");
		c.setUpForBuilder(name,partLevel);
		System.out.println("MenuBuilder: "+name);
		
		baseObject = c.getObject().getPhysicsBody();//lastSelected = 
		parts.add(c);

		//

		but = new Button("Small bar") {
			@Override
			public void Clicked() {
				incrementCount(ComponentNames.BAR3);
				Component c = ComponentBuilder.buildBar3(new GamePhysicalState(world,
						gameLoader), partLevel, true);
				//c.setUpForBuilder(ComponentNames.BAR3
				//		+ Assembler.NAME_ID_SPLIT
				//		+ componentCounts.get(ComponentNames.BAR3));
				JSONComponentName name = new JSONComponentName();
				name.setBaseName(ComponentNames.BAR3);
				name.setComponentId(Integer.toString(componentCounts.get(ComponentNames.BAR3)));
				name.setMountId("*");
				c.setUpForBuilder(name, partLevel);
				System.out.println("MenuBuilder: "+name);
				lastSelected = c.getObject().getPhysicsBody();
				parts.add(c);
			}
		};

		but.setPosition(0, 200);
		stage.addActor(but);
		
		delete = new Button("delete") {
			@Override
			public void Clicked() {
				
				if(lastSelected==null) return;
				
				//----------------------- HACK ----------------------
				if(((JSONComponentName)lastSelected.getUserData()).getBaseName().compareTo(ComponentNames.AXLE)== 0){
					Array<JointEdge> joints = lastSelected.getJointList();
					JointEdge joint = joints.first();
					lastSelected = joint.other;
				}
				//---------------------------------------------------
				
				Array<Fixture> fixtures = lastSelected.getFixtureList();
				Fixture fixture = null;
				
				for (Fixture fixtureIter : fixtures){
					if(fixtureIter.getUserData() == null) continue;
					fixture = fixtureIter;
					break;
				}
				
				if (fixture == null) return;
				if (fixture.getUserData() == null) {
					world.destroyBody(fixture.getBody());
					return;
				}
				
				for (Component part : parts){
					//----------------------- HACK ----------------------
					if(part.getBaseName().compareTo(ComponentNames.AXLE)==0){
						
						String swappedId = part.getjComponentName().getId().replace(ComponentNames.AXLE, ComponentNames.TIRE);
						if(swappedId.compareTo(((JSONComponentName)fixture.getUserData()).getId()) == 0){
							parts.remove(part);
							break;
						}
					} 
					// --------------------------------------------------
					else {
						if(part.getjComponentName().getId().compareTo(((JSONComponentName)fixture.getUserData()).getId()) == 0){
							parts.remove(part);
							break;
						}
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

		delete.setPosition(100, 0);
		delete.setHeight(50);
		delete.setWidth(50);
		stage.addActor(delete);
		

		tire_but = new Button("tire") {
			@Override
			public void Clicked() {
				incrementCount(ComponentNames.TIRE);
				Component c = ComponentBuilder.buildTire(new GamePhysicalState(world,
						gameLoader), partLevel, true);
				//c.setUpForBuilder(ComponentNames.TIRE
				//		+ Assembler.NAME_ID_SPLIT
				//		+ componentCounts.get(ComponentNames.TIRE));
				JSONComponentName name = new JSONComponentName();
				name.setBaseName(ComponentNames.TIRE);
				name.setComponentId(Integer.toString(componentCounts.get(ComponentNames.TIRE)));
				c.setUpForBuilder(name,partLevel);
				System.out.println("MenuBuilder: "+name);
				lastSelected = c.getObject().getPhysicsBody();
				parts.add(c);
			}
		};

		tire_but.setPosition(0, 0);
		stage.addActor(tire_but);

		spring_but = new Button("spring") {
			@Override
			public void Clicked() {
				incrementCount(ComponentNames.SPRINGJOINT);
				Component c = ComponentBuilder.buildSpringJoint(
						new GamePhysicalState(world, gameLoader), partLevel, true).get(0);
				//c.setUpForBuilder(Assembler.NAME_ID_SPLIT
				//		+ componentCounts.get(ComponentNames.SPRINGJOINT));
				JSONComponentName name = new JSONComponentName();
				name.setBaseName(ComponentNames.SPRINGJOINT);
				name.setComponentId(Integer.toString(componentCounts.get(ComponentNames.SPRINGJOINT)));
				c.setUpForBuilder(name,partLevel);
				System.out.println("MenuBuilder: "+name);
				lastSelected = c.getObject().getPhysicsBody();
				parts.add(c);
			}
		};

		spring_but.setPosition(0, 100);
		stage.addActor(spring_but);

		zoomIn = new Button("+") {
			@Override
			public void Clicked() {
				camera.zoom -= 0.01;
				camera.update();
			}
		};

		zoomIn.setPosition(Globals.ScreenWidth - 100, 50);
		zoomIn.setHeight(50);
		stage.addActor(zoomIn);

		zoomOut = new Button("-") {
			@Override
			public void Clicked() {
				camera.zoom += 0.01;
				camera.update();
			}
		};

		zoomOut.setPosition(Globals.ScreenWidth - 100, 0);
		zoomOut.setHeight(50);
		stage.addActor(zoomOut);

		build = new Button("build") {
			@Override
			public void Clicked() {
				if(buildCar()){
					gameLoader.setScreen(new GamePlayScreen(gameState));
					Destroy();
				}
			}

		};

		build.setPosition(0, Globals.ScreenHeight - 100);
		build.setHeight(50);
		stage.addActor(build);
		
		upload = new Button("^") {
			@Override
			public void Clicked() {
				if(buildCar()){
					backend.uploadCar();
				}
			}

		};

		upload.setPosition(0, Globals.ScreenHeight - 50);
		upload.setHeight(50);
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

		rotateLeft.setPosition(Globals.ScreenWidth - 50, 150);
		rotateLeft.setHeight(50);
		rotateLeft.setWidth(50);
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

		rotateRight.setPosition(Globals.ScreenWidth - 100, 150);
		rotateRight.setHeight(50);
		rotateRight.setWidth(50);
		stage.addActor(rotateRight);
		
		
		levelUp = new Button("+") {
			@Override
			public void Clicked() {
				
				partLevel += 1;
				if(partLevel>=15){
					partLevel = 15;	
				}
			}
		};

		levelUp.setPosition(Globals.ScreenWidth - 75, 300);
		levelUp.setHeight(50);
		levelUp.setWidth(50);
		stage.addActor(levelUp);

		levelDown = new Button("-") {
			@Override
			public void Clicked() {
				partLevel -= 1;
			}
		};
		
		partLevelText = new TextBox("1");
		partLevelText.setPosition(Globals.ScreenWidth - 75, 250);
		partLevelText.setHeight(50);
		partLevelText.setWidth(50);
		stage.addActor(partLevelText);

		levelDown.setPosition(Globals.ScreenWidth - 75, 200);
		levelDown.setHeight(50);
		levelDown.setWidth(50);
		stage.addActor(levelDown);
		

		exit = new Button("exit") {
			@Override
			public void Clicked() {
				gameLoader.setScreen(new MainMenuScreen(gameLoader));
			}
		};

		exit.setPosition(Globals.ScreenWidth - 50, Globals.ScreenHeight - 50);
		exit.setHeight(50);
		exit.setWidth(50);
		stage.addActor(exit);

		
		//JSONComponentName mouseName = new JSONComponentName();
		//mouseName.setBaseName("mouseActor");
		//mouseActor = new BaseActor(mouseName, gameState);
		//mouseActor.setSensor();
		//mouseFixture = ComponentBuilder.buildMount(new Vector2(mousePoint.x,mousePoint.y), 1, true);
		//mouseActor.getPhysicsBody().createFixture(mouseFixture);
		
	}

	public void draw(SpriteBatch batch) {
		Iterator<Component> iter = parts.iterator();

		while (iter.hasNext()) {
			Component part = iter.next();
			part.draw(batch);
		}
		
	

	}

	public void drawShapes(SpriteBatch batch) {

		partLevelText.setText(Integer.toString(partLevel));
		Integer jointType;
		
		ArrayList<String> drawIds = new ArrayList<String>();
		
		// world.QueryAABB(fixtureCallback, -20,-20, 20, 20);
		//mouseActor.setPosition(mousePoint.x, mousePoint.y);

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
				
				if(fixtureA.getUserData() !=null && fixtureB.getUserData()!=null){
					syncJointType((JSONComponentName) fixtureA.getUserData(),
							(JSONComponentName) fixtureB.getUserData());
				}
				
				jointType = lookupJointType(fixtureA);
				if(jointType == 0){
					drawFixtureSquare(fixtureA, ORANGE);
				} else if(jointType == 1){
					drawFixture(fixtureA, GREEN);
				}
				
				drawIds.add(((JSONComponentName) fixtureA.getUserData()).getMountedId());
				
				
				jointType = lookupJointType(fixtureB);
				if(jointType == 0){
					drawFixtureSquare(fixtureB, ORANGE);
				} else if(jointType == 1){
					drawFixture(fixtureB, GREEN);
				}
				
				drawIds.add(((JSONComponentName) fixtureB.getUserData()).getMountedId());
				
				/*DistanceJointDef dJoint = new DistanceJointDef();
				dJoint.initialize(fixtureA.getBody(),
						fixtureB.getBody(), ((CircleShape)fixtureA.getShape()).getPosition(),
						((CircleShape)fixtureB.getShape()).getPosition());
				dJoint.length = 0;
				dJoint.collideConnected = false;
				
				//	dJoint.frequencyHz = 10;
					//dJoint.dampingRatio = 0.5f;
				
				Joint joint = world.createJoint(dJoint);
				world.step(1, 100, 100);
				world.destroyJoint(joint);*/
				
			}
		}
		

		while (iter.hasNext()) {
			Component part = iter.next();
			ArrayList<BaseActor> bodies = part.getJointBodies();

			if (bodies == null) {
				Body body = part.getObject().getPhysicsBody();
	
				for (Fixture fixture :  body.getFixtureList()) {
					//jointType = lookupJointType(fixture);
					//if(jointType == -1){
					if(fixture.getUserData() == null){
						drawFixture(fixture, RED);
					} else 	if(!drawIds.contains(((JSONComponentName)fixture.getUserData()).getMountedId())) {
						drawFixture(fixture, RED);
					}
					//} else if(jointType == 1){
					//	;
					//}
					
				}
			} else {

				Iterator<BaseActor> bodiesIter = bodies.iterator();

				while (bodiesIter.hasNext()) {
					BaseActor base = bodiesIter.next();
					Body body = base.getPhysicsBody();
					
					for (Fixture fixture :  body.getFixtureList()) {
						//jointType = lookupJointType(fixture);
						//if(jointType == -1){
						if(fixture.getUserData() == null){
							drawFixture(fixture, RED);
						} else 	if(!drawIds.contains(((JSONComponentName)fixture.getUserData()).getMountedId())) {
							drawFixture(fixture, RED);
						}
						//} else if(jointType == 1){
						//	;
						//}
						
					}
				}
			}
			//

		}
		
	}
	
	private boolean buildCar(){
		if (assemblyRules.checkBuild(world, baseObject, parts)) {
			compiler.compile(world, parts, jointTypes);
			return true;
		} else {
			return false;
		}	
	}

	private boolean isFixtureName(JSONComponentName jsonComponentName) {

		if (jsonComponentName == null) {
			return false;
		}

		/*if (jsonComponentName.contains(Assembler.NAME_ID_SPLIT)) {
			return true;
		}*/
		
		if (jsonComponentName.getComponentId()!=null) {
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
			fixtureRenderer.box(vec.x -0.25f, vec.y-0.25f, 0, 0.5f, 0.5f, 1);
			//fixtureRenderer.circle(vec.x, vec.y, shape.getRadius() + 0.2f, 45);

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
		//fixtureDef.density = 1;
		fixtureDef.friction = 5;
		fixtureDef.restitution = 0;

		Fixture f = body.createFixture(fixtureDef);
		edgeShape.dispose();

		return f;
	}
	
	public void handleClick(float f, float g) {
		camera.unproject(mousePoint.set(f, g, 0));
		//mousePoint.x += (mousePoint.x * Globals.AspectRatio)/6;
		world.QueryAABB(mouseClickCallback, mousePoint.x - BOX_SIZE,
				mousePoint.y - BOX_SIZE, mousePoint.x + BOX_SIZE, mousePoint.y
						+ BOX_SIZE);

	}

	public void handleDrag(float f, float g) {
		camera.unproject(mousePoint.set(f, g, 0));
		//mousePoint.x += (mousePoint.x * Globals.AspectRatio)/6;
		if (!isJoined()) {
			return;
		}
		hitBody.setTransform(new Vector2(relativeX(mousePoint.x),
				relativeY(mousePoint.y)), hitBody.getAngle());
	}

	
	public void handlePan(float f, float g, float deltaX, float deltaY) {
		camera.unproject(mousePoint.set(f, g, 0));
		//mousePoint.x += (mousePoint.x * Globals.AspectRatio)/6 + deltaX/6;
		
	}
	
	public void handleRelease(float f, float g) {
		camera.unproject(mousePoint.set(f, g, 0));
		//mousePoint.x += (mousePoint.x * Globals.AspectRatio)/6;
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
			
			
			processJointClick(fixture);
			
		
			if (fixture.testPoint(mousePoint.x, mousePoint.y)) {
				
				System.out.println((JSONComponentName)fixture.getUserData());
				/*if(((String)fixture.getBody().getUserData()).contains(ComponentNames.LIFE) ||
						((String)fixture.getBody().getUserData()).contains(ComponentNames.AXLE) |  
						((String)fixture.getBody().getUserData()).contains(ComponentNames.TIRE)){
					return true;
				}*/
				
				if(((JSONComponentName)fixture.getBody().getUserData()).getBaseName().compareTo(ComponentNames.LIFE) ==0 ) {
					return true;
				}
				
				hitBody = fixture.getBody();
				mouseJoined = true;

				relativeVector.set(mousePoint.x - hitBody.getPosition().x,
						mousePoint.y - hitBody.getPosition().y);
				hitBody.setTransform(new Vector2(relativeX(mousePoint.x),
						relativeY(mousePoint.y)), hitBody.getAngle());

				if(fixture.getUserData()==null){
					return true;
				}
				return false;
			} else {
				
				
				return true;
			}
		}
	};
	
	private void processJointClick(Fixture fixture) {
		
		JSONComponentName componentName = (JSONComponentName)fixture.getUserData();
		
		//
		if(componentName == null){
			return;
		}
		
		incrementJointType(componentName);
		
		System.out.println(jointTypes);
		
	}
	
	private void syncJointType(JSONComponentName componentName,
			JSONComponentName otherComponentName) {
		
		if(otherComponentName==null || componentName==null) return;
		
		if(!jointTypes.containsKey(componentName.getMountedId())){
			jointTypes.put(componentName.getMountedId(), 0);
		}
		
		jointTypes.put(otherComponentName.getMountedId(), jointTypes.get(componentName.getMountedId()));
	}

	private Integer lookupJointType(Fixture fixture){
		
		if(fixture == null) return -1;
		if(fixture.getUserData() == null) return -1;
		
		JSONComponentName componentName = (JSONComponentName)fixture.getUserData();
		
		if(jointTypes.containsKey(componentName.getMountedId())){
			return jointTypes.get(componentName.getMountedId());
		}
		
		return -1;
	}

	private void incrementJointType(JSONComponentName componentName) {
		Integer count;
		String key = componentName.getMountedId();
		
		if (jointTypes.containsKey(key)) {
			count =  jointTypes.get(key);
			
			if(count >= 1){
				count = 0;
			} else {
				count ++;
			}
			
			jointTypes.put(key, count);
		} else {
			jointTypes.put(key, 0);
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
}

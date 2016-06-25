package GroundWorks;

import java.util.ArrayList;
import java.util.Random;

import wrapper.CameraManager;
import wrapper.GamePhysicalState;
import wrapper.Globals;
import Assembly.AssembledTrack;
import Assembly.Assembler;
import Component.ComponentNames;
import JSONifier.JSONComponentName;
import JSONifier.JSONTrack;
import JSONifier.JSONTrack.TrackType;
import Shader.GameMesh;
import UserPackage.User;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.gudesigns.climber.GameLoader;

public class GroundBuilder {

	public final static float UNIT_LENGTH = 2;
	final static float VARIATION = 1f;
	final static float BIASING = 2f;
	final static float PERIOD = 0.3f;

	// This is used to find the back edge of the camera
	public final static int BACK_EDGE_UNITS = 30;

	private final static int TRACK_Y_OFFSET = 30;
	// Change this offset to extend the initial flat ground
	private final static int TRACK_X_OFFSET = 20;

	private float variation = 0.5f, baising = 0.1f;

	private World world;
	private GameLoader gameLoader;
	private Body floor;
	private CameraManager camera;
	private EdgeShape edgeShape = new EdgeShape();
	private FixtureDef fixtureDef = new FixtureDef();
	private double angle = 0, bias = 0;
	private int shaderStart = 0, shaderEnd = 0;
	private Random r = new Random();
	private GroundDecor decor;

	private static int ADD_FLOOR_COUNT_FINAL = 0;
	private int addFloorCount;
	private int ADD_FLOOR_COUNT = 0;
	private int flatFloorCount;
	private static final int FLAT_FLOOR_COUNT = 60;

	private boolean infinate = true;
	private int mapListCounter = 0;

	private ArrayList<GroundUnitDescriptor> preMadeMapList;

	private Fixture verticalEdge;

	private ArrayList<GroundUnitDescriptor> mapList = new ArrayList<GroundUnitDescriptor>();

	private Assembler assembler = new Assembler();

	private Integer lastDrawnPointer = 0;
	private Integer lastRemovedPointer = 1;

	private GroundUnitDescriptor lastObj;
	private GroundUnitDescriptor gud;

	private static Texture groundFiller;
	private TrackType trackType = TrackType.FORREST;

	private ShaderProgram shader;
	private ShaderProgram colorShader;
	private static int vertexCount;

	// private int initial = 0 * 8;
	public boolean loading = true;

	private AssembledTrack track;

	private JSONComponentName groundFixtureName = new JSONComponentName();
	private Fixture groundFixture;

	private Color LAYER_COLOR;

	// drawEdge looks up type everytime, try to optimize.
	public GroundBuilder(GamePhysicalState gameState,
			final CameraManager camera, ShaderProgram shader,
			ShaderProgram colorShader, boolean forMainMenu, User user) {
		this.world = gameState.getWorld();
		this.gameLoader = gameState.getGameLoader();
		this.camera = camera;
		this.shader = shader;
		this.colorShader = colorShader;

		String mapString = null;

		if (!forMainMenu) {
			mapString = user.getCurrentTrack();
			trackType = JSONTrack.objectify(mapString).getType();
		}

		createFloor();
		decor = new GroundDecor(gameState);

		if (mapString == null) {
			infinate = true;
		} else {
			infinate = false;
			mapListCounter = 0;

			track = assembler.assembleTrack(mapString, gameState, new Vector2(
					TRACK_X_OFFSET, TRACK_Y_OFFSET), forMainMenu);

			preMadeMapList = track.getPoints();

			decor.addChequeredFlag(preMadeMapList);
			// mapList.addAll(assembler.assembleTrack(mapString, new
			// Vector2(-10,-50)));
			// shaderEnd = 8*15;//= mapList.get(mapList.size()-1).vertexId;

		
		}

		if (trackType == TrackType.FORREST) {
			groundFiller = gameLoader.Assets.get("worlds/forrest/texture.png",
					Texture.class);
			LAYER_COLOR = Globals.FORREST_GREEN;
		} else if (trackType == TrackType.ARTIC) {
			groundFiller = gameLoader.Assets.get("worlds/artic/texture.png",
					Texture.class);
			LAYER_COLOR = Globals.ARTIC_BLUE;
		} else {
			groundFiller = gameLoader.Assets.get("worlds/forrest/texture.png",
					Texture.class);
			LAYER_COLOR = Globals.FORREST_GREEN;
		}

		groundFixtureName.setBaseName(ComponentNames.GROUND);

	}

	public void reset() {
		lastDrawnPointer = 0;
		lastRemovedPointer = 1;

		mapList = new ArrayList<GroundUnitDescriptor>();
		createFloor();
	}

	public boolean isLoading() {
		return loading;
	}

	private void createFloor() {
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.StaticBody;
		// float w = Gdx.graphics.getWidth();
		// float h = Gdx.graphics.getHeight() - 300;
		floor = world.createBody(bodyDef2);

		JSONComponentName floorName = new JSONComponentName();
		floorName.setBaseName(ComponentNames.GROUND);

		floor.setUserData(floorName);

		GroundUnitDescriptor gud = new GroundUnitDescriptor(new Vector2(-60
				* UNIT_LENGTH, TRACK_Y_OFFSET), new Vector2(-5 * UNIT_LENGTH,
				TRACK_Y_OFFSET), true);// ,
		// "temp_ground.png","temp_ground_filler_premade.png");
		mapList.add(gud);
		// gud.fixture = drawEdge(gud.start, gud.end);
		gud.setFixture(drawEdge(gud.getStart(), gud.getEnd()));
	}

	public void addFloorUnitToMap() {

		gud = mapList.get(lastDrawnPointer);

		if (getEdge() > gud.getEnd().x) {

			lastDrawnPointer++;
			gud = mapList.get(lastDrawnPointer);
			gud.setFixture(drawEdge(gud.getStart(), gud.getEnd()));

			if (mapList.size() <= lastRemovedPointer)
				return;

			gud = mapList.get(lastRemovedPointer);

			verticalEdge = drawEdge(gud.getStart(), new Vector2(
					gud.getStart().x, gud.getStart().y + 150));

			if (getBackEdge() > gud.getStart().x) {
				ADD_FLOOR_COUNT = ADD_FLOOR_COUNT_FINAL;
				floor.destroyFixture(verticalEdge);
				verticalEdge = drawEdge(gud.getEnd(), new Vector2(
						gud.getEnd().x, gud.getEnd().y + 50));

				gud = mapList.get(lastRemovedPointer);
				shaderStart = gud.getVertexId();
				gud.deleteUnit(floor);// drawList
				// mapList.remove(lastRemovedPointer);
				lastRemovedPointer++;

			}
		} else {
			// initial = 0;
			loading = false;
			ADD_FLOOR_COUNT_FINAL = 3;
		}

		/*
		 * if (mapList.size() <= lastRemovedPointer) return;
		 * 
		 * gud = mapList.get(lastRemovedPointer);
		 * 
		 * if(getBackEdge(cam) < gud.end.x){ if(gud.isFixtureDeleted()){
		 * gud.setFixture(drawEdge(gud.start, gud.end)); lastRemovedPointer--; }
		 * }
		 */

	}

	public void getNextFloorUnit(boolean forMainMenu) {

		float randf = (float) (r.nextFloat());
		lastObj = mapList.get(mapList.size() - 1);

		generateBias(r, forMainMenu);

		randf = (float) (r.nextFloat() * variation - variation / 2 + bias);

		if (getEdge() > lastObj.getEnd().x) {
			if (infinate || forMainMenu) {
				addGroundUnitDescriptor(lastObj, randf);
			} else {
				addGroundUnitDescriptor(lastObj,
						preMadeMapList.get(mapListCounter).getStart());
				++mapListCounter;

				if (mapListCounter >= preMadeMapList.size()) {
					infinate = true;
				}
			}
			/*
			 * if (camera.position.x - GamePlayScreen.CAMERA_OFFSET >
			 * preMadeMapList .get(0).getEnd().x) { ++progressCounter; }
			 */

		}

		if (flatFloorCount >= FLAT_FLOOR_COUNT) {
			variation = VARIATION;
			baising = BIASING;
		} else {
			flatFloorCount++;
		}

	}

	private GroundUnitDescriptor newObj;

	private void addGroundUnitDescriptor(GroundUnitDescriptor lastObj,
			float randf) {
		newObj = new GroundUnitDescriptor(lastObj.getEnd(), new Vector2(
				lastObj.getEnd().x + UNIT_LENGTH, lastObj.getEnd().y + randf),
				true);
		// ,"temp_ground.png", "temp_ground_filler_premade.png");
		mapList.add(newObj);
		shaderEnd = newObj.getVertexId();
		// return newObj;
	}

	private void addGroundUnitDescriptor(GroundUnitDescriptor lastObj,
			Vector2 point) {
		newObj = new GroundUnitDescriptor(lastObj.getEnd(), new Vector2(
				point.x, point.y), true);
		// , "temp_ground.png","temp_ground_filler_premade.png");
		mapList.add(newObj);
		shaderEnd = newObj.getVertexId();
		// return newObj;
	}

	private void generateBias(Random r, boolean forMainMenu) {
		// float randf = (float) (r.nextFloat());
		bias = Math.sin(angle) * baising;

		/*
		 * if(!forMainMenu){ if (randf > 0.55f) { bias = 0; }
		 * 
		 * angle += PERIOD; if (angle > Math.PI * 2) { angle = 0; } }
		 */

	}

	private float getEdge() {
		return camera.getViewPortRightEdge() + UNIT_LENGTH * 40;
	}

	private float getBackEdge() {
		return camera.getViewPortLeftEdge() - UNIT_LENGTH * BACK_EDGE_UNITS;
	}

	public void draw(SpriteBatch batch) {

		if (addFloorCount > ADD_FLOOR_COUNT) {
			getNextFloorUnit(false);
			addFloorUnitToMap();

			addFloorCount = 0;
		}

		++addFloorCount;

		decor.draw(batch);
		track.draw(batch);

		/*
		 * Iterator<GroundUnitDescriptor> iter = mapList.iterator();
		 * 
		 * while (iter.hasNext()) { GroundUnitDescriptor groundItem =
		 * iter.next(); if (!groundItem.isFixtureDeleted())
		 * groundItem.drawShadow(batch); }
		 * 
		 * iter = mapList.iterator();
		 * 
		 * while (iter.hasNext()) { GroundUnitDescriptor groundItem =
		 * iter.next(); if (!groundItem.isFixtureDeleted())
		 * groundItem.draw(batch); }
		 */

	}

	public void drawMainMenu(SpriteBatch batch) {

		if (addFloorCount > ADD_FLOOR_COUNT) {
			getNextFloorUnit(true);
			addFloorUnitToMap();

			addFloorCount = 0;
		}

		++addFloorCount;

	}

	public void drawShapes() {
		/*
		 * Iterator<GroundUnitDescriptor> iter = mapList.iterator();
		 * 
		 * while (iter.hasNext()) { GroundUnitDescriptor groundItem =
		 * iter.next(); if (!groundItem.isFixtureDeleted())
		 * groundItem.drawShapes(cam,shader); }
		 */

		vertexCount = (shaderEnd - shaderStart) * 2;

		shader.begin();
		shader.setUniformMatrix("u_projTrans", camera.combined);
		// shader.setUniformi("u_texture", 0);
		GameMesh.drawLayer(camera, shader, shaderStart, shaderEnd,
				groundFiller, 30, Color.WHITE, 0f, 0, vertexCount);
		shader.end();

		colorShader.begin();
		colorShader.setUniformMatrix("u_projTrans", camera.combined);
		// GameMesh.drawLayer(camera, colorShader, shaderStart, shaderEnd, null,
		// 0.8f, Globals.TRANSPERENT_BLACK, 0.0f, 1, vertexCount);
		GameMesh.drawLayer(camera, colorShader, shaderStart, shaderEnd, null,
				0.6f, LAYER_COLOR, 0.1f, 1, vertexCount);
		// GameMesh.drawLayer(camera, colorShader, shaderStart, shaderEnd, null,
		// 0.1f, Globals.GREEN1, 0.6f, 3, vertexCount);
		colorShader.end();

	}

	public Fixture drawEdge(Vector2 v1, Vector2 v2) {

		edgeShape.set(v1, v2);
		fixtureDef.shape = edgeShape;
		// fixtureDef.density = 1;

		if (trackType == TrackType.FORREST) {
			fixtureDef.friction = 1f;
			fixtureDef.restitution = 0.4f;
		} else if (trackType == TrackType.ARTIC) {
			fixtureDef.friction = 0.5f;
			fixtureDef.restitution = 0.2f;
		}

		fixtureDef.filter.groupIndex = Globals.GROUND_GROUP;
		groundFixture = floor.createFixture(fixtureDef);
		groundFixture.setUserData(groundFixtureName);

		return groundFixture;
	}

	public Shape getShapeBase(Vector2 pos) {

		PolygonShape shape = new PolygonShape();

		shape.setAsBox(UNIT_LENGTH, 1f, pos, 0);

		return shape;

	}

	public void destory() {
		edgeShape.dispose();

	}

	public float getTotalTrackLength() {
		return track.getTotalTrackLength();
	}

	public boolean destroyComponent(JSONComponentName name) {
		return track.destroyComponent(name);

	}

}

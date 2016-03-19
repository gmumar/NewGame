package GroundWorks;

import java.util.ArrayList;
import java.util.Random;

import wrapper.CameraManager;
import wrapper.GamePhysicalState;
import wrapper.GamePreferences;
import wrapper.Globals;
import Assembly.Assembler;
import Component.ComponentNames;
import JSONifier.JSONComponentName;
import Shader.GameMesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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

	final static float UNIT_LENGTH = 2;
	final static float VARIATION = 1f;
	final static float BIASING = 2f;
	final static float PERIOD = 0.3f;
	public final static int BACK_EDGE_UNITS = 30;

	private final static int TRACK_HEIGHT = 30;
	private final static int TRACK_WIDTH = 0;

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

	private Preferences prefs = Gdx.app
			.getPreferences(GamePreferences.CAR_PREF_STR);

	private Assembler assembler = new Assembler();

	private Integer lastDrawnPointer = 0;
	private Integer lastRemovedPointer = 1;

	private GroundUnitDescriptor lastObj;
	private GroundUnitDescriptor gud;

	private static Texture groundFiller;

	private static ShaderProgram shader;
	private static ShaderProgram colorShader;

	private int initial = 0 * 8;
	public boolean loading = true;

	private float totalTrackLength = -1;

	private JSONComponentName groundFixtureName = new JSONComponentName();
	private Fixture groundFixture;

	public GroundBuilder(GamePhysicalState gameState,
			final CameraManager camera, ShaderProgram shader,
			ShaderProgram colorShader) {
		this.world = gameState.getWorld();
		this.gameLoader = gameState.getGameLoader();
		this.camera = camera;
		GroundBuilder.shader = shader;
		GroundBuilder.colorShader = colorShader;

		createFloor();
		decor = new GroundDecor(gameState);

		String mapString = prefs.getString(GamePreferences.TRACK_MAP_STR, null);
		// System.out.println(mapString);
		if (mapString == null) {
			infinate = true;
		} else {
			infinate = false;
			mapListCounter = 0;
			preMadeMapList = assembler.assembleTrack(mapString, new Vector2(
					TRACK_WIDTH, TRACK_HEIGHT));

			totalTrackLength = calculateTotalTrackLength(preMadeMapList);
			decor.addChequeredFlag(preMadeMapList);
			// mapList.addAll(assembler.assembleTrack(mapString, new
			// Vector2(-10,-50)));
			// shaderEnd = 8*15;//= mapList.get(mapList.size()-1).vertexId;

			// System.out.println("point on map " + mapList.size() + " vertex: "
			// + shaderEnd);
		}

		groundFiller = gameLoader.Assets.get("temp_ground_filler.png",
				Texture.class);

		groundFixtureName.setBaseName(ComponentNames.GROUND);

	}

	private float calculateTotalTrackLength(ArrayList<GroundUnitDescriptor> map) {
		GroundUnitDescriptor lastPos = preMadeMapList
				.get(preMadeMapList.size() - 1);
		return lastPos.getEnd().dst(0, lastPos.getEnd().y);
	}

	public float getTotalTrackLength() {
		return totalTrackLength;
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

		GroundUnitDescriptor gud = new GroundUnitDescriptor(new Vector2(-60
				* UNIT_LENGTH, TRACK_HEIGHT), new Vector2(-5 * UNIT_LENGTH,
				TRACK_HEIGHT), true);// ,
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
			initial = 0;
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

	public void draw(SpriteBatch batch, boolean forMainMenu) {

		if (addFloorCount > ADD_FLOOR_COUNT) {
			getNextFloorUnit(forMainMenu);
			addFloorUnitToMap();

			addFloorCount = 0;
		}

		++addFloorCount;

		if (!forMainMenu) {
			decor.draw(batch);
		}

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

	public void drawShapes() {
		/*
		 * Iterator<GroundUnitDescriptor> iter = mapList.iterator();
		 * 
		 * while (iter.hasNext()) { GroundUnitDescriptor groundItem =
		 * iter.next(); if (!groundItem.isFixtureDeleted())
		 * groundItem.drawShapes(cam,shader); }
		 */

		int vertexCount = (shaderEnd - shaderStart) * 2;

		shader.begin();
		shader.setUniformMatrix("u_projTrans", camera.combined);
		GameMesh.drawLayer(camera, shader, shaderStart, shaderEnd,
				groundFiller, 30, Color.WHITE, 0f, 0, vertexCount);
		shader.end();

		colorShader.begin();
		colorShader.setUniformMatrix("u_projTrans", camera.combined);
		GameMesh.drawLayer(camera, colorShader, shaderStart, shaderEnd, null,
				0.8f, Globals.TRANSPERENT_BLACK, 0.0f, 1, vertexCount);
		GameMesh.drawLayer(camera, colorShader, shaderStart, shaderEnd, null,
				0.6f, Globals.GREEN, 0.5f, 2, vertexCount);
		GameMesh.drawLayer(camera, colorShader, shaderStart, shaderEnd, null,
				0.1f, Globals.GREEN1, 0.6f, 3, vertexCount);
		colorShader.end();

	}

	public Fixture drawEdge(Vector2 v1, Vector2 v2) {

		edgeShape.set(v1, v2);
		fixtureDef.shape = edgeShape;
		// fixtureDef.density = 1;
		fixtureDef.friction = 1;
		fixtureDef.restitution = 0.5f;

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

}

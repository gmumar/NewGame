package GroundWorks;

import java.util.ArrayList;
import java.util.Random;

import wrapper.CameraManager;
import wrapper.GamePreferences;
import wrapper.GamePhysicalState;
import wrapper.Globals;
import Assembly.Assembler;
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
import com.gudesigns.climber.GamePlayScreen;

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

	private static final int ADD_FLOOR_COUNT_FINAL = 3;
	private int addFloorCount;
	private int ADD_FLOOR_COUNT = 0;
	private int flatFloorCount;
	private static final int FLAT_FLOOR_COUNT = 60;

	private boolean infinate = true;
	private int mapListCounter = 0;
	private int progressCounter = 0;

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
	
	private static Texture groundFiller ;
	
	private static ShaderProgram shader;
	private static ShaderProgram colorShader;

	public GroundBuilder(GamePhysicalState gameState, CameraManager camera, ShaderProgram shader, ShaderProgram colorShader) {
		this.world = gameState.getWorld();
		this.gameLoader = gameState.getGameLoader();
		this.camera = camera;
		GroundBuilder.shader = shader;
		GroundBuilder.colorShader = colorShader;

		createFloor();
		decor = new GroundDecor(gameState);

		String mapString = prefs.getString(GamePreferences.TRACK_MAP_STR, null);
		//System.out.println(mapString);
		if (mapString == null) {
			infinate = true;
		} else {
			infinate = false;
			mapListCounter = 0;
			preMadeMapList = assembler.assembleTrack(mapString, new Vector2(
					TRACK_WIDTH, TRACK_HEIGHT));

			decor.addChequeredFlag(preMadeMapList);
			// mapList.addAll(assembler.assembleTrack(mapString, new
			// Vector2(-10,-50)));
			// shaderEnd = 8*15;//= mapList.get(mapList.size()-1).vertexId;

			// System.out.println("point on map " + mapList.size() + " vertex: "
			// + shaderEnd);
		}
		
		groundFiller = gameLoader.Assets.get("temp_ground_filler.png", Texture.class);

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

	public void drawFloor(CameraManager cam) {

		gud = mapList.get(lastDrawnPointer);

		if (getEdge(cam) > gud.getEnd().x) {

			lastDrawnPointer++;
			gud = mapList.get(lastDrawnPointer);
			gud.setFixture(drawEdge(gud.getStart(), gud.getEnd()));

			if (mapList.size() <= lastRemovedPointer)
				return;

			gud = mapList.get(lastRemovedPointer);

			verticalEdge = drawEdge(gud.getStart(), new Vector2(
					gud.getStart().x, gud.getStart().y + 150));

			if (getBackEdge(cam) > gud.getStart().x) {
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

	public void updateFloor(CameraManager cam) {

		float randf = (float) (r.nextFloat());
		lastObj = mapList.get(mapList.size() - 1);

		generateBias(r);

		randf = (float) (r.nextFloat() * variation - variation / 2 + bias);

		if (getEdge(cam) > lastObj.getEnd().x) {
			if (infinate) {
				addGroundUnitDescriptor(lastObj, randf);
			} else {
				addGroundUnitDescriptor(lastObj,
						preMadeMapList.get(mapListCounter).getStart());
				++mapListCounter;

				if (mapListCounter >= preMadeMapList.size()) {
					infinate = true;
				}
			}
			if (camera.position.x - GamePlayScreen.CAMERA_OFFSET > preMadeMapList
					.get(0).getEnd().x) {
				++progressCounter;
			}

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
				lastObj.getEnd().x + UNIT_LENGTH, lastObj.getEnd().y + randf), true);
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

	private void generateBias(Random r) {
		float randf = (float) (r.nextFloat());
		bias = Math.sin(angle) * baising;

		if (randf > 0.55f) {
			bias = 0;
		}

		angle += PERIOD;
		if (angle > Math.PI * 2) {
			angle = 0;
		}

	}

	private float getEdge(CameraManager cam) {
		return cam.getViewPortRightEdge() + UNIT_LENGTH * 40;
	}

	private float getBackEdge(CameraManager cam) {
		return cam.getViewPortLeftEdge() - UNIT_LENGTH * BACK_EDGE_UNITS;
	}

	public void draw(CameraManager cam, SpriteBatch batch) {
		if (addFloorCount > ADD_FLOOR_COUNT) {
			updateFloor(cam);
			drawFloor(cam);
			addFloorCount = 0;
		}

		++addFloorCount;

		decor.draw(batch);

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

	public void drawShapes(CameraManager cam) {
		/*
		 * Iterator<GroundUnitDescriptor> iter = mapList.iterator();
		 * 
		 * while (iter.hasNext()) { GroundUnitDescriptor groundItem =
		 * iter.next(); if (!groundItem.isFixtureDeleted())
		 * groundItem.drawShapes(cam,shader); }
		 */
		shader.begin();
		GameMesh.flush(cam, shader, shaderStart, shaderEnd,
				groundFiller,
				30, Color.WHITE, 0f,0);
		shader.end();

		colorShader.begin();
		GameMesh.flush(cam, colorShader, shaderStart, shaderEnd, null, 0.8f,
				Globals.TRANSPERENT_BLACK, 0.0f,1);
		GameMesh.flush(cam, colorShader, shaderStart, shaderEnd, null, 0.6f,
				Globals.GREEN, 0.5f,2);
		GameMesh.flush(cam, colorShader, shaderStart, shaderEnd, null, 0.1f,
				Globals.GREEN1, 0.6f,3);
		colorShader.end();

	}

	public Fixture drawEdge(Vector2 v1, Vector2 v2) {

		edgeShape.set(v1, v2);
		fixtureDef.shape = edgeShape;
		//fixtureDef.density = 1;
		fixtureDef.friction = 1;
		fixtureDef.restitution = 0.5f;

		//Fixture f = ;

		return floor.createFixture(fixtureDef);
	}

	public Shape getShapeBase(Vector2 pos) {

		PolygonShape shape = new PolygonShape();

		shape.setAsBox(UNIT_LENGTH, 1f, pos, 0);

		return shape;

	}

	public void destory() {
		edgeShape.dispose();
	}

	public float getProgress() {

		float progress = ((float) progressCounter / (preMadeMapList.size()))
				* (100 - 0);
		progress = progress > 100 ? 100 : progress;

		return progress;

	}

}

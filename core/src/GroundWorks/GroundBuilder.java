package GroundWorks;

import java.util.ArrayList;
import java.util.Random;

import wrapper.CameraManager;
import wrapper.GamePreferences;
import wrapper.Globals;
import Assembly.Assembler;
import JSONifier.JSONTrack;
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
import com.gudesigns.climber.GamePlayScreen;

public class GroundBuilder {

	final static float UNIT_LENGTH = 2;
	final static float VARIATION = 1f;
	final static float BIASING = 2f;
	final static float PERIOD = 0.3f;
	public final static int BACK_EDGE_UNITS = 30;

	final static int TRACK_HEIGHT = 0;

	float variation = 0.5f, baising = 0.1f;

	World world;
	Body floor;
	CameraManager camera;
	float lastRightEdge, lastLeftEdge;
	Vector2 lastPointDrawn = null;
	EdgeShape edgeShape = new EdgeShape();
	FixtureDef fixtureDef = new FixtureDef();
	double angle = 0, bias = 0;
	int shaderStart = 0, shaderEnd = 0;
	Random r = new Random();
	GroundDecor decor;

	static final int ADD_FLOOR_COUNT_FINAL = 3;
	int addFloorCount;
	int ADD_FLOOR_COUNT = 0;
	int flatFloorCount;
	static final int FLAT_FLOOR_COUNT = 60;

	boolean infinate = true;
	int mapListCounter = 0;
	int progressCounter = 0;
	
	ArrayList<GroundUnitDescriptor> preMadeMapList;

	Fixture verticalEdge;

	ArrayList<GroundUnitDescriptor> mapList = new ArrayList<GroundUnitDescriptor>();

	Preferences prefs = Gdx.app.getPreferences(GamePreferences.CAR_PREF_STR);

	Assembler assembler = new Assembler();
	JSONTrack jsonTrack = new JSONTrack();

	Integer lastDrawnPointer = 0;
	Integer lastRemovedPointer = 1;

	public GroundBuilder(World world, CameraManager camera) {
		this.world = world;
		this.camera = camera;

		lastLeftEdge = camera.getViewPortLeftEdge();
		lastRightEdge = camera.getViewPortRightEdge();

		createFloor();
		decor = new GroundDecor(world);

		String mapString = prefs.getString(GamePreferences.TRACK_MAP_STR, null);
		System.out.println(mapString);
		if (mapString == null) {
			infinate = true;
		} else {
			infinate = false;
			mapListCounter = 0;
			preMadeMapList = assembler.assembleTrack(mapString, new Vector2(0,
					TRACK_HEIGHT));
			
			decor.addChequeredFlag(preMadeMapList);
			// mapList.addAll(assembler.assembleTrack(mapString, new
			// Vector2(-10,-50)));
			// shaderEnd = 8*15;//= mapList.get(mapList.size()-1).vertexId;

			// System.out.println("point on map " + mapList.size() + " vertex: "
			// + shaderEnd);
		}
		
		
	}

	private void createFloor() {
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.StaticBody;
		// float w = Gdx.graphics.getWidth();
		// float h = Gdx.graphics.getHeight() - 300;
		floor = world.createBody(bodyDef2);

		GroundUnitDescriptor gud = new GroundUnitDescriptor(new Vector2(-50
				* UNIT_LENGTH, TRACK_HEIGHT), new Vector2(-5 * UNIT_LENGTH,
				TRACK_HEIGHT), "temp_ground.png",
				"temp_ground_filler_premade.png");
		mapList.add(gud);
		gud.fixture = drawEdge(gud.start, gud.end);

	}

	public void drawFloor(CameraManager cam) {

		GroundUnitDescriptor gud = mapList.get(lastDrawnPointer);

		if (getEdge(cam) > gud.end.x) {

			lastDrawnPointer++;
			gud = mapList.get(lastDrawnPointer);
			gud.setFixture(drawEdge(gud.start, gud.end));

			if (mapList.size() <= lastRemovedPointer)
				return;

			gud = mapList.get(lastRemovedPointer);

			verticalEdge = drawEdge(gud.start, new Vector2(gud.start.x,
					gud.start.y + 150));

			if (getBackEdge(cam) > gud.start.x) {
				ADD_FLOOR_COUNT = ADD_FLOOR_COUNT_FINAL;
				floor.destroyFixture(verticalEdge);
				verticalEdge = drawEdge(gud.end, new Vector2(gud.end.x,
						gud.end.y + 150));

				gud = mapList.get(lastRemovedPointer);
				shaderStart = gud.vertexId;
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
		GroundUnitDescriptor lastObj = mapList.get(mapList.size() - 1);

		generateBias(r);

		randf = (float) (r.nextFloat() * variation - variation / 2 + bias);

		if (getEdge(cam) > lastObj.end.x) {
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
			if(camera.position.x - GamePlayScreen.CAMERA_OFFSET>preMadeMapList.get(0).end.x){
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

	private void addGroundUnitDescriptor(GroundUnitDescriptor lastObj,
			float randf) {
		GroundUnitDescriptor newObj = new GroundUnitDescriptor(
				lastObj.end,
				new Vector2(lastObj.end.x + UNIT_LENGTH, lastObj.end.y + randf),
				"temp_ground.png", "temp_ground_filler_premade.png");
		mapList.add(newObj);
		shaderEnd = newObj.vertexId;
		// return newObj;
	}

	private void addGroundUnitDescriptor(GroundUnitDescriptor lastObj,
			Vector2 point) {
		GroundUnitDescriptor newObj = new GroundUnitDescriptor(lastObj.end,
				new Vector2(point.x, point.y), "temp_ground.png",
				"temp_ground_filler_premade.png");
		mapList.add(newObj);
		shaderEnd = newObj.vertexId;
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

	public void drawShapes(CameraManager cam, ShaderProgram shader,
			ShaderProgram colorShader) {
		/*
		 * Iterator<GroundUnitDescriptor> iter = mapList.iterator();
		 * 
		 * while (iter.hasNext()) { GroundUnitDescriptor groundItem =
		 * iter.next(); if (!groundItem.isFixtureDeleted())
		 * groundItem.drawShapes(cam,shader); }
		 */
		shader.begin();
		GameMesh.flush(cam, shader, shaderStart, shaderEnd,
				Globals.Assets.get("temp_ground_filler.png",Texture.class), 30,
				Color.WHITE, 0f);
		shader.end();

		colorShader.begin();
		GameMesh.flush(cam, colorShader, shaderStart, shaderEnd, null, 0.8f,
				Globals.TRANSPERENT_BLACK, 0.0f);
		GameMesh.flush(cam, colorShader, shaderStart, shaderEnd, null, 0.6f,
				Globals.GREEN, 0.5f);
		GameMesh.flush(cam, colorShader, shaderStart, shaderEnd, null, 0.1f,
				Globals.GREEN1, 0.6f);
		colorShader.end();

	}

	public Fixture drawEdge(Vector2 v1, Vector2 v2) {

		edgeShape.set(v1, v2);
		fixtureDef.shape = edgeShape;
		fixtureDef.density = 1;
		fixtureDef.friction = 5;
		fixtureDef.restitution = 0;

		Fixture f = floor.createFixture(fixtureDef);

		return f;
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
		
		float progress = ((float) progressCounter / (preMadeMapList.size())) * (100-0);
		progress = progress > 100 ? 100 : progress; 
		
		return progress;

	}

}

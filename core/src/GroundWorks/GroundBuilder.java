package GroundWorks;

import java.util.ArrayList;
import java.util.Random;

import wrapper.CameraManager;
import wrapper.Globals;
import wrapper.TextureLibrary;
import Shader.GameMesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

public class GroundBuilder {

	final static float UNIT_LENGTH = 2;
	final static float VARIATION = 1f;
	final static float BIASING = 2f;
	final static float PERIOD = 0.3f;
	
	float variation = 0.5f, baising = 0.1f;

	World world;
	Body floor;
	CameraManager camera;
	float lastRightEdge, lastLeftEdge;
	Vector2 lastPointDrawn = null;
	EdgeShape edgeShape = new EdgeShape();
	FixtureDef fixtureDef = new FixtureDef();
	double angle = 0, bias = 0;
	int shaderStart=0, shaderEnd=0;
	Random r = new Random();
	
	int addFloorCount;
	final int ADD_FLOOR_COUNT = 3;
	int flatFloorCount;
	final int FLAT_FLOOR_COUNT = 20;
	
	Fixture verticalEdge;

	ArrayList<GroundUnitDescriptor> mapList = new ArrayList<GroundUnitDescriptor>();

	Integer lastDrawnPointer = 0;
	Integer lastRemovedPointer = 1;

	public GroundBuilder(World world, CameraManager camera) {
		this.world = world;
		this.camera = camera;

		lastLeftEdge = camera.getViewPortLeftEdge();
		lastRightEdge = camera.getViewPortRightEdge();

		createFloor();
		System.gc();
	}

	private void createFloor() {
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.StaticBody;
		//float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight() - 300;
		floor = world.createBody(bodyDef2);

		GroundUnitDescriptor gud = new GroundUnitDescriptor(new Vector2(-10
				* UNIT_LENGTH, -h / 2), new Vector2(-5 * UNIT_LENGTH, -h / 2),
				"temp_ground.png", "temp_ground_filler_premade.png");
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
			
			verticalEdge = drawEdge(gud.start, new Vector2(gud.start.x,gud.start.y+150));

			if (getBackEdge(cam) > gud.start.x) {
				floor.destroyFixture(verticalEdge);
				verticalEdge = drawEdge(gud.end, new Vector2(gud.end.x,gud.end.y+150));
				
				
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
			GroundUnitDescriptor newObj = new GroundUnitDescriptor(lastObj.end,
					new Vector2(lastObj.end.x + UNIT_LENGTH, lastObj.end.y
							+ randf), "temp_ground.png", "temp_ground_filler_premade.png");
			mapList.add(newObj);
			shaderEnd = newObj.vertexId;
		}
		
		
		if(flatFloorCount >= FLAT_FLOOR_COUNT){
			variation = VARIATION;
			baising = BIASING;
		}else{
			flatFloorCount++;
		}

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
		return cam.getViewPortLeftEdge() - UNIT_LENGTH * 35;
	}

	public void draw(CameraManager cam, SpriteBatch batch) {
		if(addFloorCount > ADD_FLOOR_COUNT){
			updateFloor(cam);
			drawFloor(cam);
			addFloorCount = 0;
		}
		
		++addFloorCount;

		/*Iterator<GroundUnitDescriptor> iter = mapList.iterator();
		
		while (iter.hasNext()) {
			GroundUnitDescriptor groundItem = iter.next();
			if (!groundItem.isFixtureDeleted())
				groundItem.drawShadow(batch);
		}
		
		iter = mapList.iterator();

		while (iter.hasNext()) {
			GroundUnitDescriptor groundItem = iter.next();
			if (!groundItem.isFixtureDeleted())
				groundItem.draw(batch);
		}*/

	}
	
	public void drawShapes(CameraManager cam,ShaderProgram shader){
		/*Iterator<GroundUnitDescriptor> iter = mapList.iterator();

		while (iter.hasNext()) {
			GroundUnitDescriptor groundItem = iter.next();
			if (!groundItem.isFixtureDeleted())
				groundItem.drawShapes(cam,shader);
		}*/
		shader.begin();
		GameMesh.flush(cam, shader,shaderStart,shaderEnd,TextureLibrary.getTexture("temp_ground_filler.png"), 30, Color.WHITE, 0f);
		
		GameMesh.flush(cam, shader,shaderStart,shaderEnd, null, 0.8f, Globals.TRANSPERENT_BLACK, 0.0f);
		GameMesh.flush(cam, shader,shaderStart,shaderEnd, null, 0.6f, Globals.GREEN, 0.5f);
		GameMesh.flush(cam, shader,shaderStart,shaderEnd, null, 0.1f, Globals.GREEN1, 0.6f);
		shader.end();
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
		TextureLibrary.destroyMap();
		edgeShape.dispose();
	}

}

package GroundWorks;

import java.util.ArrayList;
import java.util.Random;

import wrapper.CameraManager;

import com.badlogic.gdx.Gdx;
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

	final float UNIT_LENGTH = 10;

	World world;
	Body floor;
	CameraManager camera;
	float lastRightEdge, lastLeftEdge;
	Vector2 lastPointDrawn = null;

	ArrayList<GroundUnitDescriptor> mapList = new ArrayList<GroundUnitDescriptor>();

	Integer lastDrawnPointer = 0;
	Integer lastRemovedPointer = 1;
	
	private class GroundUnitDescriptor {
		Vector2 start, end;
		Fixture fixture;
		boolean fixtureDeleted = true;

		public GroundUnitDescriptor(Vector2 start, Vector2 end) {
			this.start = start;
			this.end = end;
		}

		public void deleteFixture() {
			floor.destroyFixture(fixture);
			fixtureDeleted = true;
		}

		public void setFixture(Fixture drawEdge) {
			this.fixture = drawEdge;
			fixtureDeleted = false;
		}

		public boolean isFixtureDeleted() {
			return fixtureDeleted;
		}
	}

	public GroundBuilder(World world, CameraManager camera) {
		this.world = world;
		this.camera = camera;

		lastLeftEdge = camera.getViewPortLeftEdge();
		lastRightEdge = camera.getViewPortRightEdge();

		createFloor();
	}

	private void createFloor() {
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.StaticBody;
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight() - 300;
		bodyDef2.position.set(0, -100);
		floor = world.createBody(bodyDef2);

		GroundUnitDescriptor gud = new GroundUnitDescriptor(new Vector2(-w / 2,
				-h / 2), new Vector2(w / 2, -h / 2));
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
	
			if ( getBackEdge(cam) > gud.start.x) {
				gud = mapList.get(lastRemovedPointer);
				gud.deleteFixture();
				lastRemovedPointer++;
			}
		}
		
		if (mapList.size() <= lastRemovedPointer)
			return;

		gud = mapList.get(lastRemovedPointer);
		
		if(getBackEdge(cam) < gud.end.x){
			if(gud.isFixtureDeleted()){
				gud.setFixture(drawEdge(gud.start, gud.end));
				lastRemovedPointer--;
			}
		}
		
		

	}

	public void updateFloor(CameraManager cam) {

		GroundUnitDescriptor lastObj = mapList.get(mapList.size() - 1);
		Random r = new Random();
		float randf = r.nextFloat() * 10 - 5;

		if (getEdge(cam) > lastObj.end.x) {
			GroundUnitDescriptor newObj = new GroundUnitDescriptor(lastObj.end,
					new Vector2(lastObj.end.x + UNIT_LENGTH, lastObj.end.y
							+ randf));
			mapList.add(newObj);
		}

	}

	private float getEdge(CameraManager cam) {
		return cam.getViewPortRightEdge() + UNIT_LENGTH * 3;
	}
	
	private float getBackEdge(CameraManager cam) {
		return cam.getViewPortLeftEdge() - UNIT_LENGTH * 8;
	}

	public void draw(CameraManager cam) {
		updateFloor(cam);
		drawFloor(cam);

	}

	public Fixture drawEdge(Vector2 v1, Vector2 v2) {
		FixtureDef fixtureDef = new FixtureDef();

		EdgeShape edgeShape = new EdgeShape();
		edgeShape.set(v1, v2);
		fixtureDef.shape = edgeShape;
		fixtureDef.density = 1;
		fixtureDef.friction = 5;
		fixtureDef.restitution = 0;

		Fixture f = floor.createFixture(fixtureDef);
		edgeShape.dispose();

		return f;
	}

	public Shape getShapeBase(Vector2 pos) {

		PolygonShape shape = new PolygonShape();

		shape.setAsBox(UNIT_LENGTH, 1f, pos, 0);

		return shape;

	}

}

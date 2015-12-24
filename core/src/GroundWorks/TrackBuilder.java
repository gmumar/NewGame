package GroundWorks;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.CameraManager;
import wrapper.TouchUnit;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class TrackBuilder {

	World world;
	Body floor;
	EdgeShape edgeShape = new EdgeShape();
	FixtureDef fixtureDef = new FixtureDef();
	Body box = null;

	CameraManager camera;

	int SQUARE = 10;

	ArrayList<GroundUnitDescriptor> mapList = new ArrayList<GroundUnitDescriptor>();

	public TrackBuilder(World world, CameraManager cam) {
		this.world = world;
		this.camera = cam;

		createFloor();
	}
	
	public ArrayList<GroundUnitDescriptor> getMapList(){
		return mapList;
	}

	private void createFloor() {
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.StaticBody;
		// float w = Gdx.graphics.getWidth();
		// float h = Gdx.graphics.getHeight() - 300;
		floor = world.createBody(bodyDef2);

		GroundUnitDescriptor gud = new GroundUnitDescriptor(new Vector2( GroundBuilder.UNIT_LENGTH, 0), new Vector2(
				  GroundBuilder.UNIT_LENGTH, 0), "temp_ground.png");
		mapList.add(gud);
		gud.fixture = drawEdge(gud.start, gud.end);
		mapList.add(gud);
		gud.fixture = drawEdge(gud.start, gud.end);

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

	public Body drawBox(float x, float y, float sizex, float sizey) {
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

		return boxBody;

	}

	public void draw(SpriteBatch batch) {

		Iterator<GroundUnitDescriptor> iter = mapList.iterator();

		while (iter.hasNext()) {
			GroundUnitDescriptor groundItem = iter.next();
			if (!groundItem.isFixtureDeleted())
				;
			// groundItem.draw(batch);
		}

	}

	public void handleTouches(ArrayList<TouchUnit> touches) {

		Iterator<TouchUnit> iter = touches.iterator();
		Vector3 point = new Vector3();

		while (iter.hasNext()) {
			TouchUnit touch = iter.next();
			if (touch.isTouched()) {

				camera.unproject(point.set(touch.screenX, touch.screenY, 0));
				GroundUnitDescriptor lastObj = mapList.get(mapList.size() - 1);

				if (point.y < lastObj.end.y + SQUARE
						&& point.y > lastObj.end.y - SQUARE
						&& point.x > lastObj.end.x
						&& point.x < lastObj.end.x + SQUARE) {

					GroundUnitDescriptor newObj = new GroundUnitDescriptor(
							lastObj.end, new Vector2(lastObj.end.x + GroundBuilder.UNIT_LENGTH, point.y),
							"temp_ground.png");//lastObj.end.x + GroundBuilder.UNIT_LENGTH

					Fixture fixture = drawEdge(newObj.start, newObj.end);
					newObj.setFixture(fixture);
					mapList.add(newObj);
					
					System.out.println(newObj.end + " " + point);

					if (box != null) {
						world.destroyBody(box);
						box = null;
					}
				} else {
					if (box == null) {
						box = drawBox(lastObj.end.x,
								lastObj.end.y - SQUARE / 2, (float) SQUARE / 2,
								(float) SQUARE);
					}
				}

			}
		}

	}

	public void destroy() {
		if(floor !=null)
			world.destroyBody(floor);
		
	}
}

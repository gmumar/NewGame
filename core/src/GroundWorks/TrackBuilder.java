package GroundWorks;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.CameraManager;
import wrapper.TouchUnit;
import JSONifier.JSONTrack;
import UserPackage.User;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class TrackBuilder {

	private World world;
	private Body floor;
	private EdgeShape edgeShape = new EdgeShape();
	private FixtureDef fixtureDef = new FixtureDef();
	private ShapeRenderer shapeRenderer;
	// private Body box = null;

	private CameraManager camera;

	private int VALID_DRAWABLE_SQUARE = 10;

	private ArrayList<GroundUnitDescriptor> mapList = new ArrayList<GroundUnitDescriptor>();

	public TrackBuilder(World world, CameraManager cam,
			ShapeRenderer fixtureRenderer) {
		this.world = world;
		this.camera = cam;
		this.shapeRenderer = fixtureRenderer;

		createFloor();

	}

	public JSONTrack loadMap() {
		return importMap(User.getInstance().getCurrentTrack());
	}

	private JSONTrack importMap(String currentTrack) {
		JSONTrack preMadeMap = JSONTrack.objectify(currentTrack);
		GroundUnitDescriptor lastObj = mapList.get(mapList.size() - 1);

		for (Vector2 point : preMadeMap.getPoints()) {
			GroundUnitDescriptor newObj = new GroundUnitDescriptor(
					lastObj.getEnd(), new Vector2(lastObj.getEnd().x
							+ GroundBuilder.UNIT_LENGTH, point.y), false);
			// ,"temp_ground.png");//lastObj.end.x +
			// GroundBuilder.UNIT_LENGTH

			Fixture fixture = drawEdge(newObj.getStart(), newObj.getEnd());
			newObj.setFixture(fixture);
			mapList.add(newObj);

			lastObj = newObj;
		}
		
		return preMadeMap;

	}

	public ArrayList<GroundUnitDescriptor> getMapList() {
		return mapList;
	}

	private void createFloor() {
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.StaticBody;
		// float w = Gdx.graphics.getWidth();
		// float h = Gdx.graphics.getHeight() - 300;
		floor = world.createBody(bodyDef2);

		GroundUnitDescriptor gud = new GroundUnitDescriptor(new Vector2(
				GroundBuilder.UNIT_LENGTH, 0), new Vector2(
				GroundBuilder.UNIT_LENGTH, 0), false);// , "temp_ground.png");
		mapList.add(gud);
		gud.setFixture(drawEdge(gud.getStart(), gud.getEnd()));
		mapList.add(gud);
		gud.setFixture(drawEdge(gud.getStart(), gud.getEnd()));

	}

	private Fixture drawEdge(Vector2 v1, Vector2 v2) {

		edgeShape.set(v1, v2);
		fixtureDef.shape = edgeShape;
		fixtureDef.density = 1;
		fixtureDef.friction = 5;
		fixtureDef.restitution = 0;

		Fixture f = floor.createFixture(fixtureDef);

		return f;
	}

	/*
	 * private Fixture drawEdge(Vector2 v1, Vector2 v2, Body body) { FixtureDef
	 * fixtureDef = new FixtureDef();
	 * 
	 * EdgeShape edgeShape = new EdgeShape(); edgeShape.set(v1, v2);
	 * fixtureDef.shape = edgeShape; fixtureDef.density = 1; fixtureDef.friction
	 * = 5; fixtureDef.restitution = 0;
	 * 
	 * Fixture f = body.createFixture(fixtureDef); edgeShape.dispose();
	 * 
	 * return f; }
	 * 
	 * private Body drawBox(float x, float y, float sizex, float sizey) {
	 * BodyDef box = new BodyDef();
	 * 
	 * box.type = BodyDef.BodyType.KinematicBody; box.position.set(x, y); Body
	 * boxBody = world.createBody(box);
	 * 
	 * drawEdge(new Vector2(x, y), new Vector2(x, sizey + y), boxBody);
	 * drawEdge(new Vector2(x, sizey + y), new Vector2(x + sizex, y + sizey),
	 * boxBody); drawEdge(new Vector2(x + sizex, y + sizey), new Vector2(x +
	 * sizex, y), boxBody); drawEdge(new Vector2(x + sizex, y), new Vector2(x,
	 * y), boxBody);
	 * 
	 * return boxBody;
	 * 
	 * }
	 */

	public void draw(SpriteBatch batch) {
		GroundUnitDescriptor lastObj = mapList.get(mapList.size() - 1);

		shapeRenderer.rect(lastObj.getEnd().x, lastObj.getEnd().y
				- VALID_DRAWABLE_SQUARE / 2, VALID_DRAWABLE_SQUARE / 2,
				VALID_DRAWABLE_SQUARE);

		/*
		 * Iterator<GroundUnitDescriptor> iter = mapList.iterator();
		 * 
		 * while (iter.hasNext()) { GroundUnitDescriptor groundItem =
		 * iter.next(); if (!groundItem.isFixtureDeleted()) ; //
		 * groundItem.draw(batch); }
		 */

	}

	public void handleTouches(ArrayList<TouchUnit> touches, int mode) {
		
		if(mode == 1){
			return;
		}

		Iterator<TouchUnit> iter = touches.iterator();
		Vector3 point = new Vector3();

		while (iter.hasNext()) {
			TouchUnit touch = iter.next();
			if (touch.isTouched()) {

				camera.unproject(point.set(touch.screenX, touch.screenY, 0));

				GroundUnitDescriptor lastObj = mapList.get(mapList.size() - 1);

				if (point.y < lastObj.getEnd().y + VALID_DRAWABLE_SQUARE
						&& point.y > lastObj.getEnd().y - VALID_DRAWABLE_SQUARE
						&& point.x > lastObj.getEnd().x
						&& point.x < lastObj.getEnd().x + VALID_DRAWABLE_SQUARE) {

					GroundUnitDescriptor newObj = new GroundUnitDescriptor(
							lastObj.getEnd(), new Vector2(lastObj.getEnd().x
									+ GroundBuilder.UNIT_LENGTH, point.y),
							false);
					// ,"temp_ground.png");//lastObj.end.x +
					// GroundBuilder.UNIT_LENGTH

					Fixture fixture = drawEdge(newObj.getStart(),
							newObj.getEnd());
					newObj.setFixture(fixture);
					mapList.add(newObj);

					/*
					 * if (box != null) { world.destroyBody(box); box = null; }
					 */
				} else if (point.x < lastObj.getEnd().x - VALID_DRAWABLE_SQUARE
						/ 4f) {
					
					
					if(mode !=2) return;

					if (point.x > 2 && mapList.size() > (point.x / 2)) {

						lastObj = mapList.get((int) (point.x / 2) - 1);

						if (point.y > lastObj.getEnd().y
								- VALID_DRAWABLE_SQUARE) {

							mapList.get((int) (point.x / 2)).deleteUnit(floor);
							mapList.remove((int) (point.x / 2));

							GroundUnitDescriptor newObj = new GroundUnitDescriptor(
									lastObj.getEnd(),
									new Vector2(lastObj.getEnd().x
											+ GroundBuilder.UNIT_LENGTH,
											point.y), false);
							// ,"temp_ground.png");//lastObj.end.x +
							// GroundBuilder.UNIT_LENGTH

							Fixture fixture = drawEdge(newObj.getStart(),
									newObj.getEnd());
							newObj.setFixture(fixture);
							mapList.add((int) (point.x / 2), newObj);
							
						}

					}
				} else {

				}

			}
		}

	}

	public void destroy() {
		if (floor != null)
			world.destroyBody(floor);

	}
}

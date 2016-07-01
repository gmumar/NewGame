package GroundWorks;

import Shader.GameMesh;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class GroundUnitDescriptor {
	private Vector2 start, end;
	private Fixture fixture;
	private boolean fixtureDeleted = true;

	// ShapeRenderer renderer = new ShapeRenderer();
	private int vertexId = 0;

	public GroundUnitDescriptor(Vector2 start, Vector2 end, boolean initFiller) {
		this.start = start;
		this.end = end;

		// initGraphic();
		if (initFiller) {
			// initGraphicFiller();
			vertexId = GameMesh.addPoint(start.x, start.y);
		}
	}

	public int getVertexId() {
		return vertexId;
	}

	public Vector2 getStart() {
		return start;
	}

	public Vector2 getEnd() {
		return end;
	}

	public void deleteUnit(Body floor) {
		floor.destroyFixture(fixture);
		fixtureDeleted = true;
		// if (graphic != null) {
		// texture.dispose();
		// graphic = null;
		// texture = null;
		// }
	}

	public void setFixture(Fixture drawEdge) {
		this.fixture = drawEdge;
		fixtureDeleted = false;
	}

	public boolean isFixtureDeleted() {
		return fixtureDeleted;
	}

	@Override
	public boolean equals(Object obj) {
		GroundUnitDescriptor other = (GroundUnitDescriptor) obj;

		return other.end.x == this.end.x;
	}

	public void match(Vector2 vector2) {
		this.start = vector2;
	}

}
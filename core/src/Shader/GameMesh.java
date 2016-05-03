package Shader;

import java.util.ArrayList;

import wrapper.CameraManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

class MeshDescriptor {
	int start = -1, end = -1;
	int index = 0, i = 0;

	float[] vertices = new float[1236];

	// short[] indices = new short[0];

	public int getStart() {
		return start == -1 ? 0 : start;
	}

	public int getEnd() {
		return end == -1 ? 0 : end;
	}
};

public class GameMesh {

	private static final int LAYER_COUNT = 4;

	private static Mesh mesh;
	private static MeshDescriptor layer;
	// private static MeshDescriptor retGlobal;

	private static MeshDescriptor retArray[];// = new
												// MeshDescriptor[LAYER_COUNT];

	// Position attribute - (x, y)
	public static final int POSITION_COMPONENTS = 2;

	// Color attribute - (r, g, b, a)
	public static final int COLOR_COMPONENTS = 4;

	// Total number of components for all attributes
	public static final int NUM_COMPONENTS = POSITION_COMPONENTS
			+ COLOR_COMPONENTS;

	// The "size" (total number of floats) for a single triangle
	public static final int PRIMITIVE_SIZE = 3 * NUM_COMPONENTS;

	// The maximum number of triangles our mesh will hold
	public static final int MAX_TRIS = 1;

	// The maximum number of vertices our mesh will hold
	public static final int MAX_VERTS = 1500;// MAX_TRIS * 3;

	// The array which holds all the data, interleaved like so:
	// x, y, r, g, b, a
	// x, y, r, g, b, a,
	// x, y, r, g, b, a,
	// ... etc ...
	// static protected float[] verts = new float[MAX_VERTS * NUM_COMPONENTS];

	static protected ArrayList<Float> vectorVerts = new ArrayList<Float>();

	// The current index that we are pushing triangles into the array
	static protected int idx = 0;

	static private int prevVectexCount = -1;
	private static short[] solvedIndices;

	// vertice floats
	static float pointX,  pointY , colorr , colorg , colorb , colora , pointXt , pointYt;
	
	static public void create(CameraManager cam, ShaderProgram shader) {
		initArrays();
		mesh = new Mesh(false, MAX_VERTS, MAX_VERTS, new VertexAttribute(
				Usage.Position, POSITION_COMPONENTS, "a_position"),
				new VertexAttribute(Usage.ColorUnpacked, COLOR_COMPONENTS,
						"a_color"), new VertexAttribute(
						Usage.TextureCoordinates, 2, "a_texCoord0"));

		// for(int i=0;i<150;i++) addPoint(0,0);
		// addTriangle(0, 0, 0, 0, 0, 0, Globals.BLUE);
		retArray = new MeshDescriptor[LAYER_COUNT];
		for (int i = 0; i < LAYER_COUNT; i++) {
			retArray[i] = new MeshDescriptor();
		}

	}

	public static void initArrays() {
		// if(verts==null)
		// verts = new float[MAX_VERTS * NUM_COMPONENTS];
		if (vectorVerts == null)
			vectorVerts = new ArrayList<Float>();

	}

	static public int addPoint(float x, float y) {
		// we don't want to hit any index out of bounds exception...
		// so we need to flush the batch if we can't store any more verts
		// if (idx==verts.length)
		// flush(cam, shader);

		initArrays();
		// now we push the vertex data into our array
		// we are assuming (0, 0) is lower left, and Y is up

		// bottom left vertex
		vectorVerts.add(x); // Position(x, y)
		vectorVerts.add(y);
		vectorVerts.add(0.0f);
		vectorVerts.add(0.0f);
		vectorVerts.add(0.0f);
		vectorVerts.add(0.0f);
		vectorVerts.add(x); // Position(x, y)
		vectorVerts.add(y);

		return vectorVerts.size();
	}

	static public void destroy() {
		mesh.dispose();
		vectorVerts = null;

		retArray = null;

		// verts = null;
	}

	static final private MeshDescriptor setUpLayerFast(int start, int end,
			float depth, boolean drawTexture, Color color, float offset,
			int meshIndex) {

		boolean same = true;

		final MeshDescriptor ret = retArray[meshIndex];

		if (ret.end != end) {
			ret.end = end;
			same = false;
		}

		if (ret.start != start) {
			same = false;
		}

		if (same) {
			return ret;
		}

		// int vertexCount = (end - start) * 2 ;

		// Declare new array with new size
		// Array size fixed to MAX
		// if(ret.vertices.length != vertexCount){
		// ret.vertices = new float[vertexCount];
		// }

		final int startDiff = (start - ret.getStart())*2;

		// Copy over from over array
		System.arraycopy(ret.vertices, startDiff , ret.vertices, 0,
				ret.vertices.length - startDiff );

		
		ret.start = start;

		int index = ret.index - startDiff ;// 0
		final int vertexLen = ret.vertices.length - 16;

		// System.out.println("vertices len: " + ret.vertices.length);

		colorr = color.r;
		colorg = color.g;
		colorb = color.b;
		colora = drawTexture ? color.a : 0.0f;
		
		// sends our vertex data to the mesh
		for (int i = ret.i; i < end;) {

			pointX = vectorVerts.get(i++);
			pointY = vectorVerts.get(i++);
			//colorr = color.r;
			//i++;
			//colorg = color.g;
			//i++;
			//colorb = color.b;
			//i++;
			//colora = drawTexture ? color.a : 0.0f;
			i+=4;
			pointXt = vectorVerts.get(i++);
			pointYt = vectorVerts.get(i++);
			
			if(index > vertexLen) break;

			ret.vertices[index++] = pointX;
			ret.vertices[index++] = pointY + offset;
			ret.vertices[index++] = colorr;
			ret.vertices[index++] = colorg;
			ret.vertices[index++] = colorb;
			ret.vertices[index++] = colora;
			ret.vertices[index++] = pointXt;
			ret.vertices[index++] = pointYt + offset;

			ret.vertices[index++] = pointX;
			ret.vertices[index++] = pointY - depth + offset;
			ret.vertices[index++] = colorr;
			ret.vertices[index++] = colorg;
			ret.vertices[index++] = colorb;
			ret.vertices[index++] = colora;
			ret.vertices[index++] = pointXt;
			ret.vertices[index++] = pointYt - depth / 2 + offset;

			ret.i = i;
		}

		ret.index = index;
		
		//retArray[meshIndex] = ret;

		return ret;
	}

	/*
	 * static private MeshDescriptor setUpLayer(int start, int end, float depth,
	 * boolean drawTexture, Color color, float offset) { retGlobal = new
	 * MeshDescriptor(); int vertexCount = (end - start) * 2;
	 * 
	 * retGlobal.vertices = new float[vertexCount]; int index = 0; // sends our
	 * vertex data to the mesh for (int i = start; i < end;) { // int index = i
	 * - start; float pointX = vectorVerts.get(i++); float pointY =
	 * vectorVerts.get(i++); float colorr = color.r; i++; float colorg =
	 * color.g; i++; float colorb = color.b; i++; float colora = drawTexture ?
	 * color.a : 0.0f; i++; float pointXt = vectorVerts.get(i++); float pointYt
	 * = vectorVerts.get(i++);
	 * 
	 * retGlobal.vertices[index++] = pointX; retGlobal.vertices[index++] =
	 * pointY + offset; retGlobal.vertices[index++] = colorr;
	 * retGlobal.vertices[index++] = colorg; retGlobal.vertices[index++] =
	 * colorb; retGlobal.vertices[index++] = colora; retGlobal.vertices[index++]
	 * = pointXt; retGlobal.vertices[index++] = pointYt + offset;
	 * 
	 * retGlobal.vertices[index++] = pointX; retGlobal.vertices[index++] =
	 * pointY - depth + offset; retGlobal.vertices[index++] = colorr;
	 * retGlobal.vertices[index++] = colorg; retGlobal.vertices[index++] =
	 * colorb; retGlobal.vertices[index++] = colora; retGlobal.vertices[index++]
	 * = pointXt; retGlobal.vertices[index++] = pointYt - depth / 2 + offset; //
	 * System.out.println("added " + tmpVerts[i - start]); }
	 * 
	 * retGlobal.indices = getIndices(vertexCount);
	 * 
	 * return retGlobal; }
	 */

	private static short[] getIndices(int vertexCount) {
		
		prevVectexCount = vertexCount;
		//int indicesLength = vertexCount ;
		solvedIndices = new short[vertexCount];
		int vertex = 0;
		int index;
		// sends our vertex data to the mesh
		// for (int i = start; i < end; ++i) {
		// index = i - start;
		for (int i = 0; i < vertexCount; i++) {
			index = i;

			solvedIndices[index] = (short) (vertex);
			if (index + 1 >= vertexCount)
				break;
			solvedIndices[index + 1] = (short) (vertex + 1);
			if (index + 2 >= vertexCount)
				break;
			solvedIndices[index + 2] = (short) (vertex + 2);
			if (index + 3 >= vertexCount)
				break;

			vertex += 1;
			i += 8;
			// System.out.println("added " + tmpVerts[i - start]);
		}

		return solvedIndices;

	}

	/*
	 * cam: camra used to draw at
	 * shader: shader program to use
	 * start: start point of the vertices to draw
	 * end: end point of the vertices to draw
	 * depth: the height of the layer
	 * color: the color of the layer
	 * offset: distance offset from the actual vertex
	 * meshindex: the layer number, this is used to lookup the same layer
	 * in repeated calls
	 * vertexCount: end - start
	 * 
	 */
	static public final void drawLayer(CameraManager cam, ShaderProgram shader,
			int start, int end, Texture texture, float depth, Color color,
			float offset, int meshIndex, int vertexCount) {

		// layer = setUpLayer(start, end, depth, texture==null?true:false,
		// color, offset);

		layer = setUpLayerFast(start, end, depth, texture == null ? true
				: false, color, offset, meshIndex);

		mesh.setVertices(layer.vertices);

		if (prevVectexCount != vertexCount) {
			mesh.setIndices(getIndices(vertexCount));// (layer.indices);
		}

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		

			// update the projection matrix so our triangles are rendered in 2D
			//shader.setUniformMatrix("u_projTrans", cam.combined);
			// render the mesh
			//mesh.render(shader, GL20.GL_TRIANGLES);

		if (texture != null) {
			// no need for depth...
			// Gdx.gl.glDepthMask(false);

			// enable blending, for alpha
			Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);

			// update the camera with our Y-up coordiantes
			// cam.setToOrtho(false, Gdx.graphics.getWidth(),
			// Gdx.graphics.getHeight());

			// start the shader before setting any uniforms
			texture.setWrap(Texture.TextureWrap.Repeat,
					Texture.TextureWrap.Repeat);
			texture.bind(0);

			// update the projection matrix so our triangles are rendered in 2D
			//shader.setUniformMatrix("u_projTrans", cam.combined);

			

			// render the mesh
			

			// re-enable depth to reset states to their default
			// Gdx.gl.glDepthMask(true);
		}
		
		mesh.render(shader, GL20.GL_TRIANGLES);

	}

}

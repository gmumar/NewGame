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
	float[] vertices;
	short[] indices;
};

public class GameMesh {
	static Mesh mesh;

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
	public static final int MAX_VERTS = 5000;//MAX_TRIS * 3;

	// The array which holds all the data, interleaved like so:
	// x, y, r, g, b, a
	// x, y, r, g, b, a,
	// x, y, r, g, b, a,
	// ... etc ...
	//static protected float[] verts = new float[MAX_VERTS * NUM_COMPONENTS];

	static protected ArrayList<Float> vectorVerts = new ArrayList<Float>();

	// The current index that we are pushing triangles into the array
	static protected int idx = 0;

	static public void create(CameraManager cam, ShaderProgram shader) {
		initArrays();
		Gdx.gl.glClear(GL20.GL_ATTACHED_SHADERS);
		mesh = new Mesh(false, MAX_VERTS, MAX_VERTS, 
				new VertexAttribute(Usage.Position,	POSITION_COMPONENTS, "a_position"), 
				new VertexAttribute(Usage.ColorUnpacked, COLOR_COMPONENTS, "a_color"), 
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0"));
		
		
		//for(int i=0;i<10;i++)
		//addTriangle(0, 0, 0, 0, 0, 0, Globals.BLUE);

	}
	
	public static void initArrays(){
		//if(verts==null)
			//verts = new float[MAX_VERTS * NUM_COMPONENTS];
		if(vectorVerts==null)
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
	
	static public void destroy(){
		mesh.dispose();
		vectorVerts = null;
		//verts = null;
	}
	
	
	static private MeshDescriptor setUpLayer(int start, int end, float depth, boolean drawTexture, Color color, float offset){
		MeshDescriptor ret = new MeshDescriptor();
		int vertexCount = (end - start)*2;
		
		ret.vertices  = new float[vertexCount];
		int index = 0;
		// sends our vertex data to the mesh
		for (int i = start; i < end;) {
			//int index = i - start;
			float pointX = vectorVerts.get(i++);
			float pointY = vectorVerts.get(i++);
			float colorr = color.r;
			i++;
			float colorg = color.g;
			i++;
			float colorb = color.b;
			i++;
			float colora = drawTexture ? color.a : 0.0f;
			i++;
			float pointXt = vectorVerts.get(i++);
			float pointYt = vectorVerts.get(i++);
			
			ret.vertices[index++] = pointX;
			ret.vertices[index++] = pointY+offset;
			ret.vertices[index++] = colorr;
			ret.vertices[index++] = colorg;
			ret.vertices[index++] = colorb;
			ret.vertices[index++] = colora;
			ret.vertices[index++] = pointXt;
			ret.vertices[index++] = pointYt+offset;
		
			ret.vertices[index++] = pointX;
			ret.vertices[index++] = pointY-depth + offset;
			ret.vertices[index++] = colorr;
			ret.vertices[index++] = colorg;
			ret.vertices[index++] = colorb;
			ret.vertices[index++] = colora;
			ret.vertices[index++] = pointXt;
			ret.vertices[index++] = pointYt-depth/2+offset;
			//System.out.println("added " + tmpVerts[i - start]);
		}
		
		int indicesLength = vertexCount+1;
		ret.indices = new short[indicesLength];
		int  vertex = 0;
		// sends our vertex data to the mesh
		//for (int i = start; i < end; ++i) {
		//	index = i - start;
		for (int i = 0; i < indicesLength; i++) {
			index = i;
			
			ret.indices[index] = (short) (vertex);
			if(index+1>=indicesLength) break;
			ret.indices[index+1] = (short) (vertex+1);
			if(index+2>=indicesLength) break;
			ret.indices[index+2] = (short) (vertex+2);
			if(index+3>=indicesLength) break;
	
			vertex+=1;
			i+=8;
			//System.out.println("added " + tmpVerts[i - start]);
		}
		
		return ret;
	}


	static public void flush(CameraManager cam, ShaderProgram shader,
			int start, int end, Texture texture, float depth, Color color, float offset) {
		
		MeshDescriptor layer = setUpLayer(start, end, depth, texture==null?true:false, color, offset);
		
		mesh.setVertices(layer.vertices);
		mesh.setIndices(layer.indices);

		if(texture == null){
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			// update the projection matrix so our triangles are rendered in 2D
			shader.setUniformMatrix("u_projTrans", cam.combined);
			// render the mesh
			mesh.render(shader, GL20.GL_TRIANGLES);
	
		}else{
			// no need for depth...
			//Gdx.gl.glDepthMask(false);
	
			// enable blending, for alpha
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	
			// update the camera with our Y-up coordiantes
			// cam.setToOrtho(false, Gdx.graphics.getWidth(),
			// Gdx.graphics.getHeight());
	
			// start the shader before setting any uniforms
			texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			texture.bind(0);
			
			// update the projection matrix so our triangles are rendered in 2D
			shader.setUniformMatrix("u_projTrans", cam.combined);
			
			shader.setUniformi("u_texture", 0);
	
			// render the mesh
			mesh.render(shader, GL20.GL_TRIANGLES);
	
	
			// re-enable depth to reset states to their default
			//Gdx.gl.glDepthMask(true);
		}

	}

}

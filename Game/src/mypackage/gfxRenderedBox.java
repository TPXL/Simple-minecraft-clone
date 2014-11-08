package mypackage;
import glmodel.GLImage;
import glmodel.GLMaterialLib;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class gfxRenderedBox {
	private FloatBuffer vertexData;
	private FloatBuffer textureData;
	private FloatBuffer normalData;
	private IntBuffer indexData;
	
	final public static float[] front = 
		{
		-0.5f, -0.5f, -0.5f,
		-0.5f,  0.5f, -0.5f,
		 0.5f,  0.5f, -0.5f,
		 0.5f, -0.5f, -0.5f,
		};
	final public static float[] bottom = 
		{
		 0.5f, -0.5f, -0.5f,
		 0.5f, -0.5f,  0.5f,
		-0.5f, -0.5f,  0.5f,
		-0.5f, -0.5f, -0.5f,
		};	
	final public static float[] top = 
		{
		-0.5f,  0.5f, -0.5f,
		-0.5f,  0.5f,  0.5f,	
		 0.5f,  0.5f,  0.5f,
		 0.5f,  0.5f, -0.5f,
		};
	final public static float[] back = 
		{
		 0.5f,  0.5f,  0.5f,
		-0.5f,  0.5f,  0.5f,
		-0.5f, -0.5f,  0.5f,
		 0.5f, -0.5f,  0.5f,
		};
	final public static float[] right = 
		{
		 0.5f, -0.5f, -0.5f,
		 0.5f,  0.5f, -0.5f,
		 0.5f,  0.5f,  0.5f,
		 0.5f, -0.5f,  0.5f,
		};
	final public static float[] left = 
		{
		-0.5f,  0.5f,  0.5f,
		-0.5f,  0.5f, -0.5f,
		-0.5f, -0.5f, -0.5f,
		-0.5f, -0.5f,  0.5f,
		};
	
	private static float vertices[] = new float[]{
		
		-0.5f, -0.5f, -0.5f,
		-0.5f,  0.5f, -0.5f,
		 0.5f,  0.5f, -0.5f,
		 0.5f, -0.5f, -0.5f,
		
		 0.5f, -0.5f, -0.5f,
		 0.5f, -0.5f,  0.5f,
		-0.5f, -0.5f,  0.5f,
		-0.5f, -0.5f, -0.5f,
		
		-0.5f,  0.5f, -0.5f,
		-0.5f,  0.5f,  0.5f,	
		 0.5f,  0.5f,  0.5f,
		 0.5f,  0.5f, -0.5f,
		
		 0.5f,  0.5f,  0.5f,
		-0.5f,  0.5f,  0.5f,
		-0.5f, -0.5f,  0.5f,
		 0.5f, -0.5f,  0.5f,
		
		 0.5f, -0.5f, -0.5f,
		 0.5f,  0.5f, -0.5f,
		 0.5f,  0.5f,  0.5f,
		 0.5f, -0.5f,  0.5f,
		
		-0.5f,  0.5f,  0.5f,
		-0.5f,  0.5f, -0.5f,
		-0.5f, -0.5f, -0.5f,
		-0.5f, -0.5f,  0.5f,
	
	};
	
	final public static float normals[] = new float[]
	{
		0f, 0f, -1f,
		0f, 0f, -1f,
		0f, 0f, -1f,
		0f, 0f, -1f,
		
		0f, -1f, 0f,
		0f, -1f, 0f,
		0f, -1f, 0f,
		0f, -1f, 0f,

		0f, 1f, 0f,
		0f, 1f, 0f,
		0f, 1f, 0f,
		0f, 1f, 0f,

		0f, 0f, 1f,
		0f, 0f, 1f,
		0f, 0f, 1f,
		0f, 0f, 1f,
		
		1f, 0f, 0f,
		1f, 0f, 0f,
		1f, 0f, 0f,
		1f, 0f, 0f,

		-1f, 0f, 0f,
		-1f, 0f, 0f,
		-1f, 0f, 0f,
		-1f, 0f, 0f,
	};
	
	final public static float tangents[] = new float[]
	{
		1, 0, 0,
		1, 0, 0,
		1, 0, 0,
		1, 0, 0,
		
		1, 0, 0,
		1, 0, 0,
		1, 0, 0,
		1, 0, 0,

		0, 0, -1,
		0, 0, -1,
		0, 0, -1,
		0, 0, -1,

		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,

		0, 0, 1,
		0, 0, 1,
		0, 0, 1,
		0, 0, 1,

		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		0, 1, 0
	};
	
	final public static float bitangents[] = new float[]
	{
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,

		0, 0, -1,
		0, 0, -1,
		0, 0, -1,
		0, 0, -1,

		1, 0, 0,
		1, 0, 0,
		1, 0, 0,
		1, 0, 0,

		1, 0, 0,
		1, 0, 0,
		1, 0, 0,
		1, 0, 0,

		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,

		0, 0, 1,
		0, 0, 1,
		0, 0, 1,
		0, 0, 1
	};
	
	
	private static float texv[] = {
			0.000000f,
			0.333333f,
			0.666667f,
			1.000000f,	
	};
	final public static float texcoords[]= new float[]{	
		texv[0], texv[2], texv[0], texv[3], texv[1], texv[3], texv[1], texv[2], //enka - zadnji del
		texv[1], texv[2], texv[1], texv[3], texv[2], texv[3], texv[2], texv[2], //dvojka - spodnji del
		texv[2], texv[2], texv[2], texv[3], texv[3], texv[3], texv[3], texv[2], //trojka - zgornji del 
		texv[0], texv[1], texv[0], texv[2], texv[1], texv[2], texv[1], texv[1], //stirka - sprednji del
		texv[1], texv[1], texv[1], texv[2], texv[2], texv[2], texv[2], texv[1], //petka - desni del
		texv[2], texv[1], texv[2], texv[2], texv[3], texv[2], texv[3], texv[1], //sestka - levi del
	};
	
	
	
	final public static int indices[] = new int[] {
		0, 1, 2, 3,
		4, 5, 6, 7,
		8, 9, 10, 11,
		12, 13, 14, 15,
		16, 17, 18, 19,
		20, 21, 22, 23	
	}; 

	public GLImage tex;
	public GLImage specMap;
	
	int vertexBufferID;
	int indicesBufferID;
	int textureBufferID;
	int normalBufferID;
	
	static HashMap<String, GLImage> textureMap = new HashMap<String, GLImage>();
	String texname;
	
	void remove()
	{
		tex = null;
		vertexData.clear();
		vertexData = null;
		textureData.clear();
		textureData = null;
		indexData.clear();
		indexData = null;
		texname = "";
	}
	
	public static void addTexture(String textureFilename)
	{
		if(textureMap.containsKey(textureFilename))
		{
			return;
		}
		GLImage tex = new GLImage();
		tex.textureHandle = GLMaterialLib.makeTexture(textureFilename);
		textureMap.put(textureFilename, tex);

	}
	float scale;
	public gfxRenderedBox(String textureFilename, float scale)
	{
		tex = textureMap.get(textureFilename);
		if(tex == null)
		{
			tex = new GLImage();
			tex.textureHandle = GLMaterialLib.makeTexture(textureFilename);
			textureMap.put(textureFilename, tex);
		}
		this.scale = scale;
		texname = textureFilename;
		init();
	}
	
	int nelements;
		
	private void init()
	{
		
	    vertexData = BufferUtils.createFloatBuffer(vertices.length);
	    for(int i=0; i < vertices.length; i++)
	    	vertexData.put(vertices[i] * scale);
	    vertexData.rewind();
	
	    indexData = BufferUtils.createIntBuffer(indices.length);
	    indexData.put(indices);
	    indexData.rewind();
	    
	    textureData = BufferUtils.createFloatBuffer(texcoords.length);
	    textureData.put(texcoords);
	    textureData.rewind();
	
	    normalData = BufferUtils.createFloatBuffer(normals.length);
	    normalData.put(normals);
	    normalData.rewind();
	    
	    vertexBufferID = GL15.glGenBuffers();
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferID);
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	
	    indicesBufferID = GL15.glGenBuffers();
	    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
	    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexData, GL15.GL_STATIC_DRAW);
	    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	
	    textureBufferID = GL15.glGenBuffers();
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureBufferID);
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureData, GL15.GL_STATIC_DRAW);
	    
	    normalBufferID = GL15.glGenBuffers();
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBufferID);
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalData, GL15.GL_STATIC_DRAW);
	    
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	    nelements = indices.length;
	}
	
	void render()
	{
		//Since rendering is done in a bulk, can move most of this out of the function and only do it once
		/*
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferID);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0L);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
        
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.textureHandle);
    		        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureBufferID);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
*/
        GL11.glDrawElements(GL11.GL_QUADS, indices.length, GL11.GL_UNSIGNED_INT, 0);
	}
}

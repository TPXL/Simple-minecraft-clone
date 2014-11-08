package mypackage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glVertexPointer;


public class gfxCardPoints {
	/*
	private FloatBuffer pointBuffer;
	private IntBuffer indicesBuffer;
	
	private float points[];
	private float colors[];
	private int indices[];
	
	
	int pointBufferID;
	int indicesBufferID;
	int colorBufferID;
	
	int x, y;
	
	public gfxCardPoints(Box [][] boxlist)
	{
		//Random seed = new Random(123456);
		x = boxlist.length;
		y = boxlist[0].length;
		points = new float[x * y * 8 *3];
		indices = new int[x * y * 24];
		colors = new float[x * y * 4 * 8];
		System.out.println("400*400*24 array created! :D" + " " + points.length +  " " + indices.length);
		for(int i=0; i < x; i++)
			for(int j=0; j < y; j++)
			{
				int ind = (i * y + j)*8*3;
				//int indc = (i*y+j) * 4*8;
				//System.out.println(i + " " + j);
				//System.out.println(ind);
				//8 points of a box
				
				points[ind] = boxlist[i][j].m_nX-0.5f;//x 
				points[ind+1] = boxlist[i][j].m_nY-0.5f;//z
				points[ind+2] = boxlist[i][j].m_nZ-0.5f;//y
				
				points[ind+3] = boxlist[i][j].m_nX-0.5f;//x 
				points[ind+4] = boxlist[i][j].m_nY+0.5f;//z
				points[ind+5] = boxlist[i][j].m_nZ-0.5f;//y
				
				points[ind+6] = boxlist[i][j].m_nX+0.5f;//x 
				points[ind+7] = boxlist[i][j].m_nY-0.5f;//z
				points[ind+8] = boxlist[i][j].m_nZ-0.5f;//y
				
				points[ind+9] = boxlist[i][j].m_nX+0.5f;//x 
				points[ind+10] = boxlist[i][j].m_nY+0.5f;//z
				points[ind+11] = boxlist[i][j].m_nZ-0.5f;//y

				points[ind+12] = boxlist[i][j].m_nX-0.5f;//x 
				points[ind+13] = boxlist[i][j].m_nY-0.5f;//z
				points[ind+14] = boxlist[i][j].m_nZ+0.5f;//y
				
				points[ind+15] = boxlist[i][j].m_nX-0.5f;//x 
				points[ind+16] = boxlist[i][j].m_nY+0.5f;//z
				points[ind+17] = boxlist[i][j].m_nZ+0.5f;//y
				
				points[ind+18] = boxlist[i][j].m_nX+0.5f;//x 
				points[ind+19] = boxlist[i][j].m_nY-0.5f;//z
				points[ind+20] = boxlist[i][j].m_nZ+0.5f;//y
				
				points[ind+21] = boxlist[i][j].m_nX+0.5f;//x 
				points[ind+22] = boxlist[i][j].m_nY+0.5f;//z
				points[ind+23] = boxlist[i][j].m_nZ+0.5f;//y
			
				//6 sides in a box
				
				indices[ind+0] = (int)ind/3+0;
				indices[ind+1] = (int)ind/3+1;
				indices[ind+2] = (int)ind/3+3;
				indices[ind+3] = (int)ind/3+2;

				indices[ind+4] = (int)ind/3+2;
				indices[ind+5] = (int)ind/3+6;
				indices[ind+6] = (int)ind/3+4;
				indices[ind+7] = (int)ind/3+0;
				
				indices[ind+8] = (int)ind/3+1;
				indices[ind+9] = (int)ind/3+5;
				indices[ind+10] = (int)ind/3+7;
				indices[ind+11] = (int)ind/3+3;
				
				indices[ind+12] = (int)ind/3+7;
				indices[ind+13] = (int)ind/3+5;
				indices[ind+14] = (int)ind/3+4;
				indices[ind+15] = (int)ind/3+6;
				
				indices[ind+16] = (int)ind/3+2;
				indices[ind+17] = (int)ind/3+3;
				indices[ind+18] = (int)ind/3+7;
				indices[ind+19] = (int)ind/3+6;
				
				indices[ind+20] = (int)ind/3+5;
				indices[ind+21] = (int)ind/3+1;
				indices[ind+22] = (int)ind/3+0;
				indices[ind+23] = (int)ind/3+4;
				/*
				for(int k =0; k < 8; k++)
				{
					colors[indc+k*4+0] = boxlist[i][j].color[0] + (seed.nextFloat()-0.5f)*0.1f;
					colors[indc+k*4+1] = boxlist[i][j].color[1] + (seed.nextFloat()-0.5f)*0.1f;
					colors[indc+k*4+2] = boxlist[i][j].color[2] + (seed.nextFloat()-0.5f)*0.1f;
					colors[indc+k*4+3] = 1.f;
				}
			}
		init();
	}
	
	
	private void init()
	{
		pointBufferID = GL15.glGenBuffers();
		indicesBufferID = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pointBufferID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		//glBindBuffer(GL_ARRAY_BUFFER_, colorBufferID);
		
		pointBuffer = ByteBuffer.allocateDirect(points.length*4 + colors.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		pointBuffer.put(points);
		pointBuffer.put(colors);
		pointBuffer.rewind();
		
		indicesBuffer = ByteBuffer.allocateDirect(indices.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.rewind();
		
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, pointBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

		glEnableClientState(GL_VERTEX_ARRAY);
		glVertexPointer(3, GL_FLOAT, 0, 0);
		
		glEnableClientState(GL11.GL_COLOR_ARRAY);
		glColorPointer(4, GL_FLOAT, 0, points.length*4);
		
		GL11.glDisableClientState(GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
	}
	
	void render()
	{
		GL11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);
		GL11.glPushMatrix();
		//GL11.glTranslatef(x/4f, y/4f, 0f);
		//GL11.glTranslatef(-x/2f, -y/2f, 0f);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pointBufferID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		glEnableClientState(GL_VERTEX_ARRAY);
		glVertexPointer(3, GL_FLOAT, 0, 0);

		glEnableClientState(GL11.GL_COLOR_ARRAY);
		glColorPointer(4, GL_FLOAT, 0, points.length*4);
		glDrawElements(GL11.GL_QUADS, indices.length, GL_UNSIGNED_INT, 0);
		
		
		GL11.glDisableClientState(GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glPopMatrix();
	}*/
}

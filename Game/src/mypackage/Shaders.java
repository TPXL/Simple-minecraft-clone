package mypackage;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;













import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.MatrixUtil;

public class Shaders {

	static final float pioveroneeighty = (float)Math.PI / 180.f;
	
	static int drawShader;
	static int shadowShader;
	static int waterShader;

	static int vertexArrayID; //Dafuq is this and what is it doing here?
	
	static int framebufferName;
	static int depthTextureHandle = -3;		//globinaska teksturea
	static int depthTextureID = -3;			//depth map sampler

	static int projectionFramebufferName;
	static int projectionDepthTextureHandle = -3;	//depth texture
	static int projectionTextureID = -3;		//address of projection texture in shader
	static int projectionDepthTextureID = -3;	//address of projection depth texture in shader
	
	static int specularMapID = -3;
	static int normalMapID = -3;
	
	
	
	//variables in shaders
	static int depthMatrixID = -3;
	static int depthTimeID = -3;
	
	static int matrixID = -3;
	static int viewMatrixID = -3;
	static int modelMatrixID = -3; 
	static int depthBiasID = -3;
	static int projectorBiasID = -3;
	static int lightInvDirID = -3;
	static int drawTextureID = -3;
	static int timeID = -3;
	
	
	//water variables
	static int waterMatrixID = -3;
	static int waterViewMatrixID = -3;
	static int waterModelMatrixID = -3; 
	static int waterShadowmapID = -3;
	static int waterDepthBiasID = -3;
	static int waterLightInvDirID = -3;
	static int waterDrawTextureID = -3;
	static int waterSpecularMapID = -3;
	static int waterTimeID = -3;
	
	//matrices in buffers
	static FloatBuffer projectionMVPBuffer;
	static FloatBuffer projectorBiasBuffer;
	static FloatBuffer depthMVPBuffer;
	static FloatBuffer dynamicDepthMVPBuffer;
	static FloatBuffer depthBiasBuffer;
	static FloatBuffer MVPBuffer;
	static FloatBuffer modelMatrixBuffer;
	static FloatBuffer viewMatrixBuffer;
	static FloatBuffer dynamicMVPBuffer;
	static FloatBuffer dynamicModelBuffer;
	static FloatBuffer dynamicViewBuffer;
	static FloatBuffer dynamicDepthBiasBuffer;
	
	//lightposition
	static float lightX = 100;
	static float lightY = 100;
	static float lightZ = 75;
	
	static int shadowmapSize = 4096;
	
	static int projectionmapSize = 2048;
	
	public static void init()
	{
		shadowShader = LoadShaders("shaders/DepthRTT.vertexshader", "shaders/DepthRTT.fragmentshader");
		drawShader = LoadShaders("shaders/ShadowMapping.vertexshader", "shaders/ShadowMapping.fragmentshader");
		waterShader = LoadShaders("shaders/Water.vertexshader", "shaders/Water.fragmentshader");
		
		depthMatrixID = GL20.glGetUniformLocation(shadowShader, "depthMVP");
		depthTimeID = GL20.glGetUniformLocation(shadowShader, "time");
		
		matrixID = GL20.glGetUniformLocation(drawShader, "MVP");
		viewMatrixID = GL20.glGetUniformLocation(drawShader, "V");
		modelMatrixID = GL20.glGetUniformLocation(drawShader, "M");
		depthTextureID = GL20.glGetUniformLocation(drawShader, "shadowMap");
		depthBiasID = GL20.glGetUniformLocation(drawShader, "DepthBiasMVP");
		projectorBiasID = GL20.glGetUniformLocation(drawShader, "ProjectorBiasMVP");
		lightInvDirID = GL20.glGetUniformLocation(drawShader, "LightInvDirection_worldspace");
		drawTextureID = GL20.glGetUniformLocation(drawShader, "myTextureSampler");
		specularMapID = GL20.glGetUniformLocation(drawShader, "mySpecularSampler");
		normalMapID = GL20.glGetUniformLocation(drawShader, "myNormalSampler");
		projectionTextureID = GL20.glGetUniformLocation(drawShader, "myProjectionSampler");
		projectionDepthTextureID = GL20.glGetUniformLocation(drawShader, "projectionShadowMap");
		timeID = GL20.glGetUniformLocation(drawShader, "time");
		
		waterMatrixID = GL20.glGetUniformLocation(waterShader, "MVP");
		waterViewMatrixID = GL20.glGetUniformLocation(waterShader, "V");
		waterModelMatrixID = GL20.glGetUniformLocation(waterShader, "M");
		waterShadowmapID = GL20.glGetUniformLocation(waterShader, "shadowMap");
		waterDepthBiasID = GL20.glGetUniformLocation(waterShader, "DepthBiasMVP");
		waterLightInvDirID = GL20.glGetUniformLocation(waterShader, "LightInvDirection_worldspace");
		waterDrawTextureID = GL20.glGetUniformLocation(waterShader, "myTextureSampler");
		waterSpecularMapID = GL20.glGetUniformLocation(waterShader, "mySpecularSampler");
		waterTimeID = GL20.glGetUniformLocation(waterShader, "time");
		
		
		System.out.println("DMID " + depthMatrixID + 
				"\ndepthTimeID " + depthTimeID +
				
				"\nMID " + matrixID + 
				"\nVMID " + viewMatrixID + 
				"\nMMID " + modelMatrixID +
				"\nSID " + depthTextureID + 
				"\nPDTID " + projectionDepthTextureID +
				"\nPTID " + projectionTextureID + 
				"\nDBID " + depthBiasID + 
				"\nPDBID " + projectorBiasID +
				"\nLIDID " + lightInvDirID +
				"\nDTID " + drawTextureID +
				"\nspecularMapID " + specularMapID + 
				"\nnormalMapID " + normalMapID + 
				"\ntimeID " + timeID + 
				
				"\nwaterMID " + waterMatrixID +
				"\nwaterVMID " + waterViewMatrixID +
				"\nwaterMMID " + waterModelMatrixID +
				"\nwaterSID " + waterShadowmapID + 
				"\nwaterDBID" + waterDepthBiasID +
				"\nwaterLIDID " + waterLightInvDirID +
				"\nwaterDTID " + waterDrawTextureID + 
				"\nwaterSPecularMapID " + waterSpecularMapID +
				"\nwaterTimeID " + waterTimeID);
		 
		
		framebufferName = GL30.glGenFramebuffers();
		depthTextureHandle = GL11.glGenTextures();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureHandle);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, shadowmapSize, shadowmapSize, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer)null);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_LEQUAL);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferName);
		GL11.glDrawBuffer(GL11.GL_NONE);	//dont write colours
		GL11.glReadBuffer(GL11.GL_NONE);
		
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthTextureHandle, 0);
		
		if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
		{
			System.out.println("Couldn't init framebuffer");
			System.exit(0);
		}
		else
			System.out.println("Framebuffer complete! " + depthTextureHandle + " " + framebufferName);
		
		projectionFramebufferName = GL30.glGenFramebuffers();
		projectionDepthTextureHandle = GL11.glGenTextures();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, projectionDepthTextureHandle);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, projectionmapSize, projectionmapSize, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer)null);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);	
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_LEQUAL);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, projectionFramebufferName);
		GL11.glDrawBuffer(GL11.GL_NONE);	//dont write colours
		GL11.glReadBuffer(GL11.GL_NONE);
		
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER,  GL30.GL_DEPTH_ATTACHMENT, projectionDepthTextureHandle, 0);
		if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
		{
			System.out.println("Couldn't init framebuffer");
			System.exit(0);
		}
		else
			System.out.println("Projection framebuffer complete! " + projectionDepthTextureHandle + " " + projectionFramebufferName);
		
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		vertexArrayID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vertexArrayID);
		
		depthBiasBuffer = BufferUtils.createFloatBuffer(16);
		depthMVPBuffer = BufferUtils.createFloatBuffer(16);
		projectionMVPBuffer = BufferUtils.createFloatBuffer(16);
		projectorBiasBuffer = BufferUtils.createFloatBuffer(16);
		MVPBuffer = BufferUtils.createFloatBuffer(16);
		modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
		viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
		dynamicMVPBuffer = BufferUtils.createFloatBuffer(16);
		dynamicModelBuffer = BufferUtils.createFloatBuffer(16);
		dynamicViewBuffer = BufferUtils.createFloatBuffer(16);
		dynamicDepthMVPBuffer = BufferUtils.createFloatBuffer(16);
		dynamicDepthBiasBuffer = BufferUtils.createFloatBuffer(16);
	}
	
	public static void sendTime(float time)
	{
		GL20.glUseProgram(drawShader);
		GL20.glUniform1f(timeID, time);
		GL20.glUseProgram(shadowShader);
		GL20.glUniform1f(depthTimeID, time);
		GL20.glUseProgram(waterShader);
		GL20.glUniform1f(waterTimeID, time);
		GL20.glUseProgram(0);
		
		lightX = (float)(Math.cos(time/1000000f /300f * pioveroneeighty) * 100f);
		lightY = (float)(Math.sin(time/1000000f /300f * pioveroneeighty) * 100f);
		if(lightY < 0)
		{
			lightY = -lightY;
			lightX = -lightX;
		}
	}
	
	public static void clearShadowDepth()
	{
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferName);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, projectionFramebufferName);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
	}
	
	public static void renderProjectionDepth(int vertexBuffer, int elementBuffer, int count)
	{
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, projectionFramebufferName);
		GL11.glViewport(0, 0, projectionmapSize, projectionmapSize);
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		GL11.glCullFace(GL11.GL_FRONT);
		//GL11.glCullFace(GL11.GL_BACK);
		
		GL20.glUseProgram(shadowShader);
		GL20.glUniformMatrix4(depthMatrixID, false, projectionMVPBuffer);
		
		GL20.glEnableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer);

		GL11.glDrawElements(GL11.GL_QUADS, count, GL11.GL_UNSIGNED_INT, 0);

		GL20.glDisableVertexAttribArray(0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL20.glUseProgram(0);
	}
	
	public static void renderShadows(int vertexBuffer, int elementBuffer, int count)
	{
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferName);
		GL11.glViewport(0, 0, shadowmapSize, shadowmapSize);
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		GL11.glCullFace(GL11.GL_FRONT);
		//GL11.glCullFace(GL11.GL_BACK);
		
		GL20.glUseProgram(shadowShader);
		GL20.glUniformMatrix4(depthMatrixID, false, depthMVPBuffer);
		
		
		GL20.glEnableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer);

		GL11.glDrawElements(GL11.GL_QUADS, count, GL11.GL_UNSIGNED_INT, 0);

		GL20.glDisableVertexAttribArray(0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL20.glUseProgram(0);
	}
	
	
	public static void render(int vertexBuffer, int uvBuffer, int normalBuffer, int elementBuffer, int tangentBuffer, int bitangentBuffer, int textureHandle, int specularMapHandle, int normalMapHandle, int projectionTextureHandle, int elementCount)
	{
		GL11.glViewport(0,  0,  1024, 768);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		GL20.glUseProgram(drawShader);
		
		GL20.glUniformMatrix4(matrixID, false, MVPBuffer);
		GL20.glUniformMatrix4(modelMatrixID, false, modelMatrixBuffer);
		GL20.glUniformMatrix4(viewMatrixID, false, viewMatrixBuffer);
		GL20.glUniformMatrix4(depthBiasID, false, depthBiasBuffer);
		GL20.glUniformMatrix4(projectorBiasID, false, projectorBiasBuffer);
		GL20.glUniform3f(lightInvDirID, lightX,  lightY, lightZ);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
		GL20.glUniform1i(drawTextureID, 0);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureHandle);
		GL20.glUniform1i(depthTextureID, 1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, specularMapHandle);
		GL20.glUniform1i(specularMapID, 2);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMapHandle);
		GL20.glUniform1i(normalMapID, 3);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, projectionDepthTextureHandle);
		GL20.glUniform1i(projectionDepthTextureID, 4);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, projectionTextureHandle);
		GL20.glUniform1i(projectionTextureID, 5);
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvBuffer);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
	
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBuffer);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tangentBuffer);
		GL20.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bitangentBuffer);
		GL20.glVertexAttribPointer(4, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
		
		GL11.glDrawElements(GL11.GL_QUADS, elementCount, GL11.GL_UNSIGNED_INT, 0);

		GL20.glUseProgram(0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
	}
	
	public static void renderWater(int vertexBuffer, int uvBuffer, int normalBuffer, int elementBuffer, int textureHandle, int specularMapHandle, int elementCount)
	{
		GL11.glViewport(0,  0,  1024, 768);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL20.glUseProgram(waterShader);
		
		GL20.glUniformMatrix4(waterMatrixID, false, MVPBuffer);
		GL20.glUniformMatrix4(waterModelMatrixID, false, modelMatrixBuffer);
		GL20.glUniformMatrix4(waterViewMatrixID, false, viewMatrixBuffer);
		GL20.glUniformMatrix4(waterDepthBiasID, false, depthBiasBuffer);
		GL20.glUniform3f(waterLightInvDirID, lightX,  lightY, lightZ);

		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
		GL20.glUniform1i(waterDrawTextureID, 0);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureHandle);
		GL20.glUniform1i(waterShadowmapID, 1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, specularMapHandle);
		GL20.glUniform1i(waterSpecularMapID, 2);
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvBuffer);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
	
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBuffer);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
		
		GL11.glDrawElements(GL11.GL_QUADS, elementCount, GL11.GL_UNSIGNED_INT, 0);

		GL20.glUseProgram(0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void renderDynamic(int vertexBuffer, int uvBuffer, int normalBuffer, int elementBuffer, int textureHandle, int specularMapHandle, int elementCount, float translateX, float translateY, float translateZ)
	{
		Matrix4f M = new Matrix4f();
		Matrix4f V = new Matrix4f();
		Matrix4f MVP = new Matrix4f();
		MVPBuffer.rewind();
		modelMatrixBuffer.rewind();
		viewMatrixBuffer.rewind();
		for(int i=0; i < 4; i++)
			for(int j=0; j < 4; j++)
				MVP.setElement(j, i, MVPBuffer.get());
		for(int i=0; i < 4; i++)
			for(int j=0; j < 4; j++)
				M.setElement(j, i, modelMatrixBuffer.get());
		for(int i=0; i < 4; i++)
			for(int j=0; j < 4; j++)
				V.setElement(j, i, viewMatrixBuffer.get());
		
		Matrix4f translator = new Matrix4f();
		translator.setIdentity();
		translator.m03 = translateX;
		translator.m13 = translateY;
		translator.m23 = translateZ;
		
		M.mul(translator);
		MVP.mul(translator);
		V.mul(translator);
		
		dynamicMVPBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j < 4; j++)
				dynamicMVPBuffer.put(MVP.getElement(j, i));
		
		dynamicModelBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j < 4; j++)
				dynamicModelBuffer.put(M.getElement(j, i));
		
		dynamicViewBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j < 4; j++)
				dynamicViewBuffer.put(V.getElement(j, i));
		
		dynamicMVPBuffer.rewind();
		dynamicModelBuffer.rewind();
		dynamicViewBuffer.rewind();
		
		GL11.glViewport(0,  0,  1024, 768);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		GL20.glUseProgram(drawShader);
		
		GL20.glUniformMatrix4(matrixID, false, dynamicMVPBuffer);
		GL20.glUniformMatrix4(modelMatrixID, false, dynamicModelBuffer);
		GL20.glUniformMatrix4(viewMatrixID, false, dynamicViewBuffer);
		GL20.glUniformMatrix4(depthBiasID, false, dynamicDepthBiasBuffer);
		GL20.glUniform3f(lightInvDirID, lightX,  lightY, lightZ);

		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
		GL20.glUniform1i(drawTextureID, 0);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureHandle);
		GL20.glUniform1i(depthTextureID, 1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, specularMapHandle);
		GL20.glUniform1i(specularMapID, 2);
		
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvBuffer);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
	
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBuffer);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
		
		GL11.glDrawElements(GL11.GL_QUADS, elementCount, GL11.GL_UNSIGNED_INT, 0);

		GL20.glUseProgram(0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
	}
	
	public static void renderDynamicShadows(int vertexBuffer, int elementBuffer, int count, float translateX, float translateY, float translateZ)
	{
		Matrix4f depthMVP = new Matrix4f();
		depthMVPBuffer.rewind();
		for(int i=0; i < 4; i++)
			for(int j=0; j < 4; j++)
				depthMVP.setElement(j, i, depthMVPBuffer.get());
		depthMVPBuffer.rewind();
		
		Matrix4f translator = new Matrix4f();
		translator.setIdentity();
		translator.m03 = translateX;
		translator.m13 = translateY+0.08f;
		translator.m23 = translateZ;
		
		depthMVP.mul(translator);
		
		dynamicDepthMVPBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j < 4; j++)
				dynamicDepthMVPBuffer.put(depthMVP.getElement(j, i));
		dynamicDepthMVPBuffer.rewind();
		
		Matrix4f biasMatrix = new Matrix4f(new float[]{	0.5f, 0.f, 0.f, 0.5f, 
				0.f, 0.5f, 0.f, 0.5f, 
				0.f, 0.f, 0.5f, 0.5f,
				0.0f, 0.0f, 0.0f, 1.f});//possibly wrong, change x and y if it doesnt works?

		Matrix4f depthBiasMVP = new Matrix4f();
		depthBiasMVP.mul(biasMatrix, depthMVP);
		
		dynamicDepthBiasBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j< 4; j++)
				dynamicDepthBiasBuffer.put(depthBiasMVP.getElement(j, i));
		dynamicDepthBiasBuffer.rewind();
		
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferName);
		GL11.glViewport(0, 0, shadowmapSize, shadowmapSize);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_FRONT);
		//GL11.glCullFace(GL11.GL_BACK);
		GL20.glUseProgram(shadowShader);
		GL20.glUniformMatrix4(depthMatrixID, false, dynamicDepthMVPBuffer);
		
		GL20.glEnableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer);

		GL11.glDrawElements(GL11.GL_QUADS, count, GL11.GL_UNSIGNED_INT, 0);

		GL20.glDisableVertexAttribArray(0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL20.glUseProgram(0);
	}
	
	
	public static void recalculateShadowMatrices(int playerX, int playerZ, int chunkSize, int viewDistance)
	{
		float left = playerX * chunkSize - viewDistance*chunkSize-15;
		float right = playerX * chunkSize + viewDistance*chunkSize + chunkSize+15;
		float bottom = playerZ * chunkSize - viewDistance*chunkSize-15;
		float top = playerZ * chunkSize + viewDistance*chunkSize + chunkSize+15;
		float zNear = 0.1f;
		float zFar = 2000;

		Matrix4f depthProjectionMatrix = ortho(left, right, top, bottom, zNear, zFar);

		float eyex = lightX + playerX * chunkSize;
		float eyey = lightY;
		float eyez = lightZ + playerZ * chunkSize;
		
		float centerX = playerX * chunkSize;
		float centerY = 0;
		float centerZ = playerZ * chunkSize;
		
		float upX = 0;
		float upY = 1;
		float upZ = 0;

		Matrix4f depthViewMatrix = lookAt(eyex, eyey, eyez, centerX, centerY, centerZ, upX, upY, upZ);
		
		Matrix4f depthMVP = new Matrix4f();
		depthMVP.mul(depthProjectionMatrix, depthViewMatrix);
		
		depthMVPBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j< 4; j++)
				depthMVPBuffer.put(depthMVP.getElement(j, i));
		depthMVPBuffer.rewind();
		
		Matrix4f biasMatrix = new Matrix4f(new float[]{	0.5f, 0.f, 0.f, 0.5f, 
				0.f, 0.5f, 0.f, 0.5f, 
				0.f, 0.f, 0.5f, 0.5f,
				0.0f, 0.0f, 0.0f, 1.f});//possibly wrong, change x and y if it doesnt works?

		Matrix4f depthBiasMVP = new Matrix4f();
		depthBiasMVP.mul(biasMatrix, depthMVP);
		
		depthBiasBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j< 4; j++)
				depthBiasBuffer.put(depthBiasMVP.getElement(j, i));
		depthBiasBuffer.rewind();
	}
	
	public static void recalculateProjectorMatrices(int playerX, int playerZ, int chunkSize, int viewDistance)
	{
		float left = playerX * chunkSize - viewDistance*chunkSize-15;
		float right = playerX * chunkSize + viewDistance*chunkSize + chunkSize+15;
		float bottom = playerZ * chunkSize - viewDistance*chunkSize-15;
		float top = playerZ * chunkSize + viewDistance*chunkSize + chunkSize+15;
		float zNear = 0.1f;
		float zFar = 2000;

		Matrix4f depthProjectionMatrix = ortho(left, right, top, bottom, zNear, zFar);
		
		float eyex = lightX + playerX * chunkSize;
		float eyey = lightY;
		float eyez = lightZ + playerZ * chunkSize;
		
		/*
		float eyex = playerX * chunkSize;
		float eyey = 100;
		float eyez = playerZ * chunkSize;
		*/
		
		float centerX = playerX * chunkSize;
		float centerY = 0;
		float centerZ = playerZ * chunkSize;
		
		float upX = 0;
		float upY = 1;
		float upZ = 0;

		Matrix4f depthViewMatrix = lookAt(eyex, eyey, eyez, centerX, centerY, centerZ, upX, upY, upZ);
		
		Matrix4f depthMVP = new Matrix4f();
		depthMVP.mul(depthProjectionMatrix, depthViewMatrix);
		
		projectionMVPBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j< 4; j++)
				projectionMVPBuffer.put(depthMVP.getElement(j, i));
		projectionMVPBuffer.rewind();
		
		Matrix4f biasMatrix = new Matrix4f(new float[]{	0.5f, 0.f, 0.f, 0.5f, 
				0.f, 0.5f, 0.f, 0.5f, 
				0.f, 0.f, 0.5f, 0.5f,
				0.0f, 0.0f, 0.0f, 1.f});//possibly wrong, change x and y if it doesnt works?

		Matrix4f depthBiasMVP = new Matrix4f();
		depthBiasMVP.mul(biasMatrix, depthMVP);
		
		projectorBiasBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j< 4; j++)
				projectorBiasBuffer.put(depthBiasMVP.getElement(j, i));
		projectorBiasBuffer.rewind();
	}
	
	private static Matrix4f ortho(float left, float right, float top, float bottom, float zNear, float zFar)
	{
		Matrix4f retMatrix = new Matrix4f();
		retMatrix.setIdentity();
		
		retMatrix.m00 = 2f/(right-left);
		retMatrix.m11 = 2f/(top-bottom);
		retMatrix.m22 = -2f/(zFar-zNear);
		retMatrix.m23 = -(zFar+zNear)/(zFar-zNear);
		retMatrix.m33 = 1;
		
		return retMatrix;
	}
	
	private static Matrix4f lookAt(float eyex, float eyey, float eyez, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)
	{
		Vector3f eye = new Vector3f(eyex, eyey, eyez);
		Vector3f center = new Vector3f(centerX, centerY, centerZ);
		Vector3f up = new Vector3f(upX, upY, upZ);
		
		Vector3f F = new Vector3f();
		Vector3f s = new Vector3f();
		Vector3f u = new Vector3f();
		
		Vector3f.sub(center, eye, F);
		F.normalise();
		up.normalise();
		Vector3f.cross(F, up, s);
		if(s.length() != 0)
			s.normalise();
		Vector3f.cross(s, F, u);
		
		Matrix4f retMatrix = new Matrix4f();
		
		retMatrix.setIdentity();
		retMatrix.m00 = s.x;
		retMatrix.m01 = s.y;
		retMatrix.m02 = s.z;
		
		retMatrix.m10 = u.x;
		retMatrix.m11 = u.y;
		retMatrix.m12 = u.z;

		retMatrix.m20 = -F.x;
		retMatrix.m21 = -F.y;
		retMatrix.m22 = -F.z;
		
		Matrix4f translator = new Matrix4f();
		translator.setIdentity();
		translator.m03 = -eye.x;
		translator.m13 = -eye.y;
		translator.m23 = -eye.z;
		
		retMatrix.mul(translator);
		
		return retMatrix;
	}
	
	public static void recalculateRenderMatrices(float rx, float ry, float rz, float scale, float translatePRX, float translatePRY, float translatePRZ, float translateX, float translateY, float translateZ)
	{
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		
		GLU.gluPerspective(45, 4.f/3.f, 0.01f, 1000.f);
		
		FloatBuffer tmpfloatbuffer = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, tmpfloatbuffer);
		GL11.glPopMatrix();
		
		Matrix4f projectionMatrix = new Matrix4f();
		for(int i=0; i < 4; i++)
			for(int j=0; j< 4; j++)
				projectionMatrix.setElement(j, i, tmpfloatbuffer.get());
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		//GL11.glPushMatrix();
		
		GL11.glLoadIdentity();
		GL11.glTranslatef(translatePRX, translatePRY, translatePRZ);
		GL11.glRotatef(rx, 1, 0, 0);
	    GL11.glRotatef(ry, 0, 1, 0);
	    GL11.glRotatef(rz, 0, 0, 1);
	    GL11.glScalef(scale, scale, scale);
	    GL11.glTranslated(translateX, translateY, translateZ);
	    
		tmpfloatbuffer.clear();
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, tmpfloatbuffer);
		
		//GL11.glPopMatrix();
		
		Matrix4f viewMatrix = new Matrix4f();
		for(int i=0; i < 4; i++)
			for(int j=0; j< 4; j++)
				viewMatrix.setElement(j, i, tmpfloatbuffer.get());
		
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.setIdentity();
		
		Matrix4f MVP = new Matrix4f();
		MVP.mul(projectionMatrix, viewMatrix);
		MVP.mul(modelMatrix);
		
		MVPBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j< 4; j++)
				MVPBuffer.put(MVP.getElement(j, i));
		MVPBuffer.rewind();
		
		modelMatrixBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j< 4; j++)
				modelMatrixBuffer.put(modelMatrix.getElement(j, i));
		modelMatrixBuffer.rewind();
		
		viewMatrixBuffer.clear();
		for(int i=0; i < 4; i++)
			for(int j=0; j< 4; j++)
				viewMatrixBuffer.put(viewMatrix.getElement(j, i));
		viewMatrixBuffer.rewind();
	}
	
	private static int LoadShaders(String vertexFile, String fragmentFile)
	{
		int VSID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		int FSID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		String vertexShaderCode = "";
		String fragmentShaderCode = "";
		try
		{
			File f = new File(vertexFile);
			Scanner sc = new Scanner(f);
			while(sc.hasNext())
				vertexShaderCode += sc.nextLine() + "\n";
			sc.close();
			f = new File(fragmentFile);
			sc = new Scanner(f);
			while(sc.hasNext())
				fragmentShaderCode += sc.nextLine() + "\n";
			sc.close();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		//System.out.println(vertexShaderCode + "\n\n" + fragmentShaderCode);
		try
		{
			byte[] vertShaderSrcBytes = vertexShaderCode.getBytes("ASCII");
			byte[] fragShaderSrcBytes = fragmentShaderCode.getBytes("ASCII");
			
			ByteBuffer vertexShaderSourceBuffer = (ByteBuffer) BufferUtils.createByteBuffer(vertShaderSrcBytes.length).put(vertShaderSrcBytes).flip();
			ByteBuffer fragmentShaderSourceBuffer = (ByteBuffer) BufferUtils.createByteBuffer(fragShaderSrcBytes.length).put(fragShaderSrcBytes).flip();
			
			
			vertexShaderSourceBuffer.rewind();
			fragmentShaderSourceBuffer.rewind();
			
			//System.out.println(vertexShaderCode);
			
			GL20.glShaderSource(VSID, vertexShaderSourceBuffer);
			GL20.glCompileShader(VSID);
			

		    IntBuffer compileStatus = BufferUtils.createIntBuffer(1);
		    GL20.glGetShader(VSID, GL20.GL_COMPILE_STATUS, compileStatus);
		    System.out.println(vertexFile);
		    // Query shader info log on error
		    if (compileStatus.get(0) != GL11.GL_TRUE) {
		      IntBuffer infoLogLength = BufferUtils.createIntBuffer(1);
		      ByteBuffer infoLog = BufferUtils.createByteBuffer(1024); // 1024 arbitrarily chosen
		      GL20.glGetShaderInfoLog(VSID, infoLogLength, infoLog);
		      byte[] infoLogBytes = new byte[infoLogLength.get(0)];
		      infoLog.get(infoLogBytes, 0, infoLogLength.get(0));
		      try {
		        System.out.print(new String(infoLogBytes, "ASCII"));
		      } catch (UnsupportedEncodingException ex) {
		        System.out.println(ex);
		        System.exit(1);
		      }
		    }
			
			
			//System.out.println(fragmentShaderCode);
			fragmentShaderSourceBuffer.rewind();
			
			GL20.glShaderSource(FSID, fragmentShaderSourceBuffer);
			GL20.glCompileShader(FSID);
			//int rez = GL20.glgets
			

		    compileStatus = BufferUtils.createIntBuffer(1);
		    GL20.glGetShader(FSID, GL20.GL_COMPILE_STATUS, compileStatus);
		    System.out.println(fragmentFile);
		    // Query shader info log on error
		    if (compileStatus.get(0) != GL11.GL_TRUE) {
		      IntBuffer infoLogLength = BufferUtils.createIntBuffer(1);
		      ByteBuffer infoLog = BufferUtils.createByteBuffer(1024); // 1024 arbitrarily chosen
		      GL20.glGetShaderInfoLog(FSID, infoLogLength, infoLog);
		      byte[] infoLogBytes = new byte[infoLogLength.get(0)];
		      infoLog.get(infoLogBytes, 0, infoLogLength.get(0));
		      try {
		        System.out.print(new String(infoLogBytes, "ASCII"));
		      } catch (UnsupportedEncodingException ex) {
		        System.out.println(ex);
		        System.exit(1);
		      }
		    }
			
			int programID = GL20.glCreateProgram();
			GL20.glAttachShader(programID, VSID);
			GL20.glAttachShader(programID, FSID);
			GL20.glLinkProgram(programID);


		    IntBuffer linkStatus = BufferUtils.createIntBuffer(1);
		    GL20.glGetProgram(programID, GL20.GL_LINK_STATUS, linkStatus);
		    if (linkStatus.get(0) == GL11.GL_TRUE) {
		    	System.out.println("Linked successfully!" + programID);
		      // Use program on success
		      //GL20.glUseProgram(program);
		    } else {
		      // Query program info log on error
		      IntBuffer infoLogLength = BufferUtils.createIntBuffer(1);
		      ByteBuffer infoLog = BufferUtils.createByteBuffer(1024); // 1024 arbitrarily chosen
		      GL20.glGetProgramInfoLog(programID, infoLogLength, infoLog);
		      byte[] infoLogBytes = new byte[infoLogLength.get(0)];
		      infoLog.get(infoLogBytes, 0, infoLogLength.get(0));
		      try {
		        System.out.print(new String(infoLogBytes, "ASCII"));
		      } catch (UnsupportedEncodingException ex) {
		        System.out.println(ex);
		        System.exit(1);
		      }
		    }
			
			GL20.glDeleteShader(VSID);
			GL20.glDeleteShader(FSID);
			return programID;
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
			return -1;
		}
	}
}

package mypackage;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import glmodel.GLImage;
import glmodel.GLMaterialLib;

public class SpriteMap {
	static GLImage texture;
	static GLImage specularMap;
	static GLImage normalMap;
	static GLImage projectionTexture;
	static HashMap<String, Integer> map;
	static float texCoords[][];
	
	private static float texv[] = {
		0.000000f,
		0.083333f,
		0.166666f,
		0.250000f
	};
	
	static void initStatic()
	{
		texture = new GLImage();
		texture.textureHandle = GLMaterialLib.makeTexture("Sprites.jpg");
		specularMap = new GLImage();
		specularMap.textureHandle = GLMaterialLib.makeTexture("specularMap.jpg");
		normalMap = new GLImage();
		normalMap.textureHandle = GLMaterialLib.makeTexture("normalMap.jpg");
		projectionTexture = new GLImage();
		projectionTexture.textureHandle = GLMaterialLib.makeTexture("projection.jpg");
		
		//tex.textureHandle = GLMaterialLib.makeTexture("uvmap.DDS");
		System.out.println("SpriteMap texture handle: " + texture.textureHandle);
		map = new HashMap<String, Integer>();
		File f = new File("textures.txt");
		try
		{
			Scanner sc = new Scanner(f);
			int nTex = 0;
			while(sc.hasNext())
			{
				String textureName = sc.next();
				map.put(textureName, nTex);
				nTex++;
			}
			sc.close();
			texCoords = new float[nTex+1][];
			float cst = 0.008f;
			for(int i=0; i < nTex+1; i++)
			{
				texCoords[i] = new float[]{
					texv[0]+(i%4)/4.f+cst, texv[2]+(i/4)/4.f+cst, texv[0]+(i%4)/4.f+cst, texv[3]+(i/4)/4.f-cst, texv[1]+(i%4)/4.f-cst, texv[3]+(i/4)/4.f-cst, texv[1]+(i%4)/4.f-cst, texv[2]+(i/4)/4.f+cst, //enka - zadnji del
					texv[1]+(i%4)/4.f+cst, texv[2]+(i/4)/4.f+cst, texv[1]+(i%4)/4.f+cst, texv[3]+(i/4)/4.f-cst, texv[2]+(i%4)/4.f-cst, texv[3]+(i/4)/4.f-cst, texv[2]+(i%4)/4.f-cst, texv[2]+(i/4)/4.f+cst, //dvojka - spodnji del
					texv[2]+(i%4)/4.f+cst, texv[2]+(i/4)/4.f+cst, texv[2]+(i%4)/4.f+cst, texv[3]+(i/4)/4.f-cst, texv[3]+(i%4)/4.f-cst, texv[3]+(i/4)/4.f-cst, texv[3]+(i%4)/4.f-cst, texv[2]+(i/4)/4.f+cst, //trojka - zgornji del 
					texv[0]+(i%4)/4.f+cst, texv[1]+(i/4)/4.f+cst, texv[0]+(i%4)/4.f+cst, texv[2]+(i/4)/4.f-cst, texv[1]+(i%4)/4.f-cst, texv[2]+(i/4)/4.f-cst, texv[1]+(i%4)/4.f-cst, texv[1]+(i/4)/4.f+cst, //stirka - sprednji del
					texv[1]+(i%4)/4.f+cst, texv[1]+(i/4)/4.f+cst, texv[1]+(i%4)/4.f+cst, texv[2]+(i/4)/4.f-cst, texv[2]+(i%4)/4.f-cst, texv[2]+(i/4)/4.f-cst, texv[2]+(i%4)/4.f-cst, texv[1]+(i/4)/4.f+cst, //petka - desni del
					texv[2]+(i%4)/4.f+cst, texv[1]+(i/4)/4.f+cst, texv[2]+(i%4)/4.f+cst, texv[2]+(i/4)/4.f-cst, texv[3]+(i%4)/4.f-cst, texv[2]+(i/4)/4.f-cst, texv[3]+(i%4)/4.f-cst, texv[1]+(i/4)/4.f+cst, //sestka - levi del
				};
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		
	}
}

package mypackage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.linearmath.Transform;


public class Chunk {
	Tile tiles[][][];	//tiles[x][y][z]
	DynamicsWorld dynamicsWorld;
	int chunkX, chunkY, chunkZ;
	static int chunkSize = 15;
	static int chunkHeight = 30;

	int vertexBufferID;
	int indexBufferID;
	int uvBufferID;
	int normalBufferID;
	int tangentBufferID;
	int bitangentBufferID;
	
	
	int waterVertexBufferID;
	int waterIndexBufferID;
	int waterNormalBufferID;
	int waterUvBufferID;
	
	int waterElements;
	int nelements;

	static float loadTime = 0;
	static int loadNum = 0;
	static float calcTime = 0;
	static int calcNum = 0;
	
	private ArrayList<Float> vertexList;	//temporary buffer for vertices to be rendered
	private ArrayList<Float> uvList;	//temporary buffer for texture coords
	private ArrayList<Float> normalList;	//temporary buffer for normals!
	private ArrayList<Integer> indexList;	//temporary buffer for index coords
	private ArrayList<Float> tangentList;
	private ArrayList<Float> bitangentList;
	
	private ArrayList<Float> waterVertexList;
	private ArrayList<Float> waterUvList;
	private ArrayList<Float> waterNormalList;
	private ArrayList<Integer> waterIndexList;
	
	private ArrayList<Tile> tileList;
	
	int simulatedBlocks;
	
	Chunk(DynamicsWorld dynamicsWorld)
	{
		tiles = new Tile[chunkSize][][];
		for(int i=0; i < tiles.length; i++)
			tiles[i] = new Tile[chunkHeight][];
		for(int i=0; i < tiles.length; i++)
			for(int j=0; j < tiles[i].length; j++)
				tiles[i][j] = new Tile[chunkSize];
		this.dynamicsWorld = dynamicsWorld;
		
		tileList = new ArrayList<Tile>();
		
		vertexList = new ArrayList<Float>();
		uvList = new ArrayList<Float>();
		indexList = new ArrayList<Integer>();
		normalList = new ArrayList<Float>();
		tangentList = new ArrayList<Float>();
		bitangentList = new ArrayList<Float>();
		
		waterVertexList = new ArrayList<Float>();
		waterUvList = new ArrayList<Float>();
		waterIndexList = new ArrayList<Integer>();
		waterNormalList = new ArrayList<Float>();
		
		vertexBufferID = GL15.glGenBuffers();
		indexBufferID = GL15.glGenBuffers();
		uvBufferID = GL15.glGenBuffers();
		normalBufferID = GL15.glGenBuffers();
		tangentBufferID = GL15.glGenBuffers();
		bitangentBufferID = GL15.glGenBuffers();
		
		waterVertexBufferID = GL15.glGenBuffers();
		waterIndexBufferID = GL15.glGenBuffers();
		waterNormalBufferID = GL15.glGenBuffers();
		waterUvBufferID = GL15.glGenBuffers();

		simulatedBlocks = 0;
	}
	
	void loadFromFile(int x, int y, int z)
	{
		loadNum++;
		System.out.println("Loading from file.");
		tileList.clear();
		float time = System.nanoTime();
		chunkX = x;
		chunkY = y;
		chunkZ = z;
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("map\\map-" + x + "-" + z + ".txt")));
			String line;
			while((line = br.readLine())!= null)
			{
				String[] p = line.split(" ");
				int cx = Integer.parseInt(p[0]);
				int cy = Integer.parseInt(p[1]);
				int cz = Integer.parseInt(p[2]);
				String texname = p[3];
				
				if(tiles[cx%chunkSize][cy%chunkHeight][cz%chunkSize] == null)
					tiles[cx%chunkSize][cy%chunkHeight][cz%chunkSize] = new Tile(texname, cx, cy, cz);
				else
					tiles[cx%chunkSize][cy%chunkHeight][cz%chunkSize].readd(texname, cx, cy, cz);
				tileList.add(tiles[cx%chunkSize][cy%chunkHeight][cz%chunkSize]);
			}
			br.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		/*
		chunkX = x;
		chunkY = y;
		chunkZ = z;
		try
		{
			File f = new File("map\\map-" + x + "-" + z + ".txt");
			//System.out.println("chunk " + x + " " + y + " " + z);
			Scanner sc = new Scanner(f);
			while(sc.hasNext())
			{
				int cx = sc.nextInt();
				int cy = sc.nextInt();
				int cz = sc.nextInt();
				//System.out.println(cx + " " + cy + " " + cz);
				String texname = sc.next();
				if(tiles[cx%chunkSize][cy%chunkHeight][cz%chunkSize] == null)
					tiles[cx%chunkSize][cy%chunkHeight][cz%chunkSize] = new Tile(texname, cx, cy, cz);
				else
					tiles[cx%chunkSize][cy%chunkHeight][cz%chunkSize].readd(texname, cx, cy, cz);
				//System.out.println("Tile " + cx + " " + cy + " " + cz);
			}
			sc.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}*/
		System.out.println("Loaded in: " + (System.nanoTime() - time)/1000000f + "\nCalculating model.");
		loadTime += (System.nanoTime() - time)/1000000f;
		//time = System.nanoTime();
		/*
		for(int i=0; i < tiles.length; i++)
			for(int j=0; j < tiles[i].length; j++)
				for(int k=0; k < tiles[i][j].length; k++)
				{
					if(tiles[i][j][k] == null || tiles[i][j][k].active == false) 
						continue; //if no tile, go on
					else if(i == 0 || i == tiles.length-1 || j == tiles[i].length-1|| k == 0 || k == tiles[i][j].length-1) 
					{	//edge blocks. Always visible
						dynamicsWorld.addRigidBody(tiles[i][j][k].body);
						simulatedBlocks++;
					}
					else if(tiles[i][j][k+1] == null || tiles[i][j][k-1] == null ||	tiles[i][j+1][k] == null || (j >= 1 && tiles[i][j-1][k] == null) || tiles[i+1][j][k] == null || tiles[i-1][j][k] == null ||
						tiles[i][j][k+1].active == false || tiles[i][j][k-1].active == false ||	tiles[i][j+1][k].active == false || (j >= 1 && tiles[i][j-1][k].active == false) || tiles[i+1][j][k].active == false || tiles[i-1][j][k].active == false)
					{	//visible blocks
						dynamicsWorld.addRigidBody(tiles[i][j][k].body);
						simulatedBlocks++;
					}
				}*/
		calculateModel();
		//System.out.println("Calculated in: " + (System.nanoTime() - time)/1000000f);
		//System.out.println("Active blocks: " + simulatedBlocks);
	}
	
	void removeFromMemory()
	{
		System.out.println("Removing from mem");
		double time = System.nanoTime();
		for(int i=0; i < tiles.length; i++)
			for(int j=0; j < tiles[i].length; j++)
				for(int k=0; k < tiles[i][j].length; k++)
				{
					if(tiles[i][j][k] != null && tiles[i][j][k].isActive)
					{
						//dynamicsWorld.removeRigidBody(tiles[i][j][k].body);
						tiles[i][j][k].remove();
					}
				}
		System.out.println("Finished removing in " + (System.nanoTime() - time)/1000000f);
		simulatedBlocks = 0;
	}
	
	public void calculateModel()
	{
		calcNum++;
		float time = System.nanoTime();

		
		vertexList.clear();
		uvList.clear();
		indexList.clear();		
		normalList.clear();
		tangentList.clear();
		bitangentList.clear();
		
		
		for(int i=0; i < tiles.length; i++)
			for(int j=0; j < tiles[i].length; j++)
				for(int k=0; k < tiles[i][j].length; k++)
				{
					if(tiles[i][j][k] == null || tiles[i][j][k].isActive == false)
						continue;	//if current title nonexistant, continue
					int th = SpriteMap.map.get(tiles[i][j][k].textureName);
					
					if(tiles[i][j][k].isWater == true)
					{
						for(int l=0; l < 4; l++)
						{
							waterVertexList.add(gfxRenderedBox.top[l*3] + chunkX *chunkSize + i);
							waterVertexList.add(gfxRenderedBox.top[l*3+1] + chunkY * chunkHeight + j);
							waterVertexList.add(gfxRenderedBox.top[l*3+2] + chunkZ * chunkSize + k);
						}
						for(int l=0; l < 8; l++)
							waterUvList.add(SpriteMap.texCoords[th][16+l]);
						for(int l=0; l < 4; l++)
							waterIndexList.add(waterIndexList.size());
						for(int l=0; l < 4; l++)
						{
							waterNormalList.add(0f);
							waterNormalList.add(1f);
							waterNormalList.add(0f);
						}
						continue;
					}
					
					//System.out.println("Current tile tex: " + th);
					boolean forceDown = false;
					boolean wildCard = false;
					if(i == 0 || tiles[i-1][j][k] == null ||tiles[i-1][j][k].isActive == false || tiles[i-1][j][k].isWater == true || wildCard)//levo je frej
					{
						for(int l=0; l < 4; l++)
						{
							vertexList.add(gfxRenderedBox.left[l*3] + chunkX *chunkSize + i);
							vertexList.add(gfxRenderedBox.left[l*3+1] + chunkY * chunkHeight + j);
							vertexList.add(gfxRenderedBox.left[l*3+2] + chunkZ * chunkSize + k);
						}
						for(int l=0; l < 8; l++)
							uvList.add(SpriteMap.texCoords[th][40+l]);
						for(int l=0; l < 4; l++)
							indexList.add(indexList.size());
						for(int l=0; l < 4; l++)
						{
							normalList.add(-1f);
							normalList.add(0f);
							normalList.add(0f);

							tangentList.add(gfxRenderedBox.tangents[60+l*3]);
							tangentList.add(gfxRenderedBox.tangents[60+l*3+1]);
							tangentList.add(gfxRenderedBox.tangents[60+l*3+2]);

							bitangentList.add(gfxRenderedBox.bitangents[60+l*3]);
							bitangentList.add(gfxRenderedBox.bitangents[60+l*3+1]);
							bitangentList.add(gfxRenderedBox.bitangents[60+l*3+2]);
						}
						
						
						forceDown = true;
					}
					if(i == chunkSize-1 || tiles[i+1][j][k] == null || tiles[i+1][j][k].isActive == false || tiles[i+1][j][k].isWater == true || wildCard)//desno je frej
					{
						for(int l=0; l < 4; l++)
						{
							vertexList.add(gfxRenderedBox.right[l*3] + chunkX *chunkSize + i);
							vertexList.add(gfxRenderedBox.right[l*3+1] + chunkY * chunkHeight + j);
							vertexList.add(gfxRenderedBox.right[l*3+2] + chunkZ * chunkSize + k);
						}
						for(int l=0; l < 8; l++)
							uvList.add(SpriteMap.texCoords[th][32+l]);
						for(int l=0; l < 4; l++)
							indexList.add(indexList.size());
						for(int l=0; l < 4; l++)
						{
							normalList.add(1f);
							normalList.add(0f);
							normalList.add(0f);

							tangentList.add(gfxRenderedBox.tangents[48+l*3]);
							tangentList.add(gfxRenderedBox.tangents[48+l*3+1]);
							tangentList.add(gfxRenderedBox.tangents[48+l*3+2]);

							bitangentList.add(gfxRenderedBox.bitangents[48+l*3]);
							bitangentList.add(gfxRenderedBox.bitangents[48+l*3+1]);
							bitangentList.add(gfxRenderedBox.bitangents[48+l*3+2]);
						}
						forceDown = true;
					}
					if(k == 0 || tiles[i][j][k-1] == null || tiles[i][j][k-1].isActive == false || tiles[i][j][k-1].isWater == true || wildCard)//spredi frej
					{
						for(int l=0; l < 4; l++)
						{
							vertexList.add(gfxRenderedBox.front[l*3] + chunkX * chunkSize + i);
							vertexList.add(gfxRenderedBox.front[l*3+1] + chunkY * chunkHeight + j);
							vertexList.add(gfxRenderedBox.front[l*3+2] + chunkZ * chunkSize + k);
						}
						for(int l=0; l < 8; l++)
							uvList.add(SpriteMap.texCoords[th][24+l]);
						for(int l=0; l < 4; l++)
							indexList.add(indexList.size());
						for(int l=0; l < 4; l++)
						{
							normalList.add(0f);
							normalList.add(0f);
							normalList.add(-1f);

							tangentList.add(gfxRenderedBox.tangents[l*3]);
							tangentList.add(gfxRenderedBox.tangents[l*3+1]);
							tangentList.add(gfxRenderedBox.tangents[l*3+2]);

							bitangentList.add(gfxRenderedBox.bitangents[l*3]);
							bitangentList.add(gfxRenderedBox.bitangents[l*3+1]);
							bitangentList.add(gfxRenderedBox.bitangents[l*3+2]);
						}
						forceDown = true;
					}
					if(k == chunkSize-1 || tiles[i][j][k+1] == null || tiles[i][j][k+1].isActive == false || tiles[i][j][k+1].isWater == true || wildCard)//zadi frej
					{
						for(int l=0; l < 4; l++)
						{
							vertexList.add(gfxRenderedBox.back[l*3] + chunkX *chunkSize + i);
							vertexList.add(gfxRenderedBox.back[l*3+1] + chunkY * chunkHeight + j);
							vertexList.add(gfxRenderedBox.back[l*3+2] + chunkZ * chunkSize + k);
						}
						for(int l=0; l < 8; l++)
						{
							uvList.add(SpriteMap.texCoords[th][l]);
						}
						for(int l=0; l < 4; l++)
							indexList.add(indexList.size());
						for(int l=0; l < 4; l++)
						{
							normalList.add(0f);
							normalList.add(0f);
							normalList.add(1f);

							tangentList.add(gfxRenderedBox.tangents[36+l*3]);
							tangentList.add(gfxRenderedBox.tangents[36+l*3+1]);
							tangentList.add(gfxRenderedBox.tangents[36+l*3+2]);

							bitangentList.add(gfxRenderedBox.bitangents[36+l*3]);
							bitangentList.add(gfxRenderedBox.bitangents[36+l*3+1]);
							bitangentList.add(gfxRenderedBox.bitangents[36+l*3+2]);
						}
						forceDown = true;
					}
					if(j == chunkHeight-1 || tiles[i][j+1][k] == null || tiles[i][j+1][k].isActive == false || tiles[i][j+1][k].isWater == true || wildCard)//zgori frej
					{
						for(int l=0; l < 4; l++)
						{
							vertexList.add(gfxRenderedBox.top[l*3] + chunkX *chunkSize + i);
							vertexList.add(gfxRenderedBox.top[l*3+1] + chunkY * chunkHeight + j);
							vertexList.add(gfxRenderedBox.top[l*3+2] + chunkZ * chunkSize + k);
						}
						for(int l=0; l < 8; l++)
							uvList.add(SpriteMap.texCoords[th][16+l]);
						for(int l=0; l < 4; l++)
							indexList.add(indexList.size());
						for(int l=0; l < 4; l++)
						{
							normalList.add(0f);
							normalList.add(1f);
							normalList.add(0f);

							tangentList.add(gfxRenderedBox.tangents[24+l*3]);
							tangentList.add(gfxRenderedBox.tangents[24+l*3+1]);
							tangentList.add(gfxRenderedBox.tangents[24+l*3+2]);

							bitangentList.add(gfxRenderedBox.bitangents[24+l*3]);
							bitangentList.add(gfxRenderedBox.bitangents[24+l*3+1]);
							bitangentList.add(gfxRenderedBox.bitangents[24+l*3+2]);
						}
						forceDown = true;
					}
					if(j > 0 && (tiles[i][j-1][k] == null || tiles[i][j-1][k].isActive == false || tiles[i][j-1][k].isWater == true) || forceDown || wildCard)//spodi frej
					{
						for(int l=0; l < 4; l++)
						{
							vertexList.add(gfxRenderedBox.bottom[l*3] + chunkX *chunkSize + i);
							vertexList.add(gfxRenderedBox.bottom[l*3+1] + chunkY * chunkHeight + j);
							vertexList.add(gfxRenderedBox.bottom[l*3+2] + chunkZ * chunkSize + k);
						}
						for(int l=0; l < 8; l++)
							uvList.add(SpriteMap.texCoords[th][8+l]);
						for(int l=0; l < 4; l++)
							indexList.add(indexList.size());
						for(int l=0; l < 4; l++)
						{
							normalList.add(0f);
							normalList.add(-1f);
							normalList.add(0f);

							tangentList.add(gfxRenderedBox.tangents[12+l*3]);
							tangentList.add(gfxRenderedBox.tangents[12+l*3+1]);
							tangentList.add(gfxRenderedBox.tangents[12+l*3+2]);

							bitangentList.add(gfxRenderedBox.bitangents[12+l*3]);
							bitangentList.add(gfxRenderedBox.bitangents[12+l*3+1]);
							bitangentList.add(gfxRenderedBox.bitangents[12+l*3+2]);
						}
					}
				}
		
		/*
		for(int ctr=0; ctr < tileList.size(); ctr++)
		{
			//if(tiles[i][j][k] == null || tiles[i][j][k].isActive == false)
			//	continue;	//if current title nonexistant, continue
			Tile cTile = tileList.get(ctr);
			Transform tileTransform = cTile.body.getWorldTransform(new Transform());
			int x = (int)tileTransform.origin.x % chunkSize, 
				y = (int)tileTransform.origin.y % chunkHeight, 
				z = (int)tileTransform.origin.z % chunkSize;
			
			int th = SpriteMap.map.get(cTile.textureName);
			
			if(cTile.isWater == true)
			{
				for(int l=0; l < 4; l++)
				{
					waterVertexList.add(gfxRenderedBox.top[l*3] + chunkX *chunkSize + x);
					waterVertexList.add(gfxRenderedBox.top[l*3+1] + chunkY * chunkHeight + y);
					waterVertexList.add(gfxRenderedBox.top[l*3+2] + chunkZ * chunkSize + z);
				}
				for(int l=0; l < 8; l++)
					waterUvList.add(SpriteMap.texCoords[th][16+l]);
				for(int l=0; l < 4; l++)
					waterIndexList.add(waterIndexList.size());
				for(int l=0; l < 4; l++)
				{
					waterNormalList.add(0f);
					waterNormalList.add(1f);
					waterNormalList.add(0f);
				}
				continue;
			}
			
			//System.out.println("Current tile tex: " + th);
			boolean forceDown = false;
			boolean wildCard = false;
			//System.out.println(tileTransform.origin.x + " " + tileTransform.origin.y + " " + tileTransform.origin.z);
			//System.out.println(x + " " + y + " " + z);
			if(x == 0 || tiles[x-1][y][z] == null ||tiles[x-1][y][z].isActive == false || tiles[x-1][y][z].isWater == true || wildCard)//levo je frej
			{
				for(int l=0; l < 4; l++)
				{
					vertexList.add(gfxRenderedBox.left[l*3] + chunkX *chunkSize + x);
					vertexList.add(gfxRenderedBox.left[l*3+1] + chunkY * chunkHeight + y);
					vertexList.add(gfxRenderedBox.left[l*3+2] + chunkZ * chunkSize + z);
				}
				for(int l=0; l < 8; l++)
					uvList.add(SpriteMap.texCoords[th][40+l]);
				for(int l=0; l < 4; l++)
					indexList.add(indexList.size());
				for(int l=0; l < 4; l++)
				{
					normalList.add(-1f);
					normalList.add(0f);
					normalList.add(0f);
				}
				forceDown = true;
			}
			if(x == chunkSize-1 || tiles[x+1][y][z] == null || tiles[x+1][y][z].isActive == false || tiles[x+1][y][z].isWater == true || wildCard)//desno je frej
			{
				for(int l=0; l < 4; l++)
				{
					vertexList.add(gfxRenderedBox.right[l*3] + chunkX *chunkSize + x);
					vertexList.add(gfxRenderedBox.right[l*3+1] + chunkY * chunkHeight + y);
					vertexList.add(gfxRenderedBox.right[l*3+2] + chunkZ * chunkSize + z);
				}
				for(int l=0; l < 8; l++)
					uvList.add(SpriteMap.texCoords[th][32+l]);
				for(int l=0; l < 4; l++)
					indexList.add(indexList.size());
				for(int l=0; l < 4; l++)
				{
					normalList.add(1f);
					normalList.add(0f);
					normalList.add(0f);
				}
				forceDown = true;
			}
			if(z == 0 || tiles[x][y][z-1] == null || tiles[x][y][z-1].isActive == false || tiles[x][y][z-1].isWater == true || wildCard)//spredi frej
			{
				for(int l=0; l < 4; l++)
				{
					vertexList.add(gfxRenderedBox.front[l*3] + chunkX * chunkSize + x);
					vertexList.add(gfxRenderedBox.front[l*3+1] + chunkY * chunkHeight + y);
					vertexList.add(gfxRenderedBox.front[l*3+2] + chunkZ * chunkSize + z);
				}
				for(int l=0; l < 8; l++)
					uvList.add(SpriteMap.texCoords[th][24+l]);
				for(int l=0; l < 4; l++)
					indexList.add(indexList.size());
				for(int l=0; l < 4; l++)
				{
					normalList.add(0f);
					normalList.add(0f);
					normalList.add(-1f);
				}
				forceDown = true;
			}
			if(z == chunkSize-1 || tiles[x][y][z+1] == null || tiles[x][y][z+1].isActive == false || tiles[x][y][z+1].isWater == true || wildCard)//zadi frej
			{
				for(int l=0; l < 4; l++)
				{
					vertexList.add(gfxRenderedBox.back[l*3] + chunkX *chunkSize + x);
					vertexList.add(gfxRenderedBox.back[l*3+1] + chunkY * chunkHeight + y);
					vertexList.add(gfxRenderedBox.back[l*3+2] + chunkZ * chunkSize + z);
				}
				for(int l=0; l < 8; l++)
				{
					uvList.add(SpriteMap.texCoords[th][l]);
				}
				for(int l=0; l < 4; l++)
					indexList.add(indexList.size());
				for(int l=0; l < 4; l++)
				{
					normalList.add(0f);
					normalList.add(0f);
					normalList.add(1f);
				}
				forceDown = true;
			}
			if(y == chunkHeight-1 || tiles[x][y+1][z] == null || tiles[x][y+1][z].isActive == false || tiles[x][y+1][z].isWater == true || wildCard)//zgori frej
			{
				for(int l=0; l < 4; l++)
				{
					vertexList.add(gfxRenderedBox.top[l*3] + chunkX *chunkSize + x);
					vertexList.add(gfxRenderedBox.top[l*3+1] + chunkY * chunkHeight + y);
					vertexList.add(gfxRenderedBox.top[l*3+2] + chunkZ * chunkSize + z);
				}
				for(int l=0; l < 8; l++)
					uvList.add(SpriteMap.texCoords[th][16+l]);
				for(int l=0; l < 4; l++)
					indexList.add(indexList.size());
				for(int l=0; l < 4; l++)
				{
					normalList.add(0f);
					normalList.add(1f);
					normalList.add(0f);
				}
				forceDown = true;
			}
			if(y > 0 && (tiles[x][y-1][z] == null || tiles[x][y-1][z].isActive == false || tiles[x][y-1][z].isWater == true) || forceDown || wildCard)//spodi frej
			{
				for(int l=0; l < 4; l++)
				{
					vertexList.add(gfxRenderedBox.bottom[l*3] + chunkX *chunkSize + x);
					vertexList.add(gfxRenderedBox.bottom[l*3+1] + chunkY * chunkHeight + y);
					vertexList.add(gfxRenderedBox.bottom[l*3+2] + chunkZ * chunkSize + z);
				}
				for(int l=0; l < 8; l++)
					uvList.add(SpriteMap.texCoords[th][8+l]);
				for(int l=0; l < 4; l++)
					indexList.add(indexList.size());
				for(int l=0; l < 4; l++)
				{
					normalList.add(0f);
					normalList.add(-1f);
					normalList.add(0f);
				}
			}
		}*/
		
		FloatBuffer vertexData;
		FloatBuffer textureData;
		FloatBuffer normalData;
		FloatBuffer tangentData;
		FloatBuffer bitangentData;
		IntBuffer indexData;
		
		vertexData = BufferUtils.createFloatBuffer(vertexList.size());
		textureData = BufferUtils.createFloatBuffer(uvList.size());
		indexData = BufferUtils.createIntBuffer(indexList.size());
		normalData = BufferUtils.createFloatBuffer(normalList.size());
		tangentData = BufferUtils.createFloatBuffer(tangentList.size());
		bitangentData = BufferUtils.createFloatBuffer(bitangentList.size());
		
		
		for(int i=0; i < vertexList.size(); i++)
			vertexData.put(vertexList.get(i));
		for(int i=0; i < uvList.size(); i++)
			textureData.put(uvList.get(i));
		for(int i=0; i < indexList.size(); i++)
			indexData.put(indexList.get(i));
		for(int i=0; i < normalList.size(); i++)
			normalData.put(normalList.get(i));
		for(int i=0; i < tangentList.size(); i++)
			tangentData.put(tangentList.get(i));
		for(int i=0; i < bitangentList.size(); i++)
			bitangentData.put(bitangentList.get(i));
		
		vertexData.rewind();
		indexData.rewind();
		textureData.rewind();
		normalData.rewind();
		tangentData.rewind();
		bitangentData.rewind();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_DYNAMIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexData, GL15.GL_DYNAMIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureData, GL15.GL_DYNAMIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalData, GL15.GL_DYNAMIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tangentBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tangentData, GL15.GL_DYNAMIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bitangentBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bitangentData, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		nelements = indexList.size();
		
		vertexList.clear();
		uvList.clear();
		indexList.clear();		
		normalList.clear();
		tangentList.clear();
		bitangentList.clear();
		
		vertexData.clear();
		textureData.clear();
		indexData.clear();
		normalData.clear();
		tangentData.clear();
		bitangentData.clear();
		
		
		vertexData = BufferUtils.createFloatBuffer(waterVertexList.size());
		textureData = BufferUtils.createFloatBuffer(waterUvList.size());
		indexData = BufferUtils.createIntBuffer(waterIndexList.size());
		normalData = BufferUtils.createFloatBuffer(waterNormalList.size());
		
		for(int i=0; i < waterVertexList.size(); i++)
			vertexData.put(waterVertexList.get(i));
		for(int i=0; i < waterUvList.size(); i++)
			textureData.put(waterUvList.get(i));
		for(int i=0; i < waterIndexList.size(); i++)
			indexData.put(waterIndexList.get(i));
		for(int i=0; i < waterNormalList.size(); i++)
			normalData.put(waterNormalList.get(i));
		
		vertexData.rewind();
		textureData.rewind();
		indexData.rewind();
		normalData.rewind();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, waterVertexBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, waterIndexBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexData, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, waterUvBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureData, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);		
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, waterNormalBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalData, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		waterElements = waterIndexList.size();

		waterVertexList.clear();
		waterUvList.clear();
		waterIndexList.clear();		
		waterNormalList.clear();
		
		vertexData.clear();
		textureData.clear();
		indexData.clear();
		normalData.clear();
		calcTime += (System.nanoTime()-time)/1000000f; 
		System.out.println("Calculated in: " + (System.nanoTime() - time)/1000000f);
	}
	
	boolean tryAdding(int x, int y, int z, String textureName)
	{
		if(tiles[x%chunkSize][y%chunkHeight][z%chunkSize] != null && tiles[x%chunkSize][y%chunkHeight][z%chunkSize].isActive == true)	//if tile exists, return
			return false;
		
		if(tiles[x%chunkSize][y%chunkHeight][z%chunkSize] == null)
			tiles[x%chunkSize][y%chunkHeight][z%chunkSize] = new Tile(textureName, x, y, z);
		else if(tiles[x%chunkSize][y%chunkHeight][z%chunkSize].isActive == false)
			tiles[x%chunkSize][y%chunkHeight][z%chunkSize].readd(textureName, x, y, z);
		tileList.add(tiles[x%chunkSize][y%chunkHeight][z%chunkSize]);
		//dynamicsWorld.addRigidBody(tiles[x%chunkSize][y%chunkHeight][z%chunkSize].body);
		calculateModel();
		return true;
	}
	
	boolean remove(int x, int y, int z)
	{
		if(tiles[x%chunkSize][y%chunkHeight][z%chunkSize] == null || tiles[x%chunkSize][y%chunkHeight][z%chunkSize].isActive == false)
			return false;
		tiles[x%chunkSize][y%chunkHeight][z%chunkSize].remove();
		tileList.remove(tiles[x%chunkSize][y%chunkHeight][z%chunkSize]);
		//dynamicsWorld.removeCollisionObject(tiles[x%chunkSize][y%chunkHeight][z%chunkSize].body);
		
		calculateModel();
		return true;
	}
	
}

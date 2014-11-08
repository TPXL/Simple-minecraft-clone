package mypackage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;


public abstract class Robert {
	static Chunk chunks[][];	//chunks[x][y]
	static int playerX = -1;	//chunk coords
	static int playerZ = -1;	//chunk coords
	static int viewDistance = -1;
	static int worldWidth = -1;
	static int worldDepth = -1; 
	static DynamicsWorld dynamicsWorld;
	static HashSet<Tile> simulatedTilesPrev;
	static HashSet<Tile> simulatedTilesCurr;
	static int simulatedBlocks = 0;
	
	public static void init(int viewdist, float pX, float pZ, int worldD, int worldW, DynamicsWorld dW)
	{
		viewDistance = viewdist;
		dynamicsWorld = dW;
		playerX = (int)(pX/Chunk.chunkSize);
		playerZ = (int)(pZ/Chunk.chunkSize);
		worldWidth = worldW;
		worldDepth = worldD;
		simulatedTilesPrev = new HashSet<Tile>();
		simulatedTilesCurr = new HashSet<Tile>();
		
		int left = playerX - viewDistance;
		int right = playerX + viewDistance;
		if(left < 0)
		{
			left = 0;
			right = 2*viewDistance+1;
		}
		else if(right > worldWidth)
		{
			right = worldWidth;
			left = worldWidth-(2*viewDistance+1);
		}
		
		int far = playerZ + viewDistance;
		int near = playerZ - viewDistance;
		if(far > worldDepth)
		{
			far = worldDepth;
			near = worldDepth -(2*viewDistance+1);
		}
		else if(near < 0)
		{
			near = 0;
			far = 2*viewDistance+1;
		}
		chunks = new Chunk[viewDistance*2+1][];
		for(int i=0; i < chunks.length; i++)
			chunks[i] = new Chunk[viewDistance*2+1];
		int size = viewDistance * 2 + 1;
		for(int i=0; i < chunks.length; i++)
			for(int j=0; j < chunks[i].length; j++)
			{
				chunks[(near+i)%size][(left+j)%size] = new Chunk(dW);
				chunks[(near+i)%size][(left+j)%size].loadFromFile(near + i, 0, left + j);
				System.out.println("Loading " + (near+i) + " " + (left+j) +" into " +i + " " + j);
				chunks[(near+i)%size][(left+j)%size].calculateModel();
			}
		Shaders.recalculateShadowMatrices(playerX < viewDistance ? viewDistance : playerX > worldWidth - viewDistance ? worldWidth : playerX, playerZ < viewDistance ? viewDistance : playerZ > worldDepth - viewDistance ? worldDepth - viewDistance : playerZ, Chunk.chunkSize, viewDistance);
		Shaders.recalculateProjectorMatrices(playerX < viewDistance ? viewDistance : playerX > worldWidth - viewDistance ? worldWidth : playerX, playerZ < viewDistance ? viewDistance : playerZ > worldDepth - viewDistance ? worldDepth - viewDistance : playerZ, Chunk.chunkSize, viewDistance);
		rerenderShadows();
		
	}
	
	public static void movePlayer(float pX, float pZ)	//NOT FINISHED
	{
		Shaders.recalculateShadowMatrices(playerX < viewDistance ? viewDistance : playerX > worldWidth - viewDistance ? worldWidth : playerX, playerZ < viewDistance ? viewDistance : playerZ > worldDepth - viewDistance ? worldDepth - viewDistance : playerZ, Chunk.chunkSize, viewDistance);
		Shaders.recalculateProjectorMatrices(playerX < viewDistance ? viewDistance : playerX > worldWidth - viewDistance ? worldWidth : playerX, playerZ < viewDistance ? viewDistance : playerZ > worldDepth - viewDistance ? worldDepth - viewDistance : playerZ, Chunk.chunkSize, 1);
		
		if((int)(pX/Chunk.chunkSize) == playerX && (int)(pZ/Chunk.chunkSize) == playerZ)	//if nothing has changed, return
		{
			rerenderShadows();	//move this somewhere sensible
			//System.out.println("Player did not change chunks");
			return;
		}
		System.out.println("Player changed chunks (" + playerX + "," + playerZ + ")->(" + (int)(pX/Chunk.chunkSize) + "," + (int)(pZ/Chunk.chunkSize) + ")");
		float time = System.nanoTime();
		if(playerX > (int)(pX/Chunk.chunkSize))	//player moved LEFT
		{
			playerX = (int)(pX/Chunk.chunkSize);	//chunk coords
			if(playerX <= worldWidth - viewDistance && playerX >= viewDistance)
			{
				System.out.println("prvi");
				int rightmost = (playerX + viewDistance +1)%(2*viewDistance+1);
				System.out.println(rightmost);
				/*
				int mid = playerZ;
				if(mid < viewDistance)
					mid = viewDistance;
				if(mid > worldWidth - viewDistance)
					mid = worldWidth - viewDistance;*/
				
				for(int i=0; i < chunks.length; i++)
				{
					chunks[rightmost][i].removeFromMemory();
					//chunks[rightmost][i].loadFromFile(playerX-viewDistance, 0, mid - viewDistance + i);
					chunks[rightmost][i].loadFromFile(playerX-viewDistance, 0, chunks[rightmost][i].chunkZ);
				}
			}
		}
		else if(playerX < (int)(pX/Chunk.chunkSize))	//player moved RIGHT
		{
			playerX = (int)(pX/Chunk.chunkSize);
			if(playerX <= worldWidth - viewDistance && playerX >= viewDistance)
			{
				System.out.println("drugi");
				int leftmost = (playerX - viewDistance - 1 + (2*viewDistance+1/*offset so never negative*/))%(2*viewDistance+1);
				System.out.println(leftmost);
				/*
				int mid = playerZ;
				if(mid < viewDistance)
					mid = viewDistance;
				if(mid > worldWidth - viewDistance)
					mid = worldWidth - viewDistance;*/
				for(int i=0; i < chunks.length; i++)
				{
					chunks[leftmost][i].removeFromMemory();
					//chunks[leftmost][i].loadFromFile(playerX+viewDistance, 0, mid - viewDistance + i);
					chunks[leftmost][i].loadFromFile(playerX+viewDistance, 0, chunks[leftmost][i].chunkZ);
				}
			}
		}
		if(playerZ > (int)(pZ/Chunk.chunkSize))	//player moved FORWARD
		{
			playerZ = (int)(pZ/Chunk.chunkSize);	
			if(playerZ >= viewDistance && playerZ <= worldDepth - viewDistance)
			{
				System.out.println("tretji");
				int backmost = (playerZ + viewDistance+1)%(2*viewDistance+1);
				System.out.println(backmost);
				/*
				int mid = playerX;
				if(mid < viewDistance)
					mid = viewDistance;
				if(mid > worldDepth - viewDistance)
					mid = worldDepth - viewDistance;*/
				for(int i=0; i < chunks.length; i++)
				{
					chunks[i][backmost].removeFromMemory();
					//chunks[i][backmost].loadFromFile(mid - viewDistance + i, 0, playerZ-viewDistance);
					chunks[i][backmost].loadFromFile(chunks[i][backmost].chunkX, 0, playerZ-viewDistance);
				}
			}
		}
		else if(playerZ < (int)(pZ/Chunk.chunkSize))	//player moved BACKWARDS
		{
			playerZ = (int)(pZ/Chunk.chunkSize);
			if(playerZ >= viewDistance && playerZ <= worldDepth - viewDistance)
			{
				System.out.println("cetrti");
				int frontmost = (playerZ - viewDistance - 1 + (2*viewDistance+1/*offset so never negative*/))%(2*viewDistance+1);
				System.out.println(frontmost);
				/*
				int mid = playerX;
				if(mid < viewDistance)
					mid = viewDistance;
				if(mid > worldDepth - viewDistance)
					mid = worldDepth - viewDistance;*/
				for(int i=0; i < chunks.length; i++)
				{
					chunks[i][frontmost].removeFromMemory();
					//chunks[i][frontmost].loadFromFile(mid - viewDistance + i, 0, playerZ+viewDistance);
					chunks[i][frontmost].loadFromFile(chunks[i][frontmost].chunkX, 0, playerZ+viewDistance);
				}
			}
		}
		Shaders.recalculateShadowMatrices(playerX < viewDistance ? viewDistance : playerX > worldWidth - viewDistance ? worldWidth : playerX, playerZ < viewDistance ? viewDistance : playerZ > worldDepth - viewDistance ? worldDepth - viewDistance : playerZ, Chunk.chunkSize, viewDistance);
		Shaders.recalculateProjectorMatrices(playerX < viewDistance ? viewDistance : playerX > worldWidth - viewDistance ? worldWidth : playerX, playerZ < viewDistance ? viewDistance : playerZ > worldDepth - viewDistance ? worldDepth - viewDistance : playerZ, Chunk.chunkSize, 1);
		System.out.println("Changed chunks in: " + (System.nanoTime() - time)/1000000f);
		rerenderShadows();	
	}
	
	public static void render()
	{        
		for(int i=0; i < chunks.length; i++)
			for(int j=0; j < chunks[i].length; j++)
			{
				if(chunks[i][j] != null)
					Shaders.render(chunks[i][j].vertexBufferID, chunks[i][j].uvBufferID, chunks[i][j].normalBufferID, chunks[i][j].indexBufferID, chunks[i][j].tangentBufferID, chunks[i][j].bitangentBufferID, SpriteMap.texture.textureHandle, SpriteMap.specularMap.textureHandle, SpriteMap.normalMap.textureHandle, SpriteMap.projectionTexture.textureHandle, chunks[i][j].nelements);
			}

	}
	public static void renderWater()
	{
		for(int i=0; i < chunks.length; i++)
			for(int j=0; j < chunks[i].length; j++)
			{
				if(chunks[i][j] != null && chunks[i][j].waterElements != 0)
					Shaders.renderWater(chunks[i][j].waterVertexBufferID, chunks[i][j].waterUvBufferID, chunks[i][j].waterNormalBufferID, chunks[i][j].waterIndexBufferID, SpriteMap.texture.textureHandle, SpriteMap.specularMap.textureHandle, chunks[i][j].waterElements);
					
			}
	}

	public static void rerenderShadows()
	{
		Shaders.clearShadowDepth();
		
		for(int i=0; i < chunks.length; i++)
			for(int j=0; j < chunks[i].length; j++)
			{
				if(chunks[i][j] != null)
				{
					Shaders.renderShadows(chunks[i][j].vertexBufferID, chunks[i][j].indexBufferID, chunks[i][j].nelements);
					Shaders.renderProjectionDepth(chunks[i][j].vertexBufferID, chunks[i][j].indexBufferID, chunks[i][j].nelements);
				}
			}
	}
	
	public static void addBlock(int x, int y, int z, String texname)
	{
		int chunkX = x/Chunk.chunkSize - playerX;
		int chunkZ = z/Chunk.chunkSize - playerZ; 
		
		if(chunks[(playerX + chunkX)%(2*viewDistance+1)][(playerZ + chunkZ)%(2*viewDistance+1)].tryAdding(x, y, z, texname))
			rerenderShadows();
	}
	
	public static Box removeBlock(int x, int y, int z)
	{
		int chunkX = x/Chunk.chunkSize - playerX;
		int chunkZ = z/Chunk.chunkSize - playerZ; 
		
		if(chunks[(playerX + chunkX)%(2*viewDistance+1)][(playerZ + chunkZ)%(2*viewDistance+1)].remove(x, y, z))
		{
			rerenderShadows();
			System.out.println("A box should drop!");
			Box ret = new Box(chunks[(playerX + chunkX)%(2*viewDistance+1)][(playerZ + chunkZ)%(2*viewDistance+1)].tiles[x%Chunk.chunkSize][y%Chunk.chunkHeight][z%Chunk.chunkSize].textureName, 0.3f);
			
			Transform startTransform = new Transform();
			startTransform.origin.set(x, y, z);
			DefaultMotionState cms = new DefaultMotionState(startTransform);
			CollisionShape mobShape = new BoxShape(new Vector3f(0.15f, 0.15f, 0.15f));
			Vector3f localInertia = new Vector3f(0f, 0f, 0f);
			mobShape.calculateLocalInertia(0.2f, localInertia);
			RigidBodyConstructionInfo crbInfo = new RigidBodyConstructionInfo(1.f, cms, mobShape, localInertia);
			RigidBody body = new RigidBody(crbInfo);
			body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
			body.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
			body.setRestitution(0.3f);
			body.setFriction(0.5f);
			
			dynamicsWorld.addRigidBody(body);
			ret.body = body;
			body.setUserPointer(ret);
			ret.recalc();
			return ret;
		}
		return null;
	}
	
	public static void simulateAround(int x, int y, int z, int radius)	//x, y, z in world not chunk coords
	{
		if(x < radius)
			x = radius;
		if(y < radius)
			y = radius;
		if(z < radius)
			z = radius;
		if(x > worldWidth * Chunk.chunkSize - radius)
			x = worldWidth * Chunk.chunkSize - radius;
		if(z > worldDepth * Chunk.chunkSize - radius)
			z = worldDepth * Chunk.chunkSize - radius;
		int added = 0;
		int size = 2*viewDistance+1;
		
		//System.out.println("x, y, z " + x + " " + y + " " + z);
		for(int i=-radius; i <= radius; i++)//x
			for(int j=-radius; j <= radius; j++)//y
				for(int k=-radius; k <= radius; k++)//z
				{
					if(    chunks[((x+i)/Chunk.chunkSize)%(size)][((z+k)/Chunk.chunkSize)%(size)].tiles[(i+x)%Chunk.chunkSize][(j+y)%Chunk.chunkHeight][(z+k)%Chunk.chunkSize] == null 
						|| chunks[((x+i)/Chunk.chunkSize)%(size)][((z+k)/Chunk.chunkSize)%(size)].tiles[(i+x)%Chunk.chunkSize][(j+y)%Chunk.chunkHeight][(z+k)%Chunk.chunkSize].isActive == false
						|| chunks[((x+i)/Chunk.chunkSize)%(size)][((z+k)/Chunk.chunkSize)%(size)].tiles[(i+x)%Chunk.chunkSize][(j+y)%Chunk.chunkHeight][(z+k)%Chunk.chunkSize].isWater == true    
					  )
						continue;
					//chunks[((x+i)/Chunk.chunkSize + playerX)%(size)][((z+k)/Chunk.chunkSize + playerZ)%(viewDistance*2+1)].tiles[(i+x)%Chunk.chunkSize][(j+y)%Chunk.chunkHeight][(z+k)%Chunk.chunkSize].simulated = true;
					if(     !simulatedTilesPrev.contains(chunks[((x+i)/Chunk.chunkSize)%(size)][((z+k)/Chunk.chunkSize)%(size)].tiles[(i+x)%Chunk.chunkSize][(j+y)%Chunk.chunkHeight][(z+k)%Chunk.chunkSize])
						 && !simulatedTilesCurr.contains(chunks[((x+i)/Chunk.chunkSize)%(size)][((z+k)/Chunk.chunkSize)%(size)].tiles[(i+x)%Chunk.chunkSize][(j+y)%Chunk.chunkHeight][(z+k)%Chunk.chunkSize]))
					{
						dynamicsWorld.addRigidBody(chunks[((x+i)/Chunk.chunkSize)%(size)][((z+k)/Chunk.chunkSize)%(size)].tiles[(i+x)%Chunk.chunkSize][(j+y)%Chunk.chunkHeight][(z+k)%Chunk.chunkSize].body);
						Transform ctransform =     chunks[((x+i)/Chunk.chunkSize)%(size)][((z+k)/Chunk.chunkSize)%(size)].tiles[(i+x)%Chunk.chunkSize][(j+y)%Chunk.chunkHeight][(z+k)%Chunk.chunkSize].body.getWorldTransform(new Transform());
						//System.out.println("Chunk: " + ((x+i)/Chunk.chunkSize) + " " + ((z+k)/Chunk.chunkSize) + ", tile " + ((i+x)) + " " + ((j+y)) + " " + ((z+k)));
						//System.out.println("Added " + ctransform.origin.x + " " + ctransform.origin.y + " " + ctransform.origin.z);
						simulatedBlocks++;
						added++;
					}
					simulatedTilesCurr.add(chunks[((x+i)/Chunk.chunkSize)%(size)][((z+k)/Chunk.chunkSize)%(viewDistance*2+1)].tiles[(i+x)%Chunk.chunkSize][(j+y)%Chunk.chunkHeight][(z+k)%Chunk.chunkSize]);
				}
		if(added != 0)
			System.out.println("Added blocks: " + added);
	}
	
	public static void removeUnneededTiles()
	{
		/*System.out.println("Removal");
		System.out.println(simulatedTilesPrev.toArray().length);
		System.out.println(simulatedTilesCurr.toArray().length);*/
		simulatedTilesPrev.removeAll(simulatedTilesCurr);
		int removed =0;
		if(simulatedTilesPrev.toArray().length != 0)
			System.out.println("Removing " + simulatedTilesPrev.toArray().length + " tiles.");
		for(Tile curr : simulatedTilesPrev)
		{
			if(curr == null)
			{
				System.out.println("Curr == null for some reason.");
				continue;
			}
			dynamicsWorld.removeRigidBody(curr.body);
			//Transform pos = curr.body.getWorldTransform(new Transform());
			//System.out.println("Removed " + pos.origin.x + " " + pos.origin.y + " " + pos.origin.z);
			simulatedBlocks--;
			removed++;
		}
		
		HashSet <Tile>tmp = simulatedTilesCurr;
		simulatedTilesCurr = simulatedTilesPrev;
		simulatedTilesPrev = tmp;
		simulatedTilesCurr.clear();
		
		//simulatedTilesPrev = simulatedTilesCurr;
		//simulatedTilesCurr = new HashSet<Tile>();
		if(removed != 0)
			System.out.println("Simulated blocks: " + simulatedBlocks + " " + removed + " " + dynamicsWorld.getNumCollisionObjects());
	}
}

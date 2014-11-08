package mypackage;
import static org.lwjgl.opengl.GL11.glClearColor;
import glmodel.GLImage;
import glmodel.GLMaterialLib;
import glmodel.GLModel;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;











import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.CollisionFilterGroups;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.dispatch.GhostObject;
import com.bulletphysics.collision.dispatch.GhostPairCallback;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Vector3f;

public class risanje {
	protected static boolean isRunning = false;
	long oldtime=0;
	long newtime=0;
	
	ArrayList<Box> droppedItems;
	
	boolean trackMouseLeft = false;
	boolean trackMouseRight = false;
	
	int width=0;
	int depth=0;
	int table[][];
	//Box boxes[][];	//Box data
	ArrayList<Box> boxes;
	Box testbox;
	
	GLModel m_obj;
	
	gfxCardPoints gf;	//graphics data
	
	float dx=0;
	float dy=0;
	float dz=-25;
	float rx=0;
	float ry=0;
	float rz=0;
	float scale=0.05f;//make a camera ):
	float pioveroneeighty = (float)Math.PI / 180.f;
	
	Follower mobs[];
	boolean tpCamera = false;	//third person camera
	boolean camPressed = false; //button for tp camera pressed?
	boolean lmbPressed = false;
	boolean rmbPressed = false;
	
	boolean simPhys = true;
	boolean simPressed = false;
	boolean drawWorld = true;
	boolean drawPressed = false;
	
	boolean tPressed = false;
	boolean timePassing = true;
	long timeSkipped = 0;
	
	float camDistance = 1.f;	//distance from char to cam
	
	int blockSelected = 1; //1 = grass, 2 = dirt, 3 = stone.
	
	Inventory inventory;
	
	DefaultCollisionConfiguration colConf;
	CollisionDispatcher dispatcher;
	ConstraintSolver solver;
	BroadphaseInterface broadphase;
	DynamicsWorld dynamicsWorld;
	
	public static void main(String[] args)
	{
		(new risanje()).execute();
	}
	
	protected void execute()
	{
		try
		{
			initDisplay();
			boxes = new ArrayList<Box>();
			droppedItems = new ArrayList<Box>();
			
			File f = new File("textures.txt");
			Scanner sc = new Scanner(f);
			
			while(sc.hasNext())
			{
				String n = sc.next();
				GLImage tex = new GLImage();
				tex.textureHandle = GLMaterialLib.makeTexture(n);
				gfxRenderedBox.textureMap.put(n, tex);
				System.out.println("Texture with ID " + tex.textureHandle + " added!");
			}
			
			
			sc.close();
			
			/*
			f = new File("data.obf");
			sc = new Scanner(f);;
			
			//boxes = new Box[width][];
			//for(int i=0; i < width; i++)
			//	boxes[i] = new Box[height];
			while(sc.hasNext()){
				int x = sc.nextInt();
				if(x > width)
					width = x;
				int y = sc.nextInt();
				int z = sc.nextInt();
				if(z > depth)
					depth = z;
				//int c = sc.nextInt();
				String texturename = sc.next();
				//System.out.println(x + " " + y + " " + z + " " + texturename);
				Box cbox = new Box(texturename);
				cbox.m_nX = x;
				cbox.m_nY = y;
				cbox.m_nZ = z;
				boxes.add(cbox);
				//-----------
				break;
				
				//--------------
			}
			sc.close();*/
			System.out.println("width: " + width + "  depth: " + depth);
			//gf = new gfxCardPoints(boxes);
		}catch(LWJGLException e)
		{
			System.err.println("Can't open display");
			e.printStackTrace();
			System.exit(0);
		}catch(Exception e)
		{
			e.printStackTrace();
			//System.exit(0);
		}
		
		
		initPhysics();
		System.out.println("Adding stone.jpg");
		gfxRenderedBox.addTexture("stone.jpg");
		System.out.println("Added stone.jpg");
		risanje.isRunning = true;
		mainLoop();
		Display.destroy();
	}
	
	private void initPhysics()
	{
		System.out.println("initializing physics!");
		colConf = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(colConf);
		broadphase = new DbvtBroadphase();
		solver = new SequentialImpulseConstraintSolver();
		

		
		broadphase.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
		
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, colConf);
		
		dynamicsWorld.setGravity(new Vector3f(0f, -20f, 0f));
		
		//CollisionShape groundShape = new BoxShape(new Vector3f(width/2f, 100, height/2f));
		
		/*
		Transform groundTransform = new Transform();
		CollisionShape groundShape = new BoxShape(new Vector3f(width/2f, 20, depth/2f));
		groundTransform.setIdentity();
		groundTransform.origin.set(width/2f, 20, depth/2f);
		DefaultMotionState ms = new DefaultMotionState(groundTransform);
		RigidBodyConstructionInfo grbInfo = new RigidBodyConstructionInfo(0.f, ms, groundShape, new Vector3f(0f, 0f, 0f));
		RigidBody groundBody = new RigidBody(grbInfo);
		dynamicsWorld.addRigidBody(groundBody);
		*/
		
		CollisionShape colShape = new BoxShape(new Vector3f(.5f, .5f, .5f));

		Transform startTransform = new Transform();
		startTransform.setIdentity();
		
		//for(int i=0; i < boxes.length; i++)
			//for(int j=0; j < boxes[i].length; j++)
		for(int i=0; i < boxes.size(); i++)	
		{	
			Box cBox = boxes.get(i);
			startTransform.origin.set(cBox.m_nX, cBox.m_nY, cBox.m_nZ);
			DefaultMotionState cms = new DefaultMotionState(startTransform);
			RigidBodyConstructionInfo crbInfo = new RigidBodyConstructionInfo(0, cms, colShape, new Vector3f(0.f, 0.f, 0.f));
			RigidBody body = new RigidBody(crbInfo);
			//body.setActivationState(RigidBody.WANTS_DEACTIVATION);
			body.setActivationState(RigidBody.DISABLE_SIMULATION);
			body.setCollisionFlags(CollisionFlags.STATIC_OBJECT);
			body.setRestitution(0f);
			//body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
			dynamicsWorld.addRigidBody(body);
			cBox.body = body;
			body.setUserPointer(cBox);
		}
		/// TEST 
		
		testbox = new Box("playa.jpg");
		
		{
			startTransform.origin.set(156f, 10f, 156f);
			DefaultMotionState cms = new DefaultMotionState(startTransform);
			//CollisionShape playerShape = new SphereShape(0.5f);
			CollisionShape playerShape = new CylinderShape(new Vector3f(0.40f, 0.40f, 0.45f));
			//CollisionShape playerShape = new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f));
			//CollisionShape playerShape = new CapsuleShape(0.5f, 1f);
			Vector3f localInertia = new Vector3f(0f, 0f, 0f);
			playerShape.calculateLocalInertia(1.f, localInertia);
			RigidBodyConstructionInfo crbInfo = new RigidBodyConstructionInfo(1.f, cms, playerShape, localInertia);
			RigidBody body = new RigidBody(crbInfo);
			body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
			body.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
			body.setRestitution(0.f);
			body.setDamping(0.f, 100.f);
			body.setAngularFactor(0);
			body.setFriction(0f);
			
			GhostObject ghost = new PairCachingGhostObject();
			ghost.setWorldTransform(startTransform);
			ghost.setCollisionShape(playerShape);
			ghost.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
			
			dynamicsWorld.addRigidBody(body);
			dynamicsWorld.addCollisionObject(ghost, (short)CollisionFilterGroups.CHARACTER_FILTER, (short)CollisionFilterGroups.STATIC_FILTER);
			
			testbox.body = body;
			testbox.ghost = ghost;
			body.setUserPointer(testbox);
			ghost.setUserPointer(testbox);
		}
		testbox.recalc();
		mobs = new Follower[1];
		Random r = new Random();
		for(int i=0; i < mobs.length; i++)
		{
			float x = testbox.m_nX;
			float z = testbox.m_nZ;
			mobs[i] = new Follower("mob.jpg");
			mobs[i].m_nX = r.nextFloat()*90-45 + x;
			mobs[i].m_nZ = r.nextFloat()*90-45 + z;
			mobs[i].m_nY = 8;
		}
		
		for(int i=0; i < mobs.length; i++)
		{
			startTransform.origin.set(mobs[i].m_nX, mobs[i].m_nY, mobs[i].m_nZ);
			DefaultMotionState cms = new DefaultMotionState(startTransform);
			CollisionShape mobShape = new CylinderShape(new Vector3f(0.50f, 0.50f, 0.50f));
			Vector3f localInertia = new Vector3f(0f, 0f, 0f);
			mobShape.calculateLocalInertia(1.f, localInertia);
			RigidBodyConstructionInfo crbInfo = new RigidBodyConstructionInfo(1.f, cms, mobShape, localInertia);
			RigidBody body = new RigidBody(crbInfo);
			body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
			body.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
			body.setRestitution(0.f);
			body.setDamping(0.f, 100.f);
			body.setAngularFactor(0);
			body.setFriction(0.1f);
			
			dynamicsWorld.addRigidBody(body);
			mobs[i].body = body;
			body.setUserPointer(mobs[i]);
		}
		
		/// TEST
		BulletStats.gNumDeepPenetrationChecks  = 0;
		BulletStats.gNumGjkChecks = 0;
		
		int numObjects = 0;
		if(dynamicsWorld!=null)
		{
			dynamicsWorld.stepSimulation(1.f/60.f, 0);
			numObjects = dynamicsWorld.getNumCollisionObjects();
		}
		
		for(int i=0; i < numObjects; i++)
		{
			CollisionObject cObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
			RigidBody body = RigidBody.upcast(cObj);
			if(body != null)
			{
				if(body.getMotionState() != null)
				{
					DefaultMotionState cms = (DefaultMotionState)body.getMotionState();
					cms.graphicsWorldTrans.set(cms.startWorldTrans);
					cObj.setWorldTransform(cms.graphicsWorldTrans);
					cObj.setInterpolationWorldTransform(cms.startWorldTrans);
					cObj.activate();
				}
				dynamicsWorld.getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(cObj.getBroadphaseHandle(), dynamicsWorld.getDispatcher());
				
				body = RigidBody.upcast(cObj);
				if(body != null && !body.isStaticObject())
				{
					RigidBody.upcast(cObj).setLinearVelocity(new Vector3f(0f, 0f, 0f));
					RigidBody.upcast(cObj).setAngularVelocity(new Vector3f(0f, 0f, 0f));
				}
				
			}
		}
		System.out.println("Done!");
	}
		
	protected void mainLoop()
	{

	
		setupView();
		SpriteMap.initStatic();
		Shaders.init();
		inventory = new Inventory();
		
		Robert.init(5, testbox.m_nX, testbox.m_nZ, 33, 33, dynamicsWorld); 	//55 je testno! make dynamic!
		

		
		double totalTime = 0;
		int ticks = 1;
		newtime = System.nanoTime();
		System.out.println("#sim objects: " + dynamicsWorld.getNumCollisionObjects());
		while(risanje.isRunning)
		{
			oldtime = newtime;
			newtime = System.nanoTime();
			float dt = newtime-oldtime;
			dt = dt/1000000000;
			dt = dt/1;
			//System.out.println(dt);
			totalTime+=dt;
			ticks++;
			if(totalTime > 1)
			{
				System.out.println(ticks/totalTime);
				ticks = 1;
				totalTime = 0;
			}
			if(drawWorld)
			{
				resetView();
				renderFrame();
			}
			processInput();
			if(simPhys)
			{
				dynamicsWorld.stepSimulation(dt, 2, 1/80f);
			}
			
			/*
			for(int i=0; i < boxes.length; i++)
				for(int j=0; j < boxes[i].length; j++)
					if(boxes[i][j].m_nY > 19 && boxes[i][j].m_nY < 24)
						System.out.println(boxes[i][j].m_nX + " " + boxes[i][j].m_nY + " " + boxes[i][j].m_nZ);
			*/
			
			testbox.recalc();
			for(Box cBox: droppedItems)
				cBox.recalc();
			//System.out.println("TESTBOX: " + testbox.m_nX + " " + testbox.m_nY + " " + testbox.m_nZ);
			Robert.movePlayer(testbox.m_nX, testbox.m_nZ);
			Robert.simulateAround((int)testbox.m_nX, (int)testbox.m_nY, (int)testbox.m_nZ, 4);
			for(int i=0; i < droppedItems.size(); i++)
			{
				Box cBox = droppedItems.get(i);
				Robert.simulateAround((int)cBox.m_nX, (int)cBox.m_nY, (int)cBox.m_nZ, 1);
				Vector3f dist = new Vector3f(testbox.m_nX, testbox.m_nY, testbox.m_nZ);
				dist.x-=cBox.m_nX;
				dist.y-=cBox.m_nY;
				dist.z-=cBox.m_nZ;
				if(dist.length() > 50)
				{
					droppedItems.remove(i);
					dynamicsWorld.removeCollisionObject(cBox.body);
					i--;
				}
				else if(dist.length() < 1.2f)
				{
					if(inventory.pickup(cBox))
					{
						droppedItems.remove(i);
						dynamicsWorld.removeCollisionObject(cBox.body);
						i--;
						inventory.print();
					}
				}
				else if(dist.length() < 3)
				{
					if(inventory.canPickup(cBox))
					{
						dist.normalize();
						cBox.body.applyCentralImpulse(dist);
					}
				}
			}
			for(int i=0; i < mobs.length; i++)
			{
				
				Robert.simulateAround((int)mobs[i].m_nX, (int)mobs[i].m_nY, (int)mobs[i].m_nZ, 1);
			}
			
			//System.out.println("Dt: " + dt);
			/*
			System.out.println("tb " + testbox.m_nX + " " + testbox.m_nY + " " + testbox.m_nZ);
			Vector3f rayvector = new Vector3f(testbox.m_nX, testbox.m_nY, testbox.m_nZ);
			rayvector.x += 3.f * Math.sin(ry * pioveroneeighty);
			rayvector.z -= 3.f * Math.cos(ry * pioveroneeighty);
			rayvector.y -= 3.f * Math.sin(rx * pioveroneeighty);
			System.out.println("rv " + rayvector.x + " " + rayvector.y + " " + rayvector.z + "\n");
			*/
			
			
			//Vector3f out = new Vector3f();
			//testbox.body.getAngularVelocity(out);
			//System.out.println(out.x + " " + out.y + " " + out.z);
			
			/*
			Quat4f rot = new Quat4f();
			testbox.body.getOrientation(rot);
			System.out.println(rot.x + " " + rot.y + " " + rot.z);
			*/
			
			//System.out.println(testbox.m_rX + " " + testbox.m_rY + " " + testbox.m_rZ);
			//System.out.println(testbox.m_nX + " " + testbox.m_nY + " " + testbox.m_nZ);
			for(int i=0; i < mobs.length; i++)
				mobs[i].recalc(new Vector3f(testbox.m_nX, testbox.m_nY, testbox.m_nZ));
			Robert.removeUnneededTiles();
			Display.update();
			//Display.sync(60);
		}
		System.out.println("Average frame time: "+(totalTime/ticks));
		System.out.println("Average load time: " + (Chunk.loadTime / Chunk.loadNum));
		System.out.println("Average model calc time: " + (Chunk.calcTime / Chunk.calcNum));
	}
	
	protected void setupView()
	{
		Mouse.setGrabbed(true);
		
	    GL11.glEnable(GL11.GL_CULL_FACE);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);

	    GL11.glViewport(0, 0, 1024, 768);

	    GL11.glMatrixMode(GL11.GL_PROJECTION);
	    GL11.glLoadIdentity();
	    GLU.gluPerspective(45, 1024 / (float)768, 0.01f, 10.0f);
		
	    // model view stack 
	    GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    GL11.glLoadIdentity();
	    // setup view space;     
	}
	
	protected void resetView()
	{
		
	    //glClearColor(0.5f, 0.1f, 0f, 1f);
		glClearColor(0.f, 0.f, 0.f, 1.f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	        
	    GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    GL11.glLoadIdentity();
	    
	    float translateZ = 0;
	    
	    if(tpCamera)
	    {
	    	Vector3f rayvector = new Vector3f(testbox.m_nX, testbox.m_nY, testbox.m_nZ);
			Vector3f posvector = new Vector3f(rayvector);
			float length = 40.f;	//make 1 before release.
			rayvector.x -= length * 10 * Math.sin(ry * pioveroneeighty) * camDistance;
			rayvector.z += length * 10 * Math.cos(ry * pioveroneeighty) * camDistance;
			rayvector.y += length * 10 * Math.sin(rx * pioveroneeighty) * camDistance;
			
			ClosestRayResultCallback rcb = new ClosestRayResultCallback(posvector, rayvector);
			try
			{
				dynamicsWorld.rayTest(posvector, rayvector, rcb);
				if(rcb.hasHit() && rcb.collisionObject.getUserPointer() instanceof Tile)
				{
					//System.out.println("WE HIT A BLOCK WITH THE CAMERA!!");
					//System.out.println(rcb.hitNormalWorld.x + " " + rcb.hitNormalWorld.y + " " + rcb.hitNormalWorld.z);
					//System.out.println(rcb.hitPointWorld.x 	+ " " + rcb.hitPointWorld.y  + " " + rcb.hitPointWorld.z);
					//System.out.println(rcb.rayFromWorld.x 	+ " " + rcb.rayFromWorld.y 	 + " " + rcb.rayFromWorld.z);
					//System.out.println(rcb.rayToWorld.x 	+ " " + rcb.rayToWorld.y 	 + " " + rcb.rayToWorld.z + "\n");
					
					Vector3f dif = new Vector3f();
					dif.x = posvector.x - rcb.hitPointWorld.x;
					dif.y = posvector.y - rcb.hitPointWorld.y;
					dif.z = posvector.z - rcb.hitPointWorld.z;
					//GL11.glTranslatef(0f, 0f, -dif.length()/10);
					translateZ = -dif.length()/20;
					//GL11.glTranslatef(0f, 0f, -length*camDistance);
				} 
				else
					translateZ = -length*camDistance/2;
					//GL11.glTranslatef(0f, 0f, -length*camDistance);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	    }
	    /*
	    GL11.glTranslatef(0, 0, translateZ);
	    GL11.glRotatef(rx, 1, 0, 0);
	    GL11.glRotatef(ry, 0, 1, 0);
	    GL11.glRotatef(rz, 0, 0, 1);
	    
	    GL11.glScalef(0.1f, 0.1f, 0.1f);
	    GL11.glTranslatef(-testbox.m_nX, -testbox.m_nY-1, -testbox.m_nZ);
	    */
	    
	    Shaders.recalculateRenderMatrices(rx, ry, rz, 0.1f, 0f, 0f, translateZ, -testbox.m_nX, -testbox.m_nY-1, -testbox.m_nZ);
	    //void recalculateRenderMatrices(float rx, float ry, float rz, float scale, float translatePRX, float translatePRY, float translatePRZ, float translateX, float translateY, float translateZ)
		
	    
	}
	
	protected void renderFrame()
	{

	    
		//gf.render();

		//testchunk1.render();
		//testchunk2.render();
		if(!timePassing)
			timeSkipped += newtime-oldtime;
		Shaders.sendTime(newtime-timeSkipped);
		
		//Robert.rerenderShadows();
		Shaders.renderDynamicShadows(testbox.graphics.vertexBufferID, testbox.graphics.indicesBufferID, testbox.graphics.nelements, testbox.m_nX, testbox.m_nY, testbox.m_nZ);
		
		Robert.render();
		Robert.renderWater();
		Shaders.renderDynamic(testbox.graphics.vertexBufferID, testbox.graphics.textureBufferID, testbox.graphics.normalBufferID, testbox.graphics.indicesBufferID, testbox.graphics.tex.textureHandle, testbox.graphics.specMap.textureHandle, testbox.graphics.nelements, testbox.m_nX, testbox.m_nY, testbox.m_nZ);
		
		for(Box cBox : droppedItems)
		{
			Shaders.renderDynamic(cBox.graphics.vertexBufferID, cBox.graphics.textureBufferID, cBox.graphics.normalBufferID, cBox.graphics.indicesBufferID, cBox.graphics.tex.textureHandle, cBox.graphics.specMap.textureHandle, cBox.graphics.nelements, cBox.m_nX, cBox.m_nY, cBox.m_nZ);
		}
		for(int i=0; i < mobs.length; i++)
		{
			Shaders.renderDynamic(mobs[i].graphics.vertexBufferID, mobs[i].graphics.textureBufferID, mobs[i].graphics.normalBufferID, mobs[i].graphics.indicesBufferID, mobs[i].graphics.tex.textureHandle, mobs[i].graphics.specMap.textureHandle, mobs[i].graphics.nelements, mobs[i].m_nX, mobs[i].m_nY, mobs[i].m_nZ);
			//	mobs[i].render3D();
		}
		/*
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, testbox.graphics.vertexBufferID);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0L);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, testbox.graphics.indicesBufferID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, testbox.graphics.textureBufferID);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
        
        
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, testbox.graphics.tex.textureHandle);
        //testbox.render3D();
        
        
        
        
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mobs[0].graphics.tex.textureHandle);
		
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        */
        
		/*
        if(blockSelected == 1)
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, gfxRenderedBox.textureMap.get("grass.jpg").textureHandle);
        else if(blockSelected == 2)
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, gfxRenderedBox.textureMap.get("dirt.jpg").textureHandle);
        else if(blockSelected == 3)
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, gfxRenderedBox.textureMap.get("stone.jpg").textureHandle);
        
        
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 1024, 768, 0, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glBegin(GL11.GL_QUADS);
          GL11.glTexCoord2f(0.666f, 0.666f);
          GL11.glVertex2f(497, 740); 
          
          GL11.glTexCoord2f(0.666f, 1f);
          GL11.glVertex2f(497, 710); 
          
          GL11.glTexCoord2f(1f, 1f);
          GL11.glVertex2f(527, 710); 
          
          GL11.glTexCoord2f(1f, 0.666f);
          GL11.glVertex2f(527, 740); 
        GL11.glEnd();
        
        
        
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        */
        //inventory.render();
        
        
	    //GL11.glTranslatef(testbox.m_nX, testbox.m_nY, testbox.m_nZ);
	    //m_obj.render();
	}
	
	protected void processInput()
	{
		if(Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			risanje.isRunning = false;
		}
		if(true)
			return;

		double dt = newtime - oldtime;
		dt = dt/10000000;
		boolean movingLR = false;
		boolean movingUD = false;
		
		Vector3f moveVector = new Vector3f(0f, 0f, 0f);
		
		
		//System.out.println(ry*pioveroneeighty + " " );
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			//testbox.body.applyCentralImpulse(new Vector3f(0.f, 0.f, -1.f));
			moveVector.z -= 1.f * Math.cos(ry*pioveroneeighty);
			moveVector.x += 1.f * Math.sin(ry*pioveroneeighty);
			movingUD = true;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			//testbox.body.applyCentralImpulse(new Vector3f(0.f, 0.f, 1.f));
			moveVector.z += 1.f * Math.cos(ry*pioveroneeighty);
			moveVector.x -= 1.f * Math.sin(ry*pioveroneeighty);
			
			movingUD = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			//testbox.body.applyCentralImpulse(new Vector3f(-1.f, 0.f, 0.f));
			moveVector.z -= 1.f * Math.sin(ry*pioveroneeighty);
			moveVector.x -= 1.f * Math.cos(ry*pioveroneeighty);
			
			movingLR = true;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			//testbox.body.applyCentralImpulse(new Vector3f(1.f, 0.f, 0.f));
			moveVector.z += 1.f * Math.sin(ry*pioveroneeighty);
			moveVector.x += 1.f * Math.cos(ry*pioveroneeighty);
			movingLR = true;
		}
		
		if(testbox.canJump && Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			moveVector.y += 1f;
			testbox.jumping = true;
		}
		
		
		
		moveVector.normalize();
		moveVector.scale(10);
		if(moveVector.length() > 0.1)
			testbox.body.applyCentralImpulse(moveVector);
		//	testbox.body.setLinearVelocity(moveVector);
		//testbox.body.setAngularVelocity(new Vector3f(0f, 0f, 0f));
		
		
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && testbox.canJump)
		{
			testbox.body.applyCentralImpulse(new Vector3f(0.f, 1.f, 0.f));
		}

		Vector3f out = new Vector3f();
		testbox.body.getLinearVelocity(out);
		
		if(out.y < 0 && testbox.jumping)
			testbox.jumping = false;
		
		/*
		if(Math.abs(out.x) > 0.5 || Math.abs(out.y) > 0.5)
		{
			out.normalize();
			out.scale(10);
		}*/
		Vector3f ref = new Vector3f(out);
		ref.y = 0;
	
		if(ref.length() > 10)
		{
			ref.normalize();
			ref.scale(10);
		}
		ref.y = out.y;

		if(!testbox.jumping && ref.y > 0)
		{
			ref.y = 0;
		}
		else if(ref.y > 8)
			ref.y = 8;
		
		if(movingUD || movingLR)
		{
			testbox.body.setLinearVelocity(ref);
		}
		else 
			testbox.body.setLinearVelocity(new Vector3f(0.f, ref.y, 0.f));


		/*
	    if (Keyboard.isKeyDown(Keyboard.KEY_HOME))		//zoom
	      dz-=dt*.3;
	    if (Keyboard.isKeyDown(Keyboard.KEY_END))
	      dz+=dt*.3;
	    if (Keyboard.isKeyDown(Keyboard.KEY_W) && scale < 3)	//scale
	    {
	      scale+=0.01*dt;     
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_S) && scale > 0.05)
	    {
	      scale-=0.01*dt;    
	    }*/
	    /*
	     * Mouse.isButtonDown(0) = LMB;
	     * Mouse.isButtonDown(1) = RMB;
	     * Mouse.isButtonDown(2) = MMB;
	     */
	    
	    if(Mouse.isButtonDown(1) && !rmbPressed)	//destroy a block
	    {
	    	rmbPressed = true;
			Vector3f rayvector = new Vector3f(testbox.m_nX, testbox.m_nY+1, testbox.m_nZ);
			Vector3f posvector = new Vector3f(rayvector);
			Vector3f dirVector = new Vector3f();
			dirVector.x += 4.f * Math.sin(ry * pioveroneeighty);
			dirVector.z -= 4.f * Math.cos(ry * pioveroneeighty);
			dirVector.y -= 4.f * Math.sin(rx * pioveroneeighty);
			dirVector.normalize();
			dirVector.scale(4);
			
			rayvector.add(dirVector);
			
			ClosestRayResultCallback rcb = new ClosestRayResultCallback(posvector, rayvector);
			dynamicsWorld.rayTest(posvector, rayvector, rcb);
			if(rcb.hasHit())
			{
				
				System.out.println("WE HIT A BLOCK!");
				System.out.println(rcb.hitNormalWorld.x + " " + rcb.hitNormalWorld.y + " " + rcb.hitNormalWorld.z);
				System.out.println(rcb.hitPointWorld.x 	+ " " + rcb.hitPointWorld.y  + " " + rcb.hitPointWorld.z);
				System.out.println(rcb.rayFromWorld.x 	+ " " + rcb.rayFromWorld.y 	 + " " + rcb.rayFromWorld.z);
				System.out.println(rcb.rayToWorld.x 	+ " " + rcb.rayToWorld.y 	 + " " + rcb.rayToWorld.z + "\n");
				RigidBody hitBody = RigidBody.upcast(rcb.collisionObject);
				
				if(hitBody != null)
				{
					if(hitBody.getUserPointer() instanceof Follower)
					{
						System.out.println("HIT FOLLOWER");
						Follower cF = (Follower)hitBody.getUserPointer();
						Transform pos = cF.body.getWorldTransform(new Transform());
						
						Random r = new Random();
						
						pos.origin.x = r.nextFloat()*90-45 + testbox.m_nX;
						pos.origin.y = r.nextFloat()*90-45 + testbox.m_nZ;
						pos.origin.z = 8;
						
						System.out.println(pos.origin.x + " " + pos.origin.y + " " + pos.origin.z);
						cF.body.setWorldTransform(pos);
						cF.body.setLinearVelocity(new Vector3f(0, 0, 0));
					}
					else if(hitBody.getUserPointer() instanceof Tile)
					{
						/*
						System.out.println("HIT BOX!");
						Box cBox = (Box)hitBody.getUserPointer();
						boxes.remove(cBox);
						dynamicsWorld.removeCollisionObject(hitBody);*/
						Transform t = hitBody.getWorldTransform(new Transform());
						Vector3f pos = t.origin;
						
						Box ret = Robert.removeBlock((int)pos.x, (int)pos.y, (int)pos.z);
						if(ret != null)
						{
							droppedItems.add(ret);
							System.out.println("removed " + pos.x + " " + pos.y + " " + pos.z);
						}
					}
					
				}
			} 
	    }
	    else if(!Mouse.isButtonDown(1))
	    {
	    	rmbPressed = false;
	    }
	    
	    if(Mouse.isButtonDown(0) && !lmbPressed && inventory.slots[inventory.selectedSlot].count > 0) //add a block
	    {
	    	lmbPressed = true;
	    	Vector3f rayvector = new Vector3f(testbox.m_nX, testbox.m_nY+0.99f, testbox.m_nZ);
	    	//Vector3f rayvector = new Vector3f(testbox.m_nX, testbox.m_nY, testbox.m_nZ);
			Vector3f posvector = new Vector3f(rayvector);
			Vector3f dirVector = new Vector3f();
			dirVector.x += 4.f * Math.sin(ry * pioveroneeighty);
			dirVector.z -= 4.f * Math.cos(ry * pioveroneeighty);
			dirVector.y -= 4.f * Math.sin(rx * pioveroneeighty);
			dirVector.normalize();
			dirVector.scale(4);
			
			rayvector.add(dirVector);
			
			ClosestRayResultCallback rcb = new ClosestRayResultCallback(posvector, rayvector);
			dynamicsWorld.rayTest(posvector, rayvector, rcb);
			if(rcb.hasHit())
			{
				System.out.println("Adding a block");
				System.out.println(rcb.hitNormalWorld.x + " " + rcb.hitNormalWorld.y + " " + rcb.hitNormalWorld.z);
				System.out.println(rcb.hitPointWorld.x 	+ " " + rcb.hitPointWorld.y  + " " + rcb.hitPointWorld.z);
				System.out.println(rcb.rayFromWorld.x 	+ " " + rcb.rayFromWorld.y 	 + " " + rcb.rayFromWorld.z);
				System.out.println(rcb.rayToWorld.x 	+ " " + rcb.rayToWorld.y 	 + " " + rcb.rayToWorld.z + "\n");
				RigidBody hitBody = RigidBody.upcast(rcb.collisionObject);
				
				if(hitBody != null)
				{
					if(hitBody.getUserPointer() instanceof Tile)
					{
						Transform t = hitBody.getWorldTransform(new Transform());
						Vector3f pos = t.origin;
						
						Vector3f something = new Vector3f(pos);
						something.sub(rcb.hitPointWorld);
						
						
						if(Math.abs(something.x) > Math.abs(something.y) && Math.abs(something.x) > Math.abs(something.z))
						{
							if(something.x < 0)	pos.x+=1;
							else pos.x-=1;
						}
						if(Math.abs(something.y) > Math.abs(something.x) && Math.abs(something.y) > Math.abs(something.z))
						{
							if(something.y < 0) pos.y+=1;
							else pos.y-=1;
						}
						if(Math.abs(something.z) > Math.abs(something.x) && Math.abs(something.z) > Math.abs(something.y))
						{
							if(something.z < 0) pos.z+=1;
							else pos.z-=1;
						}
						
						System.out.println("pos - hit point " + something.x + " " + something.y + " " + something.z);
						//pos.y += 1;
						
						Robert.addBlock((int)pos.x, (int)pos.y, (int)pos.z, inventory.slots[inventory.selectedSlot].texname);
						inventory.remove();
						inventory.print();
					}
				}
			}
	    }
	    else if(!Mouse.isButtonDown(0))
	    {
	    	lmbPressed = false;
	    }
	    
	    if(Keyboard.isKeyDown(Keyboard.KEY_1))
	    {
	    	inventory.selectedSlot = 0;
	    }
	    else if(Keyboard.isKeyDown(Keyboard.KEY_2))
	    {
	    	inventory.selectedSlot = 1;
	    }
	    else if(Keyboard.isKeyDown(Keyboard.KEY_3))
	    {
	    	inventory.selectedSlot = 2;
	    }
	    else if(Keyboard.isKeyDown(Keyboard.KEY_4))
	    {
	    	inventory.selectedSlot = 3;
	    }
	    else if(Keyboard.isKeyDown(Keyboard.KEY_5))
	    {
	    	inventory.selectedSlot = 4;
	    }
	    else if(Keyboard.isKeyDown(Keyboard.KEY_6))
	    {
	    	inventory.selectedSlot = 5;
	    }
	    else if(Keyboard.isKeyDown(Keyboard.KEY_7))
	    {
	    	inventory.selectedSlot = 6;
	    }
	    else if(Keyboard.isKeyDown(Keyboard.KEY_8))
	    {
	    	inventory.selectedSlot = 7;
	    }
	    else if(Keyboard.isKeyDown(Keyboard.KEY_9))
	    {
	    	inventory.selectedSlot = 8;
	    }
	    
	    
	    if(tpCamera)
	    {	
	    	int d = Mouse.getDWheel();
	    	//System.out.println(Mouse.getDWheel() + " " + d + " " + (d/1200f) + " " + camDistance);
		    camDistance -= d/1200.f;
		    if(camDistance > 1.f)
		    	camDistance = 1.f;
		    if(camDistance < 0.1f)
		    {
		    	camDistance = 0.1f;
		    	tpCamera = false;
		    }
	    }
	    else if(Mouse.getDWheel() < 0)
	    {
	    	tpCamera = true;
	    	camDistance = 0.1f;
	    }
	    	
	    ry+=Mouse.getDX()/10f;
	    rx-=Mouse.getDY()/10f;
	    if(rx < -90)
	    	rx = -90;
	    if(rx > 90)
	    	rx = 90;
	    
	    if(Keyboard.isKeyDown(Keyboard.KEY_F1) && !camPressed)
	    {
	    	tpCamera = !tpCamera;
	    	camPressed = true;
	    }
	    else if(!Keyboard.isKeyDown(Keyboard.KEY_F1) && camPressed)
	    {
	    	camPressed = false;
	    }
	    
	    if(Keyboard.isKeyDown(Keyboard.KEY_F9) && !simPressed)
	    {
	    	simPhys = !simPhys;
	    	simPressed = true;
	    }
	    else if(!Keyboard.isKeyDown(Keyboard.KEY_F9) && simPressed)
	    	simPressed = false;
	    
	    if(Keyboard.isKeyDown(Keyboard.KEY_F10)&& !drawPressed)
	    {
	    	drawWorld = !drawWorld;
	    	drawPressed = true;
	    }
	    else if(!Keyboard.isKeyDown(Keyboard.KEY_F10) && drawPressed)
	    	drawPressed = false;
	    
	    if(Keyboard.isKeyDown(Keyboard.KEY_T) && !tPressed)
	    {
	    	timePassing = !timePassing;
	    	tPressed = true;
	    }
	    else if(!Keyboard.isKeyDown(Keyboard.KEY_T))
	    	tPressed = false;
	    
	    
	    if(scale < 0.05)
	    	scale = 0.05f;
	    if(scale > 3)
	    	scale = 3;
	}
	
	protected void initDisplay() throws LWJGLException
	{
		DisplayMode bestMode = null;
		DisplayMode[] dm = Display.getAvailableDisplayModes();
		for(int i=0; i < dm.length; i++)
		{
			DisplayMode mode = dm[i];
			if(mode.getWidth() == 1024 && mode.getHeight() == 768 && mode.getFrequency() <= 85)
			{
				if(bestMode == null || (mode.getBitsPerPixel() >= bestMode.getBitsPerPixel() && mode.getFrequency() > bestMode.getFrequency()))
					bestMode = mode;
			}
		}
		Display.setDisplayMode(bestMode);
		Display.create(new PixelFormat(8, 8, 8, 4));
		
		Display.setTitle("MINECRAFT!");
	}

	  public static FloatBuffer allocFloats(float[] floatarray)
	  {
	    FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * 4).order(
	        ByteOrder.nativeOrder()).asFloatBuffer();
	    fb.put(floatarray).flip();
	    return fb;
	  }
}

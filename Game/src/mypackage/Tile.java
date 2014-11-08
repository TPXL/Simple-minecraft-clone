package mypackage;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
/*
 * A static block.
 */
import com.bulletphysics.linearmath.Transform;

public class Tile {	
	public RigidBody body;
	public boolean isActive;	//it should be drawn and simulated if true, if false it's just sitting there
	public String textureName;
	public boolean isWater;
	
	public Tile(String texname, int x, int y, int z)
	{
		CollisionShape colShape = new BoxShape(new Vector3f(.5f, .5f, .5f));
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		startTransform.origin.set(x, y, z);
		DefaultMotionState cms = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo crbInfo = new RigidBodyConstructionInfo(0, cms, colShape, new Vector3f(0.f, 0.f, 0.f));
		body = new RigidBody(crbInfo);
		body.setActivationState(RigidBody.DISABLE_SIMULATION);
		body.setCollisionFlags(CollisionFlags.STATIC_OBJECT);
		body.setRestitution(0f);
		body.setUserPointer(this);
		
		textureName = texname;
		if(textureName.equals("lake.jpg") || textureName.equals("sea.jpg"))
			isWater = true;
		else
			isWater = false;
		isActive = true;
	}
	
	void remove()
	{
		//body.setUserPointer(null);
		//body.destroy();	
		//body = null;
		//graphics.remove();
		//graphics = null;
		isActive = false;
	}
	
	void readd(String texname, int x, int y, int z)
	{
		isActive = true;
		textureName = texname;
		if(textureName.equals("lake.jpg") || textureName.equals("sea.jpg"))
			isWater = true;
		else
			isWater = false;
		
		Transform tran = new Transform();
		tran.setIdentity();
		tran.origin.set(x, y, z);
		body.setWorldTransform(tran);	
		body.setActivationState(RigidBody.DISABLE_SIMULATION);
		body.setCollisionFlags(CollisionFlags.STATIC_OBJECT);
	}
}

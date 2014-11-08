package mypackage;

import glmodel.GLImage;
import glmodel.GLMaterialLib;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.GhostObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;


public class Box {
	  protected float m_nX=0, m_nY=0, m_nZ=0;
	  protected float m_rX=0, m_rY=0, m_rZ=0;
	  public float scale;
	  float angle;
	  public RigidBody body;
	  public GhostObject ghost;
	  boolean jumping = false;
	  boolean canJump = false;
	  public gfxRenderedBox graphics;
	  
	  public Box(String textureFilename)
	  {
		 graphics = new gfxRenderedBox(textureFilename, 1f);
		 graphics.specMap = new GLImage();
		 scale = 1;
		 graphics.specMap.textureHandle = GLMaterialLib.makeTexture(textureFilename.substring(0, textureFilename.indexOf(".")) + "Spec.jpg");
	  }
	  
	  public Box(String textureFilename, float scale)
	  {
		 graphics = new gfxRenderedBox(textureFilename, scale);
		 graphics.specMap = new GLImage();
		 graphics.specMap.textureHandle = GLMaterialLib.makeTexture(textureFilename.substring(0, textureFilename.indexOf(".")) + "Spec.jpg");
	  }
	  
	  public void setPosition(float p_X, float p_Y, float p_Z)
	  {
	    m_nX=p_X; m_nY=p_Y; m_nZ=p_Z;
	  }
	  public void setRotation(float p_X, float p_Y, float p_Z)
	  {
	    m_rX=p_X; m_rY=p_Y; m_rZ=p_Z;
	  }
	  
	  public void recalc()
	  {
		  Vector3f pos = new Vector3f();
		  Quat4f rot = new Quat4f();
		  
		  pos = body.getCenterOfMassPosition(pos);
		  rot = body.getOrientation(rot);
		  
		  m_nX = pos.x;
		  m_nY = pos.y;
		  m_nZ = pos.z;
		  
		  float scale = (float)Math.sqrt(rot.x*rot.x + rot.y*rot.y + rot.z * rot.z);
		  
		  m_rX = rot.x/scale;
		  m_rY = rot.y/scale;
		  m_rZ = rot.z/scale;
		  angle = (float)((2*Math.acos(rot.w) * 180/Math.PI));
		  if(ghost == null)
			  return;
		  Transform out = body.getWorldTransform(new Transform());
		  ghost.setWorldTransform(out);
		  int num = ghost.getNumOverlappingObjects();
		  canJump = false;
		  //System.out.println("Colobj distances: ");
		  for(int i=0; i < num; i++)
		  {
			  CollisionObject ccObj = ghost.getOverlappingObject(i);
			  Transform cTransform = ccObj.getWorldTransform(new Transform());
			  Vector3f cDis = new Vector3f(m_nX - cTransform.origin.x, m_nY -cTransform.origin.y-1f, m_nZ - cTransform.origin.z);
			  //System.out.println(cDis.length());
			  if(cDis.length() < 0.8f)
				  canJump = true;
		  }
	  }

	  public void render3D()
	  {
	    GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    GL11.glPushMatrix();

	    GL11.glTranslatef(m_nX, m_nY, m_nZ);
   		
	    graphics.render();

	    GL11.glPopMatrix();
	  }

	  
	  
	  
}

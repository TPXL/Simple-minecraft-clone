package mypackage;

import glmodel.GLImage;
import glmodel.GLMaterialLib;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.bulletphysics.dynamics.RigidBody;


public class Follower {
	  protected float m_nX=0, m_nY=0, m_nZ=0;
	  protected float m_rX=0, m_rY=0, m_rZ=0;
	  float angle;
	  public RigidBody body;
	  public gfxRenderedBox graphics;
	  float scale;
	  
	  public Follower(String textureFilename)
	  {
		 graphics = new gfxRenderedBox(textureFilename, 1f);
		 graphics.specMap = new GLImage();
		 scale = 1;
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
	  
	  public void recalc(Vector3f playerpos)
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
		  Vector3f dis = new Vector3f(playerpos.x - m_nX, playerpos.y - m_nY, playerpos.z - m_nZ);
		  if(dis.length() < 10)
		  {
			  dis.y = 0;
			  dis.normalize();
			  dis.scale(0.3f);
			  body.applyCentralImpulse(dis);
		  }
		  Vector3f speed = body.getLinearVelocity(new Vector3f());
		  if(speed.length() > 3)
		  {
			  speed.normalize();
			  speed.scale(3);
			  body.setLinearVelocity(speed);
		  }
	  }

	  public void render3D()
	  {
	    GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    GL11.glPushMatrix();

	    GL11.glTranslatef(m_nX, m_nY, m_nZ);
   		
	    //GL11.glScalef(0.45f, 0.45f, 0.45f);
	    GL11.glScalef(0.5f, 0.5f, 0.5f);
	    graphics.render();

	    GL11.glPopMatrix();
	  }

	  
	  
	  
}

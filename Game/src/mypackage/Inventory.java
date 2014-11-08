package mypackage;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Inventory {
	static int defaultTex;
	class Item
	{
		int count;
		int maxCount;
		int type;
		String texname;
		Item()
		{
			count =0;
			maxCount = 5;
			type = gfxRenderedBox.textureMap.get("empty.jpg").textureHandle;
			texname = "";
		}
	}
	public int selectedSlot; //1-9
	Item slots[];
	
	Inventory()
	{
		defaultTex = SpriteMap.map.get("empty.jpg");
		slots = new Item[9];
		for(int i=0; i < slots.length; i++)
			slots[i] = new Item();
		System.out.println("DEFAULT TEX " + defaultTex);
		gfxRenderedBox.addTexture("background.jpg");
		gfxRenderedBox.addTexture("empty.jpg");
		
	}
	
	boolean pickup(Box box)
	{
		//first find out if there are any non-full stacks of the item
		for(int i=0; i < slots.length; i++)
		{
			if(slots[i].type == box.graphics.tex.textureHandle && slots[i].count < slots[i].maxCount)
			{
				slots[i].count++;
				return true;
			}
		}
		//find out if you can place it in an empty slot
		for(int i=0; i < slots.length; i++)
		{
			if(slots[i].count == 0)
			{
				slots[i].count++;
				slots[i].texname = box.graphics.texname;
				slots[i].type = gfxRenderedBox.textureMap.get(slots[i].texname).textureHandle;
				return true;
			}
		}
		
		return false;
	}
	
	boolean canPickup(Box box)
	{
		for(int i=0; i < slots.length; i++)
		{
			if(slots[i].type == box.graphics.tex.textureHandle && slots[i].count < slots[i].maxCount || slots[i].count == 0)
			{
				return true;
			}
		}
		return false;
	}
	
	void remove()
	{
		slots[selectedSlot].count--;
		if(slots[selectedSlot].count == 0)
		{
			slots[selectedSlot].type = gfxRenderedBox.textureMap.get("empty.jpg").textureHandle;
			slots[selectedSlot].texname = "";
		}
	}
	
	void print()
	{
		for(int i=0; i < slots.length; i++)
			System.out.println(i + ": " + slots[i].count);
	}
	
	void render()
	{
		GL20.glUseProgram(0);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glOrtho(-10, 10, -10, 10, 0.1, 10);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glTranslatef(-5.3f, 3, 0);
		GL11.glScalef(0.5f, 0.5f, 0.5f);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gfxRenderedBox.textureMap.get("background.jpg").textureHandle);
		GL11.glTranslatef(0, -1.5f * selectedSlot, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0.66f, 0.66f);
		GL11.glVertex3f(-0.05f, -0.05f, 0);
		
		GL11.glTexCoord2f(1, 0.66f);
		GL11.glVertex3f(1.05f, -0.05f, 0);
		
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(1.05f, 1.05f, 0);
		
		GL11.glTexCoord2f(0.66f, 1);
		GL11.glVertex3f(-0.05f, 1.05f, 0);
		GL11.glEnd();

		GL11.glTranslatef(0, 1.5f * selectedSlot, 0);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		for(int i=0; i < slots.length; i++)
		{
		
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, slots[i].type);
			
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0.66f, 0.66f);
			GL11.glVertex3f(0, 0, 0);
			
			GL11.glTexCoord2f(1, 0.66f);
			GL11.glVertex3f(1, 0, 0);
			
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(1, 1, 0);
			
			GL11.glTexCoord2f(0.66f, 1);
			GL11.glVertex3f(0, 1, 0);
			GL11.glEnd();
			GL11.glTranslatef(0, -1.5f, 0);
		}
		

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		
	}
}



















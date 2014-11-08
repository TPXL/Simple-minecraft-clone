import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class Vertex {
	int x, y;
	ArrayList<Edge> edges;
	ArrayList<Polygon> polygons;
	Edge downslope;
	float height;
	boolean touchesWater;
	boolean isWater;
	boolean touchesSea;
	boolean marked;
	
	//boolean isEdgeVertex;
	
	public Vertex()
	{
		x = 0; y = 0; 
		//isEdgeVertex = false;
		edges = new ArrayList<Edge>(); 
		polygons = new ArrayList<Polygon>();
		touchesWater = false;
		isWater = false;
		touchesSea = false;
		downslope = null;
	}
	public Vertex(int x, int y)
	{
		this.x = x; 
		this.y = y; 
		edges = new ArrayList<Edge>(); 
		polygons = new ArrayList<Polygon>();
		touchesWater = false;
		touchesSea = false;
		isWater = false;
		downslope = null;
	}
	
	public void draw(Graphics g, boolean drawVertices)
	{
		
		if(!drawVertices)
			return;
		
		if(edges.size() == 0 || polygons.size() == 0)
		{
			g.setColor(new Color(1.f, 0.f, 1.f));
			g.drawOval(x*2-2, y*2-2, 4, 4);
			return;
		}
		int offsetX = 1;
		int offsetY = 1;
		int mul = 2;
		
		
		
		if(downslope != null/* && (!downslope.down.touchesSea || !downslope.up.touchesSea)*/)
		{
			int x1 = x;
			int y1 = y;
			int x2 = downslope.up == this ? downslope.down.x : downslope.up.x;
			int y2 = downslope.up == this ? downslope.down.y : downslope.up.y;
			int mx = (x1+x2)/2;
			int my = (y1+y2)/2;
	
			//g.setColor(new Color(0.5f, 0.5f, 1.f));
			//g.drawLine(x1 * mul + offsetX, y1*mul+offsetY, mx*mul + offsetX, my*mul + offsetY);
	
			g.setColor(new Color(1.f, 0.f, 0.f));
			g.drawLine(mx * mul + offsetX, my*mul + offsetY, x2 * mul + offsetX, y2 * mul + offsetY);
		}
		else
		{
			g.setColor(new Color(1.f, 1.f, 1.f));
			g.fillOval(x*mul-3+offsetX, y*mul-3+offsetY, 6, 6);
		}
		
		
		if(isWater)
			g.setColor(new Color(0.f, 1.f, 1.f));
		else if(!touchesWater)
			g.setColor(new Color(1.f, 0.f, 0.f));
		else if(touchesWater && ! touchesSea)
			g.setColor(new Color(1.f, 1.f, 0.f));
		else if(touchesSea)
			g.setColor(new Color(1.f, 0.f, 1.f));
		if(marked)
			g.fillOval(x*mul-2+offsetX, y*mul-2+offsetY, 4, 4);
		/*
		if(marked)
		{
			g.setColor(new Color(1f, 1f, 0f));
			g.fillOval(x*mul-2+offsetX, y*mul-2+offsetY, 4, 4);
		}*/
	}
	
	public void seaLandWater()
	{
		int waterTouch = 0;
		
		for(int i=0; i < polygons.size(); i++)
		{
			if(!polygons.get(i).isLand)
			{
				waterTouch++;
				touchesWater = true;
			}
			if(polygons.get(i).isSea)
				touchesSea = true;
		}
		if(waterTouch == polygons.size())
		{
			isWater = true;
			//System.out.println("Water vertex!");
		}
	}
	
	public void calculateHeight()
	{
		for(int i=0; i < polygons.size(); i++)
		{
			height += polygons.get(i).height;
		}
		height /= polygons.size();
	}
}

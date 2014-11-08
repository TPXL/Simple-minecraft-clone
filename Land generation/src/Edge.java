import java.awt.Color;
import java.awt.Graphics;


public class Edge {
	Vertex up, down;
	Polygon left, right;
	int flow;
	
	public Edge(){up = null; down = null; left = null; right = null; flow = 0;}
	public Edge(Vertex up, Vertex down)
	{
		left = null; 
		right = null; 
		this.up = up; 
		this.down = down; 
		flow = 0;
		up.edges.add(this); 
		down.edges.add(this);
	}
	
	public void draw(Graphics g, boolean drawRivers, boolean drawEdges)
	{
		int offsetX = 0;
		int offsetY = 0;
		int mul = 2;
		if(flow == 0 && drawEdges)
		{
			g.setColor(new Color(0.f, 0.f, 0.3f));
			g.drawLine(up.x*mul+offsetX, up.y*mul+offsetY, down.x*mul+offsetX, down.y*mul+offsetY);
		}
		else if(flow > 0 && drawRivers)
		{
			g.setColor(new Color(0.5f, 0.5f, 1f));
			g.drawLine(up.x*mul+offsetX, up.y*mul+offsetY, down.x * mul + offsetX, down.y*mul+offsetY);
		}
		/*
		if(drawWatersheds)
		{
		Vertex l = new Vertex(0, 0);
		Vertex r = new Vertex(0, 0);
		
		for(int i=0; i < left.vertices.size(); i++)
		{
			l.x += left.vertices.get(i).x;
			l.y += left.vertices.get(i).y;
		}
		for(int i=0; i < right.vertices.size(); i++)
		{
			r.x += right.vertices.get(i).x;
			r.y += right.vertices.get(i).y;
		}
		
		r.x /= right.vertices.size();
		r.y /= right.vertices.size();
		l.x /= left.vertices.size();
		l.y /= left.vertices.size();
		g.drawLine(r.x * mul + offsetX, r.y * mul + offsetY, l.x * mul + offsetX, l.y * mul+offsetY);
		}
		*/
	}
}

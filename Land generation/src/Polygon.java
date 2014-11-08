import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Polygon {

	ArrayList<Vertex> vertices;
	ArrayList<Edge>	edges;
	Point center;
	
	int height;
	int index;
	int moisture;	//0 = highest, highe than that is lower
	
	boolean isLand;
	boolean isSea;
	boolean checked;
	
	public Polygon()
	{
		vertices = new ArrayList<Vertex>(); 
		edges = new ArrayList<Edge>();
		center = new Point();
		isLand = true;
		isSea = false;
		checked = false;
		height = -1;
		index = -1;
		moisture = 0;
	}

	public Polygon(int index)
	{
		vertices = new ArrayList<Vertex>(); 
		edges = new ArrayList<Edge>();
		center = new Point();
		isLand = true;
		isSea = false;
		checked = false;
		height = -1;
		this.index = index;
		moisture = 0;
	}
	
	public boolean isPointInside(Point in)
	{
		if(vertices.size() < 3)
			return false;
		Vertex prev = vertices.get(0);
		Vertex curr = vertices.get(1);
		boolean ccw = ((curr.x - in.x) * (prev.y - in.y) - (prev.x - in.x) * (curr.y-in.y)) >= 0;
		for(int i=1; i < vertices.size(); i++)
		{
			curr = vertices.get(i);
			
			if(ccw != (((curr.x - in.x) * (prev.y - in.y) - (prev.x - in.x) * (curr.y-in.y)) >= 0))
				return false;
			prev = curr;
		}
		curr = vertices.get(0);
		if(ccw != (((curr.x - in.x) * (prev.y - in.y) - (prev.x - in.x) * (curr.y-in.y)) >= 0))
			return false;
		return true;
	}
	
	public void draw(Graphics g, int maxh, int maxmoisture, boolean drawLakes, boolean drawHeightmap, boolean drawInverseMoisturemap)
	{
		if(((isLand && !isSea) || (!isSea && !drawLakes))&& drawHeightmap)
		{
			float r=1.f;
			if((0.32f * (float)height / maxh) >= 0 && (0.32 * (float) height / maxh) <= 1)
				r = 0.32f * (float)height /maxh;
			
			float gr = 1.f;
			if((0.67f * (float)height / maxh) >= 0 && (0.67 * (float) height / maxh) <= 1)
				gr = 0.67f * (float)height /maxh;

			float b = 1.f;
			if((0.3f * (float)height / maxh) >= 0 && (0.3 * (float) height / maxh) <= 1)
				b = 0.3f * (float)height /maxh;
			
			g.setColor(new Color(r, gr, b));
		}
		else if(isLand && !isSea && drawInverseMoisturemap)
		{
			g.setColor(new Color(0xCF * moisture/maxmoisture, 0x9F * moisture/maxmoisture, 0x04 * moisture/maxmoisture));
		}
		else if(isSea && !isLand)
			g.setColor(new Color(0.2f, 0.2f, 0.8f));
		else if(!isSea && !isLand && drawLakes)
			g.setColor(new Color(0.3f, 0.3f, 0.9f));
		else
			g.setColor(new Color(0, 0, 0));
		
		int mul = 2;
		int pointsx[] = new int[vertices.size()];
		int pointsy[] = new int[vertices.size()];
		for(int i=0; i < vertices.size(); i++)
		{
			pointsx[i] = vertices.get(i).x * mul;
			pointsy[i] = vertices.get(i).y * mul;
		}
		
		g.fillPolygon(pointsx, pointsy, pointsx.length);
	}
	void sortVertices()
	{
		Collections.sort(vertices, 
				new Comparator<Vertex>()
				{
					public int compare(Vertex arg0, Vertex arg1) {

						double ang1 = Math.atan2(center.x - arg0.x, center.y - arg0.y);
						double ang2 = Math.atan2(center.x - arg1.x, center.y - arg1.y);
						if(ang1 < 0) ang1 = ang1 + 2* Math.PI;
						if(ang2 < 0) ang2 = ang2 + 2* Math.PI;
						
						if(ang1 < ang2)
							return -1;
						else if(ang2 < ang1)
							return 1;
						System.out.println("Mismatch");
						return 0;
					}
				}
				);
	}
	void calculateCenter()
	{
		for(int i=0; i < vertices.size(); i++)
		{
			center.x += vertices.get(i).x;
			center.y += vertices.get(i).y;
		}
		center.x /= vertices.size();
		center.y /= vertices.size();
		//System.out.println(center.x + " " + center.y);
	}
}

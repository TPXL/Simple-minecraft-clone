import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;


public class NewWorld {
	
	NewWorld(){}
	
	ArrayList<Point> points;
	ArrayList<Vertex> vertices;
	ArrayList<myVertex> tocke;
	ArrayList<Polygon> polygons;
	ArrayList<Edge> edges;
	int [][]table;
	int sx;
	int sy;
	
	int maxh;
	int maxmoisture;
	
	void unmarkPolygons()
	{
		for(int i=0; i < polygons.size(); i++)
			polygons.get(i).checked = false;
	}
	
	void calculateDeserts()
	{
		ArrayList <Polygon> line = new ArrayList<Polygon>();
		for(int i=0; i < polygons.size(); i++)		//Add every polgyon touching fresh water (!isSea, !isLand or edge.flow > 0) to a line
		{
			Polygon cPoly = polygons.get(i);
			for(int j=0; j < cPoly.edges.size(); j++)
			{
				Edge jEdge = cPoly.edges.get(j);
				Polygon right = cPoly == jEdge.left ? jEdge.right : jEdge.left;
				
				if(right.checked == true)
					continue;
				
				if(!cPoly.isLand && !cPoly.isSea && right.isLand)
				{
					right.checked = true;
					line.add(right);
				}
				
				if(jEdge.flow > 0)
				{
					if(cPoly.checked == false && cPoly.isLand)
					{
						cPoly.checked = true;
						line.add(cPoly);
					}
					if(right.checked == false && cPoly. isLand)
					{
						right.checked = true;
						line.add(cPoly);
					}
				}
			}
		}
		
		while(!line.isEmpty())
		{
			ArrayList <Polygon> newLine = new ArrayList<Polygon>();
			for(int i=0; i < line.size(); i++)
			{
				Polygon cPoly = line.get(i);
				
				cPoly.checked = true;
				int min = 100000;
				for(int j=0; j < cPoly.edges.size(); j++)
				{
					Edge jEdge = cPoly.edges.get(j);
					Polygon right = jEdge.left == cPoly ? jEdge.right : jEdge.left;
					
					if(!right.isLand)
					{
						continue;
					}
					
					if(right.checked == true)
					{
						if(right.moisture < min)
							min = right.moisture;
					}
					else
					{
						newLine.add(right);
					}
				}
				cPoly.moisture = min+1;
				if(cPoly.moisture > maxmoisture)
					maxmoisture = cPoly.moisture;
			}
			System.out.println("Deserts line: " + line.size());
			line = newLine;
		}

		System.out.println("maxm " + maxmoisture);
	}
	
	void markLakes(ArrayList<Polygon> marked)
	{
		for(int i=0; i < marked.size(); i++)
		{
			Polygon cPoly = marked.get(i);
			for(int j=0; j < cPoly.edges.size(); j++)
			{
				Edge jEdge = cPoly.edges.get(j);
				Polygon right = jEdge.left == cPoly ? jEdge.right : jEdge.left;
				
				if(!right.isLand && !right.isSea && !right.checked)
				{
					right.checked = true;
					marked.add(right);
				}
			}
		}
	}
	
	void deleteUnmarkedLakes()
	{
		for(int i=0; i < polygons.size(); i++)
		{
			Polygon cPoly = polygons.get(i);
			if(!cPoly.checked && !cPoly.isLand && !cPoly.isSea)
			{
				cPoly.isLand = true;
			}
				
		}
	}
	
	void waterToSea(Polygon in)	//turn all 'lakes' that are connected to the sea to sea
	{
		for(int i=0; i < in.edges.size(); i++)
		{
			Edge cEdge = in.edges.get(i);
			Polygon right = cEdge.left != in ? cEdge.left : cEdge.right;
			if(right != in && right.checked == false && right.isLand == false)
			{
				right.checked = true;
				right.isSea = true;
				waterToSea(right);
			}
		}
	}
	
	ArrayList<Polygon> lakeHeightmap(Polygon in)
	{
		ArrayList<Polygon> addedCoast = new ArrayList<Polygon>();
		for(int i=0; i < in.edges.size(); i++)	//check all surrounding polygons for land and add them to the coastline. recursively do for the whole lake.
		{
			Edge cEdge = in.edges.get(i);
			Polygon right = cEdge.left != in ? cEdge.left : cEdge.right;
			if(right != in && right.height == -1 && right.isLand == false && right.isSea == false) //left is not this
			{
				right.height = in.height;
				addedCoast.addAll(lakeHeightmap(right));
				
			}
			if(right != in && right.height == -1 && right.isLand == true)
			{
				addedCoast.add(right);
			}
		}
		return addedCoast;
	}

	void calculateWatersheds(ArrayList <Vertex> line)
	{
		//int keks = 17;
		while(line.size() != 0/* && keks > 0*/)
		{
			System.out.println("Watershed! " + line.size());
			ArrayList <Vertex> newLine = new ArrayList<Vertex>();
			for(int i=0; i < line.size(); i++)
			{
				Vertex cVertex = line.get(i);
				boolean edgeFound = false;
				for(int j=0; j < cVertex.edges.size(); j++)
				{
					Edge jEdge = cVertex.edges.get(j);
					Vertex right = jEdge.down == cVertex ? jEdge.up : jEdge.down;
					
					if(right.touchesSea || (right.downslope != null && right.height <= cVertex.height))
					{
						cVertex.downslope = jEdge;
						edgeFound = true;
					}
					
					if(right.downslope == null && !right.touchesSea && right.marked == false && right.height >= cVertex.height)
					{
						newLine.add(right);
						right.marked = true;
					}
				}
				if(!edgeFound)
				{
					System.out.println("Edge not found for some reason.");
					for(int j=0; j < cVertex.edges.size(); j++)
					{
						Edge jEdge = cVertex.edges.get(j);
						Vertex right = jEdge.down == cVertex ? jEdge.up : jEdge.down;
						System.out.println(right.downslope != null);
					}
				}
			}
			line = newLine;
			//keks--;
		}
	}
	
	void calculateHeightmap(ArrayList <Polygon> line)
	{
		while(line.size() != 0)
		{
			System.out.println("coastline! " + line.size());
			ArrayList <Polygon> newCoastline = new ArrayList<Polygon>();
			for(int i=0; i < line.size(); i++)
			{
				Polygon cPoly = line.get(i);
				if(cPoly.height > 0)
					continue;
				
				int minheight = 1000;
				for(int j=0; j < cPoly.edges.size(); j++)	//height is min height of surroundings +1 if land and height if water
				{
					Edge cEdge = cPoly.edges.get(j);
					if(cEdge.left.height < minheight && cEdge.left.height != -1 && cEdge.left.isLand)
					{
						minheight = cEdge.left.height;
					}
					else if(cEdge.left.height <= minheight && cEdge.left.height != -1 && !cEdge.left.isLand)
					{
						minheight = cEdge.left.height-1;
					}
					else if(cEdge.right.height < minheight && cEdge.right.height != -1 && cEdge.right.isLand)
					{
						minheight = cEdge.right.height;
					}
					else if(cEdge.right.height <= minheight && cEdge.right.height != -1 && !cEdge.right.isLand)
					{
						minheight = cEdge.right.height-1;
					}
				}
				cPoly.height = minheight+1;
				if(minheight+1 > maxh)
					maxh = minheight+1;
				
				for(int j=0; j < cPoly.edges.size(); j++)	//add polygons that are farther from the coast to 'newCoastline'
				{
					Edge cEdge = cPoly.edges.get(j);
					Polygon right = cEdge.left != cPoly ? cEdge.left : cEdge.right;
					
					if(right.height == -1 && right.isSea == false)	//left is not this
					{
						if(right.isLand == false)	//got us a lake
						{
							right.height = cPoly.height;
							newCoastline.addAll(lakeHeightmap(right));	//lakes all have the same height, so process that
							cEdge.left.checked = true;
							cEdge.right.checked = true;
							
						}
						else
							newCoastline.add(right);
					}
				}
				
			}
			line = newCoastline;
		}
	}
	
	public void generate()
	{
		maxh = 0;
		maxmoisture = -1;
		//Random seed = new Random(123459);
		//int sx = 400, sy = 400;
		//int nPoints = 2500;
		
		//Random seed = new Random(123456);
		//Random seed = new Random(654321);
		//Random seed = new Random(4156321);
		
		Random seed = new Random();
		sx = 500; 
		sy = 500;
		int nPoints = 2500;
		table = new int[sx][sy];
		tocke = new ArrayList <myVertex>();
		
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		polygons = new ArrayList<Polygon>();
		
		points = new ArrayList<Point>();
		
		for(int i=0; i < nPoints; i++)	//random nPoints tock
			points.add(new Point(seed.nextInt(sx), seed.nextInt(sy)));
		for(int i=0; i < points.size(); i++)
			for(int j=0; j < points.size(); j++)
			{
				if(i == j)
					continue;
				if(points.get(i).x == points.get(j).x && points.get(i).y == points.get(j).y)
				{
					points.remove(i);
					i--;
					break;
				}
			}
		
		for(int nRelax = 0; nRelax < 8; nRelax++)
		{
			System.out.println("#relax: " + nRelax);
			for(int i=0; i < sx; i++)
				for(int j=0; j < sy; j++)
				{
					int cPoint = 0;
					float cDistance = 1000000f;
					for(int k=0; k < points.size(); k++)
					{
						float k1 = i - points.get(k).x;
						float k2 = j - points.get(k).y;
						float dis = k1*k1 + k2*k2;
						if(dis < cDistance)
						{
							cDistance = dis;
							cPoint = k;
						}
					}
					table[i][j] = cPoint;
				}
			System.out.println("Najblizji done");
			tocke.clear();
			//tocke po kotih.
			{
				myVertex nVertex = new myVertex(0, 0);
				nVertex.dotikajoci.add(table[0][0]);
				tocke.add(nVertex);
				
				nVertex = new myVertex(0, sy-1);
				nVertex.dotikajoci.add(table[0][sy-1]);
				tocke.add(nVertex);
				
				nVertex = new myVertex(sx-1, 0);
				nVertex.dotikajoci.add(table[sx-1][0]);
				tocke.add(nVertex);
				
				nVertex = new myVertex(sx-1, sy-1);
				nVertex.dotikajoci.add(table[sx-1][sy-1]);
				tocke.add(nVertex);
			}
			
			//tocke na robovih
			
			for(int i=0; i < sx-1; i++)
			{
				if(table[i][0] != table[i+1][0])
				{
					myVertex nVertex = new myVertex(i, 0);
					int a, b, c, d;
					a = table[i][0];
					b = table[i+1][0];
					c = table[i][1];
					d = table[i+1][1];
					
					nVertex.dotikajoci.add(a);
					nVertex.dotikajoci.add(b);
					if(a != c && b != c)
						nVertex.dotikajoci.add(c);
					if(a != d && b != d && c != d)
						nVertex.dotikajoci.add(d);
					
					tocke.add(nVertex);
				}
				if(table[i][sy-1] != table[i+1][sy-1])
				{
					myVertex nVertex = new myVertex(i, sy-1);
					int a, b, c, d;
					a = table[i][sy-1];
					b = table[i+1][sy-1];
					c = table[i][sy-2];
					d = table[i+1][sy-2];
					
					nVertex.dotikajoci.add(a);
					nVertex.dotikajoci.add(b);
					if(a != c && b != c)
						nVertex.dotikajoci.add(c);
					if(a != d && b != d && c != d)
						nVertex.dotikajoci.add(d);
					
					tocke.add(nVertex);
				}
			}
			
			for(int i=0; i < sy-1; i++)
			{
				if(table[0][i] != table[0][i+1])
				{
					myVertex nVertex = new myVertex(0, i);

					int a, b, c, d;
					a = table[0][i];
					b = table[0][i+1];
					c = table[1][i];
					d = table[1][i+1];
					
					nVertex.dotikajoci.add(a);
					nVertex.dotikajoci.add(b);
					if(a != c && b != c)
						nVertex.dotikajoci.add(c);
					if(a != d && b != d && c != d)
						nVertex.dotikajoci.add(d);
					tocke.add(nVertex);
				}
				if(table[sx-1][i] != table[sx-1][i+1])
				{
					myVertex nVertex = new myVertex(sx-1, i);
					
					int a, b, c, d;
					a = table[sx-1][i];
					b = table[sx-1][i+1];
					c = table[sx-2][i];
					d = table[sx-2][i+1];
					
					nVertex.dotikajoci.add(a);
					nVertex.dotikajoci.add(b);
					if(a != c && b != c)
						nVertex.dotikajoci.add(c);
					if(a != d && b != d && c != d)
						nVertex.dotikajoci.add(d);
					tocke.add(nVertex);
				}
			}
			
			//tocke vmes
			for(int i=1; i < sx-2; i++)
				for(int j=1; j < sy-2; j++)
				{
					if(table[i][j] != table[i][j+1] && table[i][j] != table[i+1][j] && table[i+1][j] != table[i][j+1])
					{
						myVertex nVertex = new myVertex(i, j);
						int a, b, c, d;
						a = table[i][j];
						b = table[i][j+1];
						c = table[i+1][j];
						d = table[i+1][j+1];
						nVertex.dotikajoci.add(a);
						nVertex.dotikajoci.add(b);
						nVertex.dotikajoci.add(c);
						if(d != a && d != b && d != c)
							nVertex.dotikajoci.add(d);
						tocke.add(nVertex);
					}
					else if(table[i][j] != table[i][j+1] && table[i][j] != table[i+1][j+1] && table[i][j+1] != table[i+1][j+1])
					{
						myVertex nVertex = new myVertex(i, j);
						int a, b, c, d;
						a = table[i][j];
						b = table[i][j+1];
						c = table[i+1][j+1];
						d = table[i+1][j];
						if( d != a && d != b && d != c)
							nVertex.dotikajoci.add(d);
						nVertex.dotikajoci.add(a);
						nVertex.dotikajoci.add(b);
						nVertex.dotikajoci.add(c);
						
						tocke.add(nVertex);
					}
					else if(table[i][j] != table[i+1][j] && table[i][j] != table[i+1][j+1] && table[i+1][j] != table[i+1][j+1])
					{
						myVertex nVertex = new myVertex(i, j);
						int a, b, c, d;
						a = table[i][j];
						b = table[i][j+1];
						c = table[i+1][j+1];
						d = table[i+1][j];
						if(b != a && b != c && b != d)
							nVertex.dotikajoci.add(b);
						nVertex.dotikajoci.add(a);
						nVertex.dotikajoci.add(c);
						nVertex.dotikajoci.add(d);
						tocke.add(nVertex);
					}
				}
			System.out.println("Tocke dodane.");
			
			//sestav poligone
			
			ArrayList <Integer> poligoni[] =new ArrayList[points.size()];
			for(int i=0; i < poligoni.length; i++)
			{
				poligoni[i] = new ArrayList<Integer>();
			}
			
			
			for(int i=0; i < tocke.size(); i++)
			{
				myVertex cTocka = tocke.get(i);
				for(int j=0; j < cTocka.dotikajoci.size(); j++)
				{
					poligoni[cTocka.dotikajoci.get(j)].add(i);
				}
			}
			
			
			
			points.clear();
			for(int i=0; i < poligoni.length; i++)	//Lloyd relaxation
			{
				if(poligoni[i].size() == 0)
					continue;
				Point nPoint = new Point();
				for(int j=0; j < poligoni[i].size(); j++)
				{
					nPoint.x += tocke.get(poligoni[i].get(j)).x;
					nPoint.y += tocke.get(poligoni[i].get(j)).y;
				}
				nPoint.x/=poligoni[i].size();
				nPoint.y/=poligoni[i].size();
				points.add(nPoint);
			}
			System.out.println("Tocke relaksirane.");
		}
		//----construct actual data sturcture
		
		for(int i=0; i < points.size(); i++)	//Array of polygons.
			polygons.add(new Polygon(i));
		
		for(int i=0; i < tocke.size(); i++)		//Add vertices to polygons and polygons to vertices
		{
			myVertex cTocka = tocke.get(i);
			Vertex cVertex = new Vertex(cTocka.x, cTocka.y);

			for(int j=0; j < cTocka.dotikajoci.size(); j++)
			{
				int temp = cTocka.dotikajoci.get(j);
				polygons.get(temp).vertices.add(cVertex);
				cVertex.polygons.add(polygons.get(cTocka.dotikajoci.get(j)));
			}
			
			vertices.add(cVertex);
		}
		
		for(int i=0; i < vertices.size(); i++)		//add polygons and vertices to edges and edges to polygons
		{
			Vertex iVertex = vertices.get(i);
			for(int j=i+1; j < vertices.size(); j++)
			{
				Vertex jVertex = vertices.get(j);
				int match = 0;
				for(int k = 0; k < iVertex.polygons.size(); k++)	//two vertexes need to share two polygons for them to be neighbours
					for(int l = 0; l < jVertex.polygons.size(); l++)
						if(iVertex.polygons.get(k) == jVertex.polygons.get(l))
						{
							match ++;
						}
				if(match < 2)
					continue;
				Edge cEdge = new Edge(jVertex, iVertex);
				
				for(int k = 0; k < iVertex.polygons.size(); k++)
					for(int l = 0; l < jVertex.polygons.size(); l++)
						if(iVertex.polygons.get(k) == jVertex.polygons.get(l))
						{
							iVertex.polygons.get(k).edges.add(cEdge);
							if(cEdge.left == null)
								cEdge.left = iVertex.polygons.get(k);
							else if(cEdge.right == null)
								cEdge.right = iVertex.polygons.get(k);
						}
				edges.add(cEdge);
			}
		}
		for(int i=0; i < polygons.size(); i++)	//calc centers for polygons. Metadata for noise and stuff.
			polygons.get(i).calculateCenter();
		
		for(int i=0; i < polygons.size(); i++)	//sort so you can draw it nicely.
			polygons.get(i).sortVertices();
		
		
		//circular island in the middle
		/*
		for(int i=0; i < polygons.size(); i++)
		{
			Polygon cPoly = polygons.get(i);
			double cx = cPoly.center.x - sx/2;
			double cy = cPoly.center.y - sy/2;
			cx /= sx;
			cy /= sy;
			double radius = cx * cx + cy*cy;
			double d = Math.sqrt(radius);
			//System.out.println((Math.sin(Math.atan2(cy, cx) * 6) * radius * 0.2));

			if(radius + (Math.sin(Math.atan2(cy, cx) * 6)) * radius * 0.2> (0.4 * 0.4))	// produces good results
			//if(d + Math.sin(Math.atan2(cy, cx)*6)*0.2*0.3 > 0.3)	//decent results
			{
				cPoly.isLand = false;
				cPoly.isSea = true;
			}
		}*/
		
		//a square-ish island
		for(int i=0; i < polygons.size(); i++)
		{
			Polygon cPoly = polygons.get(i);
			double cx = cPoly.center.x - sx/2;
			double cy = cPoly.center.y - sy/2;
			cx /= sx/2;
			cy /= sy/2;
			//double radius = Math.sqrt(cx * cx + cy * cy);
			if(cx < 0)
				cx = -cx;
			if(cy < 0)
				cy = -cy;
			double radius = cx > cy ? cx : cy;
			double thresh = 1 - radius * radius * radius * radius * radius;	//radius ^6 works well?
			float mul = 12f;
			float pnoise = (Perlin.noise((float)cx * mul + seed.nextFloat() - 0.5f, (float)cy * mul + seed.nextFloat() - 0.5f)+1)/2;
			
			if(pnoise > thresh)
			{
				cPoly.isLand = false;
				cPoly.isSea = true;
			}
		}
		
		
		
		//add lakes and some variation to the map.
		
		float xPerlinOffset = seed.nextFloat() -0.5f;
		float yPerlinOffset = seed.nextFloat() -0.5f;
		
		for(int i=0; i < polygons.size(); i++)
		{
			Polygon cPoly = polygons.get(i);
			if(!cPoly.isLand)
				continue;
			//float mul = 4f; //good results
			float mul = 10f;
			float pnoise = Perlin.noise(  ((float)cPoly.center.x/sx + xPerlinOffset)* mul, ((float)cPoly.center.y/sy + yPerlinOffset) * mul);
			if(pnoise < -0.25)	//-0.15 works well.
				cPoly.isLand = false;
		}
		
		for(int i=0; i < polygons.size(); i++)
			if(polygons.get(i).isSea == true && polygons.get(i).isLand == false && polygons.get(i).checked == false)
			{
				waterToSea(polygons.get(i));	//turn water polygons that are connected to the sea to sea.
				break;
			}
		
		for(int i=0; i < vertices.size(); i++)
			vertices.get(i).seaLandWater();
		
		ArrayList <Polygon> coastline = new ArrayList<Polygon>();
		for(int i=0; i < polygons.size(); i++)	//find all coastline polygons (is land and at least one neighbour is sea)
		{
			Polygon cPoly = polygons.get(i);
			if(cPoly.isLand == false)
				continue;
			
			boolean go = false;
			
			for(int j=0; j < cPoly.edges.size(); j++)
			{
				Edge cEdge = cPoly.edges.get(j);
				if(cEdge.left.isSea == true || cEdge.right.isSea == true)
				{
					go = true;
					break;
				}
			}
			if(!go)
				continue;
			cPoly.height = 0;
			coastline.add(cPoly);
		}
		calculateHeightmap(coastline);	//calculate polygon heights (gre od zunej na notr enga po enga)
		System.out.println("maxh" + maxh);
		
		for(int i=0; i < vertices.size(); i++)	//calc vertex heights
		{
			vertices.get(i).calculateHeight();
		}
		
		ArrayList <Vertex> vCoastline = new ArrayList<Vertex>();
		for(int i=0; i < vertices.size(); i++)
		{
			if(vertices.get(i).touchesSea && !vertices.get(i).isWater)
			{
				vCoastline.add(vertices.get(i));
				vertices.get(i).marked = true;
			}
		}
		calculateWatersheds(vCoastline);
		
		System.out.println("Adding RIVERS! ");

		unmarkPolygons();
		
		ArrayList<Polygon> lakesToKeep = new ArrayList<Polygon>();
		
		for(int i=0; i < 20; i++) //add us some RIVERS
		{
			System.out.println("River #" + i);
			Vertex cVertex = null;
			int c = 0;		//escape
			while(true && c < 1000)
			{
				int chosenOne = seed.nextInt(vertices.size()-1)+1;
				cVertex = vertices.get(chosenOne);
				if(cVertex.isWater == false && cVertex.height >= maxh/3)
					break;
				c++;
			}
			System.out.println("Selected vertex! " + i);
			Vertex pprev = null;
			Vertex prev = null;
			while(!cVertex.touchesSea)
			{
				Edge cEdge = cVertex.downslope;
				try
				{
					cEdge.flow++;
				}catch(Exception e)
				{
					System.out.println(cVertex.height + " " + cVertex.marked + " " + cVertex.x + " " + cVertex.y + " " + cVertex.touchesSea + " " + cVertex.touchesWater);
					break;
				}
				pprev = prev;
				prev = cVertex;
				
				if(!cEdge.left.isLand && !cEdge.left.isSea && !cEdge.left.checked)	//mark lakes with rivers
				{
					cEdge.left.checked = true;
					lakesToKeep.add(cEdge.left);
				}
				if(!cEdge.right.isLand && !cEdge.right.isSea && !cEdge.left.checked)	//mark lakes with rivers
				{
					cEdge.right.checked = true;
					lakesToKeep.add(cEdge.right);
				}
				
				cVertex = cVertex.downslope.down != cVertex ? cVertex.downslope.down : cVertex.downslope.up;
				
				
				
				if(pprev != null && prev != null && (prev == cVertex || pprev == cVertex))
					break;
			}
			//System.out.println("River done!");
		}
		System.out.println("RIVERS FLOOW FREELY!");
		markLakes(lakesToKeep);
		deleteUnmarkedLakes();
		
		unmarkPolygons();
		calculateDeserts();
		
		
		System.out.println("MAP DONE, YO!");
	}
	
	void exportToObj()
	{
		File f = new File("out.obj");
		f.delete();
		try
		{
			f.createNewFile();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		FileWriter fw;
		try
		{
			fw = new FileWriter(f);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		try
		{
			for(int i=0; i < polygons.size(); i++)
			{
				fw.append("v " + ((double)polygons.get(i).center.x/sx) + " " + ((double)polygons.get(i).center.y/sy) + " " + ((double)polygons.get(i).height/maxh) + "\n");
			}
			for(int i=0; i < edges.size(); i++)
			{
				fw.append("f " + (edges.get(i).left.index+1) + " " + (edges.get(i).right.index+1) + " " + (edges.get(i).right.index+1) + "\n");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			fw.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("exported!");
	}
}

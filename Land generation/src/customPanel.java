import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class CustomPanel extends JPanel implements KeyListener{

	public ArrayList<Edge> edges;
	public ArrayList<Polygon> polygons;
	public ArrayList<Vertex> vertices;
	
	public ArrayList<Point> points;
	int [][]table;
	int maxh;
	int maxmoisture;
	
	boolean drawRivers;
	boolean drawLakes;
	boolean drawHeightmap;
	boolean drawInverseMoisturemap;
	boolean drawEdges;
	boolean drawVertices;
	
	int x, y;
	
	
	CustomPanel()
	{
		this.addKeyListener(this);
		drawVertices = false;
		drawEdges = false;
		drawInverseMoisturemap = false;
		drawHeightmap = true;
		drawLakes = true;
		drawRivers = true;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		for(int i=0; i < polygons.size(); i++)
			polygons.get(i).draw(g, maxh, maxmoisture, drawLakes, drawHeightmap, drawInverseMoisturemap);
		for(int i=0; i < edges.size(); i++)
			edges.get(i).draw(g, drawRivers, drawEdges);
		for(int i=0; i < vertices.size(); i++)
			vertices.get(i).draw(g, drawVertices);
		
	}
	
	class Tree
	{
		int x, z;
		Tree(int x, int z)
		{
			this.x = x;
			this.z = z;
		}
	}
	
	public void exportBitmap()
	{
		try
		{
			BufferedImage ss = new BufferedImage(x*2, y*2, BufferedImage.TYPE_INT_RGB);
			paint(ss.getGraphics());
			File imageFile = new File("ss.bmp");
			ImageIO.write(ss, "bmp", imageFile);
			
			//File data = new File("data.obf");

			int chunkSize = 15;
			FileWriter mapFiles[][] = new FileWriter[x/chunkSize+1][];
			
			File folder = new File("map");
			if(!folder.exists())
				folder.mkdir();
			folder = null;
			
			for(int i=0; i < mapFiles.length; i++)
				mapFiles[i] = new FileWriter[y/chunkSize+1];
			for(int i=0; i < mapFiles.length; i++)
			{
				for(int j=0; j < mapFiles[i].length; j++)
				{
					File f = new File("map\\map-" + i + "-" + j + ".txt");
					if(!f.exists())
						f.createNewFile();
					mapFiles[i][j] = new FileWriter(f);
				}
			}
			
			//FileWriter fw = new FileWriter(data);
			
			int map[][] = new int [x][];//test only
			for(int i=0; i < map.length; i++)
				map[i] = new int[y];
			/*
			for(int i=0; i < map.length; i++)
				for(int j=0; j < map[i].length; j++)
					map[i][j] = new int[127];	//visina
			*/
			ArrayList <Tree> trees = new ArrayList<Tree>();
			Random r = new Random();
			for(int i=0; i < polygons.size(); i++)
			{
				Polygon cPoly = polygons.get(i);
				int minx = x, maxx = 0, miny = y, maxy = 0;
				
				
				for(int j=0; j < cPoly.vertices.size(); j++)
				{
					Vertex cVertex = cPoly.vertices.get(j);
					if(cVertex.x > maxx)
						maxx = cVertex.x;
					if(cVertex.x < minx)
						minx = cVertex.x;
					if(cVertex.y > maxy)
						maxy = cVertex.y;
					if(cVertex.y < miny)
						miny = cVertex.y;
				}

				if(cPoly.isLand)
				{
					float c = r.nextFloat();
					if(c < 0.7 * (1-((float)cPoly.moisture/(float)maxmoisture)))
					{
						//System.out.println("Drevescek!");
						while(true)
						{
							int treeX = r.nextInt(maxx-minx)+minx;
							int treeZ = r.nextInt(maxy-miny)+miny;
							if(cPoly.isPointInside(new Point(treeX, treeZ)))
							{
								trees.add(new Tree(treeX, treeZ));
								break;
							}
						}
					}
					//else
						//System.out.println("Ni drevescka!");
				}
				
				for(int j=minx; j <= maxx; j++)
					for(int k=miny; k <=maxy; k++)
						if(cPoly.isPointInside(new Point(j, k)))
						{
							map[j][k] = i;
						}
				}
			System.out.println("fw sizes: " + mapFiles.length + " " + mapFiles[0].length);
			for(int i=0; i < map.length; i++)
			{
				for(int j=0; j < map[0].length; j++)
				{
					//System.out.println(map[i][j]);
					
					Polygon cPoly = polygons.get(map[i][j]);
					mapFiles[i/chunkSize][j/chunkSize].write(i + " " + (cPoly.height>0?cPoly.height:1) + " " + j + " ");
					
					if(!cPoly.isLand && !cPoly.isSea)
						mapFiles[i/chunkSize][j/chunkSize].write("lake.jpg\n");
					if(!cPoly.isLand && cPoly.isSea)
						mapFiles[i/chunkSize][j/chunkSize].write("sea.jpg\n");
					if(cPoly.isLand)
						mapFiles[i/chunkSize][j/chunkSize].write("grass.jpg\n");
					
					for(int k= (cPoly.height > 0? cPoly.height:1)-1; k >= 1; k--)
						mapFiles[i/chunkSize][j/chunkSize].write(i + " " + k + " " + j + " dirt.jpg\n");
					mapFiles[i/chunkSize][j/chunkSize].write(i + " " + 0 + " " + j + " bedrock.jpg\n");
				}
				//System.out.println();
			}
			for(int k=0; k < trees.size(); k++)
			{
				int x = trees.get(k).x;
				int z = trees.get(k).z;
				int y = polygons.get(map[x][z]).height+1;

				for(int i=0; i < 5; i++)
					mapFiles[(x)/chunkSize][(z)/chunkSize].write((x) + " " + (y + i) + " " + (z) + " bark.jpg\n");			
				for(int i=0; i < 3; i++)
					for(int j=0; j < 3; j++)
						mapFiles[(x-1+i)/chunkSize][(z-1+j)/chunkSize].write((x-1+i) + " " + (y + 5) + " " + (z-1+j) + " leaves.jpg\n");
				for(int i=0; i < 3; i++)
					for(int j=0; j < 3; j++)
						mapFiles[(x-1+i)/chunkSize][(z-1+j)/chunkSize].write((x-1+i) + " " + (y + 9) + " " + (z-1+j) + " leaves.jpg\n");
				for(int i=0; i < 5; i++)
					for(int j=0; j < 5; j++)
						mapFiles[(x-2+i)/chunkSize][(z-2+j)/chunkSize].write((x-2+i) + " " + (y + 6) + " " + (z-2+j) + " leaves.jpg\n");
				for(int i=0; i < 5; i++)
					for(int j=0; j < 5; j++)
						mapFiles[(x-2+i)/chunkSize][(z-2+j)/chunkSize].write((x-2+i) + " " + (y + 7) + " " + (z-2+j) + " leaves.jpg\n");
				for(int i=0; i < 5; i++)
					for(int j=0; j < 5; j++)
						mapFiles[(x-2+i)/chunkSize][(z-2+j)/chunkSize].write((x-2+i) + " " + (y + 8) + " " + (z-2+j) + " leaves.jpg\n");
			}
			
			//fw.close();
			for(int i=0; i < mapFiles.length; i++)
				for(int j=0; j < mapFiles[i].length; j++)
					mapFiles[i][j].close();
			System.out.println("Screencap put into " + imageFile.getAbsolutePath() + " !");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		char keypressed = e.getKeyChar();
		if(keypressed == 'l' || keypressed == 'L')
			drawLakes = !drawLakes;
		else if(keypressed == 'r' || keypressed == 'R')
			drawRivers = !drawRivers;
		else if(keypressed == 'h' || keypressed == 'H' || keypressed == 'm' || keypressed == 'M')
		{
			drawHeightmap = !drawHeightmap;
			drawInverseMoisturemap = ! drawInverseMoisturemap;
		}
		else if(keypressed == 'e' || keypressed == 'E')
			drawEdges = !drawEdges;
		else if(keypressed == 'v' || keypressed == 'V')
			drawVertices = !drawVertices;
		else if(keypressed == 'p')
			exportBitmap();
		repaint();
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	

}

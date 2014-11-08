import javax.swing.JFrame;


public class RGTI {

	public static void main(String[] args) {
		System.out.println("Hello world!");
		//World testWorld = World.generateNewWorld(new Random(123456), 600, 600);
		NewWorld testNewWorld = new NewWorld();
		testNewWorld.generate();
		
		JFrame frame = new JFrame();
		customPanel panel = new customPanel();
		
        frame.setSize(600,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new customPanel();
        panel.setFocusable(true);
        /*panel.edges1 = testWorld.edges1;
        panel.edges2 = testWorld.edges2;
        panel.polygons = testWorld.polyList;*/
        
        panel.table = testNewWorld.table;
        panel.points = testNewWorld.points;
        
        panel.edges = testNewWorld.edges;
        panel.vertices = testNewWorld.vertices;
        panel.polygons = testNewWorld.polygons;
        panel.maxh = testNewWorld.maxh;
        panel.maxmoisture = testNewWorld.maxmoisture;
        panel.x = testNewWorld.sx;
        panel.y = testNewWorld.sy;
        panel.setSize(panel.x*2, panel.y*2);
        frame.setSize(panel.x*2, panel.y*2);
        frame.add(panel);
        frame.setVisible(true);
        
        //testNewWorld.exportToObj();
	}
	

}

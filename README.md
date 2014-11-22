Simple minecraft clone
======================

Dependencies:
-   lwjgl 2.8.1
-   jbullet 

Map Generation
--------------

This application creates a heightmap with water needed for the game to run.

Quick how to:  
Run the main class (MapGeneration.java) and it will generate a heightmap and display it in a window. The parameters are (sadly) hardcoded in the NewWorld.generate() method. After the map is generated you can use the following key commands:

-   'l' or 'L': toggles display lakes
-   'r' or 'R': toggles display rivers
-   'h', 'H', 'm' or 'M': toggles between heightmap and moisturemap display
-   'e' or 'E': toggles display edges
-   'v' or 'V': toggles display vertices
-   'p': exports the current map as a bitmap into 'ss.bmp' and the map into 'map\map-x-y.txt'

The generated map folder then needs to be put into the game folder.



Game
--------
The game project is a simple minecraft clone which allows moving around, removing and adding blocks.

Quick how to:  
You need the map folder with a generated heightmap from the Map Generation project. After that simply run the application and play the game. Controls are wasd for movement and left and right mouse clicks for adding / removing tiles.

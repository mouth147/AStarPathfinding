# AStarPathfinding

This is a multithreaded A Star pathfinding algorithm built using Java and JavaFX.
The UI allows you to generate a new map, or load an existing map from a txt file. Generating a new map consists of rough terrain, rivers, and mountains.
* Rough Terrain is half the speed of normal terrain.
* Rivers increase your speed by 4x.
* Mountains are impassable objects.

The heuristic used is a [diagonal distance](http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html#heuristics-for-grid-maps).

### Usage

You can [import this as an Eclipse project](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftasks-importproject.htm) and run the Pathfinder.java file.

Once the initial program is running you're brought to a menu that has two options. Generate a new map, or load an existing map. Generating a new map can take some time because the rivers must meet a certain criteria. Once you have a map generated or loaded you can do multiple things with it. You can export the current map with start and goal tiles via the menu, or simply press "CTRL+S". You can generate new start/goal tiles via the Current Map menu. You can paint a path (if it exists) using the 'Find optimal path using A star', and clear that path to get the original map. Adjusting the W slider adjusts the weight value which will produce a trade-off of how fast the Algorithm calculates vs. the shortest path. 

### Known Bugs

* Sometimes when building highways, it seems that a highway is broken/has missing tiles.

# AStarPathfinding

This is a multithreaded A Star pathfinding program built using Java and JavaFX.
It allows you to use the following algorithms:
* [A\* Search](https://en.wikipedia.org/wiki/A*_search_algorithm)
* [Weighted A\* Search](https://en.wikipedia.org/wiki/A*_search_algorithm#Bounded_relaxation)
* [Uniform Cost Search](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs)
* [Independent Multi-Heuristic A\*](https://www.cs.cmu.edu/~maxim/files/mha_ijrr15.pdf)
* [Shared Multi-Heuristic A\*](https://www.cs.cmu.edu/~maxim/files/mha_ijrr15.pdf)

The UI allows you to generate a new map, or load an existing map from a txt file. Generating a new map consists of rough terrain, rivers, and mountains.
* Rough Terrain is half the speed of normal terrain.
* Rivers increase your speed by 4x.
* Mountains are impassable objects.
Within the UI, you can edit weight values for the Weighted A\*, IMHA\* and SMHA\*, change between different heuristics and view values that are associated with these algorithms.

The different heuristics that are used include: 
* [Diagonal Distance](http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html#heuristics-for-grid-maps).
* [Manhattan Distance](https://en.wikipedia.org/wiki/Taxicab_geometry)
* [Euclidean Distance](https://en.wikipedia.org/wiki/Euclidean_distance)
* [Fast Approximate Distance](http://www.flipcode.com/archives/Fast_Approximate_Distance_Functions.shtml)
* My own heuristic which is titled "Enhanced Manhattan". It takes the sum of the Euclidean and Manhattan and divides it by 100. The reason I choose 100 is because the minimum path length between the start and goal tiles are 100 in manhattan distance. So by dividing it we attempt to make the algorithm admissable.

### Usage

You can [import this as an Eclipse project](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftasks-importproject.htm) and run the Pathfinder.java file.

Once the initial program is running you're brought to a menu that has two options. Generate a new map, or load an existing map. Generating a new map can take some time because the rivers must meet a certain criteria. Once you have a map generated or loaded you can do multiple things with it. You can export the current map with start and goal tiles via the menu, or simply press "CTRL+S". You can generate new start/goal tiles via the Current Map menu. You can paint a path (if it exists) using the 'Find optimal path using A star', and clear that path to get the original map. Adjusting the W slider adjusts the weight value which will produce a trade-off of how fast the Algorithm calculates vs. the shortest path. 

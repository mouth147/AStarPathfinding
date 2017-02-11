import static org.junit.Assert.*;

import java.util.ArrayList;

public class Test {

	@org.junit.Test
	public void test() {
		//fail("Not yet implemented");
		
		Coords c1 = new Coords(5, 10);
		Coords c2 = new Coords(6, 10);
		Coords c3 = new Coords(5, 10);
		String str = "6,10";
		Coords c4 = TileMap.parseCoords(str);
		System.out.println(c4);
		
		assertFalse("Shouldn't be equal", c1.equals(c2));
		assertTrue("Should be equal", c1.equals(c3));
		assertTrue("Should be equal", c2.equals(c4));
		//assertTrue("Should fail", c1.equals(c2));
		
//		Node[][] testTiles = new Node[10][10];
//		for (int i = 0; i < 10; i++) {
//			for (int j = 0; j < 10; j++) {
//				Node newNode = new Node();
//				newNode.setCoords(new Coords(i, j));
//				//System.out.println("Current x,y: " + i + "," + j + " | Coords: " + newNode.getCoords());
//				testTiles[j][i] = newNode;
//				newNode.setTerrain('1');
//			}
//		}
//		for (int i = 1; i < 9; i++) {
//			testTiles[0][i].setTerrain('a');
//		}
//		for (int i = 0; i < 7; i++) {
//			testTiles[i][8].setTerrain('a');
//		}
//		testTiles[5][5].setTerrain('0');
//		for (int y = 0; y < 10; y++) {
//			for (int x = 0; x < 10; x++) {
//				System.out.print(testTiles[y][x].getTerrain());
//			}
//			System.out.println();
//		}
//		HeuristicSearch a = new AStar(testTiles, new Coords(0, 0), new Coords(7, 9), "Manhattan");
//		ArrayList<Node> path = a.solve();
//		for (Node curr : path) {
//			System.out.println(curr);
//		}
		TileMap map = new TileMap();
		map.generateMap();
		map.generateStartAndGoal();
		
		HeuristicSearch astar = new SequentialAStar(map.getTiles(), map.getStartTile(), map.getGoalTile(), 2.0, 2.0);
		astar.solve();
		
		
		//benchmark();
	}
	
	public void pError(TileMap map, String heuristics, double wValue) {
		System.out.println("Start tile: " + map.getStartTile());
		System.out.println("Goal tile: " + map.getGoalTile());
		if (heuristics != null) System.out.println("Heuristic: " + heuristics);
		if (wValue != 0) System.out.println("Weight value: " + wValue);
	}
	
	public void benchmark() {
		
		TileMap map = new TileMap();
		AStar search;
		WeightedAStar wSearch;
		UniformCostSearch uSearch;
		map.generateMap();
		double[] runtime = new double[16];
		double[] pathLength = new double[16];
		double[] nodesExpanded = new double[16];
		String[] heuristics = {"Diagonal", "Manhattan", "Euclidean", "Enhanced Manhattan", "Fast Approximate"};
		
		for (int i = 0; i < 10; i++) {
			map.generateStartAndGoal();
			System.out.println("Current S&G: " + i);
			
			for (int j = 0; j < 5; j++) {
				search = new AStar(map.getTiles(), map.getStartTile(), map.getGoalTile(), heuristics[j]);
				
				long startTime = System.nanoTime();
				ArrayList<Node> path = search.solve();
				long endTime = System.nanoTime();
				
				runtime[j] += (endTime - startTime) / 1000000;
				if (path != null) {
					pathLength[j] += path.size();
				} else {
					pError(map, heuristics[j], 0);
				}
				nodesExpanded[j] += search.nodesExpanded;
			}
			
			for (int j = 5; j < 10; j++) {
				wSearch = new WeightedAStar(map.getTiles(), map.getStartTile(), map.getGoalTile(), heuristics[j % 5], 1.5);
				
				long startTime = System.nanoTime();
				ArrayList<Node> path = wSearch.solve();
				long endTime = System.nanoTime();
				
				runtime[j] += (endTime - startTime) / 1000000;
				if (path != null) {
					pathLength[j] += path.size();
				} else {
					pError(map, heuristics[j % 5], 1.5);
				}
				nodesExpanded[j] += wSearch.nodesExpanded;
				
				wSearch.setwValue(3.0);
				startTime = System.nanoTime();
				path = wSearch.solve();
				endTime = System.nanoTime();
				
				runtime[j + 5] += (endTime - startTime) / 1000000;
				if (path != null) {
					pathLength[j + 5] += path.size();
				} else {
					pError(map, heuristics[j % 5], 3.0);
				}
				nodesExpanded[j + 5] += wSearch.nodesExpanded;
			}
			
			uSearch = new UniformCostSearch(map.getTiles(), map.getStartTile(), map.getGoalTile());
			
			long startTime = System.nanoTime();
			ArrayList<Node> path = uSearch.solve();
			long endTime = System.nanoTime();
			
			runtime[15] += (endTime - startTime) / 1000000;
			if (path != null) pathLength[15] += path.size();
			nodesExpanded[15] += uSearch.nodesExpanded;
		}
		
		System.out.println("Map Benchmarks");
		System.out.println("--------------");
		for (int i = 0; i < 16; i++) {
			runtime[i] /= 10.0;
			pathLength[i] /= 10.0;
			nodesExpanded[i] /= 10.0;
			
		}
		
		System.out.println("A *");
		for (int i = 0; i < 5; i++) {
			System.out.println("---------------------------");
			System.out.println("Heuristic: " + heuristics[i]);
			System.out.println("Runtime: " + runtime[i]);
			System.out.println("Path Length: " + pathLength[i]);
			System.out.println("Nodes Expanded: " + nodesExpanded[i]);
		}
		
		for (int i = 5; i < 10; i++) {
			System.out.println("Weighted A* 1.5");
			System.out.println("Heuristic: " + heuristics[i % 5]);
			System.out.println("Runtime: " + runtime[i]);
			System.out.println("Path Length: " + pathLength[i]);
			System.out.println("Nodes Expanded: " + nodesExpanded[i]);
		}
		
		for (int i = 10; i < 15; i++) {
			System.out.println("Weighted A* 3.0");
			System.out.println("Heuristic: " + heuristics[i % 5]);
			System.out.println("Runtime: " + runtime[i]);
			System.out.println("Path Length: " + pathLength[i]);
			System.out.println("Nodes Expanded: " + nodesExpanded[i]);
		}
		
		System.out.println("Uniform Cost Search");
		System.out.println("Runtime: " + runtime[15]);
		System.out.println("Path Length: " + pathLength[15]);
		System.out.println("Nodes Expanded: " + nodesExpanded[15]);
		
	}

}

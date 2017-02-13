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
		
		phase2();
		
	}

	/**
	 * 
	 */
	public void phase2() {
		TileMap[] maps = generateMaps();
		Coords[][][] coords = generateCoords(maps);
		
		for (int i = 0; i < 5; i++) {
			
			System.out.println("***********   Map " + (i + 1) + "   ***********");
			System.out.println("A*");
			System.out.println("--------------------");
			benchmarkHeuristic(maps[i], coords[i], "Manhattan", 0, 0, "A*");
			System.out.println("--------------------");
			
			System.out.println("Sequential A*");
			System.out.println("---------------------");
			benchmarkHeuristic(maps[i], coords[i], "Manhattan", 1.50, 3.0, "SA*");
			System.out.println("---------------------");
			
			System.out.println("Integrated A*");
			System.out.println("---------------------");
			benchmarkHeuristic(maps[i], coords[i], "Manhattan", 1.50, 3.0, "IA*");
			System.out.println("---------------------");
		}
	}
	
	public void benchmarkHeuristic(TileMap map, Coords[][] coords, String heuristic, double w1, double w2, String algorithm) {
		
		double pathLength = 0;
		double nodesExpanded = 0;
		double runtime = 0;
		double startTime;
		double endTime;
		ArrayList<Node> path = new ArrayList<Node>();
		
		HeuristicSearch search = selectAlgorithm(algorithm, map, heuristic, w1, w2);
		
		for (int i = 0; i < 10; i++) {

			search.start = coords[i][0];
			search.goal = coords[i][1];
			
			search.resetMap();
			
			startTime = System.nanoTime();
			path = search.solve();
			endTime = System.nanoTime();

			pathLength += path.size();
			nodesExpanded += search.nodesExpanded;
			runtime += ((endTime - startTime) / 1000000);

		}
		
		pathLength /= 10;
		nodesExpanded /= 10;
		runtime /= 10;

		System.out.println("Avg Path Length: " + pathLength);
		System.out.println("Avg Nodes Expanded: " + nodesExpanded);
		System.out.println("Avg Runtime: " + runtime + " ms");
		
	}
	
	public TileMap[] generateMaps() {
		
		TileMap[] maps = new TileMap[5];
		
		for (int i = 0; i < 5; i++) {
			maps[i] = new TileMap();
			maps[i].generateMap();
		}
		
		return maps;
	}
	
	public Coords[][][] generateCoords(TileMap[] maps) {
		
		Coords[][][] startAndGoals = new Coords[5][10][2];
		
		for (int map = 0; map < 5; map++) {
			for (int coords = 0; coords < 10; coords++) {
					maps[map].generateStartAndGoal();
					startAndGoals[map][coords][0] = maps[map].getStartTile();
					startAndGoals[map][coords][1] = maps[map].getGoalTile();
			}
		}
		
		return startAndGoals;
	}
	
	public HeuristicSearch selectAlgorithm(String algorithm, TileMap map, String heuristic, double w1, double w2) {
		
		switch(algorithm) {
		
		case "A*":
			return new AStar(map.getTiles(), map.getStartTile(), map.getGoalTile(), heuristic);

		case "WA*":
			return new WeightedAStar(map.getTiles(), map.getStartTile(), map.getGoalTile(), heuristic, w1);
			
		case "UCS":
			return new UniformCostSearch(map.getTiles(), map.getStartTile(), map.getGoalTile());
			
		case "SA*":
			return new SequentialAStar(map.getTiles(), map.getStartTile(), map.getGoalTile(), w1, w2);
			
		case "IA*":
			return new IntegratedAStar(map.getTiles(), map.getStartTile(), map.getGoalTile(), w1, w2);
			
		default:
			break;
		
		}
		
		return null;
	}

}

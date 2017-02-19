import static org.junit.Assert.*;

import java.util.ArrayList;

public class Test {
	
	String runtimeStr = "";
	String pathlengthStr = "";
	String nodesExpandedStr = "";
	String memUsedStr = "";
	
	@org.junit.Test
	public void test() {
		//fail("Not yet implemented");
		
		Coords c1 = new Coords(5, 10);
		Coords c2 = new Coords(6, 10);
		Coords c3 = new Coords(5, 10);
		String str = "6,10";
		Coords c4 = TileMap.parseCoords(str);
		//System.out.println(c4);
		
		assertFalse("Shouldn't be equal", c1.equals(c2));
		assertTrue("Should be equal", c1.equals(c3));
		assertTrue("Should be equal", c2.equals(c4));
		
		//getMemoryInfo();
		//phase1();
		phase2();
		
	}
	
	public void getMemoryInfo() {
		System.out.println("Memory Requirements");
		System.out.println("-----------------------------------");
		
		TileMap map = new TileMap();
		System.out.println("TileMap: " + ObjectSizeFetcher.getObjectSize(map));
		System.out.println("Coords: " + ObjectSizeFetcher.getObjectSize(new Coords()));
		System.out.println("Node: " + ObjectSizeFetcher.getObjectSize(new Node()));
		System.out.println("Node Comparator: " + ObjectSizeFetcher.getObjectSize(new NodeComparator()));
		System.out.println("AStar: " + ObjectSizeFetcher.getObjectSize(new AStar(map.getTiles(), new Coords(), new Coords(), "Manhattan")));
		System.out.println("Weighted AStar: " + ObjectSizeFetcher.getObjectSize(new WeightedAStar(map.getTiles(), new Coords(), new Coords(), "Manhattan", 2.0)));
		System.out.println("Sequential: " + ObjectSizeFetcher.getObjectSize(new SequentialAStar(map.getTiles(), new Coords(), new Coords(), 1.0, 2.0)));
		System.out.println("Integrated: " + ObjectSizeFetcher.getObjectSize(new IntegratedAStar(map.getTiles(), new Coords(), new Coords(), 1.0, 2.0)));
		System.out.println("UCS: " + ObjectSizeFetcher.getObjectSize(new UniformCostSearch(map.getTiles(), new Coords(), new Coords())));

	}
	
	public void phase1() {
		TileMap[] maps = generateMaps();
		Coords[][][] coords = generateCoords(maps);
		String[] heuristics = {"Diagonal", "Manhattan", "Euclidean", "Enhanced Manhattan", "Fast Approximate"};

		
		for (int i = 0; i < 5; i++) {
			//System.out.println("***********   Map " + (i + 1) + "   ***********");
			
			if (i != 0) System.out.println("\\newpage");
			System.out.println("\\begin{center}");
			System.out.println("Map " + (i + 1));
			System.out.println("\\end{center}");
			System.out.println("\\begin {tabular}{ |c|c | c | c | c | c |}");
			System.out.println("\\hline");
			System.out.println("& Diagonal & Manhattan & Euclidean & Abstract & Fast Approximate \\\\ \\hline");
			setString();
			System.out.println("\\multicolumn{6}{|c|}{A* Search Algorithm} \\\\ \\hline");
			for (int j = 0; j < heuristics.length; j++) {
				//System.out.println("A*");
				//System.out.println("--------------------");
				benchmarkHeuristic(maps[i], coords[i], heuristics[j], 0, 0, "A*");
				//System.out.println("--------------------");
			}
			printAndReset();
			
			setString();
			System.out.println("\\multicolumn{6}{|c|}{Weighted A* Search Algorithm - 1.5} \\\\ \\hline");
			for (int j = 0; j < heuristics.length; j++) {
				//System.out.println("Weighted A* 1.5");
				//System.out.println("---------------------");
				benchmarkHeuristic(maps[i], coords[i], heuristics[j], 1.50, 3.0, "WA*");
				//System.out.println("---------------------");
			}
			printAndReset();
			
			setString();
			System.out.println("\\multicolumn{6}{|c|}{Weighted A* Search Algorithm - 3.0} \\\\ \\hline");
			for (int j = 0; j < heuristics.length; j++) {
				//System.out.println("Weighted A* 3.0");
				//System.out.println("---------------------");
				benchmarkHeuristic(maps[i], coords[i], heuristics[j], 3.0, 3.0, "WA*");
				//System.out.println("---------------------");
			}
			printAndReset();

			setString();
			System.out.println("\\multicolumn{6}{|c|}{Uniform Cost Search} \\\\ \\hline");
			//System.out.println("Uniform Cost Search");
			//System.out.println("---------------------");
			benchmarkHeuristic(maps[i], coords[i], "Manhattan", 1.50, 3.0, "UCS");
			//System.out.println("---------------------");
			printAndReset();
			System.out.println("\\end{tabular}");
			System.out.println("\\\\\\\\");

		}
	}

	/**
	 * 
	 */
	public void setString() {
		pathlengthStr += "Path Length";
		runtimeStr += "Runtime";
		nodesExpandedStr += "Nodes Expanded";
		memUsedStr += "Memory Used";
	}

	/**
	 * 
	 */
	public void printAndReset() {
		System.out.println(runtimeStr + "\\\\ \\hline");
		System.out.println(pathlengthStr + "\\\\ \\hline");
		System.out.println(nodesExpandedStr + "\\\\ \\hline");
		System.out.println(memUsedStr + "\\\\ \\hline");
		runtimeStr = "";
		pathlengthStr = "";
		nodesExpandedStr = "";
		memUsedStr = "";
	}

	/**
	 * 
	 */
	public void phase2() {
		TileMap[] maps = generateMaps();
		Coords[][][] coords = generateCoords(maps);
		
		for (int i = 0; i < 5; i++) {
			
			System.out.println("\\begin{center}");
			System.out.println("Map " + (i + 1));
			System.out.println("\\end{center}");
			System.out.println("\\begin {tabular}{ |c|c | c | c |}");
			System.out.println("\\hline");
			System.out.println("& A* & Sequential A* & Integrated A* \\\\ \\hline");
			setString();
			benchmarkHeuristic(maps[i], coords[i], "Manhattan", 0, 0, "A*");
			
			benchmarkHeuristic(maps[i], coords[i], "Manhattan", 1.50, 3.0, "SA*");
			
			benchmarkHeuristic(maps[i], coords[i], "Manhattan", 1.50, 3.0, "IA*");
			printAndReset();
			System.out.println("\\end{tabular}");
			System.out.println("\\\\\\\\");
		}
	}
	
	public void benchmarkHeuristic(TileMap map, Coords[][] coords, String heuristic, double w1, double w2, String algorithm) {
		
		double pathLength = 0;
		double nodesExpanded = 0;
		double runtime = 0;
		double memUsed = 0;
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
			memUsed += (56 * search.nodesOpened);

		}
		
		pathLength /= 10;
		nodesExpanded /= 10;
		runtime /= 10;
		memUsed /= 10;
		
		runtime = Math.round(runtime * 100);
		runtime /= 100;
		
		if (algorithm.equals("UCS")) {
			pathlengthStr += ("&\\multicolumn{5}{|c|}{" + Double.toString(pathLength) + "}");
			runtimeStr += ("&\\multicolumn{5}{|c|}{" + Double.toString(runtime) + "ms}");
			nodesExpandedStr += ("&\\multicolumn{5}{|c|}{" + Double.toString(nodesExpanded) + "}");
			memUsedStr += ("&\\multicolumn{5}{|c|}{" + Double.toString(memUsed) + "B}");
		} else {
			pathlengthStr += ("&" + Double.toString(pathLength));
			runtimeStr += ("&" + Double.toString(runtime) + "ms");
			nodesExpandedStr += ("&" + Double.toString(nodesExpanded));
			memUsedStr += ("&" + Double.toString(memUsed) + "B");
		}
		
		/*System.out.println("Heuristic: " + heuristic);
		System.out.println("Avg Path Length: " + pathLength);
		System.out.println("Avg Nodes Expanded: " + nodesExpanded);
		System.out.println("Avg Runtime: " + runtime + " ms");
		System.out.println("Memory used: " + memUsed + " bytes");*/
		
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

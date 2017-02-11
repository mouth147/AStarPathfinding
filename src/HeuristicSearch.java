import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public abstract class HeuristicSearch {

	protected Node [][] tiles;
	boolean DEBUG = false;
	protected Coords start;
	protected Coords goal;
	protected NodeComparator fComparator = new NodeComparator();
	protected PriorityQueue<Node> openList = new PriorityQueue<Node>(11, fComparator);
	protected HashSet<Node> closedList = new HashSet<Node>();
	protected double nodesExpanded;

	
	public HeuristicSearch(Node [][] tiles, Coords start, Coords goal) {
		this.tiles = tiles;
		this.start = start;
		this.goal = goal;
	}
	
	abstract public ArrayList<Node> solve();

	/**
	 * @param heuristic
	 * @param startNode
	 */
	public void heuristicSwitch(String heuristic, Node startNode) {
		switch(heuristic) {
			case "Diagonal":
				startNode.setH(diagonalDistanceHeuristic(startNode));
				break;
			case "Manhattan":
				startNode.setH(manhattanDistance(startNode));
				break;
			case "Fast Approximate":
				startNode.setH(fastApproximateDistance(startNode));
				break;
			case "Enhanced Manhattan":
				startNode.setH(enhancedManhattan(startNode));
				break;
			case "Euclidean":
			default:
				startNode.setH(euclideanDistance(startNode));
				break;
		}
	}

	/**
	 * @param current
	 * @return
	 */
	public ArrayList<Node> optimalPath(Node current) {
		ArrayList<Node> path = new ArrayList<Node>();
		for (Node tmp = current; tmp != null ; tmp = tmp.getParent()){
			//System.out.println(tmp.getCoords());
			path.add(0, tmp);
		}
		
		return path;
	}
	
	public boolean isValidNeighbor(Node current) {
		
		if (current == null) {
			return false;
		}
		
		Coords coords = current.getCoords();
		
		if (closedList.contains(current)) {
			return false;
		} else if (current.getTerrain() == '0') {
			return false;
		} else if ((coords.getX() < 0 || coords.getX() >= tiles[0].length) || (coords.getY() < 0 || coords.getY() >= tiles.length)) {
			return false;
		}
		
		if (DEBUG) System.out.println("Current node is a valid neighbor: " + current.getCoords());
		
		return true;
	}
	
	public void getNeighbors(Node current) {
		Coords coords = current.getCoords();

		int maxX = (coords.getX() - 1) + 3;
		int maxY = (coords.getY() - 1) + 3;
		int counter = -1;
		
		for (int i = 0, currY = coords.getY() - 1; currY < maxY; currY++, i++) {
			for (int j = 0, currX = coords.getX() - 1; currX < maxX; currX++, j++) {
				if (i == 1 && j == 1) {
					continue;
				} else if ((currX < 0 || currX >= tiles[0].length) || (currY < 0 || currY >= tiles.length)) {
					continue;
				}
				counter++;
				Node neighbor = tiles[currY][currX];
				//System.out.println("Neighbor: " + neighbor.getCoords());
				if ((i == 0 && j == 0) || (i == 0 && j == 2) || (i == 2 && j == 0) || (i == 2 && j == 2)) {
					if (neighbor.getG() == 0) {
						double gValue = getGValue(current, neighbor, true);
						neighbor.setG(gValue);
					}
				} else {
					if (neighbor.getG() == 0) {
						double gValue = getGValue(current, neighbor, false);
						neighbor.setG(gValue);
					}
				}
				current.neighbors[counter] = neighbor;
				
			}
		}
	}
	
	public double diagonalDistanceHeuristic(Node next) {
		double dx, dy, d1, d2;
		d1 = 1;
		d2 = 1.414;
		Coords coords = next.getCoords();
		
		dx = Math.abs(coords.getX() - goal.getX());
		dy = Math.abs(coords.getY() - goal.getY());
		return d1 * (dx + dy) + (d2 - 2 * d1) * Math.min(dx, dy);
	}
	
	public double manhattanDistance(Node next) {
		
		double dx, dy;
		Coords coords = next.getCoords();
		
		dx = Math.abs(coords.getX() - goal.getX());
		dy = Math.abs(coords.getY() - goal.getY());
		
		return (dx + dy);
	}
	
	public double enhancedManhattan(Node next) {
		
		double dx, dy, minPathLength = 100, avgDiagonal = ((Math.sqrt(2) + Math.sqrt(8)) / 2), avgNon = 1.5;
		Coords coords = next.getCoords();
		
		dx = Math.abs(coords.getX() - goal.getX());
		dy = Math.abs(coords.getY() - goal.getY());
		
		return ((avgNon * (dx + dy)) + (avgDiagonal * Math.sqrt(dx * dx + dy * dy))) / minPathLength;
	}
	
	public double euclideanDistance(Node next) {
		
		double dx, dy;
		Coords coords = next.getCoords();
		
		dx = Math.abs(coords.getX() - goal.getX());
		dy = Math.abs(coords.getY() - goal.getY());
		
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/*
	 * Based on a 2003 article on a function that can approximate distance without using square roots 
	 * within 2.5% average error (5% maximum error). This makes the function potentially inadmissible
	 * as a heuristic function for A*. Should mostly work similarly to Euclidean distance.
	 * 
	 * Source: http://www.flipcode.com/archives/Fast_Approximate_Distance_Functions.shtml
	 */
	public double fastApproximateDistance(Node next) {
		
		int dx, dy;
		int min, max, approx;
		Coords coords = next.getCoords();
		
		dx = Math.abs(coords.getX() - goal.getX());
		dy = Math.abs(coords.getY() - goal.getY());
		
		max = Math.max(dx, dy);
		min = Math.min(dx, dy);
		
		/* EXPLANATION FOR THE MAGIC NUMBERS
		 * 1007 and 441 are the numerators for the coefficients of the linear combinations of the functions*/
		approx = (max * 1007) + (min * 441);
		if (max < (min << 4 ))
				approx -= (max * 40); 			// figuring out how to explain this
		
		/*Original code has " >> 10" in place of "/ 1024.0 "; I'm just afraid to bitshift right*/
		return (double)((approx + 512) / 1024.0 );
	}
	
	public double getGValue(Node current, Node next, boolean diagonal) {
		
		char currTerrain = current.getTerrain();
		char nextTerrain = next.getTerrain();
		
		if (diagonal) {
			if ((currTerrain == '1' || currTerrain == 'a') && nextTerrain == '1') {
				return Math.sqrt(2);
			} else if ((currTerrain == '1' || currTerrain == 'a') && nextTerrain == 'a') {
				return Math.sqrt(2) / 4.0;
			} else if ((currTerrain == 'b' ||currTerrain == '2') && nextTerrain == '2') {
				return Math.sqrt(8);
			} else if ((currTerrain == '2' || currTerrain == 'b') && nextTerrain == 'b') {
				return Math.sqrt(8) / 4.0;
			} else if ((currTerrain == '2' && nextTerrain == '1') || (currTerrain == '1' && nextTerrain == '2')){
				return ((Math.sqrt(2) + Math.sqrt(8)) / 2.0);
			} else {
				return ((Math.sqrt(2) + Math.sqrt(8)) / 2.0) / 4.0;
			}
		} else {
			if ((currTerrain == '1' || currTerrain == 'a') && nextTerrain == '1') {
				return 1.0;
			} else if (currTerrain == '2' && nextTerrain == '2') {
				return 2.0;
			} else if ((currTerrain == '2' && nextTerrain == '1') || (currTerrain == '1' && nextTerrain == '2')) {
				return 1.5;
			} else if ((currTerrain == 'a' || currTerrain == '1') && nextTerrain == 'a')  {
				return 0.25;
			} else if ((currTerrain == 'b' || currTerrain == '2') && nextTerrain == 'b') {
				return 0.5;
			} else {
				return 1.5/4;
			}
		}
		
		
	}


}

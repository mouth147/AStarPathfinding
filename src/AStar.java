import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class AStar {

	private Node [][] tiles;
	private Coords start;
	private Coords goal;
	private NodeComparator fComparator = new NodeComparator();
	private PriorityQueue<Node> openList = new PriorityQueue<Node>(11, fComparator);
	private HashSet<Node> closedList = new HashSet<Node>();
	HashMap<Node, Double> gValues = new HashMap<Node, Double>(100);
	//HashMap<Node, Double> fValues = new HashMap<Node, Double>(100);
	
	public AStar(Node [][] tiles, Coords start, Coords goal) {
		this.tiles = tiles;
		this.start = start;
		this.goal = goal;
	}
	
	public ArrayList<Node> solve(Double wValue, boolean isUniformCost, String heuristic) {

		Node startNode = new Node();
		Node current = null;
		startNode.setCoords(start);
		startNode.setF(0);
		startNode.setG(0);
		
		openList.add(startNode);
		
		System.out.println("Starting algorithm...");
		while(!openList.isEmpty()) {
			//System.out.println("openList is not empty.");
			current = openList.remove();
			closedList.add(current);
			
			//System.out.println("Coords: " + current.getCoords());
			if (current.getCoords().equals(goal)) {
				//System.out.println("Path found!");
				System.out.println("Nodes expanded: " + closedList.size());
				return optimalPath(current);
			}
			
			getNeighbors(current);
			
			for (int i = 0; i < current.neighbors.length; i++) {
				Node neighbor = current.neighbors[i];
				//System.out.println("Current neighbor: " + neighbor);
				if (!isValidNeighbor(neighbor)) {
					continue;
				}
				
				double tentativeGScore = current.getG() + neighbor.getG();
				
				if (!openList.contains(neighbor)) {
					openList.add(neighbor);
				} else if (tentativeGScore >= gValues.get(current.neighbors[i])) {
					continue;
				}
				
				gValues.replace(neighbor, tentativeGScore);
				neighbor.setH(diagonalDistanceHeuristic(neighbor));
				if (isUniformCost) {
					neighbor.setF(tentativeGScore);
				} else {
					switch(heuristic) {
					case "Diagonal":
						neighbor.setF(neighbor.getG() + (wValue * diagonalDistanceHeuristic(neighbor)));
						break;
					case "Manhattan":
						neighbor.setF(neighbor.getG() + (wValue * manhattanDistance(neighbor)));
						break;
<<<<<<< HEAD
					case "Fast Approximate":
						neighbor.setF(neighbor.getG() + (wValue * fastApproximateDistance(neighbor)));
=======
					case "Enhanced Manhattan":
						neighbor.setF(neighbor.getG() + (wValue * enhancedManhattan(neighbor)));
>>>>>>> 520930294f0aa845a2b6a701060dfa8630d19afe
						break;
					case "Euclidean":
					default:
						neighbor.setF(neighbor.getG() + (wValue * euclideanDistance(neighbor)));
						break;
					}
				}
				neighbor.setParent(current);
				
			}
		}
		
		System.out.println("Nodes expanded: " + closedList.size());
		return null;
		
	}

	/**
	 * @param current
	 * @return
	 */
	public ArrayList<Node> optimalPath(Node current) {
		ArrayList<Node> path = new ArrayList<Node>();
		for (Node tmp = current; tmp.getParent() != null ; tmp = tmp.getParent()){
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
		
		//System.out.println("Current node is a valid neighbor: " + current.getCoords());
		
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
					neighbor.setG(getGValue(current, neighbor, true));
				} else {
					neighbor.setG(getGValue(current, neighbor, false));
				}
				gValues.put(neighbor, neighbor.getG());
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
			if (currTerrain == '1' && nextTerrain == '1') {
				return Math.sqrt(2);
			} else if (currTerrain == '2' && nextTerrain == '2') {
				return Math.sqrt(8);
			} else {
				return ((Math.sqrt(2) + Math.sqrt(8)) / 2);
			}
		} else {
			if (currTerrain == '1' && nextTerrain == '1') {
				return 1;
			} else if (currTerrain == '2' && nextTerrain == '2') {
				return 2;
			} else if ((currTerrain == '2' && nextTerrain == '1') || (currTerrain == '1' && nextTerrain == '2')) {
				return 1.5;
			} else if (currTerrain == 'a' && nextTerrain == 'a') {
				return 1/4;
			} else if (currTerrain == 'b' && nextTerrain == 'b') {
				return 2/4;
			} else {
				return 1.5/4;
			}
		}
		
		
	}
}

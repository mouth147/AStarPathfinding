import java.util.ArrayList;

public class AStar extends HeuristicSearch {
	
	protected String heuristic;

	public AStar(Node[][] tiles, Coords start, Coords goal, String heuristic) {
		super(tiles, start, goal);
		this.heuristic = heuristic;
	}
	
	public ArrayList<Node> solve() {

		Node startNode = tiles[start.getY()][start.getX()];
		Node current = null;
		heuristicSwitch(heuristic, startNode);
		startNode.setG(0);
		startNode.setF(startNode.getG() + startNode.getH());
		
		openList.add(startNode);
		
		//System.out.println("Starting algorithm...");
		while(!openList.isEmpty()) {
			if (DEBUG) {
				System.out.print("Open list before: ");
				for (Node curr : openList) {
					System.out.print("(" + curr.getCoords() + ") - " + curr.getF() + " ");
				}
				System.out.println();
			}
			//System.out.println("openList is not empty.");
			current = openList.remove();
			closedList.add(current);
			
			if (DEBUG) {
				System.out.println("---------------------------------------");
				System.out.println("Current Coords: " + current.getCoords() + " with F value: " + current.getF());
				System.out.println("---------------------------------------");
				System.out.print("Open list: ");
				for (Node curr : openList) {
					System.out.print("(" + curr.getCoords() + ") - " + curr.getF() + " ");
				}
				System.out.print("Closed list: ");
				for (Node curr : closedList) {
					System.out.print("(" + curr.getCoords() + ") ");
				}
				System.out.println();
			}
			
			if (current.getCoords().equals(goal)) {
				//System.out.println("Path found!");
				//System.out.println("Nodes expanded: " + closedList.size());
				nodesExpanded = closedList.size();
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
				
				if (!openList.contains(neighbor) || tentativeGScore < neighbor.getG()) {
					neighbor.setG(tentativeGScore);
					heuristicSwitch(heuristic, neighbor);
					neighbor.setF(neighbor.getG() + neighbor.getH());
					neighbor.setParent(current);
					if (!openList.contains(neighbor)) openList.add(neighbor);
				} 
				
				if (DEBUG) {
					System.out.println("Neighbor coords: " + neighbor.getCoords());
					System.out.println("H: " + neighbor.getH());
					System.out.println("G: " + neighbor.getG());
					System.out.println("F: " + neighbor.getF());
				}
				
				
			}
		}
		
		System.out.println("Path not found");
		nodesExpanded = closedList.size();
		return null;
		
	}

}

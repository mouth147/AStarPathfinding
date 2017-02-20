import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class IntegratedAStar extends HeuristicSearch {
	protected String[] heuristics = {"Manhattan", "Euclidean", "Enhanced Manhattan", "Fast Approximate", "Diagonal"};
	protected int numOfHeuristics = heuristics.length;
	protected PriorityQueue<Node>[] open;
	protected HashSet<Node> closedINAD;
	protected HashSet<Node> closedANCHOR;
	protected HashMap<Node, Double>[] fValues;
	protected HashMap<Node, Double> gValues;
	protected double w1, w2;
	

	@SuppressWarnings("unchecked")
	public IntegratedAStar(Node[][] tiles, Coords start, Coords goal, double w1, double w2) {
		super(tiles, start, goal);
		open = (PriorityQueue<Node>[])new PriorityQueue[numOfHeuristics];
		closedINAD = new HashSet<Node>();
		closedANCHOR = new HashSet<Node>();
		fValues = new HashMap[numOfHeuristics];
		gValues = new HashMap<Node, Double>();
		this.w1 = w1;
		this.w2 = w2;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList<Node> solve() {
		Node startNode = tiles[start.getY()][start.getX()];
		Node goalNode = tiles[goal.getY()][goal.getX()];
		
		gValues.put(startNode, 0.0);
		gValues.put(goalNode, Double.POSITIVE_INFINITY);
		
		startNode.setParent(null);
		goalNode.setParent(null);
		
		for (int currHeuristic = 0; currHeuristic < numOfHeuristics; currHeuristic++) {
			open[currHeuristic] = new PriorityQueue(11, fComparator);
			fValues[currHeuristic] = new HashMap<Node, Double>();
			
			startNode.setF(key(startNode, currHeuristic));
			fValues[currHeuristic].put(startNode, startNode.getF());
			open[currHeuristic].add(startNode);
			nodesOpened++;
			
		}
		
		while (fValues[0].get(open[0].peek()) < Double.POSITIVE_INFINITY) {
			
			for (int currHeuristic = 1; currHeuristic < numOfHeuristics; currHeuristic++) {
				
				double inadMinKey = fValues[currHeuristic].get(open[currHeuristic].peek());
				
				if (inadMinKey <= w2 * fValues[0].get(open[0].peek())) {
					if (gValues.get(goalNode) <= inadMinKey) {
						if (gValues.get(goalNode) < Double.POSITIVE_INFINITY) {
							nodesExpanded = closedINAD.size();
							return optimalPath(goalNode);
						}	
					} else {
						Node currentNode = open[currHeuristic].poll();
						expandState(currentNode);
						closedINAD.add(currentNode);
						nodesOpened++;
					}
				} else { // if inadMinKey < w2 * anchorMinKey
					if (gValues.get(goalNode) <= fValues[0].get(open[0].peek())) {
						if (gValues.get(goalNode) < Double.POSITIVE_INFINITY) {
							//System.out.println("Path found bitch!");
							nodesExpanded = closedANCHOR.size();
							return optimalPath(goalNode);
						}
					} else {
						Node currentNode = open[0].poll();
						expandState(currentNode);
						closedANCHOR.add(currentNode);
						nodesOpened++;
					}
				}
			}
			
		}
		
		System.out.println("NO PATH!");
		return null;
	}

	protected double key (Node currentNode, int currHeuristic) {
		heuristicSwitch(heuristics[currHeuristic], currentNode);
		return gValues.get(currentNode) + (w1 * currentNode.getH());
	}
	
	protected void expandState(Node currentNode) {
		
		removeNodeFromAllOpen(currentNode);
		
		getNeighbors(currentNode);
		
		boolean neverGenerated;
		
		for (int i = 0; i < currentNode.neighbors.length; i++) {
			
			Node neighbor = currentNode.neighbors[i];
			if (!isValidNeighbor(neighbor)) {
				continue;
			}
			
			neverGenerated = checkIfGenerated(neighbor);
			
			if (neverGenerated) {
				gValues.put(neighbor, Double.POSITIVE_INFINITY);
				neighbor.setParent(null);
			}
			
			double tentativeGScore = gValues.get(currentNode) + neighbor.getG();
			
			if (gValues.get(neighbor) > tentativeGScore ) {
				
				gValues.replace(neighbor, tentativeGScore);
				neighbor.setParent(currentNode);
				
				if (!closedANCHOR.contains(neighbor)) {
					neighbor.setF(key(neighbor, 0));
					fValues[0].put(neighbor, neighbor.getF());
					open[0].add(neighbor);
					if (!closedINAD.contains(neighbor)) {
						for (int heuristic = 1; heuristic < numOfHeuristics; heuristic++) {
							if (key(neighbor, heuristic) <= (w2 * key(neighbor, 0))) {
								neighbor.setF(key(neighbor, heuristic));
								fValues[heuristic].put(neighbor, neighbor.getF());
								open[heuristic].add(neighbor);
								nodesOpened++;
							}
						}
					} 
				}
			}
		}
	}
	
	void removeNodeFromAllOpen(Node currentNode) {
		
		for (int heuristic = 0; heuristic < numOfHeuristics; heuristic++) {
			PriorityQueue<Node> newPQ = new PriorityQueue<Node>(11, fComparator);
			
			while (!open[heuristic].isEmpty()) {
				Node curr = open[heuristic].poll();
				
				if (!curr.equals(currentNode)) {
					curr.setF(fValues[heuristic].get(curr));
					newPQ.add(curr);
				}
			}
			open[heuristic] = newPQ;
		}
	}
	
	boolean checkIfGenerated(Node neighbor) {
		
		for (int heuristic = 0; heuristic < numOfHeuristics; heuristic++) {
			if (open[heuristic].contains(neighbor)) {
				return false;
			}
		}
		
		if (closedINAD.contains(neighbor) || closedANCHOR.contains(neighbor)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void resetMap() {
		gValues.clear();
		closedINAD.clear();
		closedANCHOR.clear();

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				tiles[i][j].setG(0);
				tiles[i][j].setF(0);
				tiles[i][j].setH(0);
				tiles[i][j].setParent(null);
			}
		}
	}

}
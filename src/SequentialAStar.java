import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class SequentialAStar extends HeuristicSearch {
	
	protected String[] heuristics = {"Manhattan", "Euclidean", "Enhanced Manhattan", "Fast Approximate", "Diagonal"};
	protected int numOfHeuristics = heuristics.length;
	protected PriorityQueue<Node>[] open;
	protected HashSet<Node>[] closed;
	protected HashMap<Node, Double> [] gValues;
	protected HashMap<Node, Double> [] fValues;
	protected HashMap<Node, Node> [] parentPointers;
	protected double w1, w2;
	

	@SuppressWarnings("unchecked")
	public SequentialAStar(Node[][] tiles, Coords start, Coords goal, double w1, double w2) {
		super(tiles, start, goal);
		open = (PriorityQueue<Node>[])new PriorityQueue[numOfHeuristics];
		closed = (HashSet<Node>[]) new HashSet[numOfHeuristics];
		gValues = (HashMap<Node, Double>[]) new HashMap[numOfHeuristics];
		fValues = (HashMap<Node, Double>[]) new HashMap[numOfHeuristics];
		parentPointers = (HashMap<Node, Node>[]) new HashMap[numOfHeuristics];
		this.w1 = w1;
		this.w2 = w2;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList<Node> solve() {
		Node startNode = tiles[start.getY()][start.getX()];
		Node goalNode = tiles[goal.getY()][goal.getX()];
		
		for (int currHeuristic = 0; currHeuristic < numOfHeuristics; currHeuristic++) {
			open[currHeuristic] = new PriorityQueue(11, fComparator);
			closed[currHeuristic] = new HashSet();
			gValues[currHeuristic] = new HashMap<Node, Double>();
			fValues[currHeuristic] = new HashMap<Node, Double>();
			parentPointers[currHeuristic] = new HashMap<Node, Node>();
			
			gValues[currHeuristic].put(startNode, 0.0);
			gValues[currHeuristic].put(goalNode, Double.POSITIVE_INFINITY);
			
			parentPointers[currHeuristic].put(startNode, null);
			parentPointers[currHeuristic].put(goalNode, null);
			
			startNode.setF(key(startNode, currHeuristic));
			fValues[currHeuristic].put(startNode, startNode.getF());
			open[currHeuristic].add(startNode);
			
		}
		
		while (fValues[0].get(open[0].peek()) < Double.POSITIVE_INFINITY) {
			
			for (int currHeuristic = 1; currHeuristic < numOfHeuristics; currHeuristic++) {
				
				double inadMinKey = fValues[currHeuristic].get(open[currHeuristic].peek());
				
				if (inadMinKey <= w2 * fValues[0].get(open[0].peek())) {
					if (gValues[currHeuristic].get(goalNode) <= inadMinKey) {
						if (gValues[currHeuristic].get(goalNode) < Double.POSITIVE_INFINITY) {
							//System.out.println("Path found!");
							nodesExpanded = closed[currHeuristic].size();
							return returnPath(goalNode, currHeuristic);
						}	
					} else {
						Node currentNode = open[currHeuristic].poll();
						expandState(currentNode, currHeuristic);
						closed[currHeuristic].add(currentNode);
					}
				} else { // if inadMinKey < w2 * anchorMinKey
					if (gValues[0].get(goalNode) <= fValues[0].get(open[0].peek())) {
						if (gValues[0].get(goalNode) < Double.POSITIVE_INFINITY) {
							//System.out.println("Path found!");
							nodesExpanded = closed[currHeuristic].size();
							return returnPath(goalNode, 0);
						}
					} else {
						Node currentNode = open[0].poll();
						expandState(currentNode, 0);
						closed[0].add(currentNode);
					}
				}
			}
			
		}
		
		System.out.println("NO PATH!");
		return null;
	}
	
	protected ArrayList<Node> returnPath(Node currentNode, int currHeuristic) {
		
		ArrayList<Node> path = new ArrayList<Node>();
		Node tmp = currentNode;
		
		while (tmp != null) {
			path.add(0, tmp);
			tmp = parentPointers[currHeuristic].get(tmp);
		}
		
		return path;
	}
	
	protected double key (Node currentNode, int currHeuristic) {
		heuristicSwitch(heuristics[currHeuristic], currentNode);
		return gValues[currHeuristic].get(currentNode) + (w1 * currentNode.getH());
	}
	
	protected void expandState(Node currentNode, int currHeuristic) {
		
		getNeighbors(currentNode);
		
		for (int i = 0; i < currentNode.neighbors.length; i++) {
			
			Node neighbor = currentNode.neighbors[i];
			if (!isValidNeighbor(neighbor)) {
				continue;
			}
			
			if (!open[currHeuristic].contains(neighbor) && !closed[currHeuristic].contains(neighbor)) {
				gValues[currHeuristic].put(neighbor, Double.POSITIVE_INFINITY);
				parentPointers[currHeuristic].put(neighbor, null);
			}
			
			double tentativeGScore = gValues[currHeuristic].get(currentNode) + neighbor.getG();
			
			if (gValues[currHeuristic].get(neighbor) > tentativeGScore ) {
				
				//System.out.println(neighbor.getCoords() + ": " + gValues[currHeuristic].get(neighbor));
				gValues[currHeuristic].replace(neighbor, tentativeGScore);
				parentPointers[currHeuristic].replace(neighbor, currentNode);
				
				if (!closed[currHeuristic].contains(neighbor)) {
					neighbor.setF(key(neighbor, currHeuristic));
					fValues[currHeuristic].put(neighbor, neighbor.getF());
					if (!open[currHeuristic].contains(neighbor)) {
						open[currHeuristic].add(neighbor);
					} else if (open[currHeuristic].peek().equals(neighbor)) {
						open[currHeuristic].remove();
						open[currHeuristic].add(neighbor);
					} 
				}
			}
		}
	}

}

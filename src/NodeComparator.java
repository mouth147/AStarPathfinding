import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
	
	@Override
	public int compare(Node node1, Node node2) {
		if (node1.getF() < node2.getF()) {
			return -1;
		}
		if (node1.getF() > node2.getF()) {
			return 1;
		}
		
		if (node1.getF() == node2.getF()) {
			if (node1.getG() > node2.getG()) {
				return 1;
			} else if (node1.getG() < node2.getG()) {
				return -1;
			}
		}
		return 0;
	}
	
}


public class Node {
	
	private Node parent;
	private double f, g, h;
	private Coords coords;
	private char terrain;
	public Node[] neighbors;

	public Node() {
		this(0, 0, 0, null);
	}

	public Node(double f, double g, double h) {
		this(f, g, h, null);
	}
	
	public Node(double f, double g, double h, Node parent) {
		this.f = f;
		this.g = g;
		this.h = h;
		this.parent = parent;
		neighbors = new Node[8];
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getG() {
		return g;
	}

	public void setG(double g) {
		this.g = g;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}
	
	public Coords getCoords() {
		return coords;
	}

	@Override
	public String toString() {
		return "Node [coords=" + coords + ", f=" + f + ", g=" + g + ", h=" + h + ", parent=" + parent + "]";
	}

	public void setCoords(Coords coords) {
		this.coords = coords;
	}

	public char getTerrain() {
		return terrain;
	}

	public void setTerrain(char terrain) {
		this.terrain = terrain;
	}

}

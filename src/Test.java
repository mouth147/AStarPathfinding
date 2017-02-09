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
		
		Node[][] testTiles = new Node[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				Node newNode = new Node();
				newNode.setCoords(new Coords(i, j));
				//System.out.println("Current x,y: " + i + "," + j + " | Coords: " + newNode.getCoords());
				testTiles[j][i] = newNode;
				newNode.setTerrain('1');
			}
		}
		for (int i = 1; i < 9; i++) {
			testTiles[0][i].setTerrain('a');
		}
		for (int i = 0; i < 7; i++) {
			testTiles[i][8].setTerrain('a');
		}
		testTiles[5][5].setTerrain('0');
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 10; x++) {
				System.out.print(testTiles[y][x].getTerrain());
			}
			System.out.println();
		}
		AStar a = new AStar(testTiles, new Coords(0, 0), new Coords(7, 9));
		ArrayList<Node> path = a.solve(1.0, false, "Manhattan");
		for (Node curr : path) {
			System.out.println(curr);
		}
	}

}

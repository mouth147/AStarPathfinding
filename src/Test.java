import static org.junit.Assert.*;

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
		
		char[][] testTiles = new char[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if ( j % 3 == 0) {
					testTiles[i][j] = '2';
				} else if (i % 2 == 0) {
					testTiles[i][j] = 'a';
				} else {
					testTiles[i][j] = '1';
				}
			}
		}
		
		AStar a = new AStar(testTiles, new Coords(0, 0), new Coords(7, 9));
		a.solve();
	}

}

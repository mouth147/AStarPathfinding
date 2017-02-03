import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
/*
 * TODO - Start load map/export map
 */

/**
 * TileMap is a class that consists of a 120x160 grid that includes impassable mountains,
 * highways, and rough terrain. It is represented by a 2D array.
 * 
 * @author Mike Botti | Github - @mouth147
 *
 */
public class TileMap {
	
	public static final int NUM_ROWS = 120;
	public static final int NUM_COLS = 160;
	private static final int UP = 0;
	private static final int LEFT = 1;
	private static final int RIGHT = 2;
	private static final int DOWN = 3;
	
	private Node [][] tiles;
	private Coords [] roughTerrainCenters;
	private Coords start;
	private Coords goal;
	

	/**
	 * Constructor that initializes the tilemap with the appropriate
	 * grid numbers.
	 */
	public TileMap() {
		tiles  = new Node[NUM_ROWS][NUM_COLS];
		roughTerrainCenters = new Coords[8];
	}
	
	/**
	 * The main method that populates the map.
	 */
	public void generateMap() {

		System.out.println("Generating map...");
		System.out.println("Initializing map...");
		initialSetUp();
		System.out.println("Generating centers of rough terrain...");
		generateCenters();
		System.out.println("Developing tough terrain...");
		developRoughTerrain();
		System.out.println("Building highways...");
		buildHighways();
		System.out.println("God is making mountains...");
		makeMountains();
		
	}
	
	/**
	 * Generates random coordinates and returns 
	 * a Coords object.
	 */
	public static Coords generateCoords() {
		Random r = new Random();
		int x;
		int y;
		
		x = r.nextInt(NUM_COLS);
		y = r.nextInt(NUM_ROWS);
		
		return new Coords(x, y);
	}
	
	/**
	 * This method generates 8 centers for rough terrain
	 * making sure there are no duplicates.
	 */
	public void generateCenters() {
		
		for (int i = 0; i < roughTerrainCenters.length; i++) {
			Coords newCoords = generateCoords();
				for (int j = 0; j < i; j++) {
					if (newCoords.equals(roughTerrainCenters[i])) {
						while (newCoords.equals(roughTerrainCenters[i])) {
							newCoords = generateCoords();
						}
						j = 0;
					}
				}
			
			roughTerrainCenters[i] = newCoords;
			
		}
	}
	
	/**
	 * Generates a new start and a new goal tile for the current map.
	 * There are 4 sections of the map that we can have these tiles. 
	 * Top Left, Top Right, Bottom Left, Bottom Right. Using a number 
	 * generator we decide which corner to put our tiles in. 0-24, 25-49,
	 * 50-74,75-99 all represent Top Left, Top Right, Bottom Left, Bottom Right
	 * respectively.
	 */
	public void generateStartAndGoal() {
		
		clearStartAndGoal();
		
		Coords start = generateTileInRange();
		this.start = start;
		Coords goal;
		
		boolean success = false;
		
		do {
			goal = generateTileInRange();
			
			success = compareRanges(start, goal);
		} while (!success);
		this.goal = goal;
		
	}
	
	/**
	 * 
	 * Compares the distance between the start and goal tile for the algorithm
	 * 
	 * @param start - Start tile for algorithm
	 * @param goal - Possible goal tile for algorithm
	 * @return Returns True if it's in the required range, false otherwise
	 */
	public boolean compareRanges(Coords start, Coords goal) {
		
		int startX = start.getX(), goalX = goal.getX();
		int startY = start.getY(), goalY = goal.getY();
		int distance = 0;
		
		if (startX > goalX) {
			distance += (startX - goalX);
		} else {
			distance += (goalX - startX);
		}
		
		if (startY > goalY) {
			distance += (startY - goalY);
		} else {
			distance += (goalY - startY);
		}
		return distance >= 100;
	}
	
	/**
	 * Generates a tile in one of the four corners for the start/goal tiles.
	 * 
	 * @return Returns a tile that'll be in one of four corners
	 */
	public Coords generateTileInRange() {
		
		Random r = new Random();
		int x, lowX, highX;
		int y, lowY, highY;
		
		int corner = r.nextInt(100);
		if (corner < 25) {
			lowX = 0;
			highX = 20;
			lowY = 0;
			highY = 20;
		} else if (corner < 50) {
			lowX = NUM_COLS - 20;
			highX = NUM_COLS;
			lowY = 0;
			highY = 20;
		} else if (corner < 75) {
			lowX = 0;
			highX = 20;
			lowY = NUM_ROWS - 20;
			highY = NUM_ROWS;
		} else {
			lowX = NUM_COLS - 20;
			highX = NUM_COLS;
			lowY = NUM_ROWS - 20;
			highY = NUM_ROWS;
		}
		
		x = r.nextInt(highX - lowX) + lowX;
		y = r.nextInt(highY - lowY) + lowY;
		
		return new Coords(x, y);
	}
	
	/**
	 *  Clears start and goal tiles.
	 */
	public void clearStartAndGoal() {
		start = null;
		goal = null;
	}
	
	public Node[][] getTiles() {
		return tiles;
	}
	
	public Coords getStartTile() {
		return start;
	}
	
	public Coords getGoalTile() {
		return goal;
	}
	
	/**
	 * Loads a TileMap from a file within it's directory.
	 * 
	 * @param filePath - Path of the file
	 */
	public void loadMap(String filePath) {

		System.out.println("Loading map...");
		String fileContents = readFromFile(filePath);
		Scanner scanner = new Scanner(fileContents);
		
		System.out.println("Parsing file...");
		
		this.start = parseCoords(scanner.nextLine());
		this.goal = parseCoords(scanner.nextLine());
		
		System.out.println("Rough terrains...");
		for (int i = 0; i < roughTerrainCenters.length; i++) {
			String str = scanner.nextLine();
			System.out.println(str);
			roughTerrainCenters[i] = parseCoords(str);
		}
		
		System.out.println("Tiles...");
		for (int i = 0; i < NUM_ROWS; i++) {
			String currentRow = scanner.nextLine();
			for (int j = 0; j < NUM_COLS; j++) {
				Node newNode = new Node();
				newNode.setCoords(new Coords(j, i));
				newNode.setTerrain(currentRow.charAt(j));
				tiles[i][j] = newNode;
			}
		}
		
		System.out.println("Done parsing!");
		scanner.close();
	}
	
	public static Coords parseCoords(String str) {
		
		int comma = str.indexOf(',');
		if (comma == -1) {
			return null;
		}
		
		int x = Integer.parseInt(str.substring(0, comma));
		int y = Integer.parseInt(str.substring(comma + 1, str.length()));
		
		return new Coords(x, y);
	}

	/**
	 * @param filePath
	 */
	private String readFromFile(String filePath) {
		Path file = Paths.get(filePath);
		Charset charset = Charset.forName("US-ASCII");
		String str = "";
		
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				str += line + "\n";
			}
			
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
		
		return str;
	}
	
	/**
	 * Exports the current map to a file.
	 * 
	 * @param filename - Name of file to be written
	 */
	public void exportMap(File file) {
		
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(this.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method goes around the center terrain and with a 50% probabilty
	 * determines if each individual tile in the 31x31 area will be rough terrain.
	 */
	public void developRoughTerrain() {
		Coords current;
		
		for (int i = 0; i < roughTerrainCenters.length; i++) {
			current = roughTerrainCenters[i];
			int maxY = (current.getY() - 15) + 31;
			int maxX = (current.getX() - 15) + 31;
			
			for (int currY = current.getY() - 15; currY < maxY; currY++) {
				if (currY < 0 || currY >= NUM_ROWS) {
					continue;
				}
				for (int currX = current.getX() - 15; currX < maxX; currX++) {
					if (currX < 0 || currX >= NUM_COLS) {
						continue;
					}
					
					if (probability(50)) {
						tiles[currY][currX].setTerrain('2');
					}
				}
			}
		}
	}
	
	/**
	 * Returns a true or false probability.
	 * 
	 * @param percent - an integer that represents the probability 
	 * @return a boolean value if it's within the probability
	 */
	public boolean probability(int percent) {
		return new Random().nextDouble() <= (double)percent/100.0;
	}
	
	/**
	 * Set all the cells to 1 - unblocked normal terrain.
	 */
	public void initialSetUp() {
		
		for (int i = 0; i < NUM_COLS; i++) {
			for (int j = 0; j < NUM_ROWS; j++) {
				Node newNode = new Node();
				newNode.setCoords(new Coords(i, j));
				//System.out.println("Current x,y: " + j + "," + i + " | Coords: " + newNode.getCoords());
				newNode.setTerrain('1');
				tiles[j][i] = newNode;
			}
		}
	}
	
	/**
	 * Builds highways.
	 */
	public void buildHighways() {
		
		Coords[] highwayStartPoints = highwayStartCoords();
		boolean isValid;
		
		for (int i = 0; i < 4; i++) {
		if (i == 2 || i == 3) {
			System.out.println("Almost there...");
		}
			isValid = highwayPathBuilder(i, highwayStartPoints[i]);
			if (!isValid) {
				deleteAllHighways();
				i = -1;
			}
			
		}
		
	}
	
	/**
	 * Goes through the map and replaces all highways with their original terrain.
	 */
	public void deleteAllHighways() {
		for (int i = 0; i < NUM_COLS; i++) {
			for (int j = 0; j < NUM_ROWS; j++) {
				if (tiles[j][i].getTerrain() == 'a') {
					tiles[j][i].setTerrain('1');
				} else if (tiles[j][i].getTerrain() == 'b') {
					tiles[j][i].setTerrain('2');			}
			}
		}
	}
	
	/**
	 * 
	 * Attempts to build one highway for a minimum 250 times. If it cannot build it
	 * will return false. If it can build it will return true.
	 * 
	 * @param section - TOP, LEFT, BOTTOM, RIGHT
	 * @param startPoint - starting coordinates of each highway
	 * @return returns True if the highway was built successfully, False otherwise
	 */
	public boolean highwayPathBuilder(int section, Coords startPoint) {
		
		ArrayList<Coords> highway = new ArrayList<Coords>(200);
		int currentDirection = getStartingDirection(section);
		boolean successfulTile;
		int numOfAttempts = 0;
		Coords currTile;
		int currX;
		int currY;
		
		while (numOfAttempts < 10000) {
			currTile = startPoint;
			highway.add(currTile);
			currX = currTile.getX();
			currY = currTile.getY();
			successfulTile = true;
			numOfAttempts++;
			
			while ((currX >= 0 && currX < NUM_COLS) && (currY >= 0 && currY < NUM_ROWS) && successfulTile) { // while inside the grid
				highway.add(currTile);
				if (tiles[currY][currX].getTerrain() == 'a' || tiles[currY][currX].getTerrain() == 'b') {
					deleteSingleHighway(highway);
					currX = startPoint.getX();
					currY = startPoint.getY();
					successfulTile = false;
				} else if (tiles[currY][currX].getTerrain() == '1') {
					tiles[currY][currX].setTerrain('a');
				} else if (tiles[currY][currX].getTerrain() == '2') {
					tiles[currY][currX].setTerrain('b');
				}
				
				if (successfulTile) {
					currentDirection = getNextDirection(currentDirection);
					Coords newTile = getNextTile(currTile, currentDirection);
					currX = newTile.getX();
					currY = newTile.getY();
					currTile = newTile;
				}
				
			}
			
			if (highway.size() >= 100) {
				return true;
			} else {
				deleteSingleHighway(highway);
			}
		}
		
		return false;
	}
	
	/**
	 * This removes the current highway from the map.
	 * 
	 * @param highway - This is an ArrayList of the all the coords of the current highway.
	 */
	public void deleteSingleHighway(ArrayList<Coords> highway) {
		
		Iterator<Coords> itr = highway.iterator();
		int currX;
		int currY;
		Coords current;
		while(itr.hasNext()) {
			current = itr.next();
			currX = current.getX();
			currY = current.getY();
			
			if (tiles[currY][currX].getTerrain() == 'a') {
				tiles[currY][currX].setTerrain('1');
			} else if (tiles[currY][currX].getTerrain() == 'b') {
				tiles[currY][currX].setTerrain('2');
			}
			
		}
		highway.clear();
	}
	
	@Override
	public String toString() {
		String str = "";
		
		str += start + "\n";
		str += goal + "\n";
		
		for (int i = 0; i < roughTerrainCenters.length; i++) {
			str += roughTerrainCenters[i] + "\n";
		}
		
		for (int i = 0; i < 120; i++) {
			for (int j = 0; j < 160; j++) {
				str += tiles[i][j].getTerrain();
			}
			str += "\n";
		}
		return str;
	}

	/**
	 * This method gets the next tile in the highways path.
	 * 
	 * @param currTile - The current tile we're altering
	 * @param currentDirection - The current direction we're traveling
	 */
	public Coords getNextTile(Coords currTile, int currentDirection) {
		
		Coords newTile = new Coords();
		newTile.setX(currTile.getX());
		newTile.setY(currTile.getY());
		
		switch(currentDirection) {
		case UP: newTile.setY(currTile.getY() - 1); break;
		case DOWN: newTile.setY(currTile.getY() + 1); break;
		case LEFT: newTile.setX(currTile.getX() - 1); break;
		case RIGHT: newTile.setX(currTile.getX() + 1); break;
		}
		
		return newTile;
		
	}
	
	/**
	 * Builds impassable mountains. Calculates 20% of all blocks and generates random coordinates
	 * that will not be on highways. If they are on highways or existing mountains it'll attempt 
	 * to regenerate until it finds a new coordinate that isn't on a highway.
	 * 
	 */
	public void makeMountains() {
		int totalTiles = NUM_ROWS * NUM_COLS;
		int totalMountains = totalTiles / 5;
		Coords curr;
		int currX, currY;
		
		for (int i = 0; i < totalMountains; i++) {
			boolean success = false;
			
			while (!success) {
				curr = generateCoords();
				currX = curr.getX();
				currY = curr.getY();
				
				if (tiles[currY][currX].getTerrain() == 'a' || tiles[currY][currX].getTerrain() == 'b' || tiles[currY][currX].getTerrain() == '0') {
					curr = generateCoords();
				} else {
					tiles[currY][currX].setTerrain('0');
					success = true;
				}
				
			}
		}
	}
	
	/**
	 * 
	 * Gets the starting direction of the highway since it can only go UP, LEFT, DOWN, or RIGHT
	 * @param section - The section of the grid
	 * @return Returns the direction of the map unless the section is invalid then returns -1
	 */
	public int getStartingDirection(int section) {
		
		switch(section) {
		case UP: return DOWN;
		case LEFT: return RIGHT;
		case RIGHT: return LEFT;
		case DOWN: return UP;
		}
		
		return -1;
	}
	
	/**
	 * 
	 * @param currDirection - the current direction of the highway
	 * @return Returns the next direction
	 */
	public int getNextDirection(int currentDirection) {
		
		if (probability(60)) { // if true same direction
			return currentDirection;
		} else {
			if (probability(50)) { // if true turn right
				switch(currentDirection) {
				case UP: return RIGHT;
				case RIGHT: return DOWN;
				case LEFT: return UP;
				case DOWN: return LEFT;
				}
			} else { // if false turn left
				switch(currentDirection) {
				case UP: return LEFT;
				case RIGHT: return UP;
				case LEFT: return DOWN;
				case DOWN: return RIGHT;
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * Generates an array of 4 different coordinates as start points
	 * for all the highways/rivers. Each one will start at top/most left/
	 * most right/bottom respectively to try and allow each highway to 
	 * complete itself.
	 */
	public Coords[] highwayStartCoords() {
		
		Coords[] highwayStartPoints = new Coords[4];
		
		for (int i = 0; i < highwayStartPoints.length; i++) {
			highwayStartPoints[i] = generateCoords();
		}
		
		highwayStartPoints[UP].setY(0);
		highwayStartPoints[LEFT].setX(0);
		highwayStartPoints[RIGHT].setX(NUM_COLS - 1);
		highwayStartPoints[DOWN].setY(NUM_ROWS - 1);
		
		return highwayStartPoints;
	}

}

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Pathfinder extends Application {
	
	private static final int REC_HEIGHT = 5;
	private static final int REC_WIDTH = 5;
	private static final int MAP_HEIGHT = (120 * REC_HEIGHT) + 10;
	private static final int MAP_WIDTH = (160 * REC_WIDTH) + 130;
	
	/**
	 * The initial menu to this program. You can either generate a new map, or load
	 * an existing map from a file. When generating a new file, it starts a new process
	 * to run in the background while the map is generating.
	 */
	public void start(Stage primaryStage) {
		StackPane root = new StackPane();
		VBox vbox = new VBox();
		
		vbox.setSpacing(10);
		vbox.setPadding(new Insets(20));
		vbox.setAlignment(Pos.CENTER);
		
		Button generate = new Button("Generate New Map");
		Button load = new Button("Load Existing Map");
		
		generate.prefWidth(80);
		generate.prefHeight(50);
		load.prefWidth(80);
		load.prefHeight(50);
		
		/*
		 * The generate button action handler
		 */
		generate.setOnAction(new EventHandler<ActionEvent>() {
			
			Task<TileMap> task = new Task<TileMap>() {
				@Override protected TileMap call() {	
					TileMap mainMap = createMap();
					return mainMap;
				}
			};
			
			@Override
			public void handle(ActionEvent event) {
				progressAndFinish(primaryStage, root, vbox, task);
			}
		});
		
		/*
		 * Load button action handler
		 */
		load.setOnAction(new EventHandler<ActionEvent>() {
			
			FileChooser fileChooser = new FileChooser();
			File file;
			
			Task<TileMap> task = new Task<TileMap>() {

				@Override
				protected TileMap call() throws Exception {
					// TODO Auto-generated method stub
					TileMap mainMap = new TileMap();
					mainMap.loadMap(file.toString());
					return mainMap;
				}
			};

			@Override
			public void handle(ActionEvent event) {
				file = fileChooser.showOpenDialog(primaryStage);
				progressAndFinish(primaryStage, root, vbox, task);
			}
		});
		
		
		vbox.getChildren().addAll(generate, load);
		root.getChildren().add(vbox);
		
		Scene scene = new Scene(root, 300, 250);
		primaryStage.setTitle("Menu");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * 
	 * This method opens up a new window with the generated map.
	 * 
	 * @param primaryStage - Stage used to set up the scene
	 * @param mainMap - The main map
	 */
	public void showMap(Stage primaryStage, TileMap mainMap) {
		BorderPane root = new BorderPane();
		GridPane grid = new GridPane();
		Node [][]tiles = mainMap.getTiles();
		
		Label hValue = new Label("h-value: ");
		Label gValue = new Label("g-value: ");
		Label fValue = new Label("f-value: ");
		Label start = new Label("Start Tile: ");
		Label startValue = new Label();
		Label goal = new Label("Goal Tile: ");
		Label goalValue = new Label();
		VBox vbox = new VBox();
		HBox startTiles = new HBox();
		HBox goalTiles = new HBox();
		
		vbox.setSpacing(10);
		vbox.setPadding(new Insets(20));
		vbox.setAlignment(Pos.CENTER_LEFT);
		
		if (mainMap.getStartTile() == null) {
			startValue.setText("Generate new start and goal");
			goalValue.setText("Generate new start and goal");
		} else {
			startValue.setText(mainMap.getStartTile().toString());
			goalValue.setText(mainMap.getGoalTile().toString());
		}
		
		startTiles.setSpacing(5);
		goalTiles.setSpacing(5);
		startTiles.getChildren().addAll(start, startValue);
		goalTiles.getChildren().addAll(goal, goalValue);
		
		vbox.getChildren().addAll(hValue, gValue, fValue, startTiles, goalTiles);
		MenuBar menuBar = createMenu(primaryStage, mainMap, startValue, goalValue);
		
		colorGrid(grid, tiles);
		root.setTop(menuBar);
		root.setCenter(grid);
		root.setRight(vbox);
		
		Scene scene = new Scene(root, MAP_WIDTH, MAP_HEIGHT);
		primaryStage.setTitle("Map");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Generates the Menu for the map window
	 * 
	 * @return Returns a MenuBar object ready to go.
	 */
	public MenuBar createMenu(Stage primaryStage, TileMap mainMap, Label startValue, Label goalValue) {
		MenuBar menuBar = new MenuBar();
		
		Menu fileMenu = new Menu("File");
		Menu currentMenu = new Menu("Current Map");
		Menu aboutMenu = new Menu("About");
		
		MenuItem exportMap = new MenuItem("Export map");
		MenuItem exit = new MenuItem("Exit");
		
		MenuItem startAndGoal = new MenuItem("Generate start and goal tiles");
		MenuItem solveAStar = new MenuItem("Find optimal path using A*");
		
		fileMenu.getItems().addAll(exportMap, exit);
		currentMenu.getItems().addAll(startAndGoal, solveAStar);
		
		/*
		 * Export map Action Handler
		 */
		exportMap.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				FileChooser fileChooser = new FileChooser();
				
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT Files (*.txt)", "*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				
				File file = fileChooser.showSaveDialog(primaryStage);
				mainMap.exportMap(file);
			}
		});
		
		/*
		 * Generate start and goal tiles action handler
		 */
		startAndGoal.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				mainMap.generateStartAndGoal();
				startValue.setText(mainMap.getStartTile().toString());
				goalValue.setText(mainMap.getGoalTile().toString());
			}
		});
		
		/*
		 * Solve A star button
		 */
		solveAStar.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				AStar astar = new AStar(mainMap.getTiles(), mainMap.getStartTile(), mainMap.getGoalTile());
				ArrayList<Node> path = astar.solve();
				
				if (path == null) {
					System.out.println("No path found.");
				} else {
					Iterator<Node> itr = path.iterator();
					
					while (itr.hasNext()) {
						Node curr = itr.next();
						System.out.println(curr.getCoords());
					}
				}
			}
		});
		
		/*
		 * Handle exit 
		 */
		exit.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				start(primaryStage);
			}
		});
		
		// Exit accelerator
		exit.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
		
		menuBar.getMenus().addAll(fileMenu, currentMenu, aboutMenu);
		return menuBar;
	}
	
	/**
	 * Utility method to generate a map.
	 * 
	 * @return Returns the generated map
	 */
	public TileMap createMap() {
		TileMap mainMap = new TileMap();
		mainMap.generateMap();
		return mainMap;
	}
	/**
	 * 
	 * This method sets up all the rectangles for the GridPane to be colored.
	 * 
	 * @param grid - The current GridPane
	 * @param tiles - The 2D array version of the grid
	 */
	private void colorGrid(GridPane grid, Node[][] tiles) {
		Color terrain;
		for (int i = 0; i < TileMap.NUM_ROWS; i++) {
			for (int j = 0; j < TileMap.NUM_COLS; j++) {
				Rectangle rec = new Rectangle();
				rec.setHeight(REC_HEIGHT);
				rec.setWidth(REC_WIDTH);
				terrain = isColor(tiles[i][j].getTerrain());
				rec.setFill(terrain);
				GridPane.setRowIndex(rec, i);
				GridPane.setColumnIndex(rec, j);
				grid.getChildren().addAll(rec);
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		launch(args);
	}
	
	/**
	 * 
	 * Basic utility method to help determine the color of a tile.
	 * 
	 * @param value - The value of the tile in the 2D array
	 * @return Returns the Color value
	 */
	public Color isColor(char value) {
		Color terrain = Color.GREEN;
		
		switch(value) {
		case '0': terrain = Color.BLACK; break;
		case '1': terrain = Color.GREEN; break;
		case '2': terrain = Color.BROWN; break;
		case 'a': terrain = Color.AQUAMARINE; break;
		case 'b': terrain = Color.BLUE; break;
		default: break;
		}
		
		return terrain;
	}

	/**
	 * 
	 * This method starts a new thread that works on a task in the background,
	 * such as loading the map, or generating the map and puts a progress indicator
	 * on the top most layer of the Stack Pane so the UI doesn't become unresponsive.
	 * 
	 * @param primaryStage - the stage to be set on
	 * @param root - the StackPane that we put our progress indicator on
	 * @param vbox - the original VBox we have for our buttons
	 * @param task - the Task that is performed
	 */
	public void progressAndFinish(Stage primaryStage, StackPane root, VBox vbox, Task<TileMap> task) {
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		
		ProgressIndicator progress = new ProgressIndicator();
		VBox layer = new VBox(progress);
		layer.setAlignment(Pos.CENTER);
		vbox.setDisable(true);
		root.getChildren().add(layer);
		
		task.setOnSucceeded(e -> { // Once the thread is done executing open the map.
			System.out.println("Thread finished!");
			TileMap mainMap = task.getValue();
			showMap(primaryStage, mainMap);
		});
	}
	
	


}

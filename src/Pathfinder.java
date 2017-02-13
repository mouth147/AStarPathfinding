import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
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
	private static final int MAP_HEIGHT = (120 * REC_HEIGHT) + 40;
	private static final int MAP_WIDTH = (160 * REC_WIDTH) + 450;
	private ArrayList<Node> path = null;
	DecimalFormat decFormat = new DecimalFormat("0.000");
	
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
				if (file != null) {
					progressAndFinish(primaryStage, root, vbox, task);
				}
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
		
		Label hLabel = new Label("Heuristics: ");
		ChoiceBox<String> heuristics = new ChoiceBox<String>();
		heuristics.getItems().addAll("Diagonal", "Manhattan", "Euclidean", "Enhanced Manhattan", "Fast Approximate");
		heuristics.getSelectionModel().selectFirst();
		Label wValue1 = new Label("W1 Value: ");
		Slider wSlider1 = new Slider();
		Label wValue2 = new Label("W2 Value: ");
		Slider wSlider2 = new Slider();
		Label hValue = new Label("H Value: ");
		Label currTile = new Label("Current Tile: ");
		Label gValue = new Label("G Value: ");
		Label fValue = new Label("F Value: ");
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
		
		setSlider(wSlider1, wValue1);
		setSlider(wSlider2, wValue2);
		
		vbox.getChildren().addAll(hLabel, heuristics, wValue1, wSlider1, wValue2, wSlider2, hValue, gValue, fValue, startTiles, goalTiles, currTile);
		MenuBar menuBar = createMenu(primaryStage, mainMap, startValue, goalValue, grid, hValue, gValue, fValue, currTile, wSlider1, wSlider2, heuristics);
		
		colorGrid(grid, tiles, hValue, gValue, fValue, currTile);
		root.setTop(menuBar);
		root.setCenter(grid);
		root.setRight(vbox);
		
		Scene scene = new Scene(root, MAP_WIDTH, MAP_HEIGHT);
		primaryStage.setTitle("Map");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * 
	 */
	public void setSlider(Slider wSlider, Label wValue) {
		wSlider.setMin(1);
		wSlider.setMax(4);
		wSlider.setValue(1);
		wSlider.setShowTickLabels(true);
		wSlider.setShowTickMarks(true);
		wSlider.setMajorTickUnit(0.5f);
		wSlider.setBlockIncrement(0.50f);
		wSlider.valueProperty().addListener(new ChangeListener<Object>() {
			
			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2) {
				wValue.setText("W Value: " + decFormat.format(wSlider.getValue()));
			}
		});
	}

	/**
	 * Generates the Menu for the map window
	 * 
	 * @return Returns a MenuBar object ready to go.
	 */
	public MenuBar createMenu(Stage primaryStage, TileMap mainMap, Label startValue, Label goalValue, GridPane grid, Label hValue, Label gValue, Label fValue, Label currTile, Slider wSlider1, Slider wSlider2, ChoiceBox<String> heuristics) {
		MenuBar menuBar = new MenuBar();
		
		Menu fileMenu = new Menu("File");
		Menu currentMenu = new Menu("Current Map");
		Menu aboutMenu = new Menu("About");
		
		MenuItem exportMap = new MenuItem("Export map");
		MenuItem exit = new MenuItem("Exit");
		
		MenuItem startAndGoal = new MenuItem("Generate start and goal tiles");
		MenuItem solveAStar = new MenuItem("Find optimal path using A*");
		MenuItem solveWeighted = new MenuItem("Find optimal path using Weighted A*");
		MenuItem solveUniformCost = new MenuItem("Find optimal path using Uniform Cost");
		MenuItem solveSequential = new MenuItem("Find optimal path using Sequential A*");
		MenuItem solveIntegrated = new MenuItem("Find optimal path using Integrated A*");
		MenuItem clearPath = new MenuItem("Clear path");
		
		fileMenu.getItems().addAll(exportMap, exit);
		currentMenu.getItems().addAll(startAndGoal, solveAStar, solveWeighted, solveUniformCost, solveSequential, solveIntegrated, clearPath);
		if (path == null) {
			clearPath.setDisable(true);
		} else {
			clearPath.setDisable(false);
		}
		
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
		exportMap.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		
		/*
		 * Generate start and goal tiles action handler
		 */
		startAndGoal.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				mainMap.generateStartAndGoal();
				if (path != null) {
					colorGrid(grid, mainMap.getTiles(), hValue, gValue, fValue, currTile);
					clearPath.setDisable(true);
				}
				path = null;
				startValue.setText(mainMap.getStartTile().toString());
				goalValue.setText(mainMap.getGoalTile().toString());
			}
		});
		
		/*
		 * Clear path
		 */
		clearPath.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				colorGrid(grid, mainMap.getTiles(), hValue, gValue, fValue, currTile);
				clearPath.setDisable(true);
			}
		});
		
		/*
		 * Solve A star button
		 */
		solveAStar.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				Task<ArrayList<Node>> task = new Task<ArrayList<Node>>() {

					@Override
					protected ArrayList<Node> call() throws Exception {
						HeuristicSearch astar = new AStar(mainMap.getTiles(), mainMap.getStartTile(), mainMap.getGoalTile(), heuristics.getValue());
						astar.resetMap();
						long startTime = System.nanoTime();
						ArrayList<Node> path = astar.solve();
						long endTime = System.nanoTime();
						if (path != null) {
							System.out.println("------------------------------------");
							System.out.println("A* Search");
							System.out.println("Heuristic: " + heuristics.getValue());
							System.out.println("Runtime: " + ((endTime - startTime) / 1000000) + "ms");
							System.out.println("Path length: " + path.size());
							System.out.println("------------------------------------");
						}

						return path;
					}
					
				};
				
				colorGrid(grid, mainMap.getTiles(), hValue, gValue, fValue, currTile);
				colorPath(grid, task, hValue, gValue, fValue, currTile, clearPath);
			}
		});
		
		/*
		 * Solve Sequential A star button
		 */
		solveSequential.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				Task<ArrayList<Node>> task = new Task<ArrayList<Node>>() {

					@Override
					protected ArrayList<Node> call() throws Exception {
						HeuristicSearch astar = new SequentialAStar(mainMap.getTiles(), mainMap.getStartTile(), mainMap.getGoalTile(), wSlider1.getValue(), wSlider2.getValue());
						astar.resetMap();
						long startTime = System.nanoTime();
						ArrayList<Node> path = astar.solve();
						long endTime = System.nanoTime();
						if (path != null) {
							System.out.println("------------------------------------");
							System.out.println("Sequential A* Search");
							System.out.println("Heuristic: " + heuristics.getValue());
							System.out.println("Runtime: " + ((endTime - startTime) / 1000000) + "ms");
							System.out.println("Path length: " + path.size());
							System.out.println("------------------------------------");
						}

						return path;
					}
					
				};
				
				colorGrid(grid, mainMap.getTiles(), hValue, gValue, fValue, currTile);
				colorPath(grid, task, hValue, gValue, fValue, currTile, clearPath);
			}
		});
		
		/*
		 * Solve Integrated A star button
		 */
		solveIntegrated.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				Task<ArrayList<Node>> task = new Task<ArrayList<Node>>() {

					@Override
					protected ArrayList<Node> call() throws Exception {
						HeuristicSearch astar = new SequentialAStar(mainMap.getTiles(), mainMap.getStartTile(), mainMap.getGoalTile(), wSlider1.getValue(), wSlider2.getValue());
						astar.resetMap();
						long startTime = System.nanoTime();
						ArrayList<Node> path = astar.solve();
						long endTime = System.nanoTime();
						if (path != null) {
							System.out.println("------------------------------------");
							System.out.println("Integrated A* Search");
							System.out.println("Heuristic: " + heuristics.getValue());
							System.out.println("Runtime: " + ((endTime - startTime) / 1000000) + "ms");
							System.out.println("Path length: " + path.size());
							System.out.println("------------------------------------");
						}

						return path;
					}
					
				};
				
				colorGrid(grid, mainMap.getTiles(), hValue, gValue, fValue, currTile);
				colorPath(grid, task, hValue, gValue, fValue, currTile, clearPath);
			}
		});
		
		/*
		 * Solve Weighted A star button
		 */
		solveWeighted.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				Task<ArrayList<Node>> task = new Task<ArrayList<Node>>() {

					@Override
					protected ArrayList<Node> call() throws Exception {
						HeuristicSearch astar = new WeightedAStar(mainMap.getTiles(), mainMap.getStartTile(), mainMap.getGoalTile(), heuristics.getValue(), wSlider1.getValue());
						astar.resetMap();
						long startTime = System.nanoTime();
						ArrayList<Node> path = astar.solve();
						long endTime = System.nanoTime();
						if (path != null) {
							System.out.println("------------------------------------");
							System.out.println("Weighted A* Search");
							System.out.println("Heuristic: " + heuristics.getValue());
							System.out.println("Weight: " + wSlider1.getValue());
							System.out.println("Runtime: " + ((endTime - startTime) / 1000000) + "ms");
							System.out.println("Path length: " + path.size());
							System.out.println("------------------------------------");
						}

						return path;
					}
					
				};
				
				colorGrid(grid, mainMap.getTiles(), hValue, gValue, fValue, currTile);
				colorPath(grid, task, hValue, gValue, fValue, currTile, clearPath);
			}
		});
		
		/*
		 * Solve uniform cost
		 */
		solveUniformCost.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Task<ArrayList<Node>> task = new Task<ArrayList<Node>>() {

					@Override
					protected ArrayList<Node> call() throws Exception {
						HeuristicSearch astar = new UniformCostSearch(mainMap.getTiles(), mainMap.getStartTile(), mainMap.getGoalTile());
						astar.resetMap();
						long startTime = System.nanoTime();
						ArrayList<Node> path = astar.solve();
						long endTime = System.nanoTime();
						System.out.println("------------------------------------");
						System.out.println("Uniform Cost Search");
						System.out.println("Runtime: " + ((endTime - startTime) / 1000000) + "ms");
						System.out.println("Path length: " + path.size());
						System.out.println("------------------------------------");

						return path;
					}
					
				};
				
				colorGrid(grid, mainMap.getTiles(), hValue, gValue, fValue, currTile);
				colorPath(grid, task, hValue, gValue, fValue, currTile, clearPath);
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
	private void colorGrid(GridPane grid, Node[][] tiles, Label hValue, Label gValue, Label fValue, Label currTile) {
		Color terrain;
		for (int i = 0; i < TileMap.NUM_ROWS; i++) {
			for (int j = 0; j < TileMap.NUM_COLS; j++) {
				Node curr = tiles[i][j];
				curr.setF(0);
				curr.setG(0);
				curr.setH(0);
				curr.setParent(null);
				Rectangle rec = new Rectangle();
				rec.setHeight(REC_HEIGHT);
				rec.setWidth(REC_WIDTH);
				terrain = isColor(curr.getTerrain());
				rec.setFill(terrain);
				GridPane.setRowIndex(rec, i);
				GridPane.setColumnIndex(rec, j);
				grid.getChildren().addAll(rec);
				rec.setOnMouseEntered(e -> {
					hValue.setText("H Value: " + decFormat.format(curr.getH()));
					gValue.setText("G Value: " + decFormat.format(curr.getG()));
					fValue.setText("F Value: " + decFormat.format(curr.getF()));
					currTile.setText("Current Tile: " + curr.getCoords().getX() + ", " + curr.getCoords().getY());
				});
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
			root.getChildren().remove(layer);
			vbox.setDisable(false);
			showMap(primaryStage, mainMap);
		});
	}

	/**
	 * @param grid
	 * @param task
	 */
	public void colorPath(GridPane grid, Task<ArrayList<Node>> task, Label hValue, Label gValue, Label fValue, Label currTile, MenuItem clearPath) {
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		
		task.setOnSucceeded(e -> {
			path = task.getValue();
			if (path == null) {
				System.out.println("No path found!");
				Alert alert	= new Alert(AlertType.ERROR);
				alert.setTitle("Error!");
				alert.setHeaderText("Uh oh, something went wrong.");
				// Do getDialogPane with label to avoid text getting cut off
				alert.getDialogPane().setContent(new Label("No path found! Try generating new start and goal tiles."));
				alert.showAndWait();
				
			} else {
				Iterator<Node> itr = path.iterator();
				while(itr.hasNext()) {
					Node curr = itr.next();
					Rectangle rec = new Rectangle();
					rec.setHeight(REC_HEIGHT);
					rec.setWidth(REC_WIDTH);
					rec.setFill(Color.DARKORANGE);
					GridPane.setColumnIndex(rec, curr.getCoords().getX());
					GridPane.setRowIndex(rec, curr.getCoords().getY());
					grid.getChildren().addAll(rec);
					rec.setOnMouseEntered(f -> {
						hValue.setText("H Value: " + decFormat.format(curr.getH()));
						gValue.setText("G Value: " + decFormat.format(curr.getG()));
						fValue.setText("F Value: " + decFormat.format(curr.getF()));
						currTile.setText("Current Tile: " + curr.getCoords().getX() + ", " + curr.getCoords().getY());
					});
					
				}
				clearPath.setDisable(false);
			}
		});
	}
	
	


}

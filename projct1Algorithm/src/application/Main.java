
package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {

	public static int toCity = 0;
	public static int fromCity = 0;
	public static int numOfCities = 0;
	public static String path;
	public static String[] city;
	public static String[][] next;
	public static String input = "";
	public static String destination;
	public static String startingCity = "";
	public static int[][] table;
	public static int d = 0;

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {

		Font font = Font.font("Times New Roman", FontWeight.BOLD, 30);

		// --- TabPane ---
		TabPane tabPane = new TabPane();

		// --- Tab 1==> File & City Selection ---
		Label startCity = new Label("Start: ");
		startCity.setTextFill(Color.BLACK);
		startCity.setFont(font);

		Label endCity = new Label("End: ");
		endCity.setTextFill(Color.RED);
		endCity.setFont(font);

		ComboBox<String> start = new ComboBox<>();
		start.setPrefWidth(150);
		start.setPrefHeight(40);

		ComboBox<String> end = new ComboBox<>();
		end.setPrefWidth(150);
		end.setPrefHeight(40);

		Button chooseFileButton = new Button("Choose a File");
		chooseFileButton.setFont(font);
		chooseFileButton.setStyle(
				"-fx-background-color: white; -fx-border-color: blue; -fx-border-radius: 10; -fx-background-radius: 20em;");
		chooseFileButton.setTextFill(Color.GREEN);

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		final File[] f = new File[1];

		chooseFileButton.setOnAction(e -> {
			f[0] = fileChooser.showOpenDialog(primaryStage);
			if (f[0] != null) {
				try (Scanner sc = new Scanner(f[0])) {
					String number = sc.nextLine();
					numOfCities = Integer.parseInt(number);
					int numOfLine = 0;
					city = new String[numOfCities];
					String cityEnd = "";
					input = "";

					start.getItems().clear();
					end.getItems().clear();

					while (sc.hasNext()) {
						numOfLine++;
						if (numOfLine == 1) {
							String s = sc.nextLine();
							String[] str = s.split(", ");

							cityEnd = str[1];
							city[0] = str[0];
							city[numOfCities - 1] = str[1];
							continue;
						}
						String s = sc.nextLine();
						input += s + "\n";
						String[] str = s.split(", ");
						city[numOfLine - 2] = str[0];
						start.getItems().addAll(str[0]);
						end.getItems().addAll(str[0]);
					}

					start.getItems().addAll(cityEnd);
					end.getItems().addAll(cityEnd);

					start.setValue(start.getItems().get(0));
					end.setValue(end.getItems().get(numOfCities - 1));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		HBox fileSelectionHBox = new HBox(20, chooseFileButton, startCity, start, endCity, end);
		fileSelectionHBox.setPadding(new Insets(30));
		fileSelectionHBox.setAlignment(Pos.CENTER);

		Tab tabFile = new Tab("Select File & Cities");
		tabFile.setContent(fileSelectionHBox);
		tabFile.setClosable(false);

		// --- Tab 2 ===> Cost & Best Path ---
		TextField bestCostField = new TextField();
		bestCostField.setEditable(false);
		bestCostField.setPromptText("Minimum Cost");
		bestCostField.setPrefWidth(250);
		bestCostField.setPrefHeight(50);
		bestCostField.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		Button findMinCost = new Button("Find Minimum Cost");
		findMinCost.setFont(font);
		findMinCost.setStyle(
				"-fx-background-color: white; -fx-border-color: blue; -fx-border-radius: 10; -fx-background-radius: 20em;");
		findMinCost.setTextFill(Color.BLUE);

		HBox costHBox = new HBox(30, bestCostField, findMinCost);
		costHBox.setAlignment(Pos.CENTER);
		costHBox.setPadding(new Insets(30));

		TextField pathField = new TextField();
		pathField.setEditable(false);
		pathField.setPromptText("Best Path");
		pathField.setPrefWidth(600);
		pathField.setPrefHeight(50);
		pathField.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		Button printPath = new Button("Print Best Path");
		printPath.setFont(font);
		printPath.setStyle(
				"-fx-background-color: white; -fx-border-color: blue; -fx-border-radius: 10; -fx-background-radius: 20em;");
		printPath.setTextFill(Color.BLUE);

		HBox pathHBox = new HBox(30, pathField, printPath);
		pathHBox.setAlignment(Pos.CENTER);
		pathHBox.setPadding(new Insets(30));

		VBox costVBox = new VBox(20, costHBox, pathHBox);
		costVBox.setAlignment(Pos.CENTER);

		Tab tabCost = new Tab("Cost & Best Path");
		tabCost.setContent(costVBox);
		tabCost.setClosable(false);

		// --- Tab 3: Table ---
		Label lbTable = new Label("This is the table:");
		lbTable.setFont(font);

		TextArea taTable = new TextArea();
		taTable.setEditable(false);
		taTable.setPrefWidth(900);
		taTable.setPrefHeight(600);
		taTable.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

		Button showTable = new Button("Show Table");
		showTable.setFont(font);
		showTable.setStyle("-fx-background-color: white; -fx-border-color: red; -fx-border-radius: 10;");
		showTable.setTextFill(Color.BLUE);

		VBox tableVBox = new VBox(20, lbTable, taTable, showTable);
		tableVBox.setAlignment(Pos.CENTER);
		tableVBox.setPadding(new Insets(30));

		Tab tabTable = new Tab("Table");
		tabTable.setContent(tableVBox);
		tabTable.setClosable(false);

		// --- Tab 4: Alternative Paths ---
		TextArea othersArea = new TextArea();
		othersArea.setEditable(false);
		othersArea.setPrefHeight(400);
		othersArea.setPrefWidth(700);
		othersArea.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		Button printOthers = new Button("Print Alternative Paths");
		printOthers.setFont(font);
		printOthers.setStyle(
				"-fx-background-color: white; -fx-border-color: blue; -fx-border-radius: 10; -fx-background-radius: 20em;");
		printOthers.setTextFill(Color.BLUE);

		VBox alternativeVBox = new VBox(20, othersArea, printOthers);
		alternativeVBox.setAlignment(Pos.CENTER);
		alternativeVBox.setPadding(new Insets(30));

		Tab tabAlternative = new Tab("Alternative Paths");
		tabAlternative.setContent(alternativeVBox);
		tabAlternative.setClosable(false);

		// Add all tabs
		tabPane.getTabs().addAll(tabFile, tabCost, tabTable, tabAlternative);

		// --- Button Actions ---

		printPath.setOnAction(e -> {
			if (toCity >= fromCity && table[0][numOfCities - 1] != Integer.MAX_VALUE)
				pathField.setText(printBestPath(next, startingCity, next[0][numOfCities - 1]) + destination);
			else
				pathField.setText("Sorry, There is no direct way.");
		});

		printOthers.setOnAction(e -> {
			if (toCity >= fromCity && table[0][numOfCities - 1] != Integer.MAX_VALUE)
				othersArea.setText(printShortestPathsToDestination(table, city, startingCity, destination));
			else
				othersArea.setText("Sorry, There is no direct way.");
		});

		findMinCost.setOnAction(e -> {

			if (toCity >= fromCity) {
				makeAlgorithm(start, end, input);
				bestCostField.setText(Integer.toString(table[0][numOfCities - 1]));
			} else {
				bestCostField.setText("Sorry, There is no direct way.");
			}

		});

		showTable.setOnAction(e -> {
			taTable.setText("");
			if (toCity >= fromCity) {
				StringBuilder outputBuilder = new StringBuilder();
				for (int i = 1; i < toCity; i++) {
					if (next[0][i].equals("X")) {
						next[0][i] = startingCity;
					}
				}
				outputBuilder.append("          ");
				for (int i = d; i <= toCity + d; i++) {
					outputBuilder.append(String.format("%-10s", city[i]));
				}
				outputBuilder.append("\n");

				for (int i = 0; i < numOfCities; i++) {

					outputBuilder.append(String.format("%-10s", city[i + d]));
					for (int j = 0; j < numOfCities; j++) {

						if (table[i][j] == Integer.MAX_VALUE || j < i) {
							outputBuilder.append(String.format("%-10s", ""));

						} else {
							outputBuilder.append(String.format("%-10s", table[i][j]));
						}

						if (j == numOfCities - 1) {
							outputBuilder.append("\n\n");
						}
					}
				}
				taTable.setStyle("-fx-font-family: 'Courier New', monospaced;");
				taTable.appendText(outputBuilder.toString());
			} else {
				taTable.appendText("Sorry, The road is in one way and you can't back.");
			}
		});

		Scene scene = new Scene(tabPane, 1300, 800);
		primaryStage.setTitle("Information about specific trip");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// ============= printPath method ====================

	public static String printBestPath(String[][] arr, String src, String cities) {

		int size = toCity;
		String path = "";
		int diff = 0;
		while (!cities.equals(src)) {
			for (int j = size; j >= fromCity; j--) {
				if (j == fromCity)

					break;
				if (city[j + d].equals(cities)) {
					diff = size - j;
					path = city[j + d] + " => " + path;

					cities = arr[0][toCity - diff]; // I
				}
			}
		}
		return src + " -> " + path;
	}

	// ======== printShortestPathsToDestination method =================

	public static String printShortestPathsToDestination(int[][] table, String[] city, String source,
			String destination) {
		String result = "";
		int shortestDistance = table[0][numOfCities - 1];

		if (shortestDistance != Integer.MAX_VALUE) { // If there is a direct
														// connection from the
														// source to the
														// destination
			List<Path> shortestPaths = new ArrayList<>();
			List<String> currentPath = new ArrayList<>();
			currentPath.add(city[fromCity]);
			printAllShortestPathsHelper(table, city, fromCity, toCity, currentPath, shortestPaths);
			result += "Shortest paths from " + source + " to " + destination + ":\n";
			if (shortestPaths.isEmpty()) {
				result += "No paths found.\n";
			} else {
				int count = 0;
				for (int i = 1; i < shortestPaths.size(); i++) {
					Path path = shortestPaths.get(i);
					result += "Path: " + path.getPath() + "\n";
					result += "Cost: " + path.getCost() + "\n";
					count++;
					if (count == 2) {
						break;
					}
				}
			}
			return result;
		}
		return "No direct connection from " + source + " to " + destination;
	}

	// ============== printAllShortestPathsHelper method==========

	private static void printAllShortestPathsHelper(int[][] table, String[] city, int currentIndex,
			int destinationIndex, List<String> currentPath, List<Path> shortestPaths) {

		if (currentIndex < 0 || currentIndex >= table.length) {
			return;
		}

		if (currentIndex == destinationIndex) {

			shortestPaths.add(new Path(String.join(" -> ", currentPath), calculatePathCost(table, currentPath)));
		} else {
			for (int i = 0; i < city.length; i++) {
				if (i < 0 || i >= table.length) {
					continue;
				}

				if (table[currentIndex][i] != Integer.MAX_VALUE && i != currentIndex) {
					currentPath.add(city[i]);
					printAllShortestPathsHelper(table, city, i, destinationIndex, currentPath, shortestPaths);
					currentPath.remove(currentPath.size() - 1);
				}
			}
		}
	}

	// ============ calculatePathCost method ==================

	private static int calculatePathCost(int[][] table, List<String> path) {
		int cost = 0;
		for (int i = 0; i < path.size() - 1; i++) {
			int city1Index = getIndex(path.get(i));
			int city2Index = getIndex(path.get(i + 1));
			cost += table[city1Index][city2Index];
		}
		return cost;
	}

	// ============== getIndex method ====================

	private static int getIndex(String cityName) { // Assuming the city array
													// contains unique city
													// names
		for (int i = 0; i < city.length; i++) {
			if (city[i].equals(cityName)) {
				return i;
			}
		}
		return -1;
	}

	// =========== make Algorithm ===================

	public void makeAlgorithm(ComboBox<String> start, ComboBox<String> end, String input) {
		startingCity = start.getValue();
		destination = end.getValue();
		fromCity = start.getSelectionModel().getSelectedIndex();
		toCity = end.getSelectionModel().getSelectedIndex();
		d = fromCity; // d = 2
		numOfCities = toCity - fromCity + 1;
		table = new int[numOfCities][numOfCities];
		next = new String[numOfCities][numOfCities];

		// ----------------------------------------------------
		/*
		 * Initialize the distance table with 0 for same cities and MAX_VALUE
		 * for different cities
		 * 
		 * Initialize the next array with "X" to represent no direct path
		 * initially.
		 */
		for (int i = 0; i < numOfCities; i++) { // fill the table with initial
												// values
			for (int j = 0; j < numOfCities; j++) {
				if (i == j)
					table[i][j] = 0;/// distance city of i to city of j this is
									/// the same and = 0
				else
					table[i][j] = Integer.MAX_VALUE;
				next[i][j] = "X";/// this is array 2D , and contan on the next
									/// city
			}
		}

		// ----------------------------------------------------

		String[] line = new String[numOfCities - 1];
		line = input.split("\n");/// make the split for the input string

		for (int i = 0; i < numOfCities - 1; i++) {
			// take the city in the string and the cost for this city
			String[] parts = line[i + fromCity].split(", (?=\\[)");

			/// city1 0 --> numOfCities - 1
			int city1 = i;

			for (int j = 1; j < parts.length; j++) {/// delete this [] and make
													/// split by (,) split city
													/// about the cost
				String[] cityAndCosts = parts[j].replaceAll("[\\[\\]]", "").split(",");

				String item = cityAndCosts[0].trim(); // item is the city
				int city2 = 0;
				/*
				 * Start , [A,22,70], [B,8,80], [C,12,80]
				 * 
				 * A , [D,8,50],[E,10,70]
				 * 
				 */
				for (int k = fromCity; k <= toCity; k++) {
					if (start.getItems().get(k).equals(item)) {
						city2 = k - fromCity;
						break;
					}
				}

				int petrolCost = Integer.parseInt(cityAndCosts[1].trim());
				int hotelCost = Integer.parseInt(cityAndCosts[2].trim());

				table[city1][city2] = petrolCost + hotelCost;
				/*
				 * stores cost (gasoline + hotels) between city1 and city2 in
				 * table.
				 */
			}
		}

		// ----------------------------------------------------

		for (int i = 0; i < numOfCities; i++) {
			for (int j = 0; j < numOfCities; j++) {
				if (j < i)
					table[i][j] = Integer.MAX_VALUE;
				if (i == j)
					table[i][j] = 0;
			}
		}

		// ----------------------------------------------------

		/*
		 * This is the relation
		 */
		for (int i = 0; i < numOfCities; i++) { // fill the table with minimum
			for (int j = 0; j < numOfCities; j++) {
				for (int k = 0; k < numOfCities; k++) {

					if (table[j][i] == Integer.MAX_VALUE || table[i][k] == Integer.MAX_VALUE)

						continue;

					if (table[j][k] > table[j][i] + table[i][k]) {/// find other
																	/// min cost
						table[j][k] = table[j][i] + table[i][k];
						next[j][k] = city[i + d];

					}
				}
			}
		}

		// ----------------------------------------------------

		for (int i = 1; i < numOfCities; i++) {
			if (next[0][i] == "X")
				next[0][i] = startingCity;
			else
				break;
		}

		// ----------------------------------------------------

		fromCity = fromCity - d; // fromCity = 0
		toCity = toCity - d;
		for (int i = 0; i < next.length; i++)
			if (table[0][i] == 0 || table[0][i] == Integer.MAX_VALUE)
				next[0][i] = "X";

	}

	public static void main(String[] args) throws FileNotFoundException {
		launch(args);
	}
}

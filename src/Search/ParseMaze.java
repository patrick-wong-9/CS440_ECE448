package Search;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ParseMaze {
	private static int numRows;
	private static int numCols;

	public static Cell[][] maze;
	public static Cell start; 
	private static ArrayList<Cell> goals;
	/*
	 * Constructor
	 */
	public ParseMaze() {
		
	}
	/*
	 * Reads the contents of a maze stored in a text file into a 2d array. 
	 * -'%' for walls -- Cell.val = 0;
	 * -'P' for starting location  -- Cell.val = 1;
	 * -' ' for empty space -- Cell.val = 2;
	 * -'.' for goal location -- Cell.val = 3;
	 * @param file
	 */
	public static Cell[][] readFile(String fileName) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(fileName));
		goals = new ArrayList<Cell>();
		List<String> lines = new ArrayList<>();
		while(sc.hasNext()) {
			String currLine = sc.nextLine();
			lines.add(currLine);
		}
		sc.close();
		numRows = lines.size();
		numCols = lines.get(0).length();
		maze= new Cell[numRows][numCols];
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numCols; j++) {
				Character currChar = lines.get(i).charAt(j);
				Cell currCell = null;
				switch (currChar) {
				case '%': // wall
					currCell = new Cell('%',i,j);
					break;
				case '.': // goal
					currCell = new Cell('.',i,j);
					goals.add(currCell);
					break;
				case 'P': // starting position
					currCell = new Cell('P',i,j);
					start = currCell; 
					break;
				default: // movable empty space
					currCell = new Cell(' ',i,j);
					break;
				}
				maze[i][j] = currCell;
			}
		}
		return maze;
	}
	public static Cell[][] getMaze() {
		return maze;
	}
	public static void setMaze(Cell[][] maze) {
		ParseMaze.maze = maze;
	}
	public static Cell getStart() {
		return start;
	}
	public static void setStart(Cell start) {
		ParseMaze.start = start;
	}
	public static ArrayList<Cell> getGoals() {
		return goals;
	}
	public static int getNumRows() {
		return numRows;
	}
	public static void setNumRows(int numRows) {
		ParseMaze.numRows = numRows;
	}
	public static int getNumCols() {
		return numCols;
	}
	public static void setNumCols(int numCols) {
		ParseMaze.numCols = numCols;
	}
	

}

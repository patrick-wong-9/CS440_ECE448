package FlowFree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * @author Patrick Wong
 * scans txt file and stores initial State
 */
public class ImportInput {

	private static int numRows;
	private static int numCols; 
	private static State initAssignment; 

	public ImportInput(){

	}
	public static State readFile(String fileName) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(fileName));
		List<String> lines = new ArrayList<>();
		while(sc.hasNext()) {
			String currLine = sc.nextLine();
			lines.add(currLine);
		}
		sc.close();

		ArrayList<Position> variables = new ArrayList<Position>();
		ArrayList<Character> domain = new ArrayList<Character>();
		ArrayList<Position> sources = new ArrayList<Position>();
		numRows = lines.size();
		numCols = lines.get(0).length();
		//fills up domain;		
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numCols; j++) {
				Character currChar = lines.get(i).charAt(j);
				switch (currChar) {
				case '_': // empty space
					break;
				default: // sources
					if (!domain.contains(currChar)){
						domain.add(currChar);			
					}
					break;
				}
			}	
		}

		FFcell[][] temp = new FFcell[numRows][numCols];
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numCols; j++) {
				Character currChar = lines.get(i).charAt(j);
				FFcell currCell = null;
				switch (currChar) {
				case '_': // empty space
					Position pos = new Position(i,j);
					variables.add(pos);
					currCell = new FFcell('_', pos, false, domain);
					break;
				default: // sources
					Position xy = new Position(i,j);
					ArrayList<Character> d = new ArrayList<Character>();
					d.add(currChar);
					currCell = new FFcell(currChar,xy,true, d);
					sources.add(xy);
					break;
				}
				temp[i][j] = currCell;
			}	
		}
		initAssignment = new State(temp, variables, sources,domain, numRows, numCols);


		//		for(int i =0; i< sources.size(); i++){
		//			System.out.println(sources.get(i).getRow()+ "##"+ sources.get(i).getCol());
		//		}
		//		System.out.println(domain);
		return initAssignment;
	}

	public static int getNumRows() {
		return numRows;
	}

	public static void setNumRows(int numRows) {
		ImportInput.numRows = numRows;
	}

	public static int getNumCols() {
		return numCols;
	}

	public static void setNumCols(int numCols) {
		ImportInput.numCols = numCols;
	}

	public static State getInitAssignment() {
		return initAssignment;
	}

	public static void setInitAssignment(State initAssignment) {
		ImportInput.initAssignment = initAssignment;
	}





}
